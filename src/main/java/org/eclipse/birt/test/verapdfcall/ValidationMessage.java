package org.eclipse.birt.test.verapdfcall;

import java.util.Properties;

public record ValidationMessage
( String message,
  int line,
  int column,
  Severity severity,
  Properties properties
) {
	
	static public enum Severity {
		DEBUG,
		INFO,
		WARNING,
		ERROR,
		FATAL
	}

	/** A message without a specified location */
	public ValidationMessage(String message, Severity severity) {
		this(message, 0, 0, severity, null);
	}

}
