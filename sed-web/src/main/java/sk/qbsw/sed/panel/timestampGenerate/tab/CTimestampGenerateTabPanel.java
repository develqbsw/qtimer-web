package sk.qbsw.sed.panel.timestampGenerate.tab;

import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.communication.service.IBrwTimeStampGenerateService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CAjaxOverlayLinkListener;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timestampGenerate.editable.CTimestampGenerateEditableTablePanel;
import sk.qbsw.sed.web.grid.datasource.CGenerateTimestampDataSource;
import sk.qbsw.sed.web.ui.CSedSession;
import sk.qbsw.sed.web.ui.components.panel.CMassEditDialogPanel;

/**
 * SubPage: /timestampGenerate SubPage title: Generovanie výkazu práce
 * 
 * TimestampGenerateTabPanel - Pridať
 */
public class CTimestampGenerateTabPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CTimestampGenerateEditableTablePanel panelToRefreshAfterSumit;

	private AjaxFallbackButton saveButton;

	private CTimestampGenerateForm<CTmpTimeSheet> form;

	private CTmpTimeSheet formModelObject;

	@SpringBean
	private IBrwTimeStampGenerateService brwTimeStampGenerateService;

	private CompoundPropertyModel<CTmpTimeSheet> formModel;
	
	private CMassEditDialogPanel modalPanelEditToRefreshAfterSubmit;

	public CTimestampGenerateTabPanel(String id, CTimestampGenerateEditableTablePanel tablePanel, final Label tabTitle, List<CCodeListRecord> activityList, final List<CCodeListRecord> projectList) {
		super(id);

		this.panelToRefreshAfterSumit = tablePanel;

		formModelObject = new CTmpTimeSheet();
		formModelObject.setDurationInPercent(0l);
		formModelObject.setDurationInMinutes(0l);

		formModel = new CompoundPropertyModel<>(formModelObject);

		form = new CTimestampGenerateForm<CTmpTimeSheet>("timestampGenerateForm", formModel, activityList, projectList, panelToRefreshAfterSumit) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate() {
				super.onValidate();
			}
		};
		add(form);

		final AjaxLink<Void> cancelButton = new AjaxLink<Void>("backButton") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					// nastavenie default hodnot do formy
					CTmpTimeSheet c = new CTmpTimeSheet();
					Long perVal = panelToRefreshAfterSumit.getTotalTime() > 0 ? 100 - panelToRefreshAfterSumit.getSumOfDurationInPercentFromTable() : 0;
					Long minVal = panelToRefreshAfterSumit.getTotalTime() > 0 ? (panelToRefreshAfterSumit.getTotalTime() - panelToRefreshAfterSumit.getSumOfDurationInMinutesFromTable()) : 0;

					c.setDurationInPercent(perVal);
					c.setDurationInMinutes(minVal);

					form.setDefaultModelObject(c);

					form.clearValidationMessages();
					form.setValidProjects();
					form.setProjectInactiveFlag(false);

					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
					target.add(tabTitle);

					// update formulara
					target.add(form);

					// prepnutie tabu
					target.appendJavaScript("$('#tab-1').click()");

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript("Main.runCustomCheck();Main.runQTimerCheck();FormElements.init();SedApp.init();");

					refreshModalEditPanel(target);
					
				} catch (CBussinessDataException e) {

					CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateTabPanel.this);

					target.appendJavaScript("FormElements.runTimePicker();");
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
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new CAjaxOverlayLinkListener(CTimestampGenerateTabPanel.this));
			}

			@Override
			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				{
					if (panelToRefreshAfterSumit.getTotalTime() <= 0) {
						getFeedbackPanel().error(CStringResourceReader.read("timestampGenerate.error.durationIs0"));
						target.add(CTimestampGenerateTabPanel.this);
					} else {

						// ulozenie zaznamu do tabulky
						try {

							CTmpTimeSheet toSave = getTimesheetFromForm();
							toSave = brwTimeStampGenerateService.add(toSave);

							if (!projectList.contains(toSave.getProject())) {
								CCodeListRecord project = toSave.getProject();
								project.setName(project.getId().toString());
								projectList.add(project);
							}

							// vytvorenie prazdneho objektu pre formu a nasetovanie duration podla zostatku
							CTmpTimeSheet blank = new CTmpTimeSheet();
							blank.setDurationInMinutes(panelToRefreshAfterSumit.getTotalTime() > 0
									? (panelToRefreshAfterSumit.getTotalTime() - panelToRefreshAfterSumit.getSumOfDurationInMinutesFromTable() - toSave.getDurationInMinutes())
									: 0);
							blank.setDurationInPercent(
									panelToRefreshAfterSumit.getTotalTime() > 0 ? 100 - panelToRefreshAfterSumit.getSumOfDurationInPercentFromTable() - toSave.getDurationInPercent() : 0);
							form.setDefaultModelObject(blank);
							((CTimestampGenerateForm<CTmpTimeSheet>) form).setValidProjects();
							((CTimestampGenerateForm<CTmpTimeSheet>) form).setProjectInactiveFlag(false);

							// update nadpisu
							tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
							target.add(tabTitle);

							// prepnutie tabu
							target.appendJavaScript("$('#tab-1').click()");

							// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker
							// atd...
							target.appendJavaScript("Main.runCustomCheck();Main.runQTimerCheck();FormElements.init();SedApp.init();");

							panelToRefreshAfterSumit.setAllowAction(true);
							// refresh tabulky a zostatku
							((CGenerateTimestampDataSource) panelToRefreshAfterSumit.getTable().getDataSource()).insertRow(0, toSave);
							target.add(panelToRefreshAfterSumit.getTable());
							target.add(panelToRefreshAfterSumit.getRemaining());
							target.add(getFeedbackPanel());

							// update formulara - vsetko sa vynuluje
							target.add(CTimestampGenerateTabPanel.this);

							((CTimestampGenerateForm<CTmpTimeSheet>) form).clearValidationMessages();

						} catch (CBussinessDataException e) {

							CTimestampGenerateTabPanel.this.form.addRedBorderToDurationField(target);

							CBussinessDataExceptionProcessor.process(e, target, CTimestampGenerateTabPanel.this);

							target.appendJavaScript("FormElements.runTimePicker();");
						}
					}
				}
				
				refreshModalEditPanel(target);
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

	public CTmpTimeSheet getFormModelObject() {
		return formModelObject;
	}

	private CTmpTimeSheet getTimesheetFromForm() throws CBussinessDataException {

		CTmpTimeSheet tmpTimesheetFromForm = (CTmpTimeSheet) form.getModelObject();
		Long totalTime = panelToRefreshAfterSumit.getTotalTime();
		CLoggedUserRecord user = CSedSession.get().getUserModel().getRecord();

		Date from = panelToRefreshAfterSumit.getFilterModel().getObject().getDateFrom();
		Date to = panelToRefreshAfterSumit.getFilterModel().getObject().getDateTo();

		final CTmpTimeSheet tmpTimesheet = new CTmpTimeSheet(); // musi byt novy objekt

		Long minutes = tmpTimesheetFromForm.getDurationInMinutes() != null ? tmpTimesheetFromForm.getDurationInMinutes() : 0;
		Long percent = tmpTimesheetFromForm.getDurationInPercent() != null ? tmpTimesheetFromForm.getDurationInPercent() : 0;

		// ak sa vyplnali percenta tak to preklop na minuty
		if (panelToRefreshAfterSumit.usingPercent()) {
			minutes = CDateUtils.getPercentAsMinutes(percent, totalTime).longValue();
		}

		// ak sa vyplnali minuty tak to preklop na percenta
		if (panelToRefreshAfterSumit.usingMinutes()) {
			percent = CDateUtils.getMinutesAsPercent(minutes, totalTime).longValue();
		}

		tmpTimesheet.setClientId(user.getClientInfo().getClientId());
		tmpTimesheet.setDateFrom(from);
		tmpTimesheet.setDateTo(to);

		tmpTimesheet.setActivityId(tmpTimesheetFromForm.getActivityId());
		tmpTimesheet.setProjectId(tmpTimesheetFromForm.getProjectId());

		tmpTimesheet.setNote(tmpTimesheetFromForm.getNote());
		tmpTimesheet.setPhase(tmpTimesheetFromForm.getPhase());

		tmpTimesheet.setOutside(tmpTimesheetFromForm.getOutside());
		tmpTimesheet.setHomeOffice(tmpTimesheetFromForm.getHomeOffice() == null ? Boolean.FALSE : tmpTimesheetFromForm.getHomeOffice());

		tmpTimesheet.setDurationInMinutes(minutes);
		tmpTimesheet.setDurationInPercent(percent);
		tmpTimesheet.setSummaryDurationInMinutes(totalTime);

		return tmpTimesheet;
	}

	public CTimestampGenerateForm<CTmpTimeSheet> getForm() {
		return form;
	}
	
	private void refreshModalEditPanel(AjaxRequestTarget target) {
		
		if (modalPanelEditToRefreshAfterSubmit.getProjectCheckBox().getModelObject().booleanValue()) {
			modalPanelEditToRefreshAfterSubmit.getProjectCheckBox().setModelObject(Boolean.FALSE);
			modalPanelEditToRefreshAfterSubmit.getProjectContainer().setEnabled(false);
			// target.appendJavaScript("$('.search-select-projects2').select2({placeholder: '',allowClear: false});");
		} else {
			modalPanelEditToRefreshAfterSubmit.getProjectContainer().setEnabled(false);
		}
		target.add(modalPanelEditToRefreshAfterSubmit.getProjectContainer());

		if (modalPanelEditToRefreshAfterSubmit.getActivityCheckBox().getModelObject().booleanValue()) {
			modalPanelEditToRefreshAfterSubmit.getActivityCheckBox().setModelObject(Boolean.FALSE);
			modalPanelEditToRefreshAfterSubmit.getActivityContainer().setEnabled(false);
			// target.appendJavaScript("$('.search-select-activities2').select2({placeholder: '',allowClear: false});");
		} else {
			modalPanelEditToRefreshAfterSubmit.getActivityContainer().setEnabled(false);
		}
		target.add(modalPanelEditToRefreshAfterSubmit.getActivityContainer());
	}

	public void setModalPanelEditToRefreshAfterSubmit(CMassEditDialogPanel modalPanelEditToRefreshAfterSubmit) {
		this.modalPanelEditToRefreshAfterSubmit = modalPanelEditToRefreshAfterSubmit;
	}

}
