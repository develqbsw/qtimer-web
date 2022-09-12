package sk.qbsw.sed.fw.component.form.input;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import sk.qbsw.sed.fw.component.EDataType;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CRequiredTextField<T extends Serializable> extends CTextField<T> {
	private static final long serialVersionUID = 1L;

	public CRequiredTextField(String id, EDataType inputType) {
		this(id, false, inputType);
	}

	public CRequiredTextField(String id, EDataType inputType, int maxLength) {
		super(id, false, inputType, maxLength);
	}

	public CRequiredTextField(String id, boolean usePropertyValidator, EDataType inputType) {
		super(id, usePropertyValidator, inputType);

	}

	public CRequiredTextField(String id, IModel<T> model) {
		this(id, model, false);
	}

	public CRequiredTextField(String id, IModel<T> model, EDataType inputType) {
		super(id, model, inputType);
	}

	public CRequiredTextField(String id, IModel<T> model, boolean usePropertyValidator) {
		super(id, model, usePropertyValidator);
	}

	@Override
	protected void initComp() {
		super.initComp();
		setRequired(true);
	}
}
