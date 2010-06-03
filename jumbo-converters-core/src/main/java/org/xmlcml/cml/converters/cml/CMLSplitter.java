package org.xmlcml.cml.converters.cml;

import static org.xmlcml.euclid.EuclidConstants.S_PERIOD;
import static org.xmlcml.euclid.EuclidConstants.S_UNDER;

import java.io.File;
import java.io.FileOutputStream;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.commons.io.FilenameUtils;
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
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "D:\\projects\\nature",
		"-is", "cdx.cml",
		"-os", "mol.cml",
		"-converter", "org.xmlcml.cml.converters.cml.CMLSplitter",
		"-xpath", "//cml:molecule"
	};
		
	public CMLSplitter() {
		
	}

	@Override
	protected void outputNode(Node node, int serial) {
		String fileExtension = getCommand().getOutSuffix();
		if (fileExtension == null) {
			throw new RuntimeException("must set output suffix");
		}
		if (node == null) {
			throw new RuntimeException("Cannot output null node");
		}
		String filenameS = createFilename(outputDirectory, outputFile, serial);
		try {
			LOG.debug(" wrote FILE "+filenameS+" .. "+new File(filenameS).getAbsolutePath());
			CMLUtil.debug((Element)node, new FileOutputStream(filenameS), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String createFilename(File outdir, File file, int serial) {
		String filename = outdir+File.separator+FilenameUtils.getName(file.toString())+S_UNDER+serial;
		String outsuffix = getCommand().getOutSuffix();
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

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
