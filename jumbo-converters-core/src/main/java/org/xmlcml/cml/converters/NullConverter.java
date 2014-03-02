package org.xmlcml.cml.converters;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** does nothing. Provides overridable debug() method
 * @author pm286
 *
 */
public abstract class NullConverter extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger(NullConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	/** subclasses can override this to allow debug output.
	 * @param file
	 */
	protected void debug(File file) {
		
	}
	/** this is a no-op except for debug.
	 * @param infile
	 * @param outfile
	 */
	@Override
	public void convert(File infile, File outfile) {
		debug(infile);
	}
	
	/** this is a no-op except for debug.
	 * @param infile
	 * @param os
	 */
	@Override
	public void convert(File infile, OutputStream os) {
		debug(infile);
	}
	
	/** this is a no-op except for debug.
	 * @param infile
	 * @return null
	 */
	@Override
	public byte[] convertToBytes(File infile) {
		debug(infile);
		return null;
	}
	
	/** this is a no-op except for debug.
	 * @param infile
	 * @return null
	 */
	@Override
	public List<String> convertToText(File infile) {
		debug(infile);
		return null;
	}
	
	/** this is a no-op except for debug.
	 * @param infile
	 * @return null
	 */
	@Override
	public Element convertToXML(File infile) {
		debug(infile);
		return null;
	}
}
