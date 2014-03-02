package org.xmlcml.cml.converters.compchem.amber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlcml.cml.converters.compchem.amber.in.AmberFF2XMLConverter;
import org.xmlcml.cml.converters.compchem.amber.in.AmberFFXML2CMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class AmberConverters extends ConverterListImpl {


    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo(AmberFF2XMLConverter.class));
        list.add(new ConverterInfo(AmberFFXML2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
