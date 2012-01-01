package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.gamessus.log.GamessusLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class GamessUsConverters extends ConverterListImpl {

    public GamessUsConverters() {
        list.add(new ConverterInfo(GamessusLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
