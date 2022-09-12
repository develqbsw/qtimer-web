package sk.qbsw.sed.panel.timesheet.editable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.response.CGenerateReportResponseContent;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.client.ui.screen.report.IReportConstants;
import sk.qbsw.sed.communication.service.IGenerateReportClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.AJAXDownload;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.web.ui.CSedSession;

public class CTimesheetGenerateReportButtonPanel extends CPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IGenerateReportClientService generateReportClientService;

	private CGenerateReportResponseContent resp;

	public CTimesheetGenerateReportButtonPanel(String id, final CSubrodinateTimeStampBrwFilterCriteria viewTimeStampFilter, final Panel feedbackPanel) {
		super(id);

		setOutputMarkupId(true);
		this.add(new AttributeAppender("title", getString("common.button.exportWorksheet")));

		final WebMarkupContainer employeeReportButtons = new WebMarkupContainer("employeeReportButtons");

		final AJAXDownload download = new AJAXDownload() {
			private static final long serialVersionUID = 1L;

			@Override
			protected IResourceStream getResourceStream() {
				return createResourceStream(resp.getByteArray());
			}

			@Override
			protected String getFileName() {
				return resp.getFileName();
			}
		};
		employeeReportButtons.add(download);

		AjaxLink<Void> monthEmployeeReport = new AjaxLink<Void>("monthEmployeeReport") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (viewTimeStampFilter.getEmplyees().size() > 1) {
					feedbackPanel.error(CStringResourceReader.read("report.month.only_for_single_employee"));
				} else if (viewTimeStampFilter.getEmplyees().size() == 0) {
					feedbackPanel.error(CStringResourceReader.read("report.no_employee_selected"));
				} else {
					generateReport(viewTimeStampFilter, feedbackPanel, IReportConstants.MONTH_EMPLOYEE_REPORT, false);
					download.initiate(target);
					target.add(employeeReportButtons);
				}
				target.add(feedbackPanel);
			}
		};

		AjaxLink<Void> monthEmployeeReportAlsoNotConfirmed = new AjaxLink<Void>("monthEmployeeReport.alsoNotConfirmed") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (viewTimeStampFilter.getEmplyees().size() > 1) {
					feedbackPanel.error(CStringResourceReader.read("report.month.only_for_single_employee"));
				} else if (viewTimeStampFilter.getEmplyees().size() == 0) {
					feedbackPanel.error(CStringResourceReader.read("report.no_employee_selected"));
				} else {
					generateReport(viewTimeStampFilter, feedbackPanel, IReportConstants.MONTH_EMPLOYEE_REPORT, true);
					download.initiate(target);
					target.add(employeeReportButtons);
				}
				target.add(feedbackPanel);
			}
		};

		AjaxLink<Void> weekEmployeeReport = new AjaxLink<Void>("weekEmployeeReport") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (viewTimeStampFilter.getEmplyees().size() == 0) {
					feedbackPanel.error(CStringResourceReader.read("report.no_employee_selected"));
				} else {
					generateReport(viewTimeStampFilter, feedbackPanel, IReportConstants.BASIC_WEEKLY_REPORT, false);
					download.initiate(target);
					target.add(employeeReportButtons);
				}
				target.add(feedbackPanel);
			}
		};

		AjaxLink<Void> weekEmployeeReportAlsoNotConfirmed = new AjaxLink<Void>("weekEmployeeReport.alsoNotConfirmed") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (viewTimeStampFilter.getEmplyees().size() == 0) {
					feedbackPanel.error(CStringResourceReader.read("report.no_employee_selected"));
				} else {
					generateReport(viewTimeStampFilter, feedbackPanel, IReportConstants.BASIC_WEEKLY_REPORT, true);
					download.initiate(target);
					target.add(employeeReportButtons);
				}
				target.add(feedbackPanel);
			}
		};

		AjaxLink<Void> accountantReport = new AjaxLink<Void>("accountantReport") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (viewTimeStampFilter.getEmplyees().size() == 0) {
					feedbackPanel.error(CStringResourceReader.read("report.no_employee_selected"));
				} else {
					generateReport(viewTimeStampFilter, feedbackPanel, IReportConstants.MONTH_ACCOUNTANT_REPORT, false);
					download.initiate(target);
					target.add(employeeReportButtons);
				}
				target.add(feedbackPanel);
			}

			@Override
			public boolean isVisible() {
				return IUserTypeCode.ORG_ADMIN.equals(CSedSession.get().getUser().getRoleCode());
			}
		};

		AjaxLink<Void> accountantReportAlsoNotConfirmed = new AjaxLink<Void>("accountantReport.alsoNotConfirmed") {
			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (viewTimeStampFilter.getEmplyees().size() == 0) {
					feedbackPanel.error(CStringResourceReader.read("report.no_employee_selected"));
				} else {
					generateReport(viewTimeStampFilter, feedbackPanel, IReportConstants.MONTH_ACCOUNTANT_REPORT, true);
					download.initiate(target);
					target.add(employeeReportButtons);
				}
				target.add(feedbackPanel);
			}

			@Override
			public boolean isVisible() {
				return IUserTypeCode.ORG_ADMIN.equals(CSedSession.get().getUser().getRoleCode());
			}
		};

		employeeReportButtons.setOutputMarkupId(true);
		employeeReportButtons.add(monthEmployeeReport);
		employeeReportButtons.add(monthEmployeeReportAlsoNotConfirmed);
		employeeReportButtons.add(weekEmployeeReport);
		employeeReportButtons.add(weekEmployeeReportAlsoNotConfirmed);
		employeeReportButtons.add(accountantReport);
		employeeReportButtons.add(accountantReportAlsoNotConfirmed);
		this.add(employeeReportButtons);
	}

	private void generateReport(final CSubrodinateTimeStampBrwFilterCriteria viewTimeStampFilter, final Panel feedbackPanel, Long reportType, boolean alsoNotConfirmed) {
		try {
			resp = generateReportClientService.generate(CGenerateReportUtils.getParams(reportType, viewTimeStampFilter, CSedSession.get().getUser(), alsoNotConfirmed));
		} catch (CBussinessDataException e) {
			feedbackPanel.error(CStringResourceReader.read(e.getModel().getServerCode()));
			Logger.getLogger(CTimesheetGenerateReportButtonPanel.class).error(e);
		}
	}

	private IResourceStream createResourceStream(final byte[] byteArray) {

		IResourceStream resourceStream = new AbstractResourceStream() {
			private static final long serialVersionUID = 1L;

			InputStream inStream;

			@Override
			public String getContentType() {
				return "application/vnd.ms-excel";
			}

			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException {
				inStream = new ByteArrayInputStream(byteArray);
				return inStream;
			}

			@Override
			public void close() throws IOException {
				inStream.close();
			}
		};

		return resourceStream;
	}
}
