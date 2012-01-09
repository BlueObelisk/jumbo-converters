package org.xmlcml.cml.converters.compchem.gamessuk;

import org.xmlcml.cml.converters.compchem.gamessuk.log.GamessUKXLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.gamessuk.punch.GamessUKXPunchXML2CMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class GamessUKXConverters extends ConverterListImpl {

     public GamessUKXConverters() {
         list.add(new ConverterInfo(GamessUKXLog2XMLConverter.class));
         list.add(new ConverterInfo(GamessUKXPunchXML2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
