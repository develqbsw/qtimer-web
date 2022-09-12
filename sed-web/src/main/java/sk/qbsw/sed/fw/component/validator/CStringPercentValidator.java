package sk.qbsw.sed.fw.component.validator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class CStringPercentValidator<T> implements IValidator<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void validate(IValidatable<T> validatable) {
		final Long val = (Long) validatable.getValue();

		if (val <= 0 || val > 100) {
			error(validatable, "error.input.duration.percent");
		}
	}

	private void error(IValidatable<T> validatable, String errorKey) {
		ValidationError error = new ValidationError();
		error.addKey(errorKey);
		validatable.error(error);
	}
}
