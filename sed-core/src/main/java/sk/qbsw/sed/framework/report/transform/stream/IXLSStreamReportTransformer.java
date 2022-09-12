package sk.qbsw.sed.framework.report.transform.stream;

import java.util.List;

import sk.qbsw.sed.framework.report.model.CCellDefinition;
import sk.qbsw.sed.framework.report.transform.IReportTransformer;

public interface IXLSStreamReportTransformer extends IReportTransformer {

	public List<CCellDefinition> getCellDefinition();
}
