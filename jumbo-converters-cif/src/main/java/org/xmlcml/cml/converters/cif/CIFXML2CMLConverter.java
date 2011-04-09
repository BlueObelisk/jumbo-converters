package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.cml.base.CMLConstants.XSD_DOUBLE;
import static org.xmlcml.cml.base.CMLConstants.XSD_STRING;
import static org.xmlcml.cml.converters.cif.CIFConstants.DELIM;
import static org.xmlcml.cml.converters.cif.CIFConstants.IUCR_CATEGORY;
import static org.xmlcml.cml.converters.cif.CIFConstants.IUCR_PREFIX;
import static org.xmlcml.cml.converters.cif.CIFConstants.NON_NUMERIC;
import static org.xmlcml.cml.converters.cif.CIFConstants.NUMERIC;
import static org.xmlcml.euclid.EuclidConstants.S_COLON;
import static org.xmlcml.euclid.EuclidConstants.S_MINUS;
import static org.xmlcml.euclid.EuclidConstants.S_PLUS;
import static org.xmlcml.euclid.EuclidConstants.S_SLASH;
import static org.xmlcml.euclid.EuclidConstants.S_UNDER;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFDataBlock;
import org.xmlcml.cif.CIFException;
import org.xmlcml.cif.CIFItem;
import org.xmlcml.cif.CIFLoop;
import org.xmlcml.cif.CIFTableCell;
import org.xmlcml.cif.CIFUtil;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTable.TableType;
import org.xmlcml.molutil.ChemicalElement;

/** 
 * Converts CIFXML into CML
 * 
 * @author pm286
 *
 */
public class CIFXML2CMLConverter extends AbstractConverter {
	
	private CIFXML2CMLOptions converterOptions;
	OutPutModuleBuilder helper = new OutPutModuleBuilder();

	private static CIFCategory[] CML_CATEGORIES = new CIFCategory[] {
		CIFCategory.ATOM_SITE_ANISO,
		CIFCategory.ATOM_SITE,
		CIFCategory.ATOM_SITES_SOLUTION,
		CIFCategory.ATOM_TYPE,
		CIFCategory.CELL,
		CIFCategory.CHEMICAL_FORMULA,
		CIFCategory.CHEMICAL,
		CIFCategory.GEOM_ANGLE,
		CIFCategory.GEOM_BOND,
		CIFCategory.SYMMETRY_EQUIV
	};

	public CIFXML2CMLConverter() {
		this.converterOptions = new CIFXML2CMLOptions();
	}

	public CIFXML2CMLConverter(CIFXML2CMLOptions converterOptions) {
		this.converterOptions = converterOptions;
	}

	public Type getInputType() {
		return Type.XML;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts a CIFXML object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param cifxml an Element that is the root of a CIFXML document
	 */
	@Override
	public Element convertToXML(Element cifxml) {
		init();
		this.getConverterLog().addToLog(Level.INFO, "CIFXML 2 CML");
		boolean failOnError = false;
		CIF cif = null;
		try {
			cif = new CIF(cifxml, failOnError);
		} catch (CIFException e) {
			runtimeException("Problem parsing CIF: ", e);
		}
		return processCIF(cif);
	}

	/** 
	 * processes CIF using CML semantics.
	 * if there is a block labelled global, uses that as global
	 * data and adds it to the CML.
	 * Selects blocks in sequence.
	 */
	private CMLElement processCIF(CIF cif) {
		List<CIFDataBlock> blocks = cif.getDataBlockList();
		List<CIFDataBlock> globalBlocks = new ArrayList<CIFDataBlock>(1);
		List<CIFDataBlock> structureBlocks = new ArrayList<CIFDataBlock>(1);
		for (CIFDataBlock block : blocks) {
			if (CIF2CMLUtils.isGlobalBlock(block)) {
				globalBlocks.add(block);
			} else {
				structureBlocks.add(block);
			}
		}

		List<CMLElement> outputCmls = new ArrayList<CMLElement>(structureBlocks.size());
		for (CIFDataBlock block : structureBlocks) {
			outputCmls.add(processStructureDataBlock(block));
		}
		CMLElement globalCml = processGlobalBlocks(globalBlocks);
		for (CMLElement cml : outputCmls) {
			if (globalCml != null) {
				copyElementChildren(globalCml, cml);
			}
		}

		return createFinalCml(outputCmls);
	}

	private CMLElement createFinalCml(List<CMLElement> cmls) {
		CMLElement cmlRoot = new CMLCml();
		if (cmls.size() == 1) {
			cmlRoot = cmls.get(0);
		} else if (cmls.size() > 1) {
			for (CMLElement cml : cmls) {
				cmlRoot.appendChild(cml);
			}
		}
		cmlRoot.addNamespaceDeclaration("iucr", "http://www.iucr.org/dictionary/cif");
		return cmlRoot;
	}

	private CMLElement processGlobalBlocks(List<CIFDataBlock> globalBlocks) {
		CIFDataBlock block = null;
		if (globalBlocks.size() > 1) {
			block = globalBlocks.get(0);
			for (int i = 1; i < globalBlocks.size(); i++) {
				CIFDataBlock b = globalBlocks.get(i);
				copyElementChildren(b, block);
			}
		} else if (globalBlocks.size() == 1) {
			block = globalBlocks.get(0);
		} else {
			return null;
		}
		CMLCml cml = new CMLCml();
		processNonCMLItems(block, cml);
		processNonCMLLoops(block, cml);
		return cml;
	}

	private void copyElementChildren(Element from, Element to) {
		Elements els = from.getChildElements();
		for (int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			el.detach();
			to.appendChild(el);
		}
	}

	/** 
	 * processes non-global CIF datablock using CML semantics.
	 * if there is a global block this adds it to the CML.
	 * Selects block by ID. if this is null finds first block that is not
	 * global (we may change this strategy)
	 * 
	 * @return CML or null
	 */
	private CMLCml processStructureDataBlock(CIFDataBlock block) {
		CMLCml cml = new CMLCml();
		String blockId = block.getId();
		String acceptableId = makeAcceptableId(blockId);
		cml.setId(acceptableId);
		cml.setTitle(acceptableId);

		processNonCMLItems(block, cml);
		processNonCMLLoops(block, cml);

		CMLMolecule molecule = new CMLMolecule();
		molecule.setId(acceptableId);
		CMLCrystal crystal = new CMLCrystal();
		CMLSymmetry symmetry = new CMLSymmetry();
		processCMLItems(block, cml, molecule, crystal, symmetry);
		processCMLLoops(block, cml, molecule, crystal, symmetry);

		cml.appendChild(molecule);
		molecule.insertChild(crystal, 0);
		crystal.addSymmetry(symmetry);

		return cml;
	}

	/** Takes a datablock id, removes leading underscores, replaces non-alphanumerics
	 * by underscores and prepends "c" if the first character is not a letter.
	 * Null and zero-length id strings are returned as cif_s or otherwise "unknown".
	 *
	 * @param s String to substitute.
	 * @param cif_s The id string on the source CIF file.
	 * @return A munged string.
	 */
	private static String makeAcceptableId(String ss) {
		// Remove leading underscores
		while (ss.startsWith(S_UNDER)) {
			ss = ss.substring(1);
		}

		// Substitute all non alpahNumeric characters by '_'
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length(); i++) {
			char c = ss.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c)) {	
				sb.append(c);
			} else {
				sb.append('_');
			}
		}
		ss = sb.toString();

		// If the first character is not a letter prepend "c"
		return (!Character.isLetter(ss.charAt(0))) ? "c"+ss : ss;
	}

	private CIFCategory getCMLCategory(String name) {
		CIFCategory category = null;
		for (CIFCategory cat : CML_CATEGORIES) {
			if (cat.contains(name)) {
				category = cat;
			}
		}
		return category;
	}

	private CIFCategory getCMLCategory(List<String> nameList) {
		CIFCategory category = null;
		for (CIFCategory cat : CML_CATEGORIES) {
			if (cat.matches(nameList)) {
				category = cat;
			}
		}
		return category;
	}

	private void addItem(CMLElement cmlElement, CIFItem item) {
		String name = item.getName().toLowerCase();
		boolean isNumeric = NON_NUMERIC;
		if (converterOptions.getDictionary() != null) {
			CMLEntry entry = converterOptions.getDictionary().getCMLEntry(name);
			if (entry == null) {
				warn("Cannot find dictionary item: "+name);
			}
			if (entry != null && XSD_DOUBLE.equals(entry.getDataType())) {
				isNumeric = NUMERIC;
			}
		}
		CMLProperty property = makeScalarProperty(item, isNumeric);
		if (property != null) {
			cmlElement.appendChild(property);
		}
	}

	private void addLoop(CMLElement cmlElement, CIFLoop loop, String categoryName) {
		CMLTable table = makeTable(loop, categoryName);
		int deletedColumns = deleteDefaultAndIndeterminates(loop);
		// do not add if all columns have been deleted
		if(deletedColumns < loop.getNameList().size()) {
			cmlElement.appendChild(table);
		}
	}

	private int deleteDefaultAndIndeterminates(CIFLoop loop) {
		int count = 0;
		List<String> nameList = loop.getNameList();
		for (String columnName : nameList) {
			if (!"".equals(columnName)) {
				List<String> columnValues = loop.getColumnValues(columnName);
				if (CIFUtil.isDefault(columnValues)) {
					loop.deleteColumn(columnName);
					count++;
				} else if (CIFUtil.isIndeterminate(columnValues)) {
					loop.deleteColumn(columnName);
					count++;
				}
			}
		}
		return count;
	}

	@SuppressWarnings("deprecation")
	private CMLTable makeTable(CIFLoop loop, String categoryName) {
		CMLTable table = new CMLTable();
		table.setTableType(TableType.COLUMN_BASED);
		table.setDictRef(makeDictRef(categoryName));
		List<String> nameList = loop.getNameList();
		for (int i = 0; i < nameList.size(); i++) {
			String columnName = nameList.get(i);
			List<CIFTableCell> cellList = loop.getColumnCells(i);
			CMLArray arrayFromColumn = new CMLArray();
			String dataType = XSD_STRING;
			String mungedId=columnName.toLowerCase().substring(1);
			String type=helper.getDataType(mungedId);
			String units = helper.getUnitsStringorNull(mungedId);
			if(type!=null){
			    if("xsd:float".equals(type)){
			        type=XSD_DOUBLE;
			    }
			    dataType=type;
			    arrayFromColumn.setDataType(type);
			}
			if(units!=null){
			    arrayFromColumn.setUnits(units);
			}
//			    
//			if (converterOptions.getDictionary() != null) {
//				CMLEntry entry = converterOptions.getDictionary().getCMLEntry(columnName.toLowerCase());
//				if (entry == null) {
//					warn("column name not in dictionary: "+columnName);
//				} else {
//					dataType = entry.getDataType();
//					column.setDataType(dataType);
//				}
//			}
			arrayFromColumn.setDictRef(makeDictRef(columnName));
			arrayFromColumn.setDelimiter(DELIM);
			StringBuilder errorValueBuilder = new StringBuilder(DELIM);
			boolean atLeastOneError=false;
			for (CIFTableCell cell : cellList) {
				String value = cell.getValue();
				if (XSD_DOUBLE.equals(dataType)) {
					double dValue = Double.NaN;
					Double error = null;
					if (!CIFUtil.isDefault(value)) {
						try {
							double values[] = CIFUtil.getNumericValueAndSu(value);
							if (values == null) {
								warn("Cannot parse "+columnName+S_SLASH+value);
							} else {
								dValue = values[0];
								error=values[1];
							}
						} catch (RuntimeException e) {
							runtimeException("bad double: ", e);
						}
					}
					arrayFromColumn.append(dValue);
					if(error!=null && !error.isNaN()){
					    atLeastOneError=true;
					    errorValueBuilder.append(error);
					    errorValueBuilder.append(DELIM);
					}
					else{
					    errorValueBuilder.append(DELIM);
					}
				} else {
                    if (cell.getValue().equals(".")) {
                        arrayFromColumn.append("");
                    } else {
                        arrayFromColumn.append(cell.getValue().trim());
                    }
                }
			}
			if(atLeastOneError){
			    Attribute errorAttribute=new Attribute("errorValues",errorValueBuilder.toString());
			    arrayFromColumn.addAttribute(errorAttribute);
			}
			Attribute sz = arrayFromColumn.getSizeAttribute();
			if (sz != null) {
				arrayFromColumn.removeAttribute(sz);
			}
			table.addArray(arrayFromColumn);
		}

		// FIXME 'columnBased' tableType is not valid WRT the CML schema, what to do?
		table.removeAttribute("tableType");
		return table;
	}

	/** adds namespace prefix and maybe edits characters.
	 * bad idea to replace characters at present
	 * @param name raw name
	 * @return edited name
	 */
	private String makeDictRef(String name) {
		if (name == null) {
			return "";
		}
		if (name.startsWith("_")) {
			name = name.substring(1);
		}
		return IUCR_PREFIX+S_COLON+sanitize(name);
	}

	private String sanitize(String str) {
		StringBuilder sb = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (   (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9')
					|| c == '-' || c == '_' || c == '+'
						|| c == '!' || c == '&') {
				sb.append(c);
			} else {
				sb.append('_');
			}
		}
		return sb.toString();
	}

	private void processNonCMLItems(CIFDataBlock block, CMLCml cml) {
		for (CIFItem item : block.getItemList()) {
			// omit CML
			CIFCategory category = getCMLCategory(item.getName());
			if (category != null) {
				continue;
			}
			addItem((CMLElement)cml, item);
		}
	}

	private void processNonCMLLoops(CIFDataBlock block, CMLCml cml) {
		for (CIFLoop loop : block.getLoopList()) {
			// omit CML
			CIFCategory cmlCategory = getCMLCategory(loop.getNameList());
			if (cmlCategory != null) {
				continue;
			}
			String name = loop.getNameList().get(0);
			String categoryName = getCategoryFromDictionary(name);
			addLoop(cml, loop, categoryName);
		}
	}

	private CMLEntry getEntry(String name) {
		CMLEntry entry = (converterOptions.getDictionary() == null) ? null : 
			converterOptions.getDictionary().getCMLEntry(name.toLowerCase());
		return entry;
	}

	private String getCategoryFromDictionary(String id) {
		String categoryName = null;
		CMLEntry entry = getEntry(id);
		if (entry != null) {
			List<Node> scalars = CMLUtil.getQueryNodes(entry, CMLScalar.NS+"[@dictRef='"+IUCR_CATEGORY+"']", CML_XPATH);
			if (scalars.size() != 0) {
				categoryName = ((CMLScalar)scalars.get(0)).getXMLContent();
			}
		}
		return categoryName;
	}

	private void processCMLItems(CIFDataBlock block, CMLCml cml, CMLMolecule molecule, CMLCrystal crystal, CMLSymmetry symmetry) {
		for (CIFItem item : block.getItemList()) {
			String name = item.getName();
			CIFCategory category = getCMLCategory(name);
			if (category != null) {
				String categoryName = category.getName();
				if (categoryName.equals("cell")) {
					addCell(item, crystal, symmetry);
				} else if (categoryName.equals("chemical_name")) {
					addChemicalName(item, cml);
				} else if (categoryName.equals("chemical_formula")) {
					try {
						addChemicalFormula(item, cml);
					} catch (RuntimeException e) {
						runtimeException("JUMBO cannot parse formula: ", e);
					}
				} else if (categoryName.equals("atom_sites_solution")) {
					addAtomSitesSolution(item, cml);
				} else {
					warn("Unprocessed CML item: "+name);
				}
			}
		}
	}

	private void processCMLLoops(CIFDataBlock block, CMLCml cml, CMLMolecule molecule, CMLCrystal crystal, CMLSymmetry symmetry) {
		for (CIFLoop loop : block.getLoopList()) {
			int deletedColumns = deleteDefaultAndIndeterminates(loop);
			List<String> nameList = loop.getNameList();
			CIFCategory category = getCMLCategory(nameList);
			if (category != null && deletedColumns < nameList.size()) {
				if (category.equals(CIFCategory.SYMMETRY_EQUIV)) {
					addSymmetry(loop, symmetry);
				} else if (category.equals(CIFCategory.ATOM_SITE_ANISO)) {
					addAtomSiteAniso(loop, cml);
				} else if (category.equals(CIFCategory.ATOM_SITE)) {
					addAtomSite(loop, molecule);
				} else if (category.equals(CIFCategory.ATOM_TYPE)) {
					addAtomType(loop, cml);
				} else if (category.equals(CIFCategory.GEOM_BOND)) {
					addGeomBond(loop, cml);
				} else if (category.equals(CIFCategory.GEOM_ANGLE)) {
					addGeomAngle(loop, cml);
				} else if (category.equals(CIFCategory.GEOM_TORSION)) {
					addGeomTorsion(loop, cml);
				} else {
					String message = "Unknown loopable category: "+category.getName();
					runtimeException(message);
				}
			}
		}

		if (converterOptions.isAddMissingSymmetry()) {
			ensureSymmetry(block, symmetry);
		}
	}

	private void ensureSymmetry(CIFDataBlock block, CMLSymmetry symmetry) {
		if (symmetry.getChildElements().size() > 0) {
			return;
		}
		SpaceGroupTool spaceGroupTool = converterOptions.getSpaceGroupTool();
		Nodes hmNodes = block.query(".//item[@name='_symmetry_space_group_name_h-m']");
		String hmName = (hmNodes.size() == 0) ? null : hmNodes.get(0).getValue();
		if (hmName != null) {
			CMLSymmetry newSym = spaceGroupTool.getSymmetry(hmName);
			copyElementChildren(newSym, symmetry);
		}
	}

	private void addSymmetry(CIFLoop loop, CMLSymmetry symmetry) {
		CMLSymmetry sym = createSymmetryFromOperators(loop);
		copyElementChildren(sym, symmetry);
	}

	private void addCell(CIFItem item, CMLCrystal crystal, CMLSymmetry symmetry) {
		String[] names = {
				"_cell_length_a",
				"_cell_length_b",
				"_cell_length_c",
				"_cell_angle_alpha",
				"_cell_angle_beta",
				"_cell_angle_gamma"
		};
		String[] names1 = {
				"_cell_formula_units_z",
				"_cell_volume",
				"_symmetry_space_group_name_H-M"
		};
		String name = item.getName();
		int idx = CIFUtil.indexOf(name, names, true);
		// sets params in correct order
		if (idx >= 0) {
			CMLScalar scalar = makeScalar(item, NUMERIC, true);
			if (scalar != null) {
				crystal.appendChild(scalar);
			}
		} else {
			idx = CIFUtil.indexOf(name, names1, true);
			// formula units
			if (idx == 0) {
				item.processSu(false);
				Double z = item.getNumericValue();
				if (z == null) {
					if (!converterOptions.isSkipErrors()) {
						runtimeException("Bad Z value "+item.getValue());
					} else {
						warn("Valid Z value not supplied.");
					}
				} else {
					crystal.setZ((int) z.doubleValue());
				}
				// cell volume
			} else if (idx == 1) {
				// omit volume till we redesign cell
				// item.processSu(true);
				// addItem((CMLElement)crystal, item);
			} else if (idx == 2) {
				symmetry.setSpaceGroup(item.getValue());
			} else {
				warn("Unknown cell name: "+name);
				addItem((CMLElement)crystal, item);
			}
		}
	}

	private CMLScalar makeScalar(CIFItem item, boolean numeric, boolean addDictRef) {
		CMLScalar scalar = new CMLScalar();
		String value = item.getValue();
		if (CIFUtil.isIndeterminateValue(value)) {
			// omit "?"
			return null;
		} else if (CIFUtil.isDefault(value)) {
			// omit "."
			return null;
		} else {
			if (addDictRef) {
				String name = item.getName();
				scalar.setDictRef(makeDictRef(name));
			}
			if (numeric) {
				item.processSu(false);
				Double d = item.getNumericValue();
				if (d != null) {
					scalar.setValue(d.doubleValue());
					scalar.setErrorValue(item.getSu());
				} else {
					if (converterOptions.isCheckDoubles() && !converterOptions.isSkipErrors()) {
						runtimeException("Cannot parse "+item.getName()+" as double: "+item.getValue());
					} else {
						warn("Cannot parse "+item.getName()+" as double: "+item.getValue());
						scalar = null;
					}
				}
			} else {
				String v = item.getValue();
				if (v != null) {
					scalar.setValue(item.getValue());
				}
			}
		}
		return scalar;
	}

	private CMLProperty makeScalarProperty(CIFItem item, boolean numeric) {
		CMLProperty property = null;
		String value = item.getValue();
		if (CIFUtil.isIndeterminateValue(value)) {
			// omit "?"
			return null;
		} else if (CIFUtil.isDefault(value)) {
			// omit "."
			return null;
		} else {
			property = new CMLProperty();
			String name = item.getName();
			property.setDictRef(makeDictRef(name));
			property.appendChild(makeScalar(item, numeric, false));
		}
		return property;
	}

	private CMLProperty makeScalarProperty(String dictRef, String value) {
		CMLProperty property = new CMLProperty();
		CMLScalar scalar = new CMLScalar();
		scalar.setValue(value);
		property.appendChild(scalar);
		property.setDictRef(makeDictRef(dictRef));
		return property;
	}

	private CMLProperty makeScalarProperty(String dictRef, double value) {
		return makeScalarProperty(dictRef, ""+value);
	}

	private void addChemicalName(CIFItem item, CMLCml cml0) {
		String[] names = {
				"_chemical_name_common",
				"_chemical_name_systematic",
		};
		String name = item.getName();
		int idx = CIFUtil.indexOf(name, names, true);
		// sets params in correct order
		if (idx == -1) {
			warn("unknown name type "+name);
		}
		CMLProperty property = makeScalarProperty(item, NON_NUMERIC);
		if (property != null) {
			cml0.appendChild(property);
		}
	}

	private void addChemicalFormula(CIFItem item, CMLCml cml) {
		String[] names = {
				"_chemical_formula_moiety",
				"_chemical_formula_sum",
				"_chemical_formula_iupac",
				"_chemical_formula_structural",
				//
				"_chemical_formula_weight",
		};
		CMLFormula formula = null;
		String name = item.getName();
		String value = item.getValue();
		if (!CIFUtil.isIndeterminateValue(value)) {
			int idx = CIFUtil.indexOf(name, names, true);
			// sets params in correct order
			if (idx == 0) {
				// moiety
				// if this cannot be parsed we throw an error and do not
				// create the object
				try {
					formula = CMLFormula.createFormula(value, CMLFormula.Type.MOIETY);
				} catch (RuntimeException e) {
					formula = new CMLFormula();
					warn("Failed to parse formula (maybe JUMBO's fault) : "+e.getMessage());
				}
				formula.setDictRef(makeDictRef(name));
				formula.setInline(value);
			} else if (idx == 1) {
				// sum
				// if this cannot be parsed we throw an error and do not
				// create the object
				try {
					formula = CMLFormula.createFormula(value);
				} catch (RuntimeException e) {
					formula = new CMLFormula();
					warn("Failed to parse formula (maybe JUMBO's fault) : "+e.getMessage());
				}
				formula.setDictRef(makeDictRef(name));
				formula.setInline(value);
			} else if (idx == 2) {
				// iupac
				// if we cannot parse it, create formula without contents
				try {
					formula = CMLFormula.createFormula(value);
				} catch (RuntimeException e) {
					formula = new CMLFormula();
					warn("Failed to parse formula (maybe JUMBO's fault) : "+e.getMessage());
				}
				formula.setInline(value);
				formula.setDictRef(makeDictRef(name));
			} else if (idx == 3) {
				// structural, probably same as IUPAC
				try {
					formula = CMLFormula.createFormula(value);
				} catch (RuntimeException e) {
					formula = new CMLFormula();
					warn("Failed to parse formula (maybe JUMBO's fault) : "+e.getMessage());
				}
				formula.setInline(value);
				formula.setDictRef(makeDictRef(name));
			} else if (idx == 4) {
				CMLProperty property = makeScalarProperty(item, NUMERIC);
				if (property != null) {
					cml.appendChild(property);
				}
			}
		}

		if (formula != null) {
			// remove the empty atomArray child, as it is not valid WRT to CMLLite.
			Element atomArray = formula.getFirstCMLChild(CMLAtomArray.TAG);
			if (atomArray != null) {
				formula.removeChild(atomArray);
			}
			cml.appendChild(formula);
		}
	}

	private void addAtomSitesSolution(CIFItem item, CMLCml cml0) {
		String[] names = {
				"_atom_sites_solution_hydrogens",
		};
		String name = item.getName();
		int idx = CIFUtil.indexOf(name, names, true);
		// sets params in correct order
		if (idx == -1) {
			warn("Unknown atom_sites_solution "+name);
		}
		CMLProperty property = makeScalarProperty(item, NON_NUMERIC);
		if (property != null) {
			cml0.appendChild(property);
		}
	}

	private CMLSymmetry createSymmetryFromOperators(CIFLoop loop) {
		String[] names =  {
				"_symmetry_equiv_pos_as_xyz",
				"_symmetry_equiv_pos_site_id",
		};
		checkLoop(loop, names, 0, "symmetry_equiv");
		List<String> operators = loop.getColumnValues(names[0]);
		if (operators == null) {
			throw new RuntimeException("no "+names[0]);
		}

		CMLSymmetry symmetry = CMLSymmetry.createFromXYZStrings(operators);
		symmetry.normalizeCrystallographically();
		return symmetry;
	}

	private void addAtomSiteAniso(CIFLoop loop, CMLCml cml) {
		String[] names = {
				"_atom_site_aniso_label",
				"_atom_site_aniso_u_11",
				"_atom_site_aniso_u_12",
				"_atom_site_aniso_u_13",
				"_atom_site_aniso_u_22",
				"_atom_site_aniso_u_23",
				"_atom_site_aniso_u_33",
				"_atom_site_aniso_type_symbol",                
		};
		String category = "atom_site";
		checkLoop(loop, names, 0, category);
		//		CMLTable table = createTable(loop, category);
		CMLTable table = makeTable(loop, category);
		cml.appendChild(table);
	}

	private void addAtomSite(CIFLoop loop, CMLMolecule molecule) {
		String AS = "_atom_site";
		String AS_LABEL = "_atom_site_label";
		String AS_FX = "_atom_site_fract_x";
		String AS_FY = "_atom_site_fract_y";
		String AS_FZ = "_atom_site_fract_z";
		String AS_OCC = "_atom_site_occupancy";
		String AS_SYM = "_atom_site_type_symbol";
		String AS_UISO = "_atom_site_u_iso_or_equiv";
		String AS_ADP = "_atom_site_adp_type";
		String AS_CALC = "_atom_site_calc_flag";
		//		obsolete according to CIF dictionary
		String AS_REF = "_atom_site_refinement_flags";
		String AS_DISASS = "_atom_site_disorder_assembly";
		String AS_DISGRP = "_atom_site_disorder_group";
		String AS_MULT = "_atom_site_symmetry_multiplicity";
		String AS_ATT = "_atom_site_calc_attached_atom";
		String AS_THERM = "_atom_site_thermal_displace_type";
		String[] names = {
				AS_LABEL,
				AS_FX,
				AS_FY,
				AS_FZ,
				AS_OCC,
				AS_SYM,
				AS_UISO,
				AS_ADP,
				AS_CALC,
				AS_REF,
				AS_DISASS,
				AS_DISGRP,
				AS_MULT,
				AS_ATT,
				AS_THERM,
				"_atom_site_attached_hydrogens",
				"_atom_site_wyckoff_symbol",
				"_atom_site_b_iso_or_equiv",
				"_atom_site_uiso_or_equiv",
				"_atom_site_refinement_flags_posn",
		};
		checkLoop(loop, names, 0, AS);

		try {
			boolean failOnError = false;
			loop.processSu(failOnError);
		} catch (RuntimeException e) {
			runtimeException(AS+": "+e.getMessage());
		}
		// check names
		List<String> nameList = loop.getNameList();
		for (String name : nameList) {
			if (CIFUtil.indexOf(name, names, true) == -1) {
				warn("unknown atom_site name "+name);
			}
		}
		List<String> symbols = loop.getColumnValues(AS_SYM);
		List<String> labels = loop.getColumnValues(AS_LABEL);
		// atom labels
		if (symbols == null) {
			symbols = new ArrayList<String>();
			for (String label : labels) {
				symbols.add(getSymbol(label));
			}
		}
		// chemical symbols
		int i = 0;
		for (String symbol : symbols) {
			if (ChemicalElement.getChemicalElement(symbol) == null) {
				while(symbol.endsWith(S_MINUS)) {
					symbol = symbol.substring(0, symbol.length()-1);
				}
				while(symbol.endsWith(S_PLUS)) {
					symbol = symbol.substring(0, symbol.length()-1);
				}
				while(Character.isDigit(symbol.charAt(symbol.length()-1))) {
					symbol = symbol.substring(0, symbol.length()-1);
				}
				if (Character.isLowerCase(symbol.charAt(0))) {
					symbol = symbol.substring(0, 1).toUpperCase()+symbol.substring(1);
				}
				if (ChemicalElement.getChemicalElement(symbol) == null) {
					throw new RuntimeException("Bad element: "+symbol);
				}
			}
			CMLAtom atom = null;
			atom = new CMLAtom("a"+(++i));
			molecule.addAtom(atom);
			atom.setElementType(symbol);
		}
		List<CMLAtom> atoms = molecule.getAtoms();
		double[] x = loop.getNumericColumnValues(AS_FX);
		double[] y = loop.getNumericColumnValues(AS_FY);
		double[] z = loop.getNumericColumnValues(AS_FZ);
		if (x == null || y == null || z == null) {
			runtimeException("Missing fractional coordinates");
		}
		if (atoms.size() != x.length) {
			runtimeException("x ("+x.length+")does not match atoms ("+atoms.size()+")");
		}
		double[] occ = loop.getNumericColumnValues(AS_OCC);
		double[] u = loop.getNumericColumnValues(AS_UISO);
		List<String> adp = loop.getColumnValues(AS_ADP);
		List<String> calc = loop.getColumnValues(AS_CALC);
		List<String> disass = loop.getColumnValues(AS_DISASS);
		List<String> disgrp = loop.getColumnValues(AS_DISGRP);
		List<String> label = loop.getColumnValues(AS_LABEL);
		List<String> mult = loop.getColumnValues(AS_MULT);
		List<String> ref = loop.getColumnValues(AS_REF);
		i = 0;
		for (CMLAtom atom : atoms) {
			// coordinates are meaningless is calc flag = "dum"
			if (calc == null || !calc.get(i).equals("dum")) {
				atom.setXFract(x[i]);
				atom.setYFract(y[i]);
				atom.setZFract(z[i]);
			}
			// occupancy - only add if not 1.0 - NO!
			// we need occupancies on all atoms to resolve disorder
			// 
			if (occ != null /*&& occ[i] < 1.0*/) {
				atom.setOccupancy(occ[i]);
			}
			if (u != null) {
				atom.appendChild(makeScalarProperty(AS_UISO, u[i]));
			}
			if (adp != null && !CIFUtil.isIndeterminateValue(adp.get(i))) {
				atom.appendChild(makeScalarProperty(AS_ADP, adp.get(i)));
			}
			// calc flag. omit default "d" (calculated from diffraction data)
			// only add if "c" or "calc" and expand to "calc"
			if (calc != null) {
				if (calc.get(i).equals("") ||
						calc.get(i).equals("calc")) {
					atom.appendChild(makeScalarProperty(AS_CALC, "calc"));
				}
			}
			if (disass != null && !CIFUtil.isIndeterminateValue(disass.get(i))) {
				atom.appendChild(makeScalarProperty(AS_DISASS, disass.get(i)));
			}
			if (disgrp != null && !CIFUtil.isIndeterminateValue(disgrp.get(i))) {
				atom.appendChild(makeScalarProperty(AS_DISGRP, disgrp.get(i)));
			}
			if (label != null && !CIFUtil.isIndeterminateValue(label.get(i))) {
				CMLLabel atomLabel = new CMLLabel();
				atomLabel.setDictRef(makeDictRef(AS_LABEL));
				atomLabel.setCMLValue(label.get(i));
				atom.appendChild(atomLabel);
			}
			// add multiplicity if not unity
			if (mult != null) {
				int m = 0;
				try {
					m = Integer.parseInt(mult.get(i));
				} catch (NumberFormatException e) {
					runtimeException("bad atom spaceGroup multiplicity: "+e);
				}
				if (m > 1) {
					atom.setSpaceGroupMultiplicity(m);
				}
			}
			if (ref != null && !CIFUtil.isIndeterminateValue(ref.get(i))) {
				atom.appendChild(makeScalarProperty(AS_REF, ref.get(i)));
			}
			i++;
		}
	}

	private String getSymbol(String ss) {
		String s = ss;
		if (s.length() > 2) {
			s = s.substring(0, 2);
		}
		// chop anything that is not a trailing lowercase
		if (s.length() == 2) {
			if (!Character.isLowerCase(s.charAt(1))) {
				s = s.substring(0, 1);
			}
		}
		return s;
	}

	private void addAtomType(CIFLoop loop, CMLCml cml) {
		String[] names = {
				"_atom_type_symbol",
				"_atom_type_description",
				"_atom_type_scat_dispersion_real",
				"_atom_type_scat_dispersion_imag",
				"_atom_type_scat_source",
				"_atom_type_oxidation_number",
				"_atom_type_number_in_cell",
				"_atom_type_scat_Cromer_Mann_a1",
				"_atom_type_scat_Cromer_Mann_b1",
				"_atom_type_scat_Cromer_Mann_a2",
				"_atom_type_scat_Cromer_Mann_b2",
				"_atom_type_scat_Cromer_Mann_a3",
				"_atom_type_scat_Cromer_Mann_b3",
				"_atom_type_scat_Cromer_Mann_a4",
				"_atom_type_scat_Cromer_Mann_b4",
				"_atom_type_scat_Cromer_Mann_a3",
				"_atom_type_scat_Cromer_Mann_c",
				"_atom_type_radius_bond",
		};
		String category = "atom_type";
		checkLoop(loop, names, 0, category);
		CMLTable table = makeTable(loop, category);
		cml.appendChild(table);
	}

	private void checkLoop(CIFLoop loop, String[] names, int keyPos, String categoryName) {
		boolean key = false;
		for (String name : loop.getNameList()) {
			name = name.toLowerCase();
			int idx = CIFUtil.indexOf(name, names, true);
			if (idx == -1) {
				warn("Unknown "+categoryName+": "+name);
			}
			key = (key) ? key : name.equalsIgnoreCase(names[keyPos]);
		}
		if (!key) {
			runtimeException("Must give "+names[keyPos]);
		}
	}

	private void addGeomBond(CIFLoop loop, CMLCml cml) {
		String[] names = {
				"_geom_bond_atom_site_label_1",
				"_geom_bond_atom_site_label_2",
				"_geom_bond_site_symmetry_2",
				"_geom_bond_distance",
				"_geom_bond_publ_flag",
				"_geom_bond_site_symmetry_1",
		};
		String category = "geom_bond";
		checkLoop(loop, names, 0, category);
		CMLTable table = makeTable(loop, category);
		cml.appendChild(table);
	}

	private void addGeomAngle(CIFLoop loop, CMLCml cml) {
		String[] names = {
				"_geom_angle_atom_site_label_1",
				"_geom_angle_atom_site_label_2",
				"_geom_angle_atom_site_label_3",
				"_geom_angle_site_symmetry_1",
				"_geom_angle_site_symmetry_3",
				"_geom_angle",
				"_geom_angle_publ_flag",
				"_geom_angle_site_symmetry_2",
		};
		String category = "geom_angle";
		checkLoop(loop, names, 0, category);
		CMLTable table = makeTable(loop, category);
		cml.appendChild(table);
	}

	private void addGeomTorsion(CIFLoop loop, CMLCml cml) {
		String[] names = {
				"_geom_torsion_atom_site_label_1",
				"_geom_torsion_atom_site_label_2",
				"_geom_torsion_atom_site_label_3",
				"_geom_torsion_atom_site_label_4",
				"_geom_torsion_site_symmetry_1",
				"_geom_torsion_site_symmetry_3",
				"_geom_torsion",
				"_geom_torsion_publ_flag",
				"_geom_torsion_site_symmetry_2",
		};
		String category = "geom_torsion";
		checkLoop(loop, names, 0, category);
		CMLTable table = makeTable(loop, category);
		cml.appendChild(table);
	}
	
}



