package sk.qbsw.sed.fw.component.form.input;

import java.io.Serializable;

import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.IValidationError;

/**
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CTextArea<T extends Serializable> extends TextArea<T> implements IComponentLabel {
	private static final long serialVersionUID = 1L;
	private IModel<String> label;

	public CTextArea(String id) {
		super(id);
		add(new PropertyValidator<>());
	}

	@Override
	public String getComponentLabel() {
		return label.getObject();
	}

	@Override
	public void setComponentLabelKey(String labelId) {
		label = new StringResourceModel(labelId, this, null);
	}

	@Override
	public void error(IValidationError error) {
		super.error(error);
	}

	@Override
	public void setComponentLabelKey(IModel<String> model) {
		this.label = model;
	}
}
