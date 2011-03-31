package gigadot.semsci.converters.chem.uri;

import java.net.URI;

import org.xmlcml.cml.base.CMLElement;

/**
 *
 * @author wp214
 */
public interface URIGenerator {

    URI createUUIDURI(CMLElement element);

    URI createCMLURL(CMLElement element);
}
