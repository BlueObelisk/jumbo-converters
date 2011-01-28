package org.xmlcml.cml.converters.cif.dict;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.jaxen.NamespaceContext;

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
 */
public class CifDictionaryBuilder {

	private static final String URI = "http://www.xml-cml.org/dict/cif/";
	private static final String PREFIX = "cif";

	private CMLDictionary dictionary;

	public CifDictionaryBuilder() {
		dictionary = new CMLDictionary();
		dictionary.setNamespace(URI);
		dictionary.setDictionaryPrefix(PREFIX);
		dictionary.addNamespaceDeclaration("html", CIFFields.HTMLNS);
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
		}
	}

	private void parseLoopsfromDataBlock(CIFDataBlock dataBlock,
			CMLEntry entry) {
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
	}

	public static void main(String[] args) throws Exception {

		CifDictionaryBuilder.build(new File("cif_core.dic"), new File(
				"cif-dictionary.cml"));

	}

}
