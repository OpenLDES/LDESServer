package org.openldes.server.rest.treenode;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.VARY;

import io.micrometer.observation.annotation.Observed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.openldes.server.domain.converter.RdfMediaType;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.services.StreamingTreeNodeFactory;
import org.openldes.server.fetching.services.TreeNodeFetcher;
import org.openldes.server.fetching.valueobjects.FragmentPair;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.openldes.server.fetching.valueobjects.LdesFragmentRequest;
import org.openldes.server.rest.caching.CachingStrategy;
import org.openldes.server.rest.config.RestConfig;
import org.openldes.server.rest.exceptionhandling.exceptions.ConnectionException;
import org.openldes.server.rest.treenode.services.TreeNodeStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Observed
@RestController
public class TreeNodeController implements OpenApiTreeNodeController {
	private final RestConfig restConfig;
	private final TreeNodeFetcher treeNodeFetcher;
	private final StreamingTreeNodeFactory streamingTreeNodeFactory;
	private final TreeNodeStreamConverter treeNodeStreamConverter;
	private final CachingStrategy cachingStrategy;
	private static final Logger log = LoggerFactory.getLogger(TreeNodeController.class);

	public TreeNodeController(RestConfig restConfig, TreeNodeFetcher treeNodeFetcher, StreamingTreeNodeFactory streamingTreeNodeFactory, TreeNodeStreamConverter treeNodeStreamConverter, CachingStrategy cachingStrategy) {
		this.restConfig = restConfig;
		this.treeNodeFetcher = treeNodeFetcher;
        this.streamingTreeNodeFactory = streamingTreeNodeFactory;
        this.treeNodeStreamConverter = treeNodeStreamConverter;
        this.cachingStrategy = cachingStrategy;
	}

	@Override
	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionName}/{view}", produces = { MediaType.TEXT_EVENT_STREAM_VALUE })
	public ResponseEntity<ResponseBodyEmitter> retrieveLdesFragmentStreaming(@PathVariable("view") String view,
																			 @RequestParam Map<String, String> requestParameters,
																			 @PathVariable String collectionName) {
		final ViewName viewName = new ViewName(collectionName, view);
		LdesFragmentIdentifier id = new LdesFragmentIdentifier(viewName,
				requestParameters.entrySet().stream()
						.map(entry -> new FragmentPair(entry.getKey(), entry.getValue()))
						.toList());
		TreeNode treeNode = getFragmentWithoutMembers(viewName, requestParameters);

		SseEmitter emitter = new SseEmitter();
		new Thread(() -> sendStreamingFragment(emitter, treeNode, id)).start();

		String language = MediaType.TEXT_EVENT_STREAM_VALUE;
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, RestConfig.INLINE)
				.header(CACHE_CONTROL, getCacheControlHeader(treeNode))
                .header(VARY, ACCEPT)
				.eTag(cachingStrategy.generateCacheIdentifier(treeNode, language))
				.body(emitter);
	}

	@Override
	@CrossOrigin(origins = "*", allowedHeaders = "")
	@GetMapping(value = "{collectionName}/{view}")
	public ResponseEntity<TreeNode> retrieveLdesFragment(@PathVariable("view") String view,
														 @RequestParam Map<String, String> requestParameters,
														 @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = RdfMediaType.DEFAULT_RDF_MEDIA_TYPE_VALUE) String language,
														 @PathVariable String collectionName) {
		final ViewName viewName = new ViewName(collectionName, view);
		TreeNode treeNode = getFragment(viewName, requestParameters);
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, RestConfig.INLINE)
				.header(CACHE_CONTROL, getCacheControlHeader(treeNode))
                .header(VARY, ACCEPT)
                .eTag(cachingStrategy.generateCacheIdentifier(treeNode, language))
				.body(treeNode);
	}

	private void sendStreamingFragment(SseEmitter emitter, TreeNode treeNode, LdesFragmentIdentifier id) {
		Lang lang = Lang.RDFPROTO;
		try {
			emitter.send(SseEmitter.event()
					.name("metadata")
					.data(encodeModel(treeNodeStreamConverter.getMetaDataStatements(treeNode), lang))
					.comment(String.format("Metadata and relations of the LDES fragment, encoded in base64 and with %s as mimetype.", lang.getHeaderString())));

			streamingTreeNodeFactory.getMembersOfFragment(id)
					.map(member -> treeNodeStreamConverter.getMemberStatements(member, treeNode.getCollectionName())).forEach(model -> {
						try {
							emitter.send(SseEmitter.event().name("member")
									.data(encodeModel(model, lang))
									.comment(String.format("LDES member, encoded in base64 and with %s as mimetype.", lang.getHeaderString())));
						} catch (IOException exception) {
							log.error("Error while sending LDES member: {}", exception.getMessage());
							emitter.completeWithError(exception);
						}
					});
			emitter.complete();
		} catch (Exception exception) {
			String message = String.format("Error while sending LDES fragment: %s", exception.getMessage());
			log.error(message);
			try {
				emitter.send(SseEmitter.event().data(message).name("error"));
			} catch (IOException e) {
				throw new ConnectionException("Could not send previous error message to client", e);
			}
			emitter.completeWithError(exception);
		}
	}

	private byte[] encodeModel(Model model, Lang lang) throws IOException {
		String contentType = lang.getHeaderString();
		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			model.write(outputStream, contentType);
			return Base64.getEncoder().encode(outputStream.toByteArray());
		}
	}

	private TreeNode getFragment(ViewName viewName, Map<String, String> fragmentationMap) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(viewName,
				fragmentationMap.entrySet()
						.stream()
						.map(entry -> new FragmentPair(entry.getKey(), entry.getValue()))
						.toList());

		return treeNodeFetcher.getFragment(ldesFragmentRequest);
	}

	private TreeNode getFragmentWithoutMembers(ViewName viewName, Map<String, String> fragmentationMap) {
		List<FragmentPair> fragmentPairs = fragmentationMap.entrySet()
				.stream()
				.map(entry -> new FragmentPair(entry.getKey(), entry.getValue()))
				.toList();
		LdesFragmentIdentifier treeNodeId = new LdesFragmentIdentifier(viewName, fragmentPairs);

		return streamingTreeNodeFactory.getFragmentWithoutMemberData(treeNodeId);
	}

	private String getCacheControlHeader(TreeNode treeNode) {
		return treeNode.isImmutable()
				? restConfig.generateImmutableCacheControl()
				: restConfig.generateMutableCacheControl(treeNode.getNextUpdateTs());
	}

}
