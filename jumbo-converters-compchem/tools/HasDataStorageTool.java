package org.xmlcml.cml.semcompchem.tools;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLEntry;

/**
 *
 * @author Weerapong Phadungsukanan
 */
public interface HasDataStorageTool {

    void setEntry(CMLEntry entry);

    void setStringData(String value);

    String getDataType();

    boolean checkData();

    CMLElement getStorageElement();
}
