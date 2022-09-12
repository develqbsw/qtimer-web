package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.CStatsFilter;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.server.model.codelist.CTimeSheetRecordStatus;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;

/**
 * DAO for accessing time sheets records
 * 
 * @author Dalibor Rak
 * 
 */
public interface ITimesheetRecordDao extends IDao<CTimeSheetRecord> {
	
	/**
	 * Finds all records which are overlapped by entered time interval
	 * 
	 * @param timeFrom start of check interval
	 * @param timeTo   end of check interval
	 * @return list of timesheet records
	 */
	public List<CTimeSheetRecord> findAllOverlapping(Calendar timeFrom, Calendar timeTo, Long userId);

	/**
	 * Finds last timesheet record related to entered user
	 * 
	 * @param userId
	 * @return CTimeSheetRecord
	 */
	public CTimeSheetRecord findLast(Long userId);

	/**
	 * Finds last timesheet record related to entered user
	 * 
	 * @param userId
	 * @param date   date to search before
	 * @return CTimeSheetRecord
	 */
	public CTimeSheetRecord findLast(Long userId, Calendar date);

	/**
	 * Finds last working timesheet record related to entered user
	 * 
	 * @param userId
	 * @return CTimeSheetRecord
	 */
	public CTimeSheetRecord findLastWorking(Long userId);

	/**
	 * Finds last working timesheet record related to entered user
	 * 
	 * @param userId selected user
	 * @param date   date to search to
	 * @return timestamp record
	 */
	public CTimeSheetRecord findLastWorking(Long userId, Calendar date);

	/**
	 * Finds last (non external) working timesheet record related to entered user
	 * and date
	 * 
	 * @param userId selected user
	 * @param date   border time before the target timestamp will be finding
	 * @return timestamp record
	 */
	public CTimeSheetRecord findLastWorkingNonExternal(Long userId, Calendar date);

	/**
	 * Returns last (external) working timesheet records related to entered user
	 * 
	 * @param userId selected user
	 * @param date   border time before the target timestamp will be finding
	 * @param count  maximal number of records
	 * @return list of records
	 */
	public List<CTimeSheetRecord> findLastWorkingExternal(final Long userId, Calendar date, final Integer count);

	/**
	 * Finds last nonworking timesheet record related to entered user
	 * 
	 * @param userId
	 * @return CTimeSheetRecord
	 */
	public CTimeSheetRecord findLastNonWorking(Long userId);

	/**
	 * Finds last nonworking timesheet record related to entered user
	 * 
	 * @param userId
	 * @return CTimeSheetRecord
	 */
	public CTimeSheetRecord findLastNonWorking(Long userId, Calendar date);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<Object[]> findLastActivityForProjectMap(Long userId);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public CTimeSheetRecord findUnclosedRecord(Long userId);

	/**
	 * 
	 * @param clientId
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	public List<Object> findListUsersIdsWithCorruptedSummaryReportForClient(final Long clientId, Calendar timeFrom, Calendar timeTo);

	/**
	 * 
	 * @param userId
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	public Boolean hasUserUnclosedTimstempsInDateInterval(final Long userId, Calendar timeFrom, Calendar timeTo);

	/**
	 * Returns all user records for selected day
	 * 
	 * @param userId    user identifier
	 * @param checkDate selected date
	 * @return list of records (maybe empty, but not null)
	 */
	public List<CTimeSheetRecord> findUserRecords4Day(final Long userId, Calendar checkDate);

	/**
	 * Returns all user work records for selected day
	 * 
	 * @param userId
	 * @param dateFom
	 * @param dateTo
	 * @return
	 */
	public List<CTimeSheetRecord> findUserWorkRecordsInDateInterval(Long userId, Calendar dateFom, Calendar dateTo);

	/**
	 * Returns <code>true</code> if number of user records for selected day is > 0,
	 * or <code>false</code> if number of records = 0.
	 * 
	 * @param userId    user identifier
	 * @param checkDate selected date
	 * @return boolean value
	 */
	public Boolean existsUserRecords4Day(final Long userId, Calendar checkDate);

	/**
	 * 
	 */
	public Boolean existsUserWorkRecords4Day(final Long userId, Calendar checkDate);

	/**
	 * 
	 * @param userId
	 * @param dateFrom
	 * @param dateTo
	 * @return sum#averageTime
	 */
	public String getSumAndAverageTimeInTimeInterval(final Long userId, final Calendar dateFrom, final Calendar dateTo);

	public List<CTimeSheetRecord> findAll(final Collection<Long> users, Calendar dateFrom, Calendar dateTo);

	public List<CTimeSheetRecord> findSameUserRecords4Day(Long userId, Calendar checkDate, Long projectId, Long activityId, String note, String phase, Boolean outside, Boolean homeOffice);

	public List<CProjectDuration> getDataForGraphOfProjects(final Long userId, Calendar timeFrom, Calendar timeTo);

	public List<CAttendanceDuration> getDataForGraphOfAttendance(final Long userId, Calendar timeFrom, Calendar timeTo);

	public void skracovaniePoznamok(final Long timesheetId, final Long userId, Calendar timeFrom, Calendar timeTo);

	public Long countRecordsInDateIntervalByStatus(Long userId, Calendar dateFom, Calendar dateTo, CTimeSheetRecordStatus status);

	public boolean existsSubordinateRecordsForConfirmation(List<Long> userIds, Calendar dateFrom, Calendar dateTo, CTimeSheetRecordStatus status);

	public List<CStatsRecord> getDataForGraphOfStats(final CStatsFilter filter);

	public List<CTimeSheetRecord> findLast(final Long userId, Calendar date, final Integer count);
}
