package sk.qbsw.sed.panel.notifyOfApprovedRequest;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /notifyOfApprovedRequest SubPage title: Notifikácie o schválenej
 * žiadosti
 * 
 * Panel RequestTableContentPanel obsahuje panely:
 * 
 * - notifyOfApprovedRequestTablePanel - Prehľady - notifyOfApprovedRequestPanel
 * - Pridať / Detail / Editovať
 */

public class CNotifyOfApprovedRequestTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Label pageTitleSmall;
	private Label remainingVacation;

	public CNotifyOfApprovedRequestTableContentPanel(String id, Label pageTitleSmall, Label remainingVacation) {
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

		CNotifyOfApprovedRequestPanel tabPanel = new CNotifyOfApprovedRequestPanel("notify", tabTitle);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CNotifyOfApprovedRequestTablePanel tablePanel = new CNotifyOfApprovedRequestTablePanel("notifyTable", tabPanel, tabTitle);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
	}
}
