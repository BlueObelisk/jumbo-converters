package org.xmlcml.cml.converters.molecule.pubchem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLMolecule;

public class Coordinates {
	private Element coordinatesElement;
	private Element data;
	private Element type;
	private Element aid;
	private Element conformers;
	private Set<String> coordinateTypeSet;
	private CMLMolecule molecule;
	
	public Coordinates(Element coordinatesElement, CMLMolecule molecule) {
		this.molecule = molecule;
		this.coordinatesElement = coordinatesElement;
		init();
	}
	
	private void init() {
		data = CMLUtil.getSingleElement(coordinatesElement, "p:PC-Coordinates_data", PubchemUtils.NIH_XPATH);
		type = CMLUtil.getSingleElement(coordinatesElement, "p:PC-Coordinates_type", PubchemUtils.NIH_XPATH);
		aid = CMLUtil.getSingleElement(coordinatesElement, "p:PC-Coordinates_aid", PubchemUtils.NIH_XPATH);
		conformers = CMLUtil.getSingleElement(coordinatesElement, "p:PC-Coordinates_conformers", PubchemUtils.NIH_XPATH);
		if (type == null || aid == null || conformers == null) {
			throw new RuntimeException("Cannot find coordinate children");
		}
		PubchemUtils.checkUnknowns(coordinatesElement, new String[] {
			"PC-Coordinates_data",
			"PC-Coordinates_type",
			"PC-Coordinates_aid",
			"PC-Coordinates_conformers",
		});
		processData();
		processType();
		processAid();
		processConformers();
	}

	
	private void processData() {
		/*
      <PC-Coordinates_data>
        <PC-InfoData>
          <PC-InfoData_urn>
            <PC-Urn>
              <PC-Urn_label>Conformer</PC-Urn_label>
              <PC-Urn_name>RMSD</PC-Urn_name>
              <PC-Urn_datatype>
                <PC-UrnDataType value="double">7</PC-UrnDataType>
              </PC-Urn_datatype>
              <PC-Urn_release>2007.10.31</PC-Urn_release>
            </PC-Urn>
          </PC-InfoData_urn>
          <PC-InfoData_value>
            <PC-InfoData_value_fval>0.4</PC-InfoData_value_fval>
          </PC-InfoData_value>
        </PC-InfoData>
      </PC-Coordinates_data>
		 */
	}
	
	private void processType() {
		coordinateTypeSet = new HashSet<String>();
		Nodes values = type.query("p:PC-CoordinateType/@value", PubchemUtils.NIH_XPATH);
		for (int i = 0; i < values.size(); i++) {
			String val = values.get(i).getValue();
			if (val.equals("twod")) {
			} else if (val.equals("threed")) {
			} else if (val.equals("computed")) {
			} else if (val.equals("units-unknown")) {
			} else {
				throw new RuntimeException("Unknown coordinate type: "+val);
			}
			coordinateTypeSet.add(val);
		}
	}

	private void processAid() {
		List<String> aidList = new ArrayList<String>();
		Nodes values = aid.query("p:PC-Coordinates_aid_E", PubchemUtils.NIH_XPATH);
		for (int i = 0; i < values.size(); i++) {
			String val = values.get(i).getValue();
			aidList.add(val);
		}
		PubchemUtils.checkUnknowns(aid, new String[] {
			"PC-Coordinates_aid_E",
		});
	}

	private void processConformers() {
		Element conformer = CMLUtil.getSingleElement(
				conformers, "p:PC-Conformer", PubchemUtils.NIH_XPATH);
		if (conformer == null) {
			throw new RuntimeException("expected conformer element");
		}
		PubchemUtils.checkUnknowns(conformers, new String[] {
			"PC-Conformer",
		});
		Element conformerX = CMLUtil.getSingleElement(
				conformer, "p:PC-Conformer_x", PubchemUtils.NIH_XPATH);
		Element conformerY = CMLUtil.getSingleElement(
				conformer, "p:PC-Conformer_y", PubchemUtils.NIH_XPATH);
		Element conformerZ = CMLUtil.getSingleElement(
				conformer, "p:PC-Conformer_z", PubchemUtils.NIH_XPATH);
		Element conformerData = CMLUtil.getSingleElement(
				conformer, "p:PC-Conformer_data", PubchemUtils.NIH_XPATH);
		Element conformerStyle = CMLUtil.getSingleElement(
				conformer, "p:PC-Conformer_style", PubchemUtils.NIH_XPATH);
		PubchemUtils.checkUnknowns(conformer, new String[] {
			"PC-Conformer_x",
			"PC-Conformer_y",
			"PC-Conformer_z",
			"PC-Conformer_data",
			"PC-Conformer_style",
		});
		List<CMLAtom> atomList = molecule.getAtoms();
		int size = atomList.size();
		double[] x = getDoubles(conformerX, size, "p:PC-Conformer_x_E");
		double[] y = getDoubles(conformerY, size, "p:PC-Conformer_y_E");
		double[] z = (conformerZ == null) ? null : getDoubles(conformerZ, size, "p:PC-Conformer_z_E");
		if (z == null) {
			for (int i = 0; i < size; i++) {
				CMLAtom atom = atomList.get(i);
				atom.setX2(x[i]);
				atom.setY2(y[i]);
			}
		} else {
			for (int i = 0; i < size; i++) {
				CMLAtom atom = atomList.get(i);
				atom.setX3(x[i]);
				atom.setY3(y[i]);
				atom.setZ3(z[i]);
			}
		}
		if (conformerStyle != null) {
			processConformerStyle(conformerStyle);
		}
		if (conformerData != null) {
			processConformerData(conformerData);
		}
	}

	/**
	 * @param conformer
	 * @param conformerStyle
	 * @throws RuntimeException
	 */
	private void processConformerStyle(Element conformerStyle)
			throws RuntimeException {
		int size;
		Element drawAnnotations = CMLUtil.getSingleElement(
			conformerStyle, "p:PC-DrawAnnotations", PubchemUtils.NIH_XPATH);
		if (drawAnnotations == null) {
			throw new RuntimeException("null drawAnnotations");
		}
		Element annotation = CMLUtil.getSingleElement(
			drawAnnotations, "p:PC-DrawAnnotations_annotation", PubchemUtils.NIH_XPATH);
		Element aid1 = CMLUtil.getSingleElement(
			drawAnnotations, "p:PC-DrawAnnotations_aid1", PubchemUtils.NIH_XPATH);
		Element aid2 = CMLUtil.getSingleElement(
			drawAnnotations, "p:PC-DrawAnnotations_aid2", PubchemUtils.NIH_XPATH);
		PubchemUtils.checkUnknowns(drawAnnotations, new String[] {
			"PC-DrawAnnotations_annotation",
			"PC-DrawAnnotations_aid1",
			"PC-DrawAnnotations_aid2",
		});
		Nodes annotations = annotation.query(
				"p:PC-BondAnnotation/@value", PubchemUtils.NIH_XPATH);
		Nodes aid1s = aid1.query("p:PC-DrawAnnotations_aid1_E", PubchemUtils.NIH_XPATH);
		Nodes aid2s = aid2.query("p:PC-DrawAnnotations_aid2_E", PubchemUtils.NIH_XPATH);
		PubchemUtils.checkUnknowns(annotation, new String[] {
			"PC-BondAnnotation",
			});
		PubchemUtils.checkUnknowns(aid1, new String[] {
			"PC-DrawAnnotations_aid1_E",
		});
		PubchemUtils.checkUnknowns(aid2, new String[] {
			"PC-DrawAnnotations_aid2_E",
		});
		size = annotations.size();
		if (size != aid1s.size() ||
			size != aid2s.size()) {
			throw new RuntimeException("Unequal children of annotation: "+size+"/"+aid1s.size()+"/"+aid2s.size());
		}
		String[] values = new String[size];
		String[] aid1e = new String[size];
		String[] aid2e = new String[size];
		for (int i = 0; i < size; i++) {
			values[i] = annotations.get(i).getValue();
			aid1e[i] = aid1s.get(i).getValue();
			aid2e[i] = aid2s.get(i).getValue();
			CMLBond bond = molecule.getBondByAtomIds(
					PubchemUtils.createAtomId(aid1e[i]), 
					PubchemUtils.createAtomId(aid2e[i]));
			if (values[i].equals("aromatic")) {
				// orders are already set to 1/2
//				bond.setOrder("A");
			} else if (values[i].equals("wedge-down")) {
				CMLBondStereo bondStereo = new CMLBondStereo();
				bondStereo.setXMLContent(CMLBond.HATCH);
				bondStereo.setConvention("pubchem");
				bond.addBondStereo(bondStereo);
			} else if (values[i].equals("wedge-up")) {
				CMLBondStereo bondStereo = new CMLBondStereo();
				bondStereo.setXMLContent(CMLBond.WEDGE);
				bondStereo.setConvention("pubchem");
				bond.addBondStereo(bondStereo);
			} else if (values[i].equals("wavy")) {
				CMLBondStereo bondStereo = new CMLBondStereo();
				bondStereo.setXMLContent("wavy");
				bondStereo.setConvention("pubchem");
				bond.addBondStereo(bondStereo);
			} else if (values[i].equals("dotted")) {
//				CMLBondStereo bondStereo = new CMLBondStereo();
//				bondStereo.setXMLContent("wavy");
//				bondStereo.setConvention("pubchem");
//				bond.addBondStereo(bondStereo);
				bond.setOrder(CMLBond.UNKNOWN_ORDER);
			} else {
				throw new RuntimeException("unknown bond annotation: "+values[i]);
			}
		}
	}

	/**
	 * @param conformer
	 * @param conformerStyle
	 * @throws RuntimeException
	 */
	private void processConformerData(Element conformerData) {
		// no-op at present
/*
          <PC-Conformer_data>
            <PC-InfoData>
              <PC-InfoData_urn>
                <PC-Urn>
                  <PC-Urn_label>Conformer</PC-Urn_label>
                  <PC-Urn_name>ID</PC-Urn_name>
                  <PC-Urn_datatype>
                    <PC-UrnDataType value="uint64">11</PC-UrnDataType>
                  </PC-Urn_datatype>
                  <PC-Urn_version>2.2.1</PC-Urn_version>
                  <PC-Urn_software>Omega</PC-Urn_software>
                  <PC-Urn_source>openeye.com</PC-Urn_source>
                  <PC-Urn_release>2007.10.31</PC-Urn_release>
                </PC-Urn>
              </PC-InfoData_urn>
              <PC-InfoData_value>
                <PC-InfoData_value_sval>000009D700000001</PC-InfoData_value_sval>
              </PC-InfoData_value>
            </PC-InfoData>
            <PC-InfoData>
              <PC-InfoData_urn>
                <PC-Urn>
                  <PC-Urn_label>Energy</PC-Urn_label>
                  <PC-Urn_name>MMFF94 NoEstat</PC-Urn_name>
                  <PC-Urn_datatype>
                    <PC-UrnDataType value="double">7</PC-UrnDataType>
                  </PC-Urn_datatype>
                  <PC-Urn_version>1.1.1</PC-Urn_version>
                  <PC-Urn_software>Case</PC-Urn_software>
                  <PC-Urn_source>openeye.com</PC-Urn_source>
                  <PC-Urn_release>2009.03.23</PC-Urn_release>
                </PC-Urn>
              </PC-InfoData_urn>
              <PC-InfoData_value>
                <PC-InfoData_value_fval>22.9007</PC-InfoData_value_fval>
              </PC-InfoData_value>
            </PC-InfoData>
            <PC-InfoData>
              <PC-InfoData_urn>
                <PC-Urn>
                  <PC-Urn_label>Shape</PC-Urn_label>
                  <PC-Urn_name>Multipoles</PC-Urn_name>
                  <PC-Urn_datatype>
                    <PC-UrnDataType value="doublevec">8</PC-UrnDataType>
                  </PC-Urn_datatype>
                  <PC-Urn_version>1.7.0</PC-Urn_version>
                  <PC-Urn_software>OEShape</PC-Urn_software>
                  <PC-Urn_source>openeye.com</PC-Urn_source>
                  <PC-Urn_release>2009.03.23</PC-Urn_release>
                </PC-Urn>
              </PC-InfoData_urn>
              <PC-InfoData_value>
                <PC-InfoData_value_fvec>
                  <PC-InfoData_value_fvec_E>4.01</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>2.84</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0.6</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>-0.1</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>-0.4</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0.09</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0.26</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>0</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>2.1</PC-InfoData_value_fvec_E>
                  <PC-InfoData_value_fvec_E>-0.99</PC-InfoData_value_fvec_E>
                </PC-InfoData_value_fvec>
              </PC-InfoData_value>
            </PC-InfoData>
            <PC-InfoData>
              <PC-InfoData_urn>
                <PC-Urn>
                  <PC-Urn_label>Shape</PC-Urn_label>
                  <PC-Urn_name>Volume</PC-Urn_name>
                  <PC-Urn_datatype>
                    <PC-UrnDataType value="double">7</PC-UrnDataType>
                  </PC-Urn_datatype>
                  <PC-Urn_version>1.7.0</PC-Urn_version>
                  <PC-Urn_software>OEShape</PC-Urn_software>
                  <PC-Urn_source>openeye.com</PC-Urn_source>
                  <PC-Urn_release>2009.03.23</PC-Urn_release>
                </PC-Urn>
              </PC-InfoData_urn>
              <PC-InfoData_value>
                <PC-InfoData_value_fval>145.8</PC-InfoData_value_fval>
              </PC-InfoData_value>
            </PC-InfoData>
          </PC-Conformer_data>
 */
		Element data = CMLUtil.getSingleElement(
			conformerData, "p:PC-DrawAnnotations", PubchemUtils.NIH_XPATH);
	}
	/**
	 * @param conformerX
	 * @param atomList
	 * @throws RuntimeException
	 * @throws NumberFormatException
	 */
	private double[] getDoubles(Element conformerX, int size, String name)
			throws RuntimeException, NumberFormatException {
		Nodes nodes = conformerX.query(name, PubchemUtils.NIH_XPATH);
		if (nodes.size() != size) {
			throw new RuntimeException("inconsistent coordinate length");
		}
		double[] coord = new double[size];
		for (int i = 0; i < size; i++) {
			String val = null;
			try {
				val = nodes.get(i).getValue();
				coord[i] = new Double(val);
			} catch (NumberFormatException nfe) {
				throw new NumberFormatException("cannot parse as double "+val);
			}
		}
		return coord;
	}
}
