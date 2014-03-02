package org.xmlcml.cml.semcompchem.tools;

import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Real;

/**
 *
 * @author Weerapong Phadungsukanan
 */
public class ScalarTemplateTool extends AbstractTemplateTool {

    public CMLScalar getStorageElement() {
        CMLScalar scalar = new CMLScalar();
        if (checkData()) {
            String dataType = getDataType();
            try {
                if (dataType.equals(XSD_FLOAT) || dataType.equals(XSD_DOUBLE)) {
                    scalar.setValue(Real.parseDouble(rawData.trim()));
                } else if (dataType.equals(XSD_INTEGER)) {
                    scalar.setValue(Integer.parseInt(rawData.trim()));
                } else if (dataType.equals(XSD_STRING)) {
                    scalar.setValue(rawData);
                } else {
                    throw new RuntimeException("Unsupported dataType [" + dataType + "]");
                }
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Unsupported dataType [" + dataType + "]", nfe);
            }
        }
        return scalar;
    }
}
