package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

public class CaptureGroupList {
	private List<CaptureGroup> groupList;
	
	public CaptureGroupList() {
		groupList = new ArrayList<CaptureGroup>();
	}

	public boolean add(CaptureGroup e) {
		boolean b = groupList.add(e);
		return b;
	}

	public CaptureGroupList(List<CaptureGroup> captureGroupList) {
		this.setCaptureGroupList(captureGroupList);
	}
	
	public int size() {
		return getGroups().size();
	}

	void setCaptureGroupList(List<CaptureGroup> captureGroupList) {
		this.groupList = captureGroupList;
	}

	public List<CaptureGroup> getGroups() {
		return groupList;
	}

	 List<String> createDummyNameListAndAddNamesAttributeForSkippedRegex(Regex regex) {
		CaptureGroup.LOG.info("Generating dummy names for skipped element");
		List<String> nameList = new ArrayList<String>();
		String s = "";
		for (CaptureGroup group : getGroups()) {
			s += "dummy ";
			nameList.add(s);
		}
		s = s.trim();
		regex.addAttribute(new Attribute(Marker.NAMES, s));
		return nameList;
	}

	CaptureGroup get(int arg0) {
		return groupList.get(arg0);
	}

}
