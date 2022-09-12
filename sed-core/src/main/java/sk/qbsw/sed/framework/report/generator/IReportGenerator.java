package sk.qbsw.sed.framework.report.generator;

import java.io.OutputStream;

import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.framework.generator.IGenerator;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;

public interface IReportGenerator extends IGenerator {

	public static final String DEFAULT_REPORT_TEMPLATE_STORAGE = "/reports/templates/";

	public static final String PARAM_REPORT_TYPE = "reportType";

	public CReportOutputModel generateReport(OutputStream outputStream, CGenerateReportRequest request) throws Exception;

	public CReportOutputModel generateEmployeesReport(OutputStream outputStream, CGenerateEmployeesReportRequest request) throws Exception;

	public CReportOutputModel generateWorkplaceReport(OutputStream outputStream, CGenerateEmployeesReportRequest request) throws Exception;
}
