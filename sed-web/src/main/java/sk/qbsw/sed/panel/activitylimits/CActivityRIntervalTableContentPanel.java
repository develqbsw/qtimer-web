package sk.qbsw.sed.panel.activitylimits;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /activityRInterval SubPage title: Limity aktivít - Obmedzenia
 * 
 * Panel ActivityRIntervalTableContentPanel obsahuje panely:
 * 
 * - ActivityRIntervalContentPanel - Pridať / Detail / Editovať -
 * ActivityRIntervalTablePanel - Prehľady
 */
public class CActivityRIntervalTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CActivityRIntervalTableContentPanel(String id) {
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

		CActivityRIntervalContentPanel tabPanel = new CActivityRIntervalContentPanel("activityRInterval", tabTitle);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CActivityRIntervalTablePanel tablePanel = new CActivityRIntervalTablePanel("activityRIntervalTable", tabPanel, tabTitle);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel);
	}
}
