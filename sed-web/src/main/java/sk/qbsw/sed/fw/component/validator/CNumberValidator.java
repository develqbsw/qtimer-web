package sk.qbsw.sed.fw.component.validator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Validator for checking if input is a number.
 * 
 * @author Ľubomír Grňo
 *
 */
public class CNumberValidator extends PatternValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** singleton instance */
	private static final CNumberValidator INSTANCE = new CNumberValidator();

	public CNumberValidator() {
		super("^[0-9]*$");
	}

	/**
	 * Retrieves the singleton instance of <code>CNumberValidator</code>.
	 * 
	 * @return the singleton instance of <code>CNumberValidator</code>.
	 */
	public static CNumberValidator getInstance() {
		return INSTANCE;
	}

	@Override
	public void validate(IValidatable<String> validatable) {
		Object o = validatable.getValue();
		if (o instanceof Integer) {
			// ok
		} else {
			super.validate(validatable);
		}
	}
}
