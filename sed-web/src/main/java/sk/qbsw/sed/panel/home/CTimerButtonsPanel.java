package sk.qbsw.sed.panel.home;

import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * TimerButtonsPanel - pre tlačidlá:
 * 
 * začiatok práce / koniec práce, prestávka obed / koniec prestávky, mimo
 * pracoviska / koniec mimo
 */
public class CTimerButtonsPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private CTimerActionButton actionButton;
	private CTimerBreakButton breakButton;
	private CTimerWorkOutsideButton workOutsideButton;

	public CTimerButtonsPanel(String id, final CTimerPanel timerPanel) {
		super(id);
		setOutputMarkupId(true);

		Application.get().getMarkupSettings().setDefaultBeforeDisabledLink(null);

		actionButton = new CTimerActionButton("actionButton") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				timerPanel.executeActionButton(target);
			}
		};
		actionButton.add(new RemoveCssActiveBehavior());
		add(actionButton);

		breakButton = new CTimerBreakButton("breakButton") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				timerPanel.executeBreakButton(target);
			}
		};
		breakButton.add(new RemoveCssActiveBehavior());
		add(breakButton);

		workOutsideButton = new CTimerWorkOutsideButton("workOutsideButton") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				timerPanel.executeWorkOutsideButton(target);
			}
		};
		workOutsideButton.add(new RemoveCssActiveBehavior());
		add(workOutsideButton);
	}

	public CTimerActionButton getActionButton() {
		return actionButton;
	}

	public CTimerBreakButton getBreakButton() {
		return breakButton;
	}

	public CTimerWorkOutsideButton getWorkOutsideButton() {
		return workOutsideButton;
	}

	private class RemoveCssActiveBehavior extends AjaxEventBehavior {

		private static final long serialVersionUID = 1L;

		public RemoveCssActiveBehavior() {
			super("onmouseup");
		}

		@Override
		protected void onEvent(AjaxRequestTarget target) {
			target.appendJavaScript("document.activeElement.blur();");
		}
	}
}
