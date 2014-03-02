package org.xmlcml.cml.semcompchem.tools;

import org.xmlcml.cml.converters.antlr.Gaussian03AST2CML;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.euclid.Real;

/**
 *
 * @author Weerapong Phadungsukanan
 */
public class MatrixTemplateTool extends AbstractTemplateTool {

    public CMLMatrix getStorageElement() {
        CMLMatrix matrix = null;
        if (checkData()) {
            String dataType = getDataType();
            if (dataType.equals(XSD_FLOAT) || dataType.equals(XSD_DOUBLE)) {
                matrix = new CMLMatrix(splitDoubleData(rawData));
            } else if (dataType.equals(XSD_INTEGER)) {
                matrix = new CMLMatrix(splitIntData(rawData));
            } else {
                throw new UnsupportedOperationException("Unknown dataType");
            }
        }
        return matrix;
    }

    private int[][] splitIntData(String data) {
        String[] rows = data.split(Gaussian03AST2CML.MATRIX_ROW_DELIMITER);
        int[][] matrix = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split(Gaussian03AST2CML.ARRAY_DELIMITER);
            matrix[i] = new int[cols.length];
            for (int j = 0; j < cols.length; j++) {
                matrix[i][j] = Integer.parseInt(cols[j]);
            }
        }
        return matrix;
    }

    private double[][] splitDoubleData(String data) {
        String[] rows = data.split(Gaussian03AST2CML.MATRIX_ROW_DELIMITER);
        double[][] matrix = new double[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split(Gaussian03AST2CML.ARRAY_DELIMITER);
            matrix[i] = new double[cols.length];
            for (int j = 0; j < cols.length; j++) {
                matrix[i][j] = Real.parseDouble(cols[j]);
            }
        }
        return matrix;
    }

    private String getDelimiter() {
        String delimiter = template.getAttributeValue("delimiter");
        return (delimiter == null || delimiter.length() == 0) ? delimiter = S_SPACE : delimiter;
    }
}
