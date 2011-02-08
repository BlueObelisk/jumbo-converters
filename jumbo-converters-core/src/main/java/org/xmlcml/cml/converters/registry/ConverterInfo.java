package org.xmlcml.cml.converters.registry;

import org.xmlcml.cml.converters.Converter;

/**
 * @author Sam Adams
 */
public class ConverterInfo {

    private String intype;
    private String outtype;
    private String name;
    private Class<? extends Converter> converterClass;

    public ConverterInfo(String intype, String outtype, Class<? extends Converter> converterClass, String name) {
        this.intype = intype;
        this.outtype = outtype;
        this.converterClass = converterClass;
        this.name = name;
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

}
