package org.eclipse.birt.test.verapdfcall;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.birt.test.verapdfcall.ValidationMessage.Severity;

public abstract class AbstractPdfValidator implements IPdfValidator {
	
	private SortedMap<String, String> infoMap = new TreeMap<>();
	
	public AbstractPdfValidator() throws Exception {
	}
	
	@Override
	public abstract void init(Properties props) throws Exception;	
	
	@Override
	public abstract void shutdown() throws Exception;	
	
	abstract void performValidation(byte[] input, ValidationResult result) throws Exception;

	private void validate(byte[] input, ValidationResult result) {
		try {
			if (!result.isValid()) {
				System.err.println("Programming error: ValidationResult was not clean.");
				return;
			}
			performValidation(input, result);
		} catch (Throwable t) {
			result.addMessage(new ValidationMessage(t.getLocalizedMessage(), Severity.FATAL));
		}
	}
	
	@Override
	public ValidationResult validateBytes(byte[] input, String pseudoFilename) {
		ValidationResult result = new ValidationResult(pseudoFilename);
		validate(input, result);
		return result;
	}
	
	@Override
	public ValidationResult validateFile(File input) {
		ValidationResult result = new ValidationResult(input.toString());
		try (FileInputStream fis = new FileInputStream(input);
				BufferedInputStream bis = new BufferedInputStream(fis, 0x10000);
			) {
			byte[] bytes = bis.readAllBytes();
			validate(bytes, result);
		} catch (IOException ioe) {
			result.addMessage(new ValidationMessage(ioe.getLocalizedMessage(), Severity.FATAL));
		}
		return result;
	}

	@Override
	public void addInfo(String name, String value) {
		infoMap.put(name, value);
	}

	
	public SortedMap<String, String> getInfo() {
		return infoMap;
	}
	
}
