package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.framework.report.model.CReportModel;

public interface IReportSheetDao<T extends CReportModel> extends IDao<T> {

	public List<T> findByUsersAndPeriod(Set<Long> userIds, Calendar dateFrom, Calendar dateTo, boolean alsoNotConfirmed, String screenType);
}
