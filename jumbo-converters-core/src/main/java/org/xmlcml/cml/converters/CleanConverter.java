package org.xmlcml.cml.converters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * deletes the input file
 *
 * @author pm286
 *
 */
public class CleanConverter extends AbstractConverter {

	public Type getInputType() {
		return null;
	}

	public Type getOutputType() {
		return null;
	}

	private static final Logger LOG = Logger.getLogger(CleanConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	/** subclasses can override this to allow delete strategies.
	 * @param file
	 */
	protected void delete(File file) {
		try {
			LOG.debug(file);
			FileUtils.forceDelete(file);
		} catch (IOException e) {
			converterLog.addToLog(Level.INFO, "deleted "+file);
			e.printStackTrace();
		}
	}
	/** delete infile
	 * @param infile
	 * @param outfile
	 */
	@Override
	public void convert(File infile, File outfile) {
		delete(infile);
	}

	/** delete infile.
	 * @param infile
	 * @param os
	 */
	@Override
	public void convert(File infile, OutputStream os) {
		delete(infile);
	}

	/** delete infile
	 * @param infile
	 * @return null
	 */
	@Override
	public byte[] convertToBytes(File infile) {
		delete(infile);
		return null;
	}

	/** delete infile.
	 * @param infile
	 * @return null
	 */
	@Override
	public List<String> convertToText(File infile) {
		delete(infile);
		return null;
	}

	/** delete infile.
	 * @param infile
	 * @return null
	 */
	@Override
	public Element convertToXML(File infile) {
		delete(infile);
		return null;
	}


	@Override
	public String getRegistryMessage() {
		return "Deletes files (through subclassing)";
	}
}
