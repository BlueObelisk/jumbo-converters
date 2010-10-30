package org.xmlcml.cml.converters;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Type {

	private static final Logger LOG = Logger.getLogger(Type.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static final Type NULL;
	public static final Type DIRECTORY;
	public static final Type INDEX;
	public static final Type LIST;
	
	public static final Type CDX;
	public static final Type CDXML;
	public static final Type CIF;
	public static final Type CML;
	public static final Type CSV;
	public static final Type DALTON;
	public static final Type FOO;
	public static final Type FOO_XML;
	public static final Type GAMESSUK_PUNCH;
	public static final Type GAMESSUK_PUNCH_XML;
	public static final Type GAMESSUS_INPUT;
	public static final Type GAMESSUS_INPUT_XML;
	public static final Type GAMESSUS_PUNCH;
	public static final Type GAMESSUS_PUNCH_XML;
	public static final Type GAU_ARC;
	public static final Type GAU_IN;
	public static final Type GAU_LOG;
	public static final Type HTML;
	public static final Type IMAGE;
	public static final Type JDX;
	public static final Type MDL;
	public static final Type N3;
	public static final Type NAME;
	public static final Type NWCHEM;
	public static final Type NWCHEM_XML;
	public static final Type OWL;
	public static final Type PDF;
	public static final Type PNG;
	public static final Type RDFXML;
	public static final Type RPT;
	public static final Type SDF;
	public static final Type SVG;
	public static final Type SVGBYTES;  // kludge round broken DOCTYPE
	public static final Type TXT;
	public static final Type XML;
	public static final Type XHTML;
	public static final Type XYZ;
	
	private static Set<Type> typeList = new HashSet<Type>();
	private static Map<String, Set<Type>> extensionMap = 
		new HashMap<String, Set<Type>>();
	private static Map<String, Type> typeMap = 
		new HashMap<String, Type>();
	static {
		NULL = new Type("null", ObjectType.NULL, "null");
		DIRECTORY = new Type("directory", ObjectType.DIRECTORY, "directory");
		INDEX = new Type("index", ObjectType.INDEX, "index");
		LIST = new Type("list", ObjectType.LIST, "list");
		
		CDX = new Type("chemical/x-cdx", ObjectType.BYTES,
		"cdx");
		CDXML = new Type("chemical/x-cdxml", ObjectType.XML,
		"cdxml", "cdx.xml");
		CIF = new Type("chemical/x-cif", ObjectType.TEXT,
		"cif");
		CML = new Type("chemical/x-cml", ObjectType.XML,
				"cml", "cml.xml");
		CSV = new Type("text/csv", ObjectType.TEXT,
		"csv");
		DALTON = new Type("chemical/x-dalton", ObjectType.TEXT,
		".out");
		FOO = new Type("chemical/x-foo", ObjectType.TEXT,
		"foo");
		FOO_XML = new Type("chemical/x-foo-xml", ObjectType.XML,
		"foo.xml");
		GAMESSUK_PUNCH = new Type("chemical/x-gamessuk-punch", ObjectType.TEXT,
		"gamessuk.punch");
		GAMESSUK_PUNCH_XML = new Type("chemical/x-gamessuk-punch-xml", ObjectType.XML,
		"gamessuk.punch.xml");
		GAMESSUS_INPUT = new Type("chemical/x-gamessus-input", ObjectType.TEXT,
		"gamessus.input");
		GAMESSUS_INPUT_XML = new Type("chemical/x-gamessus-input-xml", ObjectType.XML,
		"gamessus.input.xml");
		GAMESSUS_PUNCH = new Type("chemical/x-gamessus-punch", ObjectType.TEXT,
		"gamessus.punch");
		GAMESSUS_PUNCH_XML = new Type("chemical/x-gamessus-punch-xml", ObjectType.XML,
		"gamessus.punch.xml");
		GAU_ARC = new Type("chemical/x-gaussian-archive", ObjectType.TEXT,
				"gau", "gau.arc");
		GAU_IN = new Type("chemical/x-gaussian-input", ObjectType.TEXT,
				"gau.in");
		GAU_LOG = new Type("chemical/x-gaussian-log", ObjectType.TEXT,
				"gau.log", "g03");
		HTML = new Type("text/html", ObjectType.TEXT,
				"html", "htm");		
		IMAGE = new Type("image", ObjectType.BYTES,
						"img");
		JDX = new Type("chemical/x-jcamp-dx", ObjectType.TEXT,
				"jdx");
		MDL = new Type("chemical/x-mdl-molfile",
				ObjectType.TEXT, "mol");
		N3 = new Type("text/n3; charset=utf-8", ObjectType.BYTES, "n3");
		NAME = new Type("text/text",
				ObjectType.TEXT, "name");
		NWCHEM = new Type("chemical/x-nwchem", ObjectType.TEXT,
		"nwchem");
		NWCHEM_XML = new Type("chemical/x-nwchem-xml", ObjectType.XML,
		"nwchem.xml");
		OWL = new Type("application/rdf+xml",
				ObjectType.XML, "owl");
		PDF = new Type("image/pdf",
				ObjectType.BYTES, "pdf");
		PNG = new Type("image/png",
				ObjectType.BYTES, "png");
		RDFXML = new Type("chemical/x-rdfxml", ObjectType.XML,
				"rdf.xml", "rdf");
		RPT = new Type("misc/rpt", ObjectType.TEXT, "rpt");
		SDF = new Type("chemical/x-mdl-sdffile",
				ObjectType.TEXT, "sdf");
		SVG = new Type("image/svg+xml", ObjectType.XML, "svg");
		SVGBYTES = new Type("image/svg+xml", ObjectType.BYTES, "svgbytes");
		TXT = new Type("text/txt", ObjectType.TEXT, "txt");
		XHTML = new Type("text/xhtml", ObjectType.XML,
				"xhtml");
		XML = new Type("application/xml", ObjectType.XML, "xml");
		XYZ = new Type("chemical/x-xyz", ObjectType.TEXT, "xyz");
		
		typeList.add(NULL);
		typeList.add(DIRECTORY);
		typeList.add(INDEX);
		typeList.add(LIST);  
		
		typeList.add(CDX);
		typeList.add(CDXML);
		typeList.add(CIF);
		typeList.add(CML);
		typeList.add(CSV);
		typeList.add(DALTON);
		typeList.add(FOO);
		typeList.add(FOO_XML);
		typeList.add(GAMESSUK_PUNCH);
		typeList.add(GAMESSUK_PUNCH_XML);
		typeList.add(GAMESSUS_PUNCH);
		typeList.add(GAMESSUS_PUNCH_XML);
		typeList.add(GAU_ARC);
		typeList.add(GAU_IN);
		typeList.add(GAU_LOG);
		typeList.add(HTML);
		typeList.add(JDX);
		typeList.add(MDL);
		typeList.add(N3);
		typeList.add(NAME);
		typeList.add(NWCHEM);
		typeList.add(NWCHEM_XML);
		typeList.add(OWL);
		typeList.add(PDF);
		typeList.add(PNG);
		typeList.add(RDFXML);
		typeList.add(RPT);
		typeList.add(SDF);
		typeList.add(SVG);
		typeList.add(SVGBYTES);
		typeList.add(TXT);
		typeList.add(XHTML);
		typeList.add(XML);
		typeList.add(XYZ);
		
		for (Type type : typeList) {
			for (String extension : type.getExtensions()) {
				Set<Type> types = extensionMap.get(extension);
				if (types == null) {
					types = new HashSet<Type>();
					extensionMap.put(extension, types);
				}
				types.add(type);
			}
			String t = type.getDefaultExtension().toUpperCase();
			typeMap.put(t, type);
		}
		
	};
	
	public static Type getType(String t) {
		return (t == null) ? null : typeMap.get(t.toUpperCase());
	}

	/**
	 * ----------possible other ones with chemical mime-------------
	 * chemical/x-cdx cdx ChemDraw eXchange file example.cdx
	 * http://www.camsoft.com/plugins/ chemical/x-chemdraw chm ChemDraw file
	 * example.chm http://www.camsoft.com/plugins/ chemical/x-cif cif
	 * Crystallographic Interchange Format example.cif example_cif
	 * http://www.bernstein-plus-sons.com/software/rasmol/ chemical/x-chem3d c3d
	 * Chem3D Format example.c3d CambridgeSoft chemical/x-cml cml Chemical
	 * Markup Language example.cml http://cml.sourceforge.net/
	 * chemical/x-daylight-smiles smi Smiles Format example.smi example_smi
	 * http://www.daylight/dayhtml/smiles/index.html chemical/x-gamess-input
	 * inp, gam GAMESS Input format example.inp example_inp
	 * http://www.msg.ameslab.gov/GAMESS/Graphics/MacMolPlt.shtml
	 * chemical/x-gaussian-input gau Gaussian Input format example.gau
	 * example_gau http://www.mdli.com/ chemical/x-gaussian-checkpoint fch,fchk
	 * Gaussian Checkpoint format example.fch http://products.camsoft.com/
	 * chemical/x-gaussian-cube cub Gaussian Cube (Wavefunction) format
	 * example.cub http://www.mdli.com/ chemical/x-gcg8-sequence gcg example.gcg
	 * example_gcg chemical/x-jcamp-dx jdx, dx JCAMP Spectroscopic Data Exchange
	 * Format example.dx example_dx http://www.mdli.com/ chemical/x-mdl-molfile
	 * mol MDL Molfile example.mol example_mol http://www.mdli.com/
	 * chemical/x-mdl-rdfile rd Reaction-data file example.rd example_rd
	 * http://www.mdli.com/ chemical/x-mdl-rxnfile rxn MDL Reaction format
	 * example.rxn example_rxn http://www.mdli.com/ chemical/x-mdl-sdfile sd MDL
	 * Structure-data file example.sd example_sd http://www.mdli.com/
	 * chemical/x-mol2 mol2 Portable representation of a SYBYL molecule
	 * example.mol2 example_mol2
	 * http://www.tripos.com/TechBriefs/mol2_format/mol2.html chemical/x-pdb pdb
	 * Protein DataBank example.pdb example_pdb http://www.mdli.com/
	 * chemical/x-xyz xyz Co-ordinate Animation format example.xyz example_xyz
	 * http://www.mdli.com/
	 * 
	 * 
	 */
	private List<String> extensions = new ArrayList<String>();
	private String mimeType;
	private ObjectType objectType;

	/**
	 * Enumeration of object types that are passed between converters.
	 * 
	 * @todo might need to add byte array.
	 * @author jimdowning
	 * 
	 */
	public static enum ObjectType {
		NULL, DIRECTORY, INDEX, LIST, TEXT, XML, BYTES;
	}

	@SuppressWarnings("unused")
	private Type() {
		;
	}

	public Type(String mimeType, ObjectType objectType, String... extensions) {
		if (mimeType == null) {
			throw new IllegalArgumentException("MIME type may not be null");
		}
		if (objectType == null) {
			throw new IllegalArgumentException("ObjectType may not be null");
		}
		if (extensions == null) {
			throw new IllegalArgumentException("extensions may not be null");
		}
		if (extensions.length == 0) {
			throw new IllegalArgumentException(
					"must supply at least one extension");
		}
		for (String s : extensions) {
			if (s == null) {
				throw new IllegalArgumentException(
						"Exception array should not contain null");
			}
		}
		this.mimeType = mimeType;
		this.objectType = objectType;
		this.extensions = Arrays.asList(extensions);
	}

	public Type(String mimeType, ObjectType objectType, String extension) {
		this(mimeType, objectType, new String[] { extension });
	}

	public List<String> getExtensions() {
		return Collections.unmodifiableList(extensions);
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getDefaultExtension() {
		return !extensions.isEmpty() ? extensions.get(0) : "";
	}

	public int hashCode() {
		int result = 19;
		result = 13 * result + extensions.hashCode();
		result = 13 * result + mimeType.hashCode();
		result = 13 * result + objectType.hashCode();
		return result;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof Type)) {
			return false;
		}
		Type t = (Type) o;
		if (!extensions.equals(t.extensions)) {	
			return false;
		}
		if (!mimeType.equals(t.mimeType)) {
			return false;
		}
		if (objectType != t.objectType) {
			return false;
		}
		return true;
	}

	public String toString() {
		return mimeType;
	}

	public ObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}
	
	/** gets suffix filter oring all extensions.
	 * requires fileFileFilter and non-hidden files
	 * @return filter passing all suffixFilters
	 */
	public IOFileFilter getSuffixFileFilter() {
		IOFileFilter fileFilter =  null;
		if (extensions.size() > 0) {
			fileFilter =
		        FileFilterUtils.andFileFilter(
		        		FileFilterUtils.fileFileFilter(),
	                    HiddenFileFilter.VISIBLE);
			IOFileFilter suffixFilter = FileFilterUtils.suffixFileFilter(extensions.get(0));
			for (int i = 1; i < extensions.size(); i++) {
				suffixFilter = FileFilterUtils.andFileFilter(
						suffixFilter,
						FileFilterUtils.suffixFileFilter(extensions.get(i))
						);			
			}
		}
		return fileFilter;
	}

	/** creates unique lookup string for foo2bar conversion.
	 * 
	 * @param foo
	 * @param bar
	 * @return
	 */
	public static String getFoo2BarString(Type foo, Type bar) {
		return foo.getMimeType()+"=>"+((bar == null) ? "null" : bar.getMimeType());
	}

	/** gets Type for extension.
	 * 
	 * @param extension
	 * @return null if not found
	 * @throws RuntimeException if more than one match
	 */
	public static Type getTypeForExtension(String extension) {
		Type type = null;
		Set<Type> types = extensionMap.get(extension);
		if (types == null) {
			// none
		} else if (types.size() > 1) {
			throw new RuntimeException("Too many types for extension: "+extension);
		} else {
			type = types.iterator().next();
		}
		return type;
	}

	public static String getTypeForFilename(String filename) {
		String extension = (filename == null) ? null : org.xmlcml.euclid.Util.getSuffix(filename);
		Type type = (extension == null) ? null : getTypeForExtension(extension);
		return (type == null) ? null : type.getDefaultExtension();
	}
}
