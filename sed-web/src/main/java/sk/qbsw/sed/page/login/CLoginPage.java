package sk.qbsw.sed.page.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.model.ILanguageConstant;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.communication.service.ILegalFormClientService;
import sk.qbsw.sed.component.behaviour.CPlaceholderBehaviour;
import sk.qbsw.sed.component.form.CForgotFormContainer;
import sk.qbsw.sed.component.form.CLicenceContainer;
import sk.qbsw.sed.component.form.CRegFormContainer;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CPasswordTextField;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.page.ANonAuthenticatedPage;
import sk.qbsw.sed.model.CLoginModel;
import sk.qbsw.sed.model.CSystemSettings;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /login SubPage title: Prihláste sa na svoj účet
 */
@MountPath(CLoginPage.PATH_SEGMENT)
public class CLoginPage extends ANonAuthenticatedPage {

	@SpringBean
	private ILegalFormClientService legalFormService;

	@SpringBean
	private CSystemSettings settings;

	public static final String PATH_SEGMENT = "login";
	private static final long serialVersionUID = 1L;
	private CStatelessForm<CLoginModel> form;
	private CTextField<String> username;
	private CPasswordTextField password;
	private AjaxButton submitBtn;

	private boolean allowAction = true;

	public CLoginPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CSedSession.get().setSlovakLocale();

		form = initializeForm();
		add(form);

		List<CCodeListRecord> validRecords = new ArrayList<>();
		try {
			validRecords = legalFormService.getValidRecords();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}

		CRegFormContainer regForm = new CRegFormContainer(validRecords);
		regForm.setRelatedFeedbackPanel(form.getFeedbackPanel());
		regForm.setOutputMarkupId(true);
		add(regForm);

		CForgotFormContainer forgotForm = new CForgotFormContainer();
		forgotForm.setRelatedFeedbackPanel(form.getFeedbackPanel());
		forgotForm.setOutputMarkupId(true);
		add(forgotForm);

		CLicenceContainer licencePanel = new CLicenceContainer();
		licencePanel.setOutputMarkupId(true);
		add(licencePanel);

		Label versionLabel = new Label("versionLabel", settings.getSystemVersion());
		add(versionLabel);
	}

	private CStatelessForm<CLoginModel> initializeForm() {
		form = new CStatelessForm<>("loginForm", new CompoundPropertyModel<CLoginModel>(new CLoginModel()), false);
		form.setOutputMarkupId(true);
		username = new CTextField<>("username", EDataType.TEXT, 100);
		username.setRequired(true);
		username.add(new CPlaceholderBehaviour(getString("page.login.content.field.username")));
		username.add(new AttributeModifier("autofocus", ""));
		password = new CPasswordTextField("password");
		password.setRequired(true);
		password.add(new CPlaceholderBehaviour(getString("page.login.content.field.password")));

		final CheckBox staySignedIn = new CheckBox("staySignedIn");

		final CFeedbackPanel feedback = new CFeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		form.setFeedbackPanel(feedback);
		form.add(feedback);

		submitBtn = new AjaxButton("submitBtn", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form2) {
				if (allowAction) {
					allowAction = false;

					CLoginModel model = (CLoginModel) form.getModelObject();

					try {
						CSedSession.get().signIn(model.getUsername(), model.getPassword(), model.isStaySignedIn());
						setResponsePage(CSedSession.get().getApplication().getHomePage());
					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, getSession(), target, getPage());
						target.appendJavaScript("validate();Main.runCustomCheck();Login.runLoginButtons();");
						target.add(form);
						allowAction = true;
					}
				}
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form2) {
				super.onError(target, form);
				target.appendJavaScript("validate();Main.runCustomCheck();Login.runLoginButtons();");
				target.add(form);
				allowAction = true;
			}
		};
		submitBtn.setDefaultFormProcessing(true);
		form.setDefaultButton(submitBtn);

		Link<Object> changeLanguage = new Link<Object>("changeLanguage") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				if (allowAction) {
					if (ILanguageConstant.SK.equals(CSedSession.get().getLocale().getLanguage())) {
						CSedSession.get().setLocale(new Locale(ILanguageConstant.EN));
					} else {
						CSedSession.get().setLocale(new Locale(ILanguageConstant.SK));
					}
					setResponsePage(CLoginPage.class);
				}
			}
		};

		form.add(username);
		form.add(password);
		form.add(staySignedIn);
		form.add(submitBtn);
		form.add(changeLanguage);

		return form;
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
