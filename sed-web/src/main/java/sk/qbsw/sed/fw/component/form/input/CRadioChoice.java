package sk.qbsw.sed.fw.component.form.input;

import java.util.List;

import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

public class CRadioChoice<T> extends RadioChoice<T> implements IComponentLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IModel<String> label;

	public CRadioChoice(String id, List<? extends T> choices) {
		super(id, choices);
	}

	public CRadioChoice(String id, IModel<T> model, List<? extends T> options) {
		super(id, model, options);
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
	public void setComponentLabelKey(IModel<String> label) {
		this.label = label;
	}
}
