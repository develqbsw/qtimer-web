package sk.qbsw.sed.framework.report.generator.name;

import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.framework.generator.IGenerator;

public interface IReportNameGenerator extends IGenerator {

	public String generateReportName(CGenerateReportRequest request) throws Exception;
}
