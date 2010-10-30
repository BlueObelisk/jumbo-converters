package gigadot.semsci.converters.chem;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.converters.compchem.gamessus.GamessUSCommon;

/**
 *
 * @author wp214
 */
public class CompChemCML2RDFConverter extends AbstractCompchem2CMLConverter {

    private static Logger LOG = Logger.getLogger(CompChemCML2RDFConverter.class);
    
	public CompChemCML2RDFConverter() {
	}
	@Override
	protected AbstractCommon getCommon() {
		return new CompChemCommon();
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
