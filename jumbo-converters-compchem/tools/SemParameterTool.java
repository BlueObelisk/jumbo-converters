package org.xmlcml.cml.semcompchem.tools;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.List;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.antlr.UnexpectedCompChemSchema;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.semcompchem.CompChemSematics;

/**
 * 
 * @author Weerapong Phadungsukanan
 * 
 */
public class SemParameterTool extends AbstractSemanticTool {

    private CMLParameter parameter;
    private Resource bnode_param_res = null;

    protected SemParameterTool(CMLParameter property) {
        this.parameter = property;
    }

    public static SemParameterTool getOrCreateTool(CMLParameter parameter) {
        SemParameterTool tool = null;
        if (parameter != null) {
            tool = (SemParameterTool) parameter.getTool();
            if (tool == null) {
                tool = new SemParameterTool(parameter);
                parameter.setTool(tool);
            }
        }
        return tool;
    }

    @Override
    public Resource getResource(Model model) {
        if (bnode_param_res == null) {
            List<CMLElement> list = parameter.getChildCMLElements();
            if (list.size() == 1) {
                // get cml type
                CMLElement elem = list.get(0);
                Literal val_res = getXSDLiteral(elem, model);

                bnode_param_res = model.createResource(CompChemSematics.cmlrdfParameter);

                bnode_param_res.addProperty(CompChemSematics.cmlrdfHasValue, val_res);

                String units = elem.getAttributeValue("units");
                if (units != null) {
                    bnode_param_res.addProperty(CompChemSematics.cmlrdfHasUnits, units);
                }

                Resource cmlType = getCMLTypeString(elem);
                bnode_param_res.addProperty(RDF.type, cmlType);
            } else {
                throw new UnexpectedCompChemSchema("Not yet support multiple elements in CMLParameter : dictRef = " + parameter.getDictRef());
            }
        }
        return bnode_param_res;
    }
}
