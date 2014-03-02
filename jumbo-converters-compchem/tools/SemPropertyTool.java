package org.xmlcml.cml.semcompchem.tools;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.List;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.antlr.UnexpectedCompChemSchema;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.semcompchem.CompChemSematics;

/**
 * 
 * @author Weerapong Phadungsukanan
 * 
 */
public class SemPropertyTool extends AbstractSemanticTool {

    private CMLProperty property;
    private Resource bnode_prop_res = null;

    protected SemPropertyTool(CMLProperty property) {
        this.property = property;
    }

    public static SemPropertyTool getOrCreateTool(CMLProperty property) {
        SemPropertyTool tool = null;
        if (property != null) {
            tool = (SemPropertyTool) property.getTool();
            if (tool == null) {
                tool = new SemPropertyTool(property);
                property.setTool(tool);
            }
        }
        return tool;
    }

    @Override
    public Resource getResource(Model model) {
        if (bnode_prop_res == null) {
            List<CMLElement> list = property.getChildCMLElements();
            if (list.size() == 1) {
                // get cml value type
                CMLElement elem = list.get(0);
                Literal val_res = getXSDLiteral(elem, model);

                bnode_prop_res = model.createResource(CompChemSematics.cmlrdfProperty);

                bnode_prop_res.addProperty(CompChemSematics.cmlrdfHasValue, val_res);

                String units = elem.getAttributeValue("units");
                if (units != null) {
                    bnode_prop_res.addProperty(CompChemSematics.cmlrdfHasUnits, units);
                }

                Resource cmlType = getCMLTypeString(elem);
                bnode_prop_res.addProperty(RDF.type, cmlType);
            } else {
                throw new UnexpectedCompChemSchema("Not yet support multiple elements in CMLProperty : dictRef = " + property.getDictRef());
            }
        }
        return bnode_prop_res;
    }
}
