package org.xmlcml.cml.converters.text;

import java.io.File;

import java.io.FileOutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.xinclude.XIncluder;
import nu.xom.xslt.XSLTransform;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;

public class TemplateDocumenter {
	private final static Logger LOG = Logger.getLogger(TemplateDocumenter.class);
	private Document templateDocument;
	private Document stylesheet;
	private Document htmlDocument;
	

	public TemplateDocumenter() {
	}
	
	private void readTemplates(String filename) throws Exception {
		File file = new File(filename);
		templateDocument = new Builder().build(file);
		try {
			XIncluder.resolveInPlace(templateDocument);
		} catch (Exception e) {
			throw new RuntimeException("Bad XInclude", e);
		}
		CMLUtil.removeWhitespaceNodes(templateDocument.getRootElement());
	}

	private void writeHtml(String outfile) throws Exception {
		FileOutputStream fos = new FileOutputStream(outfile);
		CMLUtil.debug(htmlDocument.getRootElement(),fos,1);
	}

	private void applyStyles() throws Exception {
	     XSLTransform transform = new XSLTransform(stylesheet);
	     Nodes nodes = transform.transform(templateDocument);
	     if (nodes.size() > 1) {
	    	 throw new RuntimeException("stylesheet returns more than one node");
	     }
	     htmlDocument = new Document((Element)nodes.get(0));
	}

	private void readStylesheet(String filename) throws Exception {
	     stylesheet = new Builder().build(filename);
	}

	private static void usage() {
		System.err.println("usage: <template_in> <html_out> <stylesheet>");
	}
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			usage();
		} else {
			TemplateDocumenter td = new TemplateDocumenter();
			td.readTemplates(args[0]);
			td.readStylesheet(args[2]);
			td.applyStyles();
			td.writeHtml(args[1]);
		}
	}

}
