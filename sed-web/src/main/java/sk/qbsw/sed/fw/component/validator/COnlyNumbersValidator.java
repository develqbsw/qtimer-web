package sk.qbsw.sed.fw.component.validator;

import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class COnlyNumbersValidator<T> implements IValidator<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void validate(IValidatable<T> validatable) {
		final String field = (String) validatable.getValue();

		if (!field.matches(MetaPattern.DIGITS.toString())) {
			error(validatable, "codeChange.code.onlyNumbers");
		}
	}

	private void error(IValidatable<T> validatable, String errorKey) {
		ValidationError error = new ValidationError();
		error.addKey(errorKey);
		validatable.error(error);
	}
}
