package sk.qbsw.sed.fw.component.form.input;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.converter.CConverterFactory;
import sk.qbsw.sed.fw.component.input.CInputBorder;
import sk.qbsw.sed.fw.component.validator.CNumberValidator;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CTextField<T> extends TextField<T> implements IComponentLabel {
	private static final long serialVersionUID = 1L;
	private IModel<String> label;
	private final boolean usePropertyValidator;
	private final EDataType inputType;

	public CTextField(String id, EDataType inputType) {
		this(id, false, inputType);
	}

	public CTextField(String id, EDataType inputType, long maxLength) {
		this(id, false, inputType, maxLength);
	}

	public CTextField(String id, boolean b, EDataType inputType, long maxLength) {
		this(id, false, inputType);
		addMaxLengthValidator(maxLength);
	}

	public CTextField(String id, boolean usePropertyValidator, EDataType inputType) {
		super(id);
		this.inputType = inputType;
		this.usePropertyValidator = usePropertyValidator;
		initComp();
	}

	public CTextField(String id, IModel<T> model) {
		this(id, model, false);
	}

	public CTextField(String id, IModel<T> model, EDataType inputType, int maxLength) {
		this(id, model, false, inputType);
		addMaxLengthValidator(maxLength);
	}

	public CTextField(String id, IModel<T> model, EDataType inputType) {
		this(id, model, false, inputType);
	}

	public CTextField(String id, IModel<T> model, boolean usePropertyValidator) {
		this(id, model, usePropertyValidator, null);
	}

	public CTextField(String id, IModel<T> model, boolean usePropertyValidator, EDataType inputType) {
		super(id, model);
		this.usePropertyValidator = usePropertyValidator;
		this.inputType = inputType;
		initComp();
	}

	@SuppressWarnings("unchecked")
	protected void initComp() {
		if (usePropertyValidator) {
			add(new PropertyValidator<>());
		}
		
		setComponentLabelKey(CInputBorder.PREFIX + getId());
		
		if (inputType == EDataType.NUMBER) {
			add((IValidator<? super T>) CNumberValidator.getInstance());
		}
		
		addClass();
	}

	@Override
	public String getComponentLabel() {
		if (label != null) {
			return label.getObject();
		} else {
			return "";
		}
	}

	private void addClass() {
		StringBuilder cssClass = new StringBuilder();
		cssClass.append("form-control ");
		switch (this.inputType) {
		case DATE:
			cssClass.append("date-picker ");
			break;

		default:
			break;
		}
		add(AttributeAppender.append("class", cssClass.toString()));
	}

	@Override
	public void setComponentLabelKey(String labelId) {
		label = new StringResourceModel(labelId, this, null);

	}

	@Override
	public void setComponentLabelKey(IModel<String> model) {
		label = model;

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
		IConverter converter = CConverterFactory.getConverter(type, inputType, true);
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

	/**
	 * remove all feedback messages
	 */
	public final void refresh() {
		getFeedbackMessages().clear();
	}

	private void addMaxLengthValidator(long maxLength) {
		if (maxLength != 0) {
			if (EDataType.TEXT == inputType) {
				add((IValidator<? super T>) StringValidator.maximumLength((int) maxLength));
			} else if (EDataType.NUMBER_LONG == inputType) {
				add(new RangeValidator<Long>(0l, Long.MAX_VALUE));
				add(new AttributeModifier("maxlength", maxLength));
			} else if (EDataType.NUMBER == inputType || EDataType.NUMBER_FILTER == inputType) {
				add(new RangeValidator<Integer>(0, Integer.MAX_VALUE));
				add(new AttributeModifier("maxlength", maxLength));
			}
		}
	}

	/**
	 * we does not want trim
	 */
	@Override
	public String getInput() {
		String[] input = getInputAsArray();
		if (input == null || input.length == 0) {
			return null;
		} else {
			return input[0];
		}
	}
}
