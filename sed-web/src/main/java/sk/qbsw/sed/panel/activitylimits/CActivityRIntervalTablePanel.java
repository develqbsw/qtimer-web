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

import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CCheckBoxColumn;
import sk.qbsw.sed.web.grid.column.CDayTypeColumn;
import sk.qbsw.sed.web.grid.column.CTimeColumn;
import sk.qbsw.sed.web.grid.datasource.CActivityRestrictionDataSource;

/**
 * SubPage: /activityRInterval SubPage title: Limity aktivít - Obmedzenia
 * 
 * Panel ActivityRIntervalTablePanel - Prehľady
 */
public class CActivityRIntervalTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_activity_interval
	private CSedDataGrid<IDataSource<CActivityIntervalData>, CActivityIntervalData, String> table;

	private final CActivityRIntervalContentPanel tabPanel;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	public CActivityRIntervalTablePanel(String id, CActivityRIntervalContentPanel tabPanelParam, Label tabTitleParam) {
		super(id);
		setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		final List<IGridColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String>> columns = new ArrayList<>();

		columns.add(new PropertyColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.restrictionName", this, null), "name", "name").setInitialSize(300));

		columns.add(new PropertyColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.activityType", this, null), "activityName").setInitialSize(150));

		columns.add(new CTimeColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.timeFrom", this, null), "timeFrom", "time_from").setInitialSize(80));

		columns.add(new CTimeColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.timeTo", this, null), "timeTo", "time_to").setInitialSize(80));

		columns.add(new CDayTypeColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.dayType", this, null), "dayTypeId", "dateType").setInitialSize(100));

		columns.add(new PropertyColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.groupName", this, null), "groupName").setInitialSize(200));

		columns.add(new CCheckBoxColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.valid", this, null), "valid", "valid").setInitialSize(90));

		columns.add(new PropertyColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.dateFrom", this, null), "dateValidFrom", "date_from").setInitialSize(100));

		columns.add(new PropertyColumn<IDataSource<CActivityIntervalData>, CActivityIntervalData, String, String>(
				new StringResourceModel("table.column.activityRestriction.dateTo", this, null), "dateValidTo", "date_to").setInitialSize(100));

		table = new CSedDataGrid<IDataSource<CActivityIntervalData>, CActivityIntervalData, String>("grid", new CActivityRestrictionDataSource(), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CActivityIntervalData> rowModel) {
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
		table.addBottomToolbar(new PagingToolbar<IDataSource<CActivityIntervalData>, CActivityIntervalData, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}
}
