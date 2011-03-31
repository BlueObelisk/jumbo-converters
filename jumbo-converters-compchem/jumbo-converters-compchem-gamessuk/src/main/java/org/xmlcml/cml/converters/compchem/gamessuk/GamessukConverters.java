package org.xmlcml.cml.converters.compchem.gamessuk;

import org.xmlcml.cml.converters.compchem.gamessuk.log.GamessukLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class GamessukConverters implements ConverterList {

    private final List<ConverterInfo> list;

    {
        List<ConverterInfo> list = new ArrayList<ConverterInfo>();
        list.add(new ConverterInfo("gamessuk-log", "gamessuk-log-xml", GamessukLog2XMLConverter.class, "GamessUk Log to XML"));
        this.list = Collections.unmodifiableList(list);
    }

    public List<ConverterInfo> listConverters() {
        return list;
    }


}
