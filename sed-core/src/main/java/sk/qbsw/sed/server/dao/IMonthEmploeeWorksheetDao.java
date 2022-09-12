package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;

public interface IMonthEmploeeWorksheetDao<T extends CViewTimeStamp> extends IDao<T> {
	
	/**
	 * Returns specific month records for selected user and selected time interval
	 * 
	 * @param userId   input user identifier
	 * @param dateFrom start of input time interval
	 * @param dateTo   end of input time interval
	 * @return list of records (empty but not null)
	 */
	public List<T> findByUserAndPeriod(Long userId, Calendar dateFrom, Calendar dateTo, boolean alsoNotConfirmed, String screenType);
}
