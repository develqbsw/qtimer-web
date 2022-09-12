package sk.qbsw.sed.panel.home;

import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public class CTimerAlertnessWorkButtonItem extends AjaxFallbackButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATE_NORMAL = 0;

	public static final int STATE_STRIKETHROUGH = 1;

	private int state;

	private Label label;

	public CTimerAlertnessWorkButtonItem(String id, Form<?> form) {
		super(id, form);
		setOutputMarkupPlaceholderTag(true);
		label = new Label("alertnessButtonLabel", getString("tooltip.button.alertness_work_start"));
		add(label);

		setStateNormal();
	}

	public void setStateNormal() {
		label.setDefaultModel(Model.of(getString("tooltip.button.alertness_work_start")));
		this.state = STATE_NORMAL;
	}

	public void setStateStrikethrough() {
		label.setDefaultModel(Model.of(getString("tooltip.button.alertness_work_stop")));
		this.state = STATE_STRIKETHROUGH;
	}

	public int getButtonState() {
		return this.state;
	}
}
