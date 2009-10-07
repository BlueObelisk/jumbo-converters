package org.xmlcml.cml.converters.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;

public class PDF2SVGConverter extends AbstractConverter {
	
	private static final Logger LOG = Logger.getLogger(PDF2SVGConverter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	/** output files are of form split123.svg */
	private String outputFileRoot = "split%d";
	private String tempFileRoot = "temp";
	private String PSTOEDIT = "pstoedit";
	private String OUTFORMAT = "-f plot-svg";
	private String SPLIT = "-split";
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/pdf",
		"-odir", "../temp",
		"-is", "pdf",
		"-os", "cml",
		"-converter", "org.xmlcml.cml.converters.pdf.PDF2SVGConverter"
	};
	
	public Type getInputType() {
		return Type.PDF;
	}

	public Type getOutputType() {
		return Type.SVG;
	}

	/**
	 * converts a CML object to MDL. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param in
	 *            input stream
	 */
	@Override
	public Element convertToXML(byte[] bytes) {
		String classpath = System.getProperties().getProperty("java.class.path");
		System.out.println("CP "+classpath);
//		try {
//			Runtime.getRuntime().exec("echo %classpath%");
//		} catch (Exception e) {
//			LOG.error("RUNTIME failed: "+e);
//		}
		Element xml = null;
		File tempDir = org.xmlcml.euclid.Util.getTEMP_DIRECTORY();
		try {
			FileUtils.forceMkdir(tempDir);
			FileUtils.forceDelete(tempDir);
			FileUtils.forceMkdir(tempDir);
		} catch (IOException e1) {
			throw new RuntimeException("Cannot make temp directory: "+e1);
		}
		String infilename = tempFileRoot + CMLConstants.S_PERIOD + Type.PDF.getDefaultExtension();
		File infile = new File(tempDir, infilename);
		LOG.debug("FILE "+infile.getAbsolutePath());
		try {
			FileOutputStream fos = new FileOutputStream(infile);
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Problem writing bytes "+e, e);
		}
		String command = PSTOEDIT+" "+OUTFORMAT+" "+SPLIT+" "+
		    infile.getAbsolutePath()+" "+
		    tempDir+File.separator+outputFileRoot+CMLConstants.S_PERIOD+Type.SVG.getDefaultExtension();
		LOG.debug(command);
		try {
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			LOG.error("RUNTIME failed: "+e);
		}
		
		return xml;
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}

}
