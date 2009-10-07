package org.xmlcml.cml.converters.cml.index;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;

import java.io.File;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractIndexer;
import org.xmlcml.cml.converters.Command;
import org.xmlcml.cml.converters.Type;

/**
 * applies an xpath expression to index files
 * returns XML document with <entry> elements for those 
 * files which fulfil index. 
 * Example
 * index on "//*[local-name()='cml']/@id"
 * gives:

<index>
<entry key="pyridone">
<ref id="pyridone" file="C:\Users\pm286\workspace\jumbo-converters\src\test\resources\index\index\in\pyridone.cml"/></entry>
<entry key="I">
<ref id="I" file="C:\Users\pm286\workspace\jumbo-converters\src\test\resources\index\index\in\test.cml"/>
</entry>
</index>
 * @author pm286
 *
 */
public class CMLIndexer extends AbstractIndexer {

	private static final Logger LOG = Logger.getLogger(CMLIndexer.class);
//	private List<String> xPathList = null;
	private Element element = null;
	
	protected String getIndexKey() {
		String key = null;
		xPathList = getCommand().getXPathList();
		if (xPathList == null || xPathList.size() == 0) {
			throw new RuntimeException("Indexer requires xpath");
		} else if (xPathList.size() > 1) {
			LOG.info("Can only use first index at present");
		}
		Nodes nodes = element.query(xPathList.get(0), CML_XPATH);
		key = (nodes.size() != 1) ? null : nodes.get(0).getValue();
		return key;
	}
	
	public Type getInputType() {
		return Type.CML;
	}
	
	@Override
	public void convert(File infile, File outfile) {
		try {
			element = (CMLElement) new CMLBuilder().build(infile).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("exception ", e);
			// could be many reasons
		}
		addToIndex(element, infile.getAbsolutePath());
	}

	@Override
	public int getConverterVersion() {
		return 0;
	}


}
