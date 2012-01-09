package org.xmlcml.cml.converters.compchem.mopac;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.mopac.auxx.MopacAux2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class MopacConverters extends ConverterListImpl {

    public MopacConverters() {
    	// needs adjusting templates
//        list.add(new ConverterInfo(MopacAux2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
