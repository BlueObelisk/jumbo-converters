package org.xmlcml.cml.converters.cif;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLSymmetry;


/** stores and analyzes spacegroup information
 * 
 * @author pm286
 *
 */
public class SpaceGroupTool implements CMLConstants {
	private static Logger LOG = Logger.getLogger(SpaceGroupTool.class);

	// names associated with given symmetry. Indexed by each name
	// however also has additional symmetries which are associated 
	// with names. Normally there will be several names and only one symm
	// but different setings can cause problems. This is an unavoidable mess
	// cmlList
	//    name
	//    name
	//    ...
	//    symma
	//    symmb
	private Map<String, CMLList> nameMap = null;
	private List<CMLSymmetry> allSymmetries = null;

	private String filename;
	private CMLCml cml;

	private boolean update = false;

	/** constructor.
	 * 
	 * @param filename
	 * @param update
	 */
	public SpaceGroupTool(String filename, boolean update) {
		this.filename = filename;
		this.update = update;
		init();
		try {
			this.cml = (CMLCml) new CMLBuilder().build(new FileReader(filename)).getRootElement();
			Elements symmetryList = cml.getChildCMLElements(CMLList.TAG);
			for (int i = 0; i < symmetryList.size(); i++) {
				CMLList cmlList = (CMLList) symmetryList.get(i);
				Elements names = cmlList.getChildCMLElements(CMLName.TAG);
				for (int k = 0; k < names.size(); k++) {
					CMLName name = (CMLName) names.get(k);
					String nameValue = name.getValue();
					if (nameMap.get(nameValue) != null) {
						//System.err.println("duplicate name: "+nameValue);
					}
					nameMap.put(nameValue, cmlList);
				}
				Elements symmetries = cmlList.getChildCMLElements(CMLSymmetry.TAG);
				for (int j = 0; j < symmetries.size(); j++) {
					CMLSymmetry symmetry = (CMLSymmetry) symmetries.get(j);
					allSymmetries.add(symmetry);
				}
			}
		} catch (IOException e) {
			if (!update) {
				throw new RuntimeException("Cannot read spacegroup file "+e);
			} else {
				// start new tree
				this.cml = new CMLCml();
			}
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception "+e);
		}
	}

	private void init() {
		nameMap = new HashMap<String, CMLList>();
		allSymmetries = new ArrayList<CMLSymmetry>();
	}

	/** lookup symmetry by spacegroup name
	 * use first stored symmetry at present
	 * @param spaceGroup
	 * @return first (and usually only) stored symmetry
	 */
	public CMLSymmetry getSymmetry(String spaceGroup) {
		CMLSymmetry symmetry = null;
		CMLList cmlList = nameMap.get(spaceGroup);
		if (cmlList != null) {
			Elements symmetries = cmlList.getChildCMLElements(CMLSymmetry.TAG);
			symmetry = (CMLSymmetry) symmetries.get(0);
		}
		return symmetry;
	}

	/** analyze.
	 * 
	 * @param symmetry
	 * @param hmName
	 * @param hallName
	 */
	public void analyze(CMLSymmetry symmetry, String hmName, String hallName) {
		if (symmetry == null) {
			//System.err.println("'symmetry' element is null, cannot analyze.");
			return;
		} else {
			CMLSymmetry symCopy = (CMLSymmetry)symmetry.copy();
			if (update && hmName != null) {
				hmName = hmName.trim();
				CMLList cmlList = nameMap.get(hmName);
				// name exists
				if (cmlList != null) {
					addIfNewSymmetry(hmName, cmlList, symCopy);
				} else {
					// new name
					// to save time, ADD very large groups without checking
					if (symCopy.getTransform3Elements().size() < 48) {
						if (!symCopy.isSpaceGroup()) {
							throw new RuntimeException("entry for "+hmName+" is not a group");
						}
					} 
					// check for synonyms
					CMLSymmetry existingSymmetry = null;
					for (CMLSymmetry sym : allSymmetries) {
						if (sym.isEqualTo(symCopy, 0.0001)) {
							existingSymmetry = sym;
							break;
						}
					}
					CMLName name = new CMLName();
					name.setXMLContent(hmName);
					if (existingSymmetry != null) {
						CMLList oldList = (CMLList) existingSymmetry.getParent();
						oldList.appendChild(name);
						LOG.debug("synonym "+hmName+" ... "+oldList.getId());
						// add synonym
						nameMap.put(hmName, oldList);
					} else {
						CMLList newList = new CMLList();
						newList.setId(createId(hmName));
						newList.appendChild(name);
						newList.appendChild(symCopy);
						allSymmetries.add(symCopy);
						nameMap.put(hmName, newList);
						cml.appendChild(newList);
					}
					LOG.debug("added: "+hmName);
				}
			}
		}
	}

	private void addIfNewSymmetry(String sgname, CMLList cmlList, CMLSymmetry symmetry) {
		Elements symmetryElements = cmlList.getChildCMLElements(CMLSymmetry.TAG);
		boolean equals = false;
		for (int i = 0; i < symmetryElements.size(); i++) {
			CMLSymmetry symmetryx = (CMLSymmetry) symmetryElements.get(i);
			if (symmetryx.isEqualTo(symmetry, 0.001)) {
				equals = true;
				break;
			}
		}
		if (!equals) {
			if (!symmetry.isSpaceGroup()) {
				throw new RuntimeException("entry for "+sgname+" is not a spacegroup");
			} else {
				cmlList.appendChild(symmetry);
				allSymmetries.add(symmetry);
				nameMap.put(sgname, cmlList);
			}
			LOG.debug("added new symmetry: "+sgname);
		}
	}

	private static String createId(String id) {
		id = id.replace(S_SPACE, S_EMPTY);
		return id.toLowerCase();
	}

	/** update.
	 */
	public void update() {
		/**-
		List<CMLElement> lists = cml.getChildCMLElements();
		for (int i = 0; i < lists.size(); i++) {
			CMLList listi = (CMLList) lists.get(i);
			String idi = listi.getId();
			System.err.println("..."+idi);
			List<CMLElement> symmis = listi.getChildCMLElements();
			if (symmis.size() > 1) {
				checkMultipleSymmetries(idi, symmis);
			}
			// check synonyms
			boolean checkSynonyms = false;
			if (checkSynonyms) {
				for (CMLElement symmi : symmis) {
					CMLSymmetry symmix = (CMLSymmetry) symmi;
					for (int j = i+1; j < lists.size(); j++) {
						CMLList listj = (CMLList) lists.get(j);
						String idj = listj.getId();
						List<CMLElement> symmjs = listj.getChildCMLElements();
						for (CMLElement symmj : symmjs) {
							CMLSymmetry symmjx = (CMLSymmetry) symmj;
							if (symmix.isEqualTo(symmjx, 0.0001)) {
								System.err.println("Probable synonym for spacegroup: "+idi+"..."+idj);
							}
						}
					}
				}
			}
		}
		--*/
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			int indent = 2;
			cml.debug(fos, indent);
			fos.close();
		} catch (IOException ioe) {
			throw new RuntimeException("Unexpected ioerror: "+ioe);
		}

	}

//	private void checkMultipleSymmetries(String sgname, List<CMLElement> symmetries) {
//	for (int i = 0; i < symmetries.size(); i++) {
//	CMLSymmetry symmi = (CMLSymmetry) symmetries.get(i);
//	int nsymmi = symmi.getTransform3Elements().size();
//	if (!symmi.isSpaceGroup()) {
////	throw new RuntimeException("element "+i+" in "+sgname+" is not a group");
//	System.err.println("element "+i+" in "+sgname+" is not a space group");
//	}
//	for (int j = i+1; j < symmetries.size(); j++) {
//	CMLSymmetry symmj = (CMLSymmetry) symmetries.get(j);
//	int nsymmj = symmj.getTransform3Elements().size();
//	if (nsymmi != nsymmj) {
////	throw new RuntimeException("symmetries of different sizes: "+
////	sgname+" ... "+i+S_SLASH+nsymmi+"  "+j+S_SLASH+nsymmj);
//	System.err.println("symmetries of different sizes: "+
//	sgname+" ... "+i+S_SLASH+nsymmi+"  "+j+S_SLASH+nsymmj);
//	}
//	}
//	}
//	}
}

