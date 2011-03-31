package org.xmlcml.cml.converters.marker;

import java.util.List;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLModule;

public class LegacyStore {

	// module holding the lines to be matched
	private CMLModule legacyModule;
	private int originOfParse;
	private int pointer;
	private List<CMLElement> elementList;
	
	public int getPointer() {
		return pointer;
	}

	public void setPointer(int pointer) {
		this.pointer = pointer;
	}

	public LegacyStore(CMLModule moduleToParse) {
		this.legacyModule = moduleToParse;
		setModule(legacyModule);
	}
	
	public CMLModule getModule() {
		return legacyModule;
	}

	public void setModule(CMLModule module) {
		this.legacyModule = module;
		recreateChildElementListAndResetPointer();
		this.originOfParse = 0;
		this.pointer = 0;
	}

	void recreateChildElementListAndResetPointer() {
		CMLElement currentElement = this.getCurrentElement();
		elementList = legacyModule.getChildCMLElements();
		// reset pointer as elementList may have contracted or expanded
		if (currentElement != null) {
			pointer = legacyModule.indexOf(currentElement);
		}
	}

	public int getOriginOfParse() {
		return originOfParse;
	}

	public void setOrigin(int origin) {
		if (origin < 0 || origin >= elementList.size()) {
			throw new RuntimeException("cannot set origin ("+origin+" must lie in 0-("+(elementList.size()-1)+")");
		}
		this.originOfParse = origin;
		this.pointer = origin;
	}
	
	public CMLElement getCurrentElement() {
		CMLElement currentElement = null;
		if (elementList != null) {
			currentElement = (pointer < 0 || pointer >= elementList.size()) ?
					null : elementList.get(pointer);
		}
		return currentElement;
	}

	/**
	 * increments pointer ands returns element
	 * @return value of incremented element or null
	 */
    CMLElement incrementLegacyElement() {
		incrementPointer();
		return (pointer < elementList.size()) ? elementList.get(pointer) : null;
	}
	
	boolean hasCurrentLegacyElement() {
		return pointer >= 0 && pointer <= elementList.size()-1;
	}
	
	boolean canIncrementPointer() {
		return pointer < elementList.size()-1;
	}
	/**
	 * decrements pointer and returns element
	 * @return value of decremented pointer or null
	 */
	public CMLElement decrementLegacyElement() {
		decrementPointer();
		return (pointer < 0) ? null : elementList.get(pointer);
	}
	
	/**
	 * if pointer < elementList.size() - 1 increments pointer
	 * else leaves pointer = elementList.size()
	 * @return value of incremented pointer
	 */
	private int incrementPointer() {
		if (pointer < elementList.size()) {
			pointer++;
		}
		return pointer;
	}
	
	/**
	 * if pointer > 0 decrements pointer
	 * else leaves pointer = 0
	 * @return value of decremented pointer
	 */
	public int decrementPointer() {
		if (pointer > 0) {
			pointer--;
		}
		return pointer;
	}
	
	public void packageMatches() {
		
	}

	public CMLElement getCMLElement(int j) {
		return (j < 0 || j >= elementList.size()) ? null : elementList.get(j);
	}

	public CMLElement getNextLegacyElement() {
		CMLElement element = null;
		if (canIncrementPointer()) {
			element = incrementLegacyElement();
		}
		return element;
	}
	
	public CMLElement getCurrentLegacyElement() {
		return (pointer >= 0 && pointer < elementList.size()) ? elementList.get(pointer) : null;
	}


	/**
	 * increments pointer to point beyond current position
	 * even if no more elements;
	 */
	public void forceIncrementPointer() {
		pointer++;
	}

	public String toString() {
		return legacyModule.toXML();
	}

}
