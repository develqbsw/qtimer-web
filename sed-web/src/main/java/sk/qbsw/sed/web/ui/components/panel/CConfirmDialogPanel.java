package sk.qbsw.sed.web.ui.components.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.panel.Panel;

import sk.qbsw.sed.web.ui.components.IAjaxCommand;

/**
 * Confirm panel for modal dialog
 * 
 * @author farkas.roman
 * @since 2.3.0
 * @version 2.3.0
 */
public class CConfirmDialogPanel extends Panel {
	private static final long serialVersionUID = 1L;

	/** action command to execute external code on OK button click */
	private IAjaxCommand action;

	/**
	 * 
	 * @param id
	 * @param parentWindow paren CModalBorder to hide on CANCEL button action
	 */
	public CConfirmDialogPanel(String id, final CModalBorder parentWindow) {
		super(id);

		add(new AjaxFallbackLink<Void>("btnOk") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				parentWindow.hide(target);
				action.execute(target);
			}
		});

		add(new AjaxFallbackLink<Void>("btnCancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				parentWindow.hide(target);
			}
		});
	}

	/**
	 * sets action which is executed on OK button action
	 * 
	 * @param action
	 */
	public void setAction(IAjaxCommand action) {
		this.action = action;
	}
}
