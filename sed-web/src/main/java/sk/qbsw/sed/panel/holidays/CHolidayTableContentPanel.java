package sk.qbsw.sed.panel.holidays;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /holidays SubPage title: Sviatky
 * 
 * Panel HolidayTableContentPanel obsahuje panely:
 * 
 * - HolidayContentPanel - Pridať / Detail / Editovať - HolidayTablePanel -
 * Prehľady
 */
public class CHolidayTableContentPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CHolidayTableContentPanel(String id) {
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

		CHolidayContentPanel tabPanel = new CHolidayContentPanel("holiday", tabTitle);
		tabPanel.setOutputMarkupId(true);
		add(tabPanel);

		CHolidayTablePanel tablePanel = new CHolidayTablePanel("holidayTable", tabPanel, tabTitle);
		tablePanel.setOutputMarkupId(true);
		add(tablePanel);
		tabPanel.setPanelToRefresh(tablePanel);
	}
}
