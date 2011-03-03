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
	"  <moduleerList>" +
	"    <deleter regex='.*foo.*'/>" +
	"    <deleter regex='.*bar.*' offset='1'/>" +
	"    <deleter regex='.*baz.*\\$.*frog.*' multiple='\\$'/>" +
	"    <deleter regex='.*plugh.*\\$.*frog.*' multiple='\\$' offset='-1'/>" +
	"    <moduleer mark='foo' regex='.*foo.*'/>" +
	"    <moduleer mark='bar' regex='.*bar.*' offset='1'/>" +
	"    <moduleer mark='baz' regex='.*baz.*\\$.*frog.*' multiple='\\$'/>" +
	"    <moduleer mark='plugh' regex='.*plugh.*\\$.*frog.*' multiple='\\$' offset='-1'/>" +
	"  </moduleerList>" +
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
//		Assert.assertEquals("deleterList", 0, template.getDeleterList().size());
//		Assert.assertNull("templateList", template.getTemplateContainer());
//		Assert.assertEquals("linereaders", 0, template.getLineReaderList().size());
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
//		Assert.assertEquals("pattern", "\\s*\\s*", template.getPattern().pattern());
		Assert.assertEquals("outputLevel", OutputLevel.NONE, template.getOutputLevel());
//		template.debug();
	}

	@Test
	public void testTemplate2OK() {
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
			Assert.assertTrue("exception should not now be thrown", true);
		} catch (Exception e) {
			Assert.fail("exception should not be thrown");
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
//		Assert.assertEquals("deleterList", 1, template.getDeleterList().size());
//		Assert.assertNotNull("templateList", template.getTemplateContainer());
//		Assert.assertEquals("templateList", 2, template.getTemplateContainer().getTemplateList().size());
//		Assert.assertEquals("linereaders", 0, template.getLineReaderList().size());
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("pattern", "", template.getPattern().pattern());
		Assert.assertEquals("outputLevel", OutputLevel.NONE, template.getOutputLevel());
//		template.debug();
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
//		Assert.assertEquals("deleterList", 0, template.getDeleterList().size());
//		Assert.assertNull("templateContainer", template.getTemplateContainer());
//		Assert.assertEquals("linereaders", 2, template.getLineReaderList().size());
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("pattern", "", template.getPattern().pattern());
		Assert.assertEquals("pattern", OutputLevel.NONE, template.getOutputLevel());
//		template.debug();
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
//		lineContainer.debug("LineContainer");
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
//		lineContainer.debug("LineContainer");
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
		Assert.assertEquals("template", "startChunker [.*1.*] | 2\nendChunker [.*3.*] | 0\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultEndPattern() {
		String templateS = 
			"<template pattern='.*1.*' offset='2' id='t1' name='template 1' />";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 2\nendChunker [.*1.*] | 0\n", s);
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
//		lineContainer.debug("LineContainer");
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
		String refS = "<module templateRef=\"t0\" xmlns=\"http://www.xml-cml.org/schema\">line0line1"+
		  "<module lineCount=\"3\" templateRef='t1'>line2line3line4</module>line5line6line7"+
		  "</module>";

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
		"<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1line2"+
		  "<module lineCount='2' templateRef='t1'>line3line4</module>line5line6line7"+
		"</module>";

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
			"<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1"+
			  "<module lineCount='4' templateRef='t1'>line2line3line4line5</module>line6line7"+
			"</module>";
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
			"<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1line2"+
			  "<module lineCount='3' templateRef='t1'>line3line4line5</module>line6line7"+
			"</module>";

		JumboTestUtils.assertEqualsCanonically("offset 10", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffsetRepeat() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*2.*' offset='1' id='t1' name='template 1' endPattern='.*5.*' endOffset='1'/>" +
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
		String refS = 
			"<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1line2"+
			"<module lineCount='3' templateRef='t1'>line3line4line5</module>line6line7line0line1aline2a"+
			"<module lineCount='3' templateRef='t1'>line3aline4aline5a</module>line6aline7a"+
			"</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore // FIXME not yet right // loops
	public void testTemplateChunkingOffsetRepeat1() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*\\-\\-\\-\\-.*' offset='0' id='t1' name='template 1' endPattern='.*\\-\\-\\-.*' endOffset='-1'/>" +
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
		String refS = "<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1"+
  "<module templateRef='t1'>-----line2line3line4</module>line5-----line6line7line0line1a"+
  "<module templateRef='t1'/>-----line2aline3aline4aline5aline6aline7a"+
"</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffsetRepeat2() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*ssss.*' offset='0' id='t1' name='template 1' endPattern='.*eeee.*' endOffset='1'/>" +
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
//		<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1
//		  <module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line5+++++</module>line7line8line9line10line11
//		  <module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line15+++++</module>line17line18line19
//		</module>
		
		String refS = "<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0line1"+
  "<module lineCount='5' templateRef='t1'>ssss2line3line4line5eeee6</module>line7line8line9line10line11"+
  "<module lineCount='5' templateRef='t1'>ssss12line13line14line15eeee16</module>line17line18line19line20line21"+
  "<module lineCount='5' templateRef='t1'>ssss22line23line24line25eeee26</module>line27line28line29"+
"</module>";

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
		String refS = "<module templateRef='t1' xmlns='http://www.xml-cml.org/schema'>line0"+
  "<module lineCount='15' templateRef='ta'>line1aline2line3"+
    "<module lineCount='8' templateRef='tb'>line4cline5line6line7line8line9line10line11</module>line12dline13line14line15"+
  "</module>line16bline17line18line19"+
"</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testNestedTemplates2() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*chapter.*' offset='0' id='chapter' name='template A' endPattern='.*====.*' endOffset='0'>" +
			"      <templateList>" +
			"        <template repeatCount='*' pattern='.*section.*' offset='0' id='section' name='template B' endPattern='.*####.*' endOffset='0'>" +
			"        </template>" +
			"      </templateList>" +
			"    </template>" +
			"    <template repeatCount='*' pattern='.*line.*' offset='0' id='chapterLine' name='template A' endPattern='.*line.*' endOffset='0'/>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"line1\n"+
			"line1a\n"+
			"line1b\n"+
			"line2\n"+
			"chapter1\n"+
			"line4\n"+
			"section 1\n"+
			"line6\n"+
			"line7\n"+
			"line8\n"+
			"####\n"+
			"line10\n"+
			"section 2\n"+
			"line12d\n"+
			"####\n"+
			"line14\n"+
			"====\n"+
			"line16b\n"+
			"line17\n"+
			"line18\n"+
			"line19\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			"<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='1' templateRef='chapterLine'>line1</module>"+
			  "<module lineCount='1' templateRef='chapterLine'>line1a</module>"+
			  "<module lineCount='1' templateRef='chapterLine'>line1b</module>"+
			  "<module lineCount='1' templateRef='chapterLine'>line2</module>"+
			  "<module lineCount='12' templateRef='chapter'>chapter1line4"+
			    "<module lineCount='4' templateRef='section'>section 1line6line7line8</module>####line10"+
			    "<module lineCount='2' templateRef='section'>section 2line12d</module>####line14"+
			  "</module>===="+
			  "<module lineCount='1' templateRef='chapterLine'>line16b</module>"+
			  "<module lineCount='1' templateRef='chapterLine'>line17</module>"+
			  "<module lineCount='1' templateRef='chapterLine'>line18</module>line19"+
			"</module>"+
		    "";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
//		Assert.fail("wrong output");
		
	}
	
	@Test
	public void testStartEnd00() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*start.*' offset='0' id='chapter' " +
			"        name='template A' endPattern='.*end.*' endOffset='0'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"end1\n"+
			"start2\n"+
			"end2\n"+
			"start3\n"+
			"end3\n"+
			"start4\n"+
			"end4\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
		  "<module templateRef=\"book\" xmlns=\"http://www.xml-cml.org/schema\">book"+
  		    "<module lineCount=\"1\" templateRef=\"chapter\">start1</module>end1"+
		    "<module lineCount=\"1\" templateRef=\"chapter\">start2</module>end2"+
		    "<module lineCount=\"1\" templateRef=\"chapter\">start3</module>end3"+
		    "<module lineCount=\"1\" templateRef=\"chapter\">start4</module>end4"+
		  "</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testStartEnd01() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*start.*' offset='0' id='chapter' name='template A' " +
			"        endPattern='.*end.*' endOffset='1'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"end1\n"+
			"start2\n"+
			"end2\n"+
			"start3\n"+
			"end3\n"+
			"start4\n"+
			"end4\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='2' templateRef=\"chapter\">start1end1</module>"+
			  "<module lineCount='2' templateRef=\"chapter\">start2end2</module>"+
			  "<module lineCount='2' templateRef=\"chapter\">start3end3</module>"+
			  "<module lineCount='2' templateRef=\"chapter\">start4end4</module>"+
			  "</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testStartStart0() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*start.*' offset='0' id='chapter' name='template A' " +
			"        endPattern='.*start.*' endOffset='0'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"end1\n"+
			"start2\n"+
			"end2\n"+
			"start3\n"+
			"end3\n"+
			"start4\n"+
			"end4\n"+
			"start5\n"+
			"end5\n"+
			"start6\n"+
			"end6\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='2' templateRef='chapter'>start1end1</module>"+
			  "<module lineCount='2' templateRef='chapter'>start2end2</module>"+
			  "<module lineCount='2' templateRef='chapter'>start3end3</module>"+
			  "<module lineCount='2' templateRef='chapter'>start4end4</module>"+
			  "<module lineCount='2' templateRef='chapter'>start5end5</module>start6end6"+
			  "</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
//		Assert.fail("wrong output");
	}
	
	@Test
	public void testRepeatCount() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template pattern='.*start.*' offset='0' id='chapter' name='repeat defaults to 1,1' " +
			"        endPattern='.*end.*' endOffset='1'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"content1a\n"+
			"end1\n"+
			"skip1\n"+
			"start2\n"+
			"content2a\n"+
			"end2\n"+
			"skip2\n"+
			"start3\n"+
			"content3a\n"+
			"end3\n"+
			"skip3\n"+
			"start4\n"+
			"content4a\n"+
			"end4\n"+
			"skip4\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='3' templateRef='chapter'>start1content1aend1</module>skip1start2content2aend2skip2start3content3aend3skip3start4content4aend4skip4"+
			  "</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}

	@Test
	public void testRepeatCount1() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='3' pattern='.*start.*' offset='0' id='chapter' name='template A' " +
			"        endPattern='.*end.*' endOffset='1'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"content1a\n"+
			"end1\n"+
			"skip1\n"+
			"start2\n"+
			"content2a\n"+
			"end2\n"+
			"skip2\n"+
			"start3\n"+
			"content3a\n"+
			"end3\n"+
			"skip3\n"+
			"start4\n"+
			"content4a\n"+
			"end4\n"+
			"skip4\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='3' templateRef='chapter'>start1content1aend1</module>skip1"+
			  "<module lineCount='3' templateRef='chapter'>start2content2aend2</module>skip2"+
			  "<module lineCount='3' templateRef='chapter'>start3content3aend3</module>skip3start4content4aend4skip4"+
			  "</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testMultipleChildren() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*start2.*' offset='0' id='start2Ref' name='start2' " +
			"        endPattern='.*end.*' endOffset='1'>" +
			"    </template>" +
			"    <template repeatCount='*' pattern='.*start1.*' offset='0' id='start1Ref' name='start1' " +
			"        endPattern='.*end.*' endOffset='1'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"content1a\n"+
			"end1\n"+
			"skip1\n"+
			"start2\n"+
			"content2a\n"+
			"end2\n"+
			"skip2\n"+
			"start3\n"+
			"content3a\n"+
			"end3\n"+
			"skip3\n"+
			"start1\n"+
			"content1b\n"+
			"end1\n"+
			"skip1\n"+
			"start4\n"+
			"content4a\n"+
			"end4\n"+
			"skip4\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='3' templateRef='start1Ref'>start1content1aend1</module>skip1"+
			  "<module lineCount='3' templateRef='start2Ref'>start2content2aend2</module>skip2start3content3aend3skip3" +
			  "<module lineCount='3' templateRef='start1Ref'>start1content1bend1</module>skip1start4content4aend4skip4"+
			  "</module>";
		
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testMultipleChildren1() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*start1.*' offset='0' id='start1Ref' name='start1' " +
			"        endPattern='.*end.*' endOffset='1'>" +
			"    </template>" +
			"    <template repeatCount='*' pattern='.*start2.*' offset='1' id='start2Ref' name='start2' " +
			"        endPattern='.*end.*' endOffset='0'>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"book\n"+
			"start1\n"+
			"content1a\n"+
			"end1\n"+
			"skip1\n"+
			"start2\n"+
			"content2a\n"+
			"end2\n"+
			"skip2\n"+
			"start3\n"+
			"content3a\n"+
			"end3\n"+
			"skip3\n"+
			"start1\n"+
			"content1b\n"+
			"end1\n"+
			"skip1\n"+
			"start4\n"+
			"content4a\n"+
			"end4\n"+
			"skip4\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module templateRef='book' xmlns='http://www.xml-cml.org/schema'>book"+
			  "<module lineCount='3' templateRef='start1Ref'>start1content1aend1</module>skip1start2" +
			  "<module lineCount='1' templateRef='start2Ref'>content2a</module>end2skip2start3content3aend3skip3"+
			  "<module lineCount='3' templateRef='start1Ref'>start1content1bend1</module>skip1start4content4aend4skip4"+
			  "</module>";
		
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testChildRecords() {
		String templateS = 
		"<template id='site' name='site' repeatCount='*' pattern='.*site\\s+n.*' endPattern='\\s*'>"+
	      "<record linesToRead='1' formatType='REGEX' names='q:coord'>.*\\(([^\\)]*)\\).*</record>"+
	      "<record linesToRead='*' formatType='REGEX' " +
	      "   names='q:ser q:attyp q:ser1 q:x q:y q:z'" +
	      "   >\\s*(\\d+)\\s*([A-Za-z]+)\\s*tau\\(\\s*(\\d+)\\)\\s*=\\s*\\(\\s*([\\-\\d\\.]+)\\s+([\\-\\d\\.]+)\\s+([\\-\\d\\.]+)\\s*\\)</record>"+
	    "</template>"+
	    "";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
"site n. atom positions (cryst. coord.)\n"+
" 1 C tau( 1) = ( -0.1463680 0.0000000 0.0000000 )\n"+
" 2 C tau( 2) = ( 0.0000000 0.1463680 0.0000000 )\n"+
" 3 C tau( 3) = ( 0.0000000 -0.1463680 0.0000000 )\n"+
" 4 C tau( 4) = ( 0.1463680 0.0000000 0.0000000 )\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
		"<?xml version='1.0' encoding='UTF-8'?>"+
		"<module templateRef='site' xmlns='http://www.xml-cml.org/schema'>"+
		  "<list templateRef='nullId' xmlns='http://www.xml-cml.org/schema'>"+
		    "<scalar dataType='xsd:string' dictRef='q:coord'>cryst. coord.</scalar>"+
		  "</list>"+
		  "<list templateRef='nullId' xmlns='http://www.xml-cml.org/schema'>"+
		    "<list>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser'>1</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:attyp'>C</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser1'>1</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:x'>-0.1463680</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:y'>0.0000000</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:z'>0.0000000</scalar>"+
		    "</list>"+
		    "<list>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser'>2</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:attyp'>C</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser1'>2</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:x'>0.0000000</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:y'>0.1463680</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:z'>0.0000000</scalar>"+
		    "</list>"+
		    "<list>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser'>3</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:attyp'>C</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser1'>3</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:x'>0.0000000</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:y'>-0.1463680</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:z'>0.0000000</scalar>"+
		    "</list>"+
		    "<list>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser'>4</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:attyp'>C</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:ser1'>4</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:x'>0.1463680</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:y'>0.0000000</scalar>"+
		      "<scalar dataType='xsd:string' dictRef='q:z'>0.0000000</scalar>"+
		    "</list>"+
		  "</list>"+
		"</module>"+
		  "";
		  JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testChildRecords1() {
		String templateS = 
		"<template id='site' name='site' repeatCount='*' pattern='.*site\\s+n.*' endPattern='\\s*'>"+
	      "<record linesToRead='1' formatType='REGEX' names='q:coord'>.*\\(([^\\)]*)\\).*</record>"+
	      "<record linesToRead='*' makeArray='true' formatType='REGEX' " +
	      "   names='q:ser q:attyp q:ser1 q:x q:y q:z'" +
	      "   >\\s*(\\d+)\\s*([A-Za-z]+)\\s*tau\\(\\s*(\\d+)\\)\\s*=\\s*\\(\\s*([\\-\\d\\.]+)\\s+([\\-\\d\\.]+)\\s+([\\-\\d\\.]+)\\s*\\)</record>"+
	    "</template>"+
	    "";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
"site n. atom positions (cryst. coord.)\n"+
" 1 C tau( 1) = ( -0.1463680 0.0000000 0.0000000 )\n"+
" 2 C tau( 2) = ( 0.0000000 0.1463680 0.0000000 )\n"+
" 3 C tau( 3) = ( 0.0000000 -0.1463680 0.0000000 )\n"+
" 4 C tau( 4) = ( 0.1463680 0.0000000 0.0000000 )\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
		"<?xml version='1.0' encoding='UTF-8'?>"+
		"<module templateRef='site' xmlns='http://www.xml-cml.org/schema'>"+
		  "<list templateRef='nullId' xmlns='http://www.xml-cml.org/schema'>"+
		    "<scalar dataType='xsd:string' dictRef='q:coord'>cryst. coord.</scalar>"+
		  "</list>"+
		  "<list lineCount='4' templateRef='nullId' xmlns='http://www.xml-cml.org/schema'>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:ser' size='4'>1 2 3 4</array>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:attyp' size='4'>C C C C</array>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:ser1' size='4'>1 2 3 4</array>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:x' size='4'>-0.1463680 0.0000000 0.0000000 0.1463680</array>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:y' size='4'>0.0000000 0.1463680 -0.1463680 0.0000000</array>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:z' size='4'>0.0000000 0.0000000 0.0000000 0.0000000</array>"+
		  "</list>"+
		"</module>"+
		"";
		  JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testChildRecords2() {
		String templateS = 
		"<template id='site' name='site' repeatCount='*' pattern='.*site\\s+n.*' endPattern='\\s*'>"+
	      "<record linesToRead='1' formatType='REGEX' names='q:coord'>.*\\(([^\\)]*)\\).*</record>"+
	      "<record linesToRead='*' makeArray='true' formatType='REGEX' " +
	      "   >{I,q:ser}{A,q:attyp}tau\\({I,q:ser1}\\) *= *\\({F,q:x} {F,q:y} {F,q:z}\\)</record>"+
	    "</template>"+
	    "";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
"site n. atom positions (cryst. coord.)\n"+
" 1 C tau( 1) = ( -0.1463680 0.0000000 0.0000000 )\n"+
" 2 C tau( 2) = ( 0.0000000 0.1463680 0.0000000 )\n"+
" 3 C tau( 3) = ( 0.0000000 -0.1463680 0.0000000 )\n"+
" 4 C tau( 4) = ( 0.1463680 0.0000000 0.0000000 )\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
		"<?xml version='1.0' encoding='UTF-8'?>"+
		"<module templateRef='site' xmlns='http://www.xml-cml.org/schema'>"+
		  "<list templateRef='nullId' xmlns='http://www.xml-cml.org/schema'>"+
		    "<scalar dataType='xsd:string' dictRef='q:coord'>cryst. coord.</scalar>"+
		  "</list>"+
		  "<list lineCount='4' templateRef='nullId' xmlns='http://www.xml-cml.org/schema'>"+
		    "<array delimiter='' dataType='xsd:integer' dictRef='q:ser' size='4'>1 2 3 4</array>"+
		    "<array delimiter='' dataType='xsd:string' dictRef='q:attyp' size='4'>C C C C</array>"+
		    "<array delimiter='' dataType='xsd:integer' dictRef='q:ser1' size='4'>1 2 3 4</array>"+
		    "<array delimiter='' dataType='xsd:double' dictRef='q:x' size='4'>-0.146368 0.0 0.0 0.146368</array>"+
		    "<array delimiter='' dataType='xsd:double' dictRef='q:y' size='4'>0.0 0.146368 -0.146368 0.0</array>"+
		    "<array delimiter='' dataType='xsd:double' dictRef='q:z' size='4'>0.0 0.0 0.0 0.0</array>"+
		  "</list>"+
		"</module>"+
		"";
		  JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
}
