package org.xmlcml.cml.converters.graphics;

import java.util.Collections;


import org.xmlcml.cml.converters.graphics.osra.Graphics2CMLConverter;
import org.xmlcml.cml.converters.graphics.png.CML2PNGConverter;
import org.xmlcml.cml.converters.graphics.svg.CML2SVGConverter;
import org.xmlcml.cml.converters.graphics.svg.SVG2CMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class GraphicsConverters extends ConverterListImpl {

    public GraphicsConverters() {
        list.add(new ConverterInfo(Graphics2CMLConverter.class));
        list.add(new ConverterInfo(CML2PNGConverter.class));
        list.add(new ConverterInfo(CML2SVGConverter.class));
        list.add(new ConverterInfo(SVG2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
