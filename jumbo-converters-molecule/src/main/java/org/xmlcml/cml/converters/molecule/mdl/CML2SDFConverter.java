package org.xmlcml.cml.converters.molecule.mdl;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLSelector;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;

public class CML2SDFConverter extends AbstractConverter implements
		Converter {
	
	private static final Logger LOG = Logger.getLogger(CML2SDFConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
    /**
     * SD property fields
> <MELTING.POINT>
> 55 (MD-08974) <BOILING.POINT> DT12
> DT12 55
> (MD-0894) <BOILING.POINT> FROM ARCHIVES
     */
	public final static String V3_PROPERTY_1 = "> <";
	public final static String V3_PROPERTY_2 = ">";
    
    /* terminators
     * 
     */
	public final static String SDF_END    = "$$$$";
	
	public final static String[] typicalArgsForConverterCommand = {
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "sdf",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.CML2SDFConverter"
	};
	
	public final static String[] testArgs = {
		"-quiet",
		"-sd", "src/test/resources/cml",
		"-odir", "../temp",
		"-is", "cml",
		"-os", "mol",
		"-converter", "org.xmlcml.cml.converters.molecule.mdl.CML2MDLConverter"
	};
	
	public Type getInputType() {
		return Type.CML;
	}

	public Type getOutputType() {
		return Type.SDF;
	}

	private CMLElement cml;
	private MDLConverter mdlConverter;

	public MDLConverter getMdlConverter() {
		return mdlConverter;
	}

	public void setMdlConverter(MDLConverter mdlConverter) {
		this.mdlConverter = mdlConverter;
	}
	
	
	public CML2SDFConverter() {
		mdlConverter = new MDLConverter();
	}

	/**
	 * converts a CML object to MDL. assumes a single CMLMolecule as descendant
	 * of root
	 * 
	 * @param xml
	 */
	public List<String> convertToText(Element xml) {
		cml = CMLBuilder.ensureCML(xml);
		CMLMolecule molecule = new CMLSelector(cml).getToplevelMoleculeDescendant(true);
		List<String> lines = new ArrayList<String>();
		if (molecule != null) {
			writeMoleculeAndProperties(molecule, lines);
		}
		return lines;
	}

	private void writeMoleculeAndProperties(CMLMolecule molecule,
			List<String> lines) {
		mdlConverter.writeMOL(lines, molecule);
		List<CMLProperty> propertyList = getPropertyList();
		writeSDFFields(propertyList, lines);
		writeSDFEND(lines);
	}
	
	protected List<CMLProperty> getPropertyList() {
		List<CMLProperty> propertyList = new ArrayList<CMLProperty>();
		CMLProperty property = new CMLProperty();
		property.setRole("grot");
		property.addScalar(new CMLScalar(10.3));
		property = new CMLProperty();
		property.setRole("junk");
		property.addScalar(new CMLScalar("test"));
		return propertyList;
	}
	
	/** override by specialised SDF writer
	 * 
	 */
	protected void writeSDFFields(List<String> lines) {
		
		Nodes properties = cml.query(".//*[local-name()='"+CMLProperty.TAG+"']");
		List<CMLProperty> propertyList = new ArrayList<CMLProperty>();
		for (int i = 0; i < properties.size(); i++) {
			propertyList.add((CMLProperty) properties.get(i));
		}
		writeSDFFields(propertyList, lines);
	}
	
	protected void writeSDFFields(List<CMLProperty> propertyList, List<String> lines) {
		for (CMLProperty property : propertyList) {
			outputField(property, lines);
		}
	}
	
	protected void outputField(CMLProperty property, List<String> lines) {
		outputField(property.getRole(), property.getScalarElements().get(0).getXMLContent(), lines);
	}
	
	protected void outputField(String tag, String value, List<String> lines) {
		lines.add(V3_PROPERTY_1+tag+V3_PROPERTY_2);
		// single value
		lines.add(value.trim());
		// single blank lines
		lines.add(CMLConstants.S_EMPTY);
	}
	
	private void writeSDFEND(List<String> lines) {
		lines.add(SDF_END);
	}

	@Override
	public String getRegistryInputType() {
		return null;
	}
	
	@Override
	public String getRegistryOutputType() {
		return null;
	}
	
	@Override
	public String getRegistryMessage() {
		return "null";
	}

}
