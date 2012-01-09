package org.xmlcml.cml.converters.compchem.dalton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlcml.cml.converters.compchem.dalton.log.DaltonLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class DaltonConverters extends ConverterListImpl {

    public DaltonConverters() {
    	super();
    	// needs adjusting templates
//        list.add(new ConverterInfo(DaltonLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }


}
