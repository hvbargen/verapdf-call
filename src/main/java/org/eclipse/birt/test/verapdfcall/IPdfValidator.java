package org.eclipse.birt.test.verapdfcall;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.SortedMap;

public interface IPdfValidator {

	void init(Properties props) throws Exception;

	void shutdown() throws Exception;

	ValidationResult validateBytes(byte[] input, String pseudoFilename);

	ValidationResult validateFile(File input);
	
	void addInfo(String name, String value);
	
	SortedMap<String, String> getInfo();

}