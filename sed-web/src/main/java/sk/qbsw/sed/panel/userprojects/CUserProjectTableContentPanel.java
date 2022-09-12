package sk.qbsw.sed.panel.userprojects;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /userprojects SubPage title: Moje projekty
 * 
 * Panel UserProjectTableContentPanel obsahuje panel UserProjectTablePanel
 */
public class CUserProjectTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CUserProjectTableContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CUserProjectTablePanel tablePanel = new CUserProjectTablePanel("userProjectTable");
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
