package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.gamessus.log.GamessUSXLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class GamessUSXConverters extends ConverterListImpl {

    public GamessUSXConverters() {
    	// needs adjusting templates
//        list.add(new ConverterInfo(GamessUSXLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
