package org.eclipse.birt.test.verapdfcall;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.verapdf.apps.GreenfieldCliWrapper;

import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;


public class Main {
	
	static String BASE_DIR = "C:\\Users\\Henning\\AppData\\Local\\Temp\\verapdf";

	public static void main(String[] args) throws Exception {
		
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("app.name", "VeraPDF validation GUI");
		System.setProperty("app.repo", BASE_DIR + File.separator + "bin");
		System.setProperty("app.home", BASE_DIR);
		System.setProperty("basedir", BASE_DIR);
		
		if (false) {
			GreenfieldCliWrapper.main(args);
		} else {
			// Initialization
			VeraGreenfieldFoundryProvider.initialise();
			
			// For each file:
			
			
			
			String pdf_fname = "C:/Users/Henning/Downloads/HyperlinkLabel.pdf";
			System.out.println("Start.");
			for (int i=0; i<10; i++) {
				System.out.println("run # " + i);
				checkFlavour(pdf_fname, "3b");
				checkFlavour(pdf_fname, "UA1");
			}
			System.out.println("Done.");
		}
	
	}

	/**
	 * @param pdf_fname
	 * @param flavourName
	 */
	private static void checkFlavour(String pdf_fname, String flavourName) {
		PDFAFlavour flavour = PDFAFlavour.fromString(flavourName);
		try (PDFAParser parser = Foundries.defaultInstance().createParser(new FileInputStream(pdf_fname), flavour)) {
		    PDFAValidator validator = Foundries.defaultInstance().createValidator(flavour, false);
		    ValidationResult result = validator.validate(parser);
		    if (result.isCompliant()) {
		      System.out.println(pdf_fname + ": File is a valid PDF/A " + flavourName);
		    } else {
		    	// Show the errors
		      System.err.println(pdf_fname + ": File is not a valid PDF/A " + flavourName);
		      System.err.println("Validation errors:");
		      var failedChecks = result.getFailedChecks();
		      var keys = new ArrayList<>(failedChecks.keySet());
		      Collections.sort(keys, (o1, o2) -> o1.getSpecification().compareTo(o2.getSpecification()));
		      for (var rule: keys) {
		    	  System.err.println(rule.toString() + ": " + String.valueOf(failedChecks.get(rule)));
		    	  System.err.println(rule.getSpecification().getName());
		    	  System.err.println(rule.getSpecification().getDescription());
		      }
		    }
		} catch (IOException | ValidationException | ModelParsingException | EncryptedPdfException exception) {
			System.err.println(pdf_fname + ": Exception during validation:");
			exception.printStackTrace();
		}
	}

}
