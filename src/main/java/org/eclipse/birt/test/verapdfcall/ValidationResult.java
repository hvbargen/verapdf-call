package org.eclipse.birt.test.verapdfcall;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.birt.test.verapdfcall.ValidationMessage.Severity;

public class ValidationResult {

	private boolean valid = true;

	private String inputFileName;

	private List<ValidationMessage> messages;

	public ValidationResult(String inputFileName) {
		this.inputFileName = inputFileName;
		this.messages = new ArrayList<ValidationMessage>();
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the inputFileName
	 */
	public String getInputFileName() {
		return inputFileName;
	}

	/**
	 * @return the messages
	 */
	public List<ValidationMessage> getAllMessages() {
		return messages;
	}

	/**
	 * @return only the warning messages.
	 */
	public Stream<ValidationMessage> getWarnings() {
		return messages.stream().filter(m -> m.severity() == Severity.WARNING);
	}

	/**
	 * @return messages with severity ERROR or FATAL.
	 */
	public Stream<ValidationMessage> getErrors() {
		return messages.stream().filter(m -> switch (m.severity()) {
		case Severity.ERROR, Severity.FATAL -> true;
		default -> false;
		});
	}
	
	/**
	 * Append a message.
	 * For messages with severity ERROR or FATAL, this sets the validity to false.  
	 * @param m
	 */
	public void addMessage(ValidationMessage m) {
		messages.add(m);
		switch (m.severity()) {
		case Severity.ERROR:
			valid = false;
		case Severity.FATAL: 
			valid = false; 
			break;
		default:
			;
		}
	}

}
