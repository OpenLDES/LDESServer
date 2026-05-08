package org.openldes.server.rest.eventstream.converters;

import static org.openldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.FETCH;

import java.io.IOException;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.openldes.server.admin.spi.EventStreamTO;
import org.openldes.server.admin.spi.EventStreamWriter;
import org.openldes.server.domain.converter.RdfMediaType;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class EventStreamResponseHttpConverter implements HttpMessageConverter<EventStreamTO> {
	private final EventStreamWriter eventStreamWriter;
	private final RdfModelConverter rdfModelConverter;
	public EventStreamResponseHttpConverter(EventStreamWriter eventStreamWriter, RdfModelConverter rdfModelConverter) {
		this.eventStreamWriter = eventStreamWriter;
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return EventStreamTO.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
        return RdfMediaType.getMediaTypes();
	}

	@Override
	public EventStreamTO read(Class<? extends EventStreamTO> clazz, HttpInputMessage inputMessage)
			throws HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(EventStreamTO eventStreamTO, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Lang rdfFormat = rdfModelConverter.getLangOrDefault(contentType, FETCH);
		rdfModelConverter.checkLangForRelativeUrl(rdfFormat);
		Model eventStreamModel = eventStreamWriter.write(eventStreamTO);
		RDFDataMgr.write(outputMessage.getBody(), eventStreamModel, rdfFormat);
	}
}
