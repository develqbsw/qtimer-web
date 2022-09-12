package sk.qbsw.sed.panel.pingeneration;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.communication.service.IPinCodeGeneratorClientService;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CWicketUtils;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /pinGeneration SubPage title: Generovanie PIN-u
 * 
 * Panel PinGenerationPanel
 */
public class CPinGenerationPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private Form<Void> form;

	@SpringBean
	private IUserClientService userService;

	@SpringBean
	private IPinCodeGeneratorClientService pinCodeGenerator;

	public CPinGenerationPanel(String id) {
		super(id);

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		form = new CStatelessForm<>("pinGenForm");
		form.setOutputMarkupId(true);
		add(form);

		Model<String> userModel = Model.of("");
		userModel.setObject(CSedSession.get().getUser().getName());
		CTextField<String> userField = new CTextField<>("user", userModel, EDataType.TEXT);
		userField.setEnabled(false);
		form.add(userField);

		final Model<String> loginModel = Model.of("");
		loginModel.setObject(CSedSession.get().getUser().getLogin());
		CTextField<String> loginField = new CTextField<>("login", loginModel, EDataType.TEXT);
		loginField.setEnabled(false);
		form.add(loginField);

		final Model<String> pinModel = Model.of("");
		final CTextField<String> pinField = new CTextField<>("pin", pinModel, EDataType.TEXT);
		pinField.setEnabled(false);
		pinField.setOutputMarkupId(true);
		form.add(pinField);

		AjaxFallbackLink<Object> generateButton = new AjaxFallbackLink<Object>("generate") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// generate
				try {
					pinModel.setObject(pinCodeGenerator.getGeneratedPin(CSedSession.get().getUser().getLogin(), null));
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CPinGenerationPanel.this);
				}
				target.add(pinField);
			}
		};
		form.add(generateButton);

		AjaxFallbackButton saveButton = new AjaxFallbackButton("submitBtn", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {

				try {
					String newPin = pinModel.getObject();
					if (newPin != null && !newPin.equals("")) {
						userService.changePin(loginModel.getObject(), newPin);
						CPinGenerationPanel.this.success(CStringResourceReader.read("codeChange.pin.success"));
					} else {
						CPinGenerationPanel.this.error(CStringResourceReader.read("PIN_ALREADY_IN"));
					}

					CWicketUtils.refreshFeedback(target, CPinGenerationPanel.this);

					target.add(form);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CPinGenerationPanel.this);
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
	}
}
