package org.xmlcml.cml.converters.spectrum.graphics.svg2cml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;

public class LineCluster {
	@SuppressWarnings("unused")
    private static Logger LOG = Logger.getLogger(LineCluster.class);
    
    private List<SVGLine> lineList = null;
    private List<Real2> pointList = null;
    private double eps;

	private Real2Range boundingBox;
    
	public LineCluster(double eps) {
	    lineList = new ArrayList<SVGLine>();
	    pointList = new ArrayList<Real2>();
	    this.eps = eps;
		boundingBox = new Real2Range();
    }
    
    public List<SVGLine> getLineList() {
		return lineList;
	}

	public List<Real2> getPointList() {
		return pointList;
	}

	public void addLine(SVGLine l) {
		if (!lineList.contains(l)) {
			lineList.add(l);
			addPoint(l.getXY(0));
			addPoint(l.getXY(1));
		}
	}
	
	public void addPoint(Real2 p) {
		boolean add = true;
		for (Real2 point : pointList) {
			if (point.isEqualTo(p, eps)) {
				add = false;
				break;
			}
		}
		if (add) {
			pointList.add(p);
			boundingBox.add(p);
		}
	}
	
	public boolean contains(Real2 point) {
		boolean contains = false;
		if (this.getBoundingBox().includes(point)) {
			for (Real2 p : pointList) {
				if (p.isEqualTo(point, eps)) {
					contains = true;
					break;
				}
			}
		}
		return contains;
	}
	
	public boolean containsCommonPoint(SVGLine line) {
		boolean contains = false;
		Real2Range overlapBox = line.getBoundingBox().intersectionWith(boundingBox);
		if (overlapBox != null && overlapBox.isValid()) {
			for (Real2 p : pointList) {
				if (p.isEqualTo(line.getXY(0), eps) ||
					p.isEqualTo(line.getXY(1), eps)) {
					contains = true;
					break;
				}
			}
		}
		return contains;
	}
	
	public boolean containsCommonPoint(LineCluster cluster) {
		boolean contains = false;
		Real2Range overlapBox = cluster.getBoundingBox().intersectionWith(boundingBox);
		if (overlapBox != null && overlapBox.isValid()) {
			for (Real2 p : pointList) {
				if (cluster.contains(p)) {
					contains = true;
					break;
				}
			}
		}
		return contains;
	}
	
	/** makes tJoint in either direction (line->cluster or cluster->line)
	 * 
	 * @param line
	 * @return makesJoint
	 */
	public boolean makesTJointWith(SVGLine line) {
		boolean makes = false;
		Real2Range overlapBox = line.getBoundingBox().intersectionWith(boundingBox);
		if (overlapBox != null && overlapBox.isValid()) {
			for (SVGLine l : lineList) {
				if (line.makesTJointWith(l, eps) ||
					l.makesTJointWith(line, eps)) {
					makes = true;
					break;
				}
			}
		}
		return makes;
	}
	
	/** makes tJoint in either direction
	 * 
	 * @param cluster
	 * @return makes joint
	 */
	public boolean makesTJointWith(LineCluster cluster) {
		boolean makes = false;
		Real2Range overlapBox = cluster.getBoundingBox().intersectionWith(boundingBox);
		if (overlapBox != null && overlapBox.isValid()) {
			for (SVGLine clusterLine : cluster.lineList) {
				if (this.makesTJointWith(clusterLine)) {
					makes = true;
					break;
				}
			}
		}
		return makes;
	}
	
	public void merge(LineCluster cluster) {
		for (SVGLine l : cluster.lineList) {
			if (!this.lineList.contains(l)) {
				this.lineList.add(l);
			}
		}
	}
	
	public Real2Range getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new Real2Range();
			for (Real2 r2 : pointList) {
				boundingBox.add(r2);
			}
		}
		return boundingBox;
	}
}
