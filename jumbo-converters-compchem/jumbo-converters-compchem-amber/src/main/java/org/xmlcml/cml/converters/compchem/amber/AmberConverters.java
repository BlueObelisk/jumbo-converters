package org.xmlcml.cml.converters.compchem.amber;

import org.xmlcml.cml.converters.compchem.amber.in.AmberFF2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class AmberConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("amber-ff", "amber-ff-xml", AmberFF2XMLConverter.class, "Amber-FF to Amber-FF-XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
