package sk.qbsw.sed.panel.projects;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /projects SubPage title: Projekty
 * 
 * Panel ProjectTableContentPanel obsahuje panely:
 * 
 * - ProjectContentPanel - Pridať / Detail / Editovať 
 * - ProjectTablePanel - Prehľady
 */
public class CProjectTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CProjectTableContentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				return CStringResourceReader.read("tabTitle.new");
			}
		};
		Label tabTitle = new Label("tabTitle", model);
		tabTitle.setOutputMarkupId(true);
		add(tabTitle);

		CProjectContentPanel tabPanel = new CProjectContentPanel("project", tabTitle);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CProjectTablePanel tablePanel = new CProjectTablePanel("projectTable", tabPanel, tabTitle);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel);
	}
}
