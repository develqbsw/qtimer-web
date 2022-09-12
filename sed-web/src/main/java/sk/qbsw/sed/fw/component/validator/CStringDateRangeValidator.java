package sk.qbsw.sed.fw.component.validator;

import java.text.ParseException;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import sk.qbsw.sed.common.utils.CDateUtils;

/**
 * validator for ranged text input
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CStringDateRangeValidator<T> implements IValidator<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void validate(IValidatable<T> validatable) {
		final String field = (String) validatable.getValue();
		try {
			CDateUtils.parseRange(field);
		} catch (ParseException e) {
			error(validatable, "DateRangeValidator");
		}
	}

	private void error(IValidatable<T> validatable, String errorKey) {
		ValidationError error = new ValidationError();
		error.addKey(errorKey);
		validatable.error(error);
	}
}
