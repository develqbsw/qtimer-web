package sk.qbsw.sed.panel.requestreasons;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /requestReasons SubPage title: Dôvody pre žiadosti
 * 
 * Panel RequestReasonTableContentPanel obsahuje panely:
 * 
 * - RequestReasonContentPanel - Pridať / Detail / Editovať 
 * - RequestReasonTablePanel - Prehľady
 */
public class CRequestReasonTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CRequestReasonTableContentPanel(String id) {
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

		CRequestReasonContentPanel tabPanel = new CRequestReasonContentPanel("requestReason", tabTitle);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CRequestReasonTablePanel tablePanel = new CRequestReasonTablePanel("requestReasonTable", tabPanel, tabTitle);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel);
	}
}
