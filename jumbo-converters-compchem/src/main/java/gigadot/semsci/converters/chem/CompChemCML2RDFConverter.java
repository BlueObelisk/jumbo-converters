package gigadot.semsci.converters.chem;

import gigadot.semsci.chem.dictionary.DictionaryCollection;
import gigadot.semsci.chem.schema.CompChemSemantics;
import gigadot.semsci.converters.chem.exception.UnexpectedCompChemSchema;
import gigadot.semsci.converters.chem.tools.SemMoleculeTool;
import gigadot.semsci.converters.chem.tools.SemParameterTool;
import gigadot.semsci.converters.chem.tools.SemPropertyTool;
import gigadot.semsci.converters.chem.uri.DefaultURIUUIDGenerator;
import gigadot.semsci.converters.chem.uri.HasURIGenerator;
import gigadot.semsci.converters.chem.uri.URIGenerator;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.compchem.AbstractCompchem2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLParameterList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.interfacex.HasDictRef;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author wp214
 */
public class CompChemCML2RDFConverter extends AbstractCompchem2CMLConverter {

    private static Logger LOG = Logger.getLogger(CompChemCML2RDFConverter.class);
    
	public CompChemCML2RDFConverter() {
		abstractCommon = new CompChemCommon();
	}
	
	public Type getOutputType() {
	    return Type.RDFXML;
	}
	
	public Type getInputType() {
	    return Type.CML;
	}
    
	/**
	 * converts an Foo object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param in input stream
	 */
	public Element convertToXML(Element element) {
		CompChemProcessor compchemProcessor = new CompChemProcessor();
		Element rdfElement = compchemProcessor.convertToXML(element);
		return rdfElement;
	}
    

}
