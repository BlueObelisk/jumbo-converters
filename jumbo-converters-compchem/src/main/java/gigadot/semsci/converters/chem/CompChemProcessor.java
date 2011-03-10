package gigadot.semsci.converters.chem;

import gigadot.semsci.chem.dictionary.DictionaryCollection;
import gigadot.semsci.chem.schema.CompChemSematics;
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
import org.xmlcml.cml.converters.cml.RawXML2CMLProcessor;
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

public class CompChemProcessor extends RawXML2CMLProcessor implements HasURIGenerator {
	private static Logger LOG = Logger.getLogger(CompChemProcessor.class);

    private DictionaryCollection dictionaryCollection = null;
    private URIGenerator uriGen = new DefaultURIUUIDGenerator();
    private Model compchemModel = null;
//    private Element rdfElement = null;

	public CompChemProcessor() {
	}

	public void processXML() {
		// null
	}
	
    Element convertToXML(Element xml) {
        Model compchemModel = null;
        try {
            loadDictionaries();
            CMLBuilder bu = new CMLBuilder();
            Document doc = null;
            try {
                doc = bu.build(xml.toXML(), "");
            } catch (ValidityException ex) {
                throw new RuntimeException(ex);
            } catch (ParsingException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            compchemModel = createCompChemModel((CMLCml) doc.getRootElement());
        } catch (UnexpectedCompChemSchema usex) {
            LOG.error(usex);
            throw usex;
        }
        return convertModelToXMLElement(compchemModel);
    }

    private Element convertModelToXMLElement(Model compchemModel) throws RuntimeException {
        StringWriter stw = new StringWriter();
        compchemModel.write(stw);
        StringReader str_reader = new StringReader(stw.toString());
        Document doc = null;
        CMLBuilder parser = new CMLBuilder();
        try {
            doc = parser.build(str_reader);
            return doc.getRootElement();
        } catch (ValidityException ex) {
            throw new RuntimeException("Fatal error - rdf is invalid : this should never happen if model is output in xml.", ex);
        } catch (ParsingException ex) {
            throw new RuntimeException("Fatal error in parsing rdf model to xom document : this should never happen if model is output in xml.", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error in IO when parsing rdf model to xom document : this should never happen if model is output in xml.", ex);
        }
    }

    public void setURIGenerator(URIGenerator uriGen) {
        this.uriGen = uriGen;
    }

    public URIGenerator getURIGenerator() {
        return uriGen;
    }
    
    public Model createCompChemModel(CMLCml cml) throws UnexpectedCompChemSchema {
        // expecting cml container to contain only one joblist module
        Nodes joblist_nodes = cml.query("/cml:cml/cml:module[@role='joblist']", CMLNamespace.CML_XPATH);
        Nodes molecule_nodes = cml.query("/cml:cml/cml:molecule", CMLNamespace.CML_XPATH);
        if (joblist_nodes.size() == 1) {
        	Resource path_res =  createModelAndPathRes(cml);

            // N3:   jim:computation
            // N3:       a qm:Computation;
            Resource unq_com_res = compchemModel.createResource(
                    createUUIDURIString(cml), CompChemSematics.qmComputation);
            // N3:       cml:representedBy <./target/CH4.cml>;
            unq_com_res.addProperty(CompChemSematics.cmlrdfRepresentedBy, path_res);

            CMLModule joblist_mod = (CMLModule) joblist_nodes.get(0);
            // N3:       chemid:EmpiricalFormula "H4O4Si";
            processIdentifiers(unq_com_res, joblist_mod);

            // N3:       qm:jobs ( jim:job1 ).

            // expecting a series of job module
            processJobNodes(unq_com_res, joblist_mod);
        } else if (molecule_nodes.size() == 1) {
            CMLMolecule molecule = (CMLMolecule) molecule_nodes.get(0);
            Resource molecule_res = createModelAndPathRes(molecule);
            addMolecule(molecule_res, molecule);
        } else {
            // This cannot check whether there are other type of cml elements or not.
            // This method ensures that compchemModel is never be null.
            throw new UnexpectedCompChemSchema("CompChem CML can only contain 1 joblist module.");
        }
        return compchemModel;
    }
    
    private Resource createModelAndPathRes(CMLElement cml) {
        compchemModel = ModelFactory.createDefaultModel();
        setupNsprefixes();

        // N3:    <./target/CH4.cml> a cml:dataResource.
        String path_uri = getURIGenerator().createCMLURL(cml).toString();
        
        Resource path_res = compchemModel.createResource(path_uri, CompChemSematics.cmlrdfDataResource);
        return path_res;

    }

	private void processJobNodes(Resource unq_com_res, CMLModule joblist_mod) {
		Nodes job_nodes = joblist_mod.query("./cml:module[@role='job']", CMLNamespace.CML_XPATH);
		List<RDFNode> jobs_rdf_list = new ArrayList<RDFNode>(job_nodes.size());
		for (int i = 0; i < job_nodes.size(); i++) {
		    CMLModule job_mod = (CMLModule) job_nodes.get(i);
		
		    CMLModule init_mod = extractTaskFromJob(job_mod, "init");
		    Resource unq_jinit_res = createInitTaskResource(init_mod);
		
		    CMLModule opt_mod = extractTaskFromJob(job_mod, "optimization");
		    Resource unq_jopt_res = null;
		    if (opt_mod != null) {
		        unq_jopt_res = createOptimizationTaskResource(opt_mod);
		    }
		
		    CMLModule final_mod = extractTaskFromJob(job_mod, "final");
		    Resource unq_jfinal_res = createFinalTaskResource(final_mod);
		
		    Resource unq_job_res = createJobResource(job_mod, unq_jinit_res, unq_jopt_res, unq_jfinal_res);
		
		    jobs_rdf_list.add(unq_job_res);
		}
		Iterator<RDFNode> jobs_rdf_iter = jobs_rdf_list.iterator();
		RDFList job_list = compchemModel.createList(jobs_rdf_iter);
		unq_com_res.addProperty(CompChemSematics.qmJobs, job_list);
	}

	private void processIdentifiers(Resource unq_com_res, CMLModule joblist_mod) {
		Nodes id_nodes = joblist_mod.query("./cml:identifier", CMLNamespace.CML_XPATH);
		for (int i = 0; i < id_nodes.size(); i++) {
		    CMLIdentifier id = (CMLIdentifier) id_nodes.get(i);
		    if (id.getConvention().equals("chemid:EmpiricalFormula")) {
		        unq_com_res.addProperty(CompChemSematics.chemidEmpiricalFormula, id.getCMLValue());
		    } else if (id.getConvention().equals("chemid:CanonicalSmiles")) {
		        unq_com_res.addProperty(CompChemSematics.chemidCanonicalSmiles, id.getCMLValue());
		    } else if (id.getConvention().equals("chemid:IsomericSmiles")) {
		        unq_com_res.addProperty(CompChemSematics.chemidIsomericSmiles, id.getCMLValue());
		    } else if (id.getConvention().equals("chemid:InChI")) {
		        unq_com_res.addProperty(CompChemSematics.chemidInChI, id.getCMLValue());
		    }
		}
	}

//	private void processJobNodes(Nodes job_nodes, List<RDFNode> jobs_rdf_list) {
//		for (int i = 0; i < job_nodes.size(); i++) {
//		    CMLModule job_mod = (CMLModule) job_nodes.get(i);
//
//		    CMLModule init_mod = extractTaskFromJob(job_mod, "init");
//		    Resource unq_jinit_res = createInitTaskResource(init_mod);
//
//		    CMLModule opt_mod = extractTaskFromJob(job_mod, "optimization");
//		    Resource unq_jopt_res = null;
//		    if (opt_mod != null) {
//		        unq_jopt_res = createOptimizationTaskResource(opt_mod);
//		    }
//
//		    CMLModule final_mod = extractTaskFromJob(job_mod, "final");
//		    Resource unq_jfinal_res = createFinalTaskResource(final_mod);
//
//		    Resource unq_job_res = createJobResource(job_mod, unq_jinit_res, unq_jopt_res, unq_jfinal_res);
//
//		    jobs_rdf_list.add(unq_job_res);
//		}
//	}

    private Resource createInitTaskResource(CMLModule init_mod) {
        // N3:   jim:j1init
        // N3:       a qm:Initialization;
        Resource unq_jinit_res = compchemModel.createResource(createUUIDURIString(init_mod), CompChemSematics.qmInitialization);
        // N3:       qm:hasGeometry [
        // N3:           a chem:MolecularEntity;
        // N3:           cml:representedBy <./target/CH4init.cml> ] ;
        CMLMolecule molecule = (CMLMolecule) init_mod.getFirstCMLChild(CMLMolecule.TAG);
        addMolecule(unq_jinit_res, molecule);

        CMLParameterList init_params = (CMLParameterList) init_mod.getFirstCMLChild(CMLParameterList.TAG);
        List<CMLParameter> params = init_params.getParameterDescendants();
        addPropertyOrParameterResourcesToTask(params, unq_jinit_res);
        return unq_jinit_res;
    }

	private void addMolecule(Resource unq_jinit_res, CMLMolecule molecule) {
		SemMoleculeTool semTool = SemMoleculeTool.getOrCreateTool(molecule, getURIGenerator());
        Resource bnode_geo_res = semTool.getResource(compchemModel);
        unq_jinit_res.addProperty(CompChemSematics.qmHasGeometry, bnode_geo_res);
	}

    private Resource createOptimizationTaskResource(CMLModule opt_mod) {
        // N3:   jim:j1opt
        // N3:       a qm:Optimization;
        Resource unq_jopt_res = compchemModel.createResource(createUUIDURIString(opt_mod), CompChemSematics.qmOptimization);
        // N3:       qm:hasSteps ( jim:step1 jim:step2 ) .
        Nodes step_nodes = opt_mod.query("./cml:module[@role='step']", CMLNamespace.CML_XPATH);
        List<RDFNode> step_rdf_list = new ArrayList<RDFNode>(step_nodes.size());
        for (int j = 0; j < step_nodes.size(); j++) {
            CMLModule step_mod = (CMLModule) step_nodes.get(j);
            CMLPropertyList prop_list = (CMLPropertyList) step_mod.getFirstCMLChild(CMLPropertyList.TAG);
            // N3:    jim:step2
            // N3:       a qm:OptimizationStep;
            Resource unq_opt_step_res = compchemModel.createResource(createUUIDURIString(step_mod), CompChemSematics.qmOptimizationStep);
            // N3:       qm:scfEnergy [
            // N3:           a cml:Property ;
            // N3:           cml:hasValue "-40.5111790287"^^xsd:double;
            // N3:           cml:hasUnits cml:molarEnergy ] .
            List<CMLProperty> props = prop_list.getPropertyDescendants();
            addPropertyOrParameterResourcesToTask(props, unq_opt_step_res);
            step_rdf_list.add(unq_opt_step_res);
        }
        Iterator<RDFNode> step_rdf_iter = step_rdf_list.iterator();
        RDFList step_list = compchemModel.createList(step_rdf_iter);
        unq_jopt_res.addProperty(CompChemSematics.qmHasSteps, step_list);
        return unq_jopt_res;
    }

    private Resource createFinalTaskResource(CMLModule final_mod) {
        // N3:   jim:j1final
        // N3:       a qm:FinalResult;
        Resource unq_jfinal_res = compchemModel.createResource(createUUIDURIString(final_mod), CompChemSematics.qmFinalResult);
        // N3:       qm:hasGeometry jim:mol2;
        CMLMolecule molecule = (CMLMolecule) final_mod.getFirstCMLChild(CMLMolecule.TAG);
        addMolecule(unq_jfinal_res, molecule);

        CMLPropertyList final_proplist = (CMLPropertyList) molecule.getFirstCMLChild(CMLPropertyList.TAG);
        List<CMLProperty> props = final_proplist.getPropertyDescendants();
        addPropertyOrParameterResourcesToTask(props, unq_jfinal_res);
        return unq_jfinal_res;
    }

    private Resource createJobResource(CMLModule job_mod, Resource unq_jinit_res, Resource unq_jopt_res, Resource unq_jfinal_res) {
        // N3:   jim:job1
        // N3:       a qm:ComputationalJob;
        Resource unq_job_res = compchemModel.createResource(createUUIDURIString(job_mod), CompChemSematics.qmComputationJob);
        // N3:       qm:hasInitialisation jim:j1init;
        unq_job_res.addProperty(CompChemSematics.qmHasInitialisation, unq_jinit_res);
        // N3:       qm:hasOptimization jim:j1opt;
        if (unq_jopt_res != null) {
            unq_job_res.addProperty(CompChemSematics.qmHasOptimization, unq_jopt_res);
        }
        unq_job_res.addProperty(CompChemSematics.qmHasFinalResult, unq_jfinal_res);
        return unq_job_res;
    }

    private void addPropertyOrParameterResourcesToTask(List<? extends CMLElement> props, Resource unq_jtask_res) {
        for (int j = 0; j < props.size(); j++) {
            // N3:   # What should we do about "unknown:unknown" units?
            // N3:       qm:dipole [
            // N3:           a cml:Vector3 ;
            // N3:           cml:hasValue ("1.51313187E-6" "-2.06531504E-7" "-1.331166E-7") ] .
            HasDictRef pp = (HasDictRef) props.get(j);
            String qmPredicate = formatDictRefToQMPredicate(pp.getDictRef());
            Resource bnode_pp_res = null;
            if (pp instanceof CMLProperty) {
                CMLProperty property = (CMLProperty) pp;
                SemPropertyTool semTool = SemPropertyTool.getOrCreateTool(property);
                bnode_pp_res = semTool.getResource(unq_jtask_res.getModel());
            } else if (pp instanceof CMLParameter) {
                CMLParameter parameter = (CMLParameter) pp;
                SemParameterTool semTool = SemParameterTool.getOrCreateTool(parameter);
                bnode_pp_res = semTool.getResource(unq_jtask_res.getModel());
            }
            if (bnode_pp_res != null) {
                unq_jtask_res.addProperty(CompChemSematics.qmProperty(qmPredicate), bnode_pp_res);
            }
        }
    }

    private CMLModule extractTaskFromJob(CMLModule job_mod, String role) throws UnexpectedCompChemSchema {
        Nodes task_nodes = job_mod.query("./cml:module[@role='" + role + "']", CMLNamespace.CML_XPATH);
        if (task_nodes.size() == 1) {
            return (CMLModule) task_nodes.get(0);
        } else if (task_nodes.size() == 0) {
            return null;
        } else {
            throw new UnexpectedCompChemSchema("CompChem CML can only contain 1 " + role + " module.");
        }
    }

    private String formatDictRefToQMPredicate(String dictRef) {
        String[] parts = dictRef.split("[:]");
        if (parts.length == 2) {
            return parts[1].replaceAll("\\.", "_");
        } else {
            throw new RuntimeException("Cannot format dictref : " + dictRef);
        }
    }

    /**
     * Setup prefixes for model. This is only for output to RDF, N3, etc.
     * @param compchemModel
     */
    private void setupNsprefixes() {
        compchemModel.setNsPrefix("cml", CompChemSematics.CML_SCHEMA_NS);
        compchemModel.setNsPrefix("cmlrdf", CompChemSematics.CMLRDF_SCHEMA_NS);
        compchemModel.setNsPrefix("qm", CompChemSematics.CMLQMRDF_SCHEMA_NS);
        compchemModel.setNsPrefix("chem", CompChemSematics.CMLAXIOM_NS);
        compchemModel.setNsPrefix("chemid", CompChemSematics.CHEMID_NS);
        compchemModel.setNsPrefix("xsd", CompChemSematics.XSD_NS);
        compchemModel.setNsPrefix("rdf", CompChemSematics.RDF_NS);
    }

    /**
     * This has to be uri generator or something.
     * @param local
     * @return
     */
    public final String createUUIDURIString(CMLElement element) {
        String computationInstance = getURIGenerator().createUUIDURI(element).toString();
        return computationInstance.toString();
    }

    private void loadDictionaries() {
        dictionaryCollection = new DictionaryCollection();
        dictionaryCollection.loadDictionary(DictionaryCollection.CompChemLocation);
        dictionaryCollection.loadDictionary(DictionaryCollection.PropertyLocation);
        dictionaryCollection.loadDictionary(DictionaryCollection.PropertyG03Location);
    }
    
}
