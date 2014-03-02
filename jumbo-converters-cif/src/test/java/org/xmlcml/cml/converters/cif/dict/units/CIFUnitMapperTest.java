package org.xmlcml.cml.converters.cif.dict.units;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.cif.dict.units.CIFUnitMapper;
import org.xmlcml.cml.converters.cif.dict.units.CifUnit;

public class CIFUnitMapperTest {

    @Test
    public void testCIFUnitMapper() {
        CIFUnitMapper mapper = new CIFUnitMapper();
        Assert.assertNotNull(mapper);
    }

    @Test
    public void testCifUnitMapperMap(){
        CIFUnitMapper mapper = new CIFUnitMapper();
        assertEquals(CifUnit.values().length, mapper.cifUnitMap.size());
    }
    
    @Test
    public void testGetCMLIdFor() {
        CIFUnitMapper mapper = new CIFUnitMapper();
        String cml=mapper.getCMLIdFor("A");
        assertNotNull(cml);
        assertEquals("angstrom", cml);
    }

}
