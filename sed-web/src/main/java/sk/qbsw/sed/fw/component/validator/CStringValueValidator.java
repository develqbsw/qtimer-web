package sk.qbsw.sed.fw.component.validator;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

/**
 * The method isValueValid is called only if the new value is not equals to the
 * old value
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class CStringValueValidator implements IFormValidator {

	private static final long serialVersionUID = 1L;
	private TextField<String> tf;
	private Serializable errorMessage;

	public CStringValueValidator(TextField<String> tf, Serializable errorMessage) {
		super();
		this.tf = tf;
		if (this.tf == null) {
			throw new IllegalArgumentException("component cannot be null");
		}
		
		this.errorMessage = errorMessage;
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return new FormComponent[] { tf };
	}

	@Override
	public void validate(Form<?> form) {
		if (!StringUtils.equals(tf.getModel().getObject(), tf.getValue()) && !isValueValid(tf.getValue())) {
			tf.error(errorMessage);
		}
	}

	public abstract boolean isValueValid(String value);
}
