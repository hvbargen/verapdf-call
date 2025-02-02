package org.eclipse.birt.test.verapdfcall;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.birt.test.verapdfcall.ValidationMessage.Severity;
import org.eclipse.birt.test.verapdfcall.ValidationResult;
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.VeraPDFFoundry;
import org.verapdf.pdfa.flavours.PDFAFlavour;

public class VeraPdfValidator extends AbstractPdfValidator {

	private String baseDir = null;

	// We use a LinkedHashMap because it guarantees that is iterated in order of
	// insertion.
	private VeraPDFFoundry foundry = null;
	private String[] flavourNames;
	private PDFAFlavour[] flavours;
	private PDFAValidator[] validators;

	public VeraPdfValidator() throws Exception {
		super();
	}

	@Override
	public void init(Properties props) throws Exception {
		baseDir = props.getProperty("BASE_DIR", "C:\\Users\\Henning\\AppData\\Local\\Temp\\verapdf");
		System.setProperty("app.name", "VeraPDF validation GUI");
		System.setProperty("basedir", baseDir);
		System.setProperty("app.repo", props.getProperty("APP_REPO", baseDir + File.separator + "bin"));
		System.setProperty("app.home", props.getProperty("APP_HOME", baseDir));
		VeraGreenfieldFoundryProvider.initialise();

		addInfo("Flavours", props.getProperty("FLAVOURS", "3b,UA1"));
		foundry = Foundries.defaultInstance();
		flavourNames = props.getProperty("FLAVOURS", "3b,UA1").split(",");
		flavours = new PDFAFlavour[flavourNames.length];
		validators = new PDFAValidator[flavourNames.length];
		for (int i = 0; i < flavourNames.length; i++) {
			flavours[i] = PDFAFlavour.fromString(flavourNames[i]);
			validators[i] = foundry.createValidator(flavours[i], false);
		}
	}

	@Override
	public void shutdown() throws Exception {
		// nothing to do
	}

	void performValidation(byte[] input, ValidationResult result) throws Exception {
		for (int i = 0; i < flavourNames.length; i++) {
			PDFAValidator validator = validators[i];
			try (PDFAParser parser = foundry.createParser(new ByteArrayInputStream(input), flavours[i])) {
				org.verapdf.pdfa.results.ValidationResult veraResult = validator.validate(parser);
				if (!veraResult.isCompliant()) {
					result.addMessage(new ValidationMessage("PDF is NOT COMPLIANT to flavour " + flavourNames[i],
							Severity.ERROR));
				}
				var failedChecks = veraResult.getFailedChecks();
				var keys = new ArrayList<>(failedChecks.keySet());
				Collections.sort(keys, (o1, o2) -> o1.getSpecification().compareTo(o2.getSpecification()));
				for (var rule : keys) {
					String text = rule.toString() + ": " + String.valueOf(failedChecks.get(rule));
					Properties props = new Properties();
					props.put("Specification.Name", rule.getSpecification().getName());
					props.put("Specidication.Description", rule.getSpecification().getDescription());
					result.addMessage(new ValidationMessage(text, 0, 0, Severity.ERROR, props));
				}
			} catch (IOException | ValidationException | ModelParsingException | EncryptedPdfException exc) {
				result.addMessage(new ValidationMessage(exc.getLocalizedMessage(), Severity.FATAL));
			}
		}
	}

}
