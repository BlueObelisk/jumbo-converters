package org.xmlcml.cml.converters.cli;

import org.xmlcml.cml.converters.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Plumbing and IO wrangling for converter classes - allows converter
 * implementations to implement a single conversion method between the native
 * object types of the data types on input and output.
 * 
 * @author jimdowning
 * @author petermr
 */
public abstract class AbstractConverterFactory {

	private static final Logger LOG = Logger
			.getLogger(AbstractConverterFactory.class);
	static {
		LOG.setLevel(Level.INFO);
	}


	private static Map<String, Class<?>> converterMap = new HashMap<String, Class<?>>();
	static {
		// cif
		converterMap.put(Type.getFoo2BarString(Type.CIF, Type.XML),
				org.xmlcml.cml.converters.cif.CIF2CIFXMLConverter.class);
		// cml
		converterMap.put(Type.getFoo2BarString(Type.CML, Type.CML),
				org.xmlcml.cml.converters.molecule.cml.CML2CMLConverter.class);
		converterMap.put(Type.getFoo2BarString(Type.CML, null),
				org.xmlcml.cml.converters.cml.CML2NullConverter.class);
		// mdl
		converterMap.put(Type.getFoo2BarString(Type.CML, Type.MDL),
				org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter.class);
		converterMap.put(Type.getFoo2BarString(Type.MDL, Type.CML),
				org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter.class);
		// svg
		converterMap.put(Type.getFoo2BarString(Type.CML, Type.SVG),
				org.xmlcml.cml.converters.graphics.svg.CML2SVGConverter.class);
		// xyz
		converterMap.put(Type.getFoo2BarString(Type.CML, Type.XYZ),
				org.xmlcml.cml.converters.molecule.xyz.CML2XYZConverter.class);
		converterMap.put(Type.getFoo2BarString(Type.XYZ, Type.CML),
				org.xmlcml.cml.converters.molecule.xyz.XYZ2CMLConverter.class);
	}

	/** create instance of converter
	 * guess converter classname from input and output file suffixes
	 * calls createConverterName with this packagename 
	 * and format guessed from suffixes
	 * 
	 * @param inputSuffix (e.g. mol)
	 * @param outputSuffix (e.g. cml)
	 * @return (e.g. org.xmlcml.cml.converters.mdl.MOL2CMLConverter)
	 */
	public final static Class<?> createConverterClass(
			Type inputType, 
			Type outputType) {
		if (inputType == null) {
			throw new RuntimeException("null inputType");
		}
//		if (outputType == null) {
//			throw new RuntimeException("null outputType");
//		}
		String ss = Type.getFoo2BarString(inputType, outputType);
		Class<?> converterClass = converterMap.get(ss);
		if (converterClass == null) {
			throw new RuntimeException(
				"cannot find class to convert: " + 
				inputType.getMimeType() + " to "+((outputType == null) ? "null" :outputType.getMimeType()));
		}
		try {
			converterClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("cannot instantiate: "+converterClass, e);
		}
		return converterClass;
	}
}
