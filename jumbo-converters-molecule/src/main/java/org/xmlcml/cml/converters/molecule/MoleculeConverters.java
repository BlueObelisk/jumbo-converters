package org.xmlcml.cml.converters.molecule;

import java.util.Collections;



import org.xmlcml.cml.converters.molecule.cml.CML2CMLConverter;

import org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter;
import org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter;
import org.xmlcml.cml.converters.molecule.mdl.CML2SDFConverter;
import org.xmlcml.cml.converters.molecule.mdl.SDF2CMLConverter;

import org.xmlcml.cml.converters.molecule.pubchem.PubchemXML2CMLConverter;

import org.xmlcml.cml.converters.molecule.xyz.CML2XYZConverter;
import org.xmlcml.cml.converters.molecule.xyz.XYZ2CMLConverter;

import org.xmlcml.cml.converters.registry.ConverterInfo;
import org.xmlcml.cml.converters.registry.ConverterListImpl;

/**
 * @author Sam Adams
 */
public class MoleculeConverters extends ConverterListImpl {

    public MoleculeConverters() {
        list.add(new ConverterInfo(CML2CMLConverter.class));
        list.add(new ConverterInfo(CML2MDLConverter.class));
        list.add(new ConverterInfo(MDL2CMLConverter.class));
        list.add(new ConverterInfo(CML2SDFConverter.class));
        list.add(new ConverterInfo(SDF2CMLConverter.class));
        list.add(new ConverterInfo(PubchemXML2CMLConverter.class));
        list.add(new ConverterInfo(CML2XYZConverter.class));
        list.add(new ConverterInfo(XYZ2CMLConverter.class));
        this.list = Collections.unmodifiableList(list);
    }

}
