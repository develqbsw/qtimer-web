package sk.qbsw.sed.panel.passwordchange;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /passwordChangeReception SubPage title: Zmena hesla - recepcia
 * 
 * Panel PasswordChangeContentPanel obsahuje panel PasswordChangePanel
 */
public class CPasswordChangeContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;

	private final String login;

	public final boolean isReceptionPassword;

	public CPasswordChangeContentPanel(String id, String name, String login, boolean isReceptionPassword) {
		super(id);
		this.name = name;
		this.login = login;
		this.isReceptionPassword = isReceptionPassword;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CPasswordChangePanel tablePanel = new CPasswordChangePanel("passwordChangePanel", name, login, isReceptionPassword);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
