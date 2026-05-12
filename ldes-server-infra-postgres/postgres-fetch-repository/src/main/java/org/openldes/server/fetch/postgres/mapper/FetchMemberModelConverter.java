package org.openldes.server.fetch.postgres.mapper;

import static org.openldes.server.domain.constants.ServerConstants.SERIALISATION_LANG;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class FetchMemberModelConverter implements AttributeConverter<Model, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(Model attribute) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        RDFWriter.source(attribute).lang(SERIALISATION_LANG).output(stream);
        return stream.toByteArray();
    }

    @Override
    public Model convertToEntityAttribute(byte[] dbData) {
        return RDFParser.source(new ByteArrayInputStream(dbData)).lang(SERIALISATION_LANG).toModel();
    }
}
