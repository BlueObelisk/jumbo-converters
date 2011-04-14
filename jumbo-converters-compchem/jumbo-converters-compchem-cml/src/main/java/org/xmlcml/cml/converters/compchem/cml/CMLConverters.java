package org.xmlcml.cml.converters.compchem.cml;

import org.xmlcml.cml.converters.compchem.cml.cml.CML2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author PeterMR
 */
public class CMLConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo(
        		"cml-cml", "cml-cml-xml", CML2XMLConverter.class, "CML to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
