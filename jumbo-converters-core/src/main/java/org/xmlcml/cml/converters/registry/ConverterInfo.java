package org.xmlcml.cml.converters.registry;

import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Converter;

/**
 * @author Sam Adams
 */
public class ConverterInfo {

    private String intype;
    private String outtype;
    private String name;
    private Class<? extends Converter> converterClass;

//    public ConverterInfo(String intype, String outtype, Class<? extends Converter> converterClass, String name) {
//        setFields(intype, outtype, converterClass, name);
//    }
//
	private void setFields(String intype, String outtype,
			Class<? extends Converter> converterClass, String name) {
		this.intype = intype;
        this.outtype = outtype;
        this.converterClass = converterClass;
        this.name = name;
	}
    
    public ConverterInfo(Class<? extends Converter> converterClass) {
    	try {
			AbstractConverter converter = (AbstractConverter) converterClass.newInstance();
			setFields(converter.getRegistryInputType(), converter.getRegistryOutputType(), converterClass, converter.getRegistryMessage());
		} catch (Exception e) {
			throw new RuntimeException("cannot create converter: "+converterClass, e);
		}
    	
    }

    public String getInType() {
        return intype;
    }

    public String getOutType() {
        return outtype;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Converter> getConverterClass() {
        return converterClass;
    }
    
    public String toString() {
    	return intype+"; "+outtype+"; "+name;
    }

}
