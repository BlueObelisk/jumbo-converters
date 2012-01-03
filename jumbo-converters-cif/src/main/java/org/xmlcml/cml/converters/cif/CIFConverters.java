package org.xmlcml.cml.converters.cif;

import java.util.Collections;

import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class CIFConverters extends ConverterListImpl {

    public CIFConverters() {
        list.add(new ConverterInfo(CIF2CIFXMLConverter.class));
        list.add(new ConverterInfo(CIF2CMLConverter.class));
        list.add(new ConverterInfo(CIFXML2CMLConverter.class));
        list.add(new ConverterInfo(RawCML2CompleteCMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
