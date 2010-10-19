package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.molutil.ChemicalElement;

public class GamessUSPunchBlock extends AbstractBlock {
	private static Logger LOG = Logger.getLogger(GamessUSPunchBlock.class);
	/*
	 * keywords in legacy input
	 */
	public static final String DATA = "DATA";
	public static final String GRAD = "GRAD";
	public static final String HESS = "HESS";
	public static final String VIB  = "VIB";
	public static final String ZMAT = "ZMAT";
	
	public GamessUSPunchBlock() {
		this.abstractCommon = new GamessUSCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		if (DATA.equals(getBlockName())) {
			makeData();
		} else if (GRAD.equals(getBlockName())) {
			makeGrad();
		} else if (HESS.equals(getBlockName())) {
			makeHess();
		} else if (VIB.equals(getBlockName())) {
			makeVib();
		} else if (ZMAT.equals(getBlockName())) {
			makeZmat();
		} else {
			System.err.println("Unknown blockname: "+getBlockName());
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			String entryId = getBlockName().toLowerCase();
			if (validateDictRef) {
				DictionaryTool dictionaryTool = abstractCommon.getDictionaryTool();
				if (!dictionaryTool.isIdInDictionary(entryId)) {
					LOG.warn("entryId "+entryId+" not found in dictionary: "+dictionaryTool);
				}
				String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), entryId);
				element.setAttribute("dictRef", dictRef);
			}
		} else {
			System.err.println("null element: "+getBlockName());
		}
	}

	/**
 $DATA  
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16                            
C1       0
H1          1.0     -2.9805271364       .9147039208       .1059830464
   CCD     0
        
	 */
	private void makeData() {
		int lineCount = 0;
		CMLMolecule molecule = new CMLMolecule();
		String title = lines.get(lineCount++);
		molecule.setTitle(title);
		// skip first atom line (don't understand)
		//C1       0
		lineCount++;
		
		int id = 1;
		for (; lineCount < lines.size(); lineCount += 3) {
			String line = lines.get(lineCount);
//			H1          1.0     -2.9805271364       .9147039208       .1059830464
			CMLAtom atom = new CMLAtom();
			CMLLabel label = new CMLLabel();
			label.setCMLValue(line.substring(0, 8));
			atom.addLabel(label);
			int atomicNumber = new Integer(line.substring(8, 13).trim()); 
			atom.setElementType(ChemicalElement.getElement(atomicNumber).getSymbol());
			atom.setX3(new Double(line.substring(15, 33).trim()));
			atom.setY3(new Double(line.substring(33, 51).trim()));
			atom.setZ3(new Double(line.substring(51, 69).trim()));
			atom.setId("a"+(id++));
			molecule.addAtom(atom);
		}
		element = molecule;
	}

	public static final Pattern ATOM_BOND_COUNT = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	public static final Pattern ATOM = Pattern.compile("\\s*([A-Z][a-z]?)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)");
	public static final Pattern BOND = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	
	private void makeGrad() {
		element = new CMLScalar();
	}

	private void makeHess() {
		element = new CMLScalar();
	}

	private void makeVib() {
		element = new CMLScalar();
	}

	private void makeZmat() {
		/*
        
		IZMAT =1 followed by two atom numbers. (I-J bond length)                        
		      =2 followed by three numbers. (I-J-K bond angle)                          
		      =3 followed by four numbers. (dihedral angle)                             
		         Torsion angle between planes I-J-K and J-K-L.                          
		      =4 followed by four atom numbers. (atom-plane)                            
		         Out-of-plane angle from bond I-J to plane J-K-L.                       
		      =5 followed by three numbers. (I-J-K linear bend)                         
		         Counts as 2 coordinates for the degenerate bend,                       
		         normally J is the center atom.  See $LIBE.                             
		      =6 followed by five atom numbers. (dihedral angle)                        
		         Dihedral angle between planes I-J-K and K-L-M.                         
		      =7 followed by six atom numbers. (ghost torsion)                          
		         Let A be the midpoint between atoms I and J, and                       
		         B be the midpoint between atoms M and N.  This                         
		         coordinate is the dihedral angle A-K-L-B.  The                         
		         atoms I,J and/or M,N may be the same atom number.                      
		         (If I=J AND M=N, this is a conventional torsion).                      
		         Examples: N2H4, or, with one common pair, H2POH.                       
			 */
		element = new CMLScalar();
	}

}
