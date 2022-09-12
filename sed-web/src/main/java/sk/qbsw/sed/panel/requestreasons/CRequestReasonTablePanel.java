package sk.qbsw.sed.panel.requestreasons;

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

import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CCheckBoxColumn;
import sk.qbsw.sed.web.grid.datasource.CRequestReasonDataSource;

/**
 * SubPage: /requestReasons SubPage title: Dôvody pre žiadosti
 * 
 * RequestReasonTablePanel - Prehľady
 */
public class CRequestReasonTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_request_reason
	private CSedDataGrid<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String> table;

	private final CRequestReasonContentPanel tabPanel;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	public CRequestReasonTablePanel(String id, CRequestReasonContentPanel tabPanelParam, Label tabTitleParam) {
		super(id);
		setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		final List<IGridColumn<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String>> columns = new ArrayList<>();

		columns.add(new PropertyColumn<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String, String>(
				new StringResourceModel("table.column.requestReasonType", this, null), "requestTypeDescription", "requestType").setInitialSize(200));

		columns.add(new PropertyColumn<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String, String>(
				new StringResourceModel("table.column.requestReasonCode", this, null), "code", "code").setInitialSize(200));

		columns.add(new PropertyColumn<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String, String>(
				new StringResourceModel("table.column.requestReasonName", this, null), "reasonName", "reasonName").setInitialSize(300));

		columns.add(new CCheckBoxColumn<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String, String>(
				new StringResourceModel("table.column.requestReasonValid", this, null), "valid", "valid").setInitialSize(100));

		columns.add(new CCheckBoxColumn<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String, String>(
				new StringResourceModel("table.column.requestReasonSystemFlag", this, null), "system", "system").setInitialSize(100));
		
		table = new CSedDataGrid<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String>("grid", new CRequestReasonDataSource(), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CRequestReasonRecord> rowModel) {
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
		table.addBottomToolbar(new PagingToolbar<IDataSource<CRequestReasonRecord>, CRequestReasonRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}
}
