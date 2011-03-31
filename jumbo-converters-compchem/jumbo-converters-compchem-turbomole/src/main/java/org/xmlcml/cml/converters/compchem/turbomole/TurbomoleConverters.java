package org.xmlcml.cml.converters.compchem.turbomole;

import org.xmlcml.cml.converters.compchem.turbomole.log.TurbomoleLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class TurbomoleConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("turbomole-log", "turbomole-log-xml", TurbomoleLog2XMLConverter.class, "Turbomole Log to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
