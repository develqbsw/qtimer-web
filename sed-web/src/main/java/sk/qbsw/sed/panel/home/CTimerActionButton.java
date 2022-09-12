package sk.qbsw.sed.panel.home;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public abstract class CTimerActionButton extends AjaxFallbackLink<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATE_STOPPED = 0;

	public static final int STATE_STARTED = 1;
	
	private static final String CLASS = "class";

	private int state;

	private Label label;

	public CTimerActionButton(String id) {
		super(id);
		label = new Label("actionButtonLabel", getString("tooltip.button.work_start"));
		label.setEscapeModelStrings(false);
		add(label);
	}

	public int getButtonState() {
		return this.state;
	}

	public void setStateStarted() {
		setEnabled(true);
		label.setDefaultModel(Model.of(getString("tooltip.button.work_start")));
		this.state = STATE_STARTED;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-green"));
	}

	public void setStateStopped() {
		setEnabled(true);
		label.setDefaultModel(Model.of(getString("tooltip.button.work_stop")));
		this.state = STATE_STOPPED;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-red"));
	}

	public void setStateDisabled() {
		setEnabled(false);
		this.state = STATE_STARTED;
		add(AttributeModifier.replace(CLASS, "q-timer-button btn-q-timer-disabled"));
	}
}
