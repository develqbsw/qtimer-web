package sk.qbsw.sed.component.form;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

public class CForgotFormContainer extends CPanel {

	private static final long serialVersionUID = 1L;

	CompoundPropertyModel<CRegistrationUserRecord> forgotModel = new CompoundPropertyModel<>(new CRegistrationUserRecord());
	private CStatelessForm<CRegistrationUserRecord> form = new CStatelessForm<>("forgotForm", forgotModel, false);

	@SpringBean
	private IUserClientService userService;

	private CFeedbackPanel relatedFeedbackPanel;

	public CForgotFormContainer(String id) {
		super(id);
		init();
	}

	public CForgotFormContainer() {
		super("cForgotFormContainer");
		init();
	}

	private void init() {
		this.setOutputMarkupId(true);

		CTextField<String> nick;
		EmailTextField email;
		AjaxButton submitForgotBtn;
		WebMarkupContainer goBackButton;
		final CFeedbackPanel forgotFeedback = new CFeedbackPanel("forgotFeedback");

		forgotFeedback.setOutputMarkupId(true);
		forgotFeedback.setMaxMessages(1);
		form.setFeedbackPanel(forgotFeedback);
		registerFeedbackPanel(forgotFeedback);

		nick = new CTextField<>("login", EDataType.TEXT, 100);
		nick.setRequired(true);
		nick.add(new CPlaceholderBehaviour(getString("page.register.content.field.nick")));

		email = new EmailTextField("email");
		email.setRequired(true);
		email.add(new CPlaceholderBehaviour(getString("page.register.content.field.email")));
		email.add(EmailAddressValidator.getInstance());
		email.add((IValidator<String>) StringValidator.maximumLength((int) 50));

		submitForgotBtn = new AjaxButton("submitForgotBtn", form) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {

				CRegistrationUserRecord model = (CRegistrationUserRecord) form.getModelObject();

				boolean userIsNotFromDomain = model.getLogin().contains("@");
				boolean userIsOrgAdmin = model.getLogin().contains("admin");

				if (userIsNotFromDomain || userIsOrgAdmin) {
					try {

						userService.renewPassword(model.getLogin(), model.getEmail());

						if (relatedFeedbackPanel == null) {
							forgotFeedback.info(CStringResourceReader.read("security.password_renewed"));
						} else {
							relatedFeedbackPanel.setMaxMessages(1);
							relatedFeedbackPanel.info(CStringResourceReader.read("security.password_renewed"));
							target.add(relatedFeedbackPanel.getPage());
						}

						form.setDefaultModelObject(new CRegistrationUserRecord());
						target.add(form);

					} catch (CBussinessDataException e) {

						CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
						forgotFeedback.error(getString(e.getModel().getServerCode()));
						target.appendJavaScript("validate();Login.runLoginButtons();");
						target.add(form);
					}
				} else {
					forgotFeedback.warn(getString("renew_password_message"));
					target.add(forgotFeedback);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("validate();Login.runLoginButtons();");
				target.add(form);
			}
		};
		submitForgotBtn.setDefaultFormProcessing(true);

		goBackButton = new WebMarkupContainer("goBackButton");
		goBackButton.add(new AjaxEventBehavior("onmouseup") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(final AjaxRequestTarget target) {
				form.setDefaultModelObject(new CRegistrationUserRecord());
				forgotFeedback.getFeedbackMessages().clear();
				target.appendJavaScript("Login.runLoginButtons();");
				target.add(form);
			}
		});

		form.add(forgotFeedback);
		form.add(nick);
		form.add(email);
		form.add(submitForgotBtn);
		form.add(goBackButton);
		add(form);
	}

	public void setRelatedFeedbackPanel(CFeedbackPanel feedbackPanel) {
		this.relatedFeedbackPanel = feedbackPanel;
	}
}
