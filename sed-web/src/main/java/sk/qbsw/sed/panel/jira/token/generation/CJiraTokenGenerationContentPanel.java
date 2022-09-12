package sk.qbsw.sed.panel.jira.token.generation;

import sk.qbsw.sed.fw.panel.CPanel;

/**
 * SubPage: /jiraTokenGeneration SubPage title: Generovanie JIRA tokenu
 * 
 * Panel CJiraTokenGenerationContentPanel obsahuje panel CJiraTokenGenerationPanel
 */
public class CJiraTokenGenerationContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1841787690186955660L;

	public CJiraTokenGenerationContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CJiraTokenGenerationPanel tablePanel = new CJiraTokenGenerationPanel("jiraTokenGenerationPanel");
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
