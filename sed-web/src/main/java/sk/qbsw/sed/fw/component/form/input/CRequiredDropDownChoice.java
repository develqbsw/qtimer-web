package sk.qbsw.sed.fw.component.form.input;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * Drop down choice commponent with required and label mode
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 */
public class CRequiredDropDownChoice<T> extends CDropDownChoice<T> {
	private static final long serialVersionUID = 1L;

	public CRequiredDropDownChoice(String id, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
		super(id, choices, renderer);
	}

	public CRequiredDropDownChoice(String id, IModel<T> model, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
		super(id, model, choices, renderer);
	}

	@Override
	protected void initComp() {
		this.setRequired(true);
		this.setNullValid(true);
	}
}
