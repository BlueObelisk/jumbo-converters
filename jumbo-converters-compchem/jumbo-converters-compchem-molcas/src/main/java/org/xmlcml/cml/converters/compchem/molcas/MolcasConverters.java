package org.xmlcml.cml.converters.compchem.molcas;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.molcas.log.MolcasLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class MolcasConverters extends ConverterListImpl {

    public MolcasConverters() {
    	// needs adjusting templates
//        list.add(new ConverterInfo(MolcasLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }


}
