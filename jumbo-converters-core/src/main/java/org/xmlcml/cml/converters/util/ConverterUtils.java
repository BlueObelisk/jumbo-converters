package org.xmlcml.cml.converters.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.cml.interfacex.HasDictRef;

public class ConverterUtils {

	private final static Logger LOG = Logger.getLogger(ConverterUtils.class);
	
	public static void addPrefixedDictRef(HasDictRef element, String prefix, String value) {
		element.setDictRef(DictRefAttribute.createValue(prefix, value));
	}

	/** treats the 3D atomCoordinates as atom children of another molecule
	 * the coordinates in fromAtom are wrapped in a CMLVector3 and added as children of corresponding
	 * atoms in toMolecule. The Vector is given a dictRef of prefix:localDictRef
	 * atom correspondence is done by ids. Non-existent atoms in toMolecule are ignored
	 * no atoms or molecules are deleted 
	 * @param fromMolecule
	 * @param toMolecule
	 * @param prefix for dictRef
	 * @param localDictRef
	 */
	public static void copyCoordinateVectorAsAtomChildren(
			CMLMolecule fromMolecule, CMLMolecule toMolecule, String prefix, String localDictRef) {
		if (toMolecule == null) {
			throw new RuntimeException("toMolecule must not be null");
		}
		for (int j = 0; j < fromMolecule.getAtomCount(); j++) {
			CMLAtom fromAtom = fromMolecule.getAtom(j);
			CMLVector3 vector3 = new CMLVector3(fromAtom.getXYZ3().getArray());
			vector3.setDictRef(DictRefAttribute.createValue(prefix, localDictRef));
			String id = fromAtom.getId();
			if (id == null) {
				throw new RuntimeException("Atoms must have IDs"+fromAtom.toXML());
			}
			CMLAtom toAtom = toMolecule.getAtomById(id);
			if (toAtom != null) {
				toAtom.appendChild(vector3);
			}
		}
	}

	public static Document parseHtmlWithTagSoup(InputStream is) {
        try {
            Builder builder = getTagsoupBuilder();
            return builder.build(is);
        } catch (Exception e) {
            throw new RuntimeException("Exception whilse parsing XML, due to: "+e.getMessage(), e);
        }
    }

	public static Document parseHtmlWithTagSoup(File file) {
		try {
			return parseHtmlWithTagSoup(new FileInputStream(file));
		} catch (FileNotFoundException e) {
            throw new RuntimeException("Exception whilse parsing HTML, due to: "+e.getMessage(), e);
		}
    }

	/*
	<dependency>
	    <groupId>org.ccil.cowan.tagsoup</groupId>
	    <artifactId>tagsoup</artifactId>
	    <version>1.0.1</version>
	</dependency>
*/

	public static Builder getTagsoupBuilder() {
		XMLReader tagsoup = null;
		try {
		    tagsoup = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");
		} catch (SAXException e) {
		    throw new RuntimeException("Exception whilst creating XMLReader from org.ccil.cowan.tagsoup.Parser");
		}
		return new Builder(tagsoup);
	}

	/*
	 * converts all files with suffix to output files in same directory
	 * Essentially for developers
	 * @param inputSuffix suffix including leading "." 
	 * @param outputSuffix suffix including leading "." 
	 * 
	 */
	public static void convertFilesInDirectory(AbstractConverter converter, 
			String dirName, String inputSuffix, String outputSuffix) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		if (files == null) {
			throw new RuntimeException("No files found in "+dir.getAbsolutePath());
		}
		LOG.info("Processing "+files.length+" files");
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(inputSuffix)) {
				File out = new File(file.getAbsolutePath()+outputSuffix);
				LOG.info("converting "+file+" to "+out);
				converter.convert(file, out);
			}
		}
	}
	
	/**
	 * takes care of the baseURI stuff
	 * @param baseUri
	 * @param resourceName
	 * @param clazz
	 * @return element
	 */
	public static Element buildElementIncludingBaseUri(String baseUri, String resourceName, Class<?> clazz) {
		try {
			InputStream in = clazz.getResourceAsStream(resourceName);
			if (in == null) {
				throw new FileNotFoundException("Resource not found: "+resourceName+"; baseUri: "+baseUri);
			}
			try {
				Builder builder = new Builder();
				Document doc = builder.build(in, baseUri);
				return doc.getRootElement();
			} finally {
				IOUtils.closeQuietly(in);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error creating resource", e);
		}
	}


}
