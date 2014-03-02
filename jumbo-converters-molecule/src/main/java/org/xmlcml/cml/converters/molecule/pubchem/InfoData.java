package org.xmlcml.cml.converters.molecule.pubchem;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;

public class InfoData {
	private Element urn;
	private CMLMolecule molecule;
	private String label;
	private String name;
	private String dataType;
	private String implementation;
	private String software;
	private String source;
	private String release;
	private String version;
	private String dataValue;
	private String parameters;
	/**
	 * 
	 */
	public InfoData(Element infoData, CMLMolecule molecule) {
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Compound</PC-Urn_label>
          <PC-Urn_name>Canonicalized</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="uint">5</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_release>2007.06.29</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_ival>1</PC-InfoData_value_ival>
      </PC-InfoData_value>
    </PC-InfoData>
*/
		this.molecule = molecule;
		Element infoDataUrn = CMLUtil.getSingleElement(infoData, "p:PC-InfoData_urn", PubchemUtils.NIH_XPATH);
		Element infoDataValue = CMLUtil.getSingleElement(infoData, "p:PC-InfoData_value", PubchemUtils.NIH_XPATH);
		Element unknown = CMLUtil.getSingleElement(infoData, "*[not(" +
			"local-name()='PC-InfoData_urn' or"+
			"local-name()='PC-InfoData_value'"+
			")]", PubchemUtils.NIH_XPATH);
		if (infoDataUrn == null || unknown != null) {
			throw new RuntimeException("bad children of urn");
		}
		processInfoDataUrn(infoDataUrn, infoDataValue);
	}
	
	private void processInfoDataUrn(Element infoDataUrn, Element infoDataValue) {
		urn = CMLUtil.getSingleElement(
				infoDataUrn, "p:PC-Urn", PubchemUtils.NIH_XPATH);
		Element unknown = CMLUtil.getSingleElement(infoDataUrn, "*[not(" +
			"local-name()='PC-Urn'"+
			")]", PubchemUtils.NIH_XPATH);
		if (urn == null || unknown != null) {
			throw new RuntimeException("bad children of InfoDataUrn");
		}
		label = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_label", PubchemUtils.NIH_XPATH);
		name = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_name", PubchemUtils.NIH_XPATH);
		dataType = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_datatype/p:PC-UrnDataType/@value", PubchemUtils.NIH_XPATH);
		implementation = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_implementation", PubchemUtils.NIH_XPATH);
		software = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_software", PubchemUtils.NIH_XPATH);
		source = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_source", PubchemUtils.NIH_XPATH);
		release = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_release", PubchemUtils.NIH_XPATH);
		version = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_version", PubchemUtils.NIH_XPATH);
		parameters = CMLUtil.getSingleValue(
				urn, "p:PC-Urn_parameters", PubchemUtils.NIH_XPATH);
		if (label == null) {
			throw new RuntimeException("no label in urn");
		}
		// apparently not mandatory
//		if (name == null) {
//			CMLUtil.debug(urn);
//			throw new RuntimeException("no name in urn");
//		}
		PubchemUtils.checkUnknowns(urn, new String[] {
			"PC-Urn_label",
			"PC-Urn_name",
			"PC-Urn_datatype",
			"PC-Urn_implementation",
			"PC-Urn_software",
			"PC-Urn_source",
			"PC-Urn_release",
			"PC-Urn_version",
			"PC-Urn_parameters",
		});
		dataValue = CMLUtil.getSingleValue(infoDataValue, "*", PubchemUtils.NIH_XPATH);
		CMLFormula formula = null;
		if ("E_INCHI".equals(implementation)) {
			formula = new CMLFormula();
			formula.setConvention("pubchem:Inchi");
			molecule.addFormula(formula);
		} else if ("Molecular Formula".equals(label)) {
			formula = new CMLFormula();
			formula.setConvention("pubchem:formula");
		} else if ("SMILES".equals(label)) {
			formula = new CMLFormula();
			if ("Canonical".equals(name)) {
				formula.setConvention("pubchem:CanonicalSmiles");
			} else if ("Isomeric".equals(name)) {
				formula.setConvention("pubchem:IsomericSmiles");
			}
		}
		if (formula != null) {
			molecule.addFormula(formula);
			formula.setInline(dataValue);
		} else {
			CMLProperty property = new CMLProperty();
			molecule.appendChild(property);
			CMLScalar scalar = createScalar();
			property.addScalar(scalar);
//			if (implementation == null) {
			if (implementation != null) {
				if (implementation.startsWith("E_")) {
					property.setDictRef("pubchem:"+implementation);
				} else {
					throw new RuntimeException("implementation: "+implementation);
				}
			} else if ("Canonicalized".equals(name)) {
				property.setDictRef("pubchem:canonical");
			} else if ("Mass".equals(label) && "Exact".equals(name)) {
				property.setDictRef("pubchem:exactMass");
			} else if ("Weight".equals(label) && "MonoIsotopic".equals(name)) {
				property.setDictRef("pubchem:monoisotopic");
			} else if ("Molecular Weight".equals(label)) {
				property.setDictRef("pubchem:molecularWeight");
			} else {
				property.setDictRef("pubchem:unknown");
				addAttribute(property, label, "label");
				addAttribute(property, name, "name");
				addAttribute(property, implementation, "implementation");
				addAttribute(property, software, "software");
				addAttribute(property, source, "source");
				addAttribute(property, release, "release");
				addAttribute(property, version, "version");
				addAttribute(property, parameters, "parameters");
			}
		}
	}

	/**
	 * @return
	 * @throws RuntimeException
	 */
	private CMLScalar createScalar() throws RuntimeException {
		CMLScalar scalar = null;
		if (dataType == null) {
			throw new RuntimeException("no dataType in urn");
		} else if (dataType.equals("double") ||
				dataType.equals("float")) {
			try {
				Double d = new Double(dataValue);
				scalar = new CMLScalar(d.doubleValue());
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("Cannot parse double: "+dataValue);
			}
		} else if (dataType.equals("uint")) {
			try {
				Integer ii = new Integer(dataValue);
				scalar = new CMLScalar(ii.intValue());
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("Cannot parse int: "+dataValue);
			}
		} else if (dataType.equals("fingerprint")) {
			// treat as string
			scalar = new CMLScalar(dataValue);
		} else if (dataType.equals("doublevec")) {
			// treat as string until we understand
			scalar = new CMLScalar(dataValue);
		} else if (dataType.equals("string")) {
			scalar = new CMLScalar(dataValue);
		} else {
			throw new RuntimeException("Unknown dataType "+dataType);
		}
		return scalar;
	}

	private static void addAttribute(Element element, String value, String name) {
		if (value != null) {
			Attribute att = new Attribute("pubchem:"+name, PubchemUtils.NIH_NS, value);
			element.addAttribute(att);
		}
	}
			
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Compound Complexity</PC-Urn_label>
          <PC-Urn_datatype>
            <PC-UrnDataType value="double">7</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_implementation>E_COMPLEXITY</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_fval>606</PC-InfoData_value_fval>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Count</PC-Urn_label>
          <PC-Urn_name>Hydrogen Bond Acceptor</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="uint">5</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_implementation>E_NHACCEPTORS</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_ival>3</PC-InfoData_value_ival>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Count</PC-Urn_label>
          <PC-Urn_name>Hydrogen Bond Donor</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="uint">5</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_implementation>E_NHDONORS</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_ival>0</PC-InfoData_value_ival>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Count</PC-Urn_label>
          <PC-Urn_name>Rotatable Bond</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="uint">5</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_implementation>E_NROTBONDS</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_ival>2</PC-InfoData_value_ival>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Fingerprint</PC-Urn_label>
          <PC-Urn_name>SubStructure Keys</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="fingerprint">16</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_parameters>extended 2</PC-Urn_parameters>
          <PC-Urn_implementation>E_SCREEN</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_binary>00000371E0783000000000000000000000000000000180000000326080000000000060C80000001A00000000000F14A080020208000004008802A0D2080000000020000000080100004800001200010002000004800008010388C8F08F8000000000000000800004000020000080000C000000</PC-InfoData_value_binary>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>IUPAC Name</PC-Urn_label>
          <PC-Urn_name>Allowed</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.6.0</PC-Urn_version>
          <PC-Urn_software>LexiChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>[(8R,9S,10R,13S,14S,17S)-10,13-dimethyl-3-oxo-1,2,6,7,8,9,11,12,14,15,16,17-dodecahydrocyclopenta[a]phenanthren-17-yl] acetate</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>IUPAC Name</PC-Urn_label>
          <PC-Urn_name>CAS-like Style</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.6.0</PC-Urn_version>
          <PC-Urn_software>LexiChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>acetic acid [(8R,9S,10R,13S,14S,17S)-10,13-dimethyl-3-oxo-1,2,6,7,8,9,11,12,14,15,16,17-dodecahydrocyclopenta[a]phenanthren-17-yl] ester</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>IUPAC Name</PC-Urn_label>
          <PC-Urn_name>Preferred</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.6.0</PC-Urn_version>
          <PC-Urn_software>LexiChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>[(8R,9S,10R,13S,14S,17S)-10,13-dimethyl-3-oxo-1,2,6,7,8,9,11,12,14,15,16,17-dodecahydrocyclopenta[a]phenanthren-17-yl] acetate</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>IUPAC Name</PC-Urn_label>
          <PC-Urn_name>Systematic</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.6.0</PC-Urn_version>
          <PC-Urn_software>LexiChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>[(8R,9S,10R,13S,14S,17S)-10,13-dimethyl-3-oxo-1,2,6,7,8,9,11,12,14,15,16,17-dodecahydrocyclopenta[a]phenanthren-17-yl] ethanoate</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>IUPAC Name</PC-Urn_label>
          <PC-Urn_name>Traditional</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.6.0</PC-Urn_version>
          <PC-Urn_software>LexiChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>acetic acid [(8R,9S,10R,13S,14S,17S)-3-keto-10,13-dimethyl-1,2,6,7,8,9,11,12,14,15,16,17-dodecahydrocyclopenta[a]phenanthren-17-yl] ester</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>InChI</PC-Urn_label>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_parameters>options {auxnonr donotaddh w0 fixedh recmet newps}</PC-Urn_parameters>
          <PC-Urn_implementation>E_INCHI</PC-Urn_implementation>
          <PC-Urn_version>1.0.1</PC-Urn_version>
          <PC-Urn_software>InChI</PC-Urn_software>
          <PC-Urn_source>nist.gov</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>InChI=1/C21H30O3/c1-13(22)24-19-7-6-17-16-5-4-14-12-15(23)8-10-20(14,2)18(16)9-11-21(17,19)3/h12,16-19H,4-11H2,1-3H3/t16-,17-,18-,19-,20-,21-/m0/s1</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Log P</PC-Urn_label>
          <PC-Urn_name>XLogP2</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="double">7</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_implementation>E_XLOGP2</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_fval>4.3</PC-InfoData_value_fval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Mass</PC-Urn_label>
          <PC-Urn_name>Exact</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="double">7</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>2.1</PC-Urn_version>
          <PC-Urn_software>PubChem</PC-Urn_software>
          <PC-Urn_source>ncbi.nlm.nih.gov</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_fval>330.219495</PC-InfoData_value_fval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Molecular Formula</PC-Urn_label>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>2.1</PC-Urn_version>
          <PC-Urn_software>PubChem</PC-Urn_software>
          <PC-Urn_source>ncbi.nlm.nih.gov</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>C21H30O3</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**		
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Molecular Weight</PC-Urn_label>
          <PC-Urn_datatype>
            <PC-UrnDataType value="double">7</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>2.1</PC-Urn_version>
          <PC-Urn_software>PubChem</PC-Urn_software>
          <PC-Urn_source>ncbi.nlm.nih.gov</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_fval>330.4611</PC-InfoData_value_fval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>SMILES</PC-Urn_label>
          <PC-Urn_name>Canonical</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.5.1</PC-Urn_version>
          <PC-Urn_software>OEChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>CC(=O)OC1CCC2C1(CCC3C2CCC4=CC(=O)CCC34C)C</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>SMILES</PC-Urn_label>
          <PC-Urn_name>Isomeric</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="string">1</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>1.5.1</PC-Urn_version>
          <PC-Urn_software>OEChem</PC-Urn_software>
          <PC-Urn_source>openeye.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_sval>CC(=O)O[C@H]1CC[C@@H]2[C@@]1(CC[C@H]3[C@H]2CCC4=CC(=O)CC[C@]34C)C</PC-InfoData_value_sval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Topological</PC-Urn_label>
          <PC-Urn_name>Polar Surface Area</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="double">7</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_implementation>E_TPSA</PC-Urn_implementation>
          <PC-Urn_version>3.347</PC-Urn_version>
          <PC-Urn_software>Cactvs</PC-Urn_software>
          <PC-Urn_source>xemistry.com</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_fval>43.4</PC-InfoData_value_fval>
      </PC-InfoData_value>
    </PC-InfoData>
*/
/**
    <PC-InfoData>
      <PC-InfoData_urn>
        <PC-Urn>
          <PC-Urn_label>Weight</PC-Urn_label>
          <PC-Urn_name>MonoIsotopic</PC-Urn_name>
          <PC-Urn_datatype>
            <PC-UrnDataType value="double">7</PC-UrnDataType>
          </PC-Urn_datatype>
          <PC-Urn_version>2.1</PC-Urn_version>
          <PC-Urn_software>PubChem</PC-Urn_software>
          <PC-Urn_source>ncbi.nlm.nih.gov</PC-Urn_source>
          <PC-Urn_release>2007.09.04</PC-Urn_release>
        </PC-Urn>
      </PC-InfoData_urn>
      <PC-InfoData_value>
        <PC-InfoData_value_fval>330.219495</PC-InfoData_value_fval>
      </PC-InfoData_value>
    </PC-InfoData>
  </PC-Compound_props>
*/
}
