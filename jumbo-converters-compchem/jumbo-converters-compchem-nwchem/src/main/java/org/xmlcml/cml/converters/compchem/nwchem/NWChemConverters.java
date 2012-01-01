package org.xmlcml.cml.converters.compchem.nwchem;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.nwchem.log.NWChemLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class NWChemConverters extends ConverterListImpl {

    public NWChemConverters() {
        list.add(new ConverterInfo(NWChemLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
