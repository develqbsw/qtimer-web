package sk.qbsw.sed.fw.component.validator;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.validation.validator.StringValidator;

public class CStringValidator extends StringValidator {
	private static final long serialVersionUID = 1L;

	public CStringValidator(Integer minimum, Integer maximum) {
		super(minimum, maximum);
	}

	public static CStringValidator maximumLength(int length) {
		return new CStringValidator(null, length);
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);
		if (getMaximum() != null && "textarea".equalsIgnoreCase(tag.getName())) {
			tag.put("maxlength", getMaximum());
		}
	}
}
