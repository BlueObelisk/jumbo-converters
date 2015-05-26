package org.xmlcml.cml.converters.compchem.qchem;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.qchem.log.QchemLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author ostueker
 */
public class QchemConverters extends ConverterListImpl {

    public QchemConverters() {
    	// needs adjusting templates
//        list.add(new ConverterInfo(QchemLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
