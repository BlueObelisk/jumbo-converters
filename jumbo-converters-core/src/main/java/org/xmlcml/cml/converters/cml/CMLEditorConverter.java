package org.xmlcml.cml.converters.cml;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;


public class CMLEditorConverter extends AbstractConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CMLEditorConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	private CMLEditor cmlEditor;
	private CMLElement cmlIn;
	private CMLElement cmlOut;
	
	/**
	 * 
	 */
	public Element convertToXML(Element cmlInx) {
		this.cmlIn = CMLBuilder.ensureCML(cmlInx);
		ensureCMLEditor();
		cmlOut = cmlIn;
		cmlEditor.executeCommand(cmlIn);
		return cmlOut;
	}
	
	public CMLEditor getCmlEditor() {
		ensureCMLEditor();
		return cmlEditor;
	}

	private void ensureCMLEditor() {
		if (cmlEditor == null) {
			cmlEditor = new CMLEditor();
		}
	}

}
