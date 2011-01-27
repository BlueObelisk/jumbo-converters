package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.converters.cif.dict.CIFFields;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

public class CIFFieldsTest {
	@Test
	public void testCifXom() throws CIFException, IOException {
		File in = new File("src/main/resources/cif_core.dic");
		CIFParser parser = new CIFParser();
		Document cifDict = parser.parse(in);
		CIF cif = (CIF) cifDict.getRootElement();
		CMLDictionary dictionary = new CMLDictionary();
        dictionary.setNamespace("http://www.xml-cml.org/dict/cif/");
        dictionary.setDictionaryPrefix("cif");
		for (CIFDataBlock dataBlock : cif.getDataBlockList()) {
			if ("atom_site_adp_type".equals(dataBlock.getId())) {
				CMLEntry entry = new CMLEntry();
				for(CIFFields field:CIFFields.values()){
					field.parse(dataBlock, entry);
				}
				dictionary.addEntry(entry);
				break;
			}
		}
		Assert.assertEquals(1, dictionary.getEntryElements().size());
		CMLEntry entry = dictionary.getEntryElements().get(0);
		Assert.assertNotNull(entry);
		Assert.assertEquals("atom_site_adp_type", entry.getId());
		for(Element ele:entry.getChildCMLElements()){
			System.out.println(ele.toXML());
		}
		Assert.assertNotNull(entry.getChildCMLElement("definition", 0));
	}
}
