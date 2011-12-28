package org.xmlcml.cml.converters.molecule.mdl;

import static org.xmlcml.euclid.EuclidConstants.C_QUOT;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_EQUALS;
import static org.xmlcml.euclid.EuclidConstants.S_LBRAK;
import static org.xmlcml.euclid.EuclidConstants.S_MINUS;
import static org.xmlcml.euclid.EuclidConstants.S_QUOT;
import static org.xmlcml.euclid.EuclidConstants.S_RBRAK;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;
/**
 * This class handles conversion of ChemicalMarkupLanguage documents to and from
 * MDL Information Systems MolFile format. This class has support for both the
 * V2000 and V3000 MolFile formats. When reading files the version is detected
 * automatically and can later be found using getVersion. When writing files,
 * use setVersion to dictate what verison of MolFile is written out. <br>
 * <br>
 * The converter currently has full support for the V2000 & V3000 connection
 * tables, as well as many properties of atoms including charge, isotopic mass,
 * and spin multiplicity. <br>
 * <br>
 * Stereochemistry support is currently limited.
 * 
 * @author Peter Murray-Rust, Ramin Ghorashi (2005)
 * branched/amended for Jumbo-converter 2008 (PMR)
 * 
 */
public class MDLConverter {

	private static final Logger LOG = Logger
		.getLogger(MDLConverter.class);
	
    private enum MDLTag {
        /** unknown MDL dimensional code */
        DUNK(S_SPACE+S_SPACE),
        /** represents the MDL dimensional code for a 2D molecule */
        D2("2D"),
        /** represents the MDL dimensional code for a 2D molecule */
        D3("3D"),
        /** represents the tag in the MDL Properties Block for Charge */
        M_CHG("M  CHG"),
        /**
         * represents the tag in the MDL Properties Block terminating the
         * MolFile
         */
        M_END("M  END"),
        /** represents the tag in the MDL Properties Block for an Isotopes list */
        M_ISO("M  ISO"),
        /** represents the tag in the MDL Properties Block for a Radical list */
        M_RAD("M  RAD"),
        /**
         * represents the tag in the MDL Properties Block for an SGroup atom
         * list
         */
        M_SAL("M  SAL"),
        /**
         * represents the tag in the MDL Properties Block for an SGroup bond
         * list
         */
        M_SBL("M  SBL"),
        /**
         * represents the tag in the MDL Properties Block for an SGroup bond
         * vector (display only)
         */
        M_SBV("M  SBV"),
        /**
         * represents the tag in the MDL Properties Block for the expansion
         * status of an SGroup (display only)
         */
        M_SDS_EXP("M  SDS EXP"),
        /**
         * represents the tag in the MDL Properties Block for a Unique Sgroup
         * identifier (MACCS-II)
         */
        M_SLB("M  SLB"),
        /**
         * represents the tag in the MDL Properties Block for the subscript text
         * of an SGroup
         */
        M_SMT("M  SMT"),
        /** represents the tag in the MDL Properties Block for an SGroup type */
        M_STY("M  STY"),
        /** represents the tag in the MDL Properties Block for an atom Alias */
        A__("A  "),
        /** represents the deprecated tag in the MDL Properties Block for Group */
        G__("G  "),
        /** undocumented atom label */
        V__("V  "),
        /** represents the skip tag in the MDL Properties Block */
        S_SKP("S  SKP"),

        // V3000 keywords
        /** represents the prefix of all lines in a V3000 MolFile */
        M_V30("M  V30 "),
        /** represents the V3000 keyword for the charge on an atom */
        V3_CHARGE("CHG"),
        /** represents the V3000 keyword for the isotopic mass of an atom */
        V3_ISOTOPE("MASS"),
        /** represents the V3000 keyword for the spin multiplicity of an atom */
        V3_RADICAL("RAD"),
        /** represents the V3000 keyword for the hydrogen count of an atom */
        V3_HCOUNT("HCOUNT"),
        /**
         * represents the V3000 keyword for the stereochemistry of an atom or
         * bond
         */
        V3_STEREO("CFG"),
        ;

        String tag;

        MDLTag(String tag) {
            this.tag = tag;
        }
    }
    
    public enum CoordType {
    	TWOD,
    	THREED,
    	EITHER
    }

	private static class SGroup {
	    private static Map<Integer, SGroup> idMap = new HashMap<Integer, SGroup>();
	    int id;
	    String type = "";
	    List<CMLAtom> atomList = new ArrayList<CMLAtom>();
	    List<CMLBond> bondList = new ArrayList<CMLBond>();
	    String label = "";
	    boolean expanded = false;
	    /**
	     * constructor.
	     * 
	     * @param id
	     * @param type
	     */
	    public SGroup(int id, String type) {
	        this.id = id;
	        this.type = type;
	        if (idMap.get(this.id) != null) {
	            throw new RuntimeException("duplicate SGROUP id");
	        }
	        idMap.put(this.id, this);
	    }
	
	    /**
	     * create SGroup.
	     * 
	     * @param id
	     * @return SGroup
	     */
	    public static SGroup getSGroup(int id) {
	        return idMap.get(id);
	    }
	
	    /**
	     * iterator.
	     * 
	     * @return iterator
	     */
	    public static Iterator<SGroup> getSGroupIterator() {
	        return idMap.values().iterator();
	    }
	
	    /* public */static void tidySGroups() {
	        for (SGroup sGroup : idMap.values()) {
	            sGroup.tidy();
	        }
	    }
	
	    /* public */static void clearMap() {
	        idMap = new HashMap<Integer, SGroup>();
	    }
	
	    /**
	     * add atom
	     * 
	     * @param theAtom
	     */
	    public void addAtom(CMLAtom theAtom) {
	        if (atomList.contains(theAtom)) {
	            throw new RuntimeException("Duplicate atom in SGroup: "
	                    + theAtom.getRef());
	        }
	        atomList.add(theAtom);
	    }
	
	    /**
	     * add bond
	     * 
	     * @param theBond
	     */
	    public void addBond(CMLBond theBond) {
	        if (bondList.contains(theBond)) {
	            throw new RuntimeException("Duplicate bond in SGroup: "
	                    + theBond.getRef());
	        }
	        bondList.add(theBond);
	    }
	
	    /**
	     * set label.
	     * 
	     * @param label
	     */
	    public void setLabel(String label) {
	        this.label = label;
	    }
	
	    /**
	     * set expanded
	     * 
	     * @param expanded
	     */
	    public void setExpanded(boolean expanded) {
	        this.expanded = expanded;
	    }
	
	    private void tidy() {
	        /*
	         * FIXME SGroup tidy() if (atomList.size() == 1) { String atomId =
	         * atomList.get(0).getId(); int atomNumber =
	         * parseInteger(atomId.trim())-1; CMLAtom atom =
	         * CMLMolecule.getAtom(atomNumber); if (atom == null) { throw new
	         * RuntimeException("cannot find atom: "); } if (!label.equals("")) {
	         * CMLLabel cmlLabel = new CMLLabel(); cmlLabel.setCMLValue(label);
	         * atom.addLabel(cmlLabel); } atom.setElementType("R"); //all Groups are
	         * "R" }
	         * 
	         * if (bondList.size() != 0) { // probably redundant }
	         */
	    }
	}

	/** represents the V2000 version tag */
    public final static String V2000 = "V2000";

    /** represents the V3000 version tag */
    public final static String V3000 = "V3000";

    private boolean v3000 = false;
    
    /** number of atoms to be read in the MOL molecule */
    private int molAtomCount;
    /** number of bonds to be read in the MOL molecule */
    private int molBondCount;
    private MDLTag dimensionalCode = MDLTag.DUNK;
    private String version = "V2000";
    private Map<Integer, CMLAtom> atomByNumber = new HashMap<Integer, CMLAtom>();
    private Map<Integer, CMLBond> bondByNumber = new HashMap<Integer, CMLBond>();
    private Map<CMLAtom, Integer> numberByAtom = new HashMap<CMLAtom, Integer>();

    private int nline = 0;
	private List<String> lines;
	private CMLMolecule molecule;

	private CoordType coordType;
    
	public CoordType getCoordType() {
		return coordType;
	}

	public void setCoordType(CoordType coordType) {
		this.coordType = coordType;
	}

	/** constructor
	 * 
	 */
	public MDLConverter() {
		init();
	}
	
	public int getNline() {
		return nline;
	}

	/** constructor
	 * start reading at given line
	 */
	public MDLConverter(int nline) {
		init();
		this.nline = nline;
	}
	
	protected void init() {
		coordType = CoordType.EITHER;
	}
	
    /**
     * translates CML bond order codes into MDLMol numbers
     * 
     * @param cmlCode the CML bondorder code
     * @return the MDL bondorder number
     */
    private static int molBondOrder(String cmlCode) {
        int order = 0;
        if (cmlCode == null) {
            order = 0;
        } else if (cmlCode.equals(CMLBond.UNKNOWN_ORDER)) {
            order = 0;
        } else if (cmlCode.equals(CMLBond.SINGLE_S)) {
            order = 1;
        } else if (cmlCode.equals(CMLBond.DOUBLE_D)) {
            order = 2;
        } else if (cmlCode.equals(CMLBond.TRIPLE_T)) {
            order = 3;
        } else if (cmlCode.equals(CMLBond.AROMATIC)) {
            order = 4;
        } else {
            order = 0;
        }
        return order;
    }

    /**
     * translates MDLMol bond order numbers CML codes
     * 
     * @param molNumber the MDLMol bond order number
     * @return the CML bond order code
     */
    private static String cmlBondOrder(int molNumber) {
        String order = S_EMPTY;
        if (molNumber == 0) {
            order = CMLBond.UNKNOWN_ORDER;
        } else if (molNumber == 1) {
            order = CMLBond.SINGLE_S;
        } else if (molNumber == 2) {
            order = CMLBond.DOUBLE_D;
        } else if (molNumber == 3) {
            order = CMLBond.TRIPLE_T;
        } else if (molNumber == 4) {
            order = CMLBond.AROMATIC;
        } else {
            order = CMLBond.SINGLE_S;
        }
        return order;
    }

    /**
     * translates CML bond stereo codes into MDLMol numbers
     * 
     * @param cmlCode the CML bondstereo code
     * @return the MDL bondstereo number
     */
    private static int molBondStereo(String cmlCode) {
        int stereo = 0;
        if (cmlCode == null) {
            stereo = 0;
        } else if (cmlCode.equals(CMLBond.WEDGE)) {
            stereo = 1;
        } else if (cmlCode.equals(CMLBond.HATCH)) {
            stereo = 6;
        } else {
            stereo = 0;
        }
        return stereo;
    }

    /**
     * translates CML bond stereo codes into MDLMol V3000 numbers
     * 
     * @param cmlCode the CML bondstereo code
     * @return the V3000 MDL bondstereo number
     */
    private static int v3molBondStereo(String cmlCode) {
        int stereo = 0;
        if (cmlCode == null) {
            stereo = 0;
        } else if (cmlCode.equals(CMLBond.WEDGE)) {
            stereo = 1;
        } else if (cmlCode.equals(CMLBond.HATCH)) {
            stereo = 3;
        } else {
            stereo = 0;
        }
        return stereo;
    }

    /**
     * translates MDL numbers into JUMBO-MOL codes
     */
    private static String cmlStereoBond(int molNumber) {
        String stereo = S_EMPTY;
        if (molNumber == 1) {
            stereo = CMLBond.WEDGE;
        } else if (molNumber == 6) {
            stereo = CMLBond.HATCH;
        } else {
            stereo = CMLBond.NOSTEREO;
        }
        return stereo;
    }

    /**
     * translates V3000-MDL numbers into JUMBO-MOL codes
     */
    private static String v3cmlStereoBond(int molNumber) {
        String stereo = S_EMPTY;
        if (molNumber == 1) {
            stereo = CMLBond.WEDGE;
        } else if (molNumber == 2) {
            // either TODO sort out "either" stereo config
            stereo = "";
        } else if (molNumber == 3) {
            stereo = CMLBond.HATCH;
        } else {
            stereo = CMLBond.NOSTEREO;
        }
        return stereo;
    }

    /**
     * Checks if a particular element exists in the periodic table as defined
     * within the ChemicalElement class
     * 
     * @param element
     *            AS of the element to find
     * @return true if element exists, false otherwise
     */
    private static boolean elementExists(String element) {
        return ChemicalElement.getChemicalElement(element) != null;
    }

    /**
     * Adds zeros on the left of string s until it reaches length l
     * 
     * @param s
     *            String to pad
     * @param l
     *            Required length of final string
     * @return Padded string
     */
    private static String padLeftZero(String s, int l) {
        int ll = s.length();
        while (ll++ < l) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * Parses a string as an integer. Uses Integer.parseInt after removing any
     * leading zeros
     * 
     * @param s
     *            String containing the integer to be parsed
     * @return the parsed integer
     */
    private static int parseInteger(String s) {
        int i = 0;
        int l = s.length();
        s = s.trim();

        // trim leading zeros
        while (l > 1 && s.charAt(0) == '0') {
            s = s.substring(1);
            l--;
        }

        if (!(s.equals(S_EMPTY))) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("bad integer in: " + s);
            }
        }

        return i;
    }

    /**
     * Parses the substring of a string as an integer. Uses Integer.parseInt
     * after removing any leading zeros
     * 
     * @param numbr the full string containg the integer
     * @param start the start index of the interger to be parsed
     * @param end the end index of the interger to be parsed
     * @return the parsed ingeger
     */
    private static int parseInteger(String numbr, int start, int end) {
    	int n = 0;
        if (numbr != null && numbr.length() >= end) {
            numbr = numbr.substring(start, end).trim();
            n = parseInteger(numbr);
        }
        return n;
    }

    /**
     * Outputs an integer for inclusion in an MDLMolFile, the integer is
     * outputted in the format "000" and will be 3 characters long
     * 
     * @param intgr
     * @return the integer
     */
    private static String outputMDLInt(int intgr) {
        String s = "" + intgr;
        while (s.length() < 3) {
            s = S_SPACE + s;
        }
        return s;
    }

    /**
     * Outputs a float for inclusion in an MDLMolFile, the float is outputted in
     * the format "####0.0000" and will be 10 characters long (F10.4)
     * 
     * @param value
     * @return the float
     */
    private static String outputMDLFloat(double value) {
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(symbols);
        format.applyPattern("####0.0000");

        String s = format.format(value);
        while (s.length() < 10) {
            s = S_SPACE + s;
        }
        return s;
    }

    /**
     * Sets the version of MolFile that MDLConverter will write out. If version
     * isnt set before writing a MolFile the default value of V2000 is used.
     * 
     * @param version
     *            either MDLConverter.V2000 or MDLConverter.V3000
     */
    public void setVersion(String version) {
        this.version = version;
        if (!version.equals(V2000) && !version.equals(V3000)) {
            throw new RuntimeException("unknown MDLMol version");
        }
    }

    /**
     * Returns the version of MolFile that MDLConverter will write out, if
     * called after reading in a MolFile it will return the version of that
     * MolFile
     * 
     * @return version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Reads an input stream containing either a single MDL molecule or
     * positioned at the start of one. The version of MolFile is automatically
     * detected and can be found using getVersion()
     * 
     * @param lines
     * @return the parsed CMLMolecule
     */
    public CMLMolecule readMOL(List<String> lines) {
    	molecule = new CMLMolecule();
    	this.lines = lines;
        readHeader();
        // probably EOI
        if (molecule == null) {
        	return molecule;
        }
        if (version.equals(V2000)) {
            readAtoms();
            readBonds();
            readFooter();
            addMolecularCharge();
        } else if (version.equals(V3000)) {
            // find start of CTAB
            String line = nextLine();
            while (!line.equals("BEGIN CTAB")) {
                line = nextLine();
                if (line == null) {
                    throw new RuntimeException("CTAB block not found!");
                }
            }
            v3readCountsLine(nextLine());

            // find start of ATOM block
            line = nextLine();
            while (!line.equals("BEGIN ATOM")) {
                line = nextLine();
                if (line == null) {
                	throw new RuntimeException("ATOM block not found!");
                }
            }
            v3readAtomBlock();

            // find start of BOND block
            line = nextLine();
            while (!line.equals("BEGIN BOND")) {
                line = nextLine();
                if (line == null) {
                    throw new RuntimeException("BOND block not found!");
                }
            }
            v3readBondBlock();
        } else {
            throw new RuntimeException("unknown version:" + version);
        }

        // figure out which atoms are chiral, and add atom parity information
        // List<CMLAtom> chiralAtoms = molecule.getChiralAtoms();

        return molecule;
    }

    private void addMolecularCharge() {
    	List<CMLAtom> atoms = molecule.getAtoms();
    	int formalCharge = 0;
    	for (CMLAtom atom : atoms) {
    		if (atom.getFormalChargeAttribute() != null) {
    			formalCharge+= atom.getFormalCharge();
    		}
    	}
    	molecule.setFormalCharge(formalCharge);
	}

	/**
     * Reads the header of an MDLMolFile, check the version is supported, reads
     * in the title, date, comment and counts line
     */
    private void readHeader() {
        // line 1 TITLE (MIGHT be blank)
        String title = nextLine();

        if (title.startsWith("$MDL")) {
            throw new RuntimeException("RGFiles currently not supported");
        } else if (title.startsWith("$RXN")) {
            throw new RuntimeException("RXNFiles currently not supported");
        } else if (title.startsWith("$RDFILE")) {
            throw new RuntimeException("RDFiles currently not supported");
        } else if (title.startsWith("<XDfile>")) {
            throw new RuntimeException("XDFiles currently not supported");
        }

        if (title.trim() != "") {
            molecule.setTitle(title.trim());
        }

        // line 2 HEADER
        String date = S_EMPTY;
        String line = nextLine();
        // blank line indicates EOI  
        if (line == null) {
        	molecule = null;
        	return;
        }

        if (!line.equals(S_EMPTY)) {
            try {
                date = line.substring(10, 20);
                // 2- or 3- dim? or missing
                if (line.substring(20, 22).trim().equals(MDLTag.D2.tag)) {
                    dimensionalCode = MDLTag.D2;
                } else if (line.substring(20, 22).trim().equals(MDLTag.D3.tag)) {
                    dimensionalCode = MDLTag.D3;
                }
            } catch (StringIndexOutOfBoundsException e) {
                // doesnt matter
            }
        }

        if (!date.trim().equals(S_EMPTY)) {
            int y = parseInteger(date, 4, 6);
            String year = (y > 50) ? S_EMPTY + (y + 1900) : S_EMPTY
                    + (y + 2000);
            String month = date.substring(0, 2).trim();
            String day = date.substring(2, 4).trim();
            date = year + S_MINUS + month + S_MINUS + day;
            // Date d = new Date(date);
            // TODO do something with the date read from MOL
        }

        // line 3 COMMENT
        String comment = nextLine();

        if (!comment.trim().equals(S_EMPTY)) {
            // TODO add constructor for CMLLabel to set value?
            CMLLabel label = new CMLLabel();
            label.setCMLValue(comment.trim());
        }

        // start the CTAB
        // line 4 counts
        line = nextLine();

        // read number of atoms & bonds
        molAtomCount = parseInteger(line, 0, 3);
        molBondCount = parseInteger(line, 3, 6);

        try {
            if (line.substring(34, 39) != "     ") {
                version = line.substring(34, 39);
            }
        } catch (StringIndexOutOfBoundsException e) {
            // doesnt matter
        }
    }

    /**
     * Reads in the atoms block of a MDLMol connections table
     * 
     */
    private void readAtoms() {
        CMLAtom thisAtom;
        Double x, y, z;

        for (int i = 0; i < molAtomCount; i++) {
            String line = nextLine();
            // create atom object
            thisAtom = new CMLAtom("a" + (i + 1));
            molecule.addAtom(thisAtom);
            atomByNumber.put(new Integer(i + 1), thisAtom);

            x = new Double(line.substring(0, 10).trim()).doubleValue();
            y = new Double(line.substring(10, 20).trim()).doubleValue();
            z = new Double(line.substring(20, 30).trim()).doubleValue();

            // default to 3D and log a warning
            if (dimensionalCode == null || dimensionalCode.equals(MDLTag.DUNK)) {
                thisAtom.setX3(x);
                thisAtom.setY3(y);
                thisAtom.setZ3(z);
                LOG.warn("No 2D/3D code given");
            } else  if (dimensionalCode.equals(MDLTag.D2)) {
                thisAtom.setX2(x);
                thisAtom.setY2(y);
            } else if (dimensionalCode.equals(MDLTag.D3)) {
                thisAtom.setX3(x);
                thisAtom.setY3(y);
                thisAtom.setZ3(z);
            }

            // field 3 is atom parity (a *write-only* field, so not used)
            // int parity = parseInteger(line, 39, 42);

            // field 5 stereoCareBox
            // int field5 = parseInteger(line, 45, 48);

            // field 6 valency/oxidation state
            // int oxState = parseInteger(line, 48, 51);

            // element type
            String elType = line.substring(31, 34).trim();
            // replace * (MDL) by R (CML)
            if (elType.equals(CMLConstants.S_STAR)) {
            	elType = ChemicalElement.AS.R.value;
            }
          
            if (!elementExists(elType)) {
            	CMLLabel label = new CMLLabel();
            	label.setCMLValue(elType);
            	thisAtom.addLabel(label);
            	LOG.warn(elType + " is not a valid element atomicSymbol");
            	elType = ChemicalElement.AS.R.value;
            }
            thisAtom.setElementType(elType);

            // isotope
            int delta = parseInteger(line, 34, 36);
            if (delta != 0) {
                if (elementExists(elType)) {
                    ChemicalElement chemEl = ChemicalElement
                            .getChemicalElement(elType);
                    int isotope = chemEl.getMainIsotope() + delta;
                    thisAtom.setIsotope(isotope);
                } else {
                    throw new RuntimeException("cannot find isotopic weight of " + elType);
                }
            }

            // charge
            int ch = parseInteger(line, 36, 39);
            if (ch == 4) {
                // there are issues here, '4' doesn't have a consistent meaning
                // thisAtom.setSpinMultiplicity(2);
            } else if (ch > 0) {
                thisAtom.setFormalCharge(4 - ch);
            }

            // atom-atom mapping
            int atomMap = parseInteger(line, 61, 63);
            if (atomMap != 0) {
                CMLScalar scalar = new CMLScalar();
                scalar.setDictRef(NamespaceRefAttribute.createValue("mol", "atomMap"));
                scalar.setXMLContent("" + atomMap);
                thisAtom.addScalar(scalar);
            }
        }
    }

    /**
     * Reads in the bonds block of a MDLMol connections table
     * 
     */
    private void readBonds() {
        CMLBond thisBond;

        for (int i = 0; i < molBondCount; i++) {

            String line = nextLine();

            Integer atomNumber1 = new Integer(line.substring(0, 3).trim());
            CMLAtom atom1 = atomByNumber.get(atomNumber1);

            if (atom1 == null) {
                throw new RuntimeException("Cannot resolve atomNumber :"
                        + atomNumber1 + ": in " + line);
            }

            Integer atomNumber2 = new Integer(line.substring(3, 6).trim());
            CMLAtom atom2 = atomByNumber.get(atomNumber2);

            if (atom2 == null) {
                throw new RuntimeException("Cannot resolve atomNumber :"
                        + atomNumber2 + ": in " + line);
            }

            String order = cmlBondOrder(parseInteger(line, 6, 9));
            String stereo = cmlStereoBond(parseInteger(line, 9, 12));
            thisBond = new CMLBond("b" + (i + 1), atom1, atom2);
            molecule.addBond(thisBond);
            bondByNumber.put(i + 1, thisBond);

            // order
            thisBond.setOrder(order);

            // stereo
            if (!stereo.equals(CMLBond.NOSTEREO)) {
                CMLBondStereo bs = new CMLBondStereo();
                bs.setXMLContent(stereo);
                thisBond.addBondStereo(bs);
            }
        }
    }

    /**
     * Reads the footer of an MDLMolFile including the properties block and
     * SGroups TODO readFooter is a mess!
     * 
     * added "V" for atom annotation as it was emitted by ISIS although undocumented
     */
    private void readFooter() {
        // Clear map of SGroup ID's
        SGroup.clearMap();
        while (true) {

            String line = "";
            try {
                line = nextLine().trim();
            } catch (RuntimeException e) {
                line = MDLTag.M_END.tag;
                LOG.warn("No " + MDLTag.M_END.tag
                        + " in properties; unexpected EOF");
            }

            if (line.equals(MDLTag.M_END.tag)) {
                SGroup.tidySGroups();
                break;
            } else if (line.equals(MDLTag.S_SKP.tag)) {
                // S SKPnnn - skip next n lines
                int numberOfLines = parseInteger(line, 6, 9);
                for (int i = 0; i < numberOfLines; i++) {
                    nextLine();
                }
            } else if (line.startsWith(MDLTag.M_CHG.tag)
                    || line.startsWith(MDLTag.M_RAD.tag)
                    || line.startsWith(MDLTag.M_ISO.tag)) {
                readPropertyLine(line);
            } else if (line.startsWith(MDLTag.M_STY.tag)) {
                // Sgroup FormulaType, defines SGroup
                // M STYnn8 sss ttt ...

                line = line.substring(MDLTag.M_SAL.tag.length() + 1);
                int nSGroups = parseInteger(line, 0, 3);
                line = line.substring(3);
                for (int i = 0; i < nSGroups; i++) {
                    int SGroupNumber = parseInteger(line, 8 * i, 8 * i + 3);
                    String SGroupType = line.substring(8 * i + 4, 8 * i + 7);
                    new SGroup(SGroupNumber, SGroupType);
                }

            } else if (line.startsWith(MDLTag.M_SAL.tag)) {
                // SGROUP atom list
                // M SAL sssn15 aaa ...
                line = line.substring(MDLTag.M_SAL.tag.length() + 1);
                int sgroupId = parseInteger(line, 0, 3);
                SGroup sgroup = SGroup.getSGroup(sgroupId);
                if (sgroup == null) {
                    throw new RuntimeException("Cannot find SGROUP: " + sgroupId);
                }
                line = line.substring(3);
                int nAtoms = parseInteger(line, 0, 3);
                line = line.substring(3);
                for (int i = 0; i < nAtoms; i++) {
                    line = line.substring(1);
                    sgroup.addAtom(atomByNumber
                            .get(parseInteger(line, 0, 3) - 1));
                    line = line.substring(3);
                }
            } else if (line.startsWith(MDLTag.M_SBL.tag)) {
                // SGROUP bond list
                // M SBL sssn15 bbb ...
                line = line.substring(MDLTag.M_SBL.tag.length() + 1);
                int sgroupId = parseInteger(line, 0, 3);
                line = line.substring(3);
                SGroup sgroup = SGroup.getSGroup(sgroupId);
                if (sgroup == null) {
                    throw new RuntimeException("Cannot find SGROUP: " + sgroupId);
                }
                int nbonds = parseInteger(line, 0, 3);
                line = line.substring(3);
                for (int i = 0; i < nbonds; i++) {
                    line = line.substring(1);
                    sgroup.addBond(bondByNumber
                            .get(parseInteger(line, 0, 3) - 1));
                    line = line.substring(3);
                }
            } else if (line.startsWith(MDLTag.M_SBV.tag)) {
                // Superatom Bond and Vector Information (Display only)
            } else if (line.startsWith(MDLTag.M_SDS_EXP.tag)) {
                // Sgroup index of expanded superatoms (Display only)
                // M SDS EXPn15 sss ...
                line = line.substring(MDLTag.M_SDS_EXP.tag.length());
                int nsg = parseInteger(line, 0, 3);
                line = line.substring(3);
                for (int i = 0; i < nsg; i++) {
                    line = line.substring(1); // space
                    int sgroupId = parseInteger(line, 0, 3);
                    SGroup sgroup = SGroup.getSGroup(sgroupId);
                    if (sgroup == null) {
                        throw new RuntimeException("Cannot find SGROUP: "
                                + sgroupId);
                    }
                    sgroup.setExpanded(true);
                    line = line.substring(3);
                }
            } else if (line.startsWith(MDLTag.M_SLB.tag)) {
                // Unique Sgroup identifier (MACCS-II only); seems to be
                // redundant
            } else if (line.startsWith(MDLTag.M_SMT.tag)) {
                // Sgroup Subscript (label)
                // M SMT sss m...
                line = line.substring(MDLTag.M_SMT.tag.length() + 1);
                int sgroupId = parseInteger(line, 0, 3);
                SGroup sgroup = SGroup.getSGroup(sgroupId);
                if (sgroup == null) {
                    throw new RuntimeException("Cannot find SGROUP: " + sgroupId);
                }
                line = line.substring(3);
                sgroup.setLabel(line.trim());
            } else if (line.startsWith(MDLTag.A__.tag)) {
                int atomNum = parseInteger(line, 3, 6);
                CMLAtom atom = atomByNumber.get(atomNum);
                line = nextLine().trim();
                CMLLabel label = new CMLLabel();
                label.setCMLValue(line);
                atom.addLabel(label);
                // all Groups are "R"
                atom.setElementType("R");
                LOG.info("The element of atom number " + atomNum
                        + " has been set to R with an alias/label of '" + line
                        + "'");
            } else if (line.startsWith(MDLTag.G__.tag)) {
                // obsolete and superseded by SGROUPS
                nextLine(); // skip label line
            } else if (line.startsWith(MDLTag.V__.tag)) {
            	// undocumented atom annotation
            	/*
V    2 test
V    3 4
V    4 3
V    5 2
V    6 1
*/
            	// guess this means atomNum, annotation (or any length??)
            	int atomNum = parseInteger(line, 3, 6);
            	String annotation = line.substring(6);
                CMLAtom atom = atomByNumber.get(atomNum);
                if (atom == null) {
                	throw new RuntimeException("Cannot find atom in V: "+atomNum);
                }
                CMLLabel label = new CMLLabel();
                label.setDictRef("mdl:atomLabel");
                label.setCMLValue(annotation);
                atom.addLabel(label);
            } else if (line.trim().equals(S_EMPTY)) {
            	LOG.warn("WARNING: missing '" + MDLTag.M_END.tag
                        + "'; trying to recover");
                break;
            }
        }

        // iterate round sGroups

        for (Iterator<SGroup> theSGroupIterator = SGroup.getSGroupIterator(); theSGroupIterator
                .hasNext();) {
            SGroup nextSGroup = theSGroupIterator.next();
            CMLAtomSet atomSet = new CMLAtomSet();

            atomSet.setId("sgrp" + nextSGroup.id);
            atomSet.setTitle(nextSGroup.label);

            for (CMLAtom atom : nextSGroup.atomList) {
                atomSet.addAtom(atom);
            }

            molecule.appendChild(atomSet);

        }

    }
    
    /**
     * Reads in a single line of the MDLMol properties block (either
     * MDLTag.M_RAD, MDLTag.M_CHG, MDLTag.M_ISO)
     * 
     * @param line
     */
    private void readPropertyLine(String line) {
        String propertyType = line.substring(0, 6);

        int nFields = parseInteger(line, 7, 9);
        for (int i = 0; i < nFields; i++) {
            int startAt = 8 * i + 10;
            // read from MOL
            Integer atomNumber = new Integer(parseInteger(line, startAt,
                    startAt + 3));
            int value = parseInteger(line, startAt + 4, startAt + 7);
            // write to CML
            if (propertyType.equals(MDLTag.M_CHG.tag)) {
                atomByNumber.get(atomNumber).setFormalCharge(value);
            } else if (propertyType.equals(MDLTag.M_ISO.tag)) {
                atomByNumber.get(atomNumber).setIsotope(value);
            } else if (propertyType.equals(MDLTag.M_RAD.tag)) {
                atomByNumber.get(atomNumber).setSpinMultiplicity(value);
            }
        }
    }

    /**
     * Reads in the counts line in a V3000 MolFile extracting the number of
     * atoms and number of bonds in the connection table.
     */
    private void v3readCountsLine(String line) {
        // M V30 COUNTS 25 25 0 0 0
        Iterator<String> values = v3readValues(line);
        values.next();
        molAtomCount = parseInteger(values.next());
        molBondCount = parseInteger(values.next());
    }

    /**
     * Reads in the atoms block of a MDLMol V3000 connections table
     * 
     */
    private void v3readAtomBlock() {
        for (int i = 0; i < molAtomCount; i++) {
            String line = nextLine();
            if (line.equals("END ATOM")) {
                throw new RuntimeException("unexpected end of atom block");
            }

            Iterator<String> values = v3readValues(line);
            /*
             * M V30 index type x y z aamap - M V30 [CHG=val] [RAD=val]
             * [CFG=val] [MASS=val] - M V30 [VAL=val] - M V30 [HCOUNT=val]
             * [STBOX=val] [INVRET=val] [EXACHG=val] -
             */

            Integer atomNumber = new Integer(values.next());
            String atomType = values.next();
            Double x = new Double(values.next()).doubleValue();
            Double y = new Double(values.next()).doubleValue();
            Double z = new Double(values.next()).doubleValue();
            Integer atomMap = new Integer(values.next());

            String charge = v3readKeywordValue(MDLTag.V3_CHARGE.tag, line);
            String isotope = v3readKeywordValue(MDLTag.V3_ISOTOPE.tag, line);
            String radical = v3readKeywordValue(MDLTag.V3_RADICAL.tag, line);
            String hcount = v3readKeywordValue(MDLTag.V3_HCOUNT.tag, line);

            CMLAtom thisAtom = new CMLAtom("a" + atomNumber);
            molecule.addAtom(thisAtom);
            atomByNumber.put(atomNumber, thisAtom);

            if (dimensionalCode.equals(MDLTag.D2.tag) | !(Math.abs(z) > 0.0001)) {
                thisAtom.setX2(x);
                thisAtom.setY2(y);
            } else if (dimensionalCode.equals(MDLTag.D3.tag)
                    | Math.abs(x) > 0.0001 | Math.abs(y) > 0.0001
                    | Math.abs(z) > 0.0001) {
                thisAtom.setX3(x);
                thisAtom.setY3(y);
                thisAtom.setZ3(z);
            }

            if (!elementExists(atomType)) {
                throw new RuntimeException(atomType + " is not a valid element atomicSymbol");
            }
            thisAtom.setElementType(atomType);

            if (atomMap != 0) {
                CMLScalar scalar = new CMLScalar();
                scalar.setDictRef("mol:atomMap");
                scalar.setXMLContent("" + atomMap);
                thisAtom.addScalar(scalar);
            }

            if (charge != null) {
                thisAtom.setFormalCharge(parseInteger(charge));
            }

            if (isotope != null) {
                thisAtom.setIsotope(parseInteger(isotope));
            }

            if (radical != null) {
                thisAtom.setSpinMultiplicity(parseInteger(radical));
            }

            if (hcount != null) {
                thisAtom.setHydrogenCount(parseInteger(hcount));
            }
        }
    }

    /**
     * Reads in the atoms block of a MDLMol V3000 connections table
     * 
     */
    private void v3readBondBlock() {
        /*
         * M V30 index type atom1 atom2 [CFG=val] [TOPO=val] [RXCTR=val]
         * [STBOX=val]
         */
        for (int i = 0; i < molBondCount; i++) {
            String line = nextLine();
            if (line.equals("END BOND")) {
                throw new RuntimeException("unexpected end of atom block");
            }

            Iterator<String> values = v3readValues(line);

            Integer bondNumber = new Integer(values.next());
            Integer bondOrder = new Integer(values.next());
            Integer atomNumber1 = new Integer(values.next());
            Integer atomNumber2 = new Integer(values.next());

            String bondStereo = v3readKeywordValue(MDLTag.V3_STEREO.tag, line);

            CMLAtom atom1 = atomByNumber.get(atomNumber1);
            CMLAtom atom2 = atomByNumber.get(atomNumber2);

            if (atom1 == null || atom2 == null) {
                throw new RuntimeException("Bond " + bondNumber
                        + " refers to invalid atoms");
            }

            CMLBond thisBond = new CMLBond("b" + bondNumber, atom1, atom2);
            molecule.addBond(thisBond);

            bondByNumber.put(bondNumber, thisBond);

            thisBond.setOrder(cmlBondOrder(bondOrder));

            if (bondStereo != null) {
                String stereo = v3cmlStereoBond(parseInteger(bondStereo));
                CMLBondStereo bs = new CMLBondStereo();
                bs.setXMLContent(stereo);
                thisBond.addBondStereo(bs);
            }
        }
    }

    /**
     * Reads in the values seperated by spaces on a line in a V3000 MolFile also
     * obeys rules on using quotes and doubling quotes. Stops reading in values
     * once a [Keyword=value] parameter is met
     * 
     * @param line
     *            the line in the V3000 MolFile
     * @return an iterator over the values found (in order)
     */
    private Iterator<String> v3readValues(String line) {
        List<String> values = new ArrayList<String>();
        // line should have "M V30 " chopped off
        line = line.trim() + S_SPACE;

        while (line.indexOf(S_SPACE) != -1) {
            int endOfValue = 0;
            if (line.indexOf(S_SPACE) == -1) {
                break;
            }

            if (line.startsWith(S_QUOT)) {
                line = line.substring(1);
                endOfValue = line.indexOf(S_QUOT);

                while (line.charAt(endOfValue + 1) == C_QUOT) {
                    endOfValue = endOfValue + 2
                            + line.substring(endOfValue + 2).indexOf(S_QUOT);
                }
            } else {
                endOfValue = line.indexOf(S_SPACE);
            }

            String theValue = line.substring(0, endOfValue);

            if (theValue.indexOf(S_EQUALS) != -1) {
                break;
            } else if (!theValue.equals("") && !theValue.equals(S_SPACE)) {
                if (theValue.startsWith(S_LBRAK)) {
                    // first value in bracket is a count
                    theValue = theValue.substring(1);
                }
                if (theValue.endsWith(S_RBRAK)) {
                    theValue = theValue.substring(0, theValue.length() - 1);
                }
                values.add(theValue.replaceAll(S_QUOT+S_QUOT, S_QUOT));
            }

            line = line.substring(endOfValue + 1);
        }
        return values.iterator();
    }

    /**
     * Reads in an array of values belonging to a specific keyword on a line in
     * a V3000 MolFile. [keyword=(count val1 val2 val3)] also obeys rules on
     * quotes and double quotes
     * 
     * @param keyword
     *            the keyword to find the values from
     * @param line
     *            the V3000 MolFile line
     * @return an iterator over the values (doesnt include the count)
     */
    private Iterator<String> v3readKeywordArray(String keyword, String line) {
        if (line.indexOf(keyword) == -1) {
            return null;
        } else {
            int startOfKeyword = line.indexOf(keyword) + keyword.length() + 1;
            line = line.substring(startOfKeyword);
            return v3readValues(line);
        }
    }

    /**
     * Reads in a value belonging to a specific keyword on a line in a V3000
     * MolFile. [keyword=value] also obeys rules on quotes and double quotes
     * 
     * @param keyword
     *            the keyword to find the values from
     * @param line
     *            the V3000 MolFile line
     * @return the value as a string
     */
    private String v3readKeywordValue(String keyword, String line) {
        Iterator<String> it = v3readKeywordArray(keyword, line);
        if (it != null) {
            return v3readKeywordArray(keyword, line).next();
        } else {
            return null;
        }
    }

    /**
     * Reads in the next line of the file currently being parsed
     * 
     * @return String the next line
     */
    private String nextLine() {
    	if (nline >= lines.size()) {
    		return null;
    	}
        String nextLine = lines.get(nline++);
        if (nextLine == null) {
            throw new RuntimeException("MDLConverter: Unexpected EOF: Line number:"
                    + nline);
        } else {
            if (nextLine.startsWith(MDLTag.M_V30.tag)) {
                // V3000 line
                nextLine = nextLine.substring(MDLTag.M_V30.tag.length()).trim();
                if (nextLine.endsWith(S_MINUS)) {
                    nextLine += nextLine();
                }
            }
            return nextLine;
        }
    }

    /**
     * Outputs a given CMLMolecule as an MDL MolFile, the version of MolFile
     * written can be set using setVersion(), else the default verison, V2000,
     * is written.
     * 
     * @param lines
     * @param molecule the CMLMolecule to parse and output
     */
    public void writeMOL(List<String> lines, CMLMolecule molecule) {
    	this.molecule = molecule;
		nline = 0;
    	this.lines = lines;
        int atomCount = molecule.getAtomCount();

        if (atomCount > 999 && version == V3000) {
            throw new RuntimeException("Too many atoms for MDLMolfile: "
                    + atomCount);
        } else if (atomCount > 255) {
        	LOG.warn(atomCount
                    + " may be too many atoms for some applications");
        } else if (atomCount > 0) {
            if (version.equals(V2000)) {
                writeHeader();
                writeAtoms();
                writeBonds();
                writeFooter();
            } else if (version.equals(V3000)) {
                writeHeader();
                String s = "";
                s += (MDLTag.M_V30.tag + "BEGIN CTAB");
                lines.add(v3writeCountsLine()+MDLTag.M_V30.tag + "BEGIN ATOM");
                v3writeAtomBlock();
                lines.add(MDLTag.M_V30.tag + "END ATOM");
                lines.add(MDLTag.M_V30.tag + "BEGIN BOND");
                v3writeBondBlock();
                lines.add(MDLTag.M_V30.tag + "END BOND");
                lines.add(MDLTag.M_V30.tag + "END CTAB");

                lines.add(MDLTag.M_END.tag);

            } else {
                throw new RuntimeException("unknown MDLMol version!");
            }
        }
    }

    /**
     * writes the header of an MDLMolFile
     * 
     * @param writer
     */
    private void writeHeader() {

        Calendar rightNow = Calendar.getInstance();
        if (molecule.getTitleAttribute() != null) {
        	lines.add(molecule.getTitle());
        } else {
        	lines.add(S_EMPTY);
        }
        String s = "";
        s += "  "; // users initals
        s += "CML DOM ";
        // date and time
        s += ""
                + padLeftZero("" + (rightNow.get(Calendar.MONTH) + 1), 2) + ""
                + padLeftZero("" + rightNow.get(Calendar.DAY_OF_MONTH), 2) + ""
                + ("" + rightNow.get(Calendar.YEAR)).substring(2);
        s += ""
                + padLeftZero("" + rightNow.get(Calendar.HOUR_OF_DAY), 2) + ""
                + padLeftZero("" + rightNow.get(Calendar.MINUTE), 2);

        // TODO think about working out 2D or 3D tag
        CMLAtom atom = molecule.getAtom(0);
        if (atom.hasCoordinates(CoordinateType.TWOD) &&
        		(coordType.equals(CoordType.EITHER) || coordType.equals(CoordType.TWOD))) {
            dimensionalCode = MDLTag.D2;
        } else if (atom.hasCoordinates(CoordinateType.CARTESIAN) &&
    		(coordType.equals(CoordType.EITHER) || coordType.equals(CoordType.THREED))) {
            dimensionalCode = MDLTag.D3;
        }
        s += (dimensionalCode.tag);

        lines.add(s);

        // blank line (reserved for human-readable comments)
        lines.add(S_EMPTY);

        // counts line (V2000 only)
        if (version.equals(V2000)) {
        	s = "";
            // number of atoms & number of bonds
            s += outputMDLInt(molecule.getAtomCount());
            s += outputMDLInt(molecule.getBondCount());
            // these params are obsolete (apart from version tag)
            s += "  0  0  0  0  0  0  0  0999 V2000";
            lines.add(s);
        } else if (version.equals(V3000)) {
        	lines.add("  0  0  0     0  0            999 V3000");
        }
    }

    /**
     * Writes out the atom list in the MDLMol file format
     * 
     * @param writer the writer to write data to
     */
    private void writeAtoms() {

        int i = 0;
        double x = 0;
        double y = 0;
        double z = 0;

        for (CMLAtom atom : molecule.getAtoms()) {
            // any 2D coordinates take precedence
            if (atom.hasCoordinates(CoordinateType.TWOD) &&
            		(coordType.equals(CoordType.EITHER) || coordType.equals(CoordType.TWOD))) {
                x = atom.getX2();
                y = atom.getY2();
            } else if (atom.hasCoordinates(CoordinateType.CARTESIAN) &&
        		(coordType.equals(CoordType.EITHER) || coordType.equals(CoordType.THREED))) {
                x = atom.getX3();
                y = atom.getY3();
                z = atom.getZ3();
            }

            String s = "";
            s += (outputMDLFloat(x));
            s += (outputMDLFloat(y));
            s += (outputMDLFloat(z));
            // write single whitespace after coords
            s += (S_SPACE);

            String elType = atom.getElementType();
            if (!elementExists(elType)) {
            	LOG.warn(elType + " is not a valid element atomicSymbol");
            }
            s += ((elType + "   ").substring(0, 3));

            // MDLMol has 12 fields - only some are filled here
            // field 1 - integer difference from main isotope
            String isoString = " 0";
            double isotope = 0.0;
            if (atom.getIsotopeAttribute() != null) {
                isotope = atom.getIsotope();
            }

            if (isotope > 0.0001) {
                if (elementExists(elType)) {
                    ChemicalElement chemEl = ChemicalElement
                            .getChemicalElement(elType);
                    int mainIsotope = chemEl.getMainIsotope();
                    int delta = (mainIsotope > 0) ? (int) (isotope - mainIsotope)
                            : 0;
                    isoString = (delta >= 0) ? S_SPACE + delta : "" + delta;
                } else {
                    throw new RuntimeException("cannot find weight of " + elType
                            + " to work out isotopic difference");
                }
            }

            s += (isoString);

            // field 2 charge
            /*
0 = uncharged or value other than these, 1 = +3, 2 = +2, 3 = +1,
4 = doublet radical, 5 = -1, 6 = -2, 7 = -3
             */
            String chString = "  0";
            if (atom.getFormalChargeAttribute() != null) {
                int fCharge = atom.getFormalCharge();
                int mdlCharge = 0;
                if (fCharge == 0) {
//                	mdlCharge = 0;
                } else if (fCharge > 0 && fCharge < 5) {
                	mdlCharge = 4 - fCharge;
                } else if (fCharge > -4) {
                	mdlCharge = 4 - fCharge;
                }
            }

            /*
             * if (atom.getSpinMultiplicityAttribute() != null) { int spin =
             * atom.getSpinMultiplicity(); if (spin == 1) { // there are issues
             * here, '4' doesnt have a consistent meaning chString = " 4"; } }
             */

            s += (chString);

            // field 3 is atom parity
            String parity = "  0";
            s += (parity);

            // field 4 hydrogen count - appears to be nhyd+1
            String nhString = "  0";
            if (atom.getHydrogenCountAttribute() != null) {
                int nhyd = atom.getHydrogenCount();
                nhString = "  " + (nhyd + 1);
            }
            s += (nhString);

            // field 5 stereocare
            s += ("  0");

            // field 6 valency/oxidation state
            String vState = "  0";
            s += (vState);

            // fields 7 onwards have not been implemented
            for (int j = 6; j < 12; j++) {
                s += ("  0");
            }

            lines.add(s);

            // remember serial number
            numberByAtom.put(atom, ++i);
        }
    }

    /**
     * writes out the bonds list in MDLMolFile format
     * 
     * @param writer
     */
    private void writeBonds() {

        for (CMLBond bond : molecule.getBonds()) {

            Integer atomNumber1 = numberByAtom.get(bond.getAtom(0));
            Integer atomNumber2 = numberByAtom.get(bond.getAtom(1));

            String s = "";
            s += (outputMDLInt(atomNumber1));
            s += (outputMDLInt(atomNumber2));

            // field 3 order
            s += ("  " + molBondOrder(bond.getOrder()));

            // field 4 stereo
            CMLBondStereo bs = bond.getBondStereo();
            if (bs == null) {
                s += ("  0");
            } else {
                s += ("  " + molBondStereo(bs.getXMLContent()));
            }

            // field 5 - not used
            s += ("  0");

            // field 6 - bond topology (0 = Either, 1 = Ring, 2 = Chain)
            s += ("  0");

            // field 7 - reacting center status [Reaction & Query]
            s += ("  0");
            lines.add(s);
        }
    }

    /**
     * writes footer. writes charge, isotope and spin multiplicity information
     * 
     * @param writer
     */
    private void writeFooter() {

        String atomAlias = "";

        // run through atoms and collect alias information (first label if
        // present)
        for (CMLAtom atom : molecule.getAtoms()) {
            int atomNumber = numberByAtom.get(atom);

            CMLElements<CMLLabel> labelElements = atom.getLabelElements();
            for (CMLLabel alias : labelElements) {
            	lines.add(MDLTag.A__.tag + outputMDLInt(atomNumber));
            	lines.add(alias.getCMLValue());
            }
        }

        String s = "";
        s += (atomAlias);

        s += (writePropertyLine(MDLTag.M_CHG));
        s += (writePropertyLine(MDLTag.M_RAD));
        s += (writePropertyLine(MDLTag.M_ISO));

        // TODO write SGroups

        lines.add(s + MDLTag.M_END.tag);
    }

    /**
     * writes out a property line for inclusion in the properties block of an
     * MDLMolFile, can write out MDLTag.M_CHG, MDLTag.M_ISO and MDLTag.M_RAD
     * lines
     * 
     * obeys rules on a maximum of 8 values per line
     * 
     * @param propertyType -
     *            the type of property line to write out
     * @return the property line
     */
    private String writePropertyLine(MDLTag propertyType) {
        List<Integer> values = new ArrayList<Integer>();
        List<Integer> atomNumbers = new ArrayList<Integer>();

        for (CMLAtom atom : molecule.getAtoms()) {

            Integer atomNumber = numberByAtom.get(atom);

            int fCharge = 0;
            if (atom.getFormalChargeAttribute() != null) {
                fCharge = atom.getFormalCharge();
            }
            double isotope = 0.0;
            if (atom.getIsotopeAttribute() != null) {
                isotope = atom.getIsotope();
            }
            int spin = 0;
            if (atom.getSpinMultiplicityAttribute() != null) {
                spin = atom.getSpinMultiplicity();
            }

            if (propertyType == MDLTag.M_CHG & fCharge != 0) {
                values.add(atom.getFormalCharge());
                atomNumbers.add(atomNumber);
            } else if (propertyType == MDLTag.M_ISO & isotope > 0.0001) {
                values.add(((Double) atom.getIsotope()).intValue());
                atomNumbers.add(atomNumber);
            } else if (propertyType == MDLTag.M_RAD & spin > 0.0001) {
                values.add(atom.getSpinMultiplicity());
                atomNumbers.add(atomNumber);
            }
        }

        int count = atomNumbers.size();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < (float) count / 8f; i++) {
            int thisLineCount = (count - i * 8) > 8 ? 8 : count - i * 8;
            output.append(propertyType.tag + "  " + thisLineCount);
            for (int j = 0; j < thisLineCount; j++) {
                String atomNumber = outputMDLInt(atomNumbers.get(j + i * 8));
                String value = outputMDLInt(values.get(j + i * 8));
                output.append(S_SPACE + atomNumber + S_SPACE + value);
            }
        }
        String ss = Util.rightTrim(output.toString());
        if (ss.length() > 0) {
        	ss += CMLConstants.S_NEWLINE;
        }
        return ss;

    }

    /**
     * writes a counts line for inculsion in a V3000 MDLMolFile
     * 
     * @return the counts line
     */
    private String v3writeCountsLine() {
        String counts = MDLTag.M_V30.tag + "COUNTS";
        counts += S_SPACE + molecule.getAtomCount();
        counts += S_SPACE + molecule.getBondCount();
        counts += S_SPACE + 0; // number of Sgroups
        counts += S_SPACE + 0; // number of 3D constraints
        counts += S_SPACE + 0; // 1 if molecule is pure, 0 for mix (or just not
                            // chiral)
        return counts;
    }

    /**
     * writes out the atoms block in MDLMol V3000 format
     * 
     * @param theWriter
     */
    private void v3writeAtomBlock() {

        int i = 0;
        double x = 0;
        double y = 0;
        double z = 0;

        for (CMLAtom atom : molecule.getAtoms()) {

            String elType = atom.getElementType();

            if (!elementExists(elType)) {
                throw new RuntimeException(elType + " is not a valid element atomicSymbol");
            }

            // any 2D coordinates take precedence
            if (atom.getX2Attribute() != null && atom.getY2Attribute() != null) {
                x = atom.getX2();
                y = atom.getY2();
            } else if ( // else 3D coordinates
            atom.getX3Attribute() != null && atom.getY3Attribute() != null
                    && atom.getZ3Attribute() != null) {
                x = atom.getX3();
                y = atom.getY3();
                z = atom.getZ3();
            }

            String s = "";
            s += (MDLTag.M_V30.tag + (++i) + S_SPACE + elType + S_SPACE + x
                    + S_SPACE + y);

            // prevent 0.0 for 0 z
            if (z > 0.0001) {
                s += (S_SPACE + z);
            } else {
                s += (S_SPACE + 0);
            }

            // atom-atom mapping
            s += (S_SPACE + "0");

            if (atom.getIsotopeAttribute() != null) {
                Double isotope = atom.getIsotope();
                s += (S_SPACE + MDLTag.V3_ISOTOPE.tag + S_EQUALS
                        + isotope.intValue());
            }

            if (atom.getFormalChargeAttribute() != null) {
                s += (S_SPACE + MDLTag.V3_CHARGE.tag + S_EQUALS
                        + atom.getFormalCharge());
            }

            if (atom.getSpinMultiplicityAttribute() != null) {
                s += (S_SPACE + MDLTag.V3_RADICAL.tag + S_EQUALS
                        + atom.getSpinMultiplicity());
            }

            if (atom.getHydrogenCountAttribute() != null) {
                s += (S_SPACE + MDLTag.V3_HCOUNT.tag + S_EQUALS
                        + atom.getHydrogenCount());
            }

            lines.add(s);

            // remember serial number
            numberByAtom.put(atom, i);
        }
    }

    /**
     * writes out the bonds block in MDLMol V3000 format
     * 
     * @param theWriter
     */
    private void v3writeBondBlock() {
        int i = 0;
        for (CMLBond bond : molecule.getBonds()) {

            Integer atomNumber1 = numberByAtom.get(bond.getAtom(0));
            Integer atomNumber2 = numberByAtom.get(bond.getAtom(1));
            int bondOrder = molBondOrder(bond.getOrder());

            String s = "";
            s += (MDLTag.M_V30.tag + (++i));
            s += (S_SPACE + bondOrder);
            s += (S_SPACE + atomNumber1);
            s += (S_SPACE + atomNumber2);

            CMLBondStereo bs = bond.getBondStereo();
            if (bs != null) {
                s += (S_SPACE + MDLTag.V3_STEREO.tag + S_EQUALS
                        + v3molBondStereo(bs.getXMLContent()));
            }

            lines.add(s);
        }
    }

    
//	public static void usage() {
////		AbstractLegacyConverter.usage();
//		LOG.info("MDLConverter <options>");
//		LOG.info("   -V3000 (use V3000 format)");
//	}
	
//	/**

//    /**
//     * Provides a command line and graphical user interface to WMFConverter.
//     * @param args
//     */
//    public static void main(String[] args) {
//
//        if (args.length == 0) {
//            usage();
//            System.exit(0);
//        }
////        MDLConverter converter = new MDLConverter();
//    }

	public boolean isV3000() {
		return v3000;
	}

	public void setV3000(boolean v3000) {
		this.v3000 = v3000;
	}
	
	private static void usage() {
		System.out.println("usage: MDL2CMLConverter <file.mdl> <file.xml>");
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
		} else {
			MDL2CMLConverter converter = new MDL2CMLConverter();
			converter.convert(new File(args[0]), new File(args[1]));
		}
		
	}
}