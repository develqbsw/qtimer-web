package sk.qbsw.sed.panel.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.IHomeOfficePermissionConstants;
import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.component.renderer.CSelectRecordRenderer;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CDisableEnterKeyBehavior;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CAjaxOverlayLinkListener;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.page.home.CHomePage;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 * 
 * Panel TimerPanel - Q-Timer
 */
public class CTimerPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;
	
	private static final String CLASS = "class";
	
	private static final String ON_CHANGE = "onchange";

	private static final String LABEL = "label";
	
	@SpringBean
	private IActivityClientService activityService;

	@SpringBean
	private IProjectClientService projectService;

	@SpringBean
	private ITimesheetClientService timesheetService;

	@SpringBean
	private IRequestClientService requestService;

	private final CLoggedUserRecord loggedUser;

	private Form<CTimeStampAddRecord> form = null;

	private AjaxFallbackButton changeButton = null;
	private CTimerActionButtonItem actionButton = null;
	private AjaxFallbackButton newButton = null;
	private CTimerAlertnessWorkButtonItem alertnessButton = null;
	private CTimerHomeOfficeButtonItem homeOfficeButton = null;
	private CTimerInteractiveWorkButtonItem interactiveWorkButton = null;
	private CTimerWorkOutsideButtonItem workOutsideButton = null;
	private AjaxFallbackLink<Object> refreshButton = null;

	private Boolean allowedAlertnessWork = Boolean.FALSE;
	private Boolean allowedHomeOfficeWork = Boolean.FALSE;

	private CompoundPropertyModel<CTimeStampAddRecord> timesheetModel;

	private CTimerButtonsPanel timerButtonsPanel;

	private Long timeStampId;
	private CTextField<Date> time;
	private AjaxSelfUpdatingTimerBehavior updateTimeBehavior;

	// tento flag je kvoli tomu ze po akcii sa vykona onchange na poli time. Neviem preco.
	private boolean actionClicked = false;

	/**
	 * create new timer panel
	 */
	public CTimerPanel(String id, CFeedbackPanel errorPanel) {
		super(id);
		loggedUser = CSedSession.get().getUser();

		registerFeedbackPanel(errorPanel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		this.allowedAlertnessWork = loggedUser.isAllowedAlertnessWork();

		List<CCodeListRecord> activityList = null;
		List<CCodeListRecord> projectList = null;
		List<CCodeListRecord> myProjectsList = new ArrayList<>();
		List<CCodeListRecord> lastUsedProjectsList = new ArrayList<>();
		List<CCodeListRecord> allProjectsList = new ArrayList<>();
		CPredefinedInteligentTimeStamp predefinedValues = null;

		try {

			if (loggedUser.getHomeOfficePermission().equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
				this.allowedHomeOfficeWork = Boolean.FALSE;
			} else if (loggedUser.getHomeOfficePermission().equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
				this.allowedHomeOfficeWork = requestService.isAllowedHomeOfficeForToday(loggedUser.getUserId(), Calendar.getInstance().getTime());
			} else if (loggedUser.getHomeOfficePermission().equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
				this.allowedHomeOfficeWork = Boolean.TRUE;
			} else {
				this.allowedHomeOfficeWork = Boolean.FALSE;
			}

			// ano, dalo by sa nacitat rovno pracovne, ale racej si zoberiem vsetky
			// aktivity, tie sa cache-uju
			activityList = getWorkingActivities(activityService.getValidRecordsForUser(loggedUser.getUserId()));
			projectList = projectService.getValidRecordsForUserCached(loggedUser.getUserId());
			setProjectsLists(projectList, myProjectsList, allProjectsList, lastUsedProjectsList);

			predefinedValues = timesheetService.loadPredefinedInteligentValueForUserTimerPanel(loggedUser.getUserId(), new Date());
			timesheetModel = new CompoundPropertyModel<>(predefinedValues.getModel());
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		form = new Form<>("timesheetForm", timesheetModel);
		form.add(AttributeModifier.append("name", "timesheetForm"));
		add(form);

		time = new CTextField<>("time", EDataType.TIME);
		time.setRequired(true);
		updateTimeBehavior = new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)) {

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			protected void onPostProcessTarget(AjaxRequestTarget target) {

				super.onPostProcessTarget(target);
				Date now = new Date();
				int hours = now.getHours();
				int minutes = now.getMinutes();
				int seconds = now.getSeconds();

				if (seconds != 0 || getUpdateInterval().getMilliseconds() != 60 * 1000) {
					setUpdateInterval(Duration.seconds(60 - seconds));
					target.add(refreshButton);
				}

				Date oldTime = time.getModelObject();
				int hoursOld = oldTime.getHours();
				int minutesOld = oldTime.getMinutes();

				if (hoursOld < hours || (hoursOld == hours && minutesOld < minutes)) {
					String minutesStr = minutes + "";

					if (minutes < 10) {
						minutesStr = "0" + minutesStr;
					}

					String hoursStr = hours + "";

					if (hours < 10) {
						hoursStr = "0" + hoursStr;
					}

					target.appendJavaScript("document.getElementsByName('time')[0].value = '" + hoursStr + ":" + minutesStr + "'");
					time.setModelObject(now);
				}
			}
		};
		time.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				if (actionClicked) {
					actionClicked = false;
				} else {
					updateTimeBehavior.stop(null);
				}

			}
		});
		time.add(new AjaxFormComponentUpdatingBehavior("onclick") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				updateTimeBehavior.stop(null);
			}
		});

		form.add(time);

		Select<CCodeListRecord> activity = new CActivitySelect("activity", activityList);
		activity.setRequired(CTimerPanel.this.loggedUser.getClientInfo().getActivityRequired());
		activity.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				onUpdateTimer(target);
			}
		});
		form.add(activity);

		SelectOptions<CCodeListRecord> myProjects = new SelectOptions<>("myProject", myProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> lastUsedProjects = new SelectOptions<>("lastUsedProject", lastUsedProjectsList, new CSelectRecordRenderer());
		SelectOptions<CCodeListRecord> allProjects = new SelectOptions<>("allProject", allProjectsList, new CSelectRecordRenderer());

		Select<CCodeListRecord> project = new Select<>("project");

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

		project.setRequired(CTimerPanel.this.loggedUser.getClientInfo().getProjectRequired());
		project.add(new AjaxFormComponentUpdatingBehavior(ON_CHANGE) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				onUpdateTimer(target);
			}
		});
		form.add(project);

		TextField<String> noteField = new TextField<>("timesheetNote", new PropertyModel<String>(timesheetModel, "note"));
		noteField.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				onUpdateTimer(target);
			}
		});
		noteField.add(new CDisableEnterKeyBehavior());
		form.add(noteField);

		TextField<String> phaseField = new TextField<>("timesheetPhase", new PropertyModel<String>(timesheetModel, "phase"));
		phaseField.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				onUpdateTimer(target);
			}
		});
		phaseField.add(new CDisableEnterKeyBehavior());
		phaseField.add(StringValidator.maximumLength(30));
		form.add(phaseField);

		newButton = new AjaxFallbackButton("newButton", form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new CAjaxOverlayLinkListener(form));
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form2) {
				try {
					// SED-854 - na obrazovke neviem zistiť, či bola posledná ČZ home office alebo nie tak pošlem null ,
					// doplní sa v metóde CTimesheetBaseServiceImpl.startWorking
					startActivity(null, !workOutsideButton.isVisible(), null);
					refreshAll(target);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		newButton.setOutputMarkupPlaceholderTag(true);
		newButton.setVisible(false);
		form.add(newButton);

		changeButton = new AjaxFallbackButton("changeButton", form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new CAjaxOverlayLinkListener(form));
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form2) {
				try {
					modifyActivity(!workOutsideButton.isVisible());
					refreshAll(target);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		changeButton.setOutputMarkupPlaceholderTag(true);
		changeButton.setVisible(false);
		form.add(changeButton);

		actionButton = new CTimerActionButtonItem("actionButton", form) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new CAjaxOverlayLinkListener(form));
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				executeActionButton(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		actionButton.setVisible(false);
		form.add(actionButton);

		homeOfficeButton = new CTimerHomeOfficeButtonItem("homeOfficeButton", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form2) {
				executeHomeOfficeButton(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		homeOfficeButton.setVisible(this.allowedHomeOfficeWork);
		form.add(homeOfficeButton);

		alertnessButton = new CTimerAlertnessWorkButtonItem("alertnessButton", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form2) {
				try {
					switch (alertnessButton.getButtonState()) {
					case CTimerAlertnessWorkButtonItem.STATE_NORMAL:
						startActivity(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_WORK_ALERTNESS), null, null);
						break;
					case CTimerAlertnessWorkButtonItem.STATE_STRIKETHROUGH:
						stopActivity(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_WORK_ALERTNESS));
						break;
					}
					refreshAll(target);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		alertnessButton.setVisible(allowedAlertnessWork);
		form.add(alertnessButton);

		interactiveWorkButton = new CTimerInteractiveWorkButtonItem("interactiveWorkButton", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form2) {
				try {
					switch (interactiveWorkButton.getButtonState()) {
					case CTimerInteractiveWorkButtonItem.STATE_NORMAL:
						startActivity(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_WORK_INTERACTIVE), null, null);
						break;
					case CTimerInteractiveWorkButtonItem.STATE_STRIKETHROUGH:
						stopActivity(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_WORK_INTERACTIVE));
						break;
					}
					refreshAll(target);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		interactiveWorkButton.setVisible(allowedAlertnessWork);
		form.add(interactiveWorkButton);

		workOutsideButton = new CTimerWorkOutsideButtonItem("workOutsideButton", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new CAjaxOverlayLinkListener(form));
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				executeWorkOutsideButton(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}

		};
		workOutsideButton.setVisible(false);
		form.add(workOutsideButton);

		refreshButton = new AjaxFallbackLink<Object>("refreshButton") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					refreshAll(target);
					if (target != null) {
						target.appendJavaScript("refresh($('#timerPanel_div'));");
					}
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
				}
			}

		};
		refreshButton.add(updateTimeBehavior);
		form.add(refreshButton);
		
		// skontrolujem, či používateľ má JIRA access token
		checkJiraToken();

		setPredefinedValues(predefinedValues);
	}

	@SuppressWarnings("deprecation")
	private void startActivity(final Long activityId, final Boolean outside, final Boolean homeOffice) throws CBussinessDataException {
		Date currentTime = new Date();
		CTimeStampAddRecord addRecord = this.getModel(activityId);
		Date time = addRecord.getTime();
		if (time == null) {
			time = currentTime;
		}
		time.setDate(currentTime.getDate());
		time.setMonth(currentTime.getMonth());
		time.setYear(currentTime.getYear());
		addRecord.setTime(time);

		if (null != activityId) {
			// ide o prestavku, ci inu nepracovnu aktivitu
			addRecord.setHomeOffice(Boolean.FALSE);
			addRecord.setOutside(Boolean.FALSE);
			Long message = timesheetService.startNonWorking(addRecord);
			
			// ak sa vrátila 0, tak vypíšem informatívnu hlášku, že Čas bol posunutý
			if (Long.valueOf(0).equals(message)) {
				getFeedbackPanel().success(CStringResourceReader.read("timesheet.time.shifted"));
			}
			
		} else {
			// ide o pracu
			addRecord.setHomeOffice(homeOffice);
			addRecord.setOutside(outside);
			timesheetService.startWorking(addRecord);
		}
	}

	private void modifyActivity(final Boolean outside) throws CBussinessDataException {
		CTimeStampAddRecord addRecord = this.getModel(null);

		addRecord.setOutside(outside);
		timesheetService.modifyWorking(timeStampId, addRecord);
	}

	@SuppressWarnings("deprecation")
	private void stopActivity(final Long activityId) throws CBussinessDataException {
		Date currentTime = new Date();
		CTimeStampAddRecord addRecord = this.getModel(activityId);
		Date time = addRecord.getTime();
		if (time == null) {
			time = currentTime;
		}
		time.setDate(currentTime.getDate());
		time.setMonth(currentTime.getMonth());
		time.setYear(currentTime.getYear());
		addRecord.setTime(time);

		if (null != activityId) {
			if (activityId.intValue() == CPredefinedInteligentTimeStamp.ACTIVITY_WORK_INTERACTIVE) {
				// zastavit zasah
				timesheetService.stopInteractiveWork(addRecord);
			} else if (activityId.intValue() == CPredefinedInteligentTimeStamp.ACTIVITY_WORK_ALERTNESS) {
				// zastavit pohotovost
				timesheetService.stopNonWorking(addRecord, false);
			} else {
				// zastavit prestavku
				timesheetService.stopNonWorking(addRecord);
			}
		} else {
			// ide o pracu
			timesheetService.stopWorking(addRecord);
		}
	}

	private CTimeStampAddRecord getModel(final Long activityId) {
		final CTimeStampAddRecord model = form.getModelObject();

		// common
		model.setEmployeeId(loggedUser.getUserId());

		// non working
		model.setNonWorkingActivityId(activityId);

		return model;
	}

	private void refreshAll(AjaxRequestTarget target) throws CBussinessDataException {
		this.setPredefinedValues(timesheetService.loadPredefinedInteligentValueForUserTimerPanel(loggedUser.getUserId(), new Date()));
		if (target != null) {
			target.add(form);
			target.add(timerButtonsPanel);
			target.add(getFeedbackPanel());
			target.appendJavaScript("FormElements.init();SedApp.init();SVExamples.init();Index.init();");
			updateTimeBehavior.restart(null);
			actionClicked = true;
		} else {
			setResponsePage(CHomePage.class);
		}
	}

	private void setPredefinedValues(CPredefinedInteligentTimeStamp predefinedValues) {
		this.timeStampId = predefinedValues.getTimeStampId();

		final CTimeStampAddRecord model = predefinedValues.getModel();
		final int mode = predefinedValues.getMode();

		switch (mode) {
		case CPredefinedInteligentTimeStamp.MODE_NOTHING:
			this.setModeNonExisting(model);
			break;
		case CPredefinedInteligentTimeStamp.MODE_INVALID_PROJECT_OR_ACTIVITY:
			this.processInvalidProjectOrActivity(model, predefinedValues);
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORK_STARTED:
			this.setModeWorkStarted(model);
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORK_FINISHED:
		case CPredefinedInteligentTimeStamp.MODE_WORKBREAK_FINISHED:
			this.setModeWorkFinished(model);
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORKBREAK_STARTED:
			this.setModeWorkBreakStarted(model);
			break;
		}

		timesheetModel.setObject(model);

		if (null != predefinedValues.getUnclosedDate()) {
			getFeedbackPanel().info(getString("timesheet.previous_day_close_incorrect") + CDateUtils.formatDate(predefinedValues.getUnclosedDate(), CDateUtils.DATE_FORMAT_DD_MM_YYYY));
		}
	}

	private void setModeNonExisting(final CTimeStampAddRecord model) {
		// work button
		this.timerButtonsPanel.getActionButton().setStateStarted();
		this.timerButtonsPanel.getActionButton().setVisible(Boolean.TRUE);

		// --- non work activities ---
		this.timerButtonsPanel.getBreakButton().setStateNormal();
		this.timerButtonsPanel.getBreakButton().setVisible(Boolean.FALSE);

		// SED-822a
		if (CTimerPanel.this.allowedAlertnessWork) {
			this.alertnessButton.setStateNormal();
			this.alertnessButton.setVisible(Boolean.TRUE);

			this.interactiveWorkButton.setStateNormal();
			this.interactiveWorkButton.setVisible(Boolean.TRUE);
		} else {
			this.alertnessButton.setStateNormal();
			this.alertnessButton.setVisible(Boolean.FALSE);

			this.interactiveWorkButton.setStateNormal();
			this.interactiveWorkButton.setVisible(Boolean.FALSE);
		}

		this.timerButtonsPanel.getWorkOutsideButton().setStateNormal();
		this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.TRUE);

		this.changeButton.setVisible(false);
		this.newButton.setVisible(false);
		this.actionButton.setVisible(false);
		this.workOutsideButton.setVisible(false);
		this.homeOfficeButton.setVisible(this.allowedHomeOfficeWork);

		this.changeAllWithoutButtons(Boolean.FALSE);
	}

	private void changeAllWithoutButtons(final Boolean disable) {
		this.form.setEnabled(!disable);
	}

	private void processInvalidProjectOrActivity(final CTimeStampAddRecord model, final CPredefinedInteligentTimeStamp preparedValues) {
		switch (preparedValues.getOriginalMode()) {
		case CPredefinedInteligentTimeStamp.MODE_NOTHING:
			setModeNonExisting(model);
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORK_STARTED:
			setModeWorkStarted(model);
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORK_FINISHED:
		case CPredefinedInteligentTimeStamp.MODE_WORKBREAK_FINISHED:
			setModeWorkFinished(model);
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORKBREAK_STARTED:
			setModeWorkBreakStarted(model);
			break;
		}

		getFeedbackPanel().info(getString("notes.select_project_activity"));
	}

	private void setModeWorkStarted(final CTimeStampAddRecord model) {
		if (model.getOutside() || model.getHomeOffice()) {
			this.form.add(AttributeModifier.replace(CLASS, "panel panel-work-outside panel-calendar"));

			if (model.getOutside()) {
				this.timerButtonsPanel.getWorkOutsideButton().setStateStrikethrough();
				this.homeOfficeButton.setStateNormal();
			} else if (model.getHomeOffice()) {
				this.homeOfficeButton.setStateStrikethrough();
				this.timerButtonsPanel.getWorkOutsideButton().setStateNormal();
			}

			this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.TRUE);
			this.homeOfficeButton.setVisible(this.allowedHomeOfficeWork);

			// work button
			timerButtonsPanel.getActionButton().setStateStarted();
			timerButtonsPanel.getActionButton().setVisible(Boolean.TRUE);
		} else {
			this.form.add(AttributeModifier.replace(CLASS, "panel panel-in-work panel-calendar"));

			this.timerButtonsPanel.getWorkOutsideButton().setStateNormal();
			this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.TRUE);

			this.homeOfficeButton.setStateNormal();
			this.homeOfficeButton.setVisible(this.allowedHomeOfficeWork);

			// work button
			timerButtonsPanel.getActionButton().setStateStopped();
			timerButtonsPanel.getActionButton().setVisible(Boolean.TRUE);
		}

		// -- non work buttons --
		this.timerButtonsPanel.getBreakButton().setStateNormal();
		this.timerButtonsPanel.getBreakButton().setVisible(Boolean.TRUE);

		if (CTimerPanel.this.allowedAlertnessWork) {
			this.alertnessButton.setStateNormal();
			this.alertnessButton.setVisible(Boolean.TRUE);
			// SED-290
			this.interactiveWorkButton.setStateNormal();
			this.interactiveWorkButton.setVisible(Boolean.TRUE);
		} else {
			this.alertnessButton.setStateNormal();
			this.alertnessButton.setVisible(Boolean.FALSE);
			// SED-290
			this.interactiveWorkButton.setStateNormal();
			this.interactiveWorkButton.setVisible(Boolean.FALSE);
		}

		this.changeButton.setVisible(false);
		this.newButton.setVisible(false);
		this.actionButton.setVisible(false);
		this.workOutsideButton.setVisible(false);

		this.changeAllWithoutButtons(Boolean.FALSE);
	}

	private void setModeWorkFinished(final CTimeStampAddRecord model) {
		this.form.add(AttributeModifier.replace(CLASS, "panel panel-not-in-work panel-calendar"));

		// work button
		timerButtonsPanel.getActionButton().setStateStarted();

		// -- non work buttons
		this.timerButtonsPanel.getBreakButton().setVisible(Boolean.FALSE);

		if (CTimerPanel.this.allowedAlertnessWork) {
			this.alertnessButton.setVisible(Boolean.TRUE);

			this.alertnessButton.setStateNormal();
			this.interactiveWorkButton.setStateNormal();
		}

		this.timerButtonsPanel.getWorkOutsideButton().setStateNormal();
		this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.TRUE);

		this.homeOfficeButton.setStateNormal();
		this.homeOfficeButton.setVisible(this.allowedHomeOfficeWork);

		this.changeButton.setVisible(false);
		this.newButton.setVisible(false);
		this.actionButton.setVisible(false);
		this.workOutsideButton.setVisible(false);

		this.changeAllWithoutButtons(Boolean.FALSE);
	}

	private void setModeChanged() {
		Boolean outside = form.getModelObject().getOutside();
		Boolean homeOffice = form.getModelObject().getHomeOffice();

		// SED-503
		if ((outside != null && outside)) {
			// ak som MIMO PRACOVISKA a zmenim nieco ma byt "praca mimo pracoviska - zaciatok" disable a "praca-zaciatok" enable.
			actionButton.setVisible(true);
			this.homeOfficeButton.setVisible(this.allowedHomeOfficeWork);

		} else if ((homeOffice != null && homeOffice)) {
			actionButton.setVisible(true);
			workOutsideButton.setVisible(true);
			this.homeOfficeButton.setVisible(false);
		} else {
			// ak som V PRACI (na pracovisku) a zmenim nieco ma byt "praca mimo pracoviska - zaciatok" enable a "praca-zaciatok" disable.
			workOutsideButton.setVisible(true);
			this.homeOfficeButton.setVisible(this.allowedHomeOfficeWork);
		}
		if (timerButtonsPanel.getBreakButton().getButtonState() == 0) {
			this.changeButton.setVisible(true);
		}

		this.newButton.setVisible(true);

		// work button
		this.timerButtonsPanel.getActionButton().setStateDisabled();

		// non work buttons --
		this.timerButtonsPanel.getBreakButton().setStateDisabled();
		this.alertnessButton.setVisible(Boolean.FALSE);
		this.interactiveWorkButton.setVisible(Boolean.FALSE);

		this.timerButtonsPanel.getWorkOutsideButton().setStateDisabled();

	}

	private void setModeWorkBreakStarted(final CTimeStampAddRecord model) {
		this.form.add(AttributeModifier.replace(CLASS, "panel panel-yellow panel-calendar"));

		switch (model.getNonWorkingActivityId().intValue()) {
		case CPredefinedInteligentTimeStamp.ACTIVITY_BREAK:
			timerButtonsPanel.getActionButton().setStateStopped();
			timerButtonsPanel.getActionButton().setVisible(Boolean.FALSE);

			this.timerButtonsPanel.getBreakButton().setVisible(Boolean.TRUE);
			this.timerButtonsPanel.getBreakButton().setStateStrikethrough();

			this.alertnessButton.setVisible(Boolean.FALSE);
			this.interactiveWorkButton.setVisible(Boolean.FALSE);
			this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.FALSE);
			break;
		case CPredefinedInteligentTimeStamp.ACTIVITY_WORK_ALERTNESS:
			if (CTimerPanel.this.allowedAlertnessWork) {
				// opened alertness work - allow to close one
				this.alertnessButton.setVisible(Boolean.TRUE);
				this.alertnessButton.setStateStrikethrough();

				// and allow to open interactive work
				this.interactiveWorkButton.setVisible(Boolean.TRUE);
				this.interactiveWorkButton.setStateNormal();

				this.timerButtonsPanel.getActionButton().setVisible(Boolean.TRUE);
				this.timerButtonsPanel.getActionButton().setStateDisabled();
			} else {
				this.alertnessButton.setVisible(Boolean.FALSE);
				this.interactiveWorkButton.setVisible(Boolean.FALSE);
			}

			this.timerButtonsPanel.getBreakButton().setVisible(Boolean.FALSE);
			this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.FALSE);
			this.timerButtonsPanel.getWorkOutsideButton().setStateNormal();
			this.homeOfficeButton.setStateNormal();
			break;
		case CPredefinedInteligentTimeStamp.ACTIVITY_WORK_INTERACTIVE:
			if (CTimerPanel.this.allowedAlertnessWork) {
				// opened interactive work - allow to close one
				this.interactiveWorkButton.setVisible(Boolean.TRUE);
				this.interactiveWorkButton.setStateStrikethrough();

				// disallow to user alertness button
				this.alertnessButton.setVisible(Boolean.FALSE);
				this.alertnessButton.setStateNormal();

				this.timerButtonsPanel.getActionButton().setVisible(Boolean.TRUE);
				this.timerButtonsPanel.getActionButton().setStateDisabled();
			} else {
				this.interactiveWorkButton.setVisible(Boolean.FALSE);
				this.alertnessButton.setVisible(Boolean.FALSE);
			}

			this.timerButtonsPanel.getBreakButton().setVisible(Boolean.FALSE);
			this.timerButtonsPanel.getWorkOutsideButton().setVisible(Boolean.FALSE);
			this.timerButtonsPanel.getWorkOutsideButton().setStateNormal();
			this.homeOfficeButton.setStateNormal();
			break;
		case CPredefinedInteligentTimeStamp.ACTIVITY_PARAGRAPH:
		case CPredefinedInteligentTimeStamp.ACTIVITY_PHYSICIAN_VISIT:
		case CPredefinedInteligentTimeStamp.ACTIVITY_ACCOMPANYING:
		case CPredefinedInteligentTimeStamp.ACTIVITY_PRENATAL_MEDICAL_CARE:
			// SED-608 ak mam prekazky v praci v casovaci disablovat ikony
			this.timerButtonsPanel.getActionButton().setVisible(Boolean.TRUE);
			this.timerButtonsPanel.getActionButton().setStateDisabled();
			this.timerButtonsPanel.getBreakButton().setStateDisabled();
			this.timerButtonsPanel.getWorkOutsideButton().setStateDisabled();

			this.alertnessButton.setVisible(Boolean.FALSE);
			this.interactiveWorkButton.setVisible(Boolean.FALSE);
			break;
		}

		this.changeButton.setVisible(false);
		this.newButton.setVisible(false);
		this.actionButton.setVisible(false);
		this.workOutsideButton.setVisible(false);
		this.homeOfficeButton.setVisible(false);
	}

	private void onUpdateTimer(final AjaxRequestTarget target) {
		if (CTimerActionButton.STATE_STOPPED == timerButtonsPanel.getActionButton().getButtonState()
				|| CTimerWorkOutsideButton.STATE_STRIKETHROUGH == timerButtonsPanel.getWorkOutsideButton().getButtonState()
				|| CTimerWorkOutsideButton.STATE_STRIKETHROUGH == this.homeOfficeButton.getButtonState()) {
			setModeChanged();
			target.add(changeButton);
			target.add(newButton);
			target.add(actionButton);
			target.add(workOutsideButton);
			target.add(homeOfficeButton);
			target.add(timerButtonsPanel);
			target.add(alertnessButton);
			target.add(interactiveWorkButton);
			target.appendJavaScript("document.getElementsByName('timesheetForm')[0].className = 'panel panel-change panel-calendar' ");
		}
	}

	private void setProjectsLists(List<CCodeListRecord> projectList, List<CCodeListRecord> myProjectsList, List<CCodeListRecord> allProjectsList, List<CCodeListRecord> lastUsedProjectsList) {

		boolean myProjectsSwitch = false;
		boolean lastUsedProjectsSwitch = false;

		for (CCodeListRecord project : projectList) {

			if (project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_MY)) {
				myProjectsSwitch = true;
				lastUsedProjectsSwitch = false;
				continue;
			} else if (project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_LAST_USED)) {
				myProjectsSwitch = false;
				lastUsedProjectsSwitch = true;
				continue;
			} else if (project.getId() != null && project.getId().equals(ISearchConstants.PROJECT_GROUP_ALL_OTHER)) {
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

	/**
	 * 
	 * From the list of all activities returns list of only working activities
	 */
	private List<CCodeListRecord> getWorkingActivities(List<CCodeListRecord> activityList) {
		List<CCodeListRecord> working = new ArrayList<>();

		for (CCodeListRecord activity : activityList) {
			if (!IListBoxValueTypes.NON_WORKING.equals(activity.getType()))
				working.add(activity);
		}

		return working;
	}

	public void executeActionButton(AjaxRequestTarget target) {
		try {
			switch (timerButtonsPanel.getActionButton().getButtonState()) {
			case CTimerActionButton.STATE_STOPPED:
				stopActivity(null);
				break;
			case CTimerActionButton.STATE_STARTED:
				startActivity(null, null, Boolean.FALSE);
				break;
			}

			refreshAll(target);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
		}
	}

	public void setTimerButtonsPanel(CTimerButtonsPanel timerButtonsPanel) {
		this.timerButtonsPanel = timerButtonsPanel;
	}

	public void executeBreakButton(AjaxRequestTarget target) {
		try {
			switch (timerButtonsPanel.getBreakButton().getButtonState()) {
			case CTimerBreakButton.STATE_NORMAL:
				startActivity(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK), null, null);
				break;
			case CTimerBreakButton.STATE_STRIKETHROUGH:
				stopActivity(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK));
				break;
			}
			refreshAll(target);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
		}
	}

	public void executeWorkOutsideButton(AjaxRequestTarget target) {
		try {
			switch (timerButtonsPanel.getWorkOutsideButton().getButtonState()) {
			case CTimerWorkOutsideButton.STATE_NORMAL:
				startActivity(null, Boolean.TRUE, Boolean.FALSE);
				break;
			case CTimerWorkOutsideButton.STATE_STRIKETHROUGH:
				stopActivity(null);
				break;
			}
			refreshAll(target);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
		}
	}

	public void executeHomeOfficeButton(AjaxRequestTarget target) {
		try {
			switch (this.homeOfficeButton.getButtonState()) {
			case CTimerHomeOfficeButtonItem.STATE_NORMAL:
				startActivity(null, null, Boolean.TRUE);
				break;
			case CTimerHomeOfficeButtonItem.STATE_STRIKETHROUGH:
				stopActivity(null);
				break;
			}
			refreshAll(target);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, target, CTimerPanel.this);
		}
	}
	
	private void checkJiraToken() {
		// ak QBSW používateľ nemá token a želá si byť o tom notifikovaný
		if (loggedUser != null && loggedUser.getJiraAccessToken() == null 
				&& loggedUser.getJiraTokenGeneration() != null && Boolean.TRUE.equals(loggedUser.getJiraTokenGeneration()) 
				&& loggedUser.getClientInfo() != null && loggedUser.getClientInfo().isQbsw()) {
			getFeedbackPanel().error(getString("messagesPanel.content.jiraAccessTokenNotFound"));
		}
	}

}
