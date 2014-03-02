package org.xmlcml.cml.converters.compchem.input;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * manages "keywords" and similar controls
 * @author pm286
 *
 */
public class CalculationComponents {
	private static final String CALCULATION_COMPONENTS = "calculationComponents";
	private static final String COMPONENT = "component";

	public enum CalculationComponentType {
		DFT,
		FREQUENCY,
		FUKUI,
		GEOM_OPTIMIZE,
		NMR_SHIFT,
		POPULATION_ANALYSIS,
		REACTION_COORDINATE,
		SINGLE_POINT,
		THERMOCHEMISTRY,
		TRANSITION_STATE,
		;
		
		public static CalculationComponentType get(String type) {
			for (CalculationComponentType tt : CalculationComponentType.values()) {
				if (tt.toString().equals(type)) {
					return tt;
				}
			}
			return null;
		}
	}
	
	Set<CalculationComponentType> calcTypeSet = new HashSet<CalculationComponentType>();
	private Element element;
	
	public CalculationComponents(InputStream inputStream) {
		try {
			this.element = new Builder().build(inputStream).getRootElement();
			if (!(element.getLocalName().equals(CALCULATION_COMPONENTS))) {
				throw new RuntimeException("CalculationComponent file corrupt");
			}
			Elements childElements = element.getChildElements(COMPONENT);
			for (int i = 0; i < childElements.size(); i++) {
				Element childElement = (Element) childElements.get(i);
				String type = childElement.getValue();
				CalculationComponentType cctype = CalculationComponentType.get(type);
				if (cctype != null) {
					addType(cctype);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/parse calculationComponents", e);
		}
	}
	
	public void addType(CalculationComponentType type) {
		calcTypeSet.add(type);
	}
	
	public void removeType(CalculationComponentType type) {
		calcTypeSet.remove(type);
	}
	
	public boolean has(String type) {
		CalculationComponentType cctype = CalculationComponentType.get(type);
		return calcTypeSet.contains(cctype);
	}
	
}
