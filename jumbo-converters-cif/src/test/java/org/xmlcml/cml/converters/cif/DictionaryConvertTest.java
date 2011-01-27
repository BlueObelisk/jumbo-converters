package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.junit.Test;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFParser;
import org.xmlcml.cml.converters.cif.dict.CIFDict2CMLConverter;
import org.xmlcml.cml.converters.cif.dict.CifDictionaryBuilder;

public class DictionaryConvertTest {

	@Test
	public void testCifXom() throws CIFException, IOException {
		File dir=new File("src/test/resources/dict");
		dir.mkdirs();
		File in = new File("src/main/resources/cif_core.dic");
		CIFParser parser = new CIFParser();
		Document cifDict = parser.parse(in);
		Serializer ser = new Serializer(new FileOutputStream(new File(
				"src/test/resources/dict/cif.xml")));
		ser.setIndent(2);
		ser.write(cifDict);
	}
	

	@Test
	public void testCreateDictionaryPeter() throws IOException {
		File in = new File("src/main/resources/cif_core.dic");
		CIFDict2CMLConverter dictConv = new CIFDict2CMLConverter();
		Element xml = dictConv.convertToXML(in);
		Serializer ser = new Serializer(new FileOutputStream(new File(
				"src/test/resources/dict/peter.xml")));
		ser.setIndent(2);
		ser.write(new Document(xml));
	}

	@Test
	public void testCreateDictionarySam() throws CIFException, IOException {
		File in = new File("src/main/resources/cif_core.dic");
		CifDictionaryBuilder builder = new CifDictionaryBuilder();
		CIFParser parser = new CIFParser();
		Document cifDict = parser.parse(in);
		builder.build(cifDict);
		Serializer ser = new Serializer(new FileOutputStream(new File(
				"src/test/resources/dict/sam.xml")));
		ser.setIndent(2);
		ser.write(builder.getCmlDoc());
	}

}
