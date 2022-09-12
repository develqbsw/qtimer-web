package sk.qbsw.sed.server.service.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Set;

import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.client.ui.screen.report.IReportConstants;
import sk.qbsw.sed.framework.report.IReportData;
import sk.qbsw.sed.framework.report.export.stream.IXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.generator.AReportGenerator;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.transform.stream.IXLSStreamReportTransformer;
import sk.qbsw.sed.framework.util.CResourceLocator;
import sk.qbsw.sed.server.service.CTimeUtils;

@Service(value = "reportGenerator")
public class CReportGeneratorImpl extends AReportGenerator {
	
	@Autowired
	private IReportData reportDataGetter;
	
	private IXLSStreamReportExporter reportExporter;
	private IXLSStreamReportTransformer transformer;
	private Set<Long> userIds;
	private InputStream templateFile;
	private Long reportType;
	private Long clientId;
	private Boolean onlyValid;

	@Override
	protected CReportOutputModel doGenerate(final OutputStream outputStream, final CComplexInputReportModel complexModel) throws Exception {
		this.reportExporter.setXLSTransformer(this.transformer);
		return this.reportExporter.exportToStream(outputStream, this.templateFile, complexModel);
	}

	@Override
	protected void doInitialize(CGenerateReportRequest request) throws IOException {
		this.userIds = request.getUserIds();
		this.reportType = request.getReportType();
		this.templateFile = this.getTemplateForReportType();
	}

	@Override
	public CComplexInputReportModel getReportData(CGenerateReportRequest request) throws Exception {
		final Calendar dateFrom = request.getDateFrom();
		final Calendar dateTo = request.getDateTo();
		final boolean alsoNotConfirmed = request.isAlsoNotConfirmed();
		final String screenType = request.getScreenType();
		return this.reportDataGetter.getModel(this.reportType, this.userIds, CTimeUtils.convertToStartDate(dateFrom), CTimeUtils.convertToEndDate(dateTo), alsoNotConfirmed, screenType);
	}

	private InputStream getTemplateForReportType() throws IOException {
		if (null == this.reportType) {
			throw new IllegalArgumentException("Unknown report type: null");
		}
		String template;

		if (IReportConstants.BASIC_SHORTLY_REPORT.equals(this.reportType)) {
			template = "shortly_template.xls";
			this.reportExporter = new CShortlyXLSReportExporterImpl();
			this.transformer = new CShortlyReportTransformer();
		} else if (IReportConstants.BASIC_WEEKLY_REPORT.equals(this.reportType)) {
			template = "weekly_template.xls";
			this.reportExporter = new CWeeklyXLSReportExporterImpl();
			this.transformer = new CWeeklyReportTransformer();
		} else if (IReportConstants.SUMMARY_REPORT.equals(this.reportType)) {
			template = "summary_template.xls";
			this.reportExporter = new CSummaryXLSReportExporterImpl();
			this.transformer = new CSummaryReportTransformer();
		} else if (IReportConstants.MONTH_EMPLOYEE_REPORT.equals(this.reportType)) {
			template = "month_employee_template.xls";
			this.reportExporter = new CMonthEmployeeXLSReportExporterImpl();
			this.transformer = new CMonthEmployeeReportTransformer();
		} else if (IReportConstants.MONTH_ACCOUNTANT_REPORT.equals(this.reportType)) {
			template = "month_accountant_template.xls";
			this.reportExporter = new CMonthAccountantXLSReportExporterImpl();
			this.transformer = new CMonthAccountantReportTransformer();
		} else {
			throw new IllegalArgumentException("Unknown report type: " + this.reportType);
		}

		final File tempFile = File.createTempFile("report_", Long.toString(Calendar.getInstance().getTimeInMillis()));
		InputStream templateStream = null;

		try {
			templateStream = CResourceLocator.getResourceAsInputStream(DEFAULT_REPORT_TEMPLATE_STORAGE + template);
			final FileOutputStream outputStream = new FileOutputStream(tempFile);
			StreamUtils.copy(templateStream, outputStream);
		} finally {
			if (templateStream != null) {
				templateStream.close();
			}
		}

		return new FileInputStream(tempFile);
	}

	@Override
	protected CComplexInputReportModel getReportData(CGenerateEmployeesReportRequest request) throws Exception {
		return this.reportDataGetter.getModel(request.getClientId(), request.getOnlyValid());
	}

	@Override
	protected void doInitialize(CGenerateEmployeesReportRequest request, boolean isEmployeesReport) throws IOException {
		this.clientId = request.getClientId();
		this.onlyValid = request.getOnlyValid();
		this.templateFile = isEmployeesReport ? this.getTemplateForEmployeeReport() : this.getTemplateForWorkplaceReport();
	}

	private InputStream getTemplateForEmployeeReport() throws IOException {
		String template;

		template = "employees_template.xls";
		this.reportExporter = new CEmployeesXLSReportExporterImpl();
		this.transformer = new CEmployeesReportTransformer();

		final File tempFile = File.createTempFile("report_", Long.toString(Calendar.getInstance().getTimeInMillis()));
		InputStream templateStream = null;

		try {
			templateStream = CResourceLocator.getResourceAsInputStream(DEFAULT_REPORT_TEMPLATE_STORAGE + template);
			final FileOutputStream outputStream = new FileOutputStream(tempFile);
			StreamUtils.copy(templateStream, outputStream);
		} finally {
			if (templateStream != null) {
				templateStream.close();
			}
		}

		return new FileInputStream(tempFile);
	}

	private InputStream getTemplateForWorkplaceReport() throws IOException {
		String template;

		template = "employees_working_place_template.xls";
		this.reportExporter = new CWorkplaceXLSReportExporterImpl();
		this.transformer = new CWorkplaceReportTransformer();

		final File tempFile = File.createTempFile("report_", Long.toString(Calendar.getInstance().getTimeInMillis()));
		InputStream templateStream = null;

		try {
			templateStream = CResourceLocator.getResourceAsInputStream(DEFAULT_REPORT_TEMPLATE_STORAGE + template);
			final FileOutputStream outputStream = new FileOutputStream(tempFile);
			StreamUtils.copy(templateStream, outputStream);
		} finally {
			if (templateStream != null) {
				templateStream.close();
			}
		}

		return new FileInputStream(tempFile);
	}

	@Override
	protected CComplexInputReportModel getWorkplaceReportData(CGenerateEmployeesReportRequest request) throws Exception {
		return this.reportDataGetter.getWorkplaceModel(request.getClientId(), request.getOnlyValid());
	}
}
