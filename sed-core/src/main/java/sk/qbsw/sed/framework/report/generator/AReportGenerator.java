package sk.qbsw.sed.framework.report.generator;

import java.io.OutputStream;

import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.framework.generator.AAbstractGenerator;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;

/**
 * 
 * @author lobb
 *
 */
public abstract class AReportGenerator extends AAbstractGenerator implements IReportGenerator {

	public final CReportOutputModel generateReport(final OutputStream outputStream, CGenerateReportRequest request) throws Exception {
		this.doInitialize(request);
		return this.doGenerate(outputStream, this.getReportData(request));
	}

	protected abstract CReportOutputModel doGenerate(OutputStream outputStream, final CComplexInputReportModel complexModel) throws Exception;

	protected abstract CComplexInputReportModel getReportData(CGenerateReportRequest request) throws Exception;

	// report zamestnancov
	public final CReportOutputModel generateEmployeesReport(final OutputStream outputStream, CGenerateEmployeesReportRequest request) throws Exception {
		// true = je to report zamestnancov
		this.doInitialize(request, true);
		return this.doGenerate(outputStream, this.getReportData(request));
	}

	// report zamestnancov
	protected abstract CComplexInputReportModel getReportData(CGenerateEmployeesReportRequest request) throws Exception;

	// report pracovných miest zamestnancov
	public final CReportOutputModel generateWorkplaceReport(final OutputStream outputStream, CGenerateEmployeesReportRequest request) throws Exception {
		// false = nie je to report zamestnancov ale pracovných miest
		this.doInitialize(request, false);
		return this.doGenerate(outputStream, this.getWorkplaceReportData(request));
	}

	// report pracovných miest zamestnancov
	protected abstract CComplexInputReportModel getWorkplaceReportData(CGenerateEmployeesReportRequest request) throws Exception;
}
