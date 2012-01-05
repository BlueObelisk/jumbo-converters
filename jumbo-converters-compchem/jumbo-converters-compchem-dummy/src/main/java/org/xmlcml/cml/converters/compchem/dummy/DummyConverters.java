package org.xmlcml.cml.converters.compchem.dummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlcml.cml.converters.compchem.dummy.log.DummyLog2XMLConverter;
import org.xmlcml.cml.converters.compchem.dummy.mol.DummyMol2CMLConverter;
import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author pm286
 */
public class DummyConverters extends ConverterListImpl {

    public DummyConverters() {
    	super();
        list.add(new ConverterInfo(DummyLog2XMLConverter.class));
        list.add(new ConverterInfo(DummyMol2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
