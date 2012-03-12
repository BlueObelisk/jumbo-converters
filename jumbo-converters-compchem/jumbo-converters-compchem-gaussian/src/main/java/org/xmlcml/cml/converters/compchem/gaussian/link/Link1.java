package org.xmlcml.cml.converters.compchem.gaussian.link;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLScalar;


/**
 * 
 * @author pm286
 *
 *Typical:
 *    <scalar>Entering Link 1 = /opt//g03/l1.exe PID= 14008.</scalar>
    <scalar/>
    <scalar>Copyright (c) 1988,1990,1992,1993,1995,1998,2003,2004,2007, Gaussian, Inc.</scalar>
    <scalar>All Rights Reserved.</scalar>
    <scalar/>
    <scalar>This is the Gaussian(R) 03 program. It is based on the</scalar>
    <scalar>the Gaussian(R) 98 system (copyright 1998, Gaussian, Inc.),</scalar>
    <scalar>the Gaussian(R) 94 system (copyright 1995, Gaussian, Inc.),</scalar>
    <scalar>the Gaussian 92(TM) system (copyright 1992, Gaussian, Inc.),</scalar>
    <scalar>the Gaussian 90(TM) system (copyright 1990, Gaussian, Inc.),</scalar>
    <scalar>the Gaussian 88(TM) system (copyright 1988, Gaussian, Inc.),</scalar>
    <scalar>the Gaussian 86(TM) system (copyright 1986, Carnegie Mellon</scalar>
    <scalar>University), and the Gaussian 82(TM) system (copyright 1983,</scalar>
    <scalar>Carnegie Mellon University). Gaussian is a federally registered</scalar>
    <scalar>trademark of Gaussian, Inc.</scalar>
    <scalar/>
    <scalar>This software contains proprietary and confidential information,</scalar>
    <scalar>including trade secrets, belonging to Gaussian, Inc.</scalar>
    <scalar/>
    <scalar>This software is provided under written license and may be</scalar>
    <scalar>used, copied, transmitted, or stored only in accord with that</scalar>
    <scalar>written license.</scalar>
    <scalar/>
    <scalar>The following legend is applicable only to US Government</scalar>
    <scalar>contracts under FAR:</scalar>
    <scalar/>
    <scalar>RESTRICTED RIGHTS LEGEND</scalar>
    <scalar/>
    <scalar>Use, reproduction and disclosure by the US Government is</scalar>
    <scalar>subject to restrictions as set forth in subparagraphs (a)</scalar>
    <scalar>and (c) of the Commercial Computer Software - Restricted</scalar>
    <scalar>Rights clause in FAR 52.227-19.</scalar>
    <scalar/>
    <scalar>Gaussian, Inc.</scalar>
    <scalar>340 Quinnipiac St., Bldg. 40, Wallingford CT 06492</scalar>
    <scalar/>
    <scalar/>
    <scalar>---------------------------------------------------------------</scalar>
    <scalar>Warning -- This program may not be used in any manner that</scalar>
    <scalar>competes with the business of Gaussian, Inc. or will provide</scalar>
    <scalar>assistance to any competitor of Gaussian, Inc. The licensee</scalar>
    <scalar>of this program is prohibited from giving any competitor of</scalar>
    <scalar>Gaussian, Inc. access to this program. By using this program,</scalar>
    <scalar>the user acknowledges that Gaussian, Inc. is engaged in the</scalar>
    <scalar>business of creating and licensing software in the field of</scalar>
    <scalar>computational chemistry and represents and warrants to the</scalar>
    <scalar>licensee that it is not a competitor of Gaussian, Inc. and that</scalar>
    <scalar>it will not use this program in any manner prohibited above.</scalar>
    <scalar>---------------------------------------------------------------</scalar>
    <scalar/>
    <scalar/>
    <scalar>Cite this work as:</scalar>
    <scalar>Gaussian 03, Revision E.01,</scalar>
    <scalar>M. J. Frisch, G. W. Trucks, H. B. Schlegel, G. E. Scuseria,</scalar>
    <scalar>M. A. Robb, J. R. Cheeseman, J. A. Montgomery, Jr., T. Vreven,</scalar>
    <scalar>K. N. Kudin, J. C. Burant, J. M. Millam, S. S. Iyengar, J. Tomasi,</scalar>
    <scalar>V. Barone, B. Mennucci, M. Cossi, G. Scalmani, N. Rega,</scalar>
    <scalar>G. A. Petersson, H. Nakatsuji, M. Hada, M. Ehara, K. Toyota,</scalar>
    <scalar>R. Fukuda, J. Hasegawa, M. Ishida, T. Nakajima, Y. Honda, O. Kitao,</scalar>
    <scalar>H. Nakai, M. Klene, X. Li, J. E. Knox, H. P. Hratchian, J. B. Cross,</scalar>
    <scalar>V. Bakken, C. Adamo, J. Jaramillo, R. Gomperts, R. E. Stratmann,</scalar>
    <scalar>O. Yazyev, A. J. Austin, R. Cammi, C. Pomelli, J. W. Ochterski,</scalar>
    <scalar>P. Y. Ayala, K. Morokuma, G. A. Voth, P. Salvador, J. J. Dannenberg,</scalar>
    <scalar>V. G. Zakrzewski, S. Dapprich, A. D. Daniels, M. C. Strain,</scalar>
    <scalar>O. Farkas, D. K. Malick, A. D. Rabuck, K. Raghavachari,</scalar>
    <scalar>J. B. Foresman, J. V. Ortiz, Q. Cui, A. G. Baboul, S. Clifford,</scalar>
    <scalar>J. Cioslowski, B. B. Stefanov, G. Liu, A. Liashenko, P. Piskorz,</scalar>
    <scalar>I. Komaromi, R. L. Martin, D. J. Fox, T. Keith, M. A. Al-Laham,</scalar>
    <scalar>C. Y. Peng, A. Nanayakkara, M. Challacombe, P. M. W. Gill,</scalar>
    <scalar>B. Johnson, W. Chen, M. W. Wong, C. Gonzalez, and J. A. Pople,</scalar>
    <scalar>Gaussian, Inc., Wallingford CT, 2004.</scalar>
    <scalar/>
    <scalar>******************************************</scalar>
    <scalar>Gaussian 03: AM64L-G03RevE.01 11-Sep-2007</scalar>
    <scalar>30-Mar-2009</scalar>
    <scalar>******************************************</scalar>
    <scalar>%NProcShared=2</scalar>
    <scalar>Will use up to 2 processors via shared memory.</scalar>
    <scalar>----------------------------------------------------------------------</scalar>
    <scalar>#p uB971/6-311+G(d,p) opt=(Tight, NewEstmFC, MaxCyc = 200) freq #GFInp</scalar>
    <scalar>ut Population=Regular #Integral(Grid=UltraFine) Guess=Mix NoSymmetry</scalar>
    <scalar>----------------------------------------------------------------------</scalar>
    <scalar>1/6=200,7=10,10=7,14=-1,18=20,26=3,38=1/1,3;</scalar>
    <scalar>2/9=110,15=1,17=6,18=5,40=1/2;</scalar>
    <scalar>3/5=4,6=6,7=111,11=2,16=1,24=10,25=1,30=1,74=-20,75=5/1,2,3;</scalar>
    <scalar>4/7=2,9=2,13=1/1;</scalar>
    <scalar>5/5=2,38=5/2;</scalar>
    <scalar>6/28=1/1;</scalar>
    <scalar>7/29=1,30=1/1,2,3,16;</scalar>
    <scalar>1/6=200,10=7,14=-1,18=20/3(3);</scalar>
    <scalar>2/9=110,15=1/2;</scalar>
    <scalar>6/19=2,28=1/1;</scalar>
    <scalar>99//99;</scalar>
    <scalar>2/9=110,15=1/2;</scalar>
    <scalar>3/5=4,6=6,7=111,11=2,16=1,25=1,30=1,74=-20,75=5/1,2,3;</scalar>
    <scalar>4/5=5,7=2,9=2,16=3/1;</scalar>
    <scalar>5/5=2,38=5/2;</scalar>
    <scalar>7/30=1/1,2,3,16;</scalar>
    <scalar>1/6=200,14=-1,18=20/3(-5);</scalar>
    <scalar>2/9=110,15=1/2;</scalar>
    <scalar>6/19=2,28=1/1;</scalar>
    <scalar>99/9=1/99;</scalar>
    <scalar>Leave Link 1 at Mon Mar 30 15:42:29 2009, MaxMem= 0 cpu: 0.2</scalar>

 */
public class Link1 extends GaussianLink {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GaussianLink.class);
	
	private static String LINK_REGEX = " (\\d+)/(\\d+=\\-?\\d+(,\\d+=\\-?\\d+)*)/(\\d+(,\\d+)*)(\\((\\-?\\d+)\\))?\\;";
	private static Pattern LINK_PATTERN;
	static  {
		LINK_PATTERN = Pattern.compile(LINK_REGEX);
	};

    public Link1() {
	}
    
    @Override
    protected String getTitle() {
    	return "Processes route section, builds list of links to execute, and initializes scratch files";
    }
	
	public CMLModule convert2CML() {
		cmlModule = new CMLModule();
		line_num = 0;
	    previous_line = null;
	    
        while (line_num < lineList.size()) {
        	line = lineList.get(line_num++);
//        	LOG.debug("..."+line);
        	if (line.trim().startsWith("Copyright")) {
        		readCopyright();
            } else if (line.trim().startsWith("*******************")) {
            	cmlModule.appendChild(readVersion());
            } else if (line.trim().startsWith("-----------------")) {
            	cmlModule.appendChild(readParams());
            } else if (isLinkCommand(line)) {
                cmlModule.appendChild(readLinkCommands());
            }
        }
        return cmlModule;
	}
	/**
	 * 
	 * @param line
	 * @return
	 */
	private boolean isLinkCommand(String line) {
		Matcher matcher = LINK_PATTERN.matcher(line);
		return matcher.matches();
	}
	
	/**
    <scalar>1/6=200,7=10,10=7,14=-1,18=20,26=3,38=1/1,3;</scalar>
    <scalar>2/9=110,15=1,17=6,18=5,40=1/2;</scalar>
    <scalar>3/5=4,6=6,7=111,11=2,16=1,24=10,25=1,30=1,74=-20,75=5/1,2,3;</scalar>
    <scalar>4/7=2,9=2,13=1/1;</scalar>
    <scalar>5/5=2,38=5/2;</scalar>
    <scalar>6/28=1/1;</scalar>
    <scalar>7/29=1,30=1/1,2,3,16;</scalar>
    <scalar>1/6=200,10=7,14=-1,18=20/3(3);</scalar>
    <scalar>2/9=110,15=1/2;</scalar>
    <scalar>6/19=2,28=1/1;</scalar>
    <scalar>99//99;</scalar>
    <scalar>2/9=110,15=1/2;</scalar>
    <scalar>3/5=4,6=6,7=111,11=2,16=1,25=1,30=1,74=-20,75=5/1,2,3;</scalar>
    <scalar>4/5=5,7=2,9=2,16=3/1;</scalar>
    <scalar>5/5=2,38=5/2;</scalar>
    <scalar>7/30=1/1,2,3,16;</scalar>
    <scalar>1/6=200,14=-1,18=20/3(-5);</scalar>
    <scalar>2/9=110,15=1/2;</scalar>
    <scalar>6/19=2,28=1/1;</scalar>
    <scalar>99/9=1/99;</scalar>
	 * @return
	 */
	private CMLElement readLinkCommands() {
		CMLModule cmlModule = new CMLModule();
		cmlModule.setDictRef("gaussian:links");
		while (true) {
			readLine();
			Matcher matcher = LINK_PATTERN.matcher(line);
			if (!matcher.matches()) {
				line_num--;
				break;
			}
			String overlayS = matcher.group(1);
			String pS = matcher.group(2);
			String[] params = (pS == null) ? null : pS.split(CMLConstants.S_COMMA);
			
			String jumpS = matcher.group(7);
			String sub = matcher.group(4);
			String[] subOver = sub.split(CMLConstants.S_COMMA);
			for (String s : subOver) {
				s = (s.length() == 2) ? s : "0"+s;
				String ss = overlayS + s;
				CMLModule overlayModule = new CMLModule();
				cmlModule.appendChild(overlayModule);
				overlayModule.setTitle(overlayS);
				overlayModule.setDictRef("gaussian:link"+ss);
				if (params != null) {
					for (String param : params) {
						String[] ll = param.split(CMLConstants.S_EQUALS);
						CMLParameter parameter = new CMLParameter();
						parameter.setDictRef("gaussian:p"+ll[0]);
						parameter.setCMLValue(ll[1]);
						overlayModule.appendChild(parameter);
					}
				}
				if (jumpS != null) {
					CMLParameter parameter = new CMLParameter();
					parameter.setDictRef("gaussian:jump");
					parameter.setCMLValue(jumpS);
					overlayModule.appendChild(parameter);
				}
			}
		}
		
		return cmlModule;
	}
	
	/**
    <scalar>Copyright (c) 1988,1990,1992,1993,1995,1998,2003,2004,2007, Gaussian, Inc.</scalar>
    <scalar>All Rights Reserved.</scalar>
    <scalar/>
    <scalar>This is the Gaussian(R) 03 program. It is based on the</scalar>
    <scalar>the Gaussian(R) 98 system (copyright 1998, Gaussian, Inc.),</scalar>
...
    <scalar>B. Johnson, W. Chen, M. W. Wong, C. Gonzalez, and J. A. Pople,</scalar>
    <scalar>Gaussian, Inc., Wallingford CT, 2004.</scalar>
    <scalar/>
	 */
	private void readCopyright() {
//		LOG.debug("copyright from: "+line);
		while (true) {
			readLine();
			if (line.trim().startsWith("Gaussian, Inc., Wallingford CT")) {
//				LOG.debug("read COPYRIGHT to: "+line);
				break;
			}
		}
	}

	/**
    <scalar>******************************************</scalar>
    <scalar>Gaussian 03: AM64L-G03RevE.01 11-Sep-2007</scalar>
    <scalar>30-Mar-2009</scalar>
    <scalar>******************************************</scalar>
	 */
	private CMLModule readVersion() {
		CMLModule module = new CMLModule();
		module.setDictRef("gaussian:version");
		String version = "";
		while (true) {
			readLine();
			if (line.trim().startsWith("*****************")) {
//				LOG.debug("read VERSION to: "+line);
				break;
			}
			version += line;
		}
		CMLScalar scalar = new CMLScalar(version);
		module.appendChild(scalar);
		return module;
	}

	/**
    <scalar>----------------------------------------------------------------------</scalar>
    <scalar>#p uB971/6-311+G(d,p) opt=(Tight, NewEstmFC, MaxCyc = 200) freq #GFInp</scalar>
    <scalar>ut Population=Regular #Integral(Grid=UltraFine) Guess=Mix NoSymmetry</scalar>
    <scalar>----------------------------------------------------------------------</scalar>
    */
    private CMLModule readParams() {
		CMLModule module = new CMLModule();
		module.setDictRef("gaussian:params");
		String params = "";
		while (true) {
			readLine();
			if (line.trim().startsWith("------------------")) {
//				LOG.debug("read PARAMS to: "+line);
				break;
			}
			params += line;
		}
		CMLScalar scalar = new CMLScalar(params);
		module.appendChild(scalar);
		return module;
    }


}
