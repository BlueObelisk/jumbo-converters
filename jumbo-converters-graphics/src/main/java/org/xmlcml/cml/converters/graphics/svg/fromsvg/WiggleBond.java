package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealArray.Filter;
import org.xmlcml.graphics.svg.SVGLine;

/**
 * utilities to process wiggle bond
 * @author pm286
 *
 */
public class WiggleBond {

	private static double averageRunningDotProduct(List<Real2> vList, int off) {
		int nv = vList.size();
		double sum = 0.;
		for (int i = 0; i < nv-off; i++) {
			Real2 vi = vList.get(i);
			Real2 vj = vList.get(i+off);
			double d = vi.dotProduct(vj);
			sum += d;
		}
		sum /= (double)(nv - off);
		return sum;
	}

	private static RealArray maximumAverageRunningDotProduct(List<Real2> vList, int start, int end) {
//		double maxd = Double.NEGATIVE_INFINITY;
		RealArray ra = new RealArray();
		for (int off = start; off < end; off++) {
			double sum = averageRunningDotProduct(vList, off);
			ra.addElement(sum);
		}
		return ra;
	}

	private static Real2 getPointByIndex(Real2Array ra2, double index) {
		int iindex = (int) index;
		double delta = index - iindex;
		// correct for fractional index
		Real2 target = ra2.get(iindex+1);
		Real2 target1 = ra2.get(iindex+2);
		Real2 deltaV = target1.subtract(target);
		deltaV = deltaV.multiplyBy(delta);
		Real2 targetx = target.plus(deltaV);
		return targetx;
	}

	public static SVGChemLine findCrudeWiggleAxis(Real2Array ra2, GraphicsConverterTool gc) {
		int na = ra2.size();
		List<Real2> vList = new ArrayList<Real2>();
		// incremental vectors along wiggle
		for (int i = 1; i < na; i++) {
			Real2 vector = ra2.elementAt(i).subtract(ra2.elementAt(i-1));
			vList.add(vector);
		}
		// simple autocorrelation
		int nv = vList.size();
		int start = 0;
		int end = nv-3;
		// array of dot product of first vector with subsequent vectors
		RealArray ra = maximumAverageRunningDotProduct(vList, start, end);
		// smooth it
		RealArray filter = RealArray.getFilter(2, Filter.GAUSSIAN);
		RealArray raa = ra.applyFilter(filter);
		double d = raa.largestElement();
		// sweep the array looking for the first peak after the origin
		double index = raa.findFirstLocalMaximumafter(0, d*0.5);
		// can we get a later maximum for greater accuracy?
		int nrepeat = (int) ((double)nv/index);
		if (nrepeat > 1) {
			int newstart = (int) ((nrepeat - 1) * index);
			double newIndex = raa.findFirstLocalMaximumafter(newstart, d*0.5);
			// no, reset
			if (!Double.isNaN(newIndex)) {
				index = newIndex;
			}
		}
		// get best estimate of point along axis
		Real2 repeatPoint = getPointByIndex(ra2, index);
		// and axis
		Real2 axis = repeatPoint.subtract(ra2.get(0));
		// scale to length of wiggle
		axis = axis.multiplyBy((double)(nv-1)/(index));
		
		// this is not yet used
//		Real2 midRepeatPoint = ra2.get(0).getMidPoint(repeatPoint);
		// other side of wiggle, not yet used
//		Real2 halfRepeatIndexPoint = getPointByIndex(ra2, index*0.5);
//		Real2 wiggleWidthVector = halfRepeatIndexPoint.subtract(midRepeatPoint);
//		double width = wiggleWidthVector.getLength();
		Real2 lineStart = ra2.elementAt(0);
		Real2 lineEnd = lineStart.plus(axis);
		Line2 line2 = new Line2(lineStart, lineEnd);
		SVGLine line = new SVGLine(line2);
		SVGChemLine chemLine = new SVGChemLine(line);
		chemLine.setOpacity(0.2);
		chemLine.setSVGClassName("wiggleBond");
		chemLine.setStrokeWidth(5.);
		return chemLine;
	}

}
