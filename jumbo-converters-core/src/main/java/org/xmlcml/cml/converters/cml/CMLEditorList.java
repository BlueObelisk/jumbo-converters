package org.xmlcml.cml.converters.cml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

/** a converter which contains a specific editor
 * the editor is invoked to act on the CML
 * 
 * @author pm286
 *
 */
public class CMLEditorList extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(CMLEditorList.class);
	static {
		LOG.setLevel(Level.INFO);
	};
	
	private CMLElement cmlIn;
	private CMLElement cmlOut;
	private List<CMLEditor> editorList;
	
	public CMLEditorList() {
		editorList = new ArrayList<CMLEditor>();
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * 
	 */
	public Element convertToXML(Element cmlInx) {
		this.cmlIn = CMLBuilder.ensureCML(cmlInx);
		cmlOut = cmlIn;
		for (CMLEditor cmlEditor : editorList) {
			cmlEditor.executeCommand(cmlIn);
		}
		return cmlOut;
	}
	
	public CMLEditor getCmlEditor() {
		return editorList.size() == 1 ? editorList.get(0) : null;
	}

	public void add(CMLEditor cmlEditor) {
		this.editorList.add(cmlEditor);
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			usage();
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
		CMLEditorList editorConverter = new CMLEditorList();
//		((MoleculeEditor)editorConverter.getCmlEditor()).setCalculateBonds(true);
		cmlObject = (CMLElement) editorConverter.convertToXML(cmlObject);
		cmlObject.debug("CML");
	}

	private static void usage() {
		System.out.println("usage: [zero args runs test]");
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
