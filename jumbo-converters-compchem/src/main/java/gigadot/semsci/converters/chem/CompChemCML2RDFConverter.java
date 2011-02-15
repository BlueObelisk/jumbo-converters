package gigadot.semsci.converters.chem;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;

/**
 *
 * @author wp214
 */
public class CompChemCML2RDFConverter extends AbstractCompchem2CMLConverter {

    @SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(CompChemCML2RDFConverter.class);
    
	public CompChemCML2RDFConverter() {
	}
	
	public Type getOutputType() {
	    return Type.RDFXML;
	}
	
	public Type getInputType() {
	    return Type.CML;
	}
    
	/**
	 * converts an Foo object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element element) {
		CompChemProcessor compchemProcessor = new CompChemProcessor();
		Element rdfElement = compchemProcessor.convertToXML(element);
		return rdfElement;
	}
    

}
