package sk.qbsw.sed.panel.userdetail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.file.CFileUploadField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.page.userdetail.CUserDetailPage;
import sk.qbsw.sed.panel.users.CUserContentPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /userDetail SubPage title: Moje detaily
 * 
 * Panel UserDetailPanel
 */
public class CUserDetailPanel extends CPanel {

	private static final long serialVersionUID = 1L;
	private Long entityID;

	private CUserDetailForm<CUserDetailRecord> form;

	private CUserDetailRecord detailRecord;

	private CompoundPropertyModel<CUserDetailRecord> userDetailModel;

	@SpringBean
	private IUserClientService service;

	private AjaxFallbackLink<Object> linkEdit;

	private AjaxLink<Void> cancelButton;

	private AjaxFallbackButton saveButton;

	private boolean isEditMode;

	public CUserDetailPanel(String id, Long clientId) {
		super(id);
		this.entityID = clientId;
		setDetailRecord();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		this.setOutputMarkupId(true);

		registerFeedbackPanel(new CFeedbackPanel("feedbackForPanel"));
		add(getFeedbackPanel());

		userDetailModel = new CompoundPropertyModel<>(detailRecord);
		form = new CUserDetailForm<>("userDetailForm", userDetailModel, false);
		form.setModeDetail(true);
		form.setOutputMarkupId(true);
		add(form);

		linkEdit = new AjaxFallbackLink<Object>("edit") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				isEditMode = true;
				setModeDetail(false);
				target.add(form);

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");
			}
		};
		linkEdit.setVisible(true);
		linkEdit.add(new AttributeAppender("title", getString("button.edit")));
		form.add(linkEdit);

		cancelButton = new AjaxLink<Void>("backButton") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// update formulara
				CUserDetailPanel.this.updateModel();
				setModeDetail(true);
				target.add(form);
				form.clearFeedbackMessages();

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");
			}
		};
		form.add(cancelButton);
		cancelButton.setVisible(false);

		saveButton = new AjaxFallbackButton("submitBtn", form) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					// ulozenie zaznamu
					CUserDetailRecord userRecord = (CUserDetailRecord) form.getModelObject();

					Byte[] imageAsBytes = getImageAsBytes(form);
					Boolean imageIsSet = imageAsBytes != null;

					if (imageIsSet)// ak bola vybrana nova fotka
					{
						userRecord.setPhoto(imageAsBytes);
					} else {
						userRecord.setPhoto(null); // aby som vedel na serveri rozlisit, ze nebola zmenena fotka
					}

					service.modify(userRecord);

					if (imageIsSet)// ak bola vybrana nova fotka
					{
						Long userPhotoId = service.getUserDetails(userRecord.getId()).getPhotoId();
						CSedSession.get().getUser().setUserPhotoId(userPhotoId);
					}

					if (userRecord.getJiraTokenGeneration() != null) {
						CSedSession.get().getUser().setJiraTokenGeneration(userRecord.getJiraTokenGeneration());
					}
					
					CSedSession.get().getUser().setTableRows(userRecord.getTableRows());
					CSedSession.get().setLocale(new Locale(getLanguage(userRecord.getLanguage())));

					setResponsePage(CUserDetailPage.class);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CUserDetailPanel.this);
					onError(target, form);
				} catch (CBusinessException e) // Invalid file type
				{

					getFeedbackPanel().error(CStringResourceReader.read("upload.file_invalid_type"));
					Logger.getLogger(CUserContentPanel.class).info(e.getMessage(), e);

					target.appendJavaScript("afterError(); scrollToError();");
					target.add(form);
				}
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("afterError();");
				target.add(form);
			}

			private Byte[] getImageAsBytes(Form<?> form) throws CBusinessException {
				final CFileUploadField userPhotoUpload = (CFileUploadField) form.get("photoUpload");
				FileUpload photoAsFileUpload = userPhotoUpload.getFileUpload();

				Set<String> setOfImageTypes = new HashSet<>(Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp"));
				if (photoAsFileUpload != null) {
					if (setOfImageTypes.contains(photoAsFileUpload.getContentType())) {
						byte[] photoAsBytes = photoAsFileUpload.getBytes();
						return ArrayUtils.toObject(photoAsBytes);
					} else {
						throw new CBusinessException("User tried to upload photo file with invalid type. Upload was denied");
					}
				}
				return null;
			}

		};
		form.add(saveButton);
		saveButton.setVisible(false);

	}

	public void updateModel() {
		setDetailRecord();

		form.setDefaultModelObject(detailRecord);
	}

	private void setDetailRecord() {
		try {
			this.detailRecord = service.getUserDetails(entityID);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
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
		cancelButton.setVisible(!isModeDetail);
		if ("OA".equals(CSedSession.get().getUser().getRoleCode())) {
			form.setModeDetail(isModeDetail);
		} else {
			form.setModeUserEdit(!isModeDetail);
		}
	}

	private String getLanguage(Long id) {
		if (ILanguageConstant.ID_SK.equals(id)) {
			return ILanguageConstant.SK;
		} else {
			return ILanguageConstant.EN;
		}
	}
}
