package sk.qbsw.sed.panel.activitylimits;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.toolbar.paging.PagingToolbar;

import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CXUserActivityRestrictionData;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.communication.service.IActivityRestrictionClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CEditSubmitCancelColumn;
import sk.qbsw.sed.web.grid.datasource.CEmployeeDataSource;

/**
 * SubPage: /activityREmployee SubPage title: Limity aktivít - Priradenie
 * 
 * Panel ActivityREmployeeTablePanel (tabuľka)
 */
public class CActivityREmployeeTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_user
	private CSedDataGrid<IDataSource<CEmployeeRecord>, CEmployeeRecord, String> table;

	private CFeedbackPanel errorPanel;

	private final CActivityREmployeeAssignPanel assignPanel;

	private CompoundPropertyModel<String> employeeFilterModel;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IActivityRestrictionClientService restrictionService;

	public CActivityREmployeeTablePanel(String id, final CActivityREmployeeAssignPanel assignPanell) {
		super(id);
		setOutputMarkupId(true);

		this.assignPanel = assignPanell;

		this.employeeFilterModel = new CompoundPropertyModel<>(new String());

		final List<IGridColumn<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>> columns = new ArrayList<>();

		CEditSubmitCancelColumn<IDataSource<CEmployeeRecord>, CEmployeeRecord, String> editColumn = new CEditSubmitCancelColumn<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>("esd",
				Model.of(getString("table.column.actions"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEdit(AjaxRequestTarget target, IModel<CEmployeeRecord> rowModel, WebMarkupContainer rowComponent) {
				assignPanel.update(rowModel.getObject().getUserId(), target, rowModel.getObject().getName());
				assignPanel.setEditMode(true);
				super.onEdit(target, rowModel, rowComponent);
			}

			@Override
			protected void onSubmitted(AjaxRequestTarget target, IModel<CEmployeeRecord> rowModel, WebMarkupContainer rowComponent) {
				assignPanel.setEditMode(false);

				CEmployeeActivityLimitsData data = new CEmployeeActivityLimitsData();
				data.setEmployeeId(assignPanel.getEmployeeID());
				@SuppressWarnings("unchecked")
				List<IModel<CXUserActivityRestrictionData>> list = assignPanel.getTable().getRowModels();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getObject().getAssigned() == true) {
						data.getEmployeeAssignedLimits().add(list.get(i).getObject().convert());
					} else if (list.get(i).getObject().getAssigned() == false) {
						data.getEmployeeNotAssignedLimits().add(list.get(i).getObject().convert());
					}
				}

				try {
					restrictionService.saveEmployeeLimits(data);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CActivityREmployeeTablePanel.this);
				}

				target.add(assignPanel.getTable());
				super.onSubmitted(target, rowModel, rowComponent);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target, IModel<CEmployeeRecord> rowModel, WebMarkupContainer rowComponent) {
				assignPanel.setEditMode(false);
				target.add(assignPanel.getTable());
				super.onCancel(target, rowModel, rowComponent);
			}
		};
		editColumn.setInitialSize(65);
		columns.add(editColumn);

		columns.add(new PropertyColumn<IDataSource<CEmployeeRecord>, CEmployeeRecord, String, String>(
				new StringResourceModel("table.column.employee.name", this, null), "name", "surname").setInitialSize(350));

		table = new CSedDataGrid<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>("grid", new CEmployeeDataSource(employeeFilterModel), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onRowClicked(AjaxRequestTarget target, IModel<CEmployeeRecord> rowModel) {
				super.onRowClicked(target, rowModel);
				assignPanel.update(rowModel.getObject().getUserId(), target, rowModel.getObject().getName());

			}
		};

		table.addBottomToolbar(new PagingToolbar<IDataSource<CEmployeeRecord>, CEmployeeRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		// text search filtration
		TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				employeeFilterModel.setObject(getComponent().getDefaultModelObjectAsString());
				target.add(table);
			}
		});
		add(search);
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
	}
}
