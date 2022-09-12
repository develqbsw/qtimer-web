package sk.qbsw.sed.panel.holidays;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.communication.service.IHolidayClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /holidays SubPage title: Sviatky
 *
 * HolidayContentPanel - Pridať / Detail / Editovať
 */
public class CHolidayContentPanel extends CPanel {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	private static final String MAIN_CHECK_INIT_FORM_SED_APP_INIT = "Main.runCustomCheck();FormElements.init();SedApp.init();";

	/** input timesheet */
	private CHolidayRecord holiday;

	@SpringBean
	private IHolidayClientService holidayService;

	private Long entityID;

	private CompoundPropertyModel<CHolidayRecord> holidayModel;

	private CHolidayForm<CHolidayRecord> form;

	private boolean isEditMode;

	private CHolidayTablePanel panelToRefreshAfterSumit;

	private final Label tabTitle;

	private AjaxFallbackButton saveButton;

	private AjaxFallbackLink<Object> linkEdit;

	public CHolidayContentPanel(String id, Label tabTitle) {
		super(id);
		this.tabTitle = tabTitle;

		if (entityID != null) {
			try {
				this.holiday = holidayService.getDetail(entityID);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			this.holiday = new CHolidayRecord();
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		isEditMode = entityID != null;
		holidayModel = new CompoundPropertyModel<>(holiday);

		form = new CHolidayForm<>("holidayForm", holidayModel, this, isEditMode);
		add(form);

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
				target.add(form);

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript(MAIN_CHECK_INIT_FORM_SED_APP_INIT);
			}
		};
		linkEdit.setVisible(false);
		linkEdit.add(new AttributeAppender("title", getString("button.edit")));
		form.add(linkEdit);

		final AjaxLink<Void> cancelButton = new AjaxLink<Void>("backButton") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// update nadpisu
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
				target.add(tabTitle);

				// update formulara
				entityID = null;
				CHolidayContentPanel.this.updateModel();
				setModeDetail(false);
				target.add(CHolidayContentPanel.this);
				form.clearFeedbackMessages();

				// prepnutie tabu
				target.appendJavaScript("$('#tab-1').click()");

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript(MAIN_CHECK_INIT_FORM_SED_APP_INIT);
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
					CHolidayRecord holidayRecord = (CHolidayRecord) form.getModelObject();
					if (isEditMode) {
						holidayService.modify(holidayRecord);
					} else {
						holidayService.add(holidayRecord);
					}

					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
					target.add(tabTitle);

					// update formulara
					entityID = null;
					CHolidayContentPanel.this.updateModel();
					setModeDetail(false);
					target.add(CHolidayContentPanel.this);

					// prepnutie tabu
					target.appendJavaScript("$('#tab-1').click()");

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript(MAIN_CHECK_INIT_FORM_SED_APP_INIT);

					// refresh tabulky
					target.add(panelToRefreshAfterSumit);
					target.add(getFeedbackPanel());
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CHolidayContentPanel.this);
					onError(target, form);
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

		CFeedbackPanel feedbackPanel = new CFeedbackPanel("feedback");
		registerFeedbackPanel(feedbackPanel);
		form.setFeedbackPanel(feedbackPanel);
		form.add(feedbackPanel);
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
				this.holiday = holidayService.getDetail(entityID);

			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			this.holiday = new CHolidayRecord();
		}

		form.setDefaultModelObject(holiday);

	}

	public void setModeDetail(boolean isModeDetail) {
		if (isModeDetail) {
			// detail - linkEdit show and enable
			linkEdit.setEnabled(true);
			linkEdit.add(AttributeModifier.replace("class", "btn btn-green"));
			linkEdit.setVisible(true);
		} else {
			if (isEditMode) {
				// edit - linkEdit show and disable
				linkEdit.setEnabled(false);
				linkEdit.add(AttributeModifier.append("class", "disabled"));
				linkEdit.setVisible(true);
			} else {
				// add - linkEdit hide
				linkEdit.setVisible(false);
			}
		}
		saveButton.setVisible(!isModeDetail);
		form.setModeDetail(isModeDetail);
	}

	public void setPanelToRefresh(CHolidayTablePanel panelToRefreshAfterSumit) {
		this.panelToRefreshAfterSumit = panelToRefreshAfterSumit;
	}

	public void clearFeedbackMessages() {
		this.form.clearFeedbackMessages();
	}
}
