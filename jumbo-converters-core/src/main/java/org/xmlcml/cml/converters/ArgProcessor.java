package org.xmlcml.cml.converters;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class ArgProcessor {

	private final static Logger LOG = Logger.getLogger(ArgProcessor.class);
	
	private static final String I          = "-i";
	private static final String INPUT      = "--input";
	private static final String O          = "-o";
	private static final String OUTPUT     = "--output";
	private static final String R          = "-r";
	private static final String RECURSIVE  = "--recursive";
	
	private static final String MINUS = "-";
	
	private boolean recursive = false;

	private String input;

	private String output;
	
	public ArgProcessor(String[] commandLineArgs) {
		processArgs(Arrays.asList(commandLineArgs));
	}

	private void processArgs(List<String> args) {
		ListIterator<String> listIterator = args.listIterator();
		while (listIterator.hasNext()) {
			String arg = listIterator.next();
			if (!arg.startsWith(MINUS)) {
				LOG.error("Parsing failed at: ("+arg+"), trying to recover");
				continue;
			}
			if (I.equals(arg) || INPUT.equals(arg)) {processInput(listIterator); continue;}
			if (O.equals(arg) || OUTPUT.equals(arg)) {processOutput(listIterator); continue;}
			if (R.equals(arg) || RECURSIVE.equals(arg)) {processRecursive(listIterator); continue;}
			LOG.error("Unknown arg: ("+arg+"), trying to recover");
		}
	}

	private void processInput(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		input = listIterator.next();
	}

	private void processOutput(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		output = listIterator.next();
	}

	private void processRecursive(ListIterator<String> listIterator) {
		recursive = true;
	}

	private void checkHasNext(ListIterator<String> listIterator) {
		if (!listIterator.hasNext()) {
			throw new RuntimeException("ran off end; expected more arguments");
		}
	}

	public boolean isRecursive() {
		return recursive;
	}

	public String getInput() {
		return input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
