package org.xmlcml.cml.converters.compchem.input;

import java.util.List;

import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;


public class Job extends CMLModule {
	private static Logger LOG = Logger.getLogger(Job.class);

	private Directives directives;
	private QM qm;
	private Operations operations;
	private AbstractCompchemInputProcessor processor;
	private CMLMolecule molecule = null;
	
	public Job(CMLModule jobModule, AbstractCompchemInputProcessor processor) {
		this.processor = processor;
		init(jobModule);
	}
	
	private void init(CMLModule jobModule) {
		directives = new Directives(processor);
		this.appendChild(directives);
		qm = new QM(processor);
		this.appendChild(qm);
		operations = new Operations(processor);
		this.appendChild(operations);
		getMolecule(jobModule); 
		addJobModule(jobModule);
		CMLElement.copyAttributesFromTo(jobModule, this);
	}

	public QM getQm() {
		return qm;
	}

	public Operations getOperations() {
		return operations;
	}

	public Directives getDirectives() {
		return directives;
	}

	private void getMolecule(CMLModule jobModule) {
		Nodes nodes = jobModule.query("./cml:molecule", CMLConstants.CML_XPATH);
		molecule = (nodes.size() == 1) ? (CMLMolecule) nodes.get(0).copy() : null;
		if (molecule == null) {
			molecule = processor.inputMolecule;
		}
	}
	
	public String getMoleculeRef() {
		String moleculeRef = null;
		if (molecule != null) {
			
			moleculeRef = molecule.getRef();
		}
		return moleculeRef;
	}
	
	void copyGlobalModule(CMLModule globalModule) {
		if (globalModule != null) {
			List<CMLElement> childElements = globalModule.getChildCMLElements();
			for (int i = childElements.size()-1; i >= 0; i--) {
				CMLElement copyElement = (CMLElement) childElements.get(i).copy();
				if (copyElement instanceof CMLModule) {
					addModule((CMLModule) copyElement);
				} else {
					LOG.warn("Unexpected child of globalModule: "+copyElement);
				}
			}
		}
	}

	void addJobModule(CMLModule jobModule) {
		CMLElement.copyAttributesFromTo(jobModule, this);
		Elements childModules = jobModule.getChildCMLElements(CMLModule.TAG);
		for (int i = 0; i < childModules.size(); i++) {
			CMLModule childModule = (CMLModule) childModules.get(i);
			addModule((CMLModule)childModule.copy());
		}
	}

	private void addModule(CMLModule childModule) {
		String role = childModule.getRole();
		if (role == null) {
			throw new RuntimeException("all module children should have @role");
		} else if (role.equals(Directives.ROLE)) {
			directives.addModule(childModule);
		} else if (role.equals(QM.ROLE)) {
			qm.addModule(childModule);
		} else if (role.equals(Operations.ROLE)) {
			operations.addModule(childModule);
		} else {
			throw new RuntimeException("unknown role: "+role);
		}
	}
	
}
