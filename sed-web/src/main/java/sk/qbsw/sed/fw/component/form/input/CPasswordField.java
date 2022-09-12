package sk.qbsw.sed.fw.component.form.input;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.IValidationError;

import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.converter.CConverterFactory;
import sk.qbsw.sed.fw.component.input.CInputBorder;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CPasswordField extends PasswordTextField implements IComponentLabel {
	private static final long serialVersionUID = 1L;
	private IModel<String> label;
	private final boolean usePropertyValidator;
	private final EDataType inputType;

	public CPasswordField(String id, EDataType inputType) {
		this(id, false, inputType);
	}

	public CPasswordField(String id, boolean usePropertyValidator, EDataType inputType) {
		super(id);
		this.inputType = inputType;
		this.usePropertyValidator = usePropertyValidator;
		initComp();
	}

	public CPasswordField(String id, IModel<String> model) {
		this(id, model, false);
	}

	public CPasswordField(String id, IModel<String> model, EDataType inputType) {
		this(id, model, false, inputType);
	}

	public CPasswordField(String id, IModel<String> model, boolean usePropertyValidator) {
		this(id, model, usePropertyValidator, null);
	}

	public CPasswordField(String id, IModel<String> model, boolean usePropertyValidator, EDataType inputType) {
		super(id, model);
		this.usePropertyValidator = usePropertyValidator;
		this.inputType = inputType;
		initComp();
	}

	protected void initComp() {
		setRequired(false);
		if (usePropertyValidator) {
			add(new PropertyValidator<>());
		}

		setComponentLabelKey(CInputBorder.PREFIX + getId());
	}

	@Override
	public String getComponentLabel() {
		if (label != null) {
			return label.getObject();
		} else {
			return "";
		}

	}

	@Override
	public void setComponentLabelKey(String labelId) {
		label = new StringResourceModel(labelId, this, null);

	}

	@Override
	public void error(IValidationError error) {
		super.error(error);
	}

	public EDataType getEDataType() {
		return inputType;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IConverter getConverter(Class type) {
		IConverter converter = CConverterFactory.getConverter(type, inputType);
		if (converter != null) {
			return converter;
		}
		return super.getConverter(type);
	}

	public void setTooltip(String text) {
		add(AttributeAppender.append("class", "tooltips"));
		add(AttributeModifier.append("data-placement", "bottom"));
		add(AttributeModifier.append("data-original-title", text));
		add(AttributeModifier.append("title", text));
		add(AttributeModifier.append("data-trigger", "hover"));
		add(AttributeModifier.append("data-toggle", "tooltip"));
	}

	public void setReadOnly() {
		add(AttributeAppender.append("readonly", ""));
	}

	@Override
	public void setComponentLabelKey(IModel<String> model) {
		this.label = model;
	}
}
