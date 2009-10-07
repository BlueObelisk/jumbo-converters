package org.xmlcml.cml.converters;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

/** manages files in conversions.
 * 
 * @author pm286
 *
 */
public class FileManager implements CMLConstants {

	private static final Logger LOG = Logger.getLogger(FileManager.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	private String inputExtension;
	private String outputExtension;
//	private Converter converter;
//	private Class<?> converterClass;
	private File outputDirectory;
	private File inputDirectory;
	private String exceptionMessage;
	private String fileIdExtraction;
	private String fileId;
	private File inputFile;
	private File outputFile;
	

//	public Class<?> getConverterClass() {
//		return converterClass;
//	}
//
//	public void setConverterClass(Class<?> converterClass) {
//		this.converterClass = converterClass;
//	}

	/** create file manager.
	 * 
	 * @param converterClass (null => no-op)
	 */
	public FileManager() {
		
	}
	
//	/** create file manager.
//	 * 
//	 * @param converterClass (null => no-op)
//	 */
//	private FileManager(Class<?> converterClass) {
//		if (true)
//		{
//			throw new RuntimeException("REMOVE");
//		}
//		if (converterClass != null) {
//			try {
//				converter = (Converter) converterClass.newInstance();
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}
	
	public File getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(File inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getInputExtension() {
		return inputExtension;
	}

	public void setInputExtension(String inputExtension) {
		this.inputExtension = inputExtension;
	}
	
	public String getOutputExtension() {
//		if (outputExtension == null && guessOutputExtension) {
//			guessOutputExtension();
//		}
		return outputExtension;
	}

	public void setOutputExtension(String outputExtension) {
		this.outputExtension = outputExtension;
	}

//	public Converter getConverter() {
//		return converter;
//	}
//
//	public void setConverter(Converter converter) {
//		this.converter = converter;
//	}
//	
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	public String getFullOutputDirectoryName() {
		String fullOutputDirectoryName = null;
		if (inputDirectory != null &&
				outputDirectory != null) {
			fullOutputDirectoryName = FilenameUtils.concat(
				inputDirectory.getAbsolutePath().toString().trim(), outputDirectory.toString().trim());
		}
		return fullOutputDirectoryName;
	}

	public String createOutputFileName(File file) {
		String outputFile = null;
		String fullOutputDirectoryName = this.getFullOutputDirectoryName();
		LOG.debug("file, dir "+file+"==="+fullOutputDirectoryName);
		if (file != null) {
			if (fullOutputDirectoryName != null) {
				fullOutputDirectoryName = fullOutputDirectoryName.trim();
				if (outputExtension != null) {
					outputFile = substituteInputExtensionByOutputExtension(
							file, fullOutputDirectoryName);
				} else {
					outputFile = FilenameUtils.getName(file.getAbsolutePath().trim());
					outputFile = FilenameUtils.concat(
							fullOutputDirectoryName, 
							file.getName().trim());
				}
			}
		}
		LOG.debug("????"+outputFile+"???");
		return outputFile;
	}

	String substituteInputExtensionByOutputExtension(File file,
			String fullOutputDirectoryName) {
		String outputFile;
		new File(fullOutputDirectoryName).mkdir();
		// everything after last file.separator
		String name = file.getName();
		if (inputExtension != null) {
			if (!name.endsWith(inputExtension)) {
				throw new RuntimeException("input file should end with: "+inputExtension+", found: "+name);
			}
			if( name.length() > inputExtension.length()) { 
				name = name.substring(0, name.length()-inputExtension.length()-S_PERIOD.length());
			}
		} else {
			// remove any extension after final dot
			name = FilenameUtils.removeExtension(name).trim();					
		}
		outputFile = FilenameUtils.concat(
				fullOutputDirectoryName.trim(), 
				name+ S_PERIOD + outputExtension);
		return outputFile;
	}
	
	public void setException(Exception e) {
		exceptionMessage = (e == null) ? null : e.getMessage();
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
		File inputDirectory = new File(FilenameUtils.getFullPath(inputFile.toString()));
		this.setInputDirectory(inputDirectory);
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
	
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String getFileId() {
		if (fileId == null && fileIdExtraction != null) {
			if (inputFile != null) {
				String filename = inputFile.getAbsolutePath();
				if ("name".equals(fileIdExtraction)) {
					fileId = FilenameUtils.getBaseName(filename);
				} else if ("root".equals(fileIdExtraction)) {
					fileId = FilenameUtils.getPathNoEndSeparator(filename);
					fileId = File.separator + fileId;
					fileId = fileId.substring(fileId.lastIndexOf(File.separator)+1);
				}
			}
		}
		// fall through
		if (fileId == null) {
			fileId = inputFile.getName();
			int idx = fileId.indexOf(S_PERIOD);
			if (idx != -1) {
				fileId = fileId.substring(0, idx);
			}
		}
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileIdExtraction() {
		return fileIdExtraction;
	}

	public void setFileIdExtraction(String fileIdExtraction) {
		this.fileIdExtraction = fileIdExtraction;
	}

}
