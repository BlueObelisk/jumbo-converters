package org.xmlcml.cml.converters.cml;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;

/** selects one or more nodes from a cmlElement
 * convenience methods
 * uses Xpath
 * 
 * @author pm286
 *
 */
public class CMLSelector {

	static Logger LOG = Logger.getLogger(CMLSelector.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	public static String IMMEDIATE_MOLECULE_CHILDREN =
		"./cml:molecule";
	public static String IMMEDIATE_MOLECULE_CHILD =
		"./cml:molecule[1]";
	public static String TOPLEVEL_MOLECULE_DESCENDANTS_OR_SELF =
		"//cml:molecule[not(ancestor::cml:molecule)]";
	public static String TOPLEVEL_MOLECULE_DESCENDANT_OR_SELF =
		"//cml:molecule[not(ancestor::cml:molecule)][1]";
	public static String TOPLEVEL_MOLECULE_DESCENDANTS =
		"//cml:molecule[not(ancestor::cml:molecule)]";
	public static String TOPLEVEL_MOLECULE_DESCENDANT =
		".//cml:molecule[not(ancestor::cml:molecule)][1]";
	
	public static String IMMEDIATE_REACTION_CHILDREN =
		"./cml:reaction";
	public static String IMMEDIATE_REACTION_CHILD =
		"./cml:reaction[1]";
	public static String TOPLEVEL_REACTION_DESCENDANTS =
		".//cml:reaction[not(ancestor::cml:reaction)]";
	public static String TOPLEVEL_REACTION_DESCENDANT =
		".//cml:reaction[not(ancestor::cml:reaction)][1]";
	
	private CMLElement rootElement;
	private CMLMolecule molecule;
	private List<CMLMolecule> moleculeList;
	private CMLReaction reaction;
	private List<CMLReaction> reactionList;
	
	@SuppressWarnings("unused")
	private CMLSelector() {
		
	}
	public CMLSelector(CMLElement rootElement) {
		this.setRootElement(rootElement);
	}
	public CMLElement getRootElement() {
		return rootElement;
	}

	public void setRootElement(CMLElement rootElement) {
		this.rootElement = rootElement;
	}

	public List<CMLMolecule> getImmediateMoleculeChildren() {
		moleculeList = new ArrayList<CMLMolecule>();
		if (rootElement != null) {
			Nodes nodes = rootElement.query(IMMEDIATE_MOLECULE_CHILDREN, CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				moleculeList.add((CMLMolecule) nodes.get(i));
			}
		}
		return moleculeList;
	}
	
	public CMLMolecule getImmediateMoleculeChild(boolean checkUnique) {
		getImmediateMoleculeChildren();
		getSingleMolecule(checkUnique);
		return molecule;
	}
	
	public List<CMLMolecule> getToplevelMoleculeDescendants() {
		moleculeList = new ArrayList<CMLMolecule>();
		if (rootElement != null) {
			Nodes nodes = rootElement.query(TOPLEVEL_MOLECULE_DESCENDANTS, CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				moleculeList.add((CMLMolecule) nodes.get(i));
			}
		}
		return moleculeList;
	}
	
	public CMLMolecule getToplevelMoleculeDescendant(boolean checkUnique) {
		getToplevelMoleculeDescendants();
		getSingleMolecule(checkUnique);
		return molecule;
	}
	public List<CMLMolecule> getToplevelMoleculeDescendantsOrSelf() {
		moleculeList = new ArrayList<CMLMolecule>();
		if (rootElement != null) {
			Nodes nodes = rootElement.query(TOPLEVEL_MOLECULE_DESCENDANTS_OR_SELF, CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				moleculeList.add((CMLMolecule) nodes.get(i));
			}
		}
		return moleculeList;
	}
	
	public CMLMolecule getToplevelMoleculeDescendantOrSelf(boolean checkUnique) {
		getToplevelMoleculeDescendantsOrSelf();
		getSingleMolecule(checkUnique);
		return molecule;
	}
	
	private void getSingleMolecule(boolean checkUnique) {
		molecule = null;
		if (moleculeList.size() > 0) {
			if (!checkUnique || moleculeList.size() == 1) {
				molecule = moleculeList.get(0);
			}
		}
		moleculeList = null;
	}
	
	public List<CMLReaction> getImmediateReactionChildren() {
		reactionList = new ArrayList<CMLReaction>();
		if (rootElement != null) {
			Nodes nodes = rootElement.query(IMMEDIATE_REACTION_CHILDREN, CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				reactionList.add((CMLReaction) nodes.get(i));
			}
		}
		return reactionList;
	}
	
	public CMLReaction getImmediateReactionChild(boolean checkUnique) {
		getImmediateReactionChildren();
		getSingleReaction(checkUnique);
		return reaction;
	}
	
	public List<CMLReaction> getToplevelReactionDescendants() {
		reactionList = new ArrayList<CMLReaction>();
		if (rootElement != null) {
			Nodes nodes = rootElement.query(TOPLEVEL_REACTION_DESCENDANTS, CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				reactionList.add((CMLReaction) nodes.get(i));
			}
		}
		return reactionList;
	}
	
	public CMLReaction getToplevelReactionDescendant(boolean checkUnique) {
		getToplevelReactionDescendants();
		getSingleReaction(checkUnique);
		return reaction;
	}
	
	private void getSingleReaction(boolean checkUnique) {
		reaction = null;
		if (reactionList.size() > 0) {
			if (!checkUnique || reactionList.size() == 1) {
				reaction = reactionList.get(0);
			}
		}
		reactionList = null;
	}
}
