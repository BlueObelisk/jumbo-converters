package org.xmlcml.cml.converters.compchem.mopac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlcml.cml.converters.compchem.mopac.auxx.MopacAux2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

/**
 * @author Sam Adams
 */
public class MopacConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("mopac-aux", "mopac-aux-xml", MopacAux2XMLConverter.class, "Molcas Log to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
