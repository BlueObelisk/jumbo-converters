package org.xmlcml.cml.converters.compchem.dlpoly;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.dlpoly.config.DLPolyConfig2CMLConverter;
import org.xmlcml.cml.converters.compchem.dlpoly.log.DLPolyLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author pm286
 */
public class DLPolyConverters extends ConverterListImpl {

    public DLPolyConverters() {
    	super();
        list.add(new ConverterInfo(DLPolyLog2XMLConverter.class));
        list.add(new ConverterInfo(DLPolyConfig2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
