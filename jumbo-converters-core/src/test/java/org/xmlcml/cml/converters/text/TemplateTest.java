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
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("outputLevel", OutputLevel.NONE, template.getOutputLevel());
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
			"  <record/>" +
			"  <record/>" +
			"</template>";
		try {
			@SuppressWarnings("unused")
			Template template = new Template(CMLUtil.parseXML(templateS));
			Assert.assertTrue("exception should not now be thrown", true);
		} catch (Exception e) {
			Assert.fail("exception should not be thrown "+ e);
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
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("pattern", "", template.getPattern().pattern());
		Assert.assertEquals("outputLevel", OutputLevel.NONE, template.getOutputLevel());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTemplate2b() {
		String templateS = 
			"<template id='t1' name='test' pattern=''>" +
			"  <record/>" +
			"  <record/>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		Assert.assertNotNull(template);
		Assert.assertEquals("id", "t1", template.getId());
		Assert.assertEquals("name", "test", template.getName());
		Assert.assertEquals("pattern", "", template.getPattern().pattern());
		Assert.assertEquals("pattern", OutputLevel.NONE, template.getOutputLevel());
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
	@Ignore // loops
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
		Assert.assertEquals("template", "startChunker [.*1.*] | 2\nendChunker [~] | 0\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultOffset() {
		String templateS = 
			"<template pattern='.*1.*' id='t1' name='template 1' />";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 0\nendChunker [~] | 0\n", s);
	}
	
	@Test
	public void testTemplateTemplateDefaultOffset1() {
		String templateS = 
			"<template pattern='.*1.*' id='t1' name='template 1' endOffset='1'/>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String s = template.toString();
		Assert.assertEquals("template", "startChunker [.*1.*] | 0\nendChunker [~] | 1\n", s);
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
		String refS = 
		  "<module cmlx:templateRef=\"t0\" xmlns=\"http://www.xml-cml.org/schema\" xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\nline1\n"+
		    "<module cmlx:lineCount=\"3\" cmlx:templateRef='t1'>line2\nline3\nline4\n</module>line5\nline6\nline7\n"+
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
		"<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\nline1\nline2\n"+
		  "<module cmlx:lineCount='2' cmlx:templateRef='t1'>line3\nline4\n</module>line5\nline6\nline7\n"+
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
			"<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\nline1\n"+
			  "<module cmlx:lineCount='4' cmlx:templateRef='t1'>line2\nline3\nline4\nline5\n</module>line6\nline7\n"+
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
			"<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\nline1\nline2\n"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='t1'>line3\nline4\nline5\n</module>line6\nline7\n"+
			"</module>";

		JumboTestUtils.assertEqualsCanonically("offset 10", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore
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
			"<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\nline1\nline2\n"+
			"  <module cmlx:lineCount='3' cmlx:templateRef='t1'>line3\nline4\nline5\n</module>line6\nline7\nline0\nline1a\nline2a\n"+
			"  <module cmlx:lineCount='3' cmlx:templateRef='t1'>line3a\nline4a\nline5a\n</module>line6a\nline7a\n"+
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
		String refS = "<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0line1"+
  "<module cmlx:templateRef='t1'>-----line2line3line4</module>line5-----line6line7line0line1a"+
  "<module cmlx:templateRef='t1'/>-----line2aline3aline4aline5aline6aline7a"+
"</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	public void testTemplateChunkingOffsetRepeat2() {
		String templateS = 
			"<template id='t1' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*ssss.*' offset='0' id='t1' " +
			"      name='template 1' endPattern='.*eeee.*' endOffset='1'/>" +
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
//		<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0line1
//		  <module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line5+++++</module>line7line8line9line10line11
//		  <module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line15+++++</module>line17line18line19
//		</module>
		
		String refS = "<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\nline1\n"+
  "<module cmlx:lineCount='5' cmlx:templateRef='t1'>ssss2\nline3\nline4\nline5\neeee6\n</module>line7\nline8\nline9\nline10\nline11\n"+
  "<module cmlx:lineCount='5' cmlx:templateRef='t1'>ssss12\nline13\nline14\nline15\neeee16\n</module>line17\nline18\nline19\nline20\nline21\n"+
  "<module cmlx:lineCount='5' cmlx:templateRef='t1'>ssss22\nline23\nline24\nline25\neeee26\n</module>line27\nline28\nline29\n"+
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
		String refS = "<module cmlx:templateRef='t1' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>line0\n"+
  "<module cmlx:lineCount='15' cmlx:templateRef='ta'>line1a\nline2\nline3\n"+
    "<module cmlx:lineCount='8' cmlx:templateRef='tb'>line4c\nline5\nline6\nline7\nline8\nline9\nline10\nline11\n</module>line12d\nline13\nline14\nline15\n"+
  "</module>line16b\nline17\nline18\nline19\n"+
"</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore
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
//		String refS = 
//			<module cmlx:templateRef="book" xmlns="http://www.xml-cml.org/schema" xmlns:cmlx="http://www.xml-cml.org/schema/cmlx">book
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line1</module>
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line1a</module>
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line1b</module>
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line2</module>
//			  <module cmlx:lineCount="12" cmlx:templateRef="chapter">chapter1line4
//			    <module cmlx:lineCount="4" cmlx:templateRef="section">section 1line6line7line8</module>####line10
//			    <module cmlx:lineCount="2" cmlx:templateRef="section">section 2line12d</module>####line14
//			  </module>====
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line16b</module>
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line17</module>
//			  <module cmlx:lineCount="1" cmlx:templateRef="chapterLine">line18</module>line19
//			</module>
//		    "";
//		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
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
		  "<module cmlx:templateRef=\"book\" xmlns=\"http://www.xml-cml.org/schema\" xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book\n"+
  		    "<module cmlx:lineCount=\"1\" cmlx:templateRef=\"chapter\">start1\n</module>end1\n"+
		    "<module cmlx:lineCount=\"1\" cmlx:templateRef=\"chapter\">start2\n</module>end2\n"+
		    "<module cmlx:lineCount=\"1\" cmlx:templateRef=\"chapter\">start3\n</module>end3\n"+
		    "<module cmlx:lineCount=\"1\" cmlx:templateRef=\"chapter\">start4\n</module>end4\n"+
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
			  "<module cmlx:templateRef='book' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book\n"+
			  "<module cmlx:lineCount='2' cmlx:templateRef=\"chapter\">start1\nend1\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef=\"chapter\">start2\nend2\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef=\"chapter\">start3\nend3\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef=\"chapter\">start4\nend4\n</module>"+
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
			  "<module cmlx:templateRef='book' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book\n"+
			  "<module cmlx:lineCount='2' cmlx:templateRef='chapter'>start1\nend1\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef='chapter'>start2\nend2\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef='chapter'>start3\nend3\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef='chapter'>start4\nend4\n</module>"+
			  "<module cmlx:lineCount='2' cmlx:templateRef='chapter'>start5\nend5\n</module>start6\nend6\n"+
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
			  "<module cmlx:templateRef='book' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book\n"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='chapter'>start1\ncontent1a\nend1\n</module>skip1\nstart2\ncontent2a\nend2\nskip2\nstart3\ncontent3a\nend3\nskip3\nstart4\ncontent4a\nend4\nskip4\n"+
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
			  "<module cmlx:templateRef='book' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book\n"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='chapter'>start1\ncontent1a\nend1\n</module>skip1\n"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='chapter'>start2\ncontent2a\nend2\n</module>skip2\n"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='chapter'>start3\ncontent3a\nend3\n</module>skip3\nstart4\ncontent4a\nend4\nskip4\n"+
			  "</module>";
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore // needs refactoring
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
			  "<module cmlx:templateRef='book' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='start1Ref'>start1content1aend1</module>skip1"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='start2Ref'>start2content2aend2</module>skip2start3content3aend3skip3" +
			  "<module cmlx:lineCount='3' cmlx:templateRef='start1Ref'>start1content1bend1</module>skip1start4content4aend4skip4"+
			  "</module>";
		
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore // needs refactoring
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
			  "<module cmlx:templateRef='book' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>book"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='start1Ref'>start1content1aend1</module>skip1start2" +
			  "<module cmlx:lineCount='1' cmlx:templateRef='start2Ref'>content2a</module>end2skip2start3content3aend3skip3"+
			  "<module cmlx:lineCount='3' cmlx:templateRef='start1Ref'>start1content1bend1</module>skip1start4content4aend4skip4"+
			  "</module>";
		
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore // FIXME
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
		"<module cmlx:templateRef='site' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		  "<list cmlx:templateRef='nullId' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		    "<scalar dataType='xsd:string' dictRef='q:coord'>cryst. coord.</scalar>"+
		  "</list>"+
		  "<list cmlx:templateRef='nullId' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
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
	@Ignore // FIXME needs names fixing
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
		"<module cmlx:templateRef='site' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		  "<list cmlx:templateRef='nullId' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		    "<scalar dataType='xsd:string' dictRef='q:coord'>cryst. coord.</scalar>"+
		  "</list>"+
		  "<list cmlx:lineCount='4' cmlx:templateRef='nullId' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		    "<array dataType='xsd:string' dictRef='q:ser' size='4'>1 2 3 4</array>"+
		    "<array dataType='xsd:string' dictRef='q:attyp' size='4'>C C C C</array>"+
		    "<array dataType='xsd:string' dictRef='q:ser1' size='4'>1 2 3 4</array>"+
		    "<array dataType='xsd:string' dictRef='q:x' size='4'>-0.1463680 0.0000000 0.0000000 0.1463680</array>"+
		    "<array dataType='xsd:string' dictRef='q:y' size='4'>0.0000000 0.1463680 -0.1463680 0.0000000</array>"+
		    "<array dataType='xsd:string' dictRef='q:z' size='4'>0.0000000 0.0000000 0.0000000 0.0000000</array>"+
		  "</list>"+
		"</module>"+
		"";
		  JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
	@Test
	@Ignore // FIXME
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
		"<module cmlx:templateRef='site' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		  "<list cmlx:templateRef='nullId' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		    "<scalar dataType='xsd:string' dictRef='q:coord'>cryst. coord.</scalar>"+
		  "</list>"+
		  "<list cmlx:lineCount='4' cmlx:templateRef='nullId' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		    "<array dataType='xsd:integer' dictRef='q:ser' size='4'>1 2 3 4</array>"+
		    "<array dataType='xsd:string' dictRef='q:attyp' size='4'>C C C C</array>"+
		    "<array dataType='xsd:integer' dictRef='q:ser1' size='4'>1 2 3 4</array>"+
		    "<array dataType='xsd:double' dictRef='q:x' size='4'>-0.146368 0.0 0.0 0.146368</array>"+
		    "<array dataType='xsd:double' dictRef='q:y' size='4'>0.0 0.146368 -0.146368 0.0</array>"+
		    "<array dataType='xsd:double' dictRef='q:z' size='4'>0.0 0.0 0.0 0.0</array>"+
		  "</list>"+
		"</module>"+
		"";
		  JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}
	
     @Test
     public void testCreateList() {
         String templateS = 
             "<template id='list' name='test' pattern='' dictRef=''>" +
             "  <transform process='addChild' xpath='.' elementName='cml:module' id='foo1' dictRef='bar:foo1'/>" +
             "  <transform process='addSibling' xpath='./cml:module' elementName='cml:list' id='foo2' dictRef='bar:foo2' position='1'/>" +
             "</template>";
         Template template = new Template(CMLUtil.parseXML(templateS));
         String toBeParsed = "Random text\n";
         template.applyMarkup(toBeParsed);
         LineContainer lineContainer = template.getLineContainer();
         Assert.assertNotNull(lineContainer);
         String refS = 
               "<module cmlx:templateRef='list' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>Random text\n"+
               "<list id='foo2' dictRef='bar:foo2'/>"+
               "<module id='foo1' dictRef='bar:foo1'/>"+
               "</module>";
         JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
     }
 
	
	@Test
	@Ignore // FIXME
	public void testTemplateRecords() {
		String templateS = 
			"<template id='book' name='test' pattern='' dictRef=''>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='.*start.*' id='s1' endPattern='.*end.*' endOffset='1'>" +
			"      <record id='r1' formatType='REGEX'>aaa(.*{I})</record>" +
			"      <record id='r2' formatType='REGEX'>bbb(.*{I})</record>" +
			"      <record/>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"skip0\n"+
			"start1\n"+
			"aaa1\n"+
			"bbb1\n"+
			""+
			"aaa2\n"+
			"bbb2\n"+
			"end1\n"+
			"skip1\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
			  "<module/>";
		
		JumboTestUtils.assertEqualsCanonically("offset 10 repeat", refS, lineContainer.getLinesElement(), true);
	}

	@Test
	public void testTemplateNew() {
		String templateS = 
			"<template>" +
			"  <templateList>" +
			"    <template repeatCount='1' pattern='start1\\s*' id='s1' endPattern='end1\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:x1}</record>" +
			"      <record id='r2'>{X,x:x2}</record>" +
			"      <record id='r2'>{X,x:x3}</record>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
			"start1\n"+
			"aaa1\n"+
			"end1\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
	    "<module cmlx:templateRef='NULL_ID' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s1'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x1'>start1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x2'>aaa1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x3'>end1</scalar>"+
		"   </list>"+
		" </module>"+
		"</module>"+
		"";
		
		JumboTestUtils.assertEqualsCanonically("test new", refS, lineContainer.getLinesElement(), true);
	}

	@Test
	public void testTemplateNew2() {
		String templateS = 
			"<template>" +
			"  <templateList>" +
			"    <template repeatCount='1' pattern='start1\\s*' id='s1' endPattern='end1\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:x1}</record>" +
			"      <record id='r2'>{X,x:x2}</record>" +
			"      <record id='r2'>{X,x:x3}</record>" +
			"    </template>" +
			"    <template repeatCount='1' pattern='start2\\s*' id='s2' endPattern='end2\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:y1}</record>" +
			"      <record id='r2'>{X,x:y2}</record>" +
			"      <record id='r2'>{X,x:y3}</record>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
		"start1\n"+
		"aaa1\n"+
		"end1\n"+
		"start2\n"+
		"aaa2\n"+
		"end2\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
	    "<module cmlx:templateRef='NULL_ID' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s1'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x1'>start1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x2'>aaa1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x3'>end1</scalar>"+
		"   </list>"+
		" </module>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s2'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y1'>start2</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y2'>aaa2</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y3'>end2</scalar>"+
		"   </list>"+
		" </module>"+
		"</module>"+
		"";
		
		JumboTestUtils.assertEqualsCanonically("test new", refS, lineContainer.getLinesElement(), true);
	}

	@Test
	public void testTemplateNew3() {
		String templateS = 
			"<template>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='start1\\s*' id='s1' endPattern='end1\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:x1}</record>" +
			"      <record id='r2'>{X,x:x2}</record>" +
			"      <record id='r2'>{X,x:x3}</record>" +
			"    </template>" +
			"    <template repeatCount='*' pattern='start2\\s*' id='s2' endPattern='end2\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:y1}</record>" +
			"      <record id='r2'>{X,x:y2}</record>" +
			"      <record id='r2'>{X,x:y3}</record>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
		"start1\n"+
		"aaa1\n"+
		"end1\n"+
		"start2\n"+
		"aaa2\n"+
		"end2\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
	    "<module cmlx:templateRef='NULL_ID' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s1'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x1'>start1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x2'>aaa1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x3'>end1</scalar>"+
		"   </list>"+
		" </module>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s2'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y1'>start2</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y2'>aaa2</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y3'>end2</scalar>"+
		"   </list>"+
		" </module>"+
		"</module>"+
		"";
		
		JumboTestUtils.assertEqualsCanonically("test new", refS, lineContainer.getLinesElement(), true);
	}


	@Test
	public void testTemplateNew4() {
		String templateS = 
			"<template>" +
			"  <templateList>" +
			"    <template repeatCount='*' pattern='start2\\s*' id='s2' endPattern='end2\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:y1}</record>" +
			"      <record id='r2'>{X,x:y2}</record>" +
			"      <record id='r2'>{X,x:y3}</record>" +
			"    </template>" +
			"    <template repeatCount='*' pattern='start1\\s*' id='s1' endPattern='end1\\s*' endOffset='1'>" +
			"      <record id='r1'>{X,x:x1}</record>" +
			"      <record id='r2'>{X,x:x2}</record>" +
			"      <record id='r2'>{X,x:x3}</record>" +
			"    </template>" +
			"  </templateList>" +
			"</template>";
		Template template = new Template(CMLUtil.parseXML(templateS));
		String toBeParsed = "" +
		"start1\n"+
		"aaa1\n"+
		"end1\n"+
		"start2\n"+
		"aaa2\n"+
		"end2\n"+
			"";
		template.applyMarkup(toBeParsed);
		LineContainer lineContainer = template.getLineContainer();
		Assert.assertNotNull(lineContainer);
		String refS = 
	    "<module cmlx:templateRef='NULL_ID' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s1'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x1'>start1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x2'>aaa1</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:x3'>end1</scalar>"+
		"   </list>"+
		" </module>"+
		" <module cmlx:lineCount='3' cmlx:templateRef='s2'>"+
		"    <list cmlx:templateRef='r1'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y1'>start2</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y2'>aaa2</scalar>"+
		"   </list>"+
		"   <list cmlx:templateRef='r2'>"+
		"     <scalar dataType='xsd:string' dictRef='x:y3'>end2</scalar>"+
		"   </list>"+
		" </module>"+
		"</module>"+
		"";
		
		JumboTestUtils.assertEqualsCanonically("test new", refS, lineContainer.getLinesElement(), true);
	}

}
