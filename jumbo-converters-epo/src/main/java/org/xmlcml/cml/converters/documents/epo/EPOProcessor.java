package org.xmlcml.cml.converters.documents.epo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.cml.base.CMLConstants;


import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class EPOProcessor {

	public static final String E_ABSTRACT = "abstract";
	public static final String E_DESCRIPTION = "description";
	public static final String E_HEADING = "heading";
	public static final String E_P = "p";
	public static final String A_TITLE = "title";
	
	private Element inputElement;
	private Element outputElement;
	private Map<String, Element> childElementMap;
	private Set<String> skipSections;
	private Set<String> paraStopSet;
	private Element abstractElement;
	private Element descriptionElement;
	
	
	private final static String E_FIELD = "field";
	private NameReplacer fieldReplacer = new NameReplacer(E_FIELD, "(technical\\s+)?field(\\s+.*)?");
	private final static String E_PRIOR_ART = "priorArt";
	private NameReplacer priorArtReplacer = new NameReplacer(E_PRIOR_ART, "(.*\\s+)?(prior|background)(\\s+.*(art(s)?|invention)(\\s+.*)?)?");
	private final static String E_SUMMARY_INVENTION = "summaryOfInvention";
	private NameReplacer summaryInventionReplacer = new NameReplacer(E_SUMMARY_INVENTION, "(.*\\s+)?summary(\\s+.*invention(\\s+.*)?)?");
	private final static String E_EFFECT_INVENTION = "effectOfInvention";
	private NameReplacer effectInventionReplacer = new NameReplacer(E_EFFECT_INVENTION, "(.*\\s+)?(effect(s)?|benefit(s?))(\\s+.*invention(\\s+.*)?)?");
	private final static String E_DISCLOSURE_INVENTION = "disclosureOfInvention";
	private NameReplacer disclosureInventionReplacer = new NameReplacer(E_DISCLOSURE_INVENTION, "(.*\\s+)?disclosure(\\s+.*invention(\\s+.*)?)");
	private final static String E_EXPLANATION = "explanation";
	private NameReplacer explanationReplacer = new NameReplacer(E_EXPLANATION, "(.*\\s+)?explanation(s)?(\\s+.*?)");
	private final static String E_PROBLEMS = "problemsToBeSolved";
	private NameReplacer problemsReplacer = new NameReplacer(E_PROBLEMS, "(.*\\s+)?problem(s)?(\\s+.*solv.+(\\s+.*)?)?");
	private final static String E_MEANS_SOLVING = "meansOfSolving";
	private NameReplacer meansSolvingReplacer = new NameReplacer(E_MEANS_SOLVING, "(.*\\s+)?means(\\s+.*(solv.+|accomplish.+)(\\s+.*)?)?");
	private final static String E_MODE_CARRYINGOUT = "modeCarryingOut";
	private NameReplacer modeCarryingOutReplacer = new NameReplacer(E_MODE_CARRYINGOUT, "(.*\\s+)?mode(\\s+.*carry.+(\\s+.*)?)?");
	private final static String E_DRAWINGS = "drawings";
	private NameReplacer drawingsReplacer = new NameReplacer(E_DRAWINGS, "(.*\\s+)?drawings(\\s+.*)?");
	private final static String E_USES = "uses";
	private NameReplacer usesReplacer = new NameReplacer(E_USES, "(.*\\s+)?uses(\\s+.*)?");
	private final static String E_DESCRIPTION_INVENTION = "descriptionOfInvention";
	private NameReplacer descriptionInventionReplacer = new NameReplacer(E_DESCRIPTION_INVENTION, "(detailed)?\\s+description(\\s+.*(invention|embodiment)(\\s+.*)?)?");
	private final static String E_PROCEDURES_LIST = "procedures";
	private NameReplacer proceduresListReplacer = new NameReplacer(E_PROCEDURES_LIST, "(general\\s+)?procedure(s)?(\\s+.*)?");
	private final static String E_EXAMPLE_LIST = "exampleList";
	private NameReplacer exampleListReplacer = new NameReplacer(E_EXAMPLE_LIST, "\\s*\\(?examples(:)?\\s*(\\S?.*)");
	private final static String E_EXAMPLE = "example";
	private NameReplacer exampleReplacer = new NameReplacer(E_EXAMPLE, "(.*\\s+)?example(:|\\-)?(\\s+.*)?");
	private final static String E_PREPARATION = "preparation";
	private NameReplacer preparationReplacer = new NameReplacer(E_PREPARATION, "(.*\\s+)?(preparation|synthesis)(\\s+.*)?");
	// do this with OSCAR properly
	private final static String E_COMPOUND = "compound";
	private NameReplacer compoundReplacer = new NameReplacer(E_COMPOUND, "(.*)\\s+(.*)(oxy|yl|benz|phen)+(.*)(\\s+.*)?");
	
	public EPOProcessor() {
		childElementMap = new HashMap<String, Element>();
		init();
	}
	
	private void init() {
		skipSections = new HashSet<String>();
		skipSections.add("SDOBI");
		skipSections.add("maths");
		skipSections.add("patcit");

		// may be obsolete
		paraStopSet = new HashSet<String>();
		paraStopSet.add("claims");
		paraStopSet.add(E_HEADING);
	}
	
	public void setInput(Element xml) {
		inputElement = xml;
	}
	
	public Element getOutput() {
		return outputElement;
	}
	public void analyze() {
		String inputName = inputElement.getLocalName();
		outputElement = new Element(inputName);
		childElementMap.put(inputName, outputElement);
		analyze(inputElement);
	}
	
	public void analyze(Element inputParentElement) {
		String inputParentName = inputParentElement.getLocalName();
		// make elements which cannot be bundled with para
		/*
		if (paraStopSet.contains(inputParentName)) {
			inputParentElement.addAttribute(new Attribute("paraStop", "true"));
		}
		*/
		Element outputParentElement = childElementMap.get(inputParentElement.getLocalName()); 
		Elements inputChildElements = inputParentElement.getChildElements();
		for (int i = 0; i < inputChildElements.size(); i++) {
			Element inputChildElement = inputChildElements.get(i);
			String inputChildName = inputChildElement.getLocalName();
			if (skipSections.contains(inputChildName)) {
				continue; // header stuf
			}
				
			if (outputParentElement.getChildElements(inputChildName).size() == 0) {
				Element childOutputElement = new Element(inputChildName);
				outputParentElement.appendChild(childOutputElement);
				if (!childElementMap.containsKey(inputChildName)) {
					childElementMap.put(inputChildName, childOutputElement);
				}
			}
			analyze(inputChildElement);
		}
	}

	public void process() {
		outputElement = new Element(inputElement);
		
		abstractElement = outputElement.getFirstChildElement(E_ABSTRACT);
		packageParas(abstractElement);
		
		descriptionElement = outputElement.getFirstChildElement(E_DESCRIPTION);
		packageParas(descriptionElement);
		// try to interpret headers
		interpretHeadings();
	}
	
	private void packageParas(Element parentElement) {
		Element headingElement = null;
		Elements childElements = parentElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String childName = childElement.getLocalName();
			if (E_HEADING.equals(childName)) {
				headingElement = childElement;
				String value = headingElement.getValue();
				value = stripBrackets(value);
				headingElement.addAttribute(new Attribute(A_TITLE, value));
				headingElement.removeChildren();
			} else if (E_P.equals(childName)) {
				// no heading, retain on parent
				if (headingElement == null) {
//					throw new RuntimeException("null header");
				} else {
					parentElement.removeChild(childElement);
					headingElement.appendChild(childElement);
				}
			} else {
				throw new RuntimeException("unknown element in "+parentElement.getLocalName()+": "+childName);
			}
		}
	}

	private void interpretHeadings() {
		interpretDescriptionHeadings();
	}
	
	private void interpretDescriptionHeadings() {
		Elements headingElements = descriptionElement.getChildElements(E_HEADING);
		for (int i = 0; i < headingElements.size(); i++) {
			Element headingElement = headingElements.get(i);
			String name = headingElement.getAttributeValue(A_TITLE);
			if (name == null) {
				//
			} else if (fieldReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (priorArtReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (summaryInventionReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (effectInventionReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (drawingsReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (problemsReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (explanationReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (meansSolvingReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (modeCarryingOutReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (disclosureInventionReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (descriptionInventionReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (proceduresListReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (exampleListReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (exampleReplacer.replaceHeading(descriptionElement, headingElement)) {
				
			} else if (preparationReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (usesReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else if (compoundReplacer.replaceHeading(descriptionElement, headingElement)) {
			} else {
				System.out.println("..."+headingElement.getAttributeValue(EPOProcessor.A_TITLE));
			}
		}
		// wrap examples - first single level
		Elements childElements = descriptionElement.getChildElements();
		Element exampleListElement = null;
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String name = childElement.getLocalName();
			// keep track of latest exampleList
			if (E_EXAMPLE_LIST.equals(name)) {
				exampleListElement = childElement;
			} else if (exampleListElement != null) {
				// nest examples
				descriptionElement.removeChild(childElement);
				exampleListElement.appendChild(childElement);
			}
		}
	}
	
	private static String stripBrackets(String s) {
		// some headings are of form [foo] or [foo 1] or <foo> or (foo)
		s = stripBrackets(s, CMLConstants.S_LANGLE, CMLConstants.S_RANGLE);
		s = stripBrackets(s, CMLConstants.S_LBRAK, CMLConstants.S_RBRAK);
		s = stripBrackets(s, CMLConstants.S_LSQUARE, CMLConstants.S_RSQUARE);
		return s;
	}
	
	private static String stripBrackets(String title, String lb, String rb) {
		if (title.startsWith(lb) && title.endsWith(rb)) {
			title = title.substring(lb.length(), title.length()-rb.length());
			title = title.trim();
		}
		return title;
	}
}
class NameReplacer {
	String elementName;
	Pattern pattern;
	
	public NameReplacer(String name, String patternS) {
		this.elementName = name;
		this.pattern = Pattern.compile(patternS);
	}
	
	boolean replaceHeading(Element parentElement, Element headingElement) {
		boolean replaced = false;
		String title = headingElement.getAttributeValue(EPOProcessor.A_TITLE);
		title = title.toLowerCase().trim();
		Matcher matcher = pattern.matcher(title);
		if (matcher.matches()) {
			headingElement.setLocalName(elementName);
			replaced = true;
		}
		return replaced;
	}
	
}
