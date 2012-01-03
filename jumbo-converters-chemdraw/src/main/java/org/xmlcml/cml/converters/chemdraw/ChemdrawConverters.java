package org.xmlcml.cml.converters.chemdraw;

import java.util.Collections;

import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class ChemdrawConverters extends ConverterListImpl {

    public ChemdrawConverters() {
        list.add(new ConverterInfo(CDX2CDXMLConverter.class));
        list.add(new ConverterInfo(CDX2CMLConverter.class));
        list.add(new ConverterInfo(CDXML2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }


}
