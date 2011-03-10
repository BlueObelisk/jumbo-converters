package org.xmlcml.cml.converters.text;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Int2;

public class Deleter extends Template {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(Deleter.class);
	
	public static final String TAG = "deleter";
	
	public Deleter(Element childElement) {
		super(childElement);
	}

	public void applyMarkup(LineContainer lineContainer) {
		this.deleteLines(lineContainer, Integer.MAX_VALUE);
	}

	public void deleteLines(LineContainer lineContainer, int maxRepeatCount) {
// use chunks
		int inode = 0;
		int ndeleted = 0;
		Element linesElement = lineContainer.getLinesElement();
		while (inode < linesElement.getChildCount()) {
			Node node = linesElement.getChild(inode);
			if (node instanceof Text) {
				Int2 range = lineContainer.matchLines(inode, startChunker.getPatternList());
				if (range != null) {
					// detach nodes; count does not need incrementing
					for (int i = 0; i < range.getY() - range.getX(); i++) {
						linesElement.getChild(inode).detach();
					}
					if (ndeleted++ >= maxRepeatCount) {
						break;
					}
					
				} else {
					inode++;
				}
			} else {
				inode++;
			}
		}
	}

	
}
