package org.xmlcml.cml.converters.testutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Element;

public class TemplateTester {

	private Class templateTestClass;
	private String logDir;
	private String testDir;
	private String templateDir;
	private String inputTemplateS;
	private String baseUri;
	private String codeBase;
	private String fileType;
	private String inSuffix;
	
	public TemplateTester(String codeBase, String fileType, String inSuffix, Class<?> templateTestClass) {
		this.codeBase = codeBase;
		this.fileType = fileType;
		this.inSuffix = inSuffix;
		this.templateTestClass = templateTestClass;
		logDir = "org/xmlcml/cml/converters/compchem/"+codeBase+"/"+fileType+"/";
		testDir = "src/test/resources/compchem/"+codeBase+"/"+fileType+"/";
		templateDir = logDir+"templates/";
		inputTemplateS = templateDir+"topTemplate.xml";
		baseUri = "classpath:/"+templateDir;
	}
	
	public void runCommentExamples() {
		TemplateTestUtils.runCommentExamples(inputTemplateS, baseUri);
	}
	
	public void runTestOnFile(String test) {
		String inputName = testDir+"in/"+test+inSuffix;
		String refName = testDir+"ref/"+test+".xml";
		String outName = testDir+"out/"+test+".xml";
		try {
			runTestOnFile(inputName, refName, outName);
		} catch(Exception e) {
			throw new RuntimeException("cannot read file", e);
		}
	}

	private void runTestOnFile(String inputName, String refName, String outName)
			throws FileNotFoundException, IOException {
		InputStream inputStream = new FileInputStream(inputName);
		InputStream refStream = null;
//		refStream = new FileInputStream(refName);
		OutputStream outputStream = new FileOutputStream(outName);
		TemplateTestUtils.testDocument(inputStream, refStream, outputStream, 
				inputTemplateS, baseUri, false);
	}

	public void runTemplateCommentExamples(String baseUriOffset, String templateName) {
		Element template = TemplateTestUtils.getTemplate(templateDir+baseUriOffset+templateName+".xml", baseUri+baseUriOffset);
		if (template == null) {
			throw new RuntimeException("Cannot create template: "+templateName);
		}
		TemplateTestUtils.runCommentExamples(template);
	}

	public void runTemplateCommentExamples(String templateName) {
		Element template = TemplateTestUtils.getTemplate(templateDir+templateName+".xml", baseUri);
		if (template == null) {
			throw new RuntimeException("Cannot create template: "+templateName);
		}
		TemplateTestUtils.runCommentExamples(template);
	}


}
