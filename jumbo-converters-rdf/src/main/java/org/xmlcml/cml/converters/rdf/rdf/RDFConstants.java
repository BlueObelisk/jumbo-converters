package org.xmlcml.cml.converters.rdf.rdf;

import nu.xom.XPathContext;

public class RDFConstants {

	public final static String RDF_DESCRIPTION = "rdf:Description";
	public final static String RDF_ABOUT = "about";
	public final static String RDF_DATATYPE = "datatype";
	public final static String RDF_RESOURCE = "resource";
	
    /** root of all RDF URIs */
    public final static String RDF_NS_BASE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public final static String RDF_NS = RDF_NS_BASE;
    
    /** XPathContext for RDF.
     */
    public final static XPathContext RDF_XPATH = new XPathContext("rdf", RDF_NS);
    
    /** namespace for RDFXSD.
     * 
     */
    /** root of all RDFXSD URIs */
    public final static String RDFXSD_URI = "http://www.w3.org/2001/XMLSchema#";

//    String RDFXSD_NS = RDFXSD_NS_BASE;
//    
//    /** XPathContext for RDFXSD.
//     */
//    XPathContext RDFXSD_XPATH = new XPathContext("rdf", RDF_NS);
}
