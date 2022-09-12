package sk.qbsw.sed.panel.requests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.IRequestStates;
import sk.qbsw.sed.client.model.IRequestTypes;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CRemainingVacationUtils;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.IAjaxCommand;
import sk.qbsw.sed.web.ui.components.panel.CConfirmDialogPanel;
import sk.qbsw.sed.web.ui.components.panel.CModalBorder;

public class CRequestPanel extends CPanel {

	private static final long serialVersionUID = 1L;
	
	private static final String TITLE = "title";

	private static final String MAIN_Q_TIMER_CHECK_INIT_FORM_SED_APP_INIT = "Main.runQTimerCheck();FormElements.init();SedApp.init();";
	
	private static final String TAB_TITLE_NEW = "tabTitle.new";
	
	private static final String TAB_1_CLICK = "$('#tab-1').click()";
	
	private static final String CLASS = "class";
	
	private CRequestRecord requestRecord;

	private CompoundPropertyModel<CRequestRecord> requestModel;

	private CRequestForm<CRequestRecord> form;

	private final CModalBorder modal;

	private final CConfirmDialogPanel modalPanel;

	private CRequestTablePanel panelToRefresh;

	private final Label tabTitle;
	private final Label remainingVacation;

	private Long entityID;

	private boolean isEditMode;

	private AjaxFallbackButton saveButton;

	private AjaxFallbackLink<Object> linkEdit;
	private AjaxFallbackLink<Object> cancelLink;
	private WebMarkupContainer approveReject;
	private WebMarkupContainer buttons;
	private boolean isModeDetail;

	private static final Integer COMMAND_APPROVE = 1;
	private static final Integer COMMAND_REJECT = 2;
	private static final Integer COMMAND_CANCEL = 3;

	@SpringBean
	private IRequestClientService service;

	@SpringBean
	private IUserClientService userService;

	private final List<Long> subordinateUserIds = new ArrayList<>();

	public CRequestPanel(String id, Label tabTitle, Label remainingVacation) {
		super(id);
		this.tabTitle = tabTitle;
		this.remainingVacation = remainingVacation;

		modal = new CModalBorder("modalWindow");
		modal.setOutputMarkupId(true);
		modal.getTitleModel().setObject(CStringResourceReader.read("REQUEST_DUPLICITY"));
		modalPanel = new CConfirmDialogPanel("content", modal);
		modal.add(modalPanel);
		add(modal);

		try {
			for (CCodeListRecord user : userService.listSubordinateUsers(Boolean.TRUE, Boolean.TRUE)) {
				subordinateUserIds.add(user.getId());
			}
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		setDefaultRequestRecord();
		setRemainingDays();
		requestModel = new CompoundPropertyModel<>(requestRecord);

		form = new CRequestForm<>("requestForm", requestModel);
		form.setOutputMarkupId(true);
		add(form);

		approveReject = new WebMarkupContainer("approveReject") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			Long userId = ((CSedSession) getSession()).getUser().getUserId();

			@Override
			public boolean isVisible() {
				return isModeDetail && subordinateUserIds.contains(requestModel.getObject().getOwnerId()) && !(userId.equals(requestModel.getObject().getOwnerId()))
						&& !CRequestPanel.this.requestRecord.getStatusId().equals(IRequestStates.ID_CANCELLED);
			}
		};

		approveReject.setOutputMarkupId(true);
		approveReject.add(createApproveLink());
		approveReject.add(createRejectLink());
		approveReject.add(new AttributeAppender(TITLE, getString("requests.approveReject.appender")));
		cancelLink = createCancelLink();

		// edit
		linkEdit = new AjaxFallbackLink<Object>("edit") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.edit"));
				target.add(tabTitle);

				// update formulara
				setModeDetail(false);
				form.setModeEdit(true);
				target.add(form);
				target.add(buttons);

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript(MAIN_Q_TIMER_CHECK_INIT_FORM_SED_APP_INIT);
			}
		};
		linkEdit.add(new AttributeAppender(TITLE, getString("requests.linkEdit.appender")));
		linkEdit.setVisible(false);
		buttons = new WebMarkupContainer("buttons");
		buttons.setOutputMarkupId(true);
		buttons.add(linkEdit);
		buttons.add(approveReject);
		buttons.add(cancelLink);
		add(buttons);

		final AjaxLink<Void> cancelButton = new AjaxLink<Void>("backButton") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// update nadpisu
				tabTitle.setDefaultModelObject(CStringResourceReader.read(TAB_TITLE_NEW));
				target.add(tabTitle);

				// update formulara
				entityID = null;
				CRequestPanel.this.updateModel();
				setModeDetail(false);
				form.setModeEdit(false);
				setModeAdd(true);
				target.add(CRequestPanel.this);
				form.clearFeedbackMessages();

				// prepnutie tabu
				target.appendJavaScript(TAB_1_CLICK);

				// nastavim date-range na pole datum
				form.getDateField().add(AttributeModifier.replace(CLASS, "form-control date-range"));
				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript(MAIN_Q_TIMER_CHECK_INIT_FORM_SED_APP_INIT);
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
				try {
					// ulozenie zaznamu
					final CRequestRecord requestRecord = (CRequestRecord) form.getModelObject();
					saveAndUpdate(target, requestRecord, false);

				} catch (CBussinessDataException e) {

					if (e.getModel().getServerCode().equals(CClientExceptionsMessages.REQUEST_DUPLICITY)) {
						modalPanel.setAction(new IAjaxCommand() {

							private static final long serialVersionUID = 1L;

							@Override
							public void execute(AjaxRequestTarget target) {
								try {
									saveAndUpdate(target, requestRecord, true);

								} catch (CBussinessDataException e) {
									CBussinessDataExceptionProcessor.process(e, target, CRequestPanel.this);
								}
							}
						});

						modal.getTitleModel().setObject(CStringResourceReader.read("REQUEST_DUPLICITY"));
						modal.show(target);
					} else {
						CBussinessDataExceptionProcessor.process(e, target, CRequestPanel.this);
					}
				}
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("afterError();");
				target.add(form);
			}
		};
		form.add(saveButton);
	}

	private AjaxFallbackLink<Object> createApproveLink() {
		// approve
		AjaxFallbackLink<Object> approveLink = new AjaxFallbackLink<Object>("approve") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			Long userId = ((CSedSession) getSession()).getUser().getUserId();
			Boolean isBasicEmployee = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB);

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getCommand(COMMAND_APPROVE, requestModel.getObject().getId());
				showModal(target, commandToExectute, "requests.dialog.approve");
				target.add(approveReject);
			}

			@Override
			public boolean isEnabled() {

				return isModeDetail && (requestModel.getObject().getStatusId().equals(IRequestStates.ID_CREATED) || requestModel.getObject().getStatusId().equals(IRequestStates.ID_REJECTED))
						&& !isBasicEmployee && !(userId.equals(requestModel.getObject().getOwnerId()));
			}

		};
		approveLink.add(new AttributeAppender(TITLE, getString("requests.approveRequest.appender")));
		return approveLink;
	}

	private AjaxFallbackLink<Object> createRejectLink() {
		AjaxFallbackLink<Object> rejectLink = new AjaxFallbackLink<Object>("reject") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			Long userId = ((CSedSession) getSession()).getUser().getUserId();
			Boolean isBasicEmployee = CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE) || CSedSession.get().getUser().containsRole(IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB);

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getCommand(COMMAND_REJECT, requestModel.getObject().getId());
				showModal(target, commandToExectute, "requests.dialog.reject");
				target.add(approveReject);
			}

			@Override
			public boolean isEnabled() {

				return isModeDetail && (requestModel.getObject().getStatusId().equals(IRequestStates.ID_CREATED) || requestModel.getObject().getStatusId().equals(IRequestStates.ID_APPROVED))
						&& !isBasicEmployee && !(userId.equals(requestModel.getObject().getOwnerId()));
			}

		};
		rejectLink.add(new AttributeAppender(TITLE, getString("requests.rejectRequest.appender")));
		return rejectLink;
	}

	private AjaxFallbackLink<Object> createCancelLink() {
		AjaxFallbackLink<Object> rejectLink = new AjaxFallbackLink<Object>("cancel") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				IAjaxCommand commandToExectute = getCommand(COMMAND_CANCEL, requestModel.getObject().getId());
				showModal(target, commandToExectute, "requests.dialog.cancel");
			}

			@Override
			public boolean isVisible() {
				return isModeDetail && !CRequestPanel.this.requestRecord.getStatusId().equals(IRequestStates.ID_CANCELLED)
						&& !CRequestPanel.this.requestRecord.getStatusId().equals(IRequestStates.ID_REJECTED) && subordinateUserIds.contains(requestModel.getObject().getOwnerId());
			}

			@Override
			public boolean isEnabled() {
				return isModeDetail && !(requestModel.getObject().getStatusId().equals(IRequestStates.ID_CANCELLED));
			}

		};
		rejectLink.add(new AttributeAppender(TITLE, getString("requests.linkCancelRequest.appender")));
		return rejectLink;
	}

	private void saveAndUpdate(AjaxRequestTarget target, CRequestRecord requestRecord, boolean ignoreDuplicity) throws CBussinessDataException {
		if (isEditMode) {
			service.modify(requestRecord, ignoreDuplicity);
		} else {
			service.add(requestRecord, ignoreDuplicity);
		}

		updateRemainingVacation(target);

		// update nadpisu
		tabTitle.setDefaultModelObject(CStringResourceReader.read(TAB_TITLE_NEW));
		target.add(tabTitle);

		// update formulara
		entityID = null;
		CRequestPanel.this.updateModel();
		setModeDetail(false);
		form.setModeEdit(false);
		setModeAdd(true);

		// prepnutie tabu
		target.appendJavaScript(TAB_1_CLICK);

		// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
		target.appendJavaScript(MAIN_Q_TIMER_CHECK_INIT_FORM_SED_APP_INIT);

		target.add(form);
		target.add(buttons);
		refreshTablePanel(target);
		target.add(getFeedbackPanel());
	}

	public void setEntityID(Long entityID) {
		this.entityID = entityID;
		updateModel();
	}

	protected void updateModel() {
		isEditMode = entityID != null;

		if (entityID != null) {
			try {
				this.requestRecord = service.getDetail(entityID);
				form.setDefaultModelObject(requestRecord);

				form.setOldWorkDays(requestRecord.getWorkDays());
				form.setHalfDayFlag(requestRecord.getHalfday());
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			setDefaultRequestRecord();
			form.setDefaultModelObject(requestRecord);

			form.setOldWorkDays(Float.valueOf(0));
			form.setHalfDayFlag(false);
		}
		setRemainingDays();
	}

	private void setRemainingDays() {
		try {
			CUserDetailRecord user = userService.getUserDetails(requestRecord.getOwnerId());
			Double vacation = user.getVacation();
			this.requestRecord.setRemainingDays(vacation);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
	}

	public void setModeDetail(boolean isModeDetail) {
		this.isModeDetail = isModeDetail;
		if (isModeDetail) {
			if (this.requestRecord.getStatusId().equals(IRequestStates.ID_CREATED) && subordinateUserIds.contains(requestRecord.getOwnerId())) {
				// detail - linkEdit show and enable
				linkEdit.setEnabled(true);
				linkEdit.add(AttributeModifier.replace(CLASS, "btn btn-green"));
				linkEdit.setVisible(true);
			} else {
				linkEdit.setEnabled(false);
				linkEdit.setVisible(false);
			}
		} else {
			if (isEditMode) {
				// edit - linkEdit show and disable
				linkEdit.setEnabled(false);
				linkEdit.add(AttributeModifier.append(CLASS, "disabled"));
				linkEdit.setVisible(true);
			} else {
				linkEdit.setVisible(false);
			}
		}
		saveButton.setVisible(!isModeDetail);
		form.setModeDetail(isModeDetail);
	}

	public void setPanelToRefresh(CRequestTablePanel panelToRefresh) {
		this.panelToRefresh = panelToRefresh;
	}

	public void setModeAdd(boolean b) {
		form.setModeAdd(b);
		if (b) {
			form.getModelObject().setCreateDate(new Date());
		}
	}

	public void clearFeedbackMessages() {
		this.form.clearFeedbackMessages();
	}

	private IAjaxCommand getCommand(final Integer commandType, final Long requestId) {
		return new IAjaxCommand() {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(AjaxRequestTarget target) {
				try {
					if (commandType.equals(COMMAND_APPROVE)) {
						service.approve(requestId);
					} else if (commandType.equals(COMMAND_REJECT)) {
						service.reject(requestId);
					} else if (commandType.equals(COMMAND_CANCEL)) {
						service.cancel(requestId);
						updateRemainingVacation(target);
					}

					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read(TAB_TITLE_NEW));
					target.add(tabTitle);

					// update formulara
					entityID = null;
					CRequestPanel.this.updateModel();
					setModeDetail(false);
					form.setModeEdit(false);
					setModeAdd(true);
					form.clearFeedbackMessages();

					// prepnutie tabu
					target.appendJavaScript(TAB_1_CLICK);

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript(MAIN_Q_TIMER_CHECK_INIT_FORM_SED_APP_INIT);

					target.add(form);
					target.add(buttons);
					refreshTablePanel(target);
					target.add(getFeedbackPanel());

				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CRequestPanel.this);

					// SED-552 - Zároveň po vypísaní chyby refreshnúť detail žiadosti, tak aby sa na nej tlačidlá potvrdenia,
					// zamietnutia, zrušenia a editácie zobrazovali tak ako je pre žiadosť v danom stave korektné.
					CRequestPanel.this.updateModel();
					if (!IRequestStates.ID_CREATED.equals(requestRecord.getStatusId())) { // SED-571
						linkEdit.setVisible(false);
					}
					target.add(form);
					target.add(buttons);
					target.appendJavaScript(MAIN_Q_TIMER_CHECK_INIT_FORM_SED_APP_INIT);
				}
			}
		};

	}

	private void showModal(AjaxRequestTarget target, final IAjaxCommand commandToExecute, String titleCode) {

		modal.getTitleModel().setObject(CStringResourceReader.read(titleCode));
		modalPanel.setAction(commandToExecute);
		modal.show(target);
	}

	private void setDefaultRequestRecord() {
		requestRecord = new CRequestRecord();
		requestRecord.setFullday(true);
		requestRecord.setOwnerId(CSedSession.get().getUser().getUserId());
		requestRecord.setCreateDate(new Date());
		requestRecord.setTypeId(IRequestTypes.ID_H);

		try { // SED-803 - vyplním nadriadeného
			CUserDetailRecord user = userService.getUserDetails(requestRecord.getOwnerId());
			requestRecord.setSuperior(user.getSuperior());
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
	}

	/**
	 * refresh table panel after submit. Either graph or table.
	 * 
	 * @param target
	 */
	private void refreshTablePanel(AjaxRequestTarget target) {
		if (panelToRefresh.isGraphVisible()) {
			panelToRefresh.updateGraph(target, true);
		} else {
			target.add(panelToRefresh.getTable());
		}
	}

	/**
	 * update ostavajucich dni dovolenky
	 *
	 * @param target
	 * @throws CBussinessDataException
	 */
	private void updateRemainingVacation(AjaxRequestTarget target) throws CBussinessDataException {
		if (getuser().getUserId().equals(requestRecord.getOwnerId())) {
			// ak si pridavam ziadost ja a nie moj nadriadeny
			CUserDetailRecord user = userService.getUserDetails(requestRecord.getOwnerId());
			remainingVacation.setDefaultModelObject(CRemainingVacationUtils.getText(user.getVacation()));
			target.add(remainingVacation);
		}
	}
}
