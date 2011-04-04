package org.xmlcml.cml.converters.cif.dict;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.converters.cif.dict.units.UnitType;


public class UnitTypeTest {
    @Test public void testUnitType(){
        CMLEntry entry = new CMLEntry();
        UnitType.mass.setUnitType(entry);
        Assert.assertEquals("unitType:mass", entry.getAttribute("unitType").getValue());
    }
}