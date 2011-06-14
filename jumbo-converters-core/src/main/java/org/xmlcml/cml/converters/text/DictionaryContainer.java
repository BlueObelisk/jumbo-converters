package org.xmlcml.cml.converters.text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.euclid.Util;

public class DictionaryContainer {
	private final static Logger LOG = Logger.getLogger(DictionaryContainer.class);
	public static final String TAG = "dictionary";

	private static final String HREF = "href";
	private static final String URI = "uri";

	private Element refElement;
	private String uri;
	private String href;
	private CMLDictionary cmlDictionary;

	public DictionaryContainer(Element refElement) {
		this.refElement = refElement;
		CMLUtil.debug(this.refElement, "DICTIONARY");
		processChildElementsAndAttributes();
	}

	private void processChildElementsAndAttributes() {
		processAttributes();
	}

	private void processAttributes() {
		this.uri = refElement.getAttributeValue(URI);
		this.cmlDictionary = resolveURI(uri);
		this.href = refElement.getAttributeValue(HREF);
		if (this.cmlDictionary == null && href != null) {
			try {
				InputStream is = tryAsURL();
				if (is == null) {
					is = Util.getResourceUsingContextClassLoader(href, this.getClass());
				}
				if (is != null) {
					createDictionary(is);
				}
			} catch (Exception e) {
				throw new RuntimeException("cannot create dictionary", e);
			}
			if (cmlDictionary == null) {
				throw new RuntimeException("cannot create dictionary from "+uri+" or "+href);
			}
			checkDuplicates();
		}
	}

	private void createDictionary(InputStream is) throws ParsingException,
			ValidityException, IOException {
		Element element = new CMLBuilder().build(is).getRootElement();
//		((CMLElement)element).debug("XXXXXXXX");
		if (element != null && element instanceof CMLCml) {
			Element child = element.getChildElements().get(0);
			if (child == null || !(child instanceof CMLDictionary)) {
				throw new RuntimeException("Not a well-formed dictionary: "+uri+" or "+href);
			}
			cmlDictionary = (CMLDictionary) child;
		}
	}

	private InputStream tryAsURL() {
		InputStream is = null;
		try {
			is = new URL(href).openStream();
		} catch (Exception e) {
			// not a URL
			LOG.warn("Cannot open as URL: "+href);
		}
		return is;
	}
	
	private void checkDuplicates() {
		CMLElements<CMLEntry> entryElements = cmlDictionary.getEntryElements();
		Set<String> idSet = new HashSet<String>();
//		System.out.println(">>>>>Dictionary: "+cmlDictionary.getNamespace());
		for (CMLEntry entry : entryElements) {
			String id = entry.getId();
			if (idSet.contains(id)) {
				System.out.println("    Duplicate id in dictionary: "+id); 
				throw new RuntimeException("    Duplicate id in dictionary: "+id); 
			}
			idSet.add(id);
		}
		System.out.println("==================================================");
	}

	private CMLDictionary resolveURI(String uri2) {
		LOG.trace("there is no magic yet for resolving dictionaries");
		return null;
	}

	public CMLDictionary getDictionary() {
		return cmlDictionary;
	}

}
