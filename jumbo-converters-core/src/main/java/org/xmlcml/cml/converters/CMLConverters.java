package org.xmlcml.cml.converters;

import java.util.Collections;

import org.xmlcml.cml.converters.text.Text2XMLConverter;
import org.xmlcml.cml.converters.text.Text2XMLTemplateConverter;
import org.xmlcml.cml.converters.text.XML2TextConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class CMLConverters extends ConverterListImpl {

    public CMLConverters() {
//        list.add(new ConverterInfo(Text2XMLConverter.class));
//        list.add(new ConverterInfo(Text2XMLTemplateConverter.class));
//        list.add(new ConverterInfo(XML2TextConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
