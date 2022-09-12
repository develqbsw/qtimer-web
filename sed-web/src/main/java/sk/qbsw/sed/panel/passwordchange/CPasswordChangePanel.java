package sk.qbsw.sed.panel.passwordchange;

import java.util.regex.Pattern;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CPasswordChangeRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CWicketUtils;

/**
 * SubPage: /passwordChangeReception SubPage title: Zmena hesla - recepcia
 * 
 * Panel PasswordChangePanel
 */
public class CPasswordChangePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private CPasswordChangeRecord record;

	private CompoundPropertyModel<CPasswordChangeRecord> recordModel;

	private Form<CPasswordChangeRecord> form;

	@SpringBean
	private IUserClientService userService;

	public CPasswordChangePanel(String id, String name, String login, boolean isReceptionPassword) {
		super(id);

		setOutputMarkupId(true);

		record = new CPasswordChangeRecord(name, login);
		recordModel = new CompoundPropertyModel<>(record);
		form = new CPasswordChangeForm<>("passwordChangeForm", recordModel, CPasswordChangePanel.this, isReceptionPassword);
		add(form);

		AjaxFallbackButton saveButton = new AjaxFallbackButton("submitBtn", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {

				try {
					CPasswordChangeRecord newOne = (CPasswordChangeRecord) form.getModelObject();
					if (newOne.getNewPwd().equals(newOne.getNewPwd2())) { // new password and repeated password match
						if (isValidPassword(newOne.getNewPwd(), newOne.getLogin())) { // password validation
							userService.changePassword(newOne.getLogin(), newOne.getOriginalPwd(), newOne.getNewPwd());
							CPasswordChangePanel.this.success(CStringResourceReader.read("passwordchange.success"));
							CWicketUtils.refreshFeedback(target, CPasswordChangePanel.this);
						} else { // invalid password
							CPasswordChangePanel.this.error(CStringResourceReader.read("passwordchange.invalid"));
							CWicketUtils.refreshFeedback(target, CPasswordChangePanel.this);
						}
					} else { // new password and repeated password does not match
						CPasswordChangePanel.this.error(CStringResourceReader.read("passwordchange.error"));
						CWicketUtils.refreshFeedback(target, CPasswordChangePanel.this);
					}
					target.add(form);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CPasswordChangePanel.this);
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
		add(saveButton);

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}

	/**
	 * 
	 * password je korektny ak obsahuje aspon 6 znakov, jedno male a jedno velke
	 * pismeno a nesmie sa zhodovat s loginom
	 * 
	 * @param pwd
	 * @param login
	 * @return
	 */
	private boolean isValidPassword(String pwd, String login) {
		if (pwd.equalsIgnoreCase(login) || pwd.length() < 6) {
			return false;
		}
		boolean small = Pattern.matches(".*[a-z].*", pwd);
		boolean capital = Pattern.matches(".*[A-Z].*", pwd);
		return small && capital;
	}
}
