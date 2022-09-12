package sk.qbsw.sed.panel.home;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public abstract class CTimerWorkOutsideButton extends AjaxFallbackLink<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATE_NORMAL = 0;

	public static final int STATE_STRIKETHROUGH = 1;
	
	private static final String CLASS = "class";

	private int state;

	private Label label;

	public CTimerWorkOutsideButton(String id) {
		super(id);
		label = new Label("workOutsideButtonLabel", getString("tooltip.button.work_external_start"));
		label.setEscapeModelStrings(false);
		add(label);

		setStateNormal();
	}

	public void setStateNormal() {
		setEnabled(true);
		label.setDefaultModel(Model.of(getString("tooltip.button.work_external_start")));
		this.state = STATE_NORMAL;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-azure"));
	}

	public void setStateStrikethrough() {
		setEnabled(true);
		label.setDefaultModel(Model.of(getString("tooltip.button.work_external_stop")));
		this.state = STATE_STRIKETHROUGH;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-azure"));
	}

	public int getButtonState() {
		return this.state;
	}

	public void setStateDisabled() {
		setEnabled(false);
		this.state = STATE_NORMAL;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-q-timer-disabled"));
	}
}
