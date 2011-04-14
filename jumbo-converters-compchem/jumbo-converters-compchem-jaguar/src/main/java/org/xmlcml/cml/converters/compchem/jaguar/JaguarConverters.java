package org.xmlcml.cml.converters.compchem.jaguar;

import org.xmlcml.cml.converters.compchem.jaguar.log.JaguarLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author PeterMR
 */
public class JaguarConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo(
        		"jaguar-log", "jaguar-log-xml", JaguarLog2XMLConverter.class, "Jaguar Log to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
