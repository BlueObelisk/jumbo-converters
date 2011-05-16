package org.xmlcml.cml.converters.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static final String DEFAULT_TEMPLATE_DIRECTORY = 
		"src/main/resources/org/xmlcml/cml/converters/text/ftl/";

	private static final String DEFAULT_TEMPLATE = "test.ftl";
	
	protected Configuration cfg;
	protected String directoryForTemplates;

	public FreemarkerTextConverter() {
		init();
	}
	
	protected void init() {
		super.init();
		directoryForTemplates = DEFAULT_TEMPLATE_DIRECTORY;
		createConfiguration();
	}
	
	@Override
	public List<String> convertToText(Element xmlInput) {
		List<String> stringList = new ArrayList<String>();
		StringWriter stringWriter = new StringWriter();
		convertXMLtoWriter(stringWriter);
		stringList.add(stringWriter.toString());
		return stringList;
	}

	protected void createConfiguration() {
		cfg = new Configuration();
		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {
			cfg.setDirectoryForTemplateLoading(
		        getTemplateDirectory());
		} catch (Exception e) {
			throw new RuntimeException("Cannot create configuration", e);
		}
		// Specify how templates will see the data-model. This is an advanced topic...
		// but just use this:
		cfg.setObjectWrapper(new DefaultObjectWrapper());  	
	}

	protected File getTemplateDirectory() {
		return new File(directoryForTemplates);
	}
	
	protected void convertXMLtoWriter(Writer writer) {
		createConfiguration();
        /* ------------------------------------------------------------------- */    
        /* You usually do these for many times in the application life-cycle:  */    
                
        /* Get or create a template */
        Template ftlTemplate = null;
        try {
			ftlTemplate = cfg.getTemplate(DEFAULT_TEMPLATE);
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
//        Writer out = new OutputStreamWriter(System.out);
        try {
			ftlTemplate.process(root, writer);
	        writer.flush();
		} catch (Exception e) {
			throw new RuntimeException("cannot run template", e);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		String inputFile = (args.length > 0) ? args[0] : "src/main/java/org/xmlcml/cml/converters/text/test.xml";
		String textFile = (args.length > 2) ? args[2] : "test/test.out";
		List<String> stringList = new FreemarkerTextConverter().convertToText(new FileInputStream(inputFile));
		StringBuilder sb = new StringBuilder();
		for (String s : stringList) {
			sb.append(s);
		}
		FileUtils.write(new File(textFile), sb.toString());
	}

}
