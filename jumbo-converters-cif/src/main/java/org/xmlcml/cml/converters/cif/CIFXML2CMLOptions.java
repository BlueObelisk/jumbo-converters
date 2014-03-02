package org.xmlcml.cml.converters.cif;

import java.io.File;

import org.apache.log4j.Logger;
import org.xmlcml.cif.CIFItem;
import org.xmlcml.cml.element.CMLDictionary;

/**
 * Helper class for CIFXML2CMLConverter to hold all the possible processing options.
 * 
 * @author ned24
 *
 */
public class CIFXML2CMLOptions {

	private final static Logger LOG = Logger.getLogger(CIFXML2CMLOptions.class);
	
	private static final String QUERY = "?";

	private SpaceGroupTool spaceGroupTool;

	private CMLDictionary dictionary;

	private boolean skipErrors;
	private boolean checkDoubles;
	private boolean omitIndeterminate;
	private boolean omitDefault;
	private boolean addMissingSymmetry;
	
	public CIFXML2CMLOptions() {
		setDefaults();
	}
	
	private void setDefaults() {
		skipErrors = false;
		checkDoubles = false;
		// omit "?" in CIFs 
		omitIndeterminate = true;
		// omit "." in CIFs
		omitDefault = true;
		spaceGroupTool = new SpaceGroupTool(false);
		addMissingSymmetry = true;
	}
	
	public SpaceGroupTool getSpaceGroupTool() {
		return spaceGroupTool;
	}

	public boolean isAddMissingSymmetry() {
		return addMissingSymmetry;
	}
	
	public CMLDictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(CMLDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void setSpaceGroupToolFile(File file) {
		spaceGroupTool.setSpaceGroupFile(file);
	}

	public boolean isSkipErrors() {
		return skipErrors;
	}

	public void setSkipErrors(boolean skipErrors) {
		this.skipErrors = skipErrors;
	}

	public boolean isCheckDoubles() {
		return checkDoubles;
	}

	public void setCheckDoubles(boolean checkDoubles) {
		this.checkDoubles = checkDoubles;
	}

	public boolean isOmitIndeterminate() {
		return omitIndeterminate;
	}

	public void setOmitIndeterminate(boolean omitIndeterminate) {
		this.omitIndeterminate = omitIndeterminate;
	}

	public boolean isOmitDefault() {
		return omitDefault;
	}

	public void setOmitDefault(boolean omitDefault) {
		this.omitDefault = omitDefault;
	}

	public boolean omitIndeterminate(CIFItem item) {
		boolean omit = false;
		if (item == null) {
			omit = true;
		} else {
			String value = item.getValue().trim();
			if (QUERY.equals(value) && omitIndeterminate) {
				omit = true;
				LOG.trace("omitted indeterminate item: "+item.getName());
			}
		}
		return omit;
	}
	
	

}
