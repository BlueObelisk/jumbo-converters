package org.xmlcml.cml.converters.cif.filter;

import java.io.IOException;

import nu.xom.Document;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Util;

public class CrystalFixture {

	public static Document OK_DOC = null;
	public static CMLElement OK_CML = null;
	static {
		try {
			OK_DOC = CMLUtil.parseQuietlyToCMLDocument(
				Util.getResource("cif/cifdisord/mbok1z.complete.cml").openStream());
		} catch (IOException e) {
			throw new RuntimeException("Cannot parse CML", e);
		}
		OK_CML = (CMLElement) OK_DOC.getRootElement();
	};
}
