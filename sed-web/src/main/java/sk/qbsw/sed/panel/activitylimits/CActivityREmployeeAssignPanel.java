package sk.qbsw.sed.panel.activitylimits;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.toolbar.paging.PagingToolbar;

import sk.qbsw.sed.client.model.restriction.CXUserActivityRestrictionData;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CEditableValidityColumn;
import sk.qbsw.sed.web.grid.datasource.CEmployeeRestrictionDataSource;

/**
 * SubPage: /activityREmployee SubPage title: Limity aktivít - Priradenie
 * 
 * Panel ActivityREmployeeAssignPanel (tabuľka)
 */
public class CActivityREmployeeAssignPanel extends CPanel {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long employeeID;

	// zdroj dát pre tabuľku: public.t_user_activity_restriction
	private CSedDataGrid<IDataSource<CXUserActivityRestrictionData>, CXUserActivityRestrictionData, String> table;

	private CFeedbackPanel errorPanel;

	private CompoundPropertyModel<Long> filterModel;

	private Boolean editMode;

	public CEditableValidityColumn<IDataSource<CXUserActivityRestrictionData>, CXUserActivityRestrictionData, String> col;

	@SuppressWarnings("rawtypes")
	public CSedDataGrid getTable() {
		return table;
	}

	public Long getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(Long entityID) {
		this.employeeID = entityID;
	}

	public void update(Long entityID, AjaxRequestTarget target, String name) {
		this.setEmployeeID(entityID);

		filterModel.setObject(getEmployeeID());
		target.add(table);
		target.add(getFeedbackPanel());
	}

	public void setEditMode(boolean bool) {
		editMode = bool;
		table.update();
	}

	public CActivityREmployeeAssignPanel(String id) {
		super(id);
		this.setOutputMarkupId(true);

		CFeedbackPanel feedbackPanel = new CFeedbackPanel("feedback");
		registerFeedbackPanel(feedbackPanel);

		filterModel = new CompoundPropertyModel<>(0l);
		editMode = false;

		final List<IGridColumn<IDataSource<CXUserActivityRestrictionData>, CXUserActivityRestrictionData, String>> columns = new ArrayList<>();

		columns.add(new PropertyColumn<IDataSource<CXUserActivityRestrictionData>, CXUserActivityRestrictionData, String, String>(new StringResourceModel("table.column.restriction.name", this, null),
				"name").setInitialSize(250));

		col = new CEditableValidityColumn<IDataSource<CXUserActivityRestrictionData>, CXUserActivityRestrictionData, String>("esd", Model.of(getString("table.column.restriction.assigned"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target, IModel<CXUserActivityRestrictionData> rowModel, WebMarkupContainer rowComponent) {
				if (editMode) {
					rowModel.getObject().setAssigned(!rowModel.getObject().getAssigned());
				}
				super.onClick(target, rowModel, rowComponent);
				target.add(rowComponent);
			}
		};
		col.setInitialSize(90);
		columns.add(col);

		table = new CSedDataGrid<>("gridAssign", new CEmployeeRestrictionDataSource(filterModel), columns);
		table.addBottomToolbar(new PagingToolbar<IDataSource<CXUserActivityRestrictionData>, CXUserActivityRestrictionData, String>(table));
		table.setOutputMarkupId(true);
		table.setSelectToEdit(false);
		table.setAllowSelectMultiple(true);
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}
}
