package org.xmlcml.cml.semcompchem.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.interfacex.HasUnits;
import org.xmlcml.cml.tools.EntryTool;

/**
 * Entry tool for creating the data storage from entry.
 * 
 * @author Weerapong Phadungsukanan
 * 
 */
public class EntryTool2 extends EntryTool {

    private Logger LOG = Logger.getLogger(getClass());
    protected static Map<String, Class> registeredDataConverters = null;

    private static void registerDataConverter() {
        if (registeredDataConverters == null) {
            registeredDataConverters = new HashMap(4);
            registeredDataConverters.put("array", ArrayTemplateTool.class);
            registeredDataConverters.put("vector3", Vector3TemplateTool.class);
            registeredDataConverters.put("scalar", ScalarTemplateTool.class);
            registeredDataConverters.put("matrix", MatrixTemplateTool.class);
        }
    }

    /**
     * Private constructor only used in package (Singleton Pattern).
     * use dictionaryTool.createEntryTool(entry) for normal use
     * @param entry
     */
    protected EntryTool2(CMLEntry entry) {
        super(entry);
        registerDataConverter();
    }

    /**
     * Gets EntryTool associated with entry.
     * if null creates one and sets it in entry
     * @param entry
     * @return tool
     */
    public static EntryTool2 getOrCreateTool(CMLEntry entry) {
        EntryTool2 entryTool = null;
        if (entry != null) {
            entryTool = (EntryTool2) entry.getTool();
            if (entryTool == null) {
                entryTool = new EntryTool2(entry);
                entry.setTool(entryTool);
            }
        }
        return entryTool;
    }

    /**
     * Create a CMLProperty or CMLParameter and store value(s) in the storage
     * defined in the template of the entry.
     * 
     * @param value
     * @return
     */
    public CMLElement createStorageEntry(String value) {
        CMLElement element = null;
        CMLElement storage = createStorageElement(value);
        if (storage != null) {
            HasDictRef pelem = null;
            String ns = entry.getId();
            if (ns.startsWith(CMLProperty.TAG)) {
                pelem = new CMLProperty();
            } else {
                pelem = new CMLParameter();
            }

            pelem.setDictRef("cmlqm:" + entry.getId());
            element = (CMLElement) pelem;
            element.appendChild(storage);
        }

        return element;
    }

    /**
     * Create data storage element according to the storage type of the entry.
     * @param value
     * @return
     */
    public CMLElement createStorageElement(String value) {
        CMLElement element = null;
        try {
            Class dataToolClass = registeredDataConverters.get(AbstractTemplateTool.extractTemplate(entry).getLocalName());
            HasDataStorageTool dataTool = (HasDataStorageTool) dataToolClass.newInstance();
            dataTool.setEntry(entry);
            dataTool.setStringData(value);
            element = dataTool.getStorageElement();
            String units = ((HasUnitsInEntryTemplate) dataTool).getUnitsID();
            if (units != null) {
                if (element instanceof HasUnits) {
                    AbstractTemplateTool.addUnitsToDataStorage((HasUnits) element, units);
                } else {
                    LOG.warn("Units not added for " + entry.getId()
                            + ". CML element of class " + element.getClass().getName()
                            + " cannot have units. Incorrect data storage for dictionary entry.");
                }
            }
        } catch (InstantiationException ex) {
            LOG.error("Cannot create an instance of HasDataStorageTool");
        } catch (IllegalAccessException ex) {
            LOG.error("IllegalAccessException for HasDataStorageTool");
        } catch (NullPointerException ex) {
            LOG.error("Null class or entry in registeredDataConverters.");
        }

        return element;

    }
}
