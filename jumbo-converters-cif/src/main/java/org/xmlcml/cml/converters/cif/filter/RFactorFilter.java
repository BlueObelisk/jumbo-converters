package org.xmlcml.cml.converters.cif.filter;

import nu.xom.Nodes;


import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.filter.AbstractCMLFilter;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.RealRange;


public class RFactorFilter implements AbstractCMLFilter {
	public final static String CIF_R_FACTOR_ALL  = "_refine_ls_r_factor_all";
	public final static String CIF_R_FACTOR_GT   = "_refine_ls_r_factor_gt";
	public final static String CIF_WR_FACTOR_REF = "_refine_ls_wr_factor_ref";
	public final static String CIF_WR_FACTOR_GT  = "_refine_ls_wr_factor_gt";
	
//	public final static String R_FACTOR_ALL  = "RFactorAll";
//	public final static String R_FACTOR_GT   = "RFactorGt";
//	public final static String WR_FACTOR_REF = "WRFactorRef";
//	public final static String WR_FACTOR_GT  = "WRFactorGt";
	private RealRange rFactorAllRange;
	private RealRange rFactorGtRange;
	private RealRange wrFactorRefRange;
	private RealRange wrFactorGtRange;
	/**
  <scalar dictRef="iucr:_refine_ls_r_factor_all" dataType="xsd:double" errorValue="0.0">0.1912</scalar> 
  <scalar dictRef="iucr:_refine_ls_r_factor_gt" dataType="xsd:double" errorValue="0.0">0.0733</scalar> 
  <scalar dictRef="iucr:_refine_ls_wr_factor_ref" dataType="xsd:double" errorValue="0.0">0.1375</scalar> 
  <scalar dictRef="iucr:_refine_ls_wr_factor_gt" dataType="xsd:double" errorValue="0.0">0.1092</scalar> 
 	 */
	public RFactorFilter() {
		
	}
	
	public void setTest(String name, RealRange range) {
		if (CIF_R_FACTOR_ALL.equals(name)) {
			rFactorAllRange = range;
		} else if (CIF_R_FACTOR_GT.equals(name)) {
			rFactorGtRange = range;
		} else if (CIF_WR_FACTOR_GT.equals(name)) {
			wrFactorGtRange = range;
		} else if (CIF_WR_FACTOR_REF.equals(name)) {
			wrFactorRefRange = range;
		} else {
			System.err.println("Unknown Rfactor test: "+name);
		}
	}
	
	public boolean accept(CMLElement element) {
		boolean accept = false;
		if (element != null && element instanceof CMLElement) {
			boolean all = inRange(element, CIF_R_FACTOR_ALL, rFactorAllRange);
			boolean gt = inRange(element, CIF_R_FACTOR_GT, rFactorGtRange);
			boolean wref = inRange(element, CIF_WR_FACTOR_REF, wrFactorRefRange);
			boolean wrgt = inRange(element, CIF_WR_FACTOR_GT, wrFactorGtRange);
			accept = all || gt || wref || wrgt; 

		}
		return accept;
	}
	private boolean inRange(CMLElement element, String name, RealRange range) {
		boolean inRange = false;
		Double value = getScalar(element, name);
		if (value != null && range != null) {
			inRange = range.contains(value);
		}
		return inRange;
	}

	private Double getScalar(CMLElement element, String cifName) {
		Nodes nodes = element.query(
				".//*[local-name()='scalar' and contains(@dictRef, '"+cifName+"')]");
		Double dValue = null;
		if (nodes.size() == 1) {
			String s = nodes.get(0).getValue();
			try {				
				dValue = new Double(s);
			} catch (Exception e) {
				System.err.println("Bad Rfactor: "+s);
			}
		}
		return dValue;
	}

	
}
