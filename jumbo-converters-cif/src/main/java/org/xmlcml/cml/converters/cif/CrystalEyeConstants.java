package org.xmlcml.cml.converters.cif;

import nu.xom.XPathContext;

public interface CrystalEyeConstants {
	
	public static final String CRYSTALEYE_HOME_URL = "http://wwmm.ch.cam.ac.uk/crystaleye";
	
	public static final String CRYSTALEYE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";	
	
	/*
	 * process flags for download log
	 */
	public static final String CIF2CML = "cif2Cml";
	public static final String CML2FOO = "cml2Foo";
	public static final String WEBPAGE = "webpage";
	public static final String RSS = "rss";
	public static final String DOILIST = "doilist";
	public static final String SMILESLIST = "smileslist";
	public static final String BONDLENGTHS = "bondlengths";
	public static final String CELLPARAMS = "cellParams";
	public static final String ATOMPUB = "atompub";
	public static final String VALUE = "value";
	
	/*
	 * properties file keys
	 */
	public static final String SPLITCIF_REGEX = "splitcif.regex";
	public static final String DOWNLOAD_LOG_PATH = "download.log.path";
	public static final String WRITE_DIR = "write.dir";
	public static final String PUBLISHER_ABBREVIATIONS = "publisher.abbreviations";
	public static final String RSS_FEED_TYPES = "rss.feed.types";
	public static final String SUMMARY_WRITE_DIR = "summary.write.dir";
	public static final String WEB_SUMMARY_WRITE_DIR = "web.summary.write.dir";
	public static final String RSS_WRITE_DIR = "rss.write.dir";
	public static final String RSS_ROOT_FEEDS_DIR = "rss.web.root.dir";
	public static final String SLEEP_MAX = "sleep.max";
	public static final String CIF_DICT = "cif.dict";
	public static final String SPACEGROUP_XML = "spacegroup.xml";
	public static final String DOI_LIST_PATH = "doi.list.path";
	public static final String SMILES_LIST_PATH = "smiles.list.path";
	public static final String SMILES_INDEX_PATH = "smiles.index.path";
	public static final String BOND_LENGTHS_DIR = "bond.lengths.dir";
	public static final String COD_DOWNLOADED_FILES_LIST = "cod.cifs.file";
	public static final String CELL_PARAMS_FILE = "cell.params.file";
	public static final String ATOM_PUB_ROOT_DIR = "atom.pub.dir";
	public static final String ATOM_PUB_ROOT_URL = "atom.pub.url";
	public static final String ATOM_PUB_FEED_MAX_ENTRIES = "atom.pub.feed.max.entries";
	public static final String UUID_TO_URL_FILE_PATH = "uuid.url.file.path";
	
	/*
	 * publisher DOI prefixes
	 */
	public static final String CHEMSOCJAPAN_DOI_PREFIX = "10.1246";
	public static final String RSC_DOI_PREFIX = "10.1039";
	public static final String ACS_DOI_PREFIX = "10.1021";
	public static final String ELSEVIER_DOI_PREFIX = "10.1016";
	
	/*
	 * namespaces
	 */
	public static final String CC_NS = "http://journals.iucr.org/services/cif";
	public static final String NED24_NS = "http://wwmm.ch.cam.ac.uk/ned24/";
	public static final String XHTML_NS = "http://www.w3.org/1999/xhtml";
	public static final String XLINK_NS = "http://www.w3.org/1999/xlink";
	public static final String SVG_NS = "http://www.w3.org/2000/svg";
	
	// RSS/Atom namespaces
	public static final String ATOM_1_NS = "http://www.w3.org/2005/Atom";
	public static final String RSS_1_NS = "http://purl.org/rss/1.0/";
	public static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	/*
	 * XPathContexts
	 */
	public static final XPathContext X_CC = new XPathContext("c", CC_NS);
	public static final XPathContext X_NED24 = new XPathContext("n", NED24_NS);
	public static final XPathContext X_XHTML = new XPathContext("x", XHTML_NS);
	public static final XPathContext X_SVG = new XPathContext("svg", SVG_NS);
	// for RSS/Atom
	public static final XPathContext X_ATOM1 = new XPathContext("atom1", ATOM_1_NS);
	public static final XPathContext X_DC = new XPathContext("dc", DC_NS);
	public static final XPathContext X_RSS1 = new XPathContext("rss1", RSS_1_NS);
	public static final XPathContext X_RDF = new XPathContext("rdf", RDF_NS);
	
	/*
	 * MIME types used in CrystalEye
	 */
//	public static final String COMPLETE_CML_MIME = ".complete.cml.xml";
	public static final String COMPLETE_CML_MIME = ".complete.cml";
	public static final String CIF_HTML_SUMMARY_MIME = ".cif.summary.html";
	public static final String RAW_CML_MIME = ".raw.cml.xml";
	public static final String CSV_MIME = ".csv";
	public static final String SVG_MIME = ".svg";
	public static final String HTML_MIME = ".html";
	public static final String CIF_MIME = ".cif";
	public static final String DOI_MIME = ".doi";
	public static final String DATE_MIME = ".date";
	public static final String TITLE_MIME = ".title";
	public static final String PNG_MIME = ".png";
	public static final String SMALL_PNG_MIME = ".small.png";
	public static final String PLATON_MIME = ".platon.jpeg";
	
	/*
	 * dictRefs
	 */
	public static final String POLYMERIC_FLAG_DICTREF = "ned24:isPolymeric";
	public static final String NO_BONDS_OR_CHARGES_FLAG_DICTREF = "ned24:noBondsOrChargesSet";
	
	/*
	 * RSS/CMLRSS constants
	 */
	public static final String RSS_ALL_DIR_NAME = "all";
	public static final String RSS_JOURNAL_DIR_NAME = "journal";
	public static final String RSS_ATOMS_DIR_NAME = "atoms";
	public static final String RSS_BOND_DIR_NAME = "bonds";
	public static final String RSS_CLASS_DIR_NAME = "class";
	
	public static final String RSS_DESC_VALUE_PREFIX = "CrystalEye summary of ";
	public static final String CMLRSS_DESC_VALUE_PREFIX = "CrystalEye CMLRSS summary of ";
	public static final String RSS_DIR_NAME = "rss";
	public static final String CMLRSS_DIR_NAME = "cmlrss";
	
	public static final String FEED_FILE_NAME = "feed.xml";
	
	/*
	 * miscellaneous
	 */
	public static final int MAX_CIF_SIZE_IN_BYTES = 2621440;
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String COMPLETE_CML_MIME_REGEX = COMPLETE_CML_MIME.replaceAll("\\.", "\\\\.");
}
