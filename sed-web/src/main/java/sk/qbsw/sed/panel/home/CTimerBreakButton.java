package sk.qbsw.sed.panel.home;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public abstract class CTimerBreakButton extends AjaxFallbackLink<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATE_NORMAL = 0;

	public static final int STATE_STRIKETHROUGH = 1;
	
	private static final String CLASS = "class";

	private int state;

	private Label label;

	public CTimerBreakButton(String id) {
		super(id);
		label = new Label("breakButtonLabel", getString("tooltip.button.break_start"));
		label.setEscapeModelStrings(false);
		add(label);

		setStateNormal();
	}

	public void setStateNormal() {
		setEnabled(true);
		label.setDefaultModel(Model.of(getString("tooltip.button.break_start")));
		this.state = STATE_NORMAL;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-yellow"));
	}

	public void setStateStrikethrough() {
		setEnabled(true);
		label.setDefaultModel(Model.of(getString("tooltip.button.break_stop")));
		this.state = STATE_STRIKETHROUGH;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-yellow"));
	}

	public int getButtonState() {
		return this.state;
	}

	public void setStateDisabled() {
		setEnabled(false);
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-q-timer-disabled"));
	}
}
