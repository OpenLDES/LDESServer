package org.openldes.server.domain.constants;

import java.util.List;
import org.apache.jena.riot.Lang;

public class ServerConstants {
    private ServerConstants() {
    }

    public static final String DEFAULT_BUCKET_STRING = "unknown";
    public static final List<Lang> RELATIVE_URL_INCOMPATIBLE_LANGS = List.of(
            Lang.NQUADS,
            Lang.NTRIPLES,
            Lang.RDFXML,
            Lang.RDFJSON,
            Lang.RDFPROTO,
            Lang.RDFTHRIFT
    );

    public static final Lang SERIALISATION_LANG = Lang.RDFPROTO;
}
