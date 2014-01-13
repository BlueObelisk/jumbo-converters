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
	
	public CMLEditorConverter() {
		ensureCMLEditor();
	}
	
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
	
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
//			usage();
			test();

		}
	}

	private static void test() throws Exception {
		String xmlString = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<cml xmlns='http://www.xml-cml.org/schema'>" +
				"  <molecule title='Test example'>" +
				"    <atomArray>" +
				"      <atom id='a1' elementType='C' x3='0.0' y3='0.0' z3='0.0'/>" +
				"      <atom id='a2' elementType='N' x3='1.34' y3='0.0' z3='0.0'/>" +
				"      <atom id='a3' elementType='O' x3='2.0' y3='1.1' z3='0.0'/>" +
				"    </atomArray>" +
				"  </molecule>" +
				"</cml>";
		CMLElement cmlObject = (CMLElement) new CMLBuilder().parseString(xmlString);
		CMLEditorConverter editorConverter = new CMLEditorConverter();
		editorConverter.getCmlEditor().setCalculateBonds(true);
		cmlObject = (CMLElement) editorConverter.convertToXML(cmlObject);
		cmlObject.debug("CML");
	}

	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}
