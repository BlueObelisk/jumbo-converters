package org.xmlcml.cml.converters.cif;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;

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
        Assert.assertTrue(builder.moleculeModule.getParent()==builder.crystalModule);
        Assert.assertTrue(builder.crystalModule.getParent()==builder.topModule);
    }

    @Test
    public void testAddToTop() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = new CMLMolecule();
        builder.addToTop(mol);
        Assert.assertTrue(mol.getParent()==builder.topModule);
    }

    @Test
    public void testAddToCrystal() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = new CMLMolecule();
        builder.addToCrystal(mol);
        Assert.assertTrue(mol.getParent()==builder.crystalModule);
    }

    @Test
    public void testAddToMolecule() {
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = new CMLMolecule();
        builder.addToMolecule(mol);
        Assert.assertTrue(mol.getParent()==builder.moleculeModule);
    }
    @Test(expected=IllegalArgumentException.class)
    public void nullElement(){
        OutPutModuleBuilder builder = new OutPutModuleBuilder();
        CMLMolecule mol = null;
        builder.addToMolecule(mol);
    }

}
