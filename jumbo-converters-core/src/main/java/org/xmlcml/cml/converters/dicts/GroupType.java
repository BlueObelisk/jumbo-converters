package org.xmlcml.cml.converters.dicts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.GeometryTool;
import org.xmlcml.cml.tools.MoleculeLayout;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.cml.tools.SMILESTool;
import org.xmlcml.euclid.Util;

public class GroupType extends ChemicalType implements CMLConstants {
	private static Logger LOG = Logger.getLogger(GroupType.class);

	private CMLMolecule molecule;
	private String leftString;
	private String rightString;
	private CMLFormula formula;
	private CMLMolecule groupMolecule;
	
	public CMLMolecule getGroupMolecule() {
		return groupMolecule;
	}

	public GroupType(CMLMolecule molecule, ChemicalTypeMap map) {
		super(molecule);
		this.molecule = molecule;
		addNamesToMap(molecule, map);
		Nodes nodes = molecule.query("cml:name[@convention='cml:abbrevLeft']", CML_XPATH);
		leftString = (nodes.size() > 0) ? nodes.get(0).getValue() : null;
		map.put(leftString, this);
		nodes= molecule.query("cml:name[@convention='cml:abbrevRight']", CML_XPATH);
		rightString = (nodes.size() > 0) ? nodes.get(0).getValue() : null;
		map.put(rightString, this);
		makeMolecule();
	}
	
	private void makeMolecule() {
		Nodes nodes = molecule.query("cml:formula", CML_XPATH);
		formula = (nodes.size() > 0) ? (CMLFormula) nodes.get(0): null;
		if (formula != null) {
			if ("cml:smiles".equals(formula.getConvention())) {
				String smiles = formula.getInline();
				if (smiles != null) {
					smiles = smiles.trim();
					groupMolecule = null;
					try {
						SMILESTool st = new SMILESTool();
						st.parseSMILES(smiles);
						groupMolecule = st.getMolecule();
					} catch (Exception e) {
						throw new RuntimeException(e+" in "+smiles);
					}
					// merge group into molecule WHY???????????????
					if (groupMolecule != null) {
						Elements childElements = molecule.getChildElements();
						for (int i = 0; i < childElements.size(); i++) {
							Element childElement = childElements.get(i);
							String name = childElement.getLocalName();
							if (name.equals("atomArray") || name.equals("bondArray")) {
								continue;
							}
							childElement.detach();
							groupMolecule.appendChild(childElement);
						}
//						groupMolecule.debug("BBBBBBBBBBBBBBB");
						groupMolecule.setId(molecule.getId());
						molecule = groupMolecule;
					}
				}
			}
		}
	}
	
	public void add2DCoordinates() {
		MoleculeLayout moleculeLayout = 
			new MoleculeLayout(MoleculeTool.getOrCreateTool(molecule));
		moleculeLayout.create2DCoordinates();
	}
	
	private void tidy() {
		makeMolecule();
		add2DCoordinates();
    	GeometryTool geometryTool = new GeometryTool(molecule);
    	geometryTool.addCalculatedCoordinatesForHydrogens(CoordinateType.TWOD, HydrogenControl.USE_EXPLICIT_HYDROGENS);
	}

	public static void tidy(InputStream is, String outputDir) {
		if (is != null) {
			Document document = null;
			try {
				document = new CMLBuilder().build(is);
			} catch(Exception e) {
				throw new RuntimeException("EXC ", e);
			}
			ChemicalTypeMap map = new ChemicalTypeMap();
			CMLMoleculeList moleculeList = (CMLMoleculeList) document.getRootElement();
			CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
			CMLMoleculeList newMoleculeList = new CMLMoleculeList();
			for (CMLMolecule molecule1 : molecules) {
				LOG.debug("..."+molecule1.getId());
				GroupType groupType = new GroupType(molecule1, map);
				try {
					groupType.tidy();
					CMLMolecule molecule3 = groupType.getMolecule();
					CMLMolecule molecule2 = new CMLMolecule(molecule3);
					newMoleculeList.addMolecule(molecule2);
					output(molecule2, outputDir);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("????"+molecule1.getId()+"????? EXC "+e);
				}
			}
			if (outputDir != null) {
				try {
					FileOutputStream fos = new FileOutputStream(outputDir+File.separator+"group1.xml");
					newMoleculeList.debug(fos, 1);
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
	}
	
	static void output(CMLMolecule molecule, String outputDir) throws IOException {
		if (outputDir != null) {
			FileOutputStream fos = new FileOutputStream(outputDir+File.separator+molecule.getId()+".cml");
			molecule.debug(fos, 1);
			fos.close();
		}
	}
	
	public String getLeftString() {
		return leftString;
	}

	public String getRightString() {
		return rightString;
	}
	
	public void debug(String msg) {
		if (molecule != null) {
			molecule.debug(msg);
		}
		if (groupMolecule != null) {
			groupMolecule.debug(msg);
		}
		if (formula != null) {
			formula.debug(msg);
		}
	}
	
	public String getConcise() {
		String concise = null;
		if (formula != null) {
			concise = formula.getConcise();
		}
		return concise;
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}

	public CMLFormula getFormula() {
		return formula;
	}

	private static void usage() {
		Util.println("GroupType <options>");
		Util.println("    -IN infile");
		Util.println("    -OUT outfile");
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
			System.exit(0);
		}
		String infile = null;
		String outfile = null;
		int i = 0;
		while (i < args.length) {
			if (args[i].equalsIgnoreCase("-IN")) {
				infile = args[++i]; i++;
			} else if (args[i].equalsIgnoreCase("-OUT")) {
				outfile = args[++i]; i++;
			} else {
				System.err.println("Unknown arg : "+args[i++]);
			}
		}
		InputStream is = null;
//		OutputStream os = null;
		try {
			is = (infile == null) ? null : new FileInputStream(infile);
//			os = (outfile == null) ? null : new FileOutputStream(outfile);
		} catch (IOException e) {
			System.err.println(e);
		}
		tidy(is, outfile);
	}
}
