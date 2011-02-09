package org.xmlcml.cml.converters.cif;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLModule;

public class OutPutModuleBuilder {
    public static final String CONVENTION_CRYSTALOGRAPHY = "Crystalography Experiment";
    public static final String CONVENTION_CRYSTAL = "CrystalStructure";
    public static final String CONVENTION_MOLECULAR = "Molecular";
    public static final String IUCR_DICT_URI = "http://www.iucr.org/cif";
    public static final String IUCR_DICT_PREFIX = "iucr";
    public static final String XHTML_PREFIX = "xhtml";
    public static final String XHTML_URI = "http://www.w3.org/1999/xhtml";

    CMLModule topModule;
    CMLModule crystalModule;
    CMLModule moleculeModule;
    CMLCml cml;
    boolean isFinalised = false;

    public OutPutModuleBuilder() {
        init();
    }

    private void init() {
        cml = new CMLCml();
        topModule = new CMLModule();
        topModule.addAttribute(new Attribute("convention", CONVENTION_CRYSTALOGRAPHY));
        crystalModule = new CMLModule();
        moleculeModule = new CMLModule();
        cml.addNamespaceDeclaration(IUCR_DICT_PREFIX, IUCR_DICT_URI);
        cml.addNamespaceDeclaration(XHTML_PREFIX, XHTML_URI);
        cml.appendChild(topModule);
    }

    public void finalise() {
        if (!isFinalised) {
            topModule.appendChild(crystalModule);
            crystalModule.appendChild(moleculeModule);
        }
        isFinalised = true;
    }

    public void addToTop(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        this.topModule.appendChild(element);
    }

    public void addToCrystal(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        this.crystalModule.appendChild(element);
    }

    public void addToMolecule(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        this.moleculeModule.appendChild(element);
    }
}
