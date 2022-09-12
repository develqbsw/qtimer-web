package sk.qbsw.sed.server.service.report;

import java.util.LinkedList;
import java.util.List;

import sk.qbsw.sed.framework.report.model.CCellDefinition;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.transform.stream.IXLSStreamReportTransformer;

public class CEmployeesReportTransformer implements IXLSStreamReportTransformer {

	@Override
	public List<CCellDefinition> getCellDefinition() {
		final List<CCellDefinition> cellDefinition = new LinkedList<>();

		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_INTEGER));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_INTEGER));
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_INTEGER));

		return cellDefinition;
	}
}
