package org.xmlcml.cml.converters.cif;


/**
 * @TODO We need a templating mechanism, or else use an html toolkit here.
 */
public class CifSummaryToc {

	private String title;
	private String header;
	private String table;
	private String structureCount;
	private String jmolLoadForSummary;
	private String imageLoadForSummary;
	private String maxImage;
	private int levelsBelowJmolApplet;
	
	public CifSummaryToc(String title, String header, String table, String structureCount, String jmolLoadForSummary, String imageLoadForSummary, String maxImage, int levelsBelowJmolApplet) {
		super();
		this.title = title;
		this.header = header;
		this.table = table;
		this.structureCount = structureCount;
		this.jmolLoadForSummary = jmolLoadForSummary;
		this.imageLoadForSummary = imageLoadForSummary;
		this.maxImage = maxImage;
		this.levelsBelowJmolApplet = levelsBelowJmolApplet;
	}

	public String getWebpage() {
		String levelString = "";
		if (levelsBelowJmolApplet == 0) {
			levelString = "./";
		} else if (levelsBelowJmolApplet > 0) {
			for (int i = 0; i < levelsBelowJmolApplet; i++) {
				levelString += "../";	
			}
		} else {
			throw new RuntimeException("Webpage to be created must be below directory of Jmol applet - please supply levelsBelowJmolApplet as > 0.");
		}
		
		String page = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"+
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">"+
				"<head>"+
					"<title>"+title+"</title>"+
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />"+
					"<script type=\"text/javascript\" src=\""+levelString+"Jmol.js\"></script>"+
					"<script type=\"text/javascript\" src=\""+levelString+"summary.js\"></script>"+
					"<link href=\""+levelString+"display/summary.css\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" />"+
				"</head>"+
				"<body>"+
					"<script type=\"text/javascript\">jmolInitialize(\""+levelString+"\");</script>"+
					"<div id=\"top\">" +
						"<h1>"+
							header+
						"</h1>"+
					"</div>"+
					"<div id=\"content\">"+
						table+
					"</div>"+
					"<div id=\"rendering\">"+
						"<div id=\"navigation\">"+
							"<div id=\"structNav\">"+
								"<button onclick=\"goToStructure(1);\"><<-</button>"+
								"<button onclick=\"previousStructure();\"><-</button>"+
								"<button onclick=\"nextStructure();\">-></button>"+
								"<button onclick=\"goToStructure("+structureCount+");\">->></button>"+
							"</div>"+
							"<div id=\"imgNav\">"+
								"<button onclick=\"previousImage();\">Prev</button>"+
								"<span><span id=\"currentImg\">1</span>/<span id=\"maxImage\">"+maxImage+"</span></span>"+
								"<button onclick=\"nextImage();\">Next</button>"+
							"</div>"+
						"</div>"+
						"<div id=\"jmolContainer\">"+
							"<script type=\"text/javascript\">setMaxStructNum("+structureCount+");highlightFirstStructure();maxImageNum="+maxImage+"</script>"+
							"<script type=\"text/javascript\">"+
							  "jmolApplet([360, 280], "+this.jmolLoadForSummary+");"+
							"</script>"+
							"<img id=\"twod\" src="+this.imageLoadForSummary+" width=\"358\" height=\"278\" alt=\"\" />"+
						"</div>"+
					"</div>"+
				"</body>"+
			"</html>";
		return page;
	}
}
