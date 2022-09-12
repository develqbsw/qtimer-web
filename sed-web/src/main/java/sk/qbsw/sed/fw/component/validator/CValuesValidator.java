package sk.qbsw.sed.fw.component.validator;

import java.io.Serializable;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

/**
 * The method isValueValid is called when validating
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class CValuesValidator implements IFormValidator {

	private static final long serialVersionUID = 1L;
	private FormComponent<?>[] components;
	private Serializable errorMessage;

	public CValuesValidator(Serializable errorMessage, FormComponent<?>... components) {
		super();
		this.components = components;
		if (this.components == null || this.components.length == 0) {
			throw new IllegalArgumentException("component cannot be null");
		}
		this.errorMessage = errorMessage;
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {

		return components;
	}

	@Override
	public void validate(Form<?> form) {
		if (!isValuesValid(components)) {
			for (FormComponent<?> component : components) {
				component.error(errorMessage);
			}
		}
	}

	public abstract boolean isValuesValid(FormComponent<?>[] components);
}
