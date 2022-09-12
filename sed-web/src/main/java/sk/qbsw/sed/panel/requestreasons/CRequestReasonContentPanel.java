package sk.qbsw.sed.panel.requestreasons;

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

import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.communication.service.IRequestReasonClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /requestReasons SubPage title: Dôvody pre žiadosti
 * 
 * RequestReasonContentPanel - Pridať / Detail / Editovať
 */
public class CRequestReasonContentPanel extends CPanel {

	/** serial uid */
	private static final long serialVersionUID = 1L;
	
	private static final String MAIN_CHECK_INIT_FORM_SED_APP_INIT = "Main.runCustomCheck();FormElements.init();SedApp.init();";

	/** input timesheet */
	private CRequestReasonData requestReason;

	@SpringBean
	private IRequestReasonClientService requestReasonService;

	private Long entityID;

	private CompoundPropertyModel<CRequestReasonData> requestReasonModel;

	private CRequestReasonForm<CRequestReasonData> form;

	private boolean isEditMode;

	private CRequestReasonTablePanel panelToRefreshAfterSumit;

	private final Label tabTitle;

	private AjaxFallbackButton saveButton;

	private AjaxFallbackLink<Object> linkEdit;

	public CRequestReasonContentPanel(String id, Label tabTitle) {
		super(id);
		this.tabTitle = tabTitle;

		if (entityID != null) {
			try {
				this.requestReason = requestReasonService.getDetail(entityID);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			this.requestReason = new CRequestReasonData();
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		isEditMode = entityID != null;
		requestReasonModel = new CompoundPropertyModel<>(requestReason);

		form = new CRequestReasonForm<>("requestReasonForm", requestReasonModel, this, isEditMode);
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
				CRequestReasonContentPanel.this.updateModel();
				setModeDetail(false);
				target.add(CRequestReasonContentPanel.this);
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
					CRequestReasonData requestReasonData = (CRequestReasonData) form.getModelObject();

					requestReasonService.save(requestReasonData);

					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
					target.add(tabTitle);

					// update formulara
					entityID = null;
					CRequestReasonContentPanel.this.updateModel();
					setModeDetail(false);
					target.add(CRequestReasonContentPanel.this);

					// prepnutie tabu
					target.appendJavaScript("$('#tab-1').click()");

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript(MAIN_CHECK_INIT_FORM_SED_APP_INIT);

					// refresh tabulky
					target.add(panelToRefreshAfterSumit);
					target.add(getFeedbackPanel());
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CRequestReasonContentPanel.this);
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
				this.requestReason = requestReasonService.getDetail(entityID);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			this.requestReason = new CRequestReasonData();
		}

		form.setDefaultModelObject(requestReason);
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

	public void setPanelToRefresh(CRequestReasonTablePanel panelToRefreshAfterSumit) {
		this.panelToRefreshAfterSumit = panelToRefreshAfterSumit;
	}

	public void clearFeedbackMessages() {
		this.form.clearFeedbackMessages();
	}
}
