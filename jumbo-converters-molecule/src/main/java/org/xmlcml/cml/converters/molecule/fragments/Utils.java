package org.xmlcml.cml.converters.molecule.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;


/**
 * Utility routines for CrystalEye.
 *
 * @author Nick Day
 */
public class Utils {
	
	private static final Logger LOG = Logger.getLogger(Utils.class);
	
	public static double round(double val, int places) {
		long factor = (long)Math.pow(10,places);
		val = val * factor;
		long tmp = Math.round(val);
		return (double)tmp / factor;
	}

	public static String getPathMinusMimeSet(File file) {
		String path = file.getAbsolutePath();
		String parent = file.getParent();
		String fileName = path.substring(path.lastIndexOf(File.separator)+1);
		String fileId = fileName;
		int idx = fileName.indexOf(".");
		if (idx != -1) {
			fileId = fileName.substring(0,fileName.indexOf("."));
		}
		return parent+File.separator+fileId;
	}

	public static String getPathMinusMimeSet(String path) {		
		return getPathMinusMimeSet(new File(path));
	}
	
	public static void appendToFile(File file, String content) {
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(content);
			fw.close();
		} catch(IOException e) {
			LOG.warn("IOException: "+e.getMessage());
		}
	}
	
	public static void ensureParentFile(File file) {
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
	}

	public static void writeText(File file, String content) {
		ensureParentFile(file);
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			throw new RuntimeException("Exception writing to file: "+file, e);
		}
	}

	public static void writeXML(File file, Document doc)  {
		File writeFile = file.getParentFile();
		if (writeFile != null && !writeFile.exists()) {
			writeFile.mkdirs();
		}
		try {
			Serializer serializer = new Serializer(FileUtils.openOutputStream(file));
			serializer.setIndent(2);
			serializer.write(doc);
		} catch (IOException e) {
			throw new RuntimeException("Could not write XML file to "+file);
		}
	}
	
	public static void writeXML(File file, Element element)  {
		writeXML(file, new Document((Element)element.copy()));
	}

	public static Document parseXml(String filePath) {
		return parseXml(new File(filePath));
	}

	public static Document parseXml(File file) {
		try {
			return new Builder().build(file);
		} catch (Exception e) {
			throw new RuntimeException("Exception parsing XML file ("+file+"), due to: "+e.getMessage(), e);
		}
	}
	
	public static Document parseXml(Reader reader) {
		Document doc;
		BufferedReader br = null;
		try {
			br = new BufferedReader(reader);
			doc = new Builder().build(br);
		} catch (Exception e) {
			throw new RuntimeException("Exception parsing XML due to: "+e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(br);
		}
		return doc;
	}
	
	public static Document parseUnvalidatedXml(File file) {
		org.apache.xerces.parsers.SAXParser xmlReader =  new org.apache.xerces.parsers.SAXParser() ;
		try {
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			return new Builder(xmlReader).build(file);
		} catch (Exception e) {
			throw new RuntimeException("Exception whilse parsing XML, due to: "+e.getMessage(), e);
		}
	}

	public static Document parseCml(String filePath) {
		return parseCml(new File(filePath));
	}

	public static Document parseCml(File file) {
		try {
			return new CMLBuilder().build(file);
		} catch (Exception e) {
			throw new RuntimeException("Exception parsing CML file due to: "+e.getMessage(), e);
		}
	}
	
	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(CrystalEyeConstants.CRYSTALEYE_DATE_FORMAT);
		return formatter.format(date);
	}

	public static void writeDateStamp(String path) {
		String dNow = getDate();
		Utils.writeText(new File(path), dNow);
	}

}