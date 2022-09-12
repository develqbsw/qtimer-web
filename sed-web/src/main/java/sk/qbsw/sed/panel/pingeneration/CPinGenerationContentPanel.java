package sk.qbsw.sed.panel.pingeneration;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /pinGeneration SubPage title: Generovanie PIN-u
 * 
 * Panel PinGenerationContentPanel obsahuje panel PinGenerationPanel
 */
public class CPinGenerationContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CPinGenerationContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CPinGenerationPanel tablePanel = new CPinGenerationPanel("pinGenerationPanel");
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
