package org.xmlcml.cml.converters.compchem.qespresso;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.qespresso.log.QuantumEspressoLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class QespressoConverters extends ConverterListImpl {

    public QespressoConverters() {
    	// needs adjusting templates
//        list.add(new ConverterInfo(QuantumEspressoLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
