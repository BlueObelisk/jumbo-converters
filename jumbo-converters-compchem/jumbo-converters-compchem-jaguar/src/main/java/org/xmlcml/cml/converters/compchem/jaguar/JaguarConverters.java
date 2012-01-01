package org.xmlcml.cml.converters.compchem.jaguar;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.jaguar.log.JaguarLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author PeterMR
 */
public class JaguarConverters extends ConverterListImpl {

    public JaguarConverters() {
        list.add(new ConverterInfo(JaguarLog2XMLConverter.class));
        list = Collections.unmodifiableList(list);
    }

}
