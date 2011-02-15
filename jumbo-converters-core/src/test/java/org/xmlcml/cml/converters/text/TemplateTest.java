package org.xmlcml.cml.converters.text;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Outputter.OutputLevel;
import org.xmlcml.cml.testutil.JumboTestUtils;

public class TemplateTest {
	
	String document0S = 
	"<template id='top' name='Top template' pattern=''>" +
	"  <chunkerList>" +
	"    <deleter regex='.*foo.*'/>" +
	"    <deleter regex='.*bar.*' offset='1'/>" +
	"    <deleter regex='.*baz.*\\$.*frog.*' multiple='\\$'/>" +
	"    <deleter regex='.*plugh.*\\$.*frog.*' multiple='\\$' offset='-1'/>" +
	"    <chunker mark='foo' regex='.*foo.*'/>" +
	"    <chunker mark='bar' regex='.*bar.*' offset='1'/>" +
	"    <chunker mark='baz' regex='.*baz.*\\$.*frog.*' multiple='\\$'/>" +
	"    <chunker mark='plugh' regex='.*plugh.*\\$.*frog.*' multiple='\\$' offset='-1'/>" +
	"  </chunkerList>" +
	"  <comment>foo</comment>" +
	"  <readLines id='r1' linesToRead='2'/>" +
	"  <record/>" +
	"  <comment role='DESCRIPTION'>bar</comment>" +
	"  <record/>" +
	"  <readLines id='r2' />" +
	"  <readLines/>" +
	"  <record/>" +
	"  <comment role='EXAMPLE'>baz</comment>" +
	"</template>";

	String document1S = 				
		"<!DOCTYPE template SYSTEM 'src/main/resources/org/xmlcml/cml/converters/text/text.dtd' [ " +
		"]>" +
		document0S;
		
	@Test
	public void testNoDTD() {
		String minimalS = "" +
				"<template/>";
		Element element = new CMLBuilder().parseString(minimalS);
		Assert.assertNotNull(element);
	}

	@Test
	public void testDTD() {
		String minimalS = "<?xml version='1.0' standalone='no'?>" +
				"<!DOCTYPE templates SYSTEM 'src/main/resources/org/xmlcml/cml/converters/text/text.dtd' [ " +
				"]>" +
				"<templates/>";
		Element element = new CMLBuilder().parseString(minimalS);
		Assert.assertNotNull(element);
	}

	@Test
	public void testDocumentYes() {
		String testS = "<?xml version='1.0' standalone='yes'?>" +
			document1S;
		Element element = new CMLBuilder().parseString(testS);
		Assert.assertNotNull(element);
		JumboTestUtils.assertEqualsCanonically("dtd", CMLUtil.parseXML(document0S), element);
	}

	@Test
	public void testDocumentNo() {
		String testS = "<?xml version='1.0' standalone='no'?>" +
			document1S;
		Element element = new CMLBuilder().parseString(testS);
		JumboTestUtils.assertEqualsCanonically("dtd", CMLUtil.parseXML(document0S), element);
	}

	@Test
	public void testTemplate1() {
		String templateS = "<template id='t1' name='test' pattern=''/>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		Assert.assertNotNull(template);
		Assert.assertEquals("deleterList", 0, template.getDeleterList().size());
		Assert.assertNull("templateList", template.getTemplateContainer());
		Assert.assertEquals("linereaders", 0, template.getLineReaderList().size());
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
//		Assert.assertEquals("pattern", "\\s*\\s*", template.getPattern().pattern());
		Assert.assertEquals("outputLevel", OutputLevel.NONE, template.getOutputLevel());
		template.debug();
	}

	@Test
	public void testTemplate2Fail() {
		String templateS = 
			"<template id='t1' name='test' pattern=''>" +
			"  <deleter/>" +
			"  <templateList>" +
			"    <template id='t11' name='temp11' pattern='p11'/>" +
			"    <template id='t12' name='temp12' pattern='p12'/>" +
			"  </templateList>" +
			"  <readLines/>" +
			"  <record/>" +
			"</template>";
		try {
			@SuppressWarnings("unused")
			Template template = new Template(CMLUtil.parseXML(templateS));
			Assert.fail("exception should be thrown");
		} catch (Exception e) {
//			Assert.assertTrue("exception thrown", true);
		}
	}

	@Test
	public void testTemplate2a() {
		String templateS = 
			"<template id='t1' name='test' pattern=''>" +
			"  <deleter id='d1' name='d' pattern=''/>" +
			"  <templateList>" +
			"    <template id='t11' name='temp11' pattern='p11'/>" +
			"    <template id='t12' name='temp12' pattern='p12'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		Assert.assertNotNull(template);
		Assert.assertEquals("deleterList", 1, template.getDeleterList().size());
		Assert.assertNotNull("templateList", template.getTemplateContainer());
		Assert.assertEquals("templateList", 2, template.getTemplateContainer().getTemplateList().size());
		Assert.assertEquals("linereaders", 0, template.getLineReaderList().size());
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("pattern", "", template.getPattern().pattern());
		Assert.assertEquals("outputLevel", OutputLevel.NONE, template.getOutputLevel());
		template.debug();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTemplate2b() {
		String templateS = 
			"<template id='t1' name='test' pattern=''>" +
			"  <readLines/>" +
			"  <record/>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		Assert.assertNotNull(template);
		Assert.assertEquals("deleterList", 0, template.getDeleterList().size());
		Assert.assertNull("templateContainer", template.getTemplateContainer());
		Assert.assertEquals("linereaders", 2, template.getLineReaderList().size());
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("pattern", "", template.getPattern().pattern());
		Assert.assertEquals("pattern", OutputLevel.NONE, template.getOutputLevel());
		template.debug();
	}


	@Test
	public void testTemplateNoDelete() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line1\n"+
			"line2\n"+
			"p11\n"+
			"line4\n"+
			"line5\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		lineContainer.debug("LineContainer");
	}
	

	@Test
	public void testTemplateDelete() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <deleter id='a' name='n' pattern='.*1.*'/>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line1\n"+
			"line2\n"+
			"p11\n"+
			"line4\n"+
			"line5\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		lineContainer.debug("LineContainer");
	}
	
	@Test
	public void testTemplateTemplate1() {
		String templateS = 
			"<template pattern='.*1.*' offset='0' id='t1' name='template 1' endPattern='.*3.*' endOffset='1'/>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 0\nendChunker [.*3.*] | 1\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultEndOffset() {
		String templateS = 
			"<template pattern='.*1.*' offset='2' id='t1' name='template 1' endPattern='.*3.*'/>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 2\nendChunker [.*3.*] | 2\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultEndPattern() {
		String templateS = 
			"<template pattern='.*1.*' offset='2' id='t1' name='template 1' />";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 2\nendChunker [.*1.*] | 2\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultOffset() {
		String templateS = 
			"<template pattern='.*1.*' id='t1' name='template 1' />";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 0\nendChunker [.*1.*] | 0\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultOffset1() {
		String templateS = 
			"<template pattern='.*1.*' id='t1' name='template 1' endOffset='1'/>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 0\nendChunker [.*1.*] | 1\n", s);
	}
	
	@Test
	public void testTemplateChunking() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*1.*' id='t1' name='template 1' endOffset='1'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line1\n"+
			"line2\n"+
			"p11\n"+
			"line4\n"+
			"line5\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		lineContainer.debug("LineContainer");
	}
	
	@Test
	public void testTemplateChunkingOffset00() {
		String templateS = 
			"<template id='t0' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*2.*' offset='0' id='t1' name='template 1' endPattern='.*5.*' endOffset='0'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"line2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"line6\n"+
			"line7\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = "<lineContainer>line0line1"+
		  "<chunk templateRef='t1'>line2line3line4</chunk>line5line6line7"+
		  "</lineContainer>";

		JumboTestUtils.assertEqualsCanonically("offset 00", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffset10() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*2.*' offset='1' id='t1' name='template 1' endPattern='.*5.*' endOffset='0'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"line2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"line6\n"+
			"line7\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
		"<lineContainer>line0line1line2"+
		  "<chunk templateRef='t1'>line3line4</chunk>line5line6line7"+
		"</lineContainer>";

		JumboTestUtils.assertEqualsCanonically("offset 10", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffset01() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*2.*' offset='0' id='t1' name='template 1' endPattern='.*5.*' endOffset='1'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"line2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"line6\n"+
			"line7\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		String refS = 
			"<lineContainer>line0line1"+
			  "<chunk templateRef='t1'>line2line3line4line5</chunk>line6line7"+
			"</lineContainer>";
		Assert.assertNotNull(lineContainer);
		JumboTestUtils.assertEqualsCanonically("offset 10", refS, lineContainer.getLinesElement(), true);
	}
	
	
	@Test
	public void testTemplateChunkingOffset11() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*2.*' offset='1' id='t1' name='template 1' endPattern='.*5.*' endOffset='1'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"line2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"line6\n"+
			"line7\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS =
			"<lineContainer>line0line1line2"+
			  "<chunk templateRef='t1'>line3line4line5</chunk>line6line7"+
			"</lineContainer>";

		JumboTestUtils.assertEqualsCanonically("offset 10", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffsetRepeat() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*2.*' offset='1' id='t1' name='template 1' endPattern='.*5.*' endOffset='1'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"line2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"line6\n"+
			"line7\n"+
			"line0\n"+
			"line1a\n"+
			"line2a\n"+
			"line3a\n"+
			"line4a\n"+
			"line5a\n"+
			"line6a\n"+
			"line7a\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = "<lineContainer>line0line1line2"+
"<chunk templateRef='t1'>line3line4line5</chunk>line6line7line0line1aline2a"+
"<chunk templateRef='t1'>line3aline4aline5a</chunk>line6aline7a"+
"</lineContainer>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
//	@Ignore // FIXME not yet right
	public void testTemplateChunkingOffsetRepeat1() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*\\-\\-\\-\\-.*' offset='0' id='t1' name='template 1' endPattern='.*\\-\\-\\-.*' endOffset='-1'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"-----\n"+
			"line2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"-----\n"+
			"line6\n"+
			"line7\n"+
			"line0\n"+
			"line1a\n"+
			"-----\n"+
			"line2a\n"+
			"line3a\n"+
			"line4a\n"+
			"line5a\n"+
			"line6a\n"+
			"line7a\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = "<lineContainer>line0line1"+
  "<chunk templateRef='t1'>-----line2line3line4</chunk>line5-----line6line7line0line1a"+
  "<chunk templateRef='t1'/>-----line2aline3aline4aline5aline6aline7a"+
"</lineContainer>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffsetRepeat2() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*ssss.*' offset='0' id='t1' name='template 1' endPattern='.*eeee.*' endOffset='1'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1\n"+
			"ssss2\n"+
			"line3\n"+
			"line4\n"+
			"line5\n"+
			"eeee6\n"+
			"line7\n"+
			"line8\n"+
			"line9\n"+
			"line10\n"+
			"line11\n"+
			"ssss12\n"+
			"line13\n"+
			"line14\n"+
			"line15\n"+
			"eeee16\n"+
			"line17\n"+
			"line18\n"+
			"line19\n"+
			"line20\n"+
			"line21\n"+
			"ssss22\n"+
			"line23\n"+
			"line24\n"+
			"line25\n"+
			"eeee26\n"+
			"line27\n"+
			"line28\n"+
			"line29\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
//		<lineContainer>line0line1
//		  <chunk>line5+++++</chunk>line7line8line9line10line11
//		  <chunk>line15+++++</chunk>line17line18line19
//		</lineContainer>
		
		String refS = "<lineContainer>line0line1"+
  "<chunk templateRef='t1'>ssss2line3line4line5eeee6</chunk>line7line8line9line10line11"+
  "<chunk templateRef='t1'>ssss12line13line14line15eeee16</chunk>line17line18line19line20line21"+
  "<chunk templateRef='t1'>ssss22line23line24line25eeee26</chunk>line27line28line29"+
"</lineContainer>";

		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testNestedTemplates() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*a.*' offset='0' id='ta' name='template A' endPattern='.*b.*' endOffset='0'>" +
			"      <templateList>" +
			"        <template pattern='.*c.*' offset='0' id='tb' name='template B' endPattern='.*d.*' endOffset='0'>" +
			"        </template>" +
			"      </templateList>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"line0\n"+
			"line1a\n"+
			"line2\n"+
			"line3\n"+
			"line4c\n"+
			"line5\n"+
			"line6\n"+
			"line7\n"+
			"line8\n"+
			"line9\n"+
			"line10\n"+
			"line11\n"+
			"line12d\n"+
			"line13\n"+
			"line14\n"+
			"line15\n"+
			"line16b\n"+
			"line17\n"+
			"line18\n"+
			"line19\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = "<lineContainer>line0"+
  "<chunk templateRef='ta'>line1aline2line3"+
    "<chunk templateRef='tb'>line4cline5line6line7line8line9line10line11</chunk>line12dline13line14line15"+
  "</chunk>line16bline17line18line19"+
"</lineContainer>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
}
