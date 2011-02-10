package org.xmlcml.cml.converters.cif;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

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
    public CMLModule getTopModule() {
        return topModule;
    }

    public CMLModule getCrystalModule() {
        return crystalModule;
    }

    public CMLModule getMoleculeModule() {
        return moleculeModule;
    }

    public CMLCml getCml() {
        return cml;
    }

    boolean isFinalised = false;

    public OutPutModuleBuilder() {
        init();
    }

    private void init() {
        cml = new CMLCml();
        topModule = new CMLModule();
        topModule.addAttribute(new Attribute("convention", CONVENTION_CRYSTALOGRAPHY));
        crystalModule = new CMLModule();
        crystalModule.addAttribute(new Attribute("convention", CONVENTION_CRYSTAL));
        moleculeModule = new CMLModule();
        moleculeModule.addAttribute(new Attribute("convention", CONVENTION_MOLECULAR));

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
        element.detach();
        this.topModule.appendChild(element);
    }

    public void addToCrystal(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        this.crystalModule.appendChild(element);
    }

    public void addToMolecule(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        element.detach();
        this.moleculeModule.appendChild(element);
    }

    /**
     * 
     * @param parent
     * 
     */
    public void addAllChildrenToTop(Element parent) {
        Elements children = parent.getChildElements();
        for (int x = 0; x < children.size(); x++) {
            Element element = children.get(x);
            element.detach();
            this.addToTop(element);
        }
    }

    public void cloneIdsFromElement(CMLCml parent) {
        String id = parent.getId();
        if (id != null) {
            this.topModule.setId(id);
        }
        String title= parent.getTitle();
        if(title!=null){
            this.topModule.setTitle(title);
        }
    }

}
