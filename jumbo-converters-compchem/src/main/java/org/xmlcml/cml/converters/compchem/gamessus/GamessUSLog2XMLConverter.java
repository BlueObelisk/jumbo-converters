package org.xmlcml.cml.converters.compchem.gamessus;





import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.text.ChunkerMarker;
import org.xmlcml.cml.converters.text.Text2XMLConverter;

public class GamessUSLog2XMLConverter extends Text2XMLConverter {
	
	private static final String ENTER_LINK = "enterLink";
	private static final String GROUP1 = "group1";
	private static final String LEAVE_LINK = "leaveLink";

	public GamessUSLog2XMLConverter() {
	}
	
	public String getMarkerResourceName() {
		return "org/xmlcml/cml/converters/compchem/gaussian/log/marker1.xml";
	}
	
	public Element processIntoBlocks(Element element) {
		Element newElement = element;
		Nodes scalarNodes = element.query("./*[local-name()='scalar']");
		List<Element> scalarList = new ArrayList<Element>();
		for (int i = 0; i < scalarNodes.size(); i++) {
			Element scalar = (Element)scalarNodes.get(i);
			scalarList.add(scalar);
		}
		for (int i = 0; i < scalarList.size(); i++) {
			Element scalar = scalarList.get(i);
			if (LEAVE_LINK.equals(scalar.getAttributeValue(ChunkerMarker.MARK))) {
				if (i > 0) {
					String link = scalar.getAttributeValue(GROUP1);
					Element previousScalar = scalarList.get(i-1);
					String previousLink = previousScalar.getAttributeValue(GROUP1);
					if (ENTER_LINK.equals(previousScalar.getAttributeValue(ChunkerMarker.MARK))) {
						if (link.equals(previousLink)) {
							scalar.detach();
						}
					}
				}
			}
		}
		return newElement;
	}
}
