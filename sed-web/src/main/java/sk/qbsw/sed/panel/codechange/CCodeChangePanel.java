package sk.qbsw.sed.panel.codechange;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import sk.qbsw.sed.client.model.codelist.CCodeChangeModel;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.CStatelessForm;
import sk.qbsw.sed.fw.component.form.input.CDropDownChoice;
import sk.qbsw.sed.fw.component.form.input.CLabel;
import sk.qbsw.sed.fw.component.form.input.CTextField;
import sk.qbsw.sed.fw.component.validator.COnlyNumbersValidator;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CWicketUtils;

/**
 * SubPage: /cardCodeChange SubPage title: Zmena kódu karty
 * 
 * Panel CodeChangePanel
 */
public class CCodeChangePanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private Form<CCodeChangeModel> form;

	@SpringBean
	private IUserClientService userService;

	public CCodeChangePanel(String id, final String code) {
		super(id);

		setOutputMarkupId(true);

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		form = new CStatelessForm<>("codeChangeForm", new CompoundPropertyModel<>(new CCodeChangeModel()));
		form.setOutputMarkupId(true);

		List<CCodeListRecord> employeeList = null;
		try {
			employeeList = userService.getAllValidEmployees();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			error(getString(e.getModel().getServerCode()));
		}

		CDropDownChoice<CCodeListRecord> user = new CDropDownChoice<>("user", employeeList);
		user.setNullValid(true);
		user.setRequired(true);
		form.add(user);

		// label string differs (changing pin or card code)
		Model<String> strMdl = Model.of("");
		CLabel label = new CLabel("codeLabel", strMdl, EDataType.TEXT);
		if (code.equals("pin")) {
			strMdl.setObject(getString("codeChange.pin.label"));
		} else if (code.equals("card")) {
			strMdl.setObject(getString("codeChange.card.label"));
		}
		form.add(label);

		CTextField<String> codeField = new CTextField<>("code", EDataType.TEXT);
		codeField.setRequired(true);
		codeField.add(StringValidator.maximumLength(10));
		codeField.add(new COnlyNumbersValidator<>());
		form.add(codeField);

		add(form);

		AjaxFallbackButton saveButton = new AjaxFallbackButton("submitBtn", form) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {

				try {
					CCodeChangeModel codeChange = (CCodeChangeModel) form.getModelObject();
					if (code.equals("pin")) {
						userService.changePin(codeChange.getUserId(), codeChange.getCode());
						CCodeChangePanel.this.success(CStringResourceReader.read("codeChange.pin.success"));
						CWicketUtils.refreshFeedback(target, CCodeChangePanel.this);
					} else if (code.equals("card")) {
						if (isValidCardCode(codeChange.getCode())) { // if is 10 characters long
							userService.changeCardCode(codeChange.getUserId(), codeChange.getCode());
							CCodeChangePanel.this.success(CStringResourceReader.read("codeChange.card.success"));
							CWicketUtils.refreshFeedback(target, CCodeChangePanel.this);
						} else {
							CCodeChangePanel.this.error(CStringResourceReader.read("codeChange.card.error"));
							CWicketUtils.refreshFeedback(target, CCodeChangePanel.this);
						}
					}
					target.add(form);
				} catch (CBussinessDataException e) {
					CBussinessDataExceptionProcessor.process(e, target, CCodeChangePanel.this);
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

	private boolean isValidCardCode(String code) {
		if (code.length() == 10) {
			return true;
		} else {
			return false;
		}
	}
}
