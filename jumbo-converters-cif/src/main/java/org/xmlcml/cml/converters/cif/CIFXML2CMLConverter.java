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

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
import org.xmlcml.cml.converters.cif.dict.CIFDictionary;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
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

	private static final Logger LOG = Logger.getLogger(CIFXML2CMLConverter.class);
	
	private CIFDataBlock globalBlock;
	CIF cif = null;

	private CMLCrystal crystal = null;
	private List<CMLScalar> cellParams = null;
	private CMLSymmetry symmetry;
	private CMLFormula formula;
	private String spaceGroup;
	private CMLMolecule molecule;

	private SpaceGroupTool spaceGroupTool = null;
	// omit S_QUERY in CIFs 
//	private boolean omitIndeterminate = true;
	// omit "."in CIFs
//	private boolean omitDefault = true;
    private List<CIFDataBlock> blockList;
    private CIFDictionary dictionary;
    private List<CMLElement> cmlElementList;

    private boolean noGlobal = false;
    private boolean mergeGlobal = false;
    private boolean trimElements = true;
    private boolean skipErrors = false;
    private boolean checkDoubles = false;

	final String UPDATESG_OPT_NAME = "UPDATESG";
	final String SPACEGROUP_FILE_OPT_NAME = "SPACEGROUP";
	final String SKIP_HEADER_OPT_NAME = "SKIPHEADER";
	final String SKIP_ERRORS_OPT_NAME = "SKIPERRORS";
	final String NO_GLOBAL_OPT_NAME = "NOGLOBAL";
	final String MERGE_GLOBAL_OPT_NAME = "MERGEGLOBAL";
	final String CHECK_DUPLICATES_OPT_NAME = "CHECKDUPLICATES";
	final String DEBUG_OPT_NAME = "DEBUG";
	final String ECHO_INPUT_OPT_NAME = "ECHOINPUT";
	final String ADD_CARTESIANS_OPT_NAME = "ADDCARTESIANS";

	static CIFCategory[] CML_CATEGORIES = new CIFCategory[] {
		// order matters because category not parsable
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

	public Type getInputType() {
		return Type.XML;
	}
	
	public Type getOutputType() {
		return Type.CML;
	}
	
	/** constructor.
	 *
	 */
	public CIFXML2CMLConverter() {
		init();
	}

	@Override
	protected void init() {
		cif = null;
		molecule = null;
		globalBlock = null;
		crystal = null;
		cellParams = null;
		dictionary = null;
		symmetry = null;
		formula = null;
		spaceGroup = null;
		spaceGroupTool = null;
//		spaceGroupFile = null;
//		updateSpaceGroup = false;
//		omitIndeterminate = true;
//		omitDefault = true;
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
		try {
			cif = new CIF(cifxml, failOnError);
		} catch (CIFException e) {
			runtimeException("cif parse: ", e);
		}
		Element cmlRoot = processCIF();
		LOG.debug("CIFXML 2 CML");
		return cmlRoot;
	}
	
	
	/** set dictionary.
	 * @param dictionary
	 */
	public void setDictionary(CIFDictionary dictionary) {
		this.dictionary = dictionary;
	}

	/** set cif.
	 * 
	 * @param cif
	 */
	public void setCIF(CIF cif) {
		init();
		this.cif = cif;
	}

	/** gets the CIF. 
	 * @return the CIF or null;
	 */
	public Object getLegacyObject() {
		return cif;
	}

	void setSpaceGroupAnalyzer(SpaceGroupTool spaceGroupAnalyzer) {
		this.spaceGroupTool = spaceGroupAnalyzer;
	}

	
	/** Takes a datablock id, removes leading underscores, replaces non-alphanumerics
	 * by underscores and prepends "c" if the first character is not a letter.
	 * Null and zero-length id strings are returned as cif_s or otherwise "unknown".
	 *
	 * @param s String to substitute.
	 * @param cif_s The id string on the source CIF file.
	 * @return A munged string.
	 */
   private static String makeAcceptableId(String s, String cif_s) {
	   
	   // Set a default string 
	   String s_default = (cif_s == null) ? "unknown" : cif_s ;
	   
	   // Turn null strings into "unknown"
	   String ss = (s == null) ? s_default : s;
	   
	   // Remove leading underscores
	   while (ss.startsWith(S_UNDER)) {
		   ss = ss.substring(1);
	   }
	   
	   // Turn zero-length strings into "unknown"	   
	   ss = (ss.length() == 0) ? s_default : ss;
	   
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

	/** processes non-global CIF datablock using CML semantics.
	 * if there is a global block this adds it to the CML.
	 * Selects block by ID. if this is null finds first block that is not
	 * global (we may change this strategy)
	 * @return CML or null
	 */
	private CMLCml processNonGlobalBlock(CIFDataBlock nonGlobalBlock) {
		CMLCml cml0 = null;
		if (cif != null) {
			cml0 = new CMLCml();
			String dataBlockId = nonGlobalBlock.getId();
			if (nonGlobalBlock != null) {
				String id = makeAcceptableId(dataBlockId, cif.getId());
				cml0.setId(id);
				
				// FIXME: For now we are setting the title to be equal to
				// the fileId as given in id attribute of the cif tag.
				// This applies to all non-global data blocks in the cif
				// even if each block has a logically different 'title'.
				cml0.setTitle(cif.getId());

				// Add id metadata. This refers to the id of the
				// data block in the original cif file. Note the we do
				// not use the makeAcceptableId() method to clean it up
				CMLProperty prop = new CMLProperty() ;			
				CMLMetadataList mdList = new CMLMetadataList() ;
				CMLMetadata mdItem = new CMLMetadata() ;
				
				mdItem.setName("cif:datablock") ;
				mdItem.setConvention("self:id") ;
				mdItem.setContent(dataBlockId) ;
				
				mdList.setDictRef("self:datablock") ;
				
				mdList.addMetadata(mdItem) ;
				prop.appendChild(mdList) ;
				cml0.insertChild(prop, 0) ;				
			}
			if (globalBlock != null) {
				processNonCMLItems(globalBlock, cml0);
				processNonCMLLoops(globalBlock, cml0);
				processCMLItems(globalBlock, cml0);
				processCMLLoops(globalBlock, cml0);
			}
			processNonCMLItems(nonGlobalBlock, cml0);
			processNonCMLLoops(nonGlobalBlock, cml0);
			processCMLItems(nonGlobalBlock, cml0);
			processCell();
			processCMLLoops(nonGlobalBlock, cml0);
			// append the crystal element to the top of the molecule document
			molecule.insertChild(crystal, 0);
						
			try {
				checkCML(cml0);
			} catch (RuntimeException e) {
				runtimeException("Cannot processBlock ", e);
			}
		}
		return cml0;
	}

	private void checkCML(CMLCml cml) {
		if (molecule.getChildCMLElements(CMLCrystal.TAG).size() == 0) {
			runtimeException("nonGlobalBlock "+cml.getId()+" has no cell");
		}
		if (cml.getChildCMLElements(CMLMolecule.TAG).size() == 0) {
			runtimeException("nonGlobalBlock "+cml.getId()+" has no molecule");
		}
	}
	
	public CMLElement processCIF(CIF cifxml) {
		this.cif = cifxml;
		return processCIF();
	}
	
	/** processes CIF using CML semantics.
	 * if there is a block labelled global, uses that as global
	 * data and adds it to the CML.
	 * Selects blocks in sequence.
	 */
	private CMLElement processCIF() {
		cmlElementList = new ArrayList<CMLElement>();
		blockList = new ArrayList<CIFDataBlock>();
		globalBlock = null;
		CMLElement cmlRoot = null;
		if (cif != null) {
			List<CIFDataBlock> bList = cif.getDataBlockList();
			if (bList.size() > 0) {
			
			// FIXME: kept in case the line following this one doesn't work
			// String cifId = cif.getAttributeValue("id");
			
			// This id is the one for the whole CIF 
			// (assumed to be equal to the name of the source CIF file) 
				String cifId = cif.getId();
				
				
				// Process each datablock of the cif in turn
				for (CIFDataBlock block : bList) {
					processBlock(block);
				}
				
				// Depending on the number of datablocks concatentate together
				// and generate the necessary ids for each
				if (cmlElementList.size() == 0) {
					LOG.warn("No CML elements found");
					cmlRoot = new CMLCml();
					// no-op
				} else if (cmlElementList.size() == 1) {
					cmlRoot = (CMLElement) cmlElementList.get(0);
					mungeCMLRootElementId(cmlRoot, cifId, 0);
				} else {
					cmlRoot = new CMLCml();
					int i = 0;
					for (CMLElement cmlElement : cmlElementList) {
						cmlRoot.appendChild(cmlElement);
						mungeCMLRootElementId(cmlElement, cifId, ++i);
					}
				}
			}
		}
		return cmlRoot;
	}

	private void processBlock(CIFDataBlock block) {
		symmetry = null;
		String id = block.getId();
		Nodes crystalNodes = block.query(".//item[@name='_cell_length_a']");
		Nodes moleculeNodes = block.query(".//loop[contains(concat(' ',@names,' '),' _atom_site_label ')]");
		Nodes symmetryNodes = block.query(".//loop[contains(concat(' ',@names,' '),' _symmetry_equiv_pos_as_xyz ')]");
		if (symmetryNodes.size() > 0) {
			createSymmetryFromOperators((CIFLoop)symmetryNodes.get(0));
		}
		Nodes hmNodes = block.query(".//item[@name='_symmetry_space_group_name_h-m']");
		String hmName = (hmNodes.size() == 0) ? null : hmNodes.get(0).getValue();
		// look up symmetry
		if (symmetry == null && spaceGroupTool != null && hmName != null) {
			symmetry = spaceGroupTool.getSymmetry(hmName);
		}
		Nodes hallNodes = block.query(".//item[@name='_symmetry_space_group_name_hall']");
		String hallName = (hallNodes.size() == 0) ? null : hallNodes.get(0).getValue();
		// global?
		if(crystalNodes.size() == 0 && 
				moleculeNodes.size() == 0 &&
				symmetry == null) {
			if (globalBlock != null) {
				warn("WARNING more than one global block - taking first");
			} else {
				globalBlock = block;
				if (!noGlobal || mergeGlobal) {
					blockList.add(globalBlock);
				}
			}
		} else if(crystalNodes.size() == 1 && 
				moleculeNodes.size() == 1 &&
				symmetry != null) {
			CMLElement cmlElement0 = null;
			try{
				CIFDataBlock nonGlobalBlock = block;
				cmlElement0 = processNonGlobalBlock(nonGlobalBlock);
			} catch (RuntimeException e) {
				runtimeException("processBlock ("+id+"), maybe data error("+e+")", e);
			}
			if (mergeGlobal && globalBlock != null) {
				//mergeGlobal();
			}
			if (cmlElement0 != null) {
				cmlElementList.add(cmlElement0);
			}
		} else if(crystalNodes.size() == 0) {
			throw new RuntimeException("no cell given: "+id);
		} else if(symmetry == null) {
			if (hallName == null && hmName == null) {
				if (skipErrors) {
					warn("SKIPPING ERROR - no space group or symmetry given: "+id);
				} else {
					runtimeException("no space group or symmetry given: "+id);
				}
			} else {
				if (skipErrors) {
					warn("SKIPPING ERROR - no symmetry operators given and cannot lookup for : "+id+S_SLASH+
							hmName+" ... "+hallName);
				} else {
					runtimeException("no symmetry operators given and cannot lookup for : "+id+S_SLASH+
							hmName+" ... "+hallName);
				}
			}
		} else if(moleculeNodes.size() == 0) {
			runtimeException("no explicit atoms given: "+id);
		}
		if (crystal !=null && symmetry != null && molecule != null) {
			crystal.appendChild(symmetry);
		}
		if (spaceGroupTool != null) {
			spaceGroupTool.analyze(symmetry, hmName, hallName);
		}
	}
	
	/** A nasty hack to try to get a meaningful id into the system.
	 * This is difficult, since authors often create no or awful id's.
	 * 
	 * if cifId is null and serial is 0 take no action 
	 *    (block will retain any id)
	 *    
	 * if cifId is null and serial is >= 1
	 *    if (blocks have no ids, create ids as block1, block2, &c.
	 *    if blocks have ids, leave them (must assume non-uniqueness 
	 *    already eliminated)
	 *   
	 * If cifId is non-null and serial is 0
	 * 		null or global blocks 
	 *  
	 * If cifId is non-null and serial is >=1
	 * 		append serial number for null or global blocks to cifId, 
	 * 		otherwise append element's orignal id  
	 * 
	 * @param element Usually a data-block possibly with an id attribute.
	 * @param cifId An additional string to to append to the munged id.
	 * @param serial A serial number to append to the munged id.
	 */
	private void mungeCMLRootElementId(CMLElement element, String cifId, int serial) {
		// FIXME: kept in case the line following this one doesn't work
		//String id = element.getAttributeValue("id");
		String id = element.getId();
		
		if (cifId == null) {
			if (serial == 0) {
					// leave unchanged
			} else {
				if (id == null) {
					id = "block"+serial;
				} else if (id.equalsIgnoreCase("global")) {
					id = "block"+serial;
				} else {
					// leave unchanged
				}
			}
		} else {
			if (serial == 0) {
				id = cifId;
			} else {
				if (id == null) {
					id = cifId + S_UNDER + serial;
				} else if (id.equalsIgnoreCase("global")) {
					id = cifId + S_UNDER + serial;
				} else {
					id = cifId + S_UNDER + id;
				}
			}
		}
		
		if (id != null) {
			// FIXME: kept in case the line following this one doesn't work
			//element.addAttribute(new Attribute("id", id));
			element.setId(id);
		}
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
		if (dictionary != null) {
			CMLEntry entry = dictionary.getCMLEntry(name);
			if (entry == null) {
				warn("Cannot find dictionary item: "+name);
			}
			if (entry != null && XSD_DOUBLE.equals(entry.getDataType())) {
				isNumeric = NUMERIC;
			}
		}
		CMLScalar scalar = makeScalar(item, isNumeric);
		if (scalar != null) {
			cmlElement.appendChild(scalar);
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
		table.setDictRef(makeDictRef(makeCategoryId(categoryName)));
		List<String> nameList = loop.getNameList();
		for (int i = 0; i < nameList.size(); i++) {
			String columnName = nameList.get(i);
			List<CIFTableCell> cellList = loop.getColumnCells(i);
			CMLArray column = new CMLArray();
			String dataType = XSD_STRING;
			if (dictionary != null) {
				CMLEntry entry = dictionary.getCMLEntry(columnName.toLowerCase());
				if (entry == null) {
					warn("column name not in dictionary: "+columnName);
				} else {
					dataType = entry.getDataType();
					column.setDataType(dataType);
				}
			}
			column.setDictRef(makeDictRef(columnName));
			column.setDelimiter(DELIM);
			for (CIFTableCell cell : cellList) {
				String value = cell.getValue();
				if (XSD_DOUBLE.equals(dataType)) {
					double dValue = Double.NaN;
					if (!CIFUtil.isDefault(value)) {
						try {
							double values[] = CIFUtil.getNumericValueAndSu(value);
							if (values == null) {
								warn("Cannot parse "+columnName+S_SLASH+value);
							} else {
								dValue = values[0];
							}
						} catch (RuntimeException e) {
							runtimeException("bad double: ", e);
						}
					}
					column.append(dValue);
				} else {
					column.append(cell.getValue());
				}
			}
			// this is a bug! the count output is larger by 1
			// FIXME
			Attribute sz = column.getSizeAttribute();
			if (sz != null) {
				column.removeAttribute(sz);
			}
			table.addArray(column);
		}
		return table;
	}

	/** adds namespace prefix and maybe edits characters.
	 * bad idea to replace characters at present
	 * @param name raw name
	 * @return edited name
	 */
	private String makeDictRef(String name) {
		return IUCR_PREFIX+S_COLON+name;
	}

	// of form _foo[]
	private String makeCategoryId(String name) {
		if (name != null) {
			if (!name.startsWith(S_UNDER)) {
				name = S_UNDER+name;
			}
			name = name.toLowerCase()+CIFCategory.SUFFIX;
		}
		return name;
	}

	private void processNonCMLItems(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			for (CIFItem item : block.getItemList()) {
				// omit CML
				CIFCategory category = getCMLCategory(item.getName());
				if (category != null) {
					continue;
				}
				addItem((CMLElement)cml0, item);
			}
		}
	}

	private void processNonCMLLoops(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			for (CIFLoop loop : block.getLoopList()) {
				// omit CML
				CIFCategory cmlCategory = getCMLCategory(loop.getNameList());
				if (cmlCategory != null) {
					continue;
				}
				String name = loop.getNameList().get(0);
				String categoryName = getCategoryFromDictionary(name);
				addLoop(cml0, loop, categoryName);
			}
		}
	}


	CMLEntry getEntry(String name) {
		CMLEntry entry = (dictionary == null) ? null : 
			dictionary.getCMLEntry(name.toLowerCase());
		return entry;
	}

	String getCategoryFromDictionary(String id) {
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
	private void processCMLItems(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			cellParams = null;
			//crystal = null;
			for (CIFItem item : block.getItemList()) {
				String name = item.getName();
				CIFCategory category = getCMLCategory(name);
				if (category != null) {
					String categoryName = category.getName();
					if (categoryName.equals("cell")) {
						if (crystal == null) {
							crystal = new CMLCrystal();
						}
						if (cellParams == null) {
							cellParams = new ArrayList<CMLScalar>();
							for (int i = 0; i < 6; i++) {
								cellParams.add((CMLScalar) null);
							}
						}
						addCell(item);
					} else if (categoryName.equals("chemical_name")) {
						addChemicalName(item, cml0);
					} else if (categoryName.equals("chemical_formula")) {
						try {
							addChemicalFormula(item, cml0);
						} catch (RuntimeException e) {
							runtimeException("JUMBO cannot parse formula: ", e);
						}
					} else if (categoryName.equals("atom_sites_solution")) {
						addAtomSitesSolution(item, cml0);
					} else {
						warn("Unprocessed CML item: "+name);
					}
				}
			}
		}
	}
	private void addCell(CIFItem item) {
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
			CMLScalar scalar = makeScalar(item, NUMERIC);
			if (scalar != null) {
				cellParams.set(idx, scalar);
			}
		} else {
			idx = CIFUtil.indexOf(name, names1, true);
			// formula units
			if (idx == 0) {
				item.processSu(false);
				Double z = item.getNumericValue();
				if (z == null) {
					if (!skipErrors) {
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
//				item.processSu(true);
//				addItem((CMLElement)crystal, item);
			} else if (idx == 2) {
				spaceGroup = item.getValue();
				if (symmetry != null) {
					symmetry.setSpaceGroup(spaceGroup);
				}
			} else {
				warn("Unknown cell name: "+name);
				addItem((CMLElement)crystal, item);
			}
		}
	}
	
	private CMLScalar makeScalar(CIFItem item, boolean numeric) {
		CMLScalar scalar = null;
		String value = item.getValue();
		if (CIFUtil.isIndeterminateValue(value)) {
			// omit "?"
		} else if (CIFUtil.isDefault(value)) {
			// omit "."
		} else {
			scalar = new CMLScalar();
			String name = item.getName();
			scalar.setDictRef(makeDictRef(name));
			if (numeric) {
				item.processSu(false);
				Double d = item.getNumericValue();
				if (d != null) {
					scalar.setValue(d.doubleValue());
					scalar.setErrorValue(item.getSu());
				} else {
					if (checkDoubles && !skipErrors) {
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
	private CMLScalar makeScalar(String dictRef, String value) {
		CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(makeDictRef(dictRef));
		scalar.setValue(value);
		return scalar;
	}

	private CMLScalar makeScalar(String dictRef, double value) {
		CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(makeDictRef(dictRef));
		scalar.setValue(value);
		return scalar;
	}
	private void processCell() {
		if (cellParams != null) {
			for (CMLScalar cellParam : cellParams) {
				if (cellParam == null) {
					String message = "Should have 6 cell parameters";
			    	this.getConverterLog().addToLog(Level.ERROR, message);
					if (skipErrors) {
					} else {
						runtimeException(message);
					}
				}
			}
			for (CMLScalar cellParam : cellParams) {
				if (cellParam != null) {
					crystal.appendChild(cellParam);
				}
			}
		}
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
		CMLScalar scalar = makeScalar(item, NON_NUMERIC);
		if (scalar != null) {
			cml0.appendChild(scalar);
		}
	}
	private void addChemicalFormula(CIFItem item, CMLCml cml0) {
		String[] names = {
				"_chemical_formula_moiety",
				"_chemical_formula_sum",
				"_chemical_formula_iupac",
				"_chemical_formula_structural",
				//
				"_chemical_formula_weight",
		};
		formula = null;
		String name = item.getName();
		String value = item.getValue();
		if (!CIFUtil.isIndeterminateValue(value)) {
			int idx = CIFUtil.indexOf(name, names, true);
			// sets params in correct order
			if (idx == -1) {
				// not a formula
			} else if (idx == 0) {
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
				cml0.appendChild(formula);
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
				cml0.appendChild(formula);
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
				cml0.appendChild(formula);
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
				cml0.appendChild(formula);
			} else if (idx == 4) {
				CMLScalar scalar = makeScalar(item, NUMERIC);
				if (scalar != null) {
					cml0.appendChild(scalar);
				}
			}
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
		CMLScalar scalar = makeScalar(item, NON_NUMERIC);
		if (scalar != null) {
			cml0.appendChild(scalar);
		}
	}
	
	private void processCMLLoops(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			for (CIFLoop loop : block.getLoopList()) {
				int deletedColumns = deleteDefaultAndIndeterminates(loop);
				List<String> nameList = loop.getNameList();
				CIFCategory category = getCMLCategory(nameList);
				if (category != null && deletedColumns < nameList.size()) {
//					String categoryName = category.getName();
					if (category.equals(CIFCategory.SYMMETRY_EQUIV)) {
						addSymmetry(loop);
					} else if (category.equals(CIFCategory.ATOM_SITE_ANISO)) {
						addAtomSiteAniso(loop, cml0);
					} else if (category.equals(CIFCategory.ATOM_SITE)) {
						addAtomSite(loop, cml0);
					} else if (category.equals(CIFCategory.ATOM_TYPE)) {
						addAtomType(loop, cml0);
					} else if (category.equals(CIFCategory.GEOM_BOND)) {
						addGeomBond(loop, cml0);
					} else if (category.equals(CIFCategory.GEOM_ANGLE)) {
						addGeomAngle(loop, cml0);
					} else if (category.equals(CIFCategory.GEOM_TORSION)) {
						addGeomTorsion(loop, cml0);
					} else {
						String message = "Unknown loopable category: "+category.getName();
						runtimeException(message);
					}
				}
			}
		}
	}

	private void createSymmetryFromOperators(CIFLoop loop) {
		String[] names =  {
				"_symmetry_equiv_pos_as_xyz",
				"_symmetry_equiv_pos_site_id",
		};
		checkLoop(loop, names, 0, "symmetry_equiv");
		List<String> operators = loop.getColumnValues(names[0]);
		if (operators == null) {
			throw new RuntimeException("no "+names[0]);
		}

		symmetry = null;

		symmetry = CMLSymmetry.createFromXYZStrings(operators);
		symmetry.normalizeCrystallographically();
	}

	private void addSymmetry(CIFLoop loop) {
		if (symmetry == null) {
			createSymmetryFromOperators(loop);
		}
		crystal.addSymmetry(symmetry);
		if (spaceGroup != null) {
			symmetry.setSpaceGroup(spaceGroup);
		}
	}

	private void addAtomSiteAniso(CIFLoop loop, CMLCml cml0) {
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
		cml0.appendChild(table);
	}

	private void addAtomSite(CIFLoop loop, CMLCml cml0) {
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
		molecule = new CMLMolecule();
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
				if (trimElements) {
					String symbol0 = symbol;
					while(symbol.endsWith(S_MINUS)) {
						symbol = symbol.substring(0, symbol.length()-1);
					}
					while(symbol.endsWith(S_PLUS)) {
						symbol = symbol.substring(0, symbol.length()-1);
					}
					while(Character.isDigit(symbol.charAt(symbol.length()-1))) {
						symbol = symbol.substring(0, symbol.length()-1);
					}
					if (!symbol0.equals(symbol)) {
//						LOG.debug("Trimmed element "+symbol0+" to "+symbol);
					}
		// some systems use lowercase elements
					if (Character.isLowerCase(symbol.charAt(0))) {
						symbol = symbol.substring(0, 1).toUpperCase()+symbol.substring(1);
					}
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
				atom.appendChild(makeScalar(AS_UISO, u[i]));
			}
			if (adp != null && !CIFUtil.isIndeterminateValue(adp.get(i))) {
				atom.appendChild(makeScalar(AS_ADP, adp.get(i)));
			}
			// calc flag. omit default "d" (calculated from diffraction data)
			// only add if "c" or "calc" and expand to "calc"
			if (calc != null) {
				if (calc.get(i).equals("") ||
						calc.get(i).equals("calc")) {
					atom.appendChild(makeScalar(AS_CALC, "calc"));
				}
			}
			if (disass != null && !CIFUtil.isIndeterminateValue(disass.get(i))) {
				atom.appendChild(makeScalar(AS_DISASS, disass.get(i)));
			}
			if (disgrp != null && !CIFUtil.isIndeterminateValue(disgrp.get(i))) {
				atom.appendChild(makeScalar(AS_DISGRP, disgrp.get(i)));
			}
			if (label != null && !CIFUtil.isIndeterminateValue(label.get(i))) {
				atom.appendChild(makeScalar(AS_LABEL, label.get(i)));
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
				atom.appendChild(makeScalar(AS_REF, ref.get(i)));
			}
			i++;
		}
		cml0.appendChild(molecule);
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

	private void addAtomType(CIFLoop loop, CMLCml cml0) {
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
		cml0.appendChild(table);
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
	private void addGeomBond(CIFLoop loop, CMLCml cml0) {
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
		cml0.appendChild(table);
	}
	private void addGeomAngle(CIFLoop loop, CMLCml cml0) {
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
		cml0.appendChild(table);
	}
	private void addGeomTorsion(CIFLoop loop, CMLCml cml0) {
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
		cml0.appendChild(table);
	}
	

	@Override
	public int getConverterVersion() {
		return 0;
	}

}



