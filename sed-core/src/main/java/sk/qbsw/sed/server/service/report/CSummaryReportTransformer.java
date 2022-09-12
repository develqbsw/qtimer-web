package sk.qbsw.sed.server.service.report;

import java.util.LinkedList;
import java.util.List;

import sk.qbsw.sed.framework.report.model.CCellDefinition;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.transform.stream.IXLSStreamReportTransformer;

public class CSummaryReportTransformer implements IXLSStreamReportTransformer {

	public List<CCellDefinition> getCellDefinition() {
		
		final List<CCellDefinition> cellDefinition = new LinkedList<>();

		// rok
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// mesiac
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// tyzden
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// pracovnik meno
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// projekt skupina
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// projekt id
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// pracovnik id
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// datum
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_DATE));
		// trvanie
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_DURATION));
		// cinnost
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// poznamka
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// id_etapy
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// dni - ???
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));
		// projekt meno
		cellDefinition.add(CCellDefinition.getInstance(CCellTypeEnum.TYPE_STRING));

		return cellDefinition;
	}
}
