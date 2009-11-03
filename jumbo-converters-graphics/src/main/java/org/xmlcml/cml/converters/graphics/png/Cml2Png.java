package org.xmlcml.cml.converters.graphics.png;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.renderer.IRenderer2D;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;

/** Produces png images for CML molecules using CDK.
 * 
 * @author ptc24, ned24
 *
 */
public class Cml2Png implements CMLConstants {
	
	private CMLMolecule cmlMol;

	public Color backgroundColour = Color.WHITE;
	public String fontName = "Sans Serif";
	public int fontStyle = Font.PLAIN;
	public int fontSize = 16;
	/* Gif apparently doesn't work, for strange legal reasons. */
	public String format = "png";
	
	public double occupationFactor = 0.8; /* 1.0 = no border */
	public double scaleFactor = 20.0;
	public int borderWidth = 20; /* Pixels. This is *after* a sensible margin for lettering */
	
	public int width = 500;
	public int height = 500;
	
	public Cml2Png() {
		;
	}
	
	public Cml2Png(CMLMolecule molecule) {
		this.cmlMol = new CMLMolecule(molecule);
		MoleculeTool mt = MoleculeTool.getOrCreateTool(this.cmlMol);
		mt.contractExplicitHydrogens(CMLMolecule.HydrogenControl.REPLACE_HYDROGEN_COUNT, false);
		if (molecule.getDescendantsOrMolecule().size() > 1) {
			throw new RuntimeException("CMLMolecule must not have any child molecules");
		}
	}
	
	public void renderMolecule(String path) {
		try {
			renderMolecule(new FileOutputStream(new File(path)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error writing 2D image to: "+path);
		}
	}

	public void renderMolecule(File file) {
		try {
			renderMolecule(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error writing 2D image to: "+file.getAbsolutePath());
		}
	}

	public void renderMolecule(OutputStream out) {		
		// at the moment to use this method, the CML molecule must already
		// have 2D coordinates.
		
		Renderer2DModel r2dm = new Renderer2DModel();
		Java2DRenderer r2d = new Java2DRenderer(r2dm);
		
		IMolecule cdkMol = CDKUtils.cmlMol2CdkMol(cmlMol);
		int atomCount = cdkMol.getAtomCount();
		if (atomCount > 1 && atomCount < 20) {
			fontSize = 14;
		} else if (atomCount >= 20 && atomCount < 30) {
			fontSize = 13;
		} else if (atomCount >= 30 && atomCount < 40) {
			fontSize = 12;
		} else if (atomCount >= 40 && atomCount < 50) {
			fontSize = 11;
		} else if (atomCount >= 50) {
			fontSize = 10;
		}
		/*
		for (int i = 0; i < cdkMol.getAtomCount(); i++) {
			IAtom atom = cdkMol.getAtom(i);
			atom.setPoint2d(null);
			atom.setPoint3d(null);	
		}
		
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(cdkMol);
		try {
			sdg.generateCoordinates(new Vector2d(0, 1));
		} catch (Exception e) {
			throw new RuntimeException("Error generating molecule coordinates: "+e.getMessage());
		}
		cdkMol = sdg.getMolecule();
		*/
				
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = img.getGraphics();
		g.setColor(backgroundColour);		
		g.fillRect(0, 0, width, height);
		
		GeometryTools.translateAllPositive(cdkMol);
		GeometryTools.scaleMolecule(cdkMol, new Dimension(width, height), 0.8);
		GeometryTools.center(cdkMol, new Dimension(width, height));
		r2dm.setBackgroundDimension(new Dimension(width, height));
		r2dm.setBackColor(backgroundColour);
		r2dm.setFont(new Font(fontName, fontStyle, fontSize));
		r2dm.setShowImplicitHydrogens(true);
		r2dm.setShowEndCarbons(true);
		
		if (cdkMol != null)
			r2d.paintMolecule(cdkMol, img.createGraphics());
		try {
			ImageIO.write(img, format, out);
		} catch (IOException e) {
			throw new RuntimeException("Error writing image: "+e.getMessage());
		}
	}
	
	public void setWidthAndHeight(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
}
