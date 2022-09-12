package sk.qbsw.sed.panel.activitylimits;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /activityRGroups SubPage title: Limity aktivít - Skupiny obmedzení
 * 
 * Panel ActivityRGroupTableContentPanel obsahuje panely:
 * 
 * - ActivityRGroupContentPanel - Pridať / Detail / Editovať 
 * - ActivityRGroupTablePanel - Prehľady
 */
public class CActivityRGroupTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CActivityRGroupTableContentPanel(String id) {
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

		CActivityRGroupContentPanel tabPanel = new CActivityRGroupContentPanel("activityRGroup", tabTitle);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CActivityRGroupTablePanel tablePanel = new CActivityRGroupTablePanel("activityRGroupTable", tabPanel, tabTitle);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel);
	}
}
