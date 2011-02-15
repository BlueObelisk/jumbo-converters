package org.xmlcml.cml.converters.compchem.gaussian.archive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractBlock;
import org.xmlcml.cml.converters.AbstractCommon;
import org.xmlcml.cml.converters.BlockContainer;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.Util;
import org.xmlcml.cml.converters.compchem.gaussian.GaussianCommon;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.element.CMLZMatrix;
import org.xmlcml.cml.tools.DictionaryTool;
import org.xmlcml.cml.tools.EntryTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.molutil.ChemicalElement;

/**
 * converts Gaussian archive to molecule, metadata and properties
 * 
 * @author Peter Murray-Rust
 * 
 */
public class GaussianArchiveOrigBlock extends AbstractBlock {

   private static Logger LOG = Logger.getLogger(GaussianArchiveOrigBlock.class);

   static {
      LOG.setLevel(Level.DEBUG);
   }
   // Note that Java uses backslash for escape. This affects the
   // string.split(regex)
   final static String ESCAPED_BACKSLASH = S_BACKSLASH + S_BACKSLASH;
   /** separates chunks*/
   final static String TOP_SEPARATOR =
           S_BACKSLASH + S_BACKSLASH;
   /** regex separating chunks*/
   final static String ESCAPED_TOP_SEPARATOR =
           ESCAPED_BACKSLASH + ESCAPED_BACKSLASH;
   /** separates some fields*/
   final static String LOW_SEPARATOR = S_BACKSLASH;
   /** regex separating chunks*/
   final static String ESCAPED_LOW_SEPARATOR = ESCAPED_BACKSLASH;
   final static String ATOM_SEPARATOR = S_COMMA;
   final static String KEYWORD_SEPARATOR = S_SPACE;
   final static String NAME_VALUE_SEPARATOR = S_EQUALS;
   final static String SYMBOL_SEPARATOR = S_EQUALS;
   /** dewisott */
   public final static String GAUSS_PREFIX = "gauss";
   /** dewisott */
   public final static String GAUSS_IOP = "IOP";
   /** dewisott */
   public final static String GAUSS_ONIOM = "ONIOM";
   /** dewisott */
   public final static String GAUSS_SACCI = "SAC-CI";
   /** dewisott */
   public final static String VALUE_SUFFIX = "value";
   private static String[] metadataNames = {
      //		"dummy1",
      //		"dummy2",
      "ginc",
      "calctype", // SP or FOpt
      "method",
      "basis",
      "rawformula",
      "user",
      "date",
      "zero",};
   private static int METHOD = 2;
   private static int BASIS = 3;
   /** parameterTypes
    *
    */
   public final static String[] PARAMETERTYPES = {
      "method",
      "basis",
      "basis1",
      "thermal"
   };
//   private ConverterLog converterLog;
   /** anything with this string is a keyword.
    */
   private List<NameValue> metadataList = new ArrayList<NameValue>();
   private List<String> keywordList = new ArrayList<String>();
   private List<int[]> alphaList = new ArrayList<int[]>();
   private List<int[]> betaList = new ArrayList<int[]>();
   private List<NameValue> nameValueList = new ArrayList<NameValue>();
   private String title;
   private List<String> atomStringList = new ArrayList<String>();
   private Map<String, String> symbolMap = new HashMap<String, String>();
   private CMLZMatrix zMatrix;
   private int molecularCharge = -1;
   private int spinMultiplicity = -1;
   private CMLDictionary dictionary;
   DictionaryTool dictionaryTool = null;
   private CMLCml cml;
   List<String> floatArrayList = new ArrayList<String>();
   private Map<String, CMLEntry> idIndex;
   private Command command;

   /** constructor.
    *
    * @param dictionary
    */
   public GaussianArchiveOrigBlock(BlockContainer blockContainer) {
		super(blockContainer);
   }

	@Override
	protected AbstractCommon getCommon() {
		return new GaussianCommon();
	}
	
   /**
    * @throws RuntimeException
    */
//   private void makeDictionary() throws RuntimeException {
//      if (dictionaryTool == null && dictionary != null) {
//         dictionaryTool = DictionaryTool.getOrCreateTool(dictionary);
//         dictionaryTool.setPrefix(GAUSS_PREFIX);
//         dictionaryTool.setDelimiter(S_COMMA);
//         dictionaryTool.setFailOnError(false);
//         dictionaryTool.setIgnoreCaseOfEnumerations(true);
//         idIndex = dictionaryTool.makeIndex("@id");
//
////         CMLEntry methodEntry = idIndex.get("method");
////         if (methodEntry == null) {
////            throw new RuntimeException("No method entry in dictionary");
////         }
////         EntryTool methodEntryTool = EntryTool.getOrCreateTool(methodEntry);
////         CMLEntry basisEntry = idIndex.get("basis");
////         if (basisEntry == null) {
////            throw new RuntimeException("No basis entry in dictionary");
////         }
////         EntryTool basisEntryTool = EntryTool.getOrCreateTool(basisEntry);
//      }
//   }

//	>1
//	>1
//	>GINC-GORDON
//	>SP
//	>RTD-B3LYP-FC
//	>6-311++G(d,p)
//	>C1H2O1
//	>VCD
//	>03-Dec-2002
//	>0
//	>
//	>#P B3LYP RPA=(NSTATES=4)/6-311++G(D,P) SCF=DIRECT DENSITY=CURRENT
//	>
//	>Gaussian98 Input Data formaldehyde
//	>
//	>0,1
//	>O,0,0.,0.,0.6744262648
//	>C,0,0.,0.,-0.5274890593
//	>H,0,0.,0.9393400211,-1.1152378772
//	>H,0,0.,-0.9393400211,-1.1152378772
//	>
//	>Version=SGI64-G98RevA.9
//	>State=1-A1
//	>HF=-114.5418488
//	>RMSD=3.698e-09
//	>PG=C02V [C2(C1O1),SGV(H2)]
//	>
//	>@
   String getTitle() {
      return this.title;
   }

   @Override
   public void convertToRawCML() {
	   System.err.println("convertToRawCML should be refactored");
   }
   
   CMLCml parseArchiveToCML(String archiveS) {
      cml = new CMLCml();
      String[] lines = archiveS.split(ESCAPED_TOP_SEPARATOR);
      if (lines.length < 6) {
         throw new RuntimeException(
                 "Expected 6 or more fields in archive; found: " +
                 lines.length + " ... " + archiveS.length());
      }
      // atoms may have symbolic values for coordinates
      // test whether there is a set of namevalue pairs for coords and then
      // the normal name - values
      boolean symbolicCoordinates =
              lines[4].indexOf(S_EQUALS) != -1 &&
              lines[5].indexOf(S_EQUALS) != -1;
      this.storeRawMetadata(lines[0]);
      this.storeRawKeywords(lines[1]);
      this.title = lines[2];
      if (symbolicCoordinates) {
         this.storeRawAtomAndSymbolData(lines[3], lines[4]);
      } else {
         this.storeRawAtomData(lines[3]);
      }
      int nv = (symbolicCoordinates) ? 5 : 4;
      // optional alpha orbitals of form 3,4\5,3\...
      if (lines[nv].indexOf(S_EQUALS) == -1) {
         this.storeIntegerArray(lines[nv], alphaList);
         nv++;
      }
      // optional beta orbitals of form 3,4\5,3\...
      if (lines[nv].indexOf(S_EQUALS) == -1) {
         this.storeIntegerArray(lines[nv], betaList);
         nv++;
      }
      this.storeRawNameValues(lines[nv]);
      // arrays
      for (int i = nv + 1; i < lines.length - 1; i++) {
         floatArrayList.add(lines[i]);
      }
      if (!lines[lines.length - 1].equals(S_ATSIGN) &&
              !lines[lines.length - 1].equals(S_BACKSLASH + S_ATSIGN)) {
         throw new RuntimeException("Expected @ at end of: " + this.title);
      }
      createCMLFromStoredData();
      return cml;
   }

   private void storeRawMetadata(String s) {
      String[] metadata = s.split(ESCAPED_LOW_SEPARATOR);
      if (metadata.length != 10) {
         LOG.debug("Expected 10 metadata fields: " + s);
      }
      for (int i = 2; i < metadata.length; i++) {
         // strip GINC-
         String name = (i == 2) ? metadata[i].substring(5) : metadata[i];
         metadataList.add(new NameValue(metadataNames[i - 2], name));
      }
   }

   private void storeRawKeywords(String s) {
      // remove #
      s = s.replaceAll(S_HASH, S_EMPTY);
      // normalize spaces
      s = s.replaceAll(S_SPACE + S_SPACE, S_SPACE);
      // sometimes whitespace creeps in
      s = s.replaceAll(S_SPACE + S_EQUALS, S_EQUALS);
      s = s.replaceAll(S_EQUALS + S_SPACE, S_EQUALS);
      String[] keys = s.split(KEYWORD_SEPARATOR);
      for (String key : keys) {
         if (key.equalsIgnoreCase("P")) {
         } else if (key.equalsIgnoreCase(S_EMPTY)) {
         } else if (key.toUpperCase().startsWith(GAUSS_IOP)) {
         } else if (key.toUpperCase().startsWith(GAUSS_SACCI)) {
         } else {
            keywordList.add(key);
         }
      }
   }

   private void storeRawAtomData(String s) {
      String[] atoms = s.split(ESCAPED_LOW_SEPARATOR);
      String[] keys = atoms[0].split(ATOM_SEPARATOR);
      if (keys.length != 2) {
         throw new RuntimeException("Expected 2 keys in atoms: " + atoms[0]);
      }
      molecularCharge = Integer.parseInt(keys[0].trim());
      spinMultiplicity = Integer.parseInt(keys[1].trim());
      for (int i = 1; i < atoms.length; i++) {
         atomStringList.add(atoms[i].trim());
      }
   }

   private void storeRawAtomAndSymbolData(String coordsS, String symbolsS) {
      // symbolsS is of form r1=1.2/r2=3.4...
      String[] symbols = symbolsS.split(ESCAPED_LOW_SEPARATOR);
      for (String symbol : symbols) {
         int idx = symbol.indexOf(SYMBOL_SEPARATOR);
         if (idx == -1) {
            throw new RuntimeException("Cannot interpret symbol assignation: " + symbol);
         }
         String name = symbol.substring(0, idx).trim();
         String value = symbol.substring(idx + 1).trim();
         // may be followed by scan values
         int idx0 = value.indexOf(S_COMMA);
         if (idx0 != -1) {
            value = value.substring(0, idx);
         }
         symbolMap.put(name, value);
      }
      // atoms as before, except for symbols
      String[] atoms = coordsS.split(ESCAPED_LOW_SEPARATOR);
      // these are unknown function...
      String[] keys = atoms[0].split(ATOM_SEPARATOR);
      if (keys.length != 2) {
         throw new RuntimeException("Expected 2 keys in atoms: " + atoms[0]);
      }
      molecularCharge = Integer.parseInt(keys[0]);
      spinMultiplicity = Integer.parseInt(keys[1]);
      for (int i = 1; i < atoms.length; i++) {
         atomStringList.add(atoms[i].trim());
      }
   }

   // alpha/beta orbitals
   private void storeIntegerArray(String s, List<int[]> integerArrayList) {
      String[] chunks = s.trim().split(ESCAPED_LOW_SEPARATOR);
      for (String chunk : chunks) {
         chunk = chunk.trim();
         if (chunk.length() == 0) {
            continue;
         }
         String[] ssss = chunk.split(S_COMMA);
         try {
            IntArray ii = new IntArray(ssss);
            integerArrayList.add(ii.getArray());
         } catch (Exception e) {
            throw new RuntimeException("Cannot parse integer array: " + chunk);
         }
      }
   }

   private void storeRawNameValues(String s) {
      String[] nameValues = s.split(ESCAPED_LOW_SEPARATOR);
      for (String nameValue : nameValues) {
         nameValue = nameValue.trim();
         if (nameValue.length() != 0) {
            String[] nv = nameValue.split(NAME_VALUE_SEPARATOR);
            if (nv.length != 2) {
               throw new RuntimeException("Expected name=value: " + nameValue);
            }
            nameValueList.add(new NameValue(nv[0], nv[1]));
         }
      }
   }

   private CMLMolecule makeMolecule() {

      CMLMolecule molecule = new CMLMolecule();
      molecule.setFormalCharge(molecularCharge);
      molecule.setSpinMultiplicity(spinMultiplicity);
      List<String> idList = new ArrayList<String>();
      zMatrix = null;
      if (atomStringList.size() == 0) {
         throw new RuntimeException("No atoms in log file in: " + title);
      }
      int natoms = 0;
      for (String line : atomStringList) {
         // must be z-matrix or coordinates
         String[] fields = line.split(ATOM_SEPARATOR);
         if (fields.length == 0) {
            throw new RuntimeException("must have >= 1 field for atom in: " + title);
         }
         String symbol = fields[0];
         CMLAtom atom = new CMLAtom();
         // replace X by Dummy
         if (symbol.equals("X") || symbol.equals("TV")) {
            symbol = "Dummy";
            atom.setElementType(symbol);
         }
         idList.add(Util.createAtomId(natoms + 1));
         atom.setId(idList.get(natoms));
         molecule.addAtom(atom);
         ChemicalElement chemicalElement = ChemicalElement.getChemicalElement(symbol);
         if (chemicalElement == null) {
            throw new RuntimeException("Cannot interpret as chemical element: " + symbol + " (" + natoms + S_SLASH + line + ") in: " + title);
         }
         natoms++;
         // fields seem to be either:
         // el, x, y, z	// cartesian
         // OR
         // el, unknownInt, x, y, z // alternative cartesian
         // OR zMatrix
         // el												// first atom
         // el serial, length								// second atom
         // el serial, length, serial angle					// third atom
         // el serial, length, serial angle, serial torsion	// fourth and later
         atom.setElementType(chemicalElement.getSymbol());
         if (natoms == 1) {
            if (fields.length == 1) {
               zMatrix = new CMLZMatrix();
               molecule.appendChild(zMatrix);
               Point3 xyz = new Point3(0, 0, 0);
               atom.setXYZ3(xyz);
            } else {
               addCartesians(atom, fields, natoms, line);
            }
         } else if (natoms == 2) {
            if (zMatrix != null) {
               if (fields.length != 3) {
                  throw new RuntimeException("Bad z matrix line for length : " + line + " in: " + title);
               }
               addLength(zMatrix, fields, natoms);
            } else {
               addCartesians(atom, fields, natoms, line);
            }
         } else if (natoms == 3) {
            if (zMatrix != null) {
               if (fields.length != 5) {
                  throw new RuntimeException("Bad z matrix line for angle: " + line + " in " + title);
               }
               addLength(zMatrix, fields, natoms);
               addAngle(zMatrix, fields, natoms);
            } else {
               addCartesians(atom, fields, natoms, line);
            }
         } else {
            // some torsions have a trailing integer (normally 0)
            // representing format (for ONIOM)
            if (zMatrix != null) {
               if (fields.length < 7 || fields.length > 8) {
                  throw new RuntimeException("Bad z matrix line: " + line + " in " + title);
               }

               addLength(zMatrix, fields, natoms);
               addAngle(zMatrix, fields, natoms);
               addTorsion(zMatrix, fields, natoms);
            } else {
               addCartesians(atom, fields, natoms, line);
            }
         }
      }
      if (zMatrix != null) {
         try {
            zMatrix.addCartesiansTo(molecule);
         } catch (RuntimeException e) {
            LOG.debug("Bad ZMAT");
            molecule.debug("MOL");
            throw e;
         }
      }
      // remove any dummies
      List<Node> dummyNodes = CMLUtil.getQueryNodes(molecule, ".//" +
              CMLAtom.NS + "[@elementType='Dummy']", CML_XPATH);
      for (Node dummyNode : dummyNodes) {
         dummyNode.detach();
      }
      calculateFormulaAndBonds(molecule);
      return molecule;
   }

   private void calculateFormulaAndBonds(CMLMolecule molecule) {
      MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
      moleculeTool.calculateBondedAtoms();
      moleculeTool.adjustBondOrdersToValency();
      CMLFormula formula = moleculeTool.getCalculatedFormula(
              CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
      formula.setDictRef("cml:calculatedFormula");
      molecule.addFormula(formula);
      Nodes formulaElements = molecule.query("./cml:formula", CML_XPATH);
      // Jmol doesn't like child atomArray
      for (int i = 0; i < formulaElements.size(); i++) {
         CMLFormula formulax = (CMLFormula) formulaElements.get(i);
         formulax.detachAllAtomArraysAsTheyAreAMenace();
      }
   }

   private void createCMLFromStoredData() {
//		cml = new CMLCml();
      // add molecule
      CMLMolecule molecule = makeMolecule();
      cml.appendChild(molecule);
      // add metadata
      try {
         addMetadata();
      } catch (RuntimeException e) {
         LOG.warn("Cannot add metadata: " + e.getMessage());
      }
      try {
         addAuthorFormula(molecule);
      } catch (RuntimeException e) {
    	  LOG.warn("Cannot add author and formula " + e.getMessage());
      }
      try {
         addKeywords();
      } catch (RuntimeException e) {
    	  LOG.warn("Cannot add keywords: " + e.getMessage());
      }

      molecule.setTitle(title);

      try {
         addAtomRelatedArrayAndMatrix();
      } catch (RuntimeException e) {
    	  LOG.warn("Cannot add array and matrix: " + e.getMessage());
      }
      try {
         addAlphaBetaValues();
      } catch (RuntimeException e) {
    	  LOG.warn("Cannot add alpha and beta: " + e.getMessage());
      }

      try {
         addNameValues();
      } catch (RuntimeException e) {
    	  LOG.warn("Cannot parse nameValues: " + e.getMessage());
      }
      normalizeParameters();
      normalizeProperties();

   }

   private void normalizeParameters() {
      Nodes parameterLists = cml.query("cml:parameterList", CML_XPATH);
      CMLParameterList parameterList = null;
      if (parameterLists.size() == 0) {
         parameterList = new CMLParameterList();
         cml.appendChild(parameterList);
      } else {
         parameterList = (CMLParameterList) parameterLists.get(0);
      }
      Nodes parameters = cml.query("cml:parameter", CML_XPATH);
      for (int i = 0; i < parameters.size(); i++) {
         CMLParameter parameter = (CMLParameter) parameters.get(i);
         parameter.detach();
         parameterList.appendChild(parameter);
      }
   }

   private void normalizeProperties() {
      Nodes propertyLists = cml.query("cml:propertyList", CML_XPATH);
      CMLPropertyList propertyList = null;
      if (propertyLists.size() == 0) {
         propertyList = new CMLPropertyList();
         cml.appendChild(propertyList);
      } else {
         propertyList = (CMLPropertyList) propertyLists.get(0);
      }
      Nodes propertys = cml.query("cml:property", CML_XPATH);
      for (int i = 0; i < propertys.size(); i++) {
         CMLProperty property = (CMLProperty) propertys.get(i);
         property.detach();
         propertyList.appendChild(property);
      }
   }

   void addMetadata() {
      for (int i = 0; i < metadataList.size(); i++) {
         NameValue nv = metadataList.get(i);
         String name = metadataNames[i].trim();
         String value = nv.value.trim();
         if (name.equals("rawformula")) {
            CMLFormula formula = new CMLFormula();
            formula.setInline(value);
            formula.setConvention(GAUSS_PREFIX + S_COLON + "archive");
            cml.appendChild(formula);
         } else {
            if (i == METHOD && value.startsWith(GAUSS_ONIOM)) {
               if (!metadataList.get(BASIS).value.trim().equals(S_EMPTY)) {
                  throw new RuntimeException("ONIOM should have no basis");
               }
               processONIOMMetadata(value);
            }
            if (dictionaryTool != null) {
               CMLElement element = dictionaryTool.createTypedNameValue(name, value);
               createAndAppendParameter(element);
            }
         }
      }
   }

   /**
    * @param element
    */
   private void createAndAppendParameter(CMLElement element) {
      CMLParameter parameter = new CMLParameter();
      parameter.appendChild(element);
      Attribute dictRef = element.getAttribute("dictRef");
      dictRef.detach();
      parameter.addAttribute(dictRef);
      cml.appendChild(parameter);
   }

   private void processONIOMMetadata(String value) {
      if (!value.endsWith(S_RBRAK)) {
         throw new RuntimeException("ONIOM must end with )");
      }
      value.replaceAll(S_SPACE, S_EMPTY);
      value = value.substring(GAUSS_ONIOM.length() + 1, value.length() - 1);
      String[] ss = value.split(S_COLON);
      CMLParameterList parameterList = new CMLParameterList();
      parameterList.setDictRef(GAUSS_PREFIX + S_COLON + GAUSS_ONIOM.toLowerCase());
      cml.appendChild(parameterList);
      for (String s : ss) {
         CMLParameterList subList = getParameterListForMethodAndBasis(s);
         parameterList.addParameterList(subList);
      }
   }

   void addKeywords() {
      for (String keyword : keywordList) {
         addKeyword1(keyword);
      }
   }

   void addKeyword1(String keyword) {
      if (keyword == null) {
      } else if (keyword.toUpperCase().startsWith(GAUSS_ONIOM)) {
         processONIOM(keyword);
      } else {
         //the syntax here is highly irregular and this is a kludge
         int equalsx = keyword.indexOf(S_EQUALS);
         int lbrakx = keyword.indexOf(S_LBRAK);
         int slashx = keyword.indexOf(S_SLASH);
         if (isFirst(slashx, equalsx, lbrakx)) {
            processSlash(keyword);
         } else if (isFirst(equalsx, lbrakx, slashx)) {
            processEquals(keyword);
         } else if (isFirst(lbrakx, equalsx, slashx)) {
            processBrackets(null, keyword);
         } else {
            addKeyword(keyword);
         }
      }
   }

   private boolean isFirst(int ax, int bx, int cx) {
      return (ax != -1 &&
              (bx == -1 || ax < bx) &&
              (cx == -1 || ax < cx));
   }

   void processONIOM(String keyword) {
      keyword = keyword.substring(GAUSS_ONIOM.length());
      // store the GAUSS_ONIOM keyword
      addKeyword(GAUSS_ONIOM);
      // could be GAUSS_ONIOM=
      if (keyword.startsWith(S_EQUALS)) {
         keyword = keyword.substring(1);
      }
      // could be GAUSS_ONIOM=(...) or GAUSS_ONIOM(...)
      if (keyword.startsWith(S_LBRAK) && keyword.endsWith(S_RBRAK)) {
         keyword = keyword.substring(1, keyword.length() - 1).trim();
         // layers are separated by colons
         String[] ss = keyword.split(S_COLON);
         for (String s : ss) {
            addKeyword(s);
         }
      } else {
         addKeyword(keyword);
      }
   }

   void processSlash(String key) {
      CMLParameterList parameterList = getParameterListForMethodAndBasis(key);
      if (parameterList != null) {
         cml.appendChild(parameterList);
      }
   }

   // standalone keyword
   private void addKeyword(String key) {
      // this may still contain slashes
      if (key.indexOf(S_SLASH) != -1) {
         CMLParameterList parameterList = getParameterListForMethodAndBasis(key);
         if (parameterList != null) {
            cml.appendChild(parameterList);
         }
      } else {
         if (addedMethod(key)) {
         } else if (addedBasis(key)) {
         } else {
            finallyAddKeyword(key);
         }
      }
   }

   private boolean addedMethod(String key) {
      boolean added = false;
//		if (getMethodEntry().getIndexableById(key) != null) {
////			LOG.debug("++++++++ADDED as METHOD:"+key );
//			if (dictionaryTool != null) {
//				CMLElement element = dictionaryTool.createTypedNameValue("method", key);
//				cml.appendChild(element);
//				added = true;
//			}
//		}
      return added;
   }

   private boolean addedBasis(String key) {
      boolean added = false;
//		if (getMethodEntry().getIndexableById(key) != null) {
////			LOG.debug("++++++++ADDED as BASIS:"+key );
//			if (dictionaryTool != null) {
//				CMLElement element = dictionaryTool.createTypedNameValue("basis", key);
//				cml.appendChild(element);
//				added = true;
//			}
//		}
      return added;
   }

   private void finallyAddKeyword(String key) {
      int commax = key.indexOf(S_COMMA);
      int slashx = key.indexOf(S_SLASH);
      if (slashx != -1) {
         processSlash(key);
      } else if (commax == -1) {
         try {
            if (dictionaryTool != null) {
               CMLElement element = dictionaryTool.createTypedNameValue("keyword", key);
               if (element != null) {
                  createAndAppendParameter(element);
               }
            }
         } catch (RuntimeException e) {
            command.warn("Skipped adding unknown keyword: " + e);
         }
      } else {
         String[] commaKeys = key.trim().split(S_COMMA);
         for (String comma : commaKeys) {
            finallyAddKeyword(comma.trim());
         }
      }
   }

   private CMLParameterList getParameterListForMethodAndBasis(String key) {
      CMLParameterList parameterList = null;
      String[] ss = key.split(S_SLASH);
      if (ss.length < 2) {
         throw new RuntimeException("Expected SLASH in method+basis");
      } else if (ss.length > 3) {
         LOG.debug("***************** cannot interpret more than 3 components of method+basis: " + key);
      } else {
         parameterList = getParameterList(ss, key);
      }
      return parameterList;
   }

   private CMLParameterList getParameterList(String[] ss, String key) {
      CMLParameterList parameterList = new CMLParameterList();
      int i = 0;
      for (String param : ss) {
         param = param.toLowerCase();
         String component = PARAMETERTYPES[i];
         if (dictionaryTool != null) {
            CMLEntry entry = dictionary.getCMLEntry(component);
            if (entry == null) {
               if (dictionaryTool.isFailOnError()) {
                  throw new RuntimeException("Cannot find entry for " + component + " in " + key);
               }
            } else {
               EntryTool entryTool = dictionaryTool.createEntryTool(entry);
               CMLParameter parameter = entryTool.createParameter(component, param);
               parameterList.addParameter(parameter);
            }
         }
         i++;
      }
      return parameterList;
   }

   private void processEquals(String key) {
      int idx = key.indexOf(S_EQUALS);
      if (idx == -1) {
         throw new RuntimeException("Cannot find '=' in field [" + key + "]");
      }
      String name = key.substring(0, idx);
      if (name == null || name.trim().length() == 0) {
         System.err.println(" null precedent of '=' (maybe spaces): " + key);
      } else {
         String rest = key.substring(idx + 1);
         idx = rest.indexOf(S_LBRAK);
         if (idx == -1 && dictionaryTool != null) {
            CMLElement element = dictionaryTool.createTypedNameValue(name, rest);
            createAndAppendParameter(element);
         } else {
            processBrackets(name, rest);
         }
      }
   }

   // might be a method or basis
   private void processBrackets(String name, String key) {
      key = key.trim();
      int idx = key.indexOf(S_LBRAK);
      if (idx == -1) {
         throw new RuntimeException("Cannot find bracket");
      }
      if (addedMethod(key)) {
      } else if (addedBasis(key)) {
      } else if (!key.endsWith(S_RBRAK)) {
         // this normally means the string is too complex to parse
//			throw new RuntimeException("String does not end with ) :"+key);
      } else {
         // if starts with bracket, take name from calling environment
         if (idx > 0) {
            name = key.substring(0, idx);
         }
         String rest = key.substring(idx + 1, key.length() - 1);
         String[] rests = rest.split(S_COMMA);
         for (String r : rests) {
            // EQUAL nested in bracketed list
            if (r.indexOf(S_EQUALS) != -1) {
               processEquals(r);
            } else {
               if (dictionaryTool != null) {
                  CMLElement element = dictionaryTool.createTypedNameValue(name, r);
                  createAndAppendParameter(element);
               }
            }
         }
      }
   }

   private void addAtomRelatedArrayAndMatrix() {
      int count = 0;
      for (String floatArray : floatArrayList) {
         if (floatArray.trim().length() == 0) {
            continue;
         }
         String[] ss = floatArray.trim().split(S_COMMA);
         RealArray ra = null;
         try {
            ra = new RealArray(ss);
         } catch (EuclidRuntimeException e) {
            LOG.debug("Cannot parse float array: " + floatArray);
         }
         CMLArray array = new CMLArray(ra.getArray());
         array.setDictRef(GAUSS_PREFIX + S_COLON + "float" + (++count));
         cml.appendChild(array);
      }
      if (count > 0) {
         List<Node> arrayList = CMLUtil.getQueryNodes(cml, CMLArray.NS, CML_XPATH);
         List<Node> atomList = CMLUtil.getQueryNodes(cml, ".//" + CMLAtom.NS, CML_XPATH);
         if (arrayList.size() >= 1) {
            CMLArray array0 = (CMLArray) arrayList.get(0);
            int at = atomList.size();
            int a0 = array0.getSize();
            if (a0 == (3 * at * (3 * at + 1)) / 2) {
               //
            } else {
               for (String floatArray : floatArrayList) {
                  LOG.debug("FLOATARRAY:" + floatArray);
               }
               throw new RuntimeException("BAD size atom-atom array: atoms: " + at + "/ array " + a0);
            }
         }
         if (arrayList.size() >= 2) {
            CMLArray array1 = (CMLArray) arrayList.get(1);
            int at = atomList.size();
            int a1 = array1.getSize();
            if (a1 != at * 3) {
               for (String floatArray : floatArrayList) {
                  LOG.debug("FLOATARRAY:" + floatArray);
               }
               throw new RuntimeException("BAD size for 3*at array: " + at + S_SLASH + a1);
            }
         }
      }
   }

   void addAlphaBetaValues() {
      if (dictionaryTool != null) {
         for (int[] ii : alphaList) {
            CMLElement child = dictionaryTool.createTypedNameValue(
                    "alpha", "" + ii[0] + "," + ii[1]);
            cml.appendChild(child);
         }
         for (int[] ii : betaList) {
            CMLElement child = dictionaryTool.createTypedNameValue(
                    "beta", "" + ii[0] + "," + ii[1]);
            cml.appendChild(child);
         }
      }
   }

   /**
    * these are the "final" record
    */
   void addNameValues() {
      if (dictionaryTool != null) {
         for (NameValue nv : nameValueList) {
            String name = nv.name.toLowerCase();
            String nameV = name + VALUE_SUFFIX;
            CMLEntry entry = idIndex.get(nameV);
            try {
               if (entry != null) {
                  // known names
                  EntryTool entryTool = EntryTool.getOrCreateTool(entry);
                  CMLProperty property = null;
                  if (name.equals("pg")) {
                     property = processPointGroup(nv);
                  } else if (name.equals("dipole")) {
                     property = createVectorProperty(nv, entryTool);
                  } else {
                     property = createProperty(nv, entryTool);
                  }
                  if (property != null) {
                     property.setDictRef(GAUSS_PREFIX + S_COLON + nameV);
                     cml.appendChild(property);
                  }
               } else {
                  //					// unknown names
                  if (command != null) {
                     command.info(">>> Cannot find entry for " + name + " [" + nameV + "]");
                  }
                  CMLElement element = EntryTool.createUnknownStringScalar(
                          nameV, nv.value);
                  CMLProperty property = new CMLProperty();
                  property.setDictRef(GAUSS_PREFIX + S_COLON + nameV);
                  property.appendChild(element);
                  cml.appendChild(property);
               }
            } catch (Exception e) {
               e.printStackTrace();
               throw new RuntimeException("error " + e);
            }
         }
      }
   }

   /**
    * @param elementName
    * @param idx
    * @return
    * @throws RuntimeException
    */
   @SuppressWarnings("unused")
   private void createAndAddParameter(EntryTool entryTool, String enumeratedS) {
      if (!entryTool.containsEnumeratedValue(enumeratedS)) {
         command.info(" Cannot find method (" + enumeratedS + ") in property: " + entryTool.getEntry().getId());
      }
      CMLParameter parameter = new CMLParameter();
      parameter.setDictRef(GAUSS_PREFIX + S_COLON + enumeratedS);
      cml.appendChild(parameter);
   }

   @SuppressWarnings("unused")
   private CMLProperty createMatrixProperty(NameValue nv, EntryTool entryTool) {
      entryTool.setDelimiter(S_COMMA);
      CMLMatrix matrix = entryTool.createMatrix(nv.name, nv.value);
      CMLProperty property = new CMLProperty();
      property.setDictRef(GAUSS_PREFIX + S_COLON + nv.name);
      property.appendChild(matrix);
      return property;
   }

   /**
    * @param nv
    * @param entryTool
    */
   private CMLProperty createVectorProperty(NameValue nv, EntryTool entryTool) {
      entryTool.setDelimiter(S_COMMA);
      CMLVector3 vector3 = entryTool.createVector3(nv.name, nv.value);
      CMLProperty property = new CMLProperty();
      property.setDictRef(GAUSS_PREFIX + S_COLON + nv.name);
      property.appendChild(vector3);
      return property;
   }

   /**
    * @param nv
    * @param entryTool
    */
   private CMLProperty createProperty(NameValue nv, EntryTool entryTool) {
      CMLProperty property = new CMLProperty();
      CMLElement element = entryTool.createElement(nv.name, nv.value);
      if (element == null) {
         element = new CMLScalar(nv.value);
      }
      property.setDictRef(GAUSS_PREFIX + S_COLON + nv.name);
      property.appendChild(element);
      return property;
   }

   /**
    * @param nv
    * @throws RuntimeException
    */
   private CMLProperty processPointGroup(NameValue nv) throws RuntimeException {
      CMLProperty property = new CMLProperty();
      //		    	 point group			C01 [X(C5H11N1O1)]
      String pgv = nv.value;
      CMLElement element = null;
      int idx = nv.value.indexOf(S_SPACE + S_LSQUARE);
      if (idx != -1) {
         if (!nv.value.endsWith(S_RSQUARE)) {
            throw new RuntimeException("bad pointGroup field: " + nv.value);
         }
         // split into two fields
         pgv = nv.value.substring(0, idx);
         element = dictionaryTool.createTypedNameValue(
                 "pgframe" + VALUE_SUFFIX, nv.value.substring(idx + 2, nv.value.length() - 1));
         property.appendChild(element);
      }
      CMLSymmetry symmetry = new CMLSymmetry();
      symmetry.setDictRef(GAUSS_PREFIX + S_COLON + "pointGroup");
      symmetry.setPointGroup(pgv);
      property.appendChild(symmetry);
      property.setDictRef(GAUSS_PREFIX + S_COLON + nv.name);
      return property;
   }

   private void addAuthorFormula(CMLMolecule molecule) {
      List<Node> formulas = CMLUtil.getQueryNodes(
              cml, CMLScalar.NS + "[@dictRef='" + GAUSS_PREFIX + S_COLON + "rawformula']",
              CML_XPATH);
      if (formulas.size() != 1) {
         throw new RuntimeException("No formula found in metadata");
      }
      String formulaS = ((CMLScalar) formulas.get(0)).getXMLContent();
      int idx = formulaS.indexOf(S_LBRAK);
      int charge = 0;
      int spin = 1;
      if (idx != -1) {
         String chargeS = formulaS.substring(idx + 1, formulaS.length() - 1);
         int jdx = chargeS.indexOf(S_COMMA);
         // have to work out what this is
         if (jdx == -1) {
            int ch = getCharge(chargeS);
            if (ch == Integer.MAX_VALUE) {
               spin = Integer.parseInt(chargeS);
            } else {
               charge = ch;
            }
         } else {
            charge = getCharge(chargeS.substring(0, jdx));
            spin = Integer.parseInt(chargeS.substring(jdx + 1));
         }
         formulaS = formulaS.substring(0, idx);
      }
      ((CMLScalar) formulas.get(0)).detach();
      CMLFormula formula = CMLFormula.createFormula(formulaS);
      formula.setDictRef("cml:authorFormula");
      cml.appendChild(formula);
      if (charge != 0) {
         formula.setFormalCharge(charge);
         molecule.setFormalCharge(charge);
      }
      if (spin > 1) {
         molecule.setSpinMultiplicity(spin);
      }
//		formula.debug();
   }

   private int getCharge(String s) {
      int ch = Integer.MAX_VALUE;
      int l = s.length();
      if (s.endsWith(S_MINUS)) {
         ch = -Integer.parseInt(s.substring(0, l - 1));
      } else if (s.endsWith(S_PLUS)) {
         ch = Integer.parseInt(s.substring(0, l - 1));
      }
      return ch;
   }

   private void addCartesians(CMLAtom atom, String[] fields, int natoms, String line) {
      if (fields.length < 4 || fields.length > 5) {
         throw new RuntimeException("bad number of fields for atom (" + natoms + ") / " + line + " in " + title);
      }
      int offset = fields.length - 3;
      Point3 xyz = new Point3(
              translateSymbolToDouble(fields[offset]),
              translateSymbolToDouble(fields[offset + 1]),
              translateSymbolToDouble(fields[offset + 2]));
      atom.setXYZ3(xyz);
      if (offset == 2) {
         if (!fields[1].equals("0")) {
            command.info("Skipped unknown atom field: " + fields[1] + " in " + title);
         }
      }
   }

   /** translates */
   double translateSymbolToDouble(String s) {
      String ss = s;
      // may have symbolic values
      if (symbolMap != null) {
         // might start with '-'
         String prefix = S_EMPTY;
         if (s.startsWith(S_MINUS)) {
            s = s.substring(1);
            prefix = S_MINUS;
         }
         ss = symbolMap.get(s);
         if (ss == null) {
            ss = prefix + s;
         } else {
            ss = prefix + ss;
         }
         ss = ss.replace(S_MINUS + S_MINUS, S_EMPTY);
      }
      Double doubleX = null;
      try {
         doubleX = new Double(ss);
      } catch (Exception e) {
         throw new RuntimeException("Cannot interpret as coordinate: " + ss + " {perhaps no symbol given)");
      }
      return doubleX.doubleValue();
   }

   private void addLength(CMLZMatrix zMatrix, String[] fields, int natoms) {
      Util.addLength(zMatrix, new String[]{
                 Util.createAtomId(Integer.parseInt(fields[1])),
                 Util.createAtomId(natoms),}, translateSymbolToDouble(fields[2]));
   }

   private void addAngle(CMLZMatrix zMatrix, String[] fields, int natoms) {
      Util.addAngle(zMatrix, new String[]{
                 Util.createAtomId(Integer.parseInt(fields[3])),
                 Util.createAtomId(Integer.parseInt(fields[1])),
                 Util.createAtomId(natoms)
              }, translateSymbolToDouble(fields[4]));
   }

   private void addTorsion(CMLZMatrix zMatrix, String[] fields, int natoms) {
      Util.addTorsion(zMatrix,
              new String[]{
                 Util.createAtomId(Integer.parseInt(fields[5])),
                 Util.createAtomId(Integer.parseInt(fields[3])),
                 Util.createAtomId(Integer.parseInt(fields[1])),
                 Util.createAtomId(natoms)
              }, translateSymbolToDouble(fields[6]));
   }

}

class NameValue {

   String name;
   String value;

   /** constructor
    *
    * @param n
    * @param v
    */
   public NameValue(String n, String v) {
      this.name = n;
      this.value = v;
   }

   /** to string.
    * @return string
    */
   public String toString() {
      return name + " = " + value;
   }
}

