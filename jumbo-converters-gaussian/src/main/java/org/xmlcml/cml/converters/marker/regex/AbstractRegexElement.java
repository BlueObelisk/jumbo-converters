package org.xmlcml.cml.converters.marker.regex;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;

public abstract class AbstractRegexElement extends Element {
	static Logger LOG = Logger.getLogger(AbstractRegexElement.class);

	public final static String ANY = "*";
	
	public final static String MIN_COUNT = "minCount";
	public final static String MAX_COUNT = "maxCount";

	public final static String GROUP_ATT = "group";
	public final static String GROUP_START = "(";
	public final static String GROUP_END = ")";

	public final static String TRANSLATED_ATT = "translated";

	protected AbstractRegexElement(String name) {
		super(name);
	}

	static void createSubclasses(Element element) {
		Nodes childElements = element.query("*");
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = (Element) childElements.get(i);
			String name = childElement.getLocalName();
			Element newElement;
			if (name.equals(CaptureGroupElement.CAPTURE_GROUP_TAG)) {
				newElement = new CaptureGroupElement();
			} else if (name.equals(Expression.EXPRESSION_TAG)) {
				newElement = new Expression();
			} else if (name.equals(Quantifier.QUANTIFIER_TAG)) {
				newElement = new Quantifier();
			} else if (name.equals(Regex.REGEX_TAG)) {
				newElement = new Regex();
			} else {
				throw new RuntimeException("unknown tag "+name);
			}
			CMLUtil.transferChildren(childElement, newElement);
			CMLElement.copyAttributesFromTo(childElement, newElement);
			element.replaceChild(childElement, newElement);
			createSubclasses(newElement);
		}
	}

	protected void tidyQuantifiers() {
		Nodes expNodes = this.query("//"+Quantifier.QUANTIFIER_TAG);
		for (int i = 0; i < expNodes.size(); i++) {
			Node expressionNode = expNodes.get(i);
			String value = expressionNode.getValue().trim();
			value = (value.equals(CaptureGroupElement.CAPTURE_DISABLE)) ? CaptureGroupElement.CAPTURE_NO : CaptureGroupElement.CAPTURE_YES;
			Element parent = (Element) expressionNode.getParent();
			parent.addAttribute(new Attribute(CaptureGroupElement.CAPTURE_ATT, value));
			expressionNode.detach();
		}
	}

	void tidyCountExpressions() {
		Nodes expNodes = this.query("//"+Expression.EXPRESSION_TAG);
		for (int i = 0; i < expNodes.size(); i++) {
			Node expressionNode = expNodes.get(i);
			String value = expressionNode.getValue().trim();
			Element parent = (Element) expressionNode.getParent();
			String min = "1";
			String max = "1";
			Pattern pattern = Pattern.compile(Expression.QUANTIFIER_EXPRESSION);
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) {
				if (matcher.groupCount() >= 2) {
					min = matcher.group(1);
					max = min;
				} 
				max = CaptureGroupElement.extractOptionalMaxCount(max, matcher);
			} else if(value.equals(Expression.STAR_REGEX)) {
				min = "0";
				max = ANY;
			} else if(value.equals(Expression.PLUS_REGEX)) {
				min = "1";
				max = ANY;
			} else if(value.equals(Expression.QUEST_REGEX)) {
				min = "0";
				max = "1";
			}
			int imin = Integer.parseInt(min);
			int imax = (max.equals("*")) ? Integer.MAX_VALUE : Integer.parseInt(max);
			parent.addAttribute(new Attribute(MIN_COUNT, ""+imin));
			parent.addAttribute(new Attribute(MAX_COUNT, ""+imax));
			expressionNode.detach();
		}
	}

	void createCommentsFromTags() {
		Nodes childlessBrackets = this.query("//"+CaptureGroupElement.CAPTURE_GROUP_TAG+"[count(*)='0']");
		for (int i = 0; i < childlessBrackets.size(); i++) {
			AbstractRegexElement childlessBracket = (AbstractRegexElement) childlessBrackets.get(i);
			childlessBracket.createCommentFromTag();
		}
	}
	
	void createCommentFromTag() {
		String captureGroup = this.getAttributeValue(AbstractRegexElement.GROUP_ATT);
		Pattern pattern = Pattern.compile(Tag.CAPTURE_TAG);
		int start = 0;
		StringBuilder sb = new StringBuilder();
		while (true) {
			Matcher matcher = pattern.matcher(captureGroup);
			if (matcher.find(start)) {
				if (start != 0) {
					LOG.info("two tags in childlessBracket (maybe OR): "+captureGroup);
				}
				int s1 = matcher.end();
				if (start != 0) {
					sb.append(CMLConstants.S_SPACE);
				}
				sb.append(matcher.group(1));
				start = s1;
			} else {
				break;
			}
		}
		String comment = sb.toString().trim();
		if (comment.length() > 0) {
			setComment(comment);
		}
	}

	private void setComment(String value) {
		this.addAttribute(new Attribute(Comment.COMMENT_ATT, value));
	}

	public String getComment() {
		return getAttributeValue(Comment.COMMENT_ATT);
	}

	public void setGroup(String value) {
		this.addAttribute(new Attribute(AbstractRegexElement.GROUP_ATT, value));
	}

	public String getGroup() {
		return getAttributeValue(AbstractRegexElement.GROUP_ATT);
	}

	public void setTranslated(String value) {
		this.addAttribute(new Attribute(AbstractRegexElement.TRANSLATED_ATT, value));
	}

	public String getTranslated() {
		return getAttributeValue(AbstractRegexElement.TRANSLATED_ATT);
	}

	public Integer getMin() {
		String value = getAttributeValue(AbstractRegexElement.MIN_COUNT);
		Integer intValue = Utils.parseInteger(value);
		return intValue;
	}

	String extractCaptureGroupContentIntoAttributes() {
		StringBuilder captureSB = new StringBuilder();
		for (int i = 0; i < this.getChildCount(); i++) {
			Node childNode = this.getChild(i);
			if (childNode instanceof Text) {
				Text t = (Text)childNode;
				String s = t.getValue();
				s = Utils.reEscapeMetacharacters(s);
				s = extractHeadFromTailCommentedString(t, s);
				t.setValue(s);
				captureSB.append(s);
			} else {
				captureSB.append(AbstractRegexElement.GROUP_START+
						((AbstractRegexElement)childNode).extractCaptureGroupContentIntoAttributes()+
						AbstractRegexElement.GROUP_END);
			}
		}
		String capture = captureSB.toString();
		this.addAttribute(new Attribute(AbstractRegexElement.GROUP_ATT, capture));
		return capture;
	}
	
	String extractHeadFromTailCommentedString(Text t, String stringWithTrailingComment) {
		if (stringWithTrailingComment.endsWith(Comment.COMMENT_END)) {
			int idx = stringWithTrailingComment.lastIndexOf(Comment.COMMENT_START);
			String comment = stringWithTrailingComment.substring(idx+1).trim();
			((Element)t.getParent()).addAttribute(new Attribute(Comment.COMMENT_ATT, comment));
			stringWithTrailingComment = stringWithTrailingComment.substring(0, idx);
		}
		return stringWithTrailingComment;
	}

	int processOptionalCounts(int counter) {
		Elements childElements = this.getChildElements(CaptureGroupElement.CAPTURE_GROUP_TAG);
		for (int i = 0; i < childElements.size(); i++) {
			AbstractRegexElement childElement = (AbstractRegexElement) childElements.get(i);
			counter = childElement.processOptionalCounts(counter);
		}
		return counter;
	}


	void extractCommentAttributeForLeafNodes(String captureGroup, Element bracket, Pattern pattern) {
		Matcher matcher = pattern.matcher(captureGroup);
		if (matcher.matches()) {
			bracket.addAttribute(new Attribute(Comment.COMMENT_ATT, matcher.group(1)));
		}
	}

	void detachNodes(String xpath) {
		Nodes nodes = this.query(xpath);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	void removeDescendantCapturesAndText() {
		detachNodes("//@capture");
		detachNodes("//text()");
	}
	
	TaggedSequence getTaggedSequence() {
		Nodes regexes = this.query("ancestor-or-self::"+Regex.REGEX_TAG);
		if (regexes.size() != 1) {
			throw new RuntimeException("no regex ancestor");
		}
		return ((Regex)regexes.get(0)).getTaggedSequence();
	}
	
	public SVGElement createSVG() {
		SVGElement svg = new SVG();
		svg.appendChild(createSVG(0.0, 0.0));
		return svg;
	}
	
	public SVGElement createSVG(double x, double y) {
		SVGElement svgElement = null;
		Elements childElements = this.getChildElements();
		if (childElements.size() == 0) {
			svgElement = new SVGText();
			((SVGText)svgElement).setText(this.getComment()+" "+this.getTranslated());
			svgElement.setX(x);
			svgElement.setY(y);
			
		} else {
			svgElement = new SVGG();
			double height = 0.0;
			for (int i = 0; i < this.getChildElements().size(); i++) {
				AbstractRegexElement childElement = (AbstractRegexElement) this.getChildElements().get(i);
				SVGElement childSVG = childElement.createSVG(x, y);
				x += childSVG.getWidth();
				childSVG.setX(x);
				height = y + childSVG.getHeight();
				childSVG.setY(height);
				svgElement.appendChild(childSVG);
			}
			SVGText text = new SVGText();
			text.setX(svgElement.getX());
			text.setY(height);
			text.appendChild(this.getComment());
			svgElement.appendChild(text);
		}
		return svgElement;
	}

	protected void addMatchesRecursively(List<String> grouplist, int serial) {
	}

	public void debug(String msg) {
		CMLUtil.debug(this, msg);
	}
}
