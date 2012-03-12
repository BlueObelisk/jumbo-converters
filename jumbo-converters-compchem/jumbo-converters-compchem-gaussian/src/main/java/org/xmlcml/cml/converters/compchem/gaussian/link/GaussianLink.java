package org.xmlcml.cml.converters.compchem.gaussian.link;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;


public class GaussianLink {
	protected List<String> lineList;
	private String linkName = null;
	protected CMLModule cmlModule = null;
	protected CMLMolecule molecule;
	public String line = null;
	public String previous_line = null;
	public int line_num = -1;
	
	protected GaussianLink() {
		lineList = new ArrayList<String>();
		setLinkName("default");
	}
	
	public GaussianLink(CMLMolecule molecule) {
		this();
		this.setMolecule(molecule);
	}
	
	protected String getTitle() {
		return "unprocessed link";
	}
	
	public CMLMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}

	public GaussianLink(GaussianLink glink, CMLMolecule molecule) {
		this(molecule);
		this.setGLink(glink);
	}
	
	void setGLink(GaussianLink glink) {
		this.setLinkName(glink.getLinkName());
		this.setLineList(glink.lineList);
	}

	void setLineList(List<String> lineList) {
		this.lineList = lineList;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public void add(String line) {
		lineList.add(line);
	}
	
	public String toString() {
		return "L "+getLinkName()+" / "+lineList.size();
	}
	
	public CMLModule convert2CML() {
		CMLModule cmlModule = null;
		GaussianLink subLink = null;
		
		// try to create class for link
		if (getLinkName() != null) {
			try {
				Class<?> linkClass = Class.forName(this.getClass().getPackage().getName()+".link.Link"+getLinkName());
				subLink = (GaussianLink) linkClass.newInstance();
				subLink.setMolecule(molecule);
				subLink.setGLink(this);
			} catch (ClassNotFoundException e) {
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		// if class exists use it
		if (subLink != null) {
			cmlModule = subLink.convert2CML();
			cmlModule.setDictRef("gau:link"+subLink.getLinkName());
			cmlModule.setTitle(subLink.getTitle());
		} else {
			// use default
			cmlModule = outputDefaultModule();
			cmlModule.setTitle(this.getTitle());
		}
		return cmlModule;
	}

	private CMLModule outputDefaultModule() {
		CMLModule cmlModule = new CMLModule();
		cmlModule.setDictRef("gau:link"+getLinkName());
		for (String line : lineList) {
			CMLScalar scalar = new CMLScalar(line);
			scalar.removeAttribute("dataType");
			cmlModule.appendChild(scalar);
		}
		return cmlModule;
	}

	protected String readLine() {
	    previous_line = line;
	    line = (line_num == lineList.size()) ? null : lineList.get(line_num++);
	    return line;
	}

	public String getLinkName() {
		return linkName;
	}
}

