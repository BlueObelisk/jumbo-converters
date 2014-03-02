package org.xmlcml.cml.converters.compchem.nwchem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;

public class DictionaryEditor {

	private static Logger LOG = Logger.getLogger(DictionaryEditor.class);
	
	private CMLDictionary dictionary;
	private List<Element> templateElementList;
	private Map<String, List<Element>> commentByDictRefMap;
	private String prefix;

	private Map<String, CMLEntry> entryMap;

	public static void main(String[] args) {
		String dictionaryName = (args.length >= 1) ? args[0] : 
			"src/main/resources/org/xmlcml/cml/converters/compchem/nwchem/nwchemDict.xml";
		String templateDirname = (args.length >= 2) ? args[1] : 
			"src/main/resources/org/xmlcml/cml/converters/compchem/nwchem/log/templates";
		String outdict = (args.length >= 3) ? args[2] : "out/nwchemDict.xml";
		String prefix = (args.length >= 4) ? args[3] : "n";
		DictionaryEditor editor = new DictionaryEditor();
		editor.readDictionary(prefix, dictionaryName);
		editor.readTemplateDirectory(templateDirname);
		editor.editDictionary();
		editor.writeDictionary(outdict);
	}

	private void readDictionary(String prefix, String dictionaryFilename) {
		this.prefix = prefix;
		try {
			dictionary = (CMLDictionary) new CMLBuilder().build(new FileInputStream(dictionaryFilename)).getRootElement().getChildElements().get(0);
		} catch (Exception e) {
			throw new RuntimeException("cannot read dictionary "+dictionaryFilename, e);
		}
		entryMap = new HashMap<String, CMLEntry>();
		CMLElements<CMLEntry> entries = dictionary.getEntryElements();
		for (CMLEntry entry : entries) {
			String id = entry.getId();
			entryMap.put(id, entry);
		}
	}
	
	private void readTemplateDirectory(String templateDirname) {
		File templateDir = new File(templateDirname);
		File[] templateFiles = templateDir.listFiles();
		if (templateFiles != null) {
			templateElementList = new ArrayList<Element>();
			for (File templateFile : templateFiles) {				
				try {
					if (templateFile.getPath().endsWith(".xml")) {
						Element templateElement = new Builder().build(templateFile).getRootElement();
						templateElementList.add(templateElement);
					}
				} catch (Exception e) {
					LOG.warn("cannot read template "+templateFile, e);
				}
			}
		}
	}
	
	private void writeDictionary(String string) {
		List<String> keys = new ArrayList<String>(commentByDictRefMap.keySet());
		Collections.sort(keys);
		int count = 0;
		for (String dictRefValue : keys) {
			if (dictRefValue.startsWith(prefix+":")) {
				String localId = DictRefAttribute.getLocalName(dictRefValue);
				List<Element> commentList = commentByDictRefMap.get(dictRefValue);
				int size = commentList.size();
				if (size > 1) {
					System.out.println(">>>"+size+">>>"+dictRefValue);
				}
	//			for (Element comment : commentList) {
	//				CMLUtil.debug(comment, "XXX");
	//			}
				count++;
			}
		}
		System.out.println("Final count: "+count);
	}

	private void editDictionary() {
		commentByDictRefMap = new HashMap<String, List<Element>>();
		for (Element templateElement : templateElementList) {
			Nodes inCommentNodes = templateElement.query("comment[starts-with(@class,'example.input')]");
			Nodes outCommentNodes = templateElement.query("comment[starts-with(@class,'example.output')]");
			if (inCommentNodes.size() == 1 && outCommentNodes.size() == 1) {
				Element inComment = (Element) inCommentNodes.get(0);
				Element outComment = (Element) outCommentNodes.get(0);
				Nodes outDictRefs = outComment.query(".//@dictRef");
				for (int i = 0; i < outDictRefs.size(); i++) {
					String dictRefValue = outDictRefs.get(i).getValue();
					List<Element> commentList = commentByDictRefMap.get(dictRefValue);
					if (commentList == null) {
						commentList = new ArrayList<Element>();
						commentByDictRefMap.put(dictRefValue, commentList);
					}
					commentList.add(inComment);
				}
			}
		}
	}

}
