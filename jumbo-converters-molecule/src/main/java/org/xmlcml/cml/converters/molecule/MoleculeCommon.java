package org.xmlcml.cml.converters.molecule;

import org.xmlcml.cml.converters.AbstractCommon;

/**
 * A collection of common objects such as namespaces, dictionaries used
 * in the Molecule system
 * @author pm286
 *
 */
public class MoleculeCommon extends AbstractCommon {

	private static final String MOLECULE_PREFIX = "molecule";
	private static final String MOLECULE_URI = "http://wwmm.ch.cam.ac.uk/dict/molecule";

	public static final String INPUT = "molecule_input";

	public static final String LOG = "molecule_log";
	public static final String LOG_XML = "molecule_log_xml";
	public static final String LOG_CML = "molecule_log_compchem";
	public static final String MDL = "mdl_molfile";
	public static final String PUBCHEM = "pubchem";
	public static final String PUBCHEM_XML = "pubchem_xml";
	public static final String SDF = "mdl_sdfile";
	public static final String XYZ = "xyz";

    protected String getDictionaryResource() {
    	return "org/xmlcml/cml/converters/compchem/molecule/moleculeDictionary.xml";
    }

	public String getPrefix() {
		return MOLECULE_PREFIX;
	}

	public String getNamespace() {
		return MOLECULE_URI;
	}

}
