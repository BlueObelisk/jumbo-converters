package org.xmlcml.cml.converters.metadata;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

/*
<regionList>
  <region id="camb">
    <name>Cambridgeshire</name>
    <name>Cambs</name>
  </region>
</regionList>
 */
public class Region {
	private String id;
	private List<String> nameList;
	
	
	private Region() {
	}
	
	static Region createRegion(Element regionElement) {
		Region region = new Region();
		region.id = regionElement.getAttributeValue("id");
		Nodes names = regionElement.query("./name");
		region.nameList = new ArrayList<String>();
		for (int i = 0; i < names.size(); i++) {
			region.nameList.add(names.get(i).getValue());
		}
		return region;
	}
	
//	public void addName(String name) {
//		ensureNameList();
//		nameList.add(name);
//	}
//	private void ensureNameList() {
//		if (nameList == null) {
//			nameList = new ArrayList<String>();
//		}
//	}
}
