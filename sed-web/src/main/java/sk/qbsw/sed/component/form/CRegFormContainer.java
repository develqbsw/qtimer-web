package sk.qbsw.sed.component.form;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationFormRecord;
import sk.qbsw.sed.communication.service.IRegistrationClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CPasswordTextField;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

public class CRegFormContainer extends CPanel {

	private static final long serialVersionUID = 1L;

	CompoundPropertyModel<CRegistrationFormRecord> regModel = new CompoundPropertyModel<>(new CRegistrationFormRecord());
	private CStatelessForm<CRegistrationFormRecord> form = new CStatelessForm<>("regForm", regModel);

	@SpringBean
	private IRegistrationClientService registrationService;

	private CFeedbackPanel relatedFeedbackPanel;

	public CRegFormContainer(List<CCodeListRecord> validRecords) {
		super("cRegFormContainer");
		init(validRecords);
	}

	private void init(List<CCodeListRecord> validRecords) {
		this.setOutputMarkupId(true);

		CTextField<String> orgName;
		CTextField<String> streetName;
		CTextField<String> streetNum;
		CTextField<String> city;
		CTextField<String> zip;
		CTextField<String> name;
		CTextField<String> surname;
		CTextField<String> nick;
		EmailTextField email;
		CPasswordTextField pass;
		CPasswordTextField passCheck;
		CDropDownChoice<CCodeListRecord> legacyForm;
		CheckBox agreeLicence;
		CheckBox agreeQBSW;
		AjaxButton submitRegBtn;

		final CFeedbackPanel regFeedback = new CFeedbackPanel("regFeedback");
		regFeedback.setOutputMarkupId(true);
		regFeedback.setMaxMessages(1);
		form.setFeedbackPanel(regFeedback);
		registerFeedbackPanel(regFeedback);

		orgName = new CTextField<>("orgName", EDataType.TEXT, 100);
		orgName.setRequired(true);
		orgName.add(new CPlaceholderBehaviour(getString("page.register.content.field.organizationName")));

		legacyForm = new CDropDownChoice<>("legacyForm", validRecords);
		legacyForm.setRequired(true);

		streetName = new CTextField<>("street", EDataType.TEXT, 100);
		streetName.setRequired(true);
		streetName.add(new CPlaceholderBehaviour(getString("page.register.content.field.street")));

		streetNum = new CTextField<>("streetNo", EDataType.TEXT, 10);
		streetNum.setRequired(true);
		streetNum.add(new CPlaceholderBehaviour(getString("page.register.content.field.streetNum")));

		city = new CTextField<>("city", EDataType.TEXT, 50);
		city.setRequired(true);
		city.add(new CPlaceholderBehaviour(getString("page.register.content.field.city")));

		zip = new CTextField<>("zip", EDataType.TEXT, 10);
		zip.setRequired(true);
		zip.add(new CPlaceholderBehaviour(getString("page.register.content.field.zip")));

		email = new EmailTextField("email");
		email.setRequired(true);
		email.add(new CPlaceholderBehaviour(getString("page.register.content.field.email")));
		email.add(EmailAddressValidator.getInstance());
		email.add((IValidator<String>) StringValidator.maximumLength((int) 50));

		pass = new CPasswordTextField("password");
		pass.setRequired(true);
		pass.add(new CPlaceholderBehaviour(getString("page.register.content.field.pass")));
		pass.add((IValidator<String>) StringValidator.maximumLength((int) 100));
		pass.add(new PasswordValidator());

		passCheck = new CPasswordTextField("repeatedPass");
		passCheck.setRequired(true);
		passCheck.add(new CPlaceholderBehaviour(getString("page.register.content.field.passCheck")));
		passCheck.add((IValidator<String>) StringValidator.maximumLength((int) 100));
		passCheck.add(new PasswordValidator());

		name = new CTextField<>("name", EDataType.TEXT, 50);
		name.setRequired(true);
		name.add(new CPlaceholderBehaviour(getString("page.register.content.field.name")));

		surname = new CTextField<>("surname", EDataType.TEXT, 50);
		surname.setRequired(true);
		surname.add(new CPlaceholderBehaviour(getString("page.register.content.field.surname")));

		nick = new CTextField<>("login", EDataType.TEXT, 20);
		nick.setRequired(true);
		nick.add(new CPlaceholderBehaviour(getString("page.register.content.field.nick")));
		nick.add(StringValidator.lengthBetween(2, 20));

		agreeLicence = new CheckBox("agreeLicence");
		agreeLicence.setRequired(true);

		agreeQBSW = new CheckBox("agreeQBSW");
		agreeQBSW.setRequired(true);

		pass.add(new ValueMatchValidator(passCheck, false, "CregFormContainer.ValueMatchValidator.pass-do-not-match-repeatedPass"));
		passCheck.add(new ValueMatchValidator(pass, false, "CregFormContainer.ValueMatchValidator.pass-do-not-match-repeatedPass"));
		pass.add(new ValueMatchValidator(nick, true, "CregFormContainer.ValueMatchValidator.login-and-pass-cannot-match"));
		nick.add(new ValueMatchValidator(pass, true, "CregFormContainer.ValueMatchValidator.login-and-pass-cannot-match"));

		submitRegBtn = new AjaxButton("submitRegBtn", form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				CRegistrationFormRecord model = (CRegistrationFormRecord) form.getModelObject();

				if (!model.getAgreeLicence()) {

					addError(CStringResourceReader.read("registration.accept_licence"), target, form);

				} else if (!model.getAgreeQBSW()) {

					addError(CStringResourceReader.read("registration.accept_processing"), target, form);

				} else if (model.getPassword().equals(model.getLogin()) || !(model.getPassword().matches("^(.{0,}[A-Z].{0,})$") && (model.getPassword().matches("^(.{0,}[a-z].{0,})$")))) {

					addError(CStringResourceReader.read("registration.bad_password_format"), target, form);

				} else {

					model.setUserType(IUserTypes.ORG_MAN);
					model.setIsValid(Boolean.TRUE);
					model.setIsMain(Boolean.TRUE);
					model.setCountry("");
					CCodeListRecord val = new CCodeListRecord();
					val.setId(model.getLegalForm());
					model.setLegacyForm(val);

					try {
						registrationService.register(model.getClientRecord(), model.getUserRecord());
						if (relatedFeedbackPanel == null) {
							this.info(CStringResourceReader.read("registration.successfull"));
						} else {
							relatedFeedbackPanel.setMaxMessages(1);
							relatedFeedbackPanel.info(CStringResourceReader.read("registration.successfull"));
							target.add(relatedFeedbackPanel.getPage());
						}
						form.setDefaultModelObject(new CRegistrationFormRecord());
						target.add(form);
					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
						regFeedback.error(getString(e.getModel().getServerCode()));
						target.add(form);
					}
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("validate();Main.runCustomCheck();Login.runLoginButtons();");
				target.add(form);
			}

			private void addError(String error, AjaxRequestTarget target, Form<?> form) {
				this.error(error);
				target.appendJavaScript("validate();Main.runCustomCheck();Login.runLoginButtons();");
				target.add(form);
			}
		};
		submitRegBtn.setDefaultFormProcessing(true);

		form.setOutputMarkupId(true);
		form.add(regFeedback);
		form.add(orgName);
		form.add(legacyForm);
		form.add(streetName);
		form.add(streetNum);
		form.add(city);
		form.add(zip);
		form.add(email);
		form.add(pass);
		form.add(passCheck);
		form.add(name);
		form.add(surname);
		form.add(nick);
		form.add(agreeLicence);
		form.add(agreeQBSW);
		form.add(submitRegBtn);

		add(form);
	}

	public void setRelatedFeedbackPanel(CFeedbackPanel feedbackPanel) {
		this.relatedFeedbackPanel = feedbackPanel;
	}

	private class PasswordValidator implements IValidator<String> {

		private static final long serialVersionUID = 1L;

		@Override
		public void validate(IValidatable<String> validatable) {

			final String password = validatable.getValue();

			if (!(password.matches("^(.{0,}[A-Z].{0,})$") && (password.matches("^(.{0,}[a-z].{0,})$")))) {
				error(validatable, "CregFormContainer.PasswordValidator.pass-do-not-match-pattern");
			}
		}

		private void error(IValidatable<String> validatable, String errorKey) {

			ValidationError error = new ValidationError();
			error.addKey(errorKey);
			validatable.error(error);
		}
	}

	private class ValueMatchValidator implements IValidator<String> {

		private static final long serialVersionUID = 1L;

		TextField<String> compToCompare;
		Boolean occursIfMatch;
		String errorProperty;

		public ValueMatchValidator(TextField<String> compToCompare, Boolean occursIfMatch, String errorProperty) {
			super();
			this.compToCompare = compToCompare;
			this.occursIfMatch = occursIfMatch;
			this.errorProperty = errorProperty;
		}

		@Override
		public void validate(IValidatable<String> validatable) {

			final String value = validatable.getValue();

			if (occursIfMatch) {
				if (value.equals(compToCompare.getValue())) {
					error(validatable, errorProperty);
				}
			} else {
				if (!value.equals(compToCompare.getValue())) {
					error(validatable, errorProperty);
				}
			}
		}

		private void error(IValidatable<String> validatable, String errorKey) {

			ValidationError error = new ValidationError();
			error.addKey(errorKey);
			validatable.error(error);
		}
	}
}
