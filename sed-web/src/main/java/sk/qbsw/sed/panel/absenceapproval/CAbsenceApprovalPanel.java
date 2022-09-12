package sk.qbsw.sed.panel.absenceapproval;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.codelist.CGetListOfUsersWithCorruptedSummaryReport;
import sk.qbsw.sed.client.model.params.CParameter;
import sk.qbsw.sed.client.model.params.IParameter;
import sk.qbsw.sed.communication.service.IClientParameterClientService;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.component.form.input.CLabel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /absenceApproval SubPage title: Spracovanie neprítomnosti
 * 
 * Panel AbsenceApprovalPanel
 */
public class CAbsenceApprovalPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private CAbsenceApprovalForm form;

	private CompoundPropertyModel<CGetListOfUsersWithCorruptedSummaryReport> summaryModel;

	private CLabel lastLabel;

	@SpringBean
	private IClientParameterClientService clientParameterService;

	@SpringBean
	private ITimesheetClientService timesheetService;

	public CAbsenceApprovalPanel(String id, Label pageTitleSmall) {
		super(id);
		pageTitleSmall.setDefaultModelObject(CStringResourceReader.read("absenceApproval.subtitle"));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		CFeedbackPanel errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);

		summaryModel = new CompoundPropertyModel<>(new CGetListOfUsersWithCorruptedSummaryReport());

		lastLabel = new CLabel("lastLabel", new Model<String>(), EDataType.TEXT);
		lastLabel.setOutputMarkupId(true);
		lastLabel.setDefaultModelObject(getLastTimeGenerated());
		add(lastLabel);

		form = new CAbsenceApprovalForm("absenceApprovalForm", summaryModel);
		form.setOutputMarkupId(true);

		AjaxFallbackButton button = new AjaxFallbackButton("button", form) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				CGetListOfUsersWithCorruptedSummaryReport summaryReport = (CGetListOfUsersWithCorruptedSummaryReport) form.getModelObject();

				// kontrola datumov
				if (summaryReport.getFrom().after(summaryReport.getTo())) { // datum od je neskor ako datum do
					CAbsenceApprovalPanel.this.error(CStringResourceReader.read("absenceApproval.date.invalid"));
				} else {
					try {
						timesheetService.generateApprovedEmployeesAbsenceRecords(CSedSession.get().getUser().getUserId(), summaryReport);
						lastLabel.setDefaultModelObject(getLastTimeGenerated());
						SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
						this.info(MessageFormat.format(getString("absenceApproval.success.message"), df.format(summaryReport.getFrom()), df.format(summaryReport.getTo())));
					} catch (CBussinessDataException e) {
						CBussinessDataExceptionProcessor.process(e, target, CAbsenceApprovalPanel.this);
						onError(target, form);
					}
				}
				target.add(form);
				target.add(lastLabel);
				target.add(CAbsenceApprovalPanel.this.getFeedbackPanel());
			}

			@Override
			public void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.appendJavaScript("afterError();");
				target.add(form);
			}

		};
		form.add(button);

		add(form);
	}

	private String getLastTimeGenerated() {
		String str = "";
		try {
			CParameter param = clientParameterService.getClientParameter(CSedSession.get().getUser().getClientInfo().getClientId(), IParameter.LAST_USER_ABSENCE_PROCESSING);
			if (param != null && param.getStringValue() != null) {
				str = param.getStringValue();
			}
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
		return CStringResourceReader.read("absenceApproval.last") + " " + str;
	}
}
