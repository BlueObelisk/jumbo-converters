package org.xmlcml.cml.converters.compchem;

import java.util.List;

import org.xmlcml.cml.base.CMLConstants;

public class CompchemUtils {
	private int linesToRead;
//	private NumericFormat numericFormat;
	public CompchemUtils() {
		
	}
	public double[] readFormattedNumericArray(
			List<String> lines, 
			int lineCount,
			int leftMargin, 
			NumericFormat numericFormat,
			int fieldsPerLine, 
			int fieldsToRead) {
		linesToRead = ((fieldsToRead -1)/ fieldsPerLine) + 1;
		int istart = lineCount;
		int iend = istart + linesToRead;
		int fieldsPerLastLine = fieldsToRead % fieldsPerLine;
		int fieldCounter = 0;
//		this.numericFormat = numericFormat;
		double[] doubles = new double[fieldsToRead];
		for (int i = istart; i < iend; i++) {
			int fieldsPerThisLine = (i < iend-1) ? fieldsPerLine : fieldsPerLastLine;
			double[] dd = CompchemUtils.readLine(lines.get(i), leftMargin, fieldsPerThisLine, numericFormat);
			System.arraycopy(dd, 0, doubles, fieldCounter, fieldsPerThisLine);
			fieldCounter += fieldsPerThisLine;
		}
		return doubles;
	}

	public int getLinesRead() {
		return linesToRead;
	}

	private static double[] readLine(String field, int leftMargin, int fieldsPerThisLine,
			NumericFormat numericFormat) {
		double[] doubles = new double[fieldsPerThisLine];
		int ichar = leftMargin;
		int width = numericFormat.getWidth();
		for (int i = 0; i < fieldsPerThisLine; i++) {
			doubles[i] = numericFormat.readFormattedNumber(field.substring(ichar, ichar+width));
			ichar += width;
		}
		return doubles;
	}

	@SuppressWarnings("unused")
	private static double readFormattedNumber(
			String field, int width, int decimal) {
		double d = Double.NaN;
		if (field == null) {
			
		} else if (field.trim().equals("")) {
			/** fortran can regard blank as zero */
			d = 0.0;
		} else if (field.indexOf(CMLConstants.S_STAR) != -1) {
			// asterisks are over/underflow in FORTRAN
		} else if (field.indexOf(CMLConstants.S_PERIOD) == -1) {
			// fortran can make decimal implicit ARGGGGH
			throw new RuntimeException("No decimal point given");
		} else {
			try {
				d = new Double(field.trim());
			} catch (Exception e) {
				throw new RuntimeException("cannot parse double: "+field, e);
			}
		}
		return d;
	}
}
