package org.xmlcml.cml.semcompchem.tools;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.interfacex.HasUnits;

/**
 *
 * @author Weerapong Phadungsukanan
 */
public abstract class AbstractTemplateTool extends AbstractTool implements HasDataStorageTool, HasUnitsInEntryTemplate {

    private static Logger LOG = Logger.getLogger(AbstractTemplateTool.class);
    protected CMLEntry entry = null;
    protected Element template = null;
    protected String rawData = null;
    public static final String TEMPLATE_TAG = "template";

    public void setEntry(CMLEntry entry) {
        this.entry = entry;
        if (entry == null) {
            throw new NullPointerException("Try to set null entry.");
        }
        template = extractTemplate(entry);
    }

    public static Element extractTemplate(CMLEntry entry) {
        Element temp = null;
        try {
            Element storageTemplate = entry.getFirstChildElement(TEMPLATE_TAG, CMLX_NS);
            Elements elems = storageTemplate.getChildElements();
            temp = elems.get(0);
            if (elems.size() > 1) {
                LOG.warn("More than 1 storage is found in template for entry : " + entry.getId());
            }
        } catch (NullPointerException npe) {
            LOG.error("Storage template not found for entry : " + entry.getId(), npe);
        }
        return temp;
    }

    public void setStringData(String data) {
        this.rawData = data;
    }

    /**
     * Check whether dataType and rawData are set.
     * @return
     */
    public boolean checkData() {
        String dataType = getDataType();
        if (dataType == null || rawData == null || template == null || entry == null) {
            return false;
        }
        return true;
    }

    public String getDataType() {
        if (template != null) {
            return template.getAttributeValue("dataType");
        }
        return null;
    }

    /**
     * Expecting units namespace to be CMLConstants.UNIT_NS
     * @return
     */
    public String getUnitsID() {
        if (template != null) {
            Attribute att = template.getAttribute("units");
            if (att != null) {
                String unitsWithPrefix = att.getValue();

                return unitsWithPrefix.replaceFirst("^units:", "");
            } else {
                LOG.debug("Units not found for " + entry.getId());
            }
        }
        return null;
    }

    public static void addUnitsToDataStorage(HasUnits element, String unitsID) {
        if (element != null) {
            element.setUnits(CMLConstants.CML_UNITS, unitsID, CMLConstants.UNIT_NS);
        }
    }
}
