package sk.qbsw.sed.panel.useractivities;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /useractivities SubPage title: Moje aktivity
 * 
 * Panel UserProjectTableContentPanel obsahuje panel
 * UserActivityTableContentPanel
 */
public class CUserActivityTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CUserActivityTableContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CUserActivityTablePanel tablePanel = new CUserActivityTablePanel("userActivityTable");
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
