package org.xmlcml.cml.converters.cif;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cif.CIF;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;


/** 
 * Converts a CIF into CML
 * using the other converters available
 * @author nwe23
 *
 */
public class CIF2CMLConverter extends AbstractConverter{

		
	/**
	 * converts a CIF object to CML.
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> stringList){
		CIF cif = this.cif2cifxml(stringList);
		Element rawCML = this.cfixml2cml(cif);
		Element cml = this.cml2compcml(rawCML);
		return cml;
	}

	private CIF cif2cifxml(List<String> stringList){
		CIF2CIFXMLConverter cif2cifxml=new CIF2CIFXMLConverter();
		CIF cif = cif2cifxml.parseLegacy(stringList);
		return cif;
	}
	
	private Element cfixml2cml(CIF cif){
		CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
		return cifxml2cml.convertToXML(cif);
	}
	
	private Element cml2compcml(Element cml){
		RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
		return conv.convertToXML(cml);
	}

	public Type getInputType() {
		return Type.CIF;
	}

	public Type getOutputType() {
		return Type.CML;
	}
}


