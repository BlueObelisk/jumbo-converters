package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cml.base.CMLElement;


public class CIF2CIFXMLTest {
	@Test
	public void convertToXML() throws IOException{
		List<String> stringList=(List<String>)FileUtils.readLines(new File("src/test/resources/cif/test.cif"));
		CIF2CIFXMLConverter cif2cifxml=new CIF2CIFXMLConverter();
		CIF cif = cif2cifxml.parseLegacy(stringList);
		List<CIFDataBlock> a =cif.getDataBlockList();
		Collections.sort(a);
		int score=0;
		for(CIFDataBlock block : a){
			score+=block.getChildCount();
		}
		Assert.assertEquals(123, score);
		Assert.assertEquals(2, a.size());
	}

	@Test
	public void convertRawToComplete() throws IOException{
		List<String> stringList=(List<String>)FileUtils.readLines(new File("src/test/resources/cif/test.cif"));
		CIF2CIFXMLConverter cif2cifxml=new CIF2CIFXMLConverter();
		CIF cif = cif2cifxml.parseLegacy(stringList);
		CIFXML2CMLConverter cifxml2cml = new CIFXML2CMLConverter();
		Element raw=cifxml2cml.convertToXML(cif);
		Serializer ser = new Serializer(System.out);
		ser.write(new Document(raw));
		RawCML2CompleteCMLConverter conv = new RawCML2CompleteCMLConverter();
		CMLElement cml=(CMLElement)conv.convertToXML(raw);
		cml.debug();
	}
	
}
