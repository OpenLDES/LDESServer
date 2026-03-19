package org.openldes.server.rest.caching;

import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.openldes.server.fetching.valueobjects.TreeRelation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.openldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final String hostName;

	public EtagCachingStrategy(@Value(HOST_NAME_KEY) String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String generateCacheIdentifier(String collectionName, String language) {
		return sha256Hex(hostName + "/" + collectionName + "?lang=" + language);
	}

	@Override
	public String generateCacheIdentifier(TreeNode treeNode, String language) {
		return sha256Hex(treeNode.getFragmentId()
				+ treeNode.getRelations().stream()
						.map(TreeRelation::treeNode)
						.map(LdesFragmentIdentifier::asDecodedFragmentId)
						.collect(Collectors.joining(""))
				+ treeNode.getMembers().stream()
						.map(Member::subject)
						.collect(Collectors.joining(""))
				+ language);
	}
}
