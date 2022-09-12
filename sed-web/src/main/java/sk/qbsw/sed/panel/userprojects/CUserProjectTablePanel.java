package sk.qbsw.sed.panel.userprojects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import com.inmethod.grid.toolbar.paging.PagingToolbar;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.brw.CUserProjectRecord;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectBrwFilterCriteria;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.grid.CSedDataGrid;
import sk.qbsw.sed.web.grid.column.editable.CSelectFavoriteColumn;
import sk.qbsw.sed.web.grid.datasource.CUserProjectDataSource;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /userprojects SubPage title: Moje projekty
 * 
 * Panel UserProjectTablePanel (tabuľka projektov)
 */
public class CUserProjectTablePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	// zdroj dát pre tabuľku: public.t_ct_project
	private CSedDataGrid<IDataSource<CUserProjectRecord>, CUserProjectRecord, String> table;

	private CFeedbackPanel errorPanel;

	private final CProjectBrwFilterCriteria projectFilter = new CProjectBrwFilterCriteria();

	private CompoundPropertyModel<CProjectBrwFilterCriteria> projectFilterModel;

	@SpringBean
	private IProjectClientService projectService;

	@SpringBean
	private IUserClientService userService;

	public CUserProjectTablePanel(String id) {
		super(id);
		setOutputMarkupId(true);

		this.projectFilter.setProjectId(ISearchConstants.ALL_ACTIVE);
		this.projectFilterModel = new CompoundPropertyModel<>(projectFilter);

		final List<IGridColumn<IDataSource<CUserProjectRecord>, CUserProjectRecord, String>> columns = new ArrayList<>();

		CSelectFavoriteColumn<IDataSource<CUserProjectRecord>, CUserProjectRecord, String> col = 
				new CSelectFavoriteColumn<IDataSource<CUserProjectRecord>, CUserProjectRecord, String>("esd", Model.of(getString("table.column.userproject.mine"))) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSelect(AjaxRequestTarget target, IModel<CUserProjectRecord> rowModel, WebMarkupContainer rowComponent) {
				try {
					userService.modifyMyProjects(rowModel.getObject().getProjectId(), true, CSedSession.get().getUser().getUserId());
					super.onSelect(target, rowModel, rowComponent);
					target.add(table);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CUserProjectTablePanel.this);
				}
				if (getFeedbackPanel() != null) {
					target.add(getFeedbackPanel());
				}
			}

			@Override
			protected void onDeSelect(AjaxRequestTarget target, IModel<CUserProjectRecord> rowModel, WebMarkupContainer rowComponent) {
				try {
					userService.modifyMyProjects(rowModel.getObject().getProjectId(), false, CSedSession.get().getUser().getUserId());
					super.onSelect(target, rowModel, rowComponent);
					target.add(table);

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CUserProjectTablePanel.this);
				}
				if (getFeedbackPanel() != null) {
					target.add(getFeedbackPanel());
				}
			}

		};
		col.setInitialSize(90);
		columns.add(col);

		PropertyColumn<IDataSource<CUserProjectRecord>, CUserProjectRecord, String, String> columnName = new PropertyColumn<>(new StringResourceModel("table.column.projectName", this, null), "projectName", "name");
		columnName.setInitialSize(400);
		columns.add(columnName);

		table = new CSedDataGrid<>("grid", new CUserProjectDataSource(projectFilterModel), columns);
		table.addBottomToolbar(new PagingToolbar<IDataSource<CUserProjectRecord>, CUserProjectRecord, String>(table));
		add(table);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		final Form<CProjectBrwFilterCriteria> projectFilterForm = new Form<>("filter", projectFilterModel);

		// validity filtration
		CCodeListRecord defaultOptionAllProjects = new CCodeListRecord(ISearchConstants.ALL_ACTIVE, CStringResourceReader.read("userProjectFilter.projects.all"));

		List<CCodeListRecord> flagMyOptions = new ArrayList<>();
		flagMyOptions.add(defaultOptionAllProjects);
		flagMyOptions.add(new CCodeListRecord(ISearchConstants.ALL_MY_PROJECTS, CStringResourceReader.read("userProjectFilter.projects.my")));

		final Model<CCodeListRecord> model = new Model<>();
		model.setObject(defaultOptionAllProjects);

		final CDropDownChoice<CCodeListRecord> flagMyChoice = new CDropDownChoice<>("projectId", model, flagMyOptions);
		flagMyChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				projectFilterModel.getObject().setProjectId(model.getObject().getId());
				target.add(table);
			}
		});
		projectFilterForm.add(flagMyChoice);

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
				target.add(table);
			}
		});
		projectFilterForm.add(projectGroupsChoice);

		// text search filtration
		final TextField<String> search = new TextField<>("search", new Model<String>());
		search.add(new CPlaceholderBehaviour(getString("searchbar.placeholder")));
		search.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				projectFilterModel.getObject().setProjectName(search.getInput());
				target.add(table);
			}
		});
		projectFilterForm.add(search);

		add(projectFilterForm);
	}

	private String getProjectGroup(CCodeListRecord projectGroup) {
		Long value = projectGroup.getId();
		if (value == null) {
			return ISearchConstants.ALL.toString();
		}

		// test na volbu "Vsetky zaznamy"?
		if (!ISearchConstants.ALL.equals(value)) {
			// obabreme to, ze skupina projektu ma len temporarne id (aby sme mohli pouzivat funkcionalitu listboxu)
			// a vratime skutocny nazov skupiny, ktoru pouzijeme na filtrovanie
			return (String) projectGroup.getName();
		} else {
			// v pripade, ze ide o "vsetky" vraciame kod volby - ako string
			return ISearchConstants.ALL.toString();
		}
	}
}