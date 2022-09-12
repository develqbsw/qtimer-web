package sk.qbsw.sed.panel.timesheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.IHomeOfficePermissionConstants;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.communication.service.IRequestReasonClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.web.ui.CSedSession;

public class CTimesheetForm<T extends CTimeStampRecord> extends CStatelessForm<CTimeStampRecord> {

	/** serial uid */
	private static final long serialVersionUID = 1L;
	
	private static final String ON_CHANGE = "onchange";
	
	private static final String LABEL = "label";

	@SpringBean
	private IRequestReasonClientService requestReasonService;

	@SpringBean
	private IProjectClientService projectService;

	@SpringBean
	private IActivityClientService activityService;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IRequestClientService requestService;

	private boolean isModeDetail = false;

	private WebMarkupContainer activityContainer;
	private WebMarkupContainer projectContainer;
	private WebMarkupContainer showInactiveActivitiesContainer;
	private WebMarkupContainer showInactiveProjectsContainer;
	private WebMarkupContainer requestReasonContainer;
	private WebMarkupContainer outsideContainer;
	private WebMarkupContainer homeOfficeContainer;
	private WebMarkupContainer phaseFieldContainer;
	CDropDownChoice<CCodeListRecord> reasonField;

	private List<CCodeListRecord> sicknessReasonList = null;
	private List<CCodeListRecord> workbreakReasonList = null;

	private Select<CCodeListRecord> project;
	private Select<CCodeListRecord> activity;
	private Boolean workingBefore;
	private boolean projectInactiveFlag = false;
	private boolean activityInactiveFlag = false;
	private AjaxCheckBox showInactiveActivities;

	CheckBox homeOffice;

	CDropDownChoice<CCodeListRecord> userField;

	/**
	 *
	 * @param id             - wicket id
	 * @param timesheetModel - model of form with CTimeSheetRecord
	 * @param actualPanel    - panel that renders after cancel button action
	 */
	public CTimesheetForm(String id, final IModel<CTimeStampRecord> timesheetModel, Panel actualPanel, final boolean isEditMode, List<CCodeListRecord> usersList, List<CCodeListRecord> activityList,
			List<CCodeListRecord> projectList, List<CCodeListRecord> requestReasonList) {
		super(id, timesheetModel);
		setOutputMarkupId(true);

		CTextField<Date> date = new CTextField<Date>("date", EDataType.DATE) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		date.setRequired(true);
		date.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(homeOfficeContainer);
				target.appendJavaScript("Main.runCustomCheck();");
				uncheckHomeOfficeCheckBox();
			}
		});
		add(date);

		CTextField<Date> timeFrom = new CTextField<Date>("timeFrom", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		timeFrom.setRequired(true);
		add(timeFrom);

		CTextField<Date> timeTo = new CTextField<Date>("timeTo", EDataType.TIME) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		add(timeTo);

		workingBefore = getModelObject().getShowProject();

		requestReasonContainer = new WebMarkupContainer("requestReasonContainer") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				Long activityId = activity.getModelObject().getId();
				if (activityId == null) {
					reasonField.getFeedbackMessages().clear();
					return false;
				}

				boolean visible = activityId.equals(-5l) || activityId.equals(-6l);

				if (!visible) {
					// SED-658
					reasonField.getFeedbackMessages().clear();
				}

				return visible;
			}
		};
		requestReasonContainer.setOutputMarkupId(true);
		requestReasonContainer.setOutputMarkupPlaceholderTag(true);
		add(requestReasonContainer);

		reasonField = new CDropDownChoice<CCodeListRecord>("requestReason", requestReasonList) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}

			@Override
			public boolean isRequired() {
				Long activityId = activity.getModelObject().getId();
				return activityId.equals(-5l) || activityId.equals(-6l);
			}

			@Override
			public List<? extends CCodeListRecord> getChoices() {
				Long activityId = activity.getModelObject().getId();

				if (sicknessReasonList == null) {
					try {
						CRequestReasonListsData requestReasonListsData = requestReasonService.getReasonLists(CSedSession.get().getUser().getClientInfo().getClientId());
						sicknessReasonList = requestReasonListsData.getSicknessReasonList();
						workbreakReasonList = requestReasonListsData.getWorkbreakReasonList();
					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					}
				}

				if (activityId.equals(-5l)) {
					return sicknessReasonList;
				} else if (activityId.equals(-6l)) {
					return workbreakReasonList;
				} else {
					return null;
				}
			}
		};
		reasonField.setNullValid(true);
		requestReasonContainer.add(reasonField);

		activityContainer = new WebMarkupContainer("activityContainer");
		activityContainer.setOutputMarkupPlaceholderTag(true);
		add(activityContainer);

		activity = new Select<CCodeListRecord>("activity") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};

		initActivitySelect(activityList);

		activity.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				if (workingBefore != getModelObject().getShowProject()) {
					target.add(projectContainer);
					target.add(showInactiveProjectsContainer);
					target.add(phaseFieldContainer);
					target.add(outsideContainer);
					target.add(homeOfficeContainer);
					target.add(showInactiveActivitiesContainer);

					if (getModelObject().getShowProject()) {
						target.appendJavaScript("Main.runCustomCheck();");
						target.appendJavaScript("Main.runQTimerCheck();$('.search-select-projects').select2({placeholder: '',allowClear: false});");
					} else {
						getModelObject().setPhase(null);
						target.appendJavaScript("Main.runQTimerCheck();");
					}
				}
				workingBefore = getModelObject().getShowProject();

				target.add(requestReasonContainer);
			}
		});
		activity.setRequired(true);
		activityContainer.add(activity);

		userField = new CDropDownChoice<CCodeListRecord>("employee", usersList) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		userField.setRequired(true);
		userField.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(homeOfficeContainer);
				target.appendJavaScript("Main.runCustomCheck();");
				uncheckHomeOfficeCheckBox();
			}
		});
		add(userField);

		projectContainer = new WebMarkupContainer("projectContainer") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return getModelObject().getShowProject();
			}
		};
		projectContainer.setOutputMarkupId(true);
		projectContainer.setOutputMarkupPlaceholderTag(true);
		add(projectContainer);

		project = new Select<CCodeListRecord>("project") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isRequired() {
				return CTimesheetForm.this.getModelObject().getShowProject();
			}

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};

		initProjectSelect(projectList);

		project.setRequired(CSedSession.get().getUser().getClientInfo().getProjectRequired());
		project.setOutputMarkupId(true);

		projectContainer.add(project);

		TextArea<String> noteField = new TextArea<String>("note") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};

		noteField.add(StringValidator.maximumLength(1000));
		add(noteField);

		phaseFieldContainer = new WebMarkupContainer("phaseFieldContainer") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return CTimesheetForm.this.getModelObject().getShowProject();
			}
		};
		phaseFieldContainer.setOutputMarkupId(true);
		phaseFieldContainer.setOutputMarkupPlaceholderTag(true);
		add(phaseFieldContainer);

		CTextField<String> phaseField = new CTextField<String>("phase", EDataType.TEXT) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail && CTimesheetForm.this.getModelObject().getShowProject();
			}
		};
		phaseField.setOutputMarkupId(true);
		phaseField.add(StringValidator.maximumLength(30));
		phaseFieldContainer.add(phaseField);

		showInactiveActivitiesContainer = new WebMarkupContainer("showInactiveActivitiesContainer");
		showInactiveActivitiesContainer.setOutputMarkupId(true);
		add(showInactiveActivitiesContainer);

		showInactiveActivities = new AjaxCheckBox("showInactiveActivities", new PropertyModel<Boolean>(getModel(), "activityInactive")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					activityInactiveFlag = !activityInactiveFlag;
					CTimesheetForm.this.getModelObject().setActivityInactive(activityInactiveFlag);

					List<CCodeListRecord> activityList;
					if (activityInactiveFlag) {
						activityList = activityService.getAllRecords();
					} else {
						activityList = activityService.getValidRecordsForUser(CSedSession.get().getUser().getUserId());

						CCodeListRecord activity = CTimesheetForm.this.getModelObject().getActivity();

						// ak vybrana aktivita bola neplatna tak mam problem lebo mi zmizne z listboxu
						// musim do listboxu nastavit prvu platnu aktivitu rovnakeho typu (bud pracovnu
						// alebo nepracovnu)
						if (activity != null && activity.getType() != null && !activityList.contains(activity)) {
							CCodeListRecord newActivity = null;

							for (CCodeListRecord a : activityList) {
								if (a.getType().equals(activity.getType())) {
									newActivity = a;
									break;
								}
							}
							CTimesheetForm.this.getModelObject().setActivity(newActivity);
						}
					}

					initActivitySelect(activityList);

					target.add(activityContainer);
					target.appendJavaScript("$('.search-select-activities').select2({placeholder: '',allowClear: false});");
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
			}

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};
		showInactiveActivities.setOutputMarkupId(true);
		showInactiveActivitiesContainer.add(showInactiveActivities);

		Label showInactiveActivitiesLabel = new Label("showInactiveActivitiesLabel", getString("timesheet.showInactiveActivities"));
		showInactiveActivitiesLabel.add(AttributeModifier.replace("title", getString("timesheet.showInactiveActivities.tooltip")));
		showInactiveActivitiesContainer.add(showInactiveActivitiesLabel);

		showInactiveProjectsContainer = new WebMarkupContainer("showInactiveProjectsContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return CTimesheetForm.this.getModelObject().getShowProject();
			}
		};

		Label showInactiveProjectsLabel = new Label("showInactiveProjectsLabel", getString("timesheet.showInactiveProjects"));
		showInactiveProjectsLabel.add(AttributeModifier.replace("title", getString("timesheet.showInactiveProjects.tooltip")));
		showInactiveProjectsContainer.add(showInactiveProjectsLabel);

		showInactiveProjectsContainer.setOutputMarkupId(true);
		showInactiveProjectsContainer.setOutputMarkupPlaceholderTag(true);
		add(showInactiveProjectsContainer);

		AjaxCheckBox showInactiveProjects = new AjaxCheckBox("showInactiveProjects", new PropertyModel<Boolean>(getModel(), "projectInactive")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					projectInactiveFlag = !projectInactiveFlag;
					CTimesheetForm.this.getModelObject().setProjectInactive(projectInactiveFlag);

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

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}

		};
		showInactiveProjectsContainer.add(showInactiveProjects);

		outsideContainer = new WebMarkupContainer("outsideContainer") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return CTimesheetForm.this.getModelObject().getShowProject();
			}
		};
		outsideContainer.setOutputMarkupId(true);
		outsideContainer.setOutputMarkupPlaceholderTag(true);
		add(outsideContainer);

		CheckBox outside = new CheckBox("outsideWorkplace", new PropertyModel<Boolean>(getModel(), "outsideWorkplace")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail;
			}
		};

		outsideContainer.add(outside);

		homeOfficeContainer = new WebMarkupContainer("homeOfficeContainer") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return CTimesheetForm.this.getModelObject().getShowProject();
			}
		};
		homeOfficeContainer.setOutputMarkupId(true);
		homeOfficeContainer.setOutputMarkupPlaceholderTag(true);
		add(homeOfficeContainer);

		homeOffice = new CheckBox("homeOffice", new PropertyModel<Boolean>(getModel(), "homeOffice")) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isModeDetail && homeOfficeEnabled(userField.getModel().getObject().getId(), date.getModelObject());
			}
		};

		// SED-856 a - ak je disablovaný home office, odšrtnem ho
		if (!homeOffice.isEnabled()) {
			uncheckHomeOfficeCheckBox();
		}

		homeOfficeContainer.add(homeOffice);

		CTextField<String> status = new CTextField<>("status", EDataType.TEXT);
		status.setEnabled(false);
		add(status);

		CTextField<String> changedBy = new CTextField<>("changedBy", EDataType.TEXT);
		changedBy.setEnabled(false);
		add(changedBy);

		CTextField<Date> changeTime = new CTextField<>("changeTime", EDataType.DATE_TIME);
		changeTime.setEnabled(false);
		add(changeTime);
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
	}

	private void setInactiveProjects() throws CBussinessDataException {
		List<CCodeListRecord> projectList = projectService.getAllRecords();
		project.removeAll();
		initProjectSelect(projectList);
	}

	private void setInactiveActivities() throws CBussinessDataException {
		List<CCodeListRecord> activityList = activityService.getAllRecords();
		initActivitySelect(activityList);
	}

	private void setValidProjects() throws CBussinessDataException {
		List<CCodeListRecord> projectList = projectService.getValidRecordsForUserCached(CSedSession.get().getUser().getUserId());
		project.removeAll();
		initProjectSelect(projectList);
	}

	private void setValidActivities() throws CBussinessDataException {
		List<CCodeListRecord> activityList = activityService.getValidRecordsForUser(CSedSession.get().getUser().getUserId());
		initActivitySelect(activityList);
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
		activity.addOrReplace(myActivitiesLabel);
		activity.addOrReplace(lastUsedActivitiesLabel);
		activity.addOrReplace(allActivitiesLabel);
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

	public void setProjectInactiveFlag(boolean projectInactiveFlag) throws CBussinessDataException {
		if (this.projectInactiveFlag && !projectInactiveFlag) {
			// pred tym bol neplatny a teraz je platny, musim zmenit obsah listboxu
			setValidProjects();
		}
		if (!this.projectInactiveFlag && projectInactiveFlag) {
			// pred tym nebol neplatny a teraz je neplatny, musim zmenit obsah listboxu
			setInactiveProjects();
		}
		this.projectInactiveFlag = projectInactiveFlag;
	}

	public void setActivityInactiveFlag(boolean activityInactiveFlag) throws CBussinessDataException {
		if (this.activityInactiveFlag && !activityInactiveFlag) {
			// pred tym bol neplatny a teraz je platny, musim zmenit obsah listboxu
			setValidActivities();
		}
		if (!this.activityInactiveFlag && activityInactiveFlag) {
			// pred tym nebol neplatny a teraz je neplatny, musim zmenit obsah listboxu
			setInactiveActivities();
		}
		this.activityInactiveFlag = activityInactiveFlag;
	}

	public void setWorkingBefore(Boolean workingBefore) {
		this.workingBefore = workingBefore;
	}

	public Boolean homeOfficeEnabled(Long employeeId, Date date) {

		CUserDetailRecord user = null;

		if (!(IUserTypeCode.ORG_ADMIN.equals(CSedSession.get().getUser().getRoleCode()) && employeeId == null)) {
			try {
				user = userService.getUserDetails(employeeId);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}

			if (user == null) {
				return Boolean.FALSE;
			} else if (user.getHomeOfficePermission().equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
				return Boolean.FALSE;
			} else if (user.getHomeOfficePermission().equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
				try {
					return requestService.isAllowedHomeOfficeForToday(user.getId(), date);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
				}
			} else if (user.getHomeOfficePermission().equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public void uncheckHomeOfficeCheckBox() {
		homeOffice.getModel().setObject(Boolean.FALSE);
	}

	public CDropDownChoice<CCodeListRecord> getUserField() {
		return userField;
	}
}
