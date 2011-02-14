package org.xmlcml.cml.converters.cif.dict;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFLoop;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

/**
 * @author sea36
 * @author nwe23
 */
public class CifDictionaryBuilder {

	private static final String URI = "http://www.xml-cml.org/dict/cif/";
	private static final String PREFIX = "cif";
    private static final String conv_URI="http://www.xml-cml.org/convention/";
    private static final String conv_PREFIX="convention";
	
	protected CMLDictionary dictionary;
	protected CMLDictionary unitsDict;
	private Map<String, String> unitMap=new HashMap<String, String>();

	public CifDictionaryBuilder() {
		dictionary = new CMLDictionary();
		dictionary.setNamespace(URI);
		dictionary.setDictionaryPrefix(PREFIX);
		dictionary.addNamespaceDeclaration("xhtml", CIFFields.HTMLNS);
		dictionary.addNamespaceDeclaration(conv_PREFIX, conv_URI);
		dictionary.addAttribute(new Attribute("convention","convention:dictionary"));
	}

	public void build(Document cifDict) {
		CIF cif = (CIF) cifDict.getRootElement();
		for (CIFDataBlock dataBlock : cif.getDataBlockList()) {
			if ("on_this_dictionary".equals(dataBlock.getId())) {
				continue;
			}
			if (dataBlock.getId().endsWith("[]")
					|| dataBlock.getId().endsWith("_")) {
				continue;
			}

			// Parse datablock using enum methods.
			CMLEntry entry = CIFFields.parseToEntry(dataBlock);
			dictionary.appendChild(entry);
			parseLoopsfromDataBlock(dataBlock, entry);
			this.unitMap.put(CIFFields.getLastUnit(),CIFFields.getLastUnitDesc());
		}
		//writeUnitsUsed(System.out);
		unitsDict=createUnitsDictionary(unitMap);
	}

	private CMLDictionary createUnitsDictionary(Map<String, String> unitMap){
	    UnitsDictionary dict=new UnitsDictionary();
	    dict.addUnits(unitMap);
	    return dict;
	    
	}
	
	private void writeUnitsUsed(PrintStream out) {
		for (String unit : unitMap.keySet()) {
			out.println(unit+" "+unitMap.get(unit));
		}

	}

	private void parseLoopsfromDataBlock(CIFDataBlock dataBlock, CMLEntry entry) {
		List<CIFLoop> loops = dataBlock.getLoopList();
		for (CIFLoop loop : loops) {
			List<String> nameList = loop.getNameList();
			if (nameList.contains("_enumeration")) {
				List<String> enumerationValues = loop
						.getColumnValues("_enumeration");
				for (String enumerationValue : enumerationValues) {
					Element enumeration = new Element("enumeration",
							CMLConstants.CMLX_NS);
					enumeration.appendChild(enumerationValue);
					entry.appendChild(enumeration);
				}
			}
		}
	}

	public Document getCmlDoc() {
		return dictionary.getDocument() == null ? new Document(dictionary)
				: dictionary.getDocument();
	}

	private static void build(File in, File out) throws IOException,
			CIFException {
		CIFParser parser = new CIFParser();
		Document cifDict = parser.parse(in);
		CifDictionaryBuilder builder = new CifDictionaryBuilder();
		builder.build(cifDict);

		BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(out));
		Serializer ser = new Serializer(os);
		ser.setIndent(4);
		ser.write(builder.getCmlDoc());
		os.close();
		
        os = new BufferedOutputStream(new FileOutputStream(new File(out.getAbsolutePath() + ".units")));
        ser.setOutputStream(os);
        ser.write(new Document(builder.unitsDict));
	}

	public static void main(String[] args) throws Exception {

		CifDictionaryBuilder.build(new File("src/main/resources/cif_core.dic"),
				new File("src/main/resources/cif-dictionary.cml"));
		
	}

}
