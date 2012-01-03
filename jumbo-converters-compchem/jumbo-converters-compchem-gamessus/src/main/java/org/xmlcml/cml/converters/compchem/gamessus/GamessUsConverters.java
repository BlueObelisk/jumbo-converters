package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.gamessus.log.GamessUSLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class GamessUSConverters extends ConverterListImpl {

    public GamessUSConverters() {
        list.add(new ConverterInfo(GamessUSLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
