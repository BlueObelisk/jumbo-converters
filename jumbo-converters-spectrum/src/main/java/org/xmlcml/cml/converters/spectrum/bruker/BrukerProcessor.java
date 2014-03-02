package org.xmlcml.cml.converters.spectrum.bruker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Util;

public class BrukerProcessor {

	private static final String DOLLAR2S =       "dollar2";
	private static final String EXP =            "EXP=";
	private static final String RELAX =          "##$RELAX=";
	private static final String JCAMPDX_PREFIX = "jcampdx:";
	private static final String HASH2DOLLAR =    "##$";
	private static final String DOLLAR2 =        "$$";
	private final static String BRUKER_PREFIX =  "bruker:";
	private static final String AUDIT_TRAIL =    "##AUDIT TRAIL=";
	private static final String AUDIT_TRAIL2 =   "$$ ##AUDIT TRAIL=";
	private static final String BRUKER_FILE =    "##$BRUKER FILE";
	private static final String END =            "$$ ##END=";

	private static Pattern AUDIT_TRAIL2_PATTERN = Pattern.compile("\\$\\$\\s\\#\\#AUDIT\\sTRAIL\\=\\s*(\\S.*)\\s*");
	private static Pattern DOLLAR2_PATTERN = Pattern.compile("\\$\\$\\s*(\\S.*)\\s*");
	private static Pattern DOUBLE_DOLLAR2_PATTERN = Pattern.compile("\\$\\$\\s\\$\\$\\s*(\\S.*)\\s*");
	private static Pattern SCALAR_PATTERN = Pattern.compile("\\$\\$\\s\\#\\#(\\$?[A-Za-z_0-9\\s]+)\\=\\s*(\\S.*)\\s*");
	private static Pattern ARRAY_PATTERN = Pattern.compile("\\$\\$\\s\\#\\#(\\$?[A-Za-z_0-9\\s]+)\\=\\s*\\((\\d+)\\.\\.(\\d+)\\)\\s*");
	private static Pattern BRUKER_FILE_PATTERN = Pattern.compile("\\#\\#\\$BRUKER\\sFILE\\s(EXP|PROC)=\\s*(\\S.*)\\s*");
	
	private CMLList brukerChunk;
	private int iNode;
	CMLCml topCml;
	private CMLList newChunk;
	private Pattern bRUKER_FILE_PATTERN;


	public BrukerProcessor(CMLCml topCml) {
		this.topCml  = topCml;
	}
	/**
##AUDIT TRAIL= $$ (NUMBER, WHEN, WHO, WHERE, PROCESS, VERSION, WHAT)
$$ ##TITLE= Audit trail, TOPSPIN		Version 3.0
$$ ##JCAMPDX= 5.01
$$ ##ORIGIN= Bruker BioSpin GmbH
$$ ##OWNER= wash198
$$ $$ /home/wash198/wash198/110504_M1A_AO/2/pdata/1/auditp.txt
$$ ##AUDIT TRAIL=  $$ (NUMBER, WHEN, WHO, WHERE, PROCESS, VERSION, WHAT)
(   1,<2011-05-05 08:14:45.828 -0700>,<wash198>,<bokan>,<go>,<TOPSPIN 3.0>,
      <created by zg
	started at 2011-05-04 12:19:10.371 -0700,
	POWCHK disabled, PULCHK disabled,
       configuration hash MD5:
       FB F2 62 A5 6E DD 45 EB 3D 8C 47 D0 92 9A 19 2C
       data hash MD5: 8K
       E0 42 09 AC E3 8D 4C 9C 89 F6 98 4A FA CF 56 8F>)
(   2,<2011-05-05 09:45:45.958 -0700>,<wash198>,<bokan>,<proc1d>,<TOPSPIN 3.0>,
      <Start of raw data processing
       efp LB = 400 FT_mod = 6 PKNL = 1 PHC0 = -274.3274 PHC1 = -309.6 SI = 18596 
       data hash MD5: 18596
       BC 58 46 6C 5A 8A ED 41 0F EE 60 8A 54 87 CF E9>)
(   3,<2011-05-05 09:45:56.011 -0700>,<wash198>,<bokan>,<proc1d>,<TOPSPIN 3.0>,
      <pk fgphup PHC0 = -74.6681 PHC1 = 114.4 
       data hash MD5: 18596
       4C D2 A6 95 7F BC 60 E4 8B 87 AB BF E2 9F EB 0C>)
$$ ##END=
$$ 
$$ $$ hash MD5
$$ $$ E9 6F 06 E8 F6 36 D2 A2 86 F4 E2 1F FB A3 DE 77
##$RELAX= 
	 * 
##$BRUKER FILE EXP=cpdprg2
$$   0.3u fq=cnst21
$$   0.3u pl=pl12
$$ 1 100up:0 
$$   jump to 1
##$RELAX= 
##$BRUKER FILE EXP=format.temp
$$ EDIT_PAR COMMAND FILE
	 * @param lines
	 * @return
	 */
	List<String> extractAndProcessBrukerFiles(List<String> lines) {
		List<String> jdxLines = new ArrayList<String>();
		brukerChunk = null;
		for (String line : lines) {
			if (line.startsWith(BRUKER_FILE)) {
				createBrukerFile(line);
			} else if (line.startsWith(AUDIT_TRAIL)) {
				createAuditTrail(line);
			} else if (line.startsWith(RELAX)) {
				processChunk();
				brukerChunk = null;
			} else if (brukerChunk != null) {
				CMLScalar scalar = new CMLScalar(line);
				brukerChunk.appendChild(scalar);
			} else {
				jdxLines.add(line);
			}
		}
		return jdxLines;
	}

	/**
##$BRUKER FILE EXP=scon2
$$ ##TITLE= Parameter file, TOPSPIN		Version 3.0
$$ ##JCAMPDX= 5.0
$$ ##DATATYPE= Parameter Values
$$ ##NPOINTS= 1	$$ modification sequence number
$$ ##ORIGIN= Bruker BioSpin GmbH
$$ ##OWNER= wash198
$$ $$ 2011-05-03 13:13:15.621 -0700  wash198@bokan
$$ $$ /home/wash198/wash198/110503_M190A_AO/2/scon2
$$ $$ process /opt/topspin3.0/prog/mod/go
$$ ##$BLKTR= (0..19)
3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3
$$ ##$DE1= 4.5
$$ ##$DEADC= 0.5
$$ ##$GRADPRE= (0..1)
10 10
$$ ##$HD_BLKTR= (0..19)
1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
$$ ##$HD_DE1= 5
$$ ##$HD_DEADC= 0
$$ ##END=
##$RELAX= 
	 * @param brukerChunk
	 */
	private void processChunk() {
		iNode = 0;
		newChunk = new CMLList();
		newChunk.setTitle(brukerChunk.getTitle());
		newChunk.setDictRef(brukerChunk.getDictRef());
		CMLArray array = null;
		while (iNode < brukerChunk.getChildCount()) {
			String line = brukerChunk.getChild(iNode).getValue().trim();
			if (line.startsWith(DOLLAR2)) {
				if (processAndAddAuditTrail2(line)) {
				} else if (processAndAddArray(line)) {
				} else if (processAndAddScalar(line)) {
				} else if (processAndAddDoubleDollar2Scalar(line)) {
				} else if (line.startsWith(END) || line.equals(DOLLAR2)) {
					iNode++;
				} else if (processAndAddDollar2Scalar(line)) {
				} else { 
					throw new RuntimeException("Cannot interpret line: "+line);
				}
			} else {
				throw new RuntimeException("NO $$");
			}
		}
		brukerChunk.getParent().replaceChild(brukerChunk, newChunk);
 	}
	
	private boolean processAndAddAuditTrail2(String line) {
		Matcher matcher = AUDIT_TRAIL2_PATTERN.matcher(line);
		if (matcher.matches()) {
			iNode++;
			CMLList list = new CMLList();
			list.setTitle(matcher.group(1));
			while (iNode < brukerChunk.getChildCount()) {
				line = brukerChunk.getChild(iNode).getValue();
				if (line.startsWith(DOLLAR2)) {
					break;
				}
				iNode++;
				CMLScalar scalar = new CMLScalar(line);
				list.appendChild(scalar);
			}
			newChunk.appendChild(list);
			return true;
		}
		return false;
	}
	
	private boolean processAndAddArray(String line) {
		Matcher matcher = ARRAY_PATTERN.matcher(line);
		if (matcher.matches()) {
			iNode++;
			String name = matcher.group(1);
			int start = Integer.parseInt(matcher.group(2));
			int end = Integer.parseInt(matcher.group(3));
			String arrayContent = createArrayContent();
			CMLArray array = createArray(arrayContent);
			if (array.getSize() != (end-start+1)) {
				throw new RuntimeException("Array length ("+start+".."+end+") not consistent with values ("+array.getSize()+")");
			}
			setDictRef(array, name);
			newChunk.appendChild(array);
			return true;
		}
		return false;
	}

	private String createArrayContent() {
		StringBuilder sb = new StringBuilder();
		while (iNode < brukerChunk.getChildCount()) {
			String line1 = brukerChunk.getChild(iNode).getValue();
			if (line1.startsWith(DOLLAR2)) {
				break;
			}
			iNode++;
			sb.append(line1+" ");
		}
		return sb.substring(0, sb.length()-1);
	}

	private CMLArray createArray(String tokenString) {
		String[] tokens = tokenString.split(Util.S_WHITEREGEX);
		IntArray intArray = null;
		RealArray realArray = null;
		try {
			intArray = new IntArray(tokens);
		} catch (Exception e) {
			try {
				realArray = new RealArray(tokens);
			} catch (Exception e1) {
			}
		}
		CMLArray array = null;
		if (intArray != null) {
			array = new CMLArray(intArray.getArray());
		} else if (realArray != null) {
			array = new CMLArray(realArray);
		} else {
			array = new CMLArray(tokens);
		}
		return array;
	}

	private void setDictRef(HasDictRef hasDictRef, String name) {
		name = name.toLowerCase();
		if (name.startsWith("$")) {
			name = name.substring(1);
			hasDictRef.setDictRef(BRUKER_PREFIX+name);
		} else {
			hasDictRef.setDictRef(JCAMPDX_PREFIX+name);
		}
	}

	private boolean processAndAddScalar(String line) {
		Matcher matcher = SCALAR_PATTERN.matcher(line);
		if (matcher.matches()) {
			String name = matcher.group(1).trim().replace(Util.S_SPACE, Util.S_UNDER);
			String value = matcher.group(2).trim();
			CMLScalar scalar = new CMLScalar(value);
			setDictRef(scalar, name);
			newChunk.appendChild(scalar);
			iNode++;
			return true;
		}
		return false;
	}

	private boolean processAndAddDoubleDollar2Scalar(String line) {
		Matcher matcher = DOUBLE_DOLLAR2_PATTERN.matcher(line);
		if (matcher.matches()) {
			String name = DOLLAR2S;
			String value = matcher.group(1).trim();
			CMLScalar scalar = new CMLScalar(value);
			setDictRef(scalar, name);
			newChunk.appendChild(scalar);
			iNode++;
			return true;
		}
		return false;
	}

	private boolean processAndAddDollar2Scalar(String line) {
		Matcher matcher = DOLLAR2_PATTERN.matcher(line);
		if (matcher.matches()) {
			String name = DOLLAR2S;
			String value = matcher.group(1).trim();
			CMLScalar scalar = new CMLScalar(value);
			setDictRef(scalar, name);
			newChunk.appendChild(scalar);
			iNode++;
			return true;
		}
		return false;
	}

	private void createAuditTrail(String line) {
		brukerChunk = new CMLList();
		String line0 = line.substring(AUDIT_TRAIL.length()).trim();
		brukerChunk.setTitle(line);
		brukerChunk.setDictRef(BRUKER_PREFIX+"auditTrail");
		topCml.appendChild(brukerChunk);
	}

	private void createBrukerFile(String line) {
		brukerChunk = new CMLList();
		Matcher matcher = BRUKER_FILE_PATTERN.matcher(line);
		if (!matcher.matches()) {
			throw new RuntimeException("Bruker file syntax wrong: "+line);
		}
		String filename = matcher.group(2);
		brukerChunk.setTitle(filename);
		brukerChunk.setDictRef(BRUKER_PREFIX+"file");
		topCml.appendChild(brukerChunk);
	}
}
