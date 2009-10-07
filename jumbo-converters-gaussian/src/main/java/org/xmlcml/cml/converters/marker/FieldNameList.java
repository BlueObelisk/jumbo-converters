package org.xmlcml.cml.converters.marker;

import java.util.ArrayList;
import java.util.List;

public class FieldNameList {
	public int size() {
		return nameList.size();
	}

	private List<String> nameList;
	public FieldNameList() {
		nameList = new ArrayList<String>();
	}
	
	public boolean add(String e) {
		return nameList.add(e);
	}

	public List<String> getNames() {
		return nameList;
	}
}
