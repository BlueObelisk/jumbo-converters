package org.xmlcml.cml.converters.text;

import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.RealArray;

/** extension of TransformElement functionality, separated for convenience
 * this may evolve to a subclass of TransformElement later
 * @author pm286
 *
 */
public class MoleculeTransformElement extends TransformElement {
	private final static Logger LOG = Logger.getLogger(MoleculeTransformElement.class);
	
	public static final String TAG = "moleculeTransform";

	public MoleculeTransformElement(Element element) {
		super(element);
	}
	
	public MoleculeTransformElement() {
		super();
	}

	// attributes
	private static final String ALGORITHM            = "algorithm";
	private static final String AUXILIARY_XPATH      = "auxiliaryXpath";
	private static final String COORD_TYPE           = "coordType";
	private static final String DIMENSION            = "dimension";

	public static String[] OPTIONS = 
		new String[]{
		ALGORITHM,
		AUXILIARY_XPATH,
		COORD_TYPE,
		DIMENSION,
	};
	
	private static String[] ALL_OPTIONS = new String[
	    TransformElement.OPTIONS.length + MoleculeTransformElement.OPTIONS.length];
	static {
		System.arraycopy(TransformElement.OPTIONS, 0, ALL_OPTIONS, 0, TransformElement.OPTIONS.length);
		System.arraycopy(MoleculeTransformElement.OPTIONS, 0, ALL_OPTIONS, TransformElement.OPTIONS.length, MoleculeTransformElement.OPTIONS.length);
	}
		
	
	// attribute values

	// attributes
	private String algorithm;
	private String auxiliaryXpath;
	private String coordType;
	private String dimension;
	
	// process values
	private static final String ADD_COORDS = "addCoords";
	private static final String ADD_FORMULA = "addFormula";
	private static final String ADD_HYDROGENS = "addHydrogens";
	private static final String ADD_MORGAN = "addMorgan";
	private static final String ADD_PARITY = "addParity";
	private static final String ADD_STEREO = "addStereo";
	private static final String BOND_ORDERS = "bondOrders";
	private static final String CONVOLUTE_PROPERTY = "convoluteProperty";
	private static final String CONVERT_FRACTIONAL = "convertFractional";
	private static final String CREATE_BONDS = "createBonds";
	

	// molecule operations (move elsewhere later)


	private static String[] PROCESS_NAMES = new String[] {
		ADD_COORDS,
		ADD_FORMULA,
		ADD_HYDROGENS,
		ADD_MORGAN,
		ADD_PARITY,
		ADD_STEREO,
		BOND_ORDERS,
		CONVOLUTE_PROPERTY,
		CONVERT_FRACTIONAL,
		CREATE_BONDS,
	};
	
	// attributes

	@Override
	protected void processChildElementsAndAttributes() {
		super.processAttributes();
		this.processAttributes();
	}

	@Override
	protected void processAttributes() {
		Template.checkIfAttributeNamesAreAllowed(transformElement, ALL_OPTIONS); 
				
		process = transformElement.getAttributeValue(PROCESS);
		if (process == null) {
			throw new RuntimeException("Must give process attribute");
		}
		
		algorithm                    = addAndIndexAttribute(ALGORITHM);
		auxiliaryXpath               = addAndIndexAttribute(AUXILIARY_XPATH);
		coordType                    = addAndIndexAttribute(COORD_TYPE);
		dimension                    = addAndIndexAttribute(DIMENSION);
	}

	public void applyMarkup(Element element) {
		this.parsedElement = element;
		applyMarkup();
	}

	public void applyMarkup(LineContainer lineContainer) {
		parsedElement = lineContainer.getLinesElement();
		applyMarkup();
	}

	private void applyMarkup() {
		checkProcessExists(process);
		if (process == null) {
			// never reach here
		} else if (CONVOLUTE_PROPERTY.equals(process)) {
			convoluteProperty();
		} else if (CONVERT_FRACTIONAL.equals(process)) {
			convertFractional();
		} else if (ADD_HYDROGENS.equals(process)) {
			addHydrogens();
		} else if (CREATE_BONDS.equals(process)) {
			createBonds();
		} else if (BOND_ORDERS.equals(process)) {
			calculateBondOrders();
		} else if (ADD_COORDS.equals(process)) {
			addCoordinates();
		} else if (ADD_PARITY.equals(process)) {
			addAtomParity();
		} else if (ADD_STEREO.equals(process)) {
			addBondStereo();
		} else if (ADD_FORMULA.equals(process)) {
			addFormula();
		} else if (ADD_MORGAN.equals(process)) {
			addMorgan();
		} else {
			super.unknownProcess();
		}
	}
	
// ========================== methods ===============================
	
	private void addCoordinates() {
		assertRequired(XPATH, xpath);
		assertRequired(COORD_TYPE, coordType);
	}

	private void addFormula() {
		throw new RuntimeException("addFormula NYI");
	}

	private void addHydrogens() {
		assertRequired(XPATH, xpath);
		assertRequired(COORD_TYPE, coordType);
	}

	private void addMorgan() {
		throw new RuntimeException("addMorgan NYI");
	}

	private void addAtomParity() {
		if (true) {
			throw new RuntimeException("NYI");
		}
		assertRequired(XPATH, xpath);
		assertRequired(COORD_TYPE, coordType);
		CoordinateType coordinateType = getCoordinateType(coordType);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLMolecule) {
				CMLMolecule molecule = (CMLMolecule) node;
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				moleculeTool.addAtomParity(coordinateType);
			};
		}
	}

	private static CoordinateType getCoordinateType(String coordType) {
		CoordinateType coordinateType = null;
		if (coordType == null) {
			
		} else if (CoordinateType.TWOD.toString().equalsIgnoreCase(coordType)) {
			coordinateType = CoordinateType.TWOD;
		} else if (CoordinateType.CARTESIAN.toString().equalsIgnoreCase(coordType)) {
			coordinateType = CoordinateType.CARTESIAN;
		}
		return coordinateType;
	}

	private void addBondStereo() {
		if (true) {
			throw new RuntimeException("NYI");
		}
		assertRequired(XPATH, xpath);
		assertRequired(COORD_TYPE, coordType);
		CoordinateType coordinateType = getCoordinateType(coordType);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLMolecule) {
				CMLMolecule molecule = (CMLMolecule) node;
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				moleculeTool.addBondStereo(coordinateType);
			};
		}
	}

	private void calculateBondOrders() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLMolecule) {
				CMLMolecule molecule = (CMLMolecule) node;
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				moleculeTool.adjustBondOrdersToValency();
			};
		}
	}

	private void createBonds() {
		assertRequired(XPATH, xpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLMolecule) {
				CMLMolecule molecule = (CMLMolecule) node;
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				moleculeTool.calculateBondedAtoms();
			};
		}
	}

	private void convertFractional() {
		assertRequired(XPATH, xpath);
		assertRequired(AUXILIARY_XPATH, auxiliaryXpath);
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLMolecule) {
				CMLMolecule molecule = (CMLMolecule) node;
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				CMLCrystal crystal = getCrystal(molecule, auxiliaryXpath);
			}
		}
	}

	private CMLCrystal getCrystal(CMLMolecule molecule, String auxiliaryXpath) {
		CMLCrystal crystal = null;
		return crystal;
	}

	private void convoluteProperty() {	
		assertRequired(XPATH, xpath);
		assertRequired(VALUE_XPATH, valueXpath);
		assertRequired(ARGS, args);
		String[] argList = args.trim().split(CMLConstants.S_WHITEREGEX);
		Map<String, String> nameValues = splitArgs(argList);
		
		Double damping = getDouble(nameValues, "damping", 1.0);
		Integer npasses = getInteger(nameValues, "npasses", 1);
		
		List<Node> nodeList = getXpathQueryResults();
		for (Node node : nodeList) {
			if (node instanceof CMLMolecule) {
				CMLMolecule molecule = (CMLMolecule) node;
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				RealArray realArray = moleculeTool.convolutePropertyWithNeighbours(
						npasses, valueXpath, damping);
				CMLArray array = new CMLArray(realArray);
				array.setTitle("transform valueXpath="+valueXpath+"; "+args);
				molecule.appendChild(array);
			}
		}
	}

	
}
