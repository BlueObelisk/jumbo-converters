package org.xmlcml.cml.converters;

import java.io.BufferedReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.converters.Type.ObjectType;

/**
 * Plumbing and IO wrangling for {@link Converter} classes - allows converter
 * implementations to implement a single conversion method between the native
 * object types of the data types on input and output.
 * 
 * @author jimdowning
 */
public abstract class AbstractConverter implements Converter {

   private static final String UTF_8 = "UTF-8";
/**
    * A {@link Logger} used to log messages during object conversion.
    */
   private static final Logger LOG = Logger.getLogger(AbstractConverter.class);

   static {
      LOG.setLevel(Level.INFO);
   }

    protected List<String> warnings = new ArrayList<String>();
    protected CMLElement metadataCml;
    protected ConverterLog converterLog = new ConverterLog();
    protected String fileId = null;
    private Builder builder = new Builder();
	protected LegacyProcessor legacyProcessor;

   /** auxiliary file as XML */
   protected Element auxElement;
   protected Command command;
   // change if you need faithful whitespace
   protected int indent = 2;
	private ArgProcessor argProcessor;
	private File inputDir;
	private File inputFile;
	private ByteArrayInputStream bais;
	private StringBuilder stdinBuilder;
	private File outputDir;

   public int getIndent() {
	   return indent;
   }

   /**
    * by default set to 2 for prettiness.
    * BUT this "mucks whitespace"
    * so set to ZERO if you want faithful whitespace
    * @param indent
    */
   public void setIndent(int indent) {
	   this.indent = indent;
   }

   public Command getCommand() {
      return command;
   }

   public void setCommand(Command c) {
      command = c;
   }

	/**
	 * Default constructor used to create a new {@link AbstractConverter}.
	 */
	protected AbstractConverter() {
		init();
	}

	/**
	 * Initialize the {@link AbstractConverter}.
	 * 
	 * @return
	 */
	protected void init() {
		// cmlSelector = getDefaultSelector();
		converterLog = new ConverterLog();
	}

	/**
	 * 
	 * @param f
	 * @return canHandle
	 */
   public boolean canHandleInput(File f) {
      return getInputType().getExtensions().contains(
              FilenameUtils.getExtension(f.getName()));
   }

   /**
    *
    * @param in
    */
   private void checkInputFileReadable(File in) {
      if (in == null) {
         throw new IllegalArgumentException("null input");
      }
      if (!(in.exists() && in.isFile() && in.canRead())) {
         throw new IllegalArgumentException(
                 "Problem with input file: " + in + ". Please check that the file exists and is a readable file");
      }
      fileId = FilenameUtils.getBaseName(in.getAbsolutePath());
   }

	/**
	 * 
	 * 
	 * @param t
	 *            the type of object
	 */
   private void checkInputIs(ObjectType t) {
      if (getInputType().getObjectType() != t) {
         throw new UnsupportedOperationException(
                 "Implementation does not claim to support " + t + " input:" + " call a convertToXXX(" + getInputType().
                 getObjectType() + ") method");
      }
   }

   /**
    *
    * @param in
    */
   private void checkInputStream(InputStream in) {
      if (in == null) {
         throw new IllegalArgumentException("Null input stream provided");
      }
   }

	/**
	 * 
	 * @param out
	 *            TODO clean this up - dodgy exception handling opportunity for
	 *            commons-io
	 */
   private void checkOutputFile(File out) {
	  ObjectType outputType = getOutputType().getObjectType();
      if (out == null) {
         throw new IllegalArgumentException("output file null");
      }
      File outFile = out.getAbsoluteFile();
      File dir = new File(FilenameUtils.getPath(outFile.getAbsolutePath()));
      try {
         LOG.trace("dir: " + dir.getCanonicalPath());
      } catch (IOException ee) {
         LOG.error("BUG? " + ee);
      }
      if (ObjectType.DIRECTORY.equals(outputType)) {
    	  verifyOrCreateOutputDirectory(outFile);
          return;
      }
      dir.mkdir();
      // cause problems on Unix (not allowed to touch /dev/null) 
      IOException e0 = null;
      try {
         FileUtils.touch(outFile);
      } catch (IOException e) {
         try {
            LOG.trace("path: " + outFile.getCanonicalPath());
         } catch (IOException ee) {
            LOG.error("BUG??? " + ee);
         }
         e0 = e;
      }
      if (!(outFile.canWrite())) {
         throw new IllegalArgumentException(
                 "Output file isn't writable "+e0.getMessage());
         // cause problems on Unix (not allowed to touch /dev/null) 
      }
   }

	private void verifyOrCreateOutputDirectory(File outFile) {
		if (!outFile.exists()) {
              LOG.info("creating output file as directory: " + outFile);
              try {
            	  outFile.mkdirs();
              } catch (Exception e) {
            	  throw new RuntimeException("Cannot create output directory: "+outFile);
              }
    	  }
          if ((outFile.isDirectory())) {
              LOG.info(" output file is a directory as expected: " + outFile);
           } else {
        	   throw new RuntimeException(""+this.getClass()+" requires output to be a directory");
           }
	}

   /**
    *
    * @param t
    */
   private void checkOutputIs(ObjectType t) {
      if (getOutputType().getObjectType() != t) {
         throw new UnsupportedOperationException(
                 "Implementation does not claim to support " + t + " output:" + " call a convertTo" + getOutputType().
                 getObjectType() + "(XXX) method");
      }
   }

   /**
    *
    * @param out
    */
   private void checkOutputStream(OutputStream out) {
      if (out == null) {
         throw new IllegalArgumentException("Null output stream provided");
      }
   }

   /**
    *
    */
   private void checkState() {
      if (getInputType() == null) {
         throw new RuntimeException(
                 "Implementation class has not defined a valid input type");
      }
      if (getOutputType() == null) {
         throw new RuntimeException(
                 "Implementation class has not defined a valid output type");
      }
   }

   /**
    * @param bytes
    * @param out
    */
   public void convert(byte[] bytes, File out) {
      checkOutputFile(out);
      FileOutputStream fout = null;
      try {
         fout = new FileOutputStream(out);
         convert(bytes, fout);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fout);
      }
   }

   /**
    * @param bytes
    * @param out
    */
   public void convert(byte[] bytes, OutputStream out) {
      checkOutputStream(out);
      switch (getOutputType().getObjectType()) {
         case TEXT:
            serialize(convertToText(bytes), out);
            break;
         case XML:
            serialize(convertToXML(bytes), out, this);
            break;
         case BYTES:
            serialize(convertToBytes(bytes), out);
      }
   }

   /**
    * @param xml
    * @param out
    */
   public void convert(Element xml, File out) {
      checkOutputFile(out);
      FileOutputStream fout = null;
      try {
         fout = new FileOutputStream(out);
         convert(xml, fout);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fout);
      }
   }

   /**
    * @param xml
    * @param out
    */
   public void convert(Element xml, OutputStream out) {
      checkOutputStream(out);
      switch (getOutputType().getObjectType()) {
         case TEXT:
            serialize(convertToText(xml), out);
            break;
         case XML:
            serialize(convertToXML(xml), out, this);
            break;
         case BYTES:
            serialize(convertToBytes(xml), out);
            break;
      }
   }

   /**
    * @param in
    * @param out
    */
   public void convert(File in, File out) {
      checkState();
      checkInputFileReadable(in);
      checkOutputFile(out);
      String inputExtension = FilenameUtils.getExtension(in.getName());
      if (!getInputType().getExtensions().contains(inputExtension)) {
         String warning = "Given input extension " + inputExtension + " does not match any of given extensions for " + getInputType();
         warnings.add(warning);
         LOG.trace(warning);
      }

      FileInputStream fin = null;
      FileOutputStream fout = null;
      try {
         fin = new FileInputStream(in);
         fout = new FileOutputStream(out);
         convert(fin, fout);
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fin);
         IOUtils.closeQuietly(fout);
      }
   }

   public void convert(File in, OutputStream out) {
      checkInputFileReadable(in);
      FileInputStream fin = null;
      try {
         fin = new FileInputStream(in);
         convert(fin, out);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fin);
      }
   }

   public void convert(InputStream in, File out) {
      if (out.isDirectory()) {
    	  convert(inputFile, out);
    	  LOG.debug("Not using convert for directory");
    	  return;
      }
      checkOutputFile(out);
      FileOutputStream fout = null;
      try {
         if (!out.isDirectory()) {
        	 fout = new FileOutputStream(out);
         }
         convert(in, fout);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fout);
      }
   }

   /**
    * Marshalls from stream to the implementation's input type, calls the
    * conversion, and serializes the output from the implementation's output
    * type.
    */
   public void convert(InputStream in, OutputStream out) {
      checkState();
      ObjectType objIn = getInputType().getObjectType();
      ObjectType objOut = getOutputType().getObjectType();
      switch (objIn) {
         case TEXT:
            switch (objOut) {
               case TEXT:
                  serialize(convertToText(marshallToText(in)), out);
                  break;
               case XML:
                  serialize(convertToXML(marshallToText(in)), out, this);
                  break;
               case BYTES:
                  serialize(convertToBytes(marshallToText(in)), out);
                  break;
            }
            break;
         case XML:
            switch (objOut) {
               case TEXT:
                  serialize(convertToText(marshallToXML(getBuilder(), in)), out);
                  break;
               case XML:
                  serialize(convertToXML(marshallToXML(getBuilder(), in)), out, this);
                  break;
               case BYTES:
                  serialize(convertToBytes(marshallToXML(getBuilder(), in)), out);
                  break;
            }
            break;
         case BYTES:
            switch (objOut) {
               case TEXT:
                  serialize(convertToText(marshallToBytes(in)), out);
                  break;
               case XML:
                  serialize(convertToXML(marshallToBytes(in)), out, this);
                  break;
               case BYTES:
                  serialize(convertToBytes(marshallToBytes(in)), out);
                  break;
            }
      }
   }

   public void convert(List<String> txt, File out) {
      checkOutputFile(out);
      FileOutputStream fout = null;
      try {
         fout = new FileOutputStream(out);
         convert(txt, fout);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fout);
      }
   }

   public void convert(List<String> txt, OutputStream out) {
      switch (getOutputType().getObjectType()) {
         case TEXT:
            serialize(convertToText(txt), out);
            break;
         case XML:
            serialize(convertToXML(txt), out, this);
            break;
         case BYTES:
            serialize(convertToBytes(txt), out);
            break;
      }
   }

   /**
    * This method should be overridden by implementations that convert from
    * binary gunk to text formats.
    */
   public List<String> convertToText(byte[] bytes) {
      checkInputIs(ObjectType.BYTES);
      checkOutputIs(ObjectType.TEXT);
      throw new UnsupportedOperationException(
              "convertToText(byte[] should have been overriden by the implementing class");
   }

   public List<String> convertToText(Element xml) {
      checkInputIs(ObjectType.XML);
      checkOutputIs(ObjectType.TEXT);
      throw new UnsupportedOperationException(
              "convertToText(Element should have been overriden by the implementing class");
   }

   public List<String> convertToText(List<String> text) {
      checkInputIs(ObjectType.TEXT);
      checkOutputIs(ObjectType.TEXT);
      throw new UnsupportedOperationException(
              "convertToText(List<String> should have been overriden by the implementing class");
   }

   public Element convertToXML(byte[] bytes) {
      checkInputIs(ObjectType.BYTES);
      checkOutputIs(ObjectType.XML);
      throw new UnsupportedOperationException(
              "convertToXML(byte[] should have been overriden by the implementing class");
   }

   public Element convertToXML(Element xml) {
      checkInputIs(ObjectType.XML);
      checkOutputIs(ObjectType.XML);
      throw new UnsupportedOperationException(
              "convertToXML(Element should have been overriden by the implementing class");
   }

   public Element convertToXML(File in) {
      checkInputFileReadable(in);
      FileInputStream fin = null;
      try {
         fin = new FileInputStream(in);
         return convertToXML(fin);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fin);
      }
   }

   public Element convertToXML(InputStream in) {
      checkInputStream(in);
      switch (getInputType().getObjectType()) {
         case TEXT:
            return convertToXML(marshallToText(in));
         case XML:
            return convertToXML(marshallToXML(getBuilder(), in));
         default:
            return convertToXML(marshallToBytes(in));
      }
   }

   public Element convertToXML(List<String> text) {
      checkInputIs(ObjectType.TEXT);
      checkOutputIs(ObjectType.XML);
      throw new UnsupportedOperationException(
              "convertToXML(List<String> should have been overriden by the implementing class");
   }

   public List<String> getWarnings() {
      return Collections.unmodifiableList(warnings);
   }

   public static byte[] marshallToBytes(InputStream in) {
      try {
         return IOUtils.toByteArray(in);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static List<String> marshallToText(InputStream in) {
      try {
         List<String> lines = (List<String>) IOUtils.readLines(in);
//			preprocessLines(lines);
         return lines;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /** override with anything that needs to edit the lines
    * default is no-op
    * @param lines
    */
   protected static List<String> preprocessLines(List<String> lines) {
      // no-op
      return lines;
   }

   public static Element marshallToXML(Builder builder, InputStream in) {
      String inS = null;
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(in,
                                                                      UTF_8));
         StringBuilder sb = new StringBuilder();
         while (true) {
            String line = br.readLine();
            if (line == null) {
               break;
            }
            sb.append(line);
            sb.append(CMLConstants.S_NEWLINE);
         }
         br.close();
         inS = sb.toString();
      } catch (Exception e) {
         throw new RuntimeException("could not read XML file ", e);
      }

      DTDProblem dtdProblem = new DTDProblem();
      Element element = parseString(builder, inS, dtdProblem);
      if (element == null && dtdProblem.failed) {
         Document doc = null;
         try {
            doc = CMLUtil.stripDTDAndOtherProblematicXMLHeadings(inS);
         } catch (Exception e) {
            throw new RuntimeException("Cannot parse XML ", e);
         }
         element = (doc == null) ? null : doc.getRootElement();
      }
      return element;
   }

   private static Element parseString(Builder builder, String inS,
           DTDProblem dtdProblem) {
      Document doc = null;
      try {
         doc = builder.build(new StringReader(inS));
      } catch (ValidityException e) {
         throw new RuntimeException(e);
      } catch (ParsingException e) {
         // there is a tricky bug in XOM Xerces for certain files that contain DOCTYPE
         // and references within the DTD itself
         if (e.getMessage().indexOf("Missing scheme in absolute URI reference") != -1) {
            throw new RuntimeException(
                    "XOM/Xerces bug - remove the DOCTYPE and try rerunning", e);
         }
         throw new RuntimeException(e);
      } catch (FileNotFoundException fnf) {
         // DTDs can be a menace.
         // java.io.FileNotFoundException: C:\Users\pm286\workspace\jumbo-converters\ep-patent-document-v1-4.dtd (The system cannot find the file specified)
         String msg = fnf.getMessage();
         if (msg.indexOf(".dtd ") != -1) {
            dtdProblem.failed = true;
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return (doc == null) ? null : doc.getRootElement();
   }

   /**
    *
    * @param data
    * @param out
    */
   public static void serialize(byte[] data, OutputStream out) {
      try {
         IOUtils.write(data, out);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Serialize a <a href="http://www.cafeconleche.org/XOM/">XOM</a> XML
    * {@link Element} to an {OutputSteam} using the "UTF-8" encoding
    * with an indenting of 2 space characters per indent.
    * (??It's not entirely clear why this method is static.??)
    *
    * @param xml the {@link Element} to serialize
    * @param out the {@link OutputStream} to send the serialized out put to
    */
   public static void serialize(Element xml, OutputStream out, AbstractConverter converter) {
      if (xml == null) {
         LOG.error("Null XML for output - should have been trapped");
      } else {
         try {
            Serializer serializer = new Serializer(out, UTF_8);
//            serializer.setMaxLength(0);
            // this mucks up whitespace unless set to ZERO
            serializer.setIndent(converter.getIndent());
            Document doc = new Document((Element) xml.copy());
            serializer.write(doc);
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(
                    "Failed while attempting to serialize XML ", e);
         } catch (IOException e) {
            throw new RuntimeException(
                    "Failed while attempting to serialize XML ", e);
         }
      }

   }

   /**
    *
    * @param lines
    * @param out
    */
   public static void serialize(List<String> lines, OutputStream out) {
      try {
         IOUtils.writeLines(
                 lines, System.getProperty("line.separator"), out);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * A convenience method to convert a <a href="http://www.cafeconleche.org/XOM/">XOM</a>
    * {@link Element} to "UTF-8" encoded XML text in the form of a {@link List} of {@link String}s.
    *
    * @param element the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Element} to
    * be serialized to XHTML
    * @param indent the amount of indentation per nested indent measured in spaces
    * @return a {@link List} of {@link String}s
    */
   public static List<String> marshallToText(Element element, int indent) {
      List<String> stringList = new ArrayList<String>();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (element == null) {
         throw new RuntimeException(
                 "Null element in marshallToText; your conversion may have failed");
      }
      try {
         Serializer serializer = new Serializer(baos, UTF_8);
         serializer.setIndent(indent);
         Document doc = new Document((Element) element.copy());
         serializer.write(doc);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      stringList = marshallToText(bais);
      return stringList;
   }

   /**
    * A convenience method to convert a <a href="http://www.cafeconleche.org/XOM/">XOM</a>
    * {@link Document} to XHTML text with a given encoding in the form of a
    * {@link List} of {@link String}s.
    *
    * @param doc the <a href="http://www.cafeconleche.org/XOM/">XOM</a> {@link Document} to
    * be serialized to XHTML
    * @param indent the amount of indentation per nested indent measured in spaces
    * @param encoding the encoding to use, e.g. "UTF-8" or "ISO-8859-1"
    * @return a {@link List} of {@link String}s
    */
   public static List<String> marshallToText(Document doc, int indent,
           String encoding) {
      List<String> stringList = new ArrayList<String>();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (doc == null) {
         throw new RuntimeException(
                 "Null element in marshallToText; your conversion may have failed");
      }
      try {
         Serializer serializer = new Serializer(baos, encoding);
         serializer.setIndent(indent);
         serializer.write(doc);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      stringList = marshallToText(bais);
      return stringList;
   }

   public String str(double number) {
      return new String(new Double(number).toString());
   }

   public String str(int number) {
      return new String(new Integer(number).toString());
   }

   /**
    *
    */
   public byte[] convertToBytes(File in) {
      checkInputFileReadable(in);
      FileInputStream fin = null;
      try {
         fin = new FileInputStream(in);
         return convertToBytes(fin);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fin);
      }
   }

   /**
    *
    */
   public byte[] convertToBytes(InputStream in) {
      checkInputStream(in);
      switch (getInputType().getObjectType()) {
         case TEXT:
            return convertToBytes(marshallToText(in));
         case XML:
            return convertToBytes(marshallToXML(getBuilder(), in));
         default:
            return convertToBytes(marshallToBytes(in));
      }
   }

   public byte[] convertToBytes(Element xml) {
      checkInputIs(ObjectType.XML);
      checkOutputIs(ObjectType.BYTES);
      throw new UnsupportedOperationException(
              "convertToBytes(Element should have been overriden by the implementing class");
   }

   public byte[] convertToBytes(List<String> text) {
      checkInputIs(ObjectType.TEXT);
      checkOutputIs(ObjectType.BYTES);
      throw new UnsupportedOperationException(
              "convertToBytes(List<String> should have been overriden by the implementing class");
   }

   public byte[] convertToBytes(byte[] bytes) {
      checkInputIs(ObjectType.BYTES);
      checkOutputIs(ObjectType.BYTES);
      throw new UnsupportedOperationException(
              "convertToBytes(byte[] should have been overriden by the implementing class");
   }

   public List<String> convertToText(File in) {
      checkInputFileReadable(in);
      FileInputStream fin = null;
      try {
         fin = new FileInputStream(in);
         return convertToText(fin);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(fin);
      }
   }

   public List<String> convertToText(InputStream in) {
      checkInputStream(in);
      switch (getInputType().getObjectType()) {
         case TEXT:
            return convertToText(marshallToText(in));
         case XML:
            return convertToText(marshallToXML(getBuilder(), in));
         default:
            return convertToText(marshallToBytes(in));
      }
   }

     /**
    * A helper method to ensure that errors and exceptions are
    * both logged and sent to stdout. This particular method allows
    * the message from {@link Exception} {@code e} to be appended.
    *
    * @param message a helpful error message to give to users
    * @param e the {@link Exception} that caused the problem
    */
   protected void runtimeException(String message, Exception e) {
      this.getConverterLog().addToLog(Level.ERROR, message + " " + e.
              getLocalizedMessage());
      throw new RuntimeException(message, e);
   }

   /**
    * A helper method to ensure that errors and exceptions are
    * both logged and sent to stdout.
    *
    * @param message a helpful error message to give to users
    */
   protected void runtimeException(String message) {
      this.getConverterLog().addToLog(Level.ERROR, message);
      throw new RuntimeException(message);
   }

   /**
    * A helper method to log a warning message to this {@link AbstractConverter}'s
    * {@link ConverterLog}.
    *
    * @param message a helpful warning message to give to users
    */
   protected void warn(String message) {
      this.getConverterLog().addToLog(Level.WARN, message);
   }

   /**
    * Gets the {@link ConverterLog} used by this {@link AbstractConverter} to
    * log messages to.
    *
    * @return the {@link ConverterLog} for
    */
   public ConverterLog getConverterLog() {
      return converterLog;
   }

   /**
    * Sets the {@link ConverterLog} used by this {@link AbstractConverter} to
    * log messages to.
    *
    * @param converterLog the {@link ConverterLog} to set
    */
   public void setConverterLog(ConverterLog converterLog) {
      this.converterLog = converterLog;
   }

   /**
    *
    * @return fileId
    */
   public String getFileId() {
      return fileId;
   }

   /**
    * Sets the fileId selector. Possible selectors are
    */
   public void setFileId(String fileId) {
      this.fileId = fileId;
   }

   /** ensure there is an id if possible.
    * if id is present on element use it, else use fileId
    * @param topElement
    * @return id
    * @throws RuntimeException cannot find ID
    */
   protected String ensureId(CMLElement topElement) {
      String id = (topElement == null) ? null : topElement.getId();
      if (id == null && fileId != null) {
         id = fileId;
         topElement.setId(id);
      }
      if (id == null) {
         throw new RuntimeException("Cannot find id");
      }
      return id;
   }

   /**
    * Needs to be overridden if you need your converter to use a different kind
    * of builder (especially e.g. CMLBuilder).
    *
    * @return builder
    */
   protected Builder getBuilder() {
      return builder;
   }

   /** reads auxiliary file into XML Element
    *
    * @return null if missing or not XML
    */
   public Element getAuxElement() {
      auxElement = null;
      String auxFileName = getCommand().getAuxfileName();
      if (auxFileName != null) {
         File auxFile = new File(auxFileName);
         if (!auxFile.exists()) {
            throw new RuntimeException("Cannot find auxiliary file: " + auxFile.
                    getAbsolutePath());
         }
         try {
            auxElement = new CMLBuilder().build(new FileInputStream(auxFile)).
                    getRootElement();
         } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot read auxiliary file: " + auxFileName, e);
         }
      }
      return auxElement;
   }

   /**
    * process directory specifically
    * this stub throws RuntimeException
    */
   public void processDirectory(File directory) {
      throw new RuntimeException("Must override processDirectory for: " + this.
              getClass());
   }

   public void setMetadataCml(CMLElement metadataCml) {
      this.metadataCml = metadataCml;
   }

   // override
   protected String getConverterName() {
      return this.getClass().getName();
   }

   public boolean isCMLLiteOutput() {
      getAuxElement();
      boolean outputCMLLite = (auxElement != null) ? "cml:lite".equals(auxElement.
              getAttributeValue("output")) : false;
      return outputCMLLite;
   }
   
   public String getRegistryInputType() {
	   return null;
   }
   public String getRegistryOutputType() {
	   return null;
   }
   public String getRegistryMessage() {
	   return "override RegistryFoo in: "+this.getClass().getName();
   }

	protected void usage2() {
		System.err.println("AbstractConverter, often invoked with specific command (e.g.'cif2cml'");
		System.err.println("Command line of form: '[command] [options], command being 'cif2cml', etc.");
		System.err.println("Universal options ('-f' is short for '--foo', etc.):");
		System.err.println("    -i  --input  inputSpec");
		System.err.println("                 mandatory: filename, directoryName, url, or (coming) identifier (e.g. PMID:12345678)");
		System.err.println("    -o  --output  outputSpec");
		System.err.println("                 optional: filename, directoryName; if absent genertes default");
		System.err.println("    -r  --recursive");
		System.err.println("                 recurse through directories");
		System.err.println("");
	}


   /** process simple commandline arguments.
    * 
    * @param args passed from main(args)
    */
	protected void processArgs(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			usage2();
		} else {
			runArgs(args);
		}
	}
	
    /**
     * Processes command line arguments.
     * 
     * @param commandLineArgs
     * @throws IOException 
     * @throws FileNotFoundException 
     */
	public void runArgs(String[] commandLineArgs) {
		if (commandLineArgs == null || commandLineArgs.length == 0) {
			usage2();
			return;
		}
		
		argProcessor = new ArgProcessor(commandLineArgs);
		// no -i given
		if (argProcessor.getInput() == null) {
			if (argProcessor.getUnlabelledArgs().size() == 0) {
				LOG.warn("no files given as either -i or unlabelled args, using STDIN if it exists");
//				bais = new ByteArrayInputStream(argProcessor.getInputByteArray().toByteArray());
				try {
					List<String> lines= IOUtils.readLines(System.in);
					
					IOUtils.writeLines(lines, "\n", new FileOutputStream("target/cifLines.cif"));
				} catch (IOException e) {
					e.printStackTrace();
					LOG.error("cannot debug to target");
				}
			} else {
				List<String> inputFilenames = argProcessor.getUnlabelledArgs();
				for (String filename : inputFilenames) {
					processInputFile(new File(filename));
				}
			}
		}
		
		if (argProcessor.getOutput() == null && argProcessor.getInput() != null) {
			LOG.info("creating output filenames from input");
			this.createOutputFile();
		} else {
			
		}
		
		if (argProcessor.getInput() != null) {
			if (argProcessor.getOutput() == null) {
				throw new RuntimeException("cannot create output file: "+argProcessor.getOutput());
			}
			File inputFile = new File(argProcessor.getInput());
			processInputFile(inputFile);
		} else if (bais != null) {
			processInputStream(bais, new File(argProcessor.getOutput()));
		}
	}

	private void processInputFile(File inputFile) {
		this.inputFile = inputFile;
		if (!inputFile.exists()) {
			throw new RuntimeException("Input file does not exist: "+inputFile);
		}
		
		if (inputFile.isDirectory()) {
			inputDir = inputFile;
			boolean recursive = false;
			String[] extensions = this.getExtensions();
			LOG.info("listing files "+extensions+" in directory: "+inputDir);
			List<File> files = new ArrayList<File>(FileUtils.listFiles(inputDir, extensions, recursive));
			LOG.info("processing "+files.size()+" files");
			for (File file : files) {
				try {
				process(file);
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Cannot process "+file+" ("+e+"), skipping" );
				}
			}
		} else {
			inputDir = null;
			process(inputFile);
		}
	}

	private void process(File inputFile) {
		File outputFile = createOutputFileWithMkDirs(inputFile);
		LOG.info("converting "+inputFile+" to "+outputFile);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found: "+inputFile, e);
		}
		processInputStream(fis, outputFile);
	}

	private void processInputStream(InputStream is, File outputFile) {
		convert(is, outputFile);
	}

	/** get file extensions - should be provided by each jumbo-converter subclass */
	
	protected String[] getExtensions() {
		throw new RuntimeException("Must provide file extensions for "+this.getClass());
	}

	private File createOutputFileWithMkDirs(File inputFile) {
		Type outputType = getOutputType();
		ObjectType outputObjectType = outputType.getObjectType();
		// get primary extensions from class
		String outputExtension = (Type.DIRECTORY.equals(outputObjectType)) ? null : outputType.getExtensions().get(0);
		File outputFile = new File(argProcessor.getOutput());
		outputDir = null;
		if (outputFile.isDirectory()) {
			LOG.debug("output directory:"+outputFile);
			outputDir = outputFile;
		}
		File parentFile = outputFile.getParentFile();
		if (parentFile != null) parentFile.mkdirs();
		// when 
		if (outputDir != null) {
			if (!ObjectType.DIRECTORY.equals(outputObjectType)) {
				String name = inputFile.getName();
				String basename = FilenameUtils.getBaseName(name);
				basename += (outputExtension.equals(File.separator) ? outputExtension : "."+outputExtension);
				outputFile = new File(outputDir, basename);
			} else {
				LOG.debug(this.getClass()+" will write its own output files to: "+outputDir);
			}
		}
		return outputFile;
	}

	private void createOutputFile() {
		argProcessor.setOutput(argProcessor.getInput()+".cml");
	}


}

class DTDProblem {
   boolean failed = false;
}


