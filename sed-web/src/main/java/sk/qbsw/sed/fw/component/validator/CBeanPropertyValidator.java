package sk.qbsw.sed.fw.component.validator;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CBeanPropertyValidator<T> extends Behavior implements INullAcceptingValidator<T> {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CBeanPropertyValidator.class);

	private static final Pattern MESSAGE_TEMPLATE_FIXATING_PATTERN = Pattern.compile("[{}]");

	private final Class<?> beanClass;
	private final String propertyName;
	private final Class<?>[] groups;

	public CBeanPropertyValidator(final Class<?> beanClass, String propertyName, final Class<?>... groups) {
		this.beanClass = beanClass;
		this.propertyName = propertyName;
		this.groups = groups;
	}

	@Override
	public void validate(IValidatable<T> validatable) {
		final T value = validatable.getValue();
		final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		final Set<?> violations = validator.validateValue(this.beanClass, this.propertyName, value, this.groups);

		for (final Object v : violations) {
			final ConstraintViolation<?> violation = (ConstraintViolation<?>) v;
			final Map<String, Object> atts = violation.getConstraintDescriptor().getAttributes();
			final ValidationError validationError = new ValidationError(this);
			final String fixedMessageTemplate = MESSAGE_TEMPLATE_FIXATING_PATTERN.matcher(violation.getMessageTemplate()).replaceAll("");

			validationError.setVariables(atts);
			validationError.addKey(fixedMessageTemplate);

			validatable.error(validationError);
			CBeanPropertyValidator.LOGGER.error("Violation = " + this.propertyName + " " + violation.getMessage() + " - value was " + value);
		}
		customValidation();
	}

	private void customValidation() {
		// do nothing
	}
}
