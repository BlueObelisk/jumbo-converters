package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xmlcml.cif.CIF;


public class CIF2CIFXMLTest {
	@Test
	public void convertToXML() throws IOException{
		System.out.println("classpath is: " + System.getProperty("java.class.path"));
		List<String> stringList=(List<String>)FileUtils.readLines(new File("src/test/resources/cif/test.cif"));
		CIF2CIFXMLConverter cif2cifxml=new CIF2CIFXMLConverter();
		CIF cif = cif2cifxml.parseLegacy(stringList);
		
	}
}
