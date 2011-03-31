package org.xmlcml.cml.converters.compchem.qespresso;

import org.xmlcml.cml.converters.compchem.qespresso.log.QuantumEspressoLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class QespressoConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("qespresso-log", "qespresso-log-xml", QuantumEspressoLog2XMLConverter.class, "Quantum Espresso Log to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
