package sk.qbsw.sed.panel.activitylimits;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.toolbar.paging.PagingToolbar;

import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CCheckBoxColumn;
import sk.qbsw.sed.web.grid.datasource.CActivityRGroupDataSource;

/**
 * SubPage: /activityRGroups SubPage title: Limity aktivít - Skupiny obmedzení
 * 
 * Panel ActivityRGroupTablePanel - Prehľady
 */
public class CActivityRGroupTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_restriction_group
	private CSedDataGrid<IDataSource<CGroupsAIData>, CGroupsAIData, String> table;

	private final CActivityRGroupContentPanel tabPanel;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	public CActivityRGroupTablePanel(String id, CActivityRGroupContentPanel tabPanelParam, Label tabTitleParam) {
		super(id);
		setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		final List<IGridColumn<IDataSource<CGroupsAIData>, CGroupsAIData, String>> columns = new ArrayList<>();

		columns.add(new PropertyColumn<IDataSource<CGroupsAIData>, CGroupsAIData, String, String>(
				new StringResourceModel("table.column.activityRestriction.restrictionName", this, null), "name", "name").setInitialSize(300));

		columns.add(new PropertyColumn<IDataSource<CGroupsAIData>, CGroupsAIData, String, String>(
				new StringResourceModel("table.column.activityRestriction.activityType", this, null), "activityName").setInitialSize(200));

		columns.add(new PropertyColumn<IDataSource<CGroupsAIData>, CGroupsAIData, String, String>(
				new StringResourceModel("table.column.activityRestriction.projectGroup", this, null), "projectGroup", "projectGroup").setInitialSize(200));

		columns.add(new CCheckBoxColumn<IDataSource<CGroupsAIData>, CGroupsAIData, String, String>(
				new StringResourceModel("table.column.activityRestriction.valid", this, null), "valid", "valid").setInitialSize(90));

		table = new CSedDataGrid<IDataSource<CGroupsAIData>, CGroupsAIData, String>("grid", new CActivityRGroupDataSource(), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CGroupsAIData> rowModel) {
				// update nadpisu
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.detail"));
				target.add(tabTitle);

				// update formulara
				tabPanel.setModeDetail(true);
				tabPanel.setEntityID(rowModel.getObject().getId());
				tabPanel.clearFeedbackMessages();
				target.add(tabPanel);

				// prepnutie tabu
				target.appendJavaScript("$('#tab-2').click()");
			}
		};
		table.addBottomToolbar(new PagingToolbar<IDataSource<CGroupsAIData>, CGroupsAIData, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}
}
