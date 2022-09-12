package sk.qbsw.sed.fw.component.form.input;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.IValidationError;

/**
 * Drop down choice commponent with required and label mode
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CDropDownChoice<T> extends DropDownChoice<T> implements IComponentLabel {
	private static final long serialVersionUID = 1L;
	private IModel<String> label;

	public CDropDownChoice(String id, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
		super(id, choices, renderer);
		initComp();
	}

	public CDropDownChoice(String id, List<? extends T> choices) {
		super(id, choices);
		initComp();
	}

	public CDropDownChoice(String id, IModel<T> model, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
		super(id, model, choices, renderer);
		initComp();
	}

	public CDropDownChoice(String id, IModel<T> model, List<? extends T> choices) {
		super(id, model, choices);
		initComp();
	}

	public CDropDownChoice(String id, IModel<T> model, final IModel<List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
		super(id, model, choices, renderer);
		initComp();
	}

	protected void initComp() {
		// do nothing
	}

	@Override
	public String getComponentLabel() {
		return label.getObject();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(AttributeModifier.append("class", "dropdown combobox"));
	}

	@Override
	public void setComponentLabelKey(String labelId) {
		label = new StringResourceModel(labelId, this, null);
	}

	@Override
	public void setComponentLabelKey(IModel<String> label) {
		this.label = label;
	}

	@Override
	public void error(IValidationError error) {
		super.error(error);
	}
}
