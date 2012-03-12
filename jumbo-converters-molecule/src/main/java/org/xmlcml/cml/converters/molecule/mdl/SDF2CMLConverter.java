package org.xmlcml.cml.converters.molecule.mdl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.AbstractConverter;
import org.xmlcml.cml.converters.CMLCommon;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.Type;
import org.xmlcml.cml.converters.molecule.MoleculeCommon;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasDictRef;

public class SDF2CMLConverter extends AbstractConverter implements
		Converter {
	private static final String NULL_DATA = "**NULL DATA**";
	private static final Logger LOG = Logger.getLogger(SDF2CMLConverter.class);
	private static final String PREFIX = "mdl";
	private static final Pattern FIRST_PATTERN = Pattern.compile(">\\s*.*<([^>]+)>.*");
	private static final String DELIM = CMLConstants.S_PIPE;
	private int lineCounter;
	private List<String> lines;
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public Type getInputType() {
		return Type.SDF;
	}

	public Type getOutputType() {
		return Type.CML;
	}

	/**
	 * converts an SDF object to CML. returns cml:cml/cml:molecule
	 * 
	 * @param lines
	 */
	public Element convertToXML(List<String> lines) {
		CMLCml cml = new CMLCml();
		this.lines = lines;
		if (lines != null) {
			lineCounter = 0;
			while (lineCounter < lines.size()) {
				MDLConverter converter = new MDLConverter(lineCounter);
				CMLMolecule molecule = converter.readMOL(lines);
				if (molecule == null) {
					break;
				}
				lineCounter = converter.getNline();
				CMLList list = readSDFData();
				molecule.appendChild(list);
				cml.appendChild(molecule);
			}
		}
		return cml;
	}

	private CMLList readSDFData() {
		CMLList arrayList = new CMLList();
		while (!CML2SDFConverter.SDF_END.equals(lines.get(lineCounter))) {
			HasDataType data = readData();
			arrayList.appendChild((CMLElement)data);
		}
//		System.out.println(((CMLArray)arrayList.getChildCMLElements().get(0)).getXMLContent());
		lineCounter++;
		return arrayList;
	}

	private HasDataType readData() {
		HasDataType data = null;
		String line = lines.get(lineCounter++);
		Matcher matcher = FIRST_PATTERN.matcher(line);
		if (!matcher.matches() || matcher.groupCount() != 1) {
			throw new RuntimeException("Bad line; expected SDFData at ("+lineCounter+"): "+line);
		}
		String dictRef = PREFIX+":"+matcher.group(1);
		List<String> dataLines = new ArrayList<String>();
		while (true) {
			line = lines.get(lineCounter++).trim();
			if (line.length() == 0) {
				data = createData(dictRef, dataLines);
				break;
			}
			dataLines.add(line);
		}
		return data;
	}

	private HasDataType createData(String dictRef, List<String> dataLines) {
		HasDataType data;
		if (dataLines.size() == 0) {
			data = new CMLScalar(NULL_DATA);
		} else if (dataLines.size() == 1) { 
			data = new CMLScalar(dataLines.get(0));
		} else {
			data = new CMLArray(dataLines.toArray(new String[0]),DELIM);
		}
		((HasDictRef)data).setDictRef(dictRef);
		return data;
	}

	private static void usage() {
		System.out.println("usage: MDL2CMLConverter <file.mdl> <file.xml>");
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
		} else {
			SDF2CMLConverter converter = new SDF2CMLConverter();
			converter.convert(new File(args[0]), new File(args[1]));
		}
		
	}
	
	@Override
	public String getRegistryInputType() {
		return MoleculeCommon.SDF;
	}
	
	@Override
	public String getRegistryOutputType() {
		return CMLCommon.CML;
	}
	
	@Override
	public String getRegistryMessage() {
		return "converts MDL/SDFile to CML";
	}

}
