package org.xmlcml.cml.converters.compchem.turbomole;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.turbomole.log.TurbomoleLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class TurbomoleConverters extends ConverterListImpl {

    public TurbomoleConverters() {
    	// needs adjusting templates
//        list.add(new ConverterInfo(TurbomoleLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
