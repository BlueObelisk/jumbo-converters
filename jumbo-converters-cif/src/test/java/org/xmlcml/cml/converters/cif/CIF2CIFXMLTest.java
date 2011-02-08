package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;


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
}
