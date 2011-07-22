package org.xmlcml.cml.converters.graphics.svg.elements;

import static org.junit.Assert.fail;


import static org.xmlcml.cml.base.CMLConstants.SVG_NS;

import java.util.List;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;

/**
 * @author pm286
 *
 */
public class SVGChemPathTest {

	@Test
	public void testSVGChemPathSVGPathGraphicsConverter() {
//		Util.println("=============SVGChemPathTest==============");
		String s = "" +
		"<svg:svg xmlns:svg='"+SVG_NS+"'>"+
		"<svg:path d='M 1 2 L 3 4 5 6'/>"+
		"<svg:path d='M 11 12 L 13 14 15 16'/>"+
		"</svg:svg>"+
		"";
		Element element = org.xmlcml.cml.testutil.JumboTestUtils.parseValidString(s);
		SVGSVG svg = (SVGSVG) SVGElement.createSVG(element);
		SVGChemSVG svgChem = new SVGChemSVG(svg);
		Assert.assertNotNull("svgChem not null", svgChem);
	}

	@Test
	public void testGetPathList() {
		String s = "" +
		"<svg:svg xmlns:svg='"+SVG_NS+"'>"+
		"<svg:path d='M 1 2 L 3 4 5 6'/>"+
		"<svg:path d='M 11 12 L 13 14 15 16'/>"+
		"</svg:svg>"+
		"";
		Element element = org.xmlcml.cml.testutil.JumboTestUtils.parseValidString(s);
		SVGSVG svg = (SVGSVG) SVGElement.createSVG(element);
		SVGChemSVG svgChem = new SVGChemSVG(svg);
		Assert.assertNotNull("svgChem not null", svgChem);
		List<SVGChemPath> pathList = SVGChemPath.getPathList(svgChem);
		Assert.assertEquals("path count", 2, pathList.size());
		org.xmlcml.cml.testutil.JumboTestUtils.assertEqualsIncludingFloat("path1", 		
				"<svg:path style=' fill : none; stroke : black; stroke-width : 0.5;' d='M 1 2 L 3 4 5 6' xmlns:svg='"+SVG_NS+"'/>", 
				pathList.get(0), true, 0.00001);
	}

	@Test
	public void testIsPolygon() {
		String s = 
		"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 3'/>";
		SVGChemPath svgPath = makePath(s);
		Assert.assertTrue("polygon", svgPath.isPolygon(0.001));
	}

	@Test
	public void testCountDistinctPoints() {
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		Assert.assertEquals("polygon", 1, svgPath.countDistinctPoints(0.001));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.001));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1 2'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.001));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 3'/>");
		Assert.assertEquals("polygon", 3, svgPath.countDistinctPoints(0.001));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.01 2.01'/>");
		Assert.assertEquals("polygon", 1, svgPath.countDistinctPoints(0.1));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.01 2.01 3 4'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.1));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.01 2.01 3 4 1 2'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.1));
	}
	
	@Test
	public void testCountDistinctPointsAngle() {
		Angle delta = new Angle(0.001, Angle.Units.RADIANS);
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		Assert.assertEquals("polygon", 1, svgPath.countDistinctPoints(0.001, delta));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.001, delta));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1 2'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.001, delta));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 5'/>");
		Assert.assertEquals("polygon", 2, svgPath.countDistinctPoints(0.001, delta));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 5.5'/>");
		Assert.assertEquals("polygon", 3, svgPath.countDistinctPoints(0.001, delta));
	}
	
	@Test
	public void testGetDistinctPointsAngle() {
		Angle delta = new Angle(0.001, Angle.Units.RADIANS);
// single point		
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		List<Real2> pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 1, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
// return to origin		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1 2'/>");
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
// straight line with/out angle constraint		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 5'/>");
		pointList = svgPath.extractDistinctPoints(0.001, null);
		Assert.assertEquals("distinct", 3, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
		test("distinct", new Real2(4, 5), pointList.get(2), 0.00001);
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(4, 5), pointList.get(1), 0.00001);
// bent line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 5.5'/>");
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 3, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
		test("distinct", new Real2(4, 5.5), pointList.get(2), 0.00001);
		pointList = svgPath.extractDistinctPoints(0.001, new Angle(0.4, Angle.Units.RADIANS));
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(4, 5.5), pointList.get(1), 0.00001);
// overlapping points
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001'/>");
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 1, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
// line with overlap		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001 3 4'/>");
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
// return to origin with overlap		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1.0001 2.0001'/>");
		pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
// mitered polygon		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.01 2.01 3 4 3.01 3.99 3.02 4 1.03 2.02 1 2'/>");
		pointList = svgPath.extractDistinctPoints(0.1, delta);
		Assert.assertEquals("distinct", 2, pointList.size());
		test("distinct", new Real2(1, 2), pointList.get(0), 0.00001);
		test("distinct", new Real2(3, 4), pointList.get(1), 0.00001);
// and some real examples
		
		
	}
	
	private void test(String msg, Real2 expected, Real2 test, double eps) {
		String m = org.xmlcml.cml.testutil.JumboTestUtils.testEquals(expected, test, eps);
		if (m != null) {
			Assert.fail(msg+": "+m);
		}
	}
	/**
	 * @param s
	 * @return
	 */
	private SVGChemPath makePath(String s) {
		Element element = org.xmlcml.cml.testutil.JumboTestUtils.parseValidString(s);
		SVGPath path = (SVGPath) SVGElement.createSVG(element);
		SVGChemPath svgPath = new SVGChemPath(path);
		Assert.assertNotNull("svgChem not null", svgPath);
		return svgPath;
	}


	@Test
	public void testIsZeroLength() {
//		Angle delta = new Angle(0.001, Angle.Units.RADIANS);
		// single point		
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		Assert.assertTrue("zero", svgPath.isZeroLength(0.001));
		// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertFalse("zero", svgPath.isZeroLength(0.001));
		// return to origin		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.001 2.001'/>");
		Assert.assertFalse("zero", svgPath.isZeroLength(0.001));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001 1.000 2.0002'/>");
		Assert.assertTrue("zero", svgPath.isZeroLength(0.01));
	}

	@Test
	public void testIsWedge() {
		Angle delta = new Angle(0.001, Angle.Units.RADIANS);
		double factor = 0.10;
		// single point		
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		Assert.assertFalse("wedge", svgPath.isWedge(0.001, delta, factor));
		// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertFalse("wedge", svgPath.isWedge(0.001, delta, factor));
		// return to origin		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 2.9 4.1 1 2'/>");
		Assert.assertTrue("zero", svgPath.isWedge(0.001, delta, factor));
	}

	@Test
	@Ignore
	public void testGetLongEdges() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testIsBondLike() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testIsArrowHeadLike() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetVertices() {
//		Angle delta = new Angle(0.001, Angle.Units.RADIANS);
// single point		
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
//		List<Real2> pointList = svgPath.extractDistinctPoints(0.001, delta);
		Assert.assertEquals("vertices", 1, svgPath.getVertexR2Array().size());
// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertEquals("vertices", 2, svgPath.getVertexR2Array().size());
// return to origin		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1 2'/>");
		Assert.assertEquals("vertices", 3, svgPath.getVertexR2Array().size());
// straight line with/out angle constraint		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 5'/>");
		Assert.assertEquals("vertices", 3, svgPath.getVertexR2Array().size());
// bent line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 5.5'/>");
		Assert.assertEquals("vertices", 3, svgPath.getVertexR2Array().size());
// overlapping points
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001'/>");
		Assert.assertEquals("vertices", 2, svgPath.getVertexR2Array().size());
// line with overlap		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001 3 4'/>");
		Assert.assertEquals("vertices", 3, svgPath.getVertexR2Array().size());
// return to origin with overlap		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1.0001 2.0001'/>");
		Assert.assertEquals("vertices", 3, svgPath.getVertexR2Array().size());
// mitered polygon		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.01 2.01 3 4 3.01 3.99 3.02 4 1.03 2.02 1 2'/>");
		Assert.assertEquals("vertices", 7, svgPath.getVertexR2Array().size());
// and some real examples
	}

	@Test
	@Ignore
	public void testConvertTwoPointPathToLine() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testConvertTwoPointPathToLines() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateEdges() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testTidyDAttribute() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEnclosesReal2() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEnclosesSVGLine() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testAppendPointToD() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsClosed() {
		// single point		
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		Assert.assertFalse("closed", svgPath.isClosed(0.001, new Angle(0.001, Angle.Units.RADIANS)));
		// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertFalse("closed", svgPath.isClosed(0.001, new Angle(0.001, Angle.Units.RADIANS)));
		// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1 2'/>");
		Assert.assertTrue("closed", svgPath.isClosed(0.001, new Angle(0.001, Angle.Units.RADIANS)));
		// return to origin
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1.001 2.001'/>");
		Assert.assertTrue("closed", svgPath.isClosed(0.01, new Angle(0.001, Angle.Units.RADIANS)));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001 1.000 2.0002'/>");
		Assert.assertFalse("closed", svgPath.isClosed(0.01, new Angle(0.001, Angle.Units.RADIANS)));
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1.001 2.001'/>");
		Assert.assertTrue("closed", svgPath.isClosed(0.01, new Angle(0.001, Angle.Units.RADIANS)));
	}

	@Test
	@Ignore
	public void testGetEdgeList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDistinctEdgeList() {
		Angle deltaTheta = new Angle(0.001, Angle.Units.RADIANS);
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2'/>");
		Assert.assertEquals("distinct", 0, svgPath.extractDistinctEdges(0.01, deltaTheta).size());
		// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4'/>");
		Assert.assertEquals("distinct", 2, svgPath.extractDistinctEdges(0.01, deltaTheta).size());
		// simple line		
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1 2'/>");
		Assert.assertEquals("distinct", 2, svgPath.extractDistinctEdges(0.01, deltaTheta).size());
		// return to origin
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 1.001 2.001'/>");
		Assert.assertEquals("distinct", 2, svgPath.extractDistinctEdges(0.01, deltaTheta).size());
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 1.0001 2.0001 1.000 2.0002'/>");
		Assert.assertEquals("distinct", 0, svgPath.extractDistinctEdges(0.01, deltaTheta).size());
		svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3 4 4 3 1 2'/>");
		Assert.assertEquals("distinct", 3, svgPath.extractDistinctEdges(0.01, deltaTheta).size());
	}

	@Test
	@Ignore
	public void testConcatenateShortTouchingLinesIntoPaths() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore 
	public void testGetArrowDirection() {
		// FIXME NYI
		Angle deltaTheta = new Angle(0.001, Angle.Units.RADIANS);
		SVGChemPath svgPath = makePath(
				"<svg:path xmlns:svg='"+SVG_NS+"' d='M 1 2 L 3.1 3.9 3 4 2.9 4.1 1 2'/>");
		Assert.assertEquals("distinct", 0, svgPath.getArrowDirection(0.01, deltaTheta));
	}

	@Test
	@Ignore
	public void testSetArrowTail() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetArrowPoint() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetArrowTail() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetPathType() {
		fail("Not yet implemented");
	}

}
