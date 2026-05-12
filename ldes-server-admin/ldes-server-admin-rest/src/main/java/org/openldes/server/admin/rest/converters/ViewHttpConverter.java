package org.openldes.server.admin.rest.converters;

import static org.openldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

import io.micrometer.observation.annotation.Observed;
import java.io.IOException;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.jetbrains.annotations.NotNull;
import org.openldes.server.admin.spi.ViewSpecificationConverter;
import org.openldes.server.domain.converter.RdfMediaType;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.openldes.server.domain.model.ViewSpecification;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

@Observed
@Component
public class ViewHttpConverter implements HttpMessageConverter<ViewSpecification> {
	private final ViewSpecificationConverter viewSpecificationConverter;
	private final RdfModelConverter rdfModelConverter;

	public ViewHttpConverter(ViewSpecificationConverter viewSpecificationConverter, RdfModelConverter rdfModelConverter) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(@NotNull Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(@NotNull Class<?> clazz, MediaType mediaType) {
		return ViewSpecification.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
        return RdfMediaType.getMediaTypes();
	}

	@Override
	public ViewSpecification read(@NotNull Class<? extends ViewSpecification> clazz, @NotNull HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Not supported to read a viewName");
	}

	@Override
	public void write(@NotNull ViewSpecification view, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang lang = rdfModelConverter.getLangOrDefault(contentType, REST_ADMIN);
		rdfModelConverter.checkLangForRelativeUrl(lang);
		Model model = viewSpecificationConverter.modelFromView(view);
		outputMessage.getHeaders().setContentType(MediaType.parseMediaType(lang.getHeaderString()));
		RDFDataMgr.write(outputMessage.getBody(), model, lang);
	}
}
