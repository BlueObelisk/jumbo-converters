package org.xmlcml.cml.semcompchem.tools;

import nu.xom.Attribute;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.RealArray;

/**
 *
 * @author Weerapong Phadungsukanan
 */
public class ArrayTemplateTool extends AbstractTemplateTool {

    private Logger LOG = Logger.getLogger(getClass());

    public CMLArray getStorageElement() {
        CMLArray array = null;
        if (checkData()) {
            String dataType = getDataType();
            String[] ss = splitAndCheckArrayString(rawData);
            if (dataType.equals(XSD_FLOAT) || dataType.equals(XSD_DOUBLE)) {
                array = createDoubleArrayByValue(ss);
            } else if (dataType.equals(XSD_INTEGER)) {
                array = createIntegerArrayByValue(ss);
            } else if (dataType.equals(XSD_STRING)) {
                array = new CMLArray(ss);
            } else {
                throw new UnsupportedOperationException("Unknown dataType");
            }
            try {
                RealArray ra = new RealArray(ss);
                checkValueRange(ra.getMin(), ra.getMax());
                array = new CMLArray(ra.getArray());
            } catch (EuclidRuntimeException e) {
                throw new RuntimeException("Failed to create RealArray in createDoubleArrayByValue for " + entry.getId());
            }
        }
        return array;
    }

    /**
     * Check array length against the condition of array template.
     *
     * @param ss array of Strings of raw values.
     */
    public boolean checkArrayLength(String[] ss) {
        if (getLengthAttribute() != null) {
            int l = getLength();
            if (l != ss.length) {
                LOG.info("Expected array of size " + l + "; found: " + ss.length + " for " + entry.getId());
                return false;
            }
        }
        if (getMinLengthAttribute() != null) {
            int l = getMinLength();
            if (l > ss.length) {
                LOG.info("Expected array of size >= " + l + "); found: " + ss.length + " for " + entry.getId());
                return false;
            }
        }
        if (getMaxLengthAttribute() != null) {
            int l = getMaxLength();
            if (l < ss.length) {
                LOG.info("Expected array of size =< " + l + "); found: " + ss.length + " for " + entry.getId());
                return false;
            }
        }
        return true;
    }

    /**
     * Check double range.
     * 
     * @param minInclusive
     * @param maxInclusive
     * @return true if given range is valid.
     */
    private boolean checkValueRange(double minInclusive, double maxInclusive) {
        Attribute minInclusiveAttribute = getMinInclusiveAttribute();
        Attribute maxInclusiveAttribute = getMaxInclusiveAttribute();
        if (minInclusiveAttribute != null && !Double.isNaN(minInclusive)) {
            if (minInclusive < getMinInclusive()) {
                LOG.info("value (" + minInclusive + ") outside minimum: " + getMinInclusive() + " for " + entry.getId());
                return false;
            }
        }
        if (maxInclusiveAttribute != null && !Double.isNaN(maxInclusive)) {
            if (maxInclusive > getMaxInclusive()) {
                LOG.info("value (" + maxInclusive + ") outside maximum: " + entry.getMaxInclusive() + " for " + entry.getId());
                return false;
            }
        }
        return true;
    }

    /**
     * Check integer range.
     *
     * @param minInclusive
     * @param maxInclusive
     * @return true if given range is valid.
     */
    private boolean checkValueRange(int minInclusive, int maxInclusive) {
        return checkValueRange((double) minInclusive, (double) maxInclusive);
    }

    /**
     * Split a raw data into array of string check its length against the condition
     * of array template.
     *
     * @param value String to be splited and checked.
     * @return primitive array of Strings.
     */
    private String[] splitAndCheckArrayString(String value) {
        String[] ss = value.split(getDelimiter());
        if (checkArrayLength(ss)) {
            return ss;
        }
        throw new RuntimeException("Unexpected array size for " + entry.getId());
    }

    private CMLArray createDoubleArrayByValue(String[] ss) {
        CMLArray array = null;
        try {
            RealArray ra = new RealArray(ss);
            if (checkValueRange(ra.getMin(), ra.getMax())) {
                array = new CMLArray(ra.getArray());
            } else {
                throw new RuntimeException("Invalid data range for entry : " + entry.getId());
            }
        } catch (EuclidRuntimeException e) {
            throw new RuntimeException("Failed to create RealArray in createDoubleArrayByValue");
        }
        return array;
    }

    private CMLArray createIntegerArrayByValue(String[] ss) {
        CMLArray array = null;
        try {
            IntArray ia = new IntArray(ss);
            if (checkValueRange(ia.getMin(), ia.getMax())) {
                array = new CMLArray(ia.getArray());
            } else {
                throw new RuntimeException("Invalid data range for entry : " + entry.getId());
            }
        } catch (EuclidRuntimeException e) {
            throw new RuntimeException("Failed to create IntArray in createIntegerArrayByValue");
        }
        return array;
    }

    private Attribute getLengthAttribute() {
        return template.getAttribute("length");
    }

    private int convertIntegerAttribute(Attribute att) {
        if (att != null) {
            return Integer.parseInt(att.getValue());
        }
        return 0;
    }

    private int getLength() {
        return convertIntegerAttribute(getLengthAttribute());
    }

    private Attribute getMinLengthAttribute() {
        return template.getAttribute("minLength");
    }

    private int getMinLength() {
        return convertIntegerAttribute(getMinLengthAttribute());
    }

    private Attribute getMaxLengthAttribute() {
        return template.getAttribute("maxLength");
    }

    private int getMaxLength() {
        return convertIntegerAttribute(getMaxLengthAttribute());
    }

    private Attribute getMinInclusiveAttribute() {
        return template.getAttribute("minInclusive");
    }

    private double getMinInclusive() {
        Attribute att = getMinInclusiveAttribute();
        if (att == null) {
            return Double.NaN;
        }
        return Real.parseDouble(att.getValue());
    }

    private Attribute getMaxInclusiveAttribute() {
        return template.getAttribute("maxInclusive");
    }

    private double getMaxInclusive() {
        Attribute att = getMaxInclusiveAttribute();
        if (att == null) {
            return Double.NaN;
        }
        return Real.parseDouble(att.getValue());
    }

    private String getDelimiter() {
        String delimiter = template.getAttributeValue("delimiter");
        return (delimiter == null || delimiter.length() == 0) ? delimiter = S_SPACE : delimiter;
    }
}
