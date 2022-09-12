package sk.qbsw.sed.framework.generator;

import java.io.IOException;

import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;

/**
 * 
 * @author lobb
 *
 */
public abstract class AAbstractGenerator implements IGenerator {

	protected abstract void doInitialize(CGenerateReportRequest request) throws IOException;

	protected abstract void doInitialize(CGenerateEmployeesReportRequest request, boolean isEmployeesReport) throws IOException;
}
