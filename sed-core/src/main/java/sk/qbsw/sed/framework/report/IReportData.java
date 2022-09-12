package sk.qbsw.sed.framework.report;

import java.util.Calendar;
import java.util.Set;

import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IReportData {

	public CComplexInputReportModel getModel(final Long reportType, Set<Long> userIds, Calendar dateFrom, Calendar dateTo, boolean alsoNotConfirmed, String screenType) throws CBusinessException;

	public CComplexInputReportModel getModel(Long clientId, Boolean onlyValid) throws CBusinessException;

	public CComplexInputReportModel getWorkplaceModel(Long clientId, Boolean onlyValid) throws CBusinessException;
}
