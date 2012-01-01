package org.xmlcml.cml.converters.compchem.cml;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.cml.cml.CML2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author PeterMR
 */
public class CMLConverters extends ConverterListImpl {

    public CMLConverters() {
        list.add(new ConverterInfo(CML2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
