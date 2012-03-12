package org.xmlcml.cml.converters.graphics.svg.fromsvg;

import org.xmlcml.cml.converters.graphics.svg.elements.SVGChemLine.StereoBondType;

public interface HasStereoBondType {

	void setStereoBondType(StereoBondType type);
	
	StereoBondType getStereoBondType();
}
