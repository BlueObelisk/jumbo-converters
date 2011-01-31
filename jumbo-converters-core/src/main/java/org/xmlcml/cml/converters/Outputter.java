package org.xmlcml.cml.converters;

import nu.xom.Element;

import org.apache.log4j.Logger;

public class Outputter {
	private final static Logger LOG = Logger.getLogger(Outputter.class);
	
	public static final String OUTPUT = "output";

	public enum OutputLevel {
		VERBOSE,
		NORMAL,
		NONE,
		;
		private OutputLevel() {}
		public int getPriority() {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].equals(this)) {
					return i;
				}
			}
			return -1;
		}
		public static int compareTo(OutputLevel level1, OutputLevel level2) {
			int delta = level1.getPriority() - level2.getPriority();
			return (delta == 0) ? 0 : ((delta > 0) ? 1 : -1);
		}
	}
	
	public static OutputLevel extractOutputLevel(Element element) {
		String outputString = element.getAttributeValue(OUTPUT);
		OutputLevel outputLevel = OutputLevel.NONE;
		if (outputString != null) {
			outputLevel = OutputLevel.valueOf(outputString);
			LOG.trace("OUTPUTLEVEL "+outputLevel);
		} 
		return outputLevel;
	}
	
	public static boolean canOutput(OutputLevel outputLevel, OutputLevel maxLevel) {
		return outputLevel.getPriority() - maxLevel.getPriority() >= 0;
	}
	
}
