package org.xmlcml.cml.converters;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Level;

/**
 * A class that helps to write logs for the {@link AbstractConverter}s.
 * 
 *
 */
public class ConverterLog {
	
	/**
	 * The messages in the log.
	 */
	private StringBuilder logMessages;

	/**
	 * Construct a {@link ConverterLog}. Currently just uses a {@link StringBuilder}
	 * for the internal representation of the log.
	 */
	public ConverterLog() {
		clear();
	}
	
	/**
	 * Clears the {@link ConverterLog}.
	 */
	public void clear() {
		logMessages = new StringBuilder();
	}
	
	/**
	 * Append a line to the log in the form: 
	 * "&lt;level&gt;: &lt;message&gt;"
	 * 
	 * @param level the {@link Level} to set this message to
	 * @param message the message {@link String} to append to the log
	 */
	public void addToLog(Level level, String message) {
		logMessages.append(level.toString());
		logMessages.append(": ");
		logMessages.append(message);
		logMessages.append("\n");
	}
	
	/**
	 * Writes out the log to an {@link OutputStream}.
	 * 
	 * @param out the {@link OutputStream} to write to
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		out.write(logMessages.toString().getBytes());
	}
	
	/**
	 * Writes out the log as a {@link String}.
	 * 
	 * @return the log as a {@link String}
	 */
	public String toString() {
		return logMessages.toString();
	}

	/**
	 * Get the log messages {@link StringBuilder}.
	 * 
	 * @return the log messages {@link StringBuilder}
	 */
	public StringBuilder getLogMessages() {
		return logMessages;
	}

	/**
	 * Set the log messages {@link StringBuilder}.
	 * 
	 * @param logMessages the log messages to set
	 */
	public void setLogMessages(StringBuilder logMessages) {
		this.logMessages = logMessages;
	}
	
}
