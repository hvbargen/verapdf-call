package org.eclipse.birt.test.verapdfcall;

import java.io.File;
import java.util.Properties;

public class Main {

	static String BASE_DIR = "C:\\Users\\Henning\\AppData\\Local\\Temp\\verapdf";

	public static void main(String[] args) throws Exception {

		System.setProperty("file.encoding", "UTF-8");

		Properties props = new Properties();
		props.setProperty("BASE_DIR", BASE_DIR);
		props.setProperty("FLAVOURS", "3b,UA1");

		VeraPdfValidator validator = new VeraPdfValidator();
		validator.init(props);

		String pdf_fname = "C:/Users/Henning/Downloads/HyperlinkLabel.pdf";

		System.out.println("Start.");
		for (int i = 0; i < 10; i++) {
			System.out.println("run # " + i);
			ValidationResult result = validator.validateFile(new File(pdf_fname));
			if (result.isValid()) {
				System.out.println("is valid " + validator.getInfo().get("Flavours"));
			} else {
				System.err.println("is INVALID " + validator.getInfo().get("Flavours"));
				if (i == 0) {
					for (ValidationMessage m : result.getErrors().toList()) {
						System.err.println(m.toString());
					}
				}
			}
		}
		System.out.println("Done.");
	}

}
