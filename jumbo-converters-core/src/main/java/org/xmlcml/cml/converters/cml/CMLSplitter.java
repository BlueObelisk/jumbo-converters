package org.xmlcml.cml.converters.cml;

import static org.xmlcml.euclid.EuclidConstants.S_PERIOD;

import static org.xmlcml.euclid.EuclidConstants.S_UNDER;

import java.io.File;
import java.io.FileOutputStream;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractSplitter;
import org.xmlcml.cml.converters.Type;

/** splits file into multiple parts using xpath.
 * 
 * @author pm286
 *
 */
public class CMLSplitter extends AbstractSplitter {

	private static final Logger LOG = Logger.getLogger(CMLSplitter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected String xPath;

	protected String outDirname;

	protected String outputName;

	public CMLSplitter() {
		this.xpath = xPath; 
//		this.outputFile = new File(outputName);
		this.outputDirectory = new File(outDirname);
	}

	@Override
	protected void outputNode(Node node, int serial) {
		if (node == null) {
			throw new RuntimeException("Cannot output null node");
		}
		String filenameS = createFilename(outputDirectory, outputFile, serial);
		try {
			LOG.debug(" wrote FILE "+filenameS+" .. "+new File(filenameS).getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(filenameS);
			CMLUtil.debug((Element)node, fos, 0);
			IOUtils.closeQuietly(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String createFilename(File outdir, File file, int serial) {
		String filename = outdir+File.separator+FilenameUtils.getName(file.toString())+S_UNDER+serial;
		
		String outsuffix = getOutputType().getDefaultExtension();
		if (outsuffix != null) {
			filename += S_PERIOD+outsuffix;
		}
		return filename;
	}

	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	public static void main(String[] args) throws Exception {
		CMLSplitter splitter = new CMLSplitter();
		String infile = (args.length > 0) ? args[0] : "junk";
		String outputDirectory = (args.length > 1) ? args[1] : "out";
		String outputFile = (args.length > 2) ? args[2] : "molecule";
		String xpath = (args.length > 3) ? args[3] : "./*[local-name()='molecule']";
		splitter.setOutputDirectory(new File(outputDirectory));
		splitter.setOutputFile(new File(outputFile));
		splitter.setXpath(xpath);
		splitter.convertToXML(new File(infile));
	}
}
