package sk.qbsw.sed.panel.timestampGenerate.tab;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.IHomeOfficePermissionConstants;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.component.validator.CStringMinutesValidator;
import sk.qbsw.sed.fw.component.validator.CStringPercentValidator;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.panel.timestampGenerate.editable.CTimestampGenerateEditableTablePanel;
import sk.qbsw.sed.web.ui.CSedSession;

public class CTimestampGenerateForm<T extends CTmpTimeSheet> extends CStatelessForm<CTmpTimeSheet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String LABEL = "label";
	
	private static final String CLASS = "class";

	@SpringBean
	private IProjectClientService projectService;

	@SpringBean
	private IRequestClientService requestService;

	private WebMarkupContainer projectContainer;
	private WebMarkupContainer activityContainer;
	private WebMarkupContainer phaseFieldContainer;
	private Select<CCodeListRecord> project;
	private Select<CCodeListRecord> activity;
	private CTextField<String> durationField;
	private CTextField<String> percentField;

	private WebMarkupContainer percentContainer;

	private WebMarkupContainer durationContainer;

	private final CTimestampGenerateEditableTablePanel panelToRefreshAfterSumit;

	private boolean projectInactiveFlag = false;

	public CTimestampGenerateForm(String id, final IModel<CTmpTimeSheet> formModel, List<CCodeListRecord> activityList, 
			List<CCodeListRecord> projectList, final CTimestampGenerateEditableTablePanel panelToRefreshAfterSumit) {
		super(id, formModel);
		setOutputMarkupId(true);

		this.panelToRefreshAfterSumit = panelToRefreshAfterSumit;

		// employee
		CLoggedUserRecord r = CSedSession.get().getUser();
		IModel<String> userNameModel = new Model<>(r.getName() + " " + r.getSurname());
		CTextField<String> userField = new CTextField<String>("employee", userNameModel, EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return false;
			}
		};
		add(userField);

		// project
		projectContainer = new WebMarkupContainer("projectContainer");
		projectContainer.setOutputMarkupId(true);
		projectContainer.setOutputMarkupPlaceholderTag(true);
		add(projectContainer);

		project = new Select<>("project");
		initProjectSelect(projectList);
		project.setRequired(CSedSession.get().getUser().getClientInfo().getProjectRequired());
		project.setOutputMarkupId(true);

		projectContainer.add(project);

		activityContainer = new WebMarkupContainer("activityContainer");
		activityContainer.setOutputMarkupId(true);
		activityContainer.setOutputMarkupPlaceholderTag(true);
		add(activityContainer);

		activity = new Select<>("activity");
		initActivitySelect(activityList);
		activity.setRequired(CSedSession.get().getUser().getClientInfo().getActivityRequired());
		activity.setOutputMarkupId(true);

		activityContainer.add(activity);

		// note
		final TextArea<String> noteField = new TextArea<>("note");
		noteField.add(StringValidator.maximumLength(1000));
		add(noteField);

		phaseFieldContainer = new WebMarkupContainer("phaseFieldContainer");
		phaseFieldContainer.setOutputMarkupId(true);
		phaseFieldContainer.setOutputMarkupPlaceholderTag(true);
		add(phaseFieldContainer);

		CTextField<String> phaseField = new CTextField<>("phase", EDataType.TEXT);
		phaseField.add(StringValidator.maximumLength(30));
		phaseField.setOutputMarkupId(true);
		phaseFieldContainer.add(phaseField);

		CheckBox showInactiveActivities = new CheckBox("showInactiveActivities", new PropertyModel<Boolean>(getModel(), "activityInactive"));
		add(showInactiveActivities);

		Label showInactiveActivitiesLabel = new Label("showInactiveActivitiesLabel", getString("timesheet.showInactiveActivities"));
		showInactiveActivitiesLabel.add(AttributeModifier.replace("title", getString("timesheet.showInactiveActivities.tooltip")));
		add(showInactiveActivitiesLabel);

		Label showInactiveProjectsLabel = new Label("showInactiveProjectsLabel", getString("timesheet.showInactiveProjects"));
		showInactiveProjectsLabel.add(AttributeModifier.replace("title", getString("timesheet.showInactiveProjects.tooltip")));
		add(showInactiveProjectsLabel);

		AjaxCheckBox showInactiveProjects = new AjaxCheckBox("showInactiveProjects", new PropertyModel<Boolean>(getModel(), "projectInactive")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					projectInactiveFlag = !projectInactiveFlag;
					CTimestampGenerateForm.this.getModelObject().setProjectInactive(projectInactiveFlag);

					List<CCodeListRecord> projectList;
					
					if (projectInactiveFlag) {
						projectList = projectService.getAllRecords();
					} else {
						projectList = projectService.getValidRecordsForUserCached(CSedSession.get().getUser().getUserId());
					}

					project.removeAll();
					initProjectSelect(projectList);

					target.add(projectContainer);
					target.appendJavaScript("$('.search-select-projects').select2({placeholder: '',allowClear: false});");
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
			}
		};
		add(showInactiveProjects);

		WebMarkupContainer outsideContainer = new WebMarkupContainer("outsideContainer");
		outsideContainer.setOutputMarkupId(true);
		outsideContainer.setOutputMarkupPlaceholderTag(true);
		add(outsideContainer);

		CheckBox outside = new CheckBox("outside", new PropertyModel<Boolean>(getModel(), "outside"));
		outsideContainer.add(outside);

		WebMarkupContainer homeOfficeContainer = new WebMarkupContainer("homeOfficeContainer");
		homeOfficeContainer.setOutputMarkupId(true);
		homeOfficeContainer.setOutputMarkupPlaceholderTag(true);
		add(homeOfficeContainer);

		CheckBox homeOffice = new CheckBox("homeOffice", new PropertyModel<Boolean>(getModel(), "homeOffice")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				Date filterDateFrom = panelToRefreshAfterSumit.getFilterModel().getObject().getDateFrom();
				Date filterDateTo = panelToRefreshAfterSumit.getFilterModel().getObject().getDateTo();
				CLoggedUserRecord userDetailRecord = CSedSession.get().getUser();
				return enableHomeOffice(userDetailRecord, filterDateFrom, filterDateTo);
			}
		};

		homeOffice.setModelObject(Boolean.FALSE);
		homeOfficeContainer.add(homeOffice);

		durationField = new CTextField<String>("durationInMinutes", EDataType.TIME) {
			@Override
			public IConverter getConverter(Class type) {

				return new CStringToMinutesConverter();
			}

			@Override
			protected String getModelValue() {
				if (this.getModelObject() == null)
					return "";
				else {
					Long retVal = panelToRefreshAfterSumit.getTotalTime() > 0 ? (panelToRefreshAfterSumit.getTotalTime() - panelToRefreshAfterSumit.getSumOfDurationInMinutesFromTable()) : 0;

					if (retVal < 0) {
						return "00:00";
					} else {
						return CDateUtils.getMinutesAsString(retVal);
					}
				}
			}
		};
		durationField.add(new CStringMinutesValidator<>());
		durationField.setOutputMarkupId(true);

		durationContainer = new WebMarkupContainer("durationInMinutesContainer") {
			@Override
			public boolean isVisible() {
				return panelToRefreshAfterSumit.usingMinutes();
			}
		};
		durationContainer.add(durationField);
		durationContainer.setOutputMarkupId(true);
		durationContainer.setOutputMarkupPlaceholderTag(true);
		add(durationContainer);

		percentField = new CTextField<String>("durationInPercent", EDataType.TEXT) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected String getModelValue() {
				if (this.getModelObject() == null)
					return "";
				else {
					Long retVal = panelToRefreshAfterSumit.getTotalTime() > 0 ? 100 - panelToRefreshAfterSumit.getSumOfDurationInPercentFromTable() : 0;

					if (retVal < 0l) {
						return "0";
					} else {
						return retVal.toString();
					}
				}
			}
		};
		percentField.add(new CStringPercentValidator<>());
		percentField.setOutputMarkupId(true);

		percentContainer = new WebMarkupContainer("durationInPercentContainer") {
			public boolean isVisible() {
				return panelToRefreshAfterSumit.usingPercent();
			}
		};
		percentContainer.add(percentField);
		percentContainer.setOutputMarkupId(true);
		percentContainer.setOutputMarkupPlaceholderTag(true);
		add(percentContainer);
	}

	private void setProjectsLists(List<CCodeListRecord> projectList, List<CCodeListRecord> myProjectsList, List<CCodeListRecord> allProjectsList, List<CCodeListRecord> lastUsedProjectsList) {

		boolean myProjectsSwitch = false;
		boolean lastUsedProjectsSwitch = false;

		for (CCodeListRecord project : projectList) {

			if ((project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_MY))) {
				myProjectsSwitch = true;
				lastUsedProjectsSwitch = false;
				continue;
			} else if ((project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_LAST_USED))) {
				myProjectsSwitch = false;
				lastUsedProjectsSwitch = true;
				continue;
			} else if ((project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_ALL_OTHER))) {
				myProjectsSwitch = false;
				lastUsedProjectsSwitch = false;
				continue;
			}

			if (myProjectsSwitch) {
				myProjectsList.add(project);
			} else if (lastUsedProjectsSwitch) {
				lastUsedProjectsList.add(project);
			} else {
				allProjectsList.add(project);
			}

		}

	}

	private void initProjectSelect(List<CCodeListRecord> projectList) {

		List<CCodeListRecord> myProjectsList = new ArrayList<>();
		List<CCodeListRecord> lastUsedProjectsList = new ArrayList<>();
		List<CCodeListRecord> allProjectsList = new ArrayList<>();

		setProjectsLists(projectList, myProjectsList, allProjectsList, lastUsedProjectsList);

		SelectOptions<CCodeListRecord> myProjects = new SelectOptions<>("myProject", myProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedProjects = new SelectOptions<>("lastUsedProject", lastUsedProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allProjects = new SelectOptions<>("allProject", allProjectsList, new CSelectRecordRenderer());

		WebMarkupContainer myProjectsLabel = new WebMarkupContainer("myProjectsLabel");
		myProjectsLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.project.all_my_projects"))));

		WebMarkupContainer lastUsedProjectsLabel = new WebMarkupContainer("lastUsedProjectsLabel");
		lastUsedProjectsLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.project.last_used"))));

		WebMarkupContainer allProjectsLabel = new WebMarkupContainer("allProjectsLabel");
		allProjectsLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.project.all_projects"))));

		myProjectsLabel.add(myProjects);
		lastUsedProjectsLabel.add(lastUsedProjects);
		allProjectsLabel.add(allProjects);
		project.add(myProjectsLabel);
		project.add(lastUsedProjectsLabel);
		project.add(allProjectsLabel);

	}

	private void initActivitySelect(List<CCodeListRecord> activityList) {
		List<CCodeListRecord> myActivitiesList = new ArrayList<>();
		List<CCodeListRecord> lastUsedActivitiesList = new ArrayList<>();
		List<CCodeListRecord> allActivitiesList = new ArrayList<>();

		setActivitiesList(activityList, myActivitiesList, allActivitiesList, lastUsedActivitiesList);

		SelectOptions<CCodeListRecord> myActivities = new SelectOptions<>("myActivity", myActivitiesList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedActivities = new SelectOptions<>("lastUsedActivity", lastUsedActivitiesList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allActivities = new SelectOptions<>("allActivity", allActivitiesList, new CSelectRecordRenderer());

		WebMarkupContainer myActivitiesLabel = new WebMarkupContainer("myActivitiesLabel");
		myActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.all_my_activities"))));

		WebMarkupContainer lastUsedActivitiesLabel = new WebMarkupContainer("lastUsedActivitiesLabel");
		lastUsedActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.last_used"))));

		WebMarkupContainer allActivitiesLabel = new WebMarkupContainer("allActivitiesLabel");
		allActivitiesLabel.add(new AttributeAppender(LABEL, new Model<String>(getString("label.group.activity.all_activities"))));

		myActivitiesLabel.add(myActivities);
		lastUsedActivitiesLabel.add(lastUsedActivities);
		allActivitiesLabel.add(allActivities);
		activity.add(myActivitiesLabel);
		activity.add(lastUsedActivitiesLabel);
		activity.add(allActivitiesLabel);
	}

	private void setActivitiesList(List<CCodeListRecord> activityList, List<CCodeListRecord> myActivitiesList, List<CCodeListRecord> allActivitiesList, List<CCodeListRecord> lastUsedActivitiesList) {

		boolean myActivitiesSwitch = false;
		boolean lastUsedActivitiesSwitch = false;

		for (CCodeListRecord activity : activityList) {

			if ((activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_MY))) {
				myActivitiesSwitch = true;
				lastUsedActivitiesSwitch = false;
				continue;
			} else if ((activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_LAST_USED))) {
				myActivitiesSwitch = false;
				lastUsedActivitiesSwitch = true;
				continue;
			} else if ((activity.getId() != null && activity.getId().equals(ISearchConstants.ACTIVITY_GROUP_ALL_OTHER))) {
				myActivitiesSwitch = false;
				lastUsedActivitiesSwitch = false;
				continue;
			}

			if (myActivitiesSwitch) {
				myActivitiesList.add(activity);
			} else if (lastUsedActivitiesSwitch) {
				lastUsedActivitiesList.add(activity);
			} else {
				allActivitiesList.add(activity);
			}
		}
	}

	public void setValidProjects() throws CBussinessDataException {
		List<CCodeListRecord> projectList = projectService.getValidRecordsForUserCached(CSedSession.get().getUser().getUserId());
		project.removeAll();
		initProjectSelect(projectList);
	}

	public void clearValidationMessages() {
		durationField.getFeedbackMessages().clear();
		percentField.getFeedbackMessages().clear();

		percentContainer.add(new AttributeModifier(CLASS, ""));
		durationContainer.add(new AttributeModifier(CLASS, ""));
	}

	public void addRedBorderToDurationField(AjaxRequestTarget target) {

		if (panelToRefreshAfterSumit.usingPercent()) {
			percentContainer.add(new AttributeModifier(CLASS, "has-error"));
			target.add(percentContainer);
		} else if (panelToRefreshAfterSumit.usingMinutes()) {
			durationContainer.add(new AttributeModifier(CLASS, "has-error"));
			target.add(durationContainer);
		}
	}

	public boolean isProjectInactiveFlag() {
		return projectInactiveFlag;
	}

	public void setProjectInactiveFlag(boolean projectInactiveFlag) {
		this.projectInactiveFlag = projectInactiveFlag;
	}

	public CTextField<String> getPercentField() {
		return percentField;
	}

	public CTextField<String> getDurationField() {
		return durationField;
	}

	public boolean enableHomeOffice(CLoggedUserRecord loggedUser, Date dateFrom, Date dateTo) {

		if ((loggedUser.getHomeOfficePermission()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
			return false;
		} else if ((loggedUser.getHomeOfficePermission()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
			try {
				return requestService.isAllowedHomeOfficeInInterval(loggedUser.getUserId(), loggedUser.getClientInfo().getClientId(), dateFrom, dateTo);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				error(getString(e.getModel().getServerCode()));
			}
		} else if ((loggedUser.getHomeOfficePermission()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
			return true;
		}
		return false;
	}
}
