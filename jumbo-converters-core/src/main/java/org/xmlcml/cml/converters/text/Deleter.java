package org.xmlcml.cml.converters.text;

import nu.xom.Element;

import org.apache.log4j.Logger;

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
		throw new RuntimeException("delete not implemented");
//		while (inode < lineContainer.getChildCount()) {
//			Node node = lineContainer.getChild(inode);
//			if (node instanceof Text) {
//				Int2 range = lineContainer.matchLines(inode, patterns);
//				if (range != null) {
//					// detach nodes; count does not need incrementing
//					for (int i = 0; i < range.getY() - range.getX(); i++) {
//						lineContainer.getChild(inode).detach();
//					}
//					if (ndeleted++ >= maxRepeatCount) {
//						break;
//					}
//					
//				} else {
//					inode++;
//				}
//			} else {
//				inode++;
//			}
//		}
	}

	
}
