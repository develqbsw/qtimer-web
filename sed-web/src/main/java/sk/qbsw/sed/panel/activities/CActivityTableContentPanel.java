package sk.qbsw.sed.panel.activities;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /activities SubPage title: Aktivity
 * 
 * Panel ActivityTableContentPanel obsahuje panely:
 * 
 * - ActivityContentPanel - Pridať / Detail / Editovať 
 * - ActivityTablePanel - Prehľady
 */
public class CActivityTableContentPanel extends CPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CActivityTableContentPanel(String id) {
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

	CActivityContentPanel tabPanel = new CActivityContentPanel("activity", tabTitle);
	tabPanel.setOutputMarkupId(true);
	add(tabPanel);

	CActivityTablePanel tablePanel = new CActivityTablePanel("activityTable", tabPanel, tabTitle);
	tablePanel.setOutputMarkupId(true);
	add(tablePanel);
	tabPanel.setPanelToRefresh(tablePanel);
    }
}
