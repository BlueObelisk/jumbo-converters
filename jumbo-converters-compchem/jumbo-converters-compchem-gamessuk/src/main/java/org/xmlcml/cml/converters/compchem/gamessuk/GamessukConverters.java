package org.xmlcml.cml.converters.compchem.gamessuk;

import org.xmlcml.cml.converters.compchem.gamessuk.log.GamessukLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.gamessuk.punch.GamessUKPunchXML2CMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class GamessukConverters extends ConverterListImpl {

     public GamessukConverters() {
         list.add(new ConverterInfo(GamessukLog2XMLConverter.class));
         list.add(new ConverterInfo(GamessUKPunchXML2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
