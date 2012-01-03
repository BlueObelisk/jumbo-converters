package org.xmlcml.cml.converters.spectrum;

import java.util.Collections;

import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;
import org.xmlcml.cml.converters.spectrum.jdx.CMLSpect2JDXConverter;
import org.xmlcml.cml.converters.spectrum.jdx.JDX2CMLConverter;
import org.xmlcml.cml.converters.spectrum.oscar.OSCAR2CMLSpectConverter;
import org.xmlcml.cml.converters.spectrum.svg.CMLHNMRSpect2SVGConverter;
import org.xmlcml.cml.converters.spectrum.svg.CMLSpect2SVGConverter;
import org.xmlcml.cml.converters.spectrum.svg.SVG2CMLSpectConverter;

/**
 * @author Sam Adams
 */
public class SpectrumConverters extends ConverterListImpl {

    public SpectrumConverters() {
        list.add(new ConverterInfo(CMLSpect2JDXConverter.class));
        list.add(new ConverterInfo(JDX2CMLConverter.class));
        list.add(new ConverterInfo(OSCAR2CMLSpectConverter.class));
        list.add(new ConverterInfo(CMLHNMRSpect2SVGConverter.class));
        list.add(new ConverterInfo(CMLSpect2SVGConverter.class));
        list.add(new ConverterInfo(SVG2CMLSpectConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
