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
	 * 
	 * @return
	 */
	void convert(File in, OutputStream out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(InputStream in, OutputStream out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(Element xml, OutputStream out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(List<String> txt, OutputStream out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(byte[] bytes, OutputStream out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(File in, File out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(InputStream in, File out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(Element cml, File out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(List<String> txt, File out);

	/**
	 * 
	 * 
	 * @return
	 */
	void convert(byte[] bytes, File out);

	/**
	 * 
	 * 
	 * @return
	 */
	List<String> convertToText(File in);

	/**
	 * 
	 * 
	 * @return
	 */
	List<String> convertToText(InputStream in);

	/**
	 * 
	 * 
	 * @return
	 */
	List<String> convertToText(Element xml);

	/**
	 * 
	 * 
	 * @return
	 */
	List<String> convertToText(List<String> text);

	/**
	 * 
	 * 
	 * @return
	 */
	List<String> convertToText(byte[] bytes);

	/**
	 * 
	 * 
	 * @return
	 */
	Element convertToXML(File in);

	/**
	 * Converts an {@link InputStream} to an {@link Element}.
	 * 
	 * @return
	 */
	Element convertToXML(InputStream in);

	/**
	 * 
	 * 
	 * @return
	 */
	Element convertToXML(Element xml);

	/**
	 * 
	 * 
	 * @return
	 */
	Element convertToXML(List<String> text);

	/**
	 * 
	 * 
	 * @return
	 */
	Element convertToXML(byte[] bytes);

	/**
	 * 
	 * 
	 * @return
	 */
	byte[] convertToBytes(File in);

	/**
	 * 
	 * 
	 * @return
	 */
	byte[] convertToBytes(InputStream in);

	/**
	 * 
	 * 
	 * @return
	 */
	byte[] convertToBytes(Element xml);

	/**
	 * 
	 * 
	 * @return
	 */
	byte[] convertToBytes(List<String> text);

	/**
	 * 
	 * 
	 * @return
	 */
	byte[] convertToBytes(byte[] bytes);

   Command getCommand();
	/**
	 * 
	 * 
	 * @return
	 */
	Type getOutputType();

	/**
	 * 
	 * 
	 * @return
	 */
	Type getInputType();

	/**
	 * Return a list of any processing problems that did not result in an error.
	 * 
	 * @return
	 */
	List<String> getWarnings();

	/**
	 * 
	 * 
	 * @return
	 */
	boolean canHandleInput(File f);

	/**
	 * 
	 * 
	 * @return
	 */
	void setConverterLog(ConverterLog log);

	/**
	 * 
	 * 
	 * @return
	 */
	ConverterLog getConverterLog();
	
	/**
	 * 
	 * 
	 * @return
	 */
	void setFileId(String fileId);
	
	/**
	 * 
	 * 
	 * @return
	 */
	public void setMetadataCml(CMLElement metadataCML) ;


}
