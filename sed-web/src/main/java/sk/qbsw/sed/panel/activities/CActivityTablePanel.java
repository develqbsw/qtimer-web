package sk.qbsw.sed.panel.activities;

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

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CCheckBoxColumn;
import sk.qbsw.sed.web.grid.column.CTimeColumn;
import sk.qbsw.sed.web.grid.datasource.CActivityDataSource;

/**
 * SubPage: /activities SubPage title: Aktivity
 * 
 * ActivityTablePanel - Prehľady
 */
public class CActivityTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_ct_activity
	private CSedDataGrid<IDataSource<CActivityRecord>, CActivityRecord, String> table;

	private final CActivityContentPanel tabPanel;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	public CActivityTablePanel(String id, CActivityContentPanel tabPanelParam, Label tabTitleParam) {
		super(id);
		setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		final List<IGridColumn<IDataSource<CActivityRecord>, CActivityRecord, String>> columns = new ArrayList<>();

		PropertyColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String> columnID = new PropertyColumn<>(new StringResourceModel("table.column.activityId", this, null), "id", "id");
		columnID.setInitialSize(100);
		columns.add(columnID);

		PropertyColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String> columnOrder = 
				new PropertyColumn<>(new StringResourceModel("table.column.activityOrder", this, null), "order", "order");
		columnOrder.setInitialSize(100);
		columns.add(columnOrder);

		PropertyColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String> columnName = 
				new PropertyColumn<>(new StringResourceModel("table.column.activityName", this, null), "name", "name");
		columnName.setInitialSize(200);
		columns.add(columnName);

		columns.add(new CTimeColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.timeMin", this, null), "timeMin", "timeMin").setInitialSize(100));
		columns.add(new CTimeColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.timeMax", this, null), "timeMax", "timeMax").setInitialSize(100));

		PropertyColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String> columnHoursMax = 
				new PropertyColumn<>(new StringResourceModel("table.column.hoursMax", this, null), "hoursMax", "hoursMax");
		columnHoursMax.setInitialSize(150);
		columns.add(columnHoursMax);

		columns.add(new CCheckBoxColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.flagExport", this, null), "flagExport", "flagExport"));
		columns.add(new CCheckBoxColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.flagSum", this, null), "flagSum", "flagSum").setInitialSize(180));

		columns.add(new CCheckBoxColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.activityActive", this, null), "active", "valid"));
		columns.add(new PropertyColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.activityChangeTime", this, null), "changeTime", "changeTime"));
		columns.add(new CCheckBoxColumn<IDataSource<CActivityRecord>, CActivityRecord, String, String>(new StringResourceModel("table.column.activityFlagDefault", this, null), "flagDefault", "flagDefault"));

		table = new CSedDataGrid<IDataSource<CActivityRecord>, CActivityRecord, String>("grid", new CActivityDataSource(), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CActivityRecord> rowModel) {
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
		table.addBottomToolbar(new PagingToolbar<IDataSource<CActivityRecord>, CActivityRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}
}
