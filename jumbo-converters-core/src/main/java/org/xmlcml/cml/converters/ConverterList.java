package org.xmlcml.cml.converters;

import java.io.File;
import java.io.FileOutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLList;

public class ConverterList {

	private static final Logger LOG = Logger.getLogger( ConverterList.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	protected CMLList list;
	
	public ConverterList() {
		list = new CMLList();
	}
	
	public Element getList() {
		return list;
	}
	
	public void writeFile(String filename) {
		try {
			Element listCopy = (Element)list.copy();
			if (listCopy.getChildElements().size() > 0) {
				FileOutputStream fos = FileUtils.openOutputStream(new File(filename)) ;
				Document doc = new Document(listCopy);
				Serializer ser = new Serializer(fos);
				ser.setIndent(1);
				ser.write(doc);
				IOUtils.closeQuietly(fos) ;
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Cannot write list ", e);
		}
	}

}
