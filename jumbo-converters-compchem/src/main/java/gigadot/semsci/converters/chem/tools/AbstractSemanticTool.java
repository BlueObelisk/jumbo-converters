package gigadot.semsci.converters.chem.tools;

import gigadot.semsci.chem.schema.CompChemSematics;
import gigadot.semsci.converters.chem.uri.DefaultURIUUIDGenerator;
import gigadot.semsci.converters.chem.uri.HasURIGenerator;
import gigadot.semsci.converters.chem.uri.URIGenerator;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author Weerapong Phadungsukanan
 * 
 */
public abstract class AbstractSemanticTool extends AbstractTool implements HasURIGenerator {

    protected Logger logger = Logger.getLogger(getClass());
    protected URIGenerator uriGen = new DefaultURIUUIDGenerator();

    public abstract Resource getResource(Model model);

    public void setURIGenerator(URIGenerator uriGen) {
        this.uriGen = uriGen;
    }

    public URIGenerator getURIGenerator() {
        return uriGen;
    }

    protected static Resource getCMLTypeString(CMLElement elem) {
        Resource res = null;
        if (elem instanceof CMLScalar) {
            res = CompChemSematics.cmlScalar;
        } else if (elem instanceof CMLArray) {
            res = CompChemSematics.cmlArray;
        } else if (elem instanceof CMLMatrix) {
            res = CompChemSematics.cmlMatrix;
        } else if (elem instanceof CMLVector3) {
            res = CompChemSematics.cmlVector3;
        }
        return res;
    }

    protected static Literal getXSDLiteral(CMLElement elem, Model model) {
        String XSDDataType = null;
        Literal val_res = null;
        if (elem instanceof CMLScalar) {
            CMLScalar scalar = (CMLScalar) elem;
            XSDDataType = scalar.getDataType();
            val_res = model.createTypedLiteral(scalar.getValue(), XSDDataType);
        } else {
            XSDDataType = CMLConstants.XSD_STRING;
            val_res = model.createTypedLiteral(elem.getValue(), XSDDataType);
        }
        return val_res;
    }
}
