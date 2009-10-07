package org.xmlcml.cml.converters.molecule.pubchem;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;

public class CompoundCount {

	private Element compoundCount =  null;
	private CMLMolecule molecule;
	
	/**
	 * 
	 */
	public CompoundCount(Element pcCompound, CMLMolecule molecule) {
		/**		
  <PC-Compound_count>
    <PC-Count>
      <PC-Count_heavy-atom>24</PC-Count_heavy-atom>
      <PC-Count_atom-chiral>6</PC-Count_atom-chiral>
      <PC-Count_atom-chiral-def>6</PC-Count_atom-chiral-def>
      <PC-Count_atom-chiral-undef>0</PC-Count_atom-chiral-undef>
      <PC-Count_bond-chiral>0</PC-Count_bond-chiral>
      <PC-Count_bond-chiral-def>0</PC-Count_bond-chiral-def>
      <PC-Count_bond-chiral-undef>0</PC-Count_bond-chiral-undef>
      <PC-Count_isotope-atom>0</PC-Count_isotope-atom>
      <PC-Count_covalent-unit>1</PC-Count_covalent-unit>
      <PC-Count_tautomers>5</PC-Count_tautomers>
    </PC-Count>
  </PC-Compound_count>
*/
		compoundCount = CMLUtil.getSingleElement(
				pcCompound, "p:PC-Compound_count", PubchemUtils.NIH_XPATH);
			this.molecule = molecule;
			addProperty("PC-Count_heavy-atom");
			addProperty("PC-Count_atom-chiral");
			addProperty("PC-Count_atom-chiral-def");
			addProperty("PC-Count_atom-chiral-undef");
			addProperty("PC-Count_bond-chiral");
			addProperty("PC-Count_bond-chiral-def");
			addProperty("PC-Count_bond-chiral-undef");
			addProperty("PC-Count_isotope-atom");
			addProperty("PC-Count_covalent-unit");
			addProperty("PC-Count_tautomers");
		
	}
	
	private void addProperty(String name) {
		String value = CMLUtil.getSingleValue(
				compoundCount, "p:PC-Count/p:"+name, PubchemUtils.NIH_XPATH);
		if (value != null) {
			try {
				int ii = new Integer(value).intValue();
				CMLProperty property = new CMLProperty();
				property.setDictRef("pubchem:"+name);
				CMLScalar scalar = new CMLScalar(ii);
				property.appendChild(scalar);
				molecule.appendChild(property);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("cannot parse integer "+value);
			}
		}
	}
	
//	private void addTo(CMLMolecule molecule) {
//	     heavyAtom = getInteger(compoundCount, "PC-Count_heavy-atom");
//	     atomChiral = getInteger(compoundCount, "PC-Count_atom-chiral");
//	     atomChiralDef = getInteger(compoundCount, "PC-Count_atom-chiral-def");
//	     atomChiralUndef = getInteger(compoundCount, "PC-Count_atom-chiral-undef");
//	     bondChiral = getInteger(compoundCount, "PC-Count_bond-chiral");
//	     bondChiralDef = getInteger(compoundCount, "PC-Count_bond-chiral-def");
//	     bondChiralUndef = getInteger(compoundCount, "PC-Count_bond-chiral-undef");
//	     isotopeAtom = getInteger(compoundCount, "PC-Count_isotope-atom");
//	     covalentUnit = getInteger(compoundCount, "PC-Count_covalent-unit");
//	     tautomers = getInteger(compoundCount, "PC-Count_tautomers");
//		
//	}

}
