package sk.qbsw.sed.panel.presenceemail;

import org.apache.wicket.markup.html.basic.Label;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /presenceEmail SubPage title: Email o dochádzke
 * 
 * Panel PresenceEmailPanel obsahuje panel PresenceEmailTablePanel
 */
public class CPresenceEmailPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private Label pageTitleSmall;

	public CPresenceEmailPanel(String id, Label pageTitleSmall) {
		super(id);
		this.pageTitleSmall = pageTitleSmall;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CPresenceEmailTablePanel tablePanel = new CPresenceEmailTablePanel("presenceEmailTable", pageTitleSmall);
		tablePanel.registerFeedbackPanel(this.getFeedbackPanel());
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
