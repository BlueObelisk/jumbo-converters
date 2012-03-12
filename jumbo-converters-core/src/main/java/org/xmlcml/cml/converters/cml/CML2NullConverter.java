package org.xmlcml.cml.converters.cml;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.NullConverter;
import org.xmlcml.cml.converters.Type;

public class CML2NullConverter extends NullConverter implements
		Converter {

	private static final Logger LOG = Logger.getLogger(CML2NullConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.NULL;
	}

	/** this is a no-op except for debug.
	 * @param infile
	 * @param outfile
	 */
	protected void debug(File infile) {
		LOG.debug("file "+infile.getAbsolutePath());
	}
	
	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}


}
