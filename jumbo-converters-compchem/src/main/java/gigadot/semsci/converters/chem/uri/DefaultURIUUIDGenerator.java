package gigadot.semsci.converters.chem.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.xmlcml.cml.base.CMLElement;

/**
 *
 * @author wp214
 */
public class DefaultURIUUIDGenerator implements URIGenerator {

    public static final String PATH_URI = "http://ch.cam.ac.uk/data/";

    public URI createUUIDURI(CMLElement element) {
        try {
            return new URI("uri", "uuid:" + UUID.randomUUID().toString(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param subpath must not be gin with / but ends with /
     * @return
     */
    public URI createCMLURL(CMLElement element) {
        try {
            return new URI(PATH_URI + UUID.randomUUID().toString() + ".cml");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createResourcePathURI(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return PATH_URI + path;
    }
}
