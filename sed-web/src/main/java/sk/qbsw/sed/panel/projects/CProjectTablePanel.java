package sk.qbsw.sed.panel.projects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectBrwFilterCriteria;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.userprojects.CUserProjectTablePanel;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.CCheckBoxColumn;
import sk.qbsw.sed.web.grid.datasource.CProjectDataSource;
import sk.qbsw.sed.web.grid.toolbar.CPagingToolbar;

/**
 * SubPage: /projects SubPage title: Projekty
 * 
 * Panel ProjectTablePanel - Prehľady
 */
public class CProjectTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_ct_project
	private CSedDataGrid<IDataSource<CProjectRecord>, CProjectRecord, String> table;

	private final CProjectContentPanel tabPanel;

	private final Label tabTitle;

	private CFeedbackPanel errorPanel;

	private final CProjectBrwFilterCriteria projectFilter = new CProjectBrwFilterCriteria();

	private CompoundPropertyModel<CProjectBrwFilterCriteria> projectFilterModel;

	@SpringBean
	private IProjectClientService projectService;

	public CProjectTablePanel(String id, CProjectContentPanel tabPanelParam, Label tabTitleParam) {
		super(id);
		setOutputMarkupId(true);

		this.tabPanel = tabPanelParam;
		this.tabTitle = tabTitleParam;

		this.projectFilter.setProjectId(ISearchConstants.ALL_ACTIVE);
		this.projectFilterModel = new CompoundPropertyModel<>(projectFilter);

		final List<IGridColumn<IDataSource<CProjectRecord>, CProjectRecord, String>> columns = new ArrayList<>();

		PropertyColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnID = new PropertyColumn<>(new StringResourceModel("table.column.projectId", this, null), "id", "id");
		columnID.setInitialSize(100);
		columns.add(columnID);

		PropertyColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnOrder = new PropertyColumn<>(new StringResourceModel("table.column.projectOrder", this, null), "order", "order");
		columnOrder.setInitialSize(100);
		columns.add(columnOrder);

		PropertyColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnName = new PropertyColumn<>(new StringResourceModel("table.column.projectName", this, null), "name", "name");
		columnName.setInitialSize(400);
		columns.add(columnName);
		
		PropertyColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnEviproCode = new PropertyColumn<>(new StringResourceModel("table.column.projectCode", this, null), "eviproCode", "eviproCode");
		columnEviproCode.setInitialSize(120);
		columns.add(columnEviproCode);

		CCheckBoxColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnValid = new CCheckBoxColumn<>(new StringResourceModel("table.column.projectValid", this, null), "active", "valid");
		columnValid.setInitialSize(100);
		columns.add(columnValid);

		PropertyColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnChangeTime = new PropertyColumn<>(new StringResourceModel("table.column.projectChangeTime", this, null), "changeTime", "changeTime");
		columnChangeTime.setInitialSize(120);
		columns.add(columnChangeTime);

		CCheckBoxColumn<IDataSource<CProjectRecord>, CProjectRecord, String, String> columnFlagDefault = new CCheckBoxColumn<>(new StringResourceModel("table.column.projectDefault", this, null), "flagDefault", "flagDefault");
		columnFlagDefault.setInitialSize(110);
		columns.add(columnFlagDefault);

		table = new CSedDataGrid<IDataSource<CProjectRecord>, CProjectRecord, String>("grid", new CProjectDataSource(projectFilterModel), columns) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDoubleRowClicked(AjaxRequestTarget target, IModel<CProjectRecord> rowModel) {
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
		table.addBottomToolbar(new CPagingToolbar<IDataSource<CProjectRecord>, CProjectRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CProjectBrwFilterCriteria> projectFilterForm = new Form<>("filter", projectFilterModel);

		// validity filtration
		CCodeListRecord defaultOptionValid = new CCodeListRecord(ISearchConstants.ALL_ACTIVE, CStringResourceReader.read("projectFilter.validity.option.valid"));

		List<CCodeListRecord> validityOptions = new ArrayList<>();
		validityOptions.add(new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("projectFilter.validity.option.all")));
		validityOptions.add(defaultOptionValid);
		validityOptions.add(new CCodeListRecord(ISearchConstants.ALL_NOT_ACTIVE, CStringResourceReader.read("projectFilter.validity.option.invalid")));

		final Model<CCodeListRecord> validityModel = new Model<>();
		validityModel.setObject(defaultOptionValid);

		final CDropDownChoice<CCodeListRecord> validityChoice = new CDropDownChoice<>("projectId", validityModel, validityOptions);
		validityChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				projectFilterModel.getObject().setProjectId(validityModel.getObject().getId());
				table.setFirstPage();
				target.add(table);
			}
		});
		projectFilterForm.add(validityChoice);

		CCodeListRecord defaultOptionGroup = new CCodeListRecord(ISearchConstants.ALL, CStringResourceReader.read("projectFilter.group.option.all"));
		List<CCodeListRecord> projectGroupsOptions = new ArrayList<>();
		projectGroupsOptions.add(defaultOptionGroup);
		try {
			projectGroupsOptions.addAll(projectService.getAllRecordsWithGroups().getProjectGroups());
		} catch (CBussinessDataException e) {
			Logger.getLogger(CUserProjectTablePanel.class).error(e);
			throw new CSystemFailureException(e);
		}

		final Model<CCodeListRecord> projectGroupsModel = new Model<>();
		projectGroupsModel.setObject(defaultOptionGroup);

		final CDropDownChoice<CCodeListRecord> projectGroupsChoice = new CDropDownChoice<>("projectGroup", projectGroupsModel, projectGroupsOptions);
		projectGroupsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				projectFilterModel.getObject().setProjectGroup(getProjectGroup(projectGroupsModel.getObject()));
				table.setFirstPage();
				target.add(table);
			}
		});
		projectFilterForm.add(projectGroupsChoice);

		// text search filtration
		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				projectFilterModel.getObject().setProjectName(search.getInput());
				table.setFirstPage();
				target.add(table);
			}
		});
		projectFilterForm.add(search);
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));

		add(projectFilterForm);
	}

	private String getProjectGroup(CCodeListRecord projectGroup) {
		Long value = projectGroup.getId();
		if (value == null) {
			return ISearchConstants.ALL.toString();
		}

		// test na volbu "Vsetky zaznamy"?
		if (!ISearchConstants.ALL.equals(value)) {
			// obabreme to, ze skupina projektu ma len temporarne id (aby sme mohli pouzivat funkcionalitu listboxu) a vratime skutocny nazov skupiny, ktoru pouzijeme na filtrovanie
			return (String) projectGroup.getName();
		} else {
			// v pripade, ze ide o "vsetky" vraciame kod volby - ako string
			return ISearchConstants.ALL.toString();
		}
	}
}
