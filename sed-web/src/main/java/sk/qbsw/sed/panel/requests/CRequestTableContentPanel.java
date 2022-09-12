package sk.qbsw.sed.panel.requests;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /requests SubPage title: Žiadosti
 * 
 * Panel RequestTableContentPanel obsahuje panely:
 * 
 * - RequestTablePanel - Prehľady - RequestPanel - Pridať / Detail / Editovať
 */
public class CRequestTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Label pageTitleSmall;
	private Label remainingVacation;

	public CRequestTableContentPanel(String id, Label pageTitleSmall, Label remainingVacation) {
		super(id);
		this.pageTitleSmall = pageTitleSmall;
		this.remainingVacation = remainingVacation;
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

		CRequestPanel tabPanel = new CRequestPanel("request", tabTitle, remainingVacation);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CRequestTablePanel tablePanel = new CRequestTablePanel("requestTable", tabPanel, tabTitle, pageTitleSmall);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel);
	}
}
