package org.xmlcml.cml.converters.cif;

import static org.xmlcml.cml.converters.cif.CrystalEyeConstants.COMPLETE_CML_MIME;

import org.xmlcml.cml.converters.cif.CrystalEyeUtils.DisorderType;

public class SingleStructureSummary {
	
	@SuppressWarnings("unused")
	private String publisherTitle;
	@SuppressWarnings("unused")
	private String journalTitle;
	@SuppressWarnings("unused")
	private String year;
	@SuppressWarnings("unused")
	private String issueNum;
	private String doi;
	private String title;
	private String displayPathPrefix;
	private String id;
	private String crystComp;
	private String inchi;
	private String smiles;
	private int folderDepth;
	@SuppressWarnings("unused")
	private String prefix;
	private DisorderType disordered;

	public SingleStructureSummary(String publisherTitle, String journalTitle, String year, String issueNum, String doi, String title, String displayPathPrefix, String id, String crystComp, String inchi, String smiles, int folderDepth, String prefix, DisorderType disordered) {
		super();
		this.publisherTitle = publisherTitle;
		this.journalTitle = journalTitle;
		this.year = year;
		this.issueNum = issueNum;
		this.doi = doi;
		this.title = title;
		this.displayPathPrefix = displayPathPrefix;
		this.id = id;
		this.crystComp = crystComp;
		this.inchi = inchi;
		this.smiles = smiles;
		this.folderDepth = folderDepth;
		this.prefix = prefix;
		this.disordered = disordered;
	}

	public SingleStructureSummary() {
		;
	}
	
	private String getDisorderedSection() {
		String output = "";
		if (disordered.equals(DisorderType.UNPROCESSED)) {
			output = "<div style=\"border: 1px dashed red; text-align: center; font-size: 12px; background: #ffbbbb;\">"+
					"<p style=\"color: red;\">We could not resolve the disorder in this crystal structure.</p>"+
				"</div>";
		} else if (disordered.equals(DisorderType.PROCESSED)) {
			output = "<div style=\"border: 1px dashed green; text-align: center; font-size: 12px; background: #99ff99;\">"+
					"<p style=\"color: green;\">The structure displayed is the major occupied structure from the crystal.</p>"+
				"</div>";
		}
		return output;
	}

	public String getWebPage() {
		String disorderedSection = getDisorderedSection();
		
		String indexBackTrack = "";
		for (int i = 0; i < folderDepth; i++) {
			indexBackTrack += "../";
		}
		String crystalBackTrack = "";
		for (int i = 0; i < folderDepth-3; i++) {
			crystalBackTrack += "../";
		}		
		// make smiles td
		String smilesSection = "";
		String inchiSection = "";
		if (!"".equals(smiles) && smiles != null) {
			smilesSection = "<tr>"+
								"<td>"+
									"<p class=\"title\">SMILES:</p>"+
								"</td>"+
								"<td>"+
									"<p>"+smiles+"</p>"+
								"</td>"+
							"</tr>";
		}
		if (!"".equals(inchi) && inchi != null) {
			inchiSection = "<tr>"+
								"<td>"+
									"<p class=\"title\">InChI:</p>"+
								"</td>"+
								"<td>"+
									"<p>"+
										inchi+
									"</p>"+
								"</td>"+
							"</tr>";
		}
		
		String page = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
			"<html>"+
			"<head>"+
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
				"<title>"+title+"</title>"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\""+displayPathPrefix+"display/eprints.css\" title=\"screen stylesheet\" media=\"screen\" />"+
				"<link rel=\"Top\" href=\"http://wwmm.ch.cam.ac.uk\" />"+
				"<script src=\""+displayPathPrefix+"Jmol.js\" type=\"text/javascript\"></script>"+
			"</head>"+
			"<body topmargin=\"0\" rightmargin=\"0\" leftmargin=\"0\" height=\"100%\" id=\"page_abstract\" bgcolor=\"#ffffff\" marginheight=\"0\" marginwidth=\"0\" text=\"#000000\">"+
				"<script type=\"text/javascript\">jmolInitialize(\""+displayPathPrefix+"\");</script>"+
				//"<h1>"+formula+"</h1>"+
				"<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"800\">"+
					"<tbody>"+
						"<tr height=\"100%\">"+
							"<td class=\"shadow_l\" rowspan=\"2\" width=\"25\"></td>"+
							"<td>"+
								"<p style=\"margin-top: 20px;\"></p>"+
								"<table border=\"0\" cellpadding=\"4\" cellspacing=\"0\" height=\"100%\" width=\"100%\">"+
									"<tbody>"+
										"<tr>"+
											"<td align=\"left\">"+
												"<div style=\"width: 49%; float: left; margin-right: 1%; margin-top: 20px;\">"+
													"<!-- Open Knowledge Link -->"+
														"<a href=\"http://okd.okfn.org/\">"+
													  	"<img alt=\"This material is Open Knowlege\" border=\"0\" src=\"http://m.okfn.org/images/ok_buttons/od_80x15_red_green.png\" /></a>"+
													  	"<br /><br />"+
												  	"<!-- /Open Knowledge Link -->"+
													"<a href=\""+indexBackTrack+"index.html\">&lt;&lt; Table of Contents</a><br />"+
											//		"<a href=\""+crystalBackTrack+prefix+"\">&lt;&lt; Crystal summary</a><br />"+
													"<br />"+
													"<strong>DOI: </strong>"+
													"<span style=\"font-size: 0.9em;\">"+
														"<a href=\"http://dx.doi.org/"+doi+"\">"+
															doi+
														"</a>"+
													"</span>"+
													"<br />"+
													"<table border=\"0\" cellpadding=\"3\" width=\"100%\">"+
														"<tbody>"+
															"<tr>"+
																"<td colspan=\"2\">"+
																	"<h2>Available Resources</h2>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td colspan=\"2\">"+
																	"<h3>Result files</h3>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td bgcolor=\"#c0ffc0\">"+
																	"<a href=\"./"+ id +COMPLETE_CML_MIME+"\">CML</a>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td colspan=\"2\">"+
																	"<h3><a name=\"?\"></a>Images</h3>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td bgcolor=\"#c0ffc0\">"+
																	"<a href=\"./"+ id + ".png\">Large 2D Structure</a>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td colspan=\"2\">"+
																	"<h3><a name=\"?\"></a>Bond Properties</h3>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td bgcolor=\"#c0ffc0\">"+
																	"<a href=\"./"+ id + ".lengths.html\">Lengths</a>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td bgcolor=\"#c0ffc0\">"+
																	"<a href=\"./"+ id + ".angles.html\">Angles</a>"+
																"</td>"+
															"</tr>"+
															"<tr>"+
																"<td bgcolor=\"#c0ffc0\">"+
																	"<a href=\"./"+ id + ".torsions.html\">Torsions</a>"+
																"</td>"+
															"</tr>"+
															crystComp+
														"</tbody>"+
													"</table>"+
												"</div>"+
												"<div style=\"width: 49%; float: left;\">"+
													disorderedSection+
													"<p>"+
														"<script type=\"text/javascript\">"+
															"jmolApplet(370, \"load ./"+id+COMPLETE_CML_MIME+"; set unitcell on;\");"+
														"</script>"+
														"</p>"+
												"</div>"+
												"<br />"+
											"</td>"+
										"</tr>"+
										"<tr>"+
											"<td>"+
												"<table id=\"identifiers\" style=\"margin-top: 20px;\">"+
													inchiSection+
													smilesSection+
												"</table>"+
											"</td>"+
										"</tr>"+
									"</tbody>"+
								"</table>"+
							"</td>"+
						"</tr>"+
					"</tbody>"+
				"</table>"+
			"</body>"+
		"</html>";
		return page;
	}
}
