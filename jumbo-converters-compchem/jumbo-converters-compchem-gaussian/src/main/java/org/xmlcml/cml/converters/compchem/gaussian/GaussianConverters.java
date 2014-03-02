package org.xmlcml.cml.converters.compchem.gaussian;

import java.util.Collections;

import org.xmlcml.cml.converters.compchem.gaussian.log.GaussianLog2XMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class GaussianConverters extends ConverterListImpl {

    public GaussianConverters() {
        list.add(new ConverterInfo(GaussianLog2XMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }


}
