package org.xmlcml.cml.converters;

import java.util.ArrayList;
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
	
	private static final String E          = "-e";
	private static final String EXTENSION  = "--extension";
	private static final String I          = "-i";
	private static final String INPUT      = "--input";
	private static final String O          = "-o";
	private static final String OUTPUT     = "--output";
	private static final String R          = "-r";
	private static final String RECURSIVE  = "--recursive";
	private static final String X          = "-x";
	private static final String XPATH  = "--xpath";
	
	private static final String MINUS = "-";
	
	private boolean recursive = false;
	private String input;
	private String output;
	private List<String> unlabelledArgs;
	private List<String> extensionList = new ArrayList<String>();
	private String xpath;
	
	public ArgProcessor(String[] commandLineArgs) {
		processArgs(Arrays.asList(commandLineArgs));
	}

	private void processArgs(List<String> args) {
		ListIterator<String> listIterator = args.listIterator();
		unlabelledArgs = new ArrayList<String>();
		while (listIterator.hasNext()) {
			String arg = listIterator.next();
			if (!arg.startsWith(MINUS)) {
//				LOG.error("Parsing failed at: ("+arg+"), trying to recover");
				unlabelledArgs.add(arg);
				continue;
			}
			if (E.equals(arg) || EXTENSION.equals(arg)) {processExtension(listIterator); continue;}
			if (I.equals(arg) || INPUT.equals(arg)) {processInput(listIterator); continue;}
			if (O.equals(arg) || OUTPUT.equals(arg)) {processOutput(listIterator); continue;}
			if (R.equals(arg) || RECURSIVE.equals(arg)) {processRecursive(listIterator); continue;}
			if (X.equals(arg) || XPATH.equals(arg)) {processXpath(listIterator); continue;}
			LOG.error("Unknown arg: ("+arg+"), trying to recover");
		}
		processNonMinusArgs();
	}

	private void processNonMinusArgs() {
		if (unlabelledArgs.size() == 0) {
			// specific input
			if (input != null) {
				return;
			} else {
				throw new RuntimeException("Reading from STDIN not yet implemented");
			}
		} else {
			if (input != null) {
				throw new RuntimeException("Cannot give both filenames and -i option");
			} else {
				
			}
		}
	}

	private void processExtension(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String extension = listIterator.next();
		extensionList.add(extension);
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

	private void processXpath(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		xpath = listIterator.next();
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

	public List<String> getUnlabelledArgs() {
		return unlabelledArgs;
	}
}
