package org.xmlcml.cml.converters.cif;

import javax.print.attribute.standard.OutputDeviceAssigned;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.converters.cif.dict.CifDictionaryBuilder;
import org.xmlcml.cml.converters.cif.dict.UnitsDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;

public class OutPutModuleBuilderTest {

    @Test
    public void testOutPutModuleBuilder() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        Assert.assertNotNull(builder);
    }

    @Test
    public void testFinalise() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        Assert.assertEquals(false, builder.isFinalised);
        builder.finalise();
        Assert.assertEquals(true, builder.isFinalised);
        Assert.assertTrue(builder.moleculeModule.getParent() == builder.crystalModule);
        Assert.assertTrue(builder.crystalModule.getParent() == builder.topModule);
    }

    @Test
    public void testAddToTop() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = new CMLMolecule();
        builder.addToTop(mol);
        Assert.assertTrue(mol.getParent() == builder.topModule);
    }

    @Test
    public void testAddToCrystal() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = new CMLMolecule();
        builder.addToCrystal(mol);
        Assert.assertTrue(mol.getParent() == builder.crystalModule);
    }

    @Test
    public void testAddToMolecule() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = new CMLMolecule();
        builder.addToMolecule(mol);
        Assert.assertTrue(mol.getParent() == builder.moleculeModule);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullElement() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = null;
        builder.addToMolecule(mol);
    }

    @Test
    public void testAddAllChildren() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        Element root = new Element("root");
        root.appendChild(new Element("child1"));
        root.appendChild(new Element("child2"));
        root.appendChild("text"); // Text nodes not copied
        Assert.assertEquals(3, root.getChildCount());
        builder.addAllChildrenToTop(root);
        Assert.assertEquals(2, builder.topModule.getChildCount());
    }

    @Test
    public void testIndexDictionary() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        int sizeOfMap = builder.idMap.keySet().size();
        System.out.println(sizeOfMap);
        // CMLEntry entry=builder.idMap.get("cell_measurement_theta_max");
        CMLEntry entry = builder.idMap.get("cell_measurement_wavelength");
        for (CMLEntry test : builder.idMap.values()) {
            System.out.println(test.getId());
        }
        Assert.assertNotNull(entry);
    }

    @Test
    public void testFetchDataType() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLProperty prop = new CMLProperty();
        prop.addNamespaceDeclaration(CifDictionaryBuilder.PREFIX, CifDictionaryBuilder.URI);
        prop.setDictRef("iucr:cell_measurement_wavelength");
        prop.addScalar(new CMLScalar("0.0622"));
        String type = prop.getScalarElements().get(0).getDataType();
        Assert.assertEquals("xsd:string", type);
        builder.fetchDataType(prop);
        type = prop.getScalarElements().get(0).getDataType();
        Assert.assertEquals("xsd:double", type);
    }
}
