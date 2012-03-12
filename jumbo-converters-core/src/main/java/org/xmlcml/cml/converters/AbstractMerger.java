package org.xmlcml.cml.converters;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.io.File;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractMerger extends AbstractConverter {

	private static final Logger LOG = Logger.getLogger( AbstractMerger.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private String xpath;
	protected File outputDirectory;
	protected File outputFile;
	
	public AbstractMerger() {
	}
	
	@Override
	public Element convertToXML(Element cml) {
		checkXPath();
		checkFile();
		Nodes splitNodes = cml.query(xpath, CML_XPATH);
		for (int i = 0; i < splitNodes.size(); i++) {
			outputNode(splitNodes.get(i), i);
		}
		return null;
	}
	
	protected abstract void outputNode(Node node, int serial);
	
	private void checkFile() {
		if (outputFile == null) {
			throw new RuntimeException("Must set output file");
		}
		if (outputDirectory == null) {
			throw new RuntimeException("Must set outputDirectory");
		}
	}
	private void checkXPath() {
		if (xpath == null) {
			throw new RuntimeException("Must set xpath for splitter");
		}
	}
	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	/** sets outputDirectory and current outputFile
	 * if outputDirectory is null, it is assumed to be taken from full pathname 
	 * of outputFile.
	 * if outputDirectory is not null, assumed to override directory of outputFile
	 * but subclasses can create their own logic
	 * @param outputDirectory
	 * @param outputFile
	 */
	public void setFiles(File outputDirectory, File outputFile) {
		this.outputDirectory = outputDirectory;
		this.outputFile = outputFile;
	}
}
