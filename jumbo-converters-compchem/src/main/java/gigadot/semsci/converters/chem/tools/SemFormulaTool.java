package gigadot.semsci.converters.chem.tools;

import gigadot.semsci.chem.schema.CompChemSematics;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLFormula;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author Weerapong Phadungsukanan
 * 
 */
public class SemFormulaTool extends AbstractSemanticTool {

    private static final String UNITS = "units";
	private CMLFormula formula;
    private Resource bnode_prop_res = null;

    protected SemFormulaTool(CMLFormula formula) {
        this.formula = formula;
    }

    public static SemFormulaTool getOrCreateTool(CMLFormula formula) {
        SemFormulaTool tool = null;
        if (formula != null) {
            tool = (SemFormulaTool) formula.getTool();
            if (tool == null) {
                tool = new SemFormulaTool(formula);
                formula.setTool(tool);
            }
        }
        return tool;
    }

    @Override
    public Resource getResource(Model model) {
        if (bnode_prop_res == null) {
        	formula.normalize();
        	double[] counts = formula.getCounts();
        	String[] elementTypes = formula.getElementTypes();
        	for (int i = 0; i < counts.length; i++) {
        		addCountElementTriples(model, counts[i], elementTypes[i]);
        	}
        	addFormalCharge(model, formula.getFormalCharge());
        }
        return bnode_prop_res;
    }
    
    void addFormalCharge(Model model, int charge) {
    	Literal val_res = model.createTypedLiteral(charge, CMLConstants.XSD_INTEGER);
        Resource res = model.createResource(CompChemSematics.cmlrdfProperty("hasCharge"));
        res.addProperty(CompChemSematics.cmlrdfHasValue, val_res);
 //       bnode_prop_res.addProperty(res, "xx");
    }
    
    void addCountElementTriples(Model model, double count, String elementType) {
    	Literal count_res = model.createTypedLiteral(count, CMLConstants.XSD_DOUBLE);
//    	bnode_prop_res.addProperty("has"+elementType, count_res);
    }
    
}
