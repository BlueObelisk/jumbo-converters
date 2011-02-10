package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlcml.cml.converters.compchem.amber.in.AmberFF2XMLConverter;
import org.xmlcml.cml.converters.compchem.gaussian.log.GaussianLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

/**
 * @author Sam Adams
 */
public class CompChemConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("amber-ff", "amber-ff-xml", AmberFF2XMLConverter.class, "Amber-FF to Amber-FF-XML"));
        list.add(new ConverterInfo("gau-log", "gau-log-xml", GaussianLog2XMLConverter.class, "GaussianLog to GaussianLogXML"));

        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
