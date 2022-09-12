package sk.qbsw.sed.panel.home;

import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;

public class CTimerWorkOutsideButtonItem extends AjaxFallbackButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CTimerWorkOutsideButtonItem(String id, Form<?> form) {
		super(id, form);
		setOutputMarkupPlaceholderTag(true);
		Label label = new Label("workOutsideButtonLabel", getString("tooltip.button.work_external"));
		add(label);
	}
}
