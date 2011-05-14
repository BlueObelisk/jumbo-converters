package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Point3;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class FreemarkerTextConverter extends XML2TextConverter {
	private static final Logger LOG = Logger.getLogger(FreemarkerTextConverter.class);
	private Configuration cfg;

	public FreemarkerTextConverter() {
	}
	
	@Override
	public List<String> convertToText(Element xmlInput) {
		List<String> stringList = new ArrayList<String>();
		return stringList;
	}

	protected static String convertToText(String inputFile, String xslFile) {
		String s = null;
		try {
			Document inputDoc = new Builder().build(new FileInputStream(inputFile));
			Document xslDoc = new Builder().build(new FileInputStream(xslFile));
			FreemarkerTextConverter converter = new FreemarkerTextConverter();
			converter.runFTL();
			List<String> lines = converter.convertToText(inputDoc.getRootElement());
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(line);
			}
			s = sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("Failed Freemarker", e);
		}
		return s;
	}
	
	private void createConfiguration() {
		cfg = new Configuration();
		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {
			cfg.setDirectoryForTemplateLoading(
		        new File("src/main/resources/org/xmlcml/cml/converters/text/ftl/"));
		} catch (Exception e) {
			throw new RuntimeException("Cannot create configuration", e);
		}
		// Specify how templates will see the data-model. This is an advanced topic...
		// but just use this:
		cfg.setObjectWrapper(new DefaultObjectWrapper());  	}
	
	private void runFTL() {
		createConfiguration();
	}
	
	private void test() {
		createConfiguration();
        /* ------------------------------------------------------------------- */    
        /* You usually do these for many times in the application life-cycle:  */    
                
        /* Get or create a template */
        Template ftlTemplate = null;
        try {
			ftlTemplate = cfg.getTemplate("test.ftl");
		} catch (IOException e) {
			throw new RuntimeException("cannot create template", e);
		}

        /* Create a data-model */
        Map root = new HashMap();
        root.put("user", "Big Joe");
        Map latest = new HashMap();
        root.put("latestProduct", latest);
        latest.put("url", "products/greenmouse.html");
        latest.put("name", "green mouse");
/*
  <#list job.module['init'].atoms as atom>
${atom.elementType}  ${atom.x3}  ${atom.y3}  ${atom.z3}
</#list>
 */
        CMLMolecule molecule = new CMLMolecule();
        molecule.setId("mol");
        for (int i = 0; i < 10; i++) {
        	CMLAtom atom = new CMLAtom("a"+(i+1));
        	atom.setElementType("C");
        	atom.setXYZ3(new Point3(1.1*i, 2.2*i, 3.3*i));
        	molecule.addAtom(atom);
        }
        root.put("molecule", molecule);
//        Map atoms = new HashMap();
//        root.put("atoms", atoms);
//        List<CMLAtom> atomList = molecule.getAtoms();
//        root.put("atomList", atomList);
//        for (CMLAtom atom : atomList) {
//        	atoms.put(atom.getId(), atom);
//        }

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(System.out);
        try {
			ftlTemplate.process(root, out);
	        out.flush();
		} catch (Exception e) {
			throw new RuntimeException("cannot run template", e);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		String inputFile = (args.length > 0) ? args[0] : "src/main/java/org/xmlcml/cml/converters/text/test.xml";
		String xslFile = (args.length > 1) ? args[1] : "src/main/java/org/xmlcml/cml/converters/text/test.xsl";
		String textFile = (args.length > 2) ? args[2] : "test/test.out";
		new FreemarkerTextConverter().test();
		String text = convertToText(inputFile, xslFile);
		FileUtils.write(new File(textFile), text);
	}

}
