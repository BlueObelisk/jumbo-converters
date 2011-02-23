package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class QespressoTest {
	@Test
	public void test0() throws Exception {
		generateAndCompareXml(
			"qespresso",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"qespresso0.out",
			"template0.xml",
			"qespresso0.xml"
		);
	}

	@Test
	public void test1() throws Exception {
		generateAndCompareXml(
			"qespresso1",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"qespresso1.out",
			"template1.xml",
			"qespresso1.xml"
		);
	}
	
	@Test
	public void test2() throws Exception {
		generateAndCompareXml(
			"qespresso2",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"qespresso2.out",
			"template2.xml",
			"qespresso2.xml"
		);
	}
	
	@Test
	public void methane() throws Exception {
		generateAndCompareXml(
			"methane",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"methane.out",
			"template1.xml",
			"methane.xml"
		);
	}
	
	@Test
	public void benzene() throws Exception {
		generateAndCompareXml(
			"benzene",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"benzene.scf.out",
			"template1.xml",
			"benzene.scf.xml"
		);
	}
	
	@Test
	@Ignore
	public void clathrate() throws Exception {
		generateAndCompareXml(
			"clathrate",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"clathrate.relax.out",
			"template1.xml",
			"clathrate.relax.xml"
		);
	}
	
	@Test
	public void k_bcc() throws Exception {
		generateAndCompareXml(
			"k_bcc",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"k_bcc.scf.out",
			"template1.xml",
			"k_bcc.scf.xml"
		);
	}
	
	@Test
	public void pd_111() throws Exception {
		generateAndCompareXml(
			"pd_111",
			"src/test/resources/org/xmlcml/cml/converters/text/", 
			"target/test/text",
			"pd_111_11.relax.out",
			"template1.xml",
			"pd_111_11.relax.xml"
		);
	}
	
	private void generateAndCompareXml(
			String title, String dirS, String outDirS, String infileS, String templateFileS, String refS) throws Exception {
		File dir = new File(dirS);
		dir.mkdirs();
		File out = new File(outDirS);
		out.mkdirs();
		String stringToBeParsed = FileUtils.readFileToString(new File(dir, infileS));
		Assert.assertNotNull(stringToBeParsed);
		String templateS = FileUtils.readFileToString(new File(dir, templateFileS));
		Template template = new Template(CMLUtil.parseXML(templateS));
		template.applyMarkup(stringToBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Element linesElement = lineContainer.getNormalizedLinesElement();
		CMLUtil.debug(lineContainer.getLinesElement(), new FileOutputStream(new File(out, refS)), 1);
		String refXml = FileUtils.readFileToString(new File(dir, refS));
		JumboTestUtils.assertEqualsCanonically(title, refXml, linesElement, true);
	}
}
