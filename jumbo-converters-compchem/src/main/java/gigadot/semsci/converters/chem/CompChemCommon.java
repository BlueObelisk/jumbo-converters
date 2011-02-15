package gigadot.semsci.converters.chem;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Foo system
 * @author pm286
 *
 */
public class CompChemCommon extends AbstractCommon {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(CompChemCommon.class);
	
	private static final String COMPCHEM_PREFIX = "compchem";
	private static final String COMPCHEM_URI = "http://wwmm.ch.cam.ac.uk/dict/compchem";
	
    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/compchemDictionary.xml";
    }
    
	public String getPrefix() {
		return COMPCHEM_PREFIX;
	}
	
	public String getNamespace() {
		return COMPCHEM_URI;
	}
	
}
