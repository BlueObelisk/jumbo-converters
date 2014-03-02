package org.xmlcml.cml.converters;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;

/**
 * A general contract for a converter between different types.
 * 
 * @author jimdowning
 */
public interface Converter {
	
	/**
	 * 
	 * @param in
	 * @param out
	 */
	void convert(File in, OutputStream out);

	/**
	 * 
	 * @param in
	 * @param out
	 */
	void convert(InputStream in, OutputStream out);

	/**
	 * 
	 * @param xml
	 * @param out
	 */
	void convert(Element xml, OutputStream out);

	/**
	 * 
	 * @param txt
	 * @param out
	 */
	void convert(List<String> txt, OutputStream out);

	/**
	 * 
	 * @param bytes
	 * @param out
	 */
	void convert(byte[] bytes, OutputStream out);

	/**
	 * 
	 * @param in
	 * @param out
	 */
	void convert(File in, File out);

	/**
	 * 
	 * @param in
	 * @param out
	 */
	void convert(InputStream in, File out);

	/**
	 * 
	 * @param cml
	 * @param out
	 */
	void convert(Element cml, File out);

	/**
	 * 
	 * @param txt
	 * @param out
	 */
	void convert(List<String> txt, File out);

	/**
	 * 
	 * @param bytes
	 * @param out
	 */
	void convert(byte[] bytes, File out);

	/**
	 * 
	 * @param in
	 * @return strings
	 */
	List<String> convertToText(File in);

	/**
	 * 
	 * @param in
	 * @return strings
	 */
	List<String> convertToText(InputStream in);

	/**
	 * 
	 * @param xml
	 * @return strings
	 */
	List<String> convertToText(Element xml);

	/**
	 * 
	 * @param text
	 * @return strings
	 */
	List<String> convertToText(List<String> text);

	/**
	 * 
	 * @param bytes
	 * @return strings
	 */
	List<String> convertToText(byte[] bytes);

	/**
	 * 
	 * @param in
	 * @return element
	 */
	Element convertToXML(File in);

	/**
	 * Converts an {@link InputStream} to an {@link Element}.
	 * 
	 * @param in
	 * @return element
	 */
	Element convertToXML(InputStream in);

	/**
	 * 
	 * @param xml
	 * @return element
	 */
	Element convertToXML(Element xml);

	/**
	 * 
	 * @param text
	 * @return element
	 */
	Element convertToXML(List<String> text);

	/**
	 * 
	 * @param bytes
	 * @return element
	 */
	Element convertToXML(byte[] bytes);

	/**
	 * 
	 * @param in
	 * @return bytes
	 */
	byte[] convertToBytes(File in);

	/**
	 * 
	 * @param in
	 * @return bytes
	 */
	byte[] convertToBytes(InputStream in);

	/**
	 * 
	 * @param xml
	 * @return bytes
	 */
	byte[] convertToBytes(Element xml);

	/**
	 * 
	 * @param text
	 * @return bytes
	 */
	byte[] convertToBytes(List<String> text);

	/**
	 * 
	 * @param bytes
	 * @return bytes
	 */
	byte[] convertToBytes(byte[] bytes);

   Command getCommand();
	/**
	 * 
	 * @return type
	 */
	Type getOutputType();

	/**
	 * 
	 * @return type
	 */
	Type getInputType();

	/**
	 * Return a list of any processing problems that did not result in an error.
	 * 
	 * @return warnings
	 */
	List<String> getWarnings();

	/**
	 * 
	 * @param f
	 * @return canHandle
	 */
	boolean canHandleInput(File f);

	/**
	 * 
	 * @param log
	 */
	void setConverterLog(ConverterLog log);

	/**
	 * 
	 * @return converterLog
	 */
	ConverterLog getConverterLog();
	
	/**
	 * 
	 * @param fileId
	 */
	void setFileId(String fileId);
	
	/**
	 * 
	 * @param metadataCML
	 */
	public void setMetadataCml(CMLElement metadataCML) ;


}
