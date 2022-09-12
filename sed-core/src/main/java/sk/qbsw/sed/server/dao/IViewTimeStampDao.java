package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CGetSumAndAverageTimeForUser;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * DAO accessing model for brw timestamps
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IViewTimeStampDao extends IDao<CViewTimeStamp> {

	/**
	 * Finds data for browser of timestamps for actual user
	 * 
	 * @param user     logged user
	 * @param criteria criteria from screen
	 * @return list of data model
	 */
	public List<CViewTimeStamp> findAll(CUser user, IFilterCriteria criteria, int from, int count);

	public List<CViewTimeStamp> findAll(CUser user, IFilterCriteria criteria, int from, int count, String sortProperty, boolean sortAsc);

	public Long count(CUser user, IFilterCriteria criteria);

	/**
	 * Finds data for browser of timestamps for seected user
	 * 
	 * @param user
	 * @param filter
	 * @return
	 */
	public List<CViewTimeStamp> findAll(final CUser user, final IFilterCriteria filter);

	public List<CGetSumAndAverageTimeForUser> getSumAndAvgTimeInTimeInterval(final CSubrodinateTimeStampBrwFilterCriteria filter);

	public Long geCountOfWorkedDaysInTimeInterval(final CSubrodinateTimeStampBrwFilterCriteria filter);

	/**
	 * spočíta čas z platných záznamov výkazu práce pre aktivitu pre konkretny rok
	 * 
	 * @param userId
	 * @param activityId
	 * @param year
	 * @return
	 */
	public Long getDurationOfActivityInMinutes(final Long userId, final Long activityId, final int year, final Long timestampId);

	public List<CViewTimeStamp> getHolidayRecordsFromInterval(final CUser user, final Calendar dateFrom, Calendar dateTo);

	public Boolean existHolidayRecordForDay(final CUser user, final Calendar cal, final Long id);

	public List<CViewTimeStamp> getDoctorVisitTimeStamps(final Date dateFrom, Long clientId);
	
	public List<CViewTimeStamp> getHomeOfficeTimeStamps(final Date dateFrom, Long clientId);

	public List<CViewTimeStamp> getNotHomeOfficeTimeStamps(final Date dateFrom, Long clientId);

}
