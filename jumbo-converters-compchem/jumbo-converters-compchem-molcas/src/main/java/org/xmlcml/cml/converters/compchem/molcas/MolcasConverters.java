package org.xmlcml.cml.converters.compchem.molcas;

import org.xmlcml.cml.converters.compchem.molcas.log.MolcasLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class MolcasConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("molcas-log", "molcas-log-xml", MolcasLog2XMLConverter.class, "Molcas Log to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
