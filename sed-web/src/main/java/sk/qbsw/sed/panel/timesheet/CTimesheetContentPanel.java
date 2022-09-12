package sk.qbsw.sed.panel.timesheet;

import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.ITimeSheetRecordStates;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IBrwTimeStampService;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CRemainingVacationUtils;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CMassEditDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

/**
 * SubPage: /timesheet SubPage title: Výkaz práce
 * 
 * Panel TimesheetContentPanel - Pridať / Detail / Editovať
 */
public class CTimesheetContentPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private static final String TAB_1_CLICK = "$('#tab-1').click()";
	
	private static final String MAIN_CHECK_INIT_FORM_ELEMENTS_SED_APP_INIT = "Main.runCustomCheck();Main.runQTimerCheck();FormElements.init();SedApp.init();";
	
	/** input timesheet */
	private CTimeStampRecord timesheet;

	/** modal confirm dialog */
	private CModalBorder modal;

	/** content panel for confirm dialog */
	private CConfirmDialogPanel modalPanel;

	@SpringBean
	private ITimesheetClientService timesheetService;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IBrwTimeStampService timestampService;

	private Long entityID;

	private CompoundPropertyModel<CTimeStampRecord> timesheetModel;

	private CTimesheetForm<CTimeStampRecord> form;

	private boolean isEditMode; // edit <--> add

	private Panel panelToRefreshAfterSumit;

	private Panel feedbackToRefreshAfterSumit;

	private CMassEditDialogPanel modalPanelEditToRefreshAfterSubmit;

	private AjaxFallbackButton saveButton;

	private AjaxFallbackLink<Object> linkEdit;

	private final Label remainingVacation;

	private AjaxFallbackLink<Object> linkSplit;

	private CModalBorder defaultTimestampModal;

	private CModalWindowTimesheetContentPanel modalWindowTimesheetContentPanel;

	public CTimesheetContentPanel(String id, final Label tabTitle, List<CCodeListRecord> usersList, List<CCodeListRecord> activityList, List<CCodeListRecord> projectList,
			List<CCodeListRecord> requestReasonList, Label remainingVacation) {
		super(id);

		initModal();
		updateModel();

		isEditMode = entityID != null;
		timesheetModel = new CompoundPropertyModel<>(timesheet);
		this.remainingVacation = remainingVacation;

		form = new CTimesheetForm<CTimeStampRecord>("timesheetForm", timesheetModel, this, isEditMode, usersList, activityList, projectList, requestReasonList) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate() {
				super.onValidate();

			}
		};
		add(form);

		linkEdit = new AjaxFallbackLink<Object>("edit") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.edit"));
					target.add(tabTitle);

					// update formulara
					setModeDetail(false, null, null);
					target.add(form);

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript(MAIN_CHECK_INIT_FORM_ELEMENTS_SED_APP_INIT);
					
					refreshModalEditPanel(target);
					
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					getFeedbackPanel().error(getString(e.getModel().getServerCode()));
				}
			}
		};
		linkEdit.setVisible(false);
		linkEdit.add(new AttributeAppender("title", getString("button.edit")));
		form.add(linkEdit);

		linkSplit = new AjaxFallbackLink<Object>("split") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalWindowTimesheetContentPanel.setDateFrom(form.getModel().getObject().getDate());
				target.add(modalWindowTimesheetContentPanel);
				defaultTimestampModal.show(target);
			}
		};
		linkSplit.setVisible(false);
		linkSplit.add(new AttributeAppender("title", getString("button.split")));
		form.add(linkSplit);

		final AjaxLink<Void> cancelButton = new AjaxLink<Void>("backButton") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
					target.add(tabTitle);

					// update formulara
					entityID = null;
					CTimesheetContentPanel.this.updateModel();
					setModeDetail(false, null, null);

					// SED-856 a - ak je disablovaný home office, odšrtnem ho
					if (!getForm().homeOffice.isEnabled()) {
						getForm().uncheckHomeOfficeCheckBox();
					}

					target.add(CTimesheetContentPanel.this);
					form.clearFeedbackMessages();

					// prepnutie tabu
					target.appendJavaScript(TAB_1_CLICK);

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript(MAIN_CHECK_INIT_FORM_ELEMENTS_SED_APP_INIT);
					
					refreshModalEditPanel(target);
					
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
					getFeedbackPanel().error(getString(e.getModel().getServerCode()));
				}
			}
		};
		form.add(cancelButton);

		saveButton = new AjaxFallbackButton("submitBtn", form) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				CTimeStampRecord timeSheetRecord = (CTimeStampRecord) form.getModelObject();

				String errorMsg = CTimesheetValidations.validateActivityDuration(timeSheetRecord.getActivityId(), timeSheetRecord.getRequestReasonId(), timeSheetRecord.getTimeFrom(),
						timeSheetRecord.getTimeTo());
				if (errorMsg != null) {
					error(getString(errorMsg));
					target.add(getFeedbackPanel());
					return;
				}

				try {
					if (isEditMode) {
						timesheetService.modify(timeSheetRecord);
					} else {
						timesheetService.add(timeSheetRecord);
					}

					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
					target.add(tabTitle);

					// update formulara
					entityID = null;
					CTimesheetContentPanel.this.updateModel();
					setModeDetail(false, null, null);
					target.add(CTimesheetContentPanel.this);

					// prepnutie tabu
					target.appendJavaScript(TAB_1_CLICK);

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript(MAIN_CHECK_INIT_FORM_ELEMENTS_SED_APP_INIT);
					
					refreshModalEditPanel(target);

					// refresh tabulky
					target.add(panelToRefreshAfterSumit);
					target.add(feedbackToRefreshAfterSumit);
					target.add(getFeedbackPanel());

					updateRemainingVacation(target);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CTimesheetContentPanel.this);
					onError(target, form);
				}
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("afterError();Main.runCustomCheck();Main.runQTimerCheck();");
				refreshModalEditPanel(target);
				target.add(form);
			}

		};
		form.add(saveButton);

		CFeedbackPanel feedbackPanel = new CFeedbackPanel("feedback");
		registerFeedbackPanel(feedbackPanel);
		form.setFeedbackPanel(feedbackPanel);
		form.add(feedbackPanel);
	}

	public void setPanelToRefresh(Panel panelToRefreshAfterSumit, Panel feedbackToRefreshAfterSumit, CMassEditDialogPanel modalPanelEditToRefreshAfterSubmit) {
		this.panelToRefreshAfterSumit = panelToRefreshAfterSumit;
		this.feedbackToRefreshAfterSumit = feedbackToRefreshAfterSumit;
		this.modalPanelEditToRefreshAfterSubmit = modalPanelEditToRefreshAfterSubmit;
	}

	public Long getEntityID() {
		return entityID;
	}

	public void setEntityID(Long entityID) {
		this.entityID = entityID;
		updateModel();
	}

	private void updateModel() {
		isEditMode = entityID != null;

		if (entityID != null) {
			try {
				this.timesheet = timesheetService.getDetail(entityID);
				form.setProjectInactiveFlag(timesheet.getProjectInactive() == null ? false : timesheet.getProjectInactive());
				form.setActivityInactiveFlag(timesheet.getActivityInactive() == null ? false : timesheet.getActivityInactive());
				form.setWorkingBefore(timesheet.getShowProject());
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			try {
				final CLoggedUserRecord user = CSedSession.get().getUser();

				if (IUserTypeCode.ORG_ADMIN.equals(CSedSession.get().getUser().getRoleCode())) {
					// admin nema predefined timestamp
					this.timesheet = new CTimeStampRecord();
					this.timesheet.setDateFrom(new Date());
				} else {
					CPredefinedTimeStamp predefined = timesheetService.loadPredefinedValue(CSedSession.get().getUser().getUserId(), false);
					if (predefined != null) {
						this.timesheet = predefined.getRecordToShow();

						if (timesheet.getProjectInactive() != null && timesheet.getProjectInactive()) {
							// na pridani chceme zobrazit len platne projekty
							timesheet.setProjectInactive(false);
						}

						if (timesheet.getActivityInactive() != null && timesheet.getActivityInactive()) {
							// na pridani chceme zobrazit len platne aktivity
							timesheet.setActivityInactive(false);
						}

					} else {
						// ak je nova organizacia a novy zamestnanec
						this.timesheet = new CTimeStampRecord();
						this.timesheet.setDateFrom(new Date());
						// SED-822, nasetovanie userId aby nepadal null pointer, tento user bude predvyplnený v dropDowne Používateľ
						this.timesheet.setEmployeeId(CSedSession.get().getUser().getUserId());
					}
				}
				if (form != null) {
					// aby tam nahodou neostali aj neplatne
					form.setProjectInactiveFlag(false);
					form.setActivityInactiveFlag(false);

					form.setWorkingBefore(timesheet.getShowProject() == null ? false : timesheet.getShowProject());
				}
				setDefaultUserValues(user);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		}

		if (form != null) {
			form.setDefaultModelObject(timesheet);
		}
	}

	protected void refreshTablePanel(AjaxRequestTarget target) {
		// prepnutie tabu
		target.appendJavaScript(TAB_1_CLICK);

		// refresh tabuľky
		target.add(panelToRefreshAfterSumit);
		target.add(feedbackToRefreshAfterSumit);
		target.add(getFeedbackPanel());

		CTimesheetContentPanel.this.updateModel();
		target.add(CTimesheetContentPanel.this.getForm());
	}

	protected void refreshTablePanelWithoutTabClick(AjaxRequestTarget target) {

		// refresh tabuľky
		target.add(panelToRefreshAfterSumit);
		target.add(feedbackToRefreshAfterSumit);
		target.add(getFeedbackPanel());

		CTimesheetContentPanel.this.updateModel();
		target.add(CTimesheetContentPanel.this.getForm());
	}

	/**
	 * Sets default user values
	 */
	private void setDefaultUserValues(CLoggedUserRecord user) {
		// sets detail about changing user
		this.timesheet.setChangedByName(user.getName());
		this.timesheet.setChangedBySurname(user.getSurname());
		this.timesheet.setChangeTime(new Date());
	}

	// rowId resp. id časovej značky používam aby som rozhodol, či na detaile
	// mám zobraziť editačné tlačidlá
	public void setModeDetail(boolean isModeDetail, Long rowId, CCodeListRecord employee) throws CBussinessDataException {
		if (isModeDetail) {
			boolean autorizovanyUser = true;

			if (rowId != null) {
				autorizovanyUser = this.timestampService.showEditButtonOnDetail(rowId);
			}
			if (!autorizovanyUser) {
				List<CCodeListRecord> userFieldList = (List<CCodeListRecord>) form.getUserField().getChoices();

				// pridám usera, ktorého časovú značku prezerám ale nie je môj podriadený
				userFieldList.add(employee);
			} else {
				// vymažem a znova naplním listbox Zamestnanec
				List<CCodeListRecord> userFieldList = (List<CCodeListRecord>) form.getUserField().getChoices();
				userFieldList.clear();

				List<CCodeListRecord> subList = userService.listSubordinateUsers(Boolean.TRUE, Boolean.TRUE);
				userFieldList.addAll(subList);
			}

			if (form.getModelObject().getStatusId().equals(ITimeSheetRecordStates.ID_NEW) && autorizovanyUser) {
				// detail - linkEdit show and enable
				linkEdit.setEnabled(true);
				linkEdit.add(AttributeModifier.replace("class", "btn btn-green"));
				linkEdit.setVisible(true);

				// ak nie je vyplnený čas do - časový značku nemôžem deliť alebo to nie je pracovná aktivita
				if (form.getModelObject().getDateTo() == null || !IListBoxValueTypes.WORKING.equals(form.getModelObject().getActivity().getType())) {
					linkSplit.setVisible(false);
				} else {
					linkSplit.setVisible(true);
				}
			} else {
				// if is confirmed do not show
				linkEdit.setEnabled(false);
				linkEdit.setVisible(false);
				linkSplit.setVisible(false);
			}
		} else {
			if (isEditMode) {
				// edit - linkEdit show and disable
				linkEdit.setEnabled(false);
				linkEdit.add(AttributeModifier.append("class", "disabled"));
				linkEdit.setVisible(true);
				linkSplit.setVisible(false);
			} else {
				// add - linkEdit hide
				linkEdit.setVisible(false);
				linkSplit.setVisible(false);
			}
		}
		saveButton.setVisible(!isModeDetail);
		form.setModeDetail(isModeDetail);
	}

	public void clearFeedbackMessages() {
		this.form.clearFeedbackMessages();
	}

	private void initModal() {
		modal = new CModalBorder("modalWindow");
		modal.setOutputMarkupId(true);
		modalPanel = new CConfirmDialogPanel("content", modal);
		modal.add(modalPanel);
		add(modal);

		defaultTimestampModal = new CModalBorder("defaultTimestampModal");
		defaultTimestampModal.setOutputMarkupId(true);
		defaultTimestampModal.getTitleModel().setObject(CStringResourceReader.read("timesheet.dialog.split"));
		modalWindowTimesheetContentPanel = new CModalWindowTimesheetContentPanel("defaultTimestampModalPanel", defaultTimestampModal, this);
		defaultTimestampModal.add(modalWindowTimesheetContentPanel);
		modalWindowTimesheetContentPanel.setOutputMarkupId(true);
		add(defaultTimestampModal);
	}

	public CTimesheetForm<CTimeStampRecord> getForm() {
		return this.form;
	}

	/**
	 * update ostavajucich dni dovolenky
	 *
	 * @param target
	 * @throws CBussinessDataException
	 */
	private void updateRemainingVacation(AjaxRequestTarget target) throws CBussinessDataException {
		if (getuser().getUserId().equals(timesheet.getEmployeeId())) {
			// ak si pridavam ziadost ja a nie moj nadriadeny
			CUserDetailRecord user = userService.getUserDetails(timesheet.getEmployeeId());
			remainingVacation.setDefaultModelObject(CRemainingVacationUtils.getText(user.getVacation()));
			target.add(remainingVacation);
		}
	}

	public Panel getPanelToRefreshAfterSumit() {
		return panelToRefreshAfterSumit;
	}

	public Panel getFeedbackToRefreshAfterSumit() {
		return feedbackToRefreshAfterSumit;
	}
	
	private void refreshModalEditPanel(AjaxRequestTarget target) {
		
		if (modalPanelEditToRefreshAfterSubmit.getProjectCheckBox().getModelObject().booleanValue()) {
			modalPanelEditToRefreshAfterSubmit.getProjectCheckBox().setModelObject(Boolean.FALSE);
			modalPanelEditToRefreshAfterSubmit.getProjectContainer().setEnabled(false);
			//target.appendJavaScript("$('.search-select-projects2').select2({placeholder: '',allowClear: false});");
		} else {
			modalPanelEditToRefreshAfterSubmit.getProjectContainer().setEnabled(false);
		}
		target.add(modalPanelEditToRefreshAfterSubmit.getProjectContainer());

		if (modalPanelEditToRefreshAfterSubmit.getActivityCheckBox().getModelObject().booleanValue()) {
			modalPanelEditToRefreshAfterSubmit.getActivityCheckBox().setModelObject(Boolean.FALSE);
			modalPanelEditToRefreshAfterSubmit.getActivityContainer().setEnabled(false);
			//target.appendJavaScript("$('.search-select-activities2').select2({placeholder: '',allowClear: false});");
		} else {
			modalPanelEditToRefreshAfterSubmit.getActivityContainer().setEnabled(false);
		}
		target.add(modalPanelEditToRefreshAfterSubmit.getActivityContainer());
	}
}
