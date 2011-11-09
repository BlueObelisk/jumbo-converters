package org.xmlcml.cml.converters.graphics.png;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.vecmath.Vector2d;

import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.graphics.CDKUtils;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.MoleculeTool;

/** Produces png images for CML molecules using CDK.
 *
 * @author ptc24, ned24
 *
 */
public class Cml2Png implements CMLConstants {

    private static Logger LOG = Logger.getLogger(Cml2Png.class);
    public Color backgroundColour = Color.WHITE;
    public String fontName = "MonoSpaced";
    public int fontStyle = Font.BOLD;
    public int fontSize = 16;
    public boolean colourAtoms = true;
    public boolean fixedWidthAndHeight = false;
    /* If fixedWidthAndHeight... */
    public int width = 500;
    public int height = 500;
    public double occupationFactor = 0.8; /* 1.0 = no border */
    /* else ... */

    public double scaleFactor = 20.0;
    public int borderWidth = 20; /* Pixels. This is *after* a sensible margin for lettering */
    /* ...endif */

    private CMLMolecule cmlMol;
    private String format = "png";

    public Cml2Png() {
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
        renderMolecule(new File(path));
    }

    public void renderMolecule(File file) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            renderMolecule(fout);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error writing 2D image to: " + file.getAbsolutePath());
        } finally {
            IOUtils.closeQuietly(fout);
        }
    }

    public void renderMolecule(OutputStream out) {
//		// at the moment to use this method, the CML molecule must already
//		// have 2D coordinates.
//
        Nodes noXYCoords = cmlMol.query("//*[local-name()='molecule']"
                + "/*[local-name()='atomArray']"
                + "/*[local-name()='atom' and (not(@x2) or not(@y2))]");
        if (noXYCoords.size() > 0) {
            try {
                cmlMol = CDKUtils.add2DCoords(cmlMol);
            } catch (NullPointerException e) {
                LOG.trace("Cannot add coordinates by CDK");
                return;
            }
        }

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



        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        g.setColor(backgroundColour);
        g.fillRect(0, 0, width, height);

        GeometryTools.translateAllPositive(cdkMol);
        // this seems to break it
//		GeometryTools.scaleMolecule(cdkMol, new Dimension(width, height), 0.8);
        GeometryTools.center(cdkMol, new Dimension(width, height));
        r2dm.setBackgroundDimension(new Dimension(width, height));
        r2dm.setBackColor(backgroundColour);
        r2dm.setFont(new Font(fontName, fontStyle, fontSize));
        r2dm.setShowImplicitHydrogens(true);
        r2dm.setShowEndCarbons(true);
        r2dm.setForeColor(Color.black);
        r2dm.setColorAtomsByType(true);

        if (cdkMol != null) {
            try {
                r2d.paintMolecule(cdkMol, img.createGraphics(), new Rectangle(width, height));
            } catch (Exception e) {
                LOG.error("Cannot create PNG " + e.getMessage());
            }
        }

        try {
//			BufferedImage img = renderMolecule(cdkMol);
            ImageIO.write(img, format, out);
        } catch (Exception e) {
            throw new RuntimeException("Error writing image: " + e.getMessage());
        }
    }

    private IMolecule forceSDGGenerationOfCoords(IMolecule cdkMol) {
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
            throw new RuntimeException("Error generating molecule coordinates: " + e.getMessage());
        }
        cdkMol = sdg.getMolecule();
        return cdkMol;
    }

    public void setWidthAndHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
