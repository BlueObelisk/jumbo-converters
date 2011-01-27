package org.xmlcml.cml.converters.format;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.util.JumboReader;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLMatrix;

public class MatrixLineReader extends LineReader {
	private static final Logger LOG = Logger.getLogger(MatrixLineReader.class);

	public static final String MATRIX_LINE_READER = "bracketedLineReader";

	public MatrixLineReader(Element childElement) {
		super(MATRIX_LINE_READER, childElement);
	}

	@Override
	public CMLElement readLinesAndParse(JumboReader jumboReader) {
		if (rows == null || columns == null) {
			throw new RuntimeException("Must give rows and columns");
		}
		// this is not the most efficient way to read the matrix
		CMLArrayList arrayList = jumboReader.readTableColumnsAsArrayList(this);
		int columnsFound = arrayList.getArrays().size();
		if (columnsFound != columns) {
			throw new RuntimeException("columns found ("+columnsFound+") != expected ("+columns+")");
		}
		int rowsFound = arrayList.getArrays().get(0).getArraySize();
		if (rowsFound != rows) {
			throw new RuntimeException("rows found ("+rowsFound+") != expected ("+rows+")");
		}
		CMLMatrix cmlMatrix = null;
		if (CMLConstants.XSD_DOUBLE.equals(arrayList.getArrays().get(0).getDataType())) {
			cmlMatrix = createDoubleMatrix(arrayList);
		} else if (CMLConstants.XSD_INTEGER.equals(arrayList.getArrays().get(0).getDataType())) {
			cmlMatrix = createIntegerMatrix(arrayList);
		}
		jumboReader.addElementWithDictRef(cmlMatrix, localDictRef);
		return cmlMatrix;
	}
	private CMLMatrix createDoubleMatrix(CMLArrayList arrayList) {
		CMLMatrix cmlMatrix;
		double[][] matrixElements = new double[rows][columns];
		for (int jcol = 0; jcol < arrayList.getArraysCount(); jcol++) {
			double[] column = arrayList.getArrays().get(jcol).getDoubles();
			for (int irow = 0; irow < column.length; irow++) {
				matrixElements[irow][jcol] = column[irow];
			}
		}
		cmlMatrix = new CMLMatrix(matrixElements);
		return cmlMatrix;
	}

	private CMLMatrix createIntegerMatrix(CMLArrayList arrayList) {
		CMLMatrix cmlMatrix;
		int[][] matrixElements = new int[rows][columns];
		for (int jcol = 0; jcol < arrayList.getArraysCount(); jcol++) {
			int[] column = arrayList.getArrays().get(jcol).getInts();
			for (int irow = 0; irow < column.length; irow++) {
				matrixElements[irow][jcol] = column[irow];
			}
		}
		cmlMatrix = new CMLMatrix(matrixElements);
		return cmlMatrix;
	}



}
