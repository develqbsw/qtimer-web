package sk.qbsw.sed.panel.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.file.CFileUploadField;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.userdetail.CUserDetailForm;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /org/structure SubPage title: Organizačná štruktúra
 *
 * Panel CUserContentPanel - Pridať / Detail / Editovať
 */
public class CUserContentPanel extends CPanel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private CUserDetailRecord user;

	@SpringBean
	private IUserClientService userService;

	private Long entityID;

	private CompoundPropertyModel<CUserDetailRecord> userModel;

	private CUserDetailForm<CUserDetailRecord> form;

	private AjaxFallbackLink<Object> linkEdit;

	private AjaxFallbackButton saveButton;

	private boolean isEditMode;

	private final Label tabTitle;

	private COrgStructureTreePanel panelToRefreshAfterSumit;

	public CUserContentPanel(String id, Label tabTitle) {
		super(id);
		this.tabTitle = tabTitle;
		if (entityID != null) {
			try {
				this.user = userService.getUserDetails(entityID);
				form.setModeDetail(true);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			this.user = new CUserDetailRecord();
			setPredefinedValuesForAddingUser(user);
		}
	}

	public void changeUser(AjaxRequestTarget target, CViewOrganizationTreeNodeRecord object) {
		setModeDetail(true);
		this.entityID = object.getUserId();
		updateModel();
		userModel.setObject(user);
		form.getImage().setUrlForUser(user.getPhotoId());// nastav spravnu url pre zobrazovaneho usera
		target.add(form);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		isEditMode = entityID != null;

		registerFeedbackPanel(new CFeedbackPanel("feedbackForPanel"));
		add(getFeedbackPanel());

		userModel = new CompoundPropertyModel<>(user);

		form = new CUserDetailForm<>("userForm", userModel, isEditMode, true);
		form.setOutputMarkupId(true);
		add(form);

		form.add(getSuperiorDropdown()); // dropdown na vyber nadriadeneho

		linkEdit = new AjaxFallbackLink<Object>("edit") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.edit"));
				target.add(tabTitle);

				isEditMode = true;
				setModeDetail(false);
				target.add(form);

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");
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
				CUserContentPanel.this.updateModel();
				setModeDetail(false);
				target.add(form);
				form.clearFeedbackMessages();
				target.add(CUserContentPanel.this.getFeedbackPanel());

				// prepnutie tabu
				target.appendJavaScript("$('#tab-1').click()");

				// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
				target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");
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
					CUserDetailRecord userRecord = (CUserDetailRecord) form.getModelObject();
					userRecord.setIsMain(false);
					
					// nacitanie suboru z formy
					Byte[] imageAsBytes = getImageAsBytes(form);
					Boolean imageIsSet = imageAsBytes != null;

					if (imageIsSet) { // ak bola vybrana nova fotka
						userRecord.setPhoto(imageAsBytes);
					} else {
						userRecord.setPhoto(null); // aby som vedel na serveri rozlisit, ze nebola zmenena fotka
					}

					if (isEditMode) {
						userService.modify(userRecord);

						if (userRecord.getJiraTokenGeneration() != null) {
							CSedSession.get().getUser().setJiraTokenGeneration(userRecord.getJiraTokenGeneration());
						}

					} else {
						userService.add(userRecord);
					}

					// update nadpisu
					tabTitle.setDefaultModelObject(CStringResourceReader.read("tabTitle.new"));
					target.add(tabTitle);

					// update formulara
					entityID = null;
					CUserContentPanel.this.updateModel();
					target.add(form);

					// prepnutie tabu
					target.appendJavaScript("$('#tab-1').click()");

					// spustenie javascripts, aby fungoval custom checkbox, select2, datepicker atd...
					target.appendJavaScript("Main.runCustomCheck();FormElements.init();SedApp.init();");

					// refresh tabulky
					target.add(panelToRefreshAfterSumit);
					target.add(CUserContentPanel.this.getFeedbackPanel());

				} catch (CBussinessDataException e) { // User is not in domain
				
					CBussinessDataExceptionProcessor.process(e, target, CUserContentPanel.this);

					target.appendJavaScript("afterError(); scrollToError();");
					target.add(form);

				} catch (CBusinessException e) { // Invalid file type
				
					getFeedbackPanel().error(CStringResourceReader.read("upload.file_invalid_type"));
					Logger.getLogger(CUserContentPanel.class).info(e.getMessage(), e);

					target.appendJavaScript("afterError(); scrollToError();");
					target.add(form);
				}
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("afterError(); scrollToError();");

				if (((CUserDetailForm) form).isFileTooBig()) {// FileTooLargeException
					target.add(((CUserDetailForm) form).getFeedbackPanel());
				} else { // ine validacie
					target.add(form);
				}

				((CUserDetailForm) form).setFileTooBig(false);
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
	}

	private CDropDownChoice<CCodeListRecord> getSuperiorDropdown() {
		List<CCodeListRecord> allEmployees = new ArrayList<>();
		try {
			allEmployees = userService.getAllValidEmployees();
		} catch (CBussinessDataException e) {
			Logger.getLogger(CUserContentPanel.class).info(e.getMessage(), e);
			CUserContentPanel.this.getFeedbackPanel().error(CStringResourceReader.read("userDetail.superior"));
		}

		CDropDownChoice<CCodeListRecord> superiorField = new CDropDownChoice<CCodeListRecord>("superiorIdField", allEmployees) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !isEditMode;
			}
		};
		superiorField.setNullValid(true);
		return superiorField;
	}

	private void updateModel() {

		isEditMode = entityID != null;
		if (entityID != null) {
			try {
				this.user = userService.getUserDetails(entityID);
				form.setModeDetail(true);
			} catch (CBussinessDataException e) {
				CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			}
		} else {
			this.user = new CUserDetailRecord();
			setPredefinedValuesForAddingUser(user);
		}
		form.setDefaultModelObject(user);
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

	public void setPanelToRefresh(COrgStructureTreePanel panel) {
		this.panelToRefreshAfterSumit = panel;
	}

	private void setPredefinedValuesForAddingUser(CUserDetailRecord user) {
		user.setIsValid(true);
		user.setAbsentCheck(true);
		user.setUserType(IUserTypeCode.ID_EMPLOYEE);
		user.setUserTypeString("Zamestnanec");
		user.setLanguage(ILanguageConstant.ID_SK);
		user.setTableRows(10);
		// priznak generovania tokenu pre JIRU nastavím na true
		user.setJiraTokenGeneration(true); 
		
		if (form != null && form.getImage() != null) {
			form.getImage().setUrlForUser(null);
		}
	}

	public void clearFeedbackMessages() {
		this.form.clearFeedbackMessages();
	}
}
