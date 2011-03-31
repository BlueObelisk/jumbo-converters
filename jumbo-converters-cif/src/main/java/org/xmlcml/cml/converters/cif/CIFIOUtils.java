package org.xmlcml.cml.converters.cif;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlcml.cml.base.CMLBuilder;

public class CIFIOUtils {

	public static void appendToFile(File file, String content) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
			fw.write(content);
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(fw);
		}
	}

	public static String stream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

//	public static String fetchWebPage(String url) {
//		HttpClient client = new HttpClient();
//		InputStream in = null;
//		GetMethod method = new GetMethod(url);
//		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//				new DefaultHttpMethodRetryHandler(10, false));
//		method.getParams().setSoTimeout(60000);
//		int statusCode;
//		String html = "";
//		try {
//			statusCode = client.executeMethod(method);
//			if (statusCode != HttpStatus.SC_OK) {
//				System.err.println("Method failed: " + method.getStatusLine());
//			}
//			in = method.getResponseBodyAsStream();
//			html = stream2String(in);
//		} catch (HttpException e) {
//			System.err.println(e.getMessage());
//		} catch (IOException e) {
//			System.err.println(e.getMessage());
//		} finally {
//			if (method != null) {
//				method.releaseConnection();
//			}
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//					System.err.println("Error closing InputStream: " + in);
//				}
//			}
//		}
//		return html;
//	}
//
//	public static void saveFileFromUrl(String url, String outPath) {
//		HttpClient client = new HttpClient();
//		InputStream in = null;
//		FileOutputStream fos = null;
//		GetMethod method = new GetMethod(url);
//		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//				new DefaultHttpMethodRetryHandler(10, false));
//		method.getParams().setSoTimeout(20000);
//		int statusCode;
//		try {
//			statusCode = client.executeMethod(method);
//			if (statusCode != HttpStatus.SC_OK) {
//				System.err.println("Method failed: " + method.getStatusLine());
//			}
//			in = method.getResponseBodyAsStream();
//			fos = new FileOutputStream(outPath);
//			byte[] buf = new byte[256];
//			int read = 0;
//			while ((read = in.read(buf)) > 0) {
//				fos.write(buf, 0, read);
//			}
//		} catch (HttpException e) {
//			System.err.println(e.getMessage());
//		} catch (IOException e) {
//			System.err.println(e.getMessage());
//		} finally {
//			if (method != null) {
//				method.releaseConnection();
//			}
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//					System.err.println("Error closing InputStream: " + in);
//				}
//			}
//			if (fos != null) {
//				try {
//					fos.close();
//				} catch (IOException e) {
//					System.err
//							.println("Error closing FileOutputStream: " + fos);
//				}
//			}
//		}
//	}

	public static void writeText(String content, String fileName) {
		if (content == null) {
			throw new IllegalStateException("Content to be written is null.");
		} else if (fileName == null) {
			throw new IllegalStateException("File name is null.");
		} else {
			File parentDir = new File(fileName).getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new FileWriter(fileName));
				out.write(content);
				out.close();
			} catch (IOException e) {
				throw new RuntimeException("Error writing text to "
						+ fileName, e);
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					throw new RuntimeException(
							"Cannot close writer: " + out, e);
				}
			}
		}
	}

	public static void writeXML(Document doc, String fileName) {
		File writeFile = new File(fileName).getParentFile();
		if (!writeFile.exists()) {
			writeFile.mkdirs();
		}
		try {
			Serializer serializer = null;
			serializer = new Serializer(new FileOutputStream(fileName));
			serializer.write(doc);
		} catch (IOException e) {
			throw new RuntimeException("Could not write XML file to "
					+ fileName);
		}
	}

	public static void writePrettyXML(Document doc, String fileName) {
		File writeFile = new File(fileName).getParentFile();
		if (!writeFile.exists()) {
			writeFile.mkdirs();
		}
		Serializer serializer;
		try {
			serializer = new Serializer(new FileOutputStream(fileName));
			serializer.setIndent(2);
			serializer.write(doc);
		} catch (IOException e) {
			throw new RuntimeException("Could not write XML file to "
					+ fileName);
		}
	}

//	public static Document parseWebPage(String url) {
//		Document doc = null;
//		XMLReader tagsoup;
//		String response = fetchWebPage(url);
//		try {
//			tagsoup = XMLReaderFactory
//					.createXMLReader("org.ccil.cowan.tagsoup.Parser");
//			Builder builder = new Builder(tagsoup);
//			doc = builder.build(new BufferedReader(new StringReader(response)));
//		} catch (IOException e) {
//			throw new RuntimeException("Could not read webpage at "
//					+ url, e);
//		} catch (ValidityException e) {
//			throw new RuntimeException("Webpage at " + url
//					+ " is not valid XML.", e);
//		} catch (ParsingException e) {
//			throw new RuntimeException("Could not parse webpage at "
//					+ url, e);
//		} catch (SAXException e) {
//			throw new RuntimeException(
//					"Could not create XMLReader from org.ccil.cowan.tagsoup.Parser",
//					e);
//		}
//		return doc;
//	}

//	public static Document parseWebPageMinusComments(String url) {
//		String response = fetchWebPage(url);
//
//		String patternStr = "<!--(.*)?-->";
//		String replacementStr = "";
//		Pattern pattern = Pattern.compile(patternStr);
//		Matcher matcher = pattern.matcher(response);
//		String html = matcher.replaceAll(replacementStr);
//
//		// done specifically because I found this horror on the
//		// Chemistry Letters site and it broke Tagsoup/XOM!
//		patternStr = "<!-->";
//		replacementStr = "";
//		pattern = Pattern.compile(patternStr);
//		matcher = pattern.matcher(html);
//		html = matcher.replaceAll(replacementStr);
//
//		Document doc = null;
//		XMLReader tagsoup;
//		try {
//			tagsoup = XMLReaderFactory
//					.createXMLReader("org.ccil.cowan.tagsoup.Parser");
//			Builder builder = new Builder(tagsoup);
//			doc = builder.build(new BufferedReader(new StringReader(html)));
//		} catch (IOException e) {
//			throw new RuntimeException("Could not read webpage at "
//					+ url, e);
//		} catch (ValidityException e) {
//			throw new RuntimeException("Webpage at " + url
//					+ " is not valid XML.", e);
//		} catch (ParsingException e) {
//			throw new RuntimeException("Could not parse webpage at "
//					+ url, e);
//		} catch (SAXException e) {
//			throw new RuntimeException(
//					"Could not create XMLReader from org.ccil.cowan.tagsoup.Parser",
//					e);
//		}
//		return doc;
//	}

	public static Document parseXmlFile(String filePath) {
		return parseXmlFile(new File(filePath));
	}

	public static Document parseXmlFile(File file) {
		try {
			return parseXmlFile(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file "
					+ file.getAbsolutePath(), e);
		}
	}

	public static Document parseXmlFile(Reader reader) {
		Document doc;
		try {
			doc = new Builder().build(new BufferedReader(reader));
		} catch (ValidityException e) {
			throw new RuntimeException("Invalid XML", e);
		} catch (ParsingException e) {
			throw new RuntimeException("Could not parse XML", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding", e);
		} catch (IOException e) {
			throw new RuntimeException("Input exception", e);
		}
		return doc;
	}

	public static Document parseCmlFile(String filePath) {
		return parseCmlFile(new File(filePath));
	}

	public static Document parseCmlFile(File file) {
		Document doc;
		try {
			doc = new CMLBuilder().build(new BufferedReader(
					new FileReader(file)));
		} catch (ValidityException e) {
			throw new RuntimeException("File at "
					+ file.getAbsolutePath() + " is not valid XML", e);
		} catch (ParsingException e) {
			throw new RuntimeException("Could not parse file at "
					+ file.getAbsolutePath(), e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"File at " + file.getAbsolutePath()
							+ " is in an unsupported encoding", e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File at "
					+ file.getAbsolutePath() + " could not be found", e);
		} catch (IOException e) {
			throw new RuntimeException("Could read file at "
					+ file.getAbsolutePath(), e);
		}
		return doc;
	}

	public static Document parseHtmlWithTagsoup(String html) {
		Document doc = null;
		XMLReader tagsoup;
		try {
			tagsoup = XMLReaderFactory
					.createXMLReader("org.ccil.cowan.tagsoup.Parser");
			Builder builder = new Builder(tagsoup);
			doc = builder.build(new BufferedReader(new StringReader(html)));
		} catch (IOException e) {
			throw new RuntimeException("Could not read html", e);
		} catch (ValidityException e) {
			throw new RuntimeException("HTML not valid XML.", e);
		} catch (ParsingException e) {
			throw new RuntimeException("Could not parse HTML", e);
		} catch (SAXException e) {
			throw new RuntimeException(
					"Could not create XMLReader from org.ccil.cowan.tagsoup.Parser",
					e);
		}
		return doc;
	}

	public static Properties loadProperties(String filepath) throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(filepath));
		return props;
	}
}
