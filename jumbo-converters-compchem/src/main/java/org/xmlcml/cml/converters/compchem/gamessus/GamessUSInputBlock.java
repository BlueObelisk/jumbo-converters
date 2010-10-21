package org.xmlcml.cml.converters.compchem.gamessus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;

public class GamessUSInputBlock extends AbstractBlock {

	/*
	 * keywords in legacy input
	 */
	public static final String BASIS  = "BASIS";
	public static final String CONTRL = "CONTRL";
	public static final String DATA   = "DATA";
	public static final String DFT    = "DFT";
	public static final String GUESS  = "GUESS";
	public static final String SCF    = "SCF";
	public static final String STATPT = "STATPT";
	public static final String SYSTEM = "SYSTEM";
	public static final String ZMAT   = "ZMAT";
	public static final String DELIM   = CMLConstants.S_PIPE;
	
	public GamessUSInputBlock(BlockContainer blockContainer) {
		super(blockContainer);
		this.abstractCommon = new GamessUSCommon();
	}

	/**
	 * the main method. Iterates over the blocks created by the input process
	 * 
	 */
	public void convertToRawCML() {
		if (DATA.equalsIgnoreCase(getBlockName())) {
			makeData();
		} else if (CONTRL.equalsIgnoreCase(getBlockName())) {
			makeContrl();
		} else if (SYSTEM.equalsIgnoreCase(getBlockName())) {
			makeSystem();
		} else if (DFT.equalsIgnoreCase(getBlockName())) {
			makeDft();
		} else if (BASIS.equalsIgnoreCase(getBlockName())) {
			makeBasis();
		} else if (SCF.equalsIgnoreCase(getBlockName())) {
			makeScf();
		} else if (GUESS.equalsIgnoreCase(getBlockName())) {
			makeGuess();
		} else if (STATPT.equalsIgnoreCase(getBlockName())) {
			makeStatpt();
		} else if (ZMAT.equalsIgnoreCase(getBlockName())) {
			makeZmat();
		} else {
			System.err.println("Unknown blockname: "+getBlockName());
		}
		/**
		 * valuable for blocks to have a dictRef
		 */
		if (element != null) {
			String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), getBlockName());
			element.setAttribute("dictRef", dictRef);
		} else {
			System.err.println("null element: "+getBlockName());
		}
	}

	private void makeStatpt() {
		element = makeModule();
	}

	private CMLModule makeModule() {
		CMLModule module = new CMLModule();
		for (String line : lines) {
			
			line = line.replaceAll("\\s*=\\s*", "=");
			line = line.replaceAll("\\,\\s*", "\\,");
			line = line.trim();
			if (line.length() > 0) { 
				String[] tokens = line.split("\\s+");
				for (String token : tokens) {
					addProperty(module, line, token);
				}
			}
		}
		return module;
	}

	private void addProperty(CMLModule module, String line, String token) {
		String[] nameValues = token.split("=");
		if (nameValues.length != 2) {
			throw new RuntimeException("bad nameValue: "+line);
		}
		CMLProperty property = new CMLProperty();
		String name = nameValues[0].toLowerCase();
		String dictRef = DictRefAttribute.createValue(abstractCommon.getPrefix(), name);
		property.setDictRef(dictRef);
		String value = nameValues[1];
		value = value.replaceAll("\\.T\\.|\\.t\\.|TRUE", "true");
		value = value.replaceAll("\\.F\\.|\\.f\\.|FALSE", "false");
		CMLScalar scalar = new CMLScalar(value);
		property.addScalar(scalar);
		module.appendChild(property);
	}

	private void makeGuess() {
		element = makeModule();
	}

	private void makeScf() {
		element = makeModule();
	}

	private void makeBasis() {
		element = makeModule();
	}

	private void makeDft() {
		element = makeModule();
	}

	private void makeSystem() {
		element = makeModule();
	}

	private void makeContrl() {
		element = makeModule();
	}
	
	private void makeZmat() {
		element = makeModule();
	}
	/**
 $DATA
HCO-L-Ala-NH2 - OPTIMIZE - B3LYP/cc-pVDZ - N_at = 16
C1
H1    
C2         1 rC2H1        
N3         2 rN3C2             1 aN3C2H1            
O4         2 rO4C2             3 aO4C2N3                 1 pO4C2N3H1                
C5         3 rC5N3             2 aC5N3C2                 1 dC5N3C2H1                
..
H16       13 rH16N13           7 aH16N13C7              15 pH16N13C7H15             

rC2H1         1.0889743331
rN3C2         1.3394412977
..
rH16N13       0.9941599400
aN3C2H1             110.9023260337
aO4C2N3             128.5418780807
..
aH16N13C7           119.3011272991
pO4C2N3H1                  179.1630605030
dC5N3C2H1                  178.0029069366
..
pH16N13C7H15              -146.5149511775
	 */
	private void makeData() {
		int lineCount = 0;
		CMLMolecule molecule = new CMLMolecule();
		// blank line should be skipped
		String title = lines.get(lineCount++);
		if (title.length() == 0) {
			title = lines.get(lineCount++);
		}
		molecule.setTitle(title);
		String pointGroupCharge = lines.get(lineCount++).trim();
		String[] tokens = pointGroupCharge.split("\\s+");
//		if (tokens.length != 2) {
//			throw new RuntimeException("Bad point group and charge: "+pointGroupCharge);
//		}
		String pointGroup = tokens[0];
		CMLSymmetry symmetry = new CMLSymmetry();
		symmetry.setPointGroup(pointGroup);
		molecule.appendChild(symmetry);

		if (tokens.length == 2) {
			int charge = Integer.parseInt(tokens[1]);
			molecule.setFormalCharge(charge);
		}
		int id = 1;
		List<String> stringList = new ArrayList<String>();
		for (; lineCount < lines.size(); lineCount++) {
			// TODO
		}
		element = molecule;
	}

	public static final Pattern ATOM_BOND_COUNT = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	public static final Pattern ATOM = Pattern.compile("\\s*([A-Z][a-z]?)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)\\s*([\\-\\+]?\\d*\\.?\\d+)");
	public static final Pattern BOND = Pattern.compile("\\s*(\\d+)\\s+(\\d+)");
	

}
