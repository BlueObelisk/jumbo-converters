package org.xmlcml.cml.semcompchem.tools;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.euclid.RealArray;

/**
 *
 * @author Weerapong Phadungsukanan
 */
public class Vector3TemplateTool extends AbstractTemplateTool {

    public CMLVector3 getStorageElement() {
        CMLVector3 vector3 = null;
        if (checkData()) {
            String dataType = getDataType();
            if (dataType.equals(XSD_FLOAT) || dataType.equals(XSD_DOUBLE)) {
                RealArray array = new RealArray(rawData.split(getDelimiter()));
                vector3 = new CMLVector3(array.getArray());
            } else {
                throw new UnsupportedOperationException("CMLVector3 only supports xsd:float or xsd:double. Check dictionary.");
            }
        }
        return vector3;
    }

    private String getDelimiter() {
        String delimiter = template.getAttributeValue("delimiter");
        return (delimiter == null || delimiter.length() == 0) ? delimiter = S_SPACE : delimiter;
    }
}
