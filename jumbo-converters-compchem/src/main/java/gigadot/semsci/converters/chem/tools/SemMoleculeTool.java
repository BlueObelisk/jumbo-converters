package gigadot.semsci.converters.chem.tools;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.xmlcml.cml.element.CMLMolecule;
import gigadot.semsci.converters.chem.uri.URIGenerator;
import gigadot.semsci.chem.schema.CompChemSematics;

/**
 * 
 * @author Weerapong Phadungsukanan
 * 
 */
public class SemMoleculeTool extends AbstractSemanticTool {

    private CMLMolecule molecule = null;
    private Resource bnode_geo_res = null;

    protected SemMoleculeTool(CMLMolecule molecule) {
        this.molecule = molecule;
    }

    public static SemMoleculeTool getOrCreateTool(CMLMolecule molecule, URIGenerator uriGen) {
        SemMoleculeTool tool = null;
        if (molecule != null) {
            tool = (SemMoleculeTool) molecule.getTool();
            if (tool == null) {
                tool = new SemMoleculeTool(molecule);
                tool.setURIGenerator(uriGen);
                molecule.setTool(tool);
            }
        }
        return tool;
    }

    @Override
    public Resource getResource(Model model) {
        if (bnode_geo_res == null) {
            String partfinal_uri = getURIGenerator().createCMLURL(molecule).toString();
            Resource path_res = model.createResource(partfinal_uri, CompChemSematics.cmlrdfDataResource);
            bnode_geo_res = model.createResource(CompChemSematics.axiomMolecularEntity);
            bnode_geo_res.addProperty(CompChemSematics.cmlrdfRepresentedBy, path_res);
        }
        return bnode_geo_res;
    }
}
