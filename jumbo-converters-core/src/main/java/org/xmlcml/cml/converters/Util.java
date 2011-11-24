package org.xmlcml.cml.converters;

import java.io.IOException;
import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLTorsion;

public class Util {


	/**
	 * ??????????????
	 * @param bytes
	 * @return element
	 * @throws RuntimeException
	 */
	public static Element stripPrologAndParse(byte[] bytes) throws RuntimeException {
		StringBuffer sb = new StringBuffer();
		boolean foundRoot = false;
		boolean inAngle = false;
		for (byte b : bytes) {
			char c = (char) b;
			if (!foundRoot) {
				if (c == CMLConstants.C_LANGLE) {
					inAngle = true;
				} else {
					if (inAngle) {
						if (c == CMLConstants.C_RANGLE) {
							inAngle = false;
						} else if (Character.isLetter(c) || c == CMLConstants.C_UNDER) {
							sb.append(CMLConstants.C_LANGLE);
							sb.append(c);
							foundRoot = true;
						}
					}
					inAngle = false;
				}
			} else {
				sb.append(c);
			}
		}
		Element xml = null;
		try {
			xml = new Builder().build(new StringReader(sb.toString())).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse SVG input", e);
		}
		return xml;
	}

	public static void sanitizeOutput(AbstractConverter converter, CMLCml cml) {
		if (converter.isCMLLiteOutput()) {
			// remove bondStereo (CMLLite bug)
			Nodes nodes = cml.query("//cml:bondStereo", CMLConstants.CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
			// remove non-default attributes
			nodes = cml.query("//@*[not(namespace-uri()='')]", CMLConstants.CML_XPATH);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
	}

	@Deprecated
	/** use CMLUtil */
	public static Document stripDTDAndOtherProblematicXMLHeadings(String s) throws IOException {
		return CMLUtil.stripDTDAndOtherProblematicXMLHeadings(s);
	}

	/** create atom id from serial number.
	 *
	 * @param serial
	 * @return id
	 */
	public static String createAtomId(int serial) {
		return "a"+serial;
	}

	/** add angle to a cml element.
	 *
	 * @param element to add to
	 * @param atomRefs3
	 * @param value angle
	 */
	public static void addAngle(CMLElement element, String[] atomRefs3, double value) {
		CMLAngle angle = new CMLAngle();
		angle.setAtomRefs3(atomRefs3);
		angle.setXMLContent(value);
		element.appendChild(angle);
	}

	/** add length to a cml element.
	 *
	 * @param element to add to
	 * @param atomRefs2
	 * @param value length
	 */
	public static void addLength(CMLElement element, String[] atomRefs2, double value) {
		CMLLength length = new CMLLength();
		length.setAtomRefs2(atomRefs2);
		length.setXMLContent(value);
		element.appendChild(length);
	}

	/** add torsion to a element.
	 *
	 * @param element to add to
	 * @param atomRefs4
	 * @param value torsion
	 */
	public static void addTorsion(CMLElement element, String[] atomRefs4,
			double value) {
		CMLTorsion torsion = new CMLTorsion();
		torsion.setAtomRefs4(atomRefs4);
		torsion.setXMLContent(value);
		element.appendChild(torsion);
	}
}
