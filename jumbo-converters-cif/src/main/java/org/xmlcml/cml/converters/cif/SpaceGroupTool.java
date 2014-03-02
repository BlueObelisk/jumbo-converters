package org.xmlcml.cml.converters.cif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Elements;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLName;
import org.xmlcml.cml.element.CMLSymmetry;


/** 
 * Stores and analyzes spacegroup information.
 * 
 * @author pm286
 *
 */
public class SpaceGroupTool implements CMLConstants {
	private static Logger LOG = Logger.getLogger(SpaceGroupTool.class);

	/*
	 names associated with given symmetry. Indexed by each name
	 however also has additional symmetries which are associated 
	 with names. Normally there will be several names and only one symm
	 but different setings can cause problems. This is an unavoidable mess
	 cmlList
	    name
	    name
	    ...
	    symma
	    symmb
	 */
	private Map<String, CMLList> nameMap = new HashMap<String, CMLList>();
	private List<CMLSymmetry> allSymmetries = new ArrayList<CMLSymmetry>();

	private CMLCml cml;
	private boolean update = false;
	private File spaceGroupFile;

	public SpaceGroupTool(boolean update) {
		this(null, update);
	}

	/** constructor.
	 * 
	 * @param spaceGroupFile
	 * @param update
	 */
	public SpaceGroupTool(File spaceGroupFile, boolean update) {
		this.update = update;
		this.spaceGroupFile = spaceGroupFile;
	}
	
	public void setSpaceGroupFile(File spaceGroupFile) {
		this.spaceGroupFile = spaceGroupFile;
	}

	private void parseSpaceGroupFile() {
		InputStream is = null;
		try {
			if (spaceGroupFile == null) {
				is = getClass().getResourceAsStream("/space-groups.xml");
			} else {
				is = FileUtils.openInputStream(spaceGroupFile);
			}
			this.cml = (CMLCml) new CMLBuilder().build(is).getRootElement();
			Elements symmetryList = cml.getChildCMLElements(CMLList.TAG);
			for (int i = 0; i < symmetryList.size(); i++) {
				CMLList cmlList = (CMLList) symmetryList.get(i);
				Elements names = cmlList.getChildCMLElements(CMLName.TAG);
				for (int k = 0; k < names.size(); k++) {
					CMLName name = (CMLName) names.get(k);
					String nameValue = name.getValue();
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

	/** 
	 * lookup symmetry by spacegroup name
	 * use first stored symmetry at present
	 * 
	 * @param spaceGroup
	 * 
	 * @return first (and usually only) stored symmetry
	 */
	public CMLSymmetry getSymmetry(String spaceGroup) {
		if (cml == null) {
			parseSpaceGroupFile();
		}
		CMLSymmetry symmetry = null;
		CMLList cmlList = nameMap.get(spaceGroup);
		if (cmlList != null) {
			Elements symmetries = cmlList.getChildCMLElements(CMLSymmetry.TAG);
			symmetry = (CMLSymmetry) symmetries.get(0);
		}
		return symmetry;
	}

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

	public void update() {
		try {
			FileOutputStream fos = new FileOutputStream(spaceGroupFile);
			int indent = 2;
			cml.debug(fos, indent);
			fos.close();
		} catch (IOException ioe) {
			throw new RuntimeException("Unexpected ioerror: "+ioe);
		}
	}

}

