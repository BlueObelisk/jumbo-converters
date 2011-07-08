package org.xmlcml.cml.converters.compchem.nwchem.merge;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.XML2XMLConverter;
import org.xmlcml.cml.converters.text.XML2XMLTransformConverter;
import org.xmlcml.cml.converters.util.ConverterUtils;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

public class CMLMerger extends XML2XMLTransformConverter {
	private static final Logger LOG = Logger.getLogger(CMLMerger.class);
	static {
		LOG.setLevel(Level.INFO);
	}	

	// dummy filename?
	private static final String DEFAULT_TEMPLATE_RESOURCE = 
		"org/xmlcml/cml/converters/compchem/nwchem/merge/precomp2postcomp.xml";
	
	private static final String BASE_URI = 
		"classpath:/"+DEFAULT_TEMPLATE_RESOURCE;
	
	public CMLMerger(File transformFile) throws IOException {
		super(transformFile);
	}
	public CMLMerger(InputStream inputStream) throws IOException {
		super(inputStream);
	}
	// dummy name?
	public CMLMerger() {
		this(BASE_URI, "precomp2postcomp.xml");
	}
	
	public CMLMerger(String baseUri, String templateName) {
		this(ConverterUtils.buildElementIncludingBaseUri(
				baseUri, templateName, CMLMerger.class));
	}
	
	public CMLMerger(Element templateElement) {
		super(templateElement);
	}

	/**
	 * converts an Foo to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element xml) {
		return super.convertToXML(xml);
	}

	private void runTests(String dirName) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		if (files == null) {
			throw new RuntimeException("No files found in "+dir.getAbsolutePath());
		}
		LOG.info("Processing "+files.length+" files");
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".out")) {
				File out = new File(file.getAbsolutePath()+".cml");
//					if (!out.exists()) {
					System.out.println("converting "+file+" to "+out);
					this.convert(file, out);
//					}
			}
		}
	}
	

	public static void main(String[] args) throws IOException {
		if (args.length == 1) {
			CMLMerger converter = new CMLMerger();
			converter.runTests(args[0]);
		} else if (args.length == 2) {
			CMLMerger converter = new CMLMerger(BASE_URI,
			"templates/"+args[1]);
			converter.runTests(args[0]);
		} else {
			String transformResource = "org/xmlcml/cml/converters/compchem/nwchem/merge/precomp2postcomp.xml";
			String infilename = "src/test/resources/org/xmlcml/cml/converters/compchem/nwchem/merge/in/test1.xml";
			String outfilename = "src/test/resources/org/xmlcml/cml/converters/compchem/nwchem/merge/out/test1.xml";
			InputStream transformStream = Util.getResourceUsingContextClassLoader(
					transformResource, CMLMerger.class);
			XML2XMLConverter converter = new CMLMerger(transformStream);
			File in = new File(infilename);
			File out = new File(outfilename);
			converter.convert(in, out);
			Element testNode = CMLUtil.parseQuietlyIntoCML(out);
//			Element refNode = CMLUtil.parseQuietlyIntoCML(new File(reffilename));
//			convert("ch3f", 
//					"org/xmlcml/cml/converters/compchem/nwchem/log/nwchem2compchem.xml",
//					"src/test/resources/compchem/nwchem/log/ref/ch3f_rot.cml",
//					"src/test/resources/compchem/nwchem/log/refcompchem/ch3f_rot.cml",
//					"test/ch3f_rot.compchem.xml");
//			convert("fukui", 
//					"org/xmlcml/cml/converters/compchem/nwchem/log/nwchem2compchem.xml",
//					"src/test/resources/compchem/nwchem/log/ref/fukuilite.xml",
//					"src/test/resources/compchem/nwchem/log/refcompchem/fukuilite.cml",
//					"test/fukuilite.compchem.xml");
		}
	}
	
	private static void convert(String title, String transformResource, String infilename, String reffilename,
			String outfilename) throws FileNotFoundException, IOException {
		InputStream transformStream = Util.getResourceUsingContextClassLoader(
			transformResource, CMLMerger.class);
		XML2XMLConverter converter = new CMLMerger(transformStream);
		File in = new File(infilename);
		File out = new File(outfilename);
		converter.convert(in, out);
		Element testNode = CMLUtil.parseQuietlyIntoCML(out);
		Element refNode = CMLUtil.parseQuietlyIntoCML(new File(reffilename));
//		JumboTestUtils.assertEqualsCanonically(title, refNode, testNode, true);
	}
}
