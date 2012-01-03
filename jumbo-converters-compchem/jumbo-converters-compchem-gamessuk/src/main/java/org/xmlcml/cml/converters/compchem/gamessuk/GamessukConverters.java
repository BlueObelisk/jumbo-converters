package org.xmlcml.cml.converters.compchem.gamessuk;

import org.xmlcml.cml.converters.compchem.gamessuk.log.GamessUKLog2XMLConverter;
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
public class GamessUKConverters extends ConverterListImpl {

     public GamessUKConverters() {
         list.add(new ConverterInfo(GamessUKLog2XMLConverter.class));
         list.add(new ConverterInfo(GamessUKPunchXML2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
