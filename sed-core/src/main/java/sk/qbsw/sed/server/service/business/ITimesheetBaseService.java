package sk.qbsw.sed.server.service.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sk.qbsw.sed.client.model.CStatsFilter;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.timestamp.CLastExternaProjectActivity;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CPredefinedTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.response.CGetInfoForMobileTimerResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeForUsersResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeResponseContent;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;

public interface ITimesheetBaseService {
	
	/**
	 * Reads detail of timestamp record
	 * 
	 * @param timeStampId
	 * @return
	 */
	public CTimeStampRecord getDetail(Long timeStampId);

	/**
	 * Creates new timestamp record
	 * 
	 * @param record input data object
	 * @return output record contains some data from created persistent entity
	 * @throws CSecurityException in error case
	 */
	public CLockRecord add(CTimeStampRecord record, boolean alertnessworkAlreadyGenerated) throws CBusinessException;

	/**
	 * Creates new timestamp record with data entered as table row
	 * 
	 * @param record input data object
	 * @return record contains some data from created persistent entity
	 * @throws CBusinessException in error case
	 * @throws CSecurityException in error case
	 */
	public CTimeStampRecord addBrwItem(final CTimeStampRecord record, boolean alertnessworkAlreadyGenerated) throws CBusinessException;

	/**
	 * Modifies timestamp record
	 * 
	 * @param id        record identifier
	 * @param newRecord new record data
	 * @param timestamp time for timestamp start
	 * @return output record contains some data from created persistent entity
	 * @throws CSecurityException in error case
	 */
	public CLockRecord modify(Long id, CTimeStampRecord newRecord, Date timestamp) throws CBusinessException;

	/**
	 * Modifies timestamp record
	 * 
	 * @param id        record identifier
	 * @param newRecord new record data
	 * @param timestamp time for timestamp start
	 * @return output record contains some data from created persistent entity
	 * @throws CSecurityException in error case
	 */
	public CLockRecord delete(Long id) throws CBusinessException;

	/**
	 * Modifies timestamp record with data entered as table row
	 * 
	 * @param id        record identifier
	 * @param newRecord new record data
	 * @param timestamp time for timestamp start
	 * @return output record contains some data from created persistent entity
	 * @throws CBusinessException in error case
	 * @throws CSecurityException in error case
	 */
	public CTimeSheetRecord modifyBrwItem(final Long id, final CTimeStampRecord newRecord, final Date timestamp) throws CBusinessException;

	/**
	 * Returns predefined values for adding new timeshet record
	 * 
	 * @return
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	public CPredefinedTimeStamp loadPredefinedValue(Long userId, Boolean forSubordinateEmployee) throws CBusinessException;

	/**
	 * Return timestamp for for reception panel
	 * 
	 * @param userId          user identifier of the selected user
	 * @param timeToPredefine the date for which the timestamp will be finding
	 * @return timestamp object
	 * @throws CSecurityException
	 */
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForReceptionPanel(Long userId, Date timeToPredefine) throws CSecurityException;

	/**
	 * Return timestamp for add new timestamp screen and user timer panel
	 * 
	 * @param userId          user identifier of the selected user
	 * @param timeToPredefine the date for which the timestamp will be finding
	 * @return timestamp object
	 * @throws CSecurityException
	 */
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForUserTimerPanel(final Long userId, final Date timeToPredefine) throws CSecurityException;

	public Long startNonWorking(CTimeStampAddRecord record) throws CBusinessException;

	public void startWorking(CTimeStampAddRecord record) throws CBusinessException;

	public void modifyWorking(final Long id, CTimeStampAddRecord record) throws CBusinessException;

	public void stopWorking(CTimeStampAddRecord record) throws CBusinessException;

	public void stopNonWorking(CTimeStampAddRecord record, boolean continueWork) throws CBusinessException;

	public void stopNonWorking(CTimeStampAddRecord record) throws CBusinessException;

	public void stopInteractiveWork(CTimeStampAddRecord record) throws CBusinessException;

	public Map<Long, Long> getLastProjectToActivityRelationMap() throws CSecurityException;

	public CTimeStampAddRecord getUnclosedTimesheet(final Long userId) throws CBusinessException;

	/**
	 * Returns list of user names that has corrupted data for summary report
	 * generating process
	 * 
	 * @param userId identifier of the user that send a request
	 * @param from   start date of the time interval for report generating
	 * @param to     end date of the time interval for report generating
	 * @return list of user names
	 * @throws CBusinessException in business error case
	 * @throws CSecurityException in security error case
	 */
	public List<String> getListOfUsersWithCorruptedSummaryReport(Long userId, Date from, Date to) throws CBusinessException;

	/**
	 * Returns boolean value of the operation result. Operation generates all
	 * records for the users, that have approved absence request and no other
	 * activity records exists for the selected day.
	 * 
	 * @param userId   identifier of the user that send a request
	 * @param dateFrom the start date of the date interval for one the target
	 *                 operation is required
	 * @param dateTo   the end date of the date interval for one the target
	 *                 operation is required
	 * @return boolean value
	 * @throws CBusinessException in business error case
	 * @throws CSecurityException in security error case
	 */
	public Boolean generateApprovedEmployeesAbsenceRecords(Long userId, Date dateFrom, Date dateTo) throws CBusinessException;

	/**
	 * Generates full work report timestamps from prepared shorted temporary
	 * timestemps
	 * 
	 * @param userId   selected user identifier
	 * @param dateFrom start of generation date interval
	 * @param dateTo   end of generation date interval
	 * @return string message as result
	 * @throws CBusinessException in business error
	 * @throws CSecurityException in security error
	 */
	public String generateUserTimestampsFromPreparedItems(Long userId, Date dateFrom, Date dateTo, Long summaryWorkDurationInMinutes) throws CBusinessException;

	/**
	 * Returns object of two lists: projects/activities
	 * 
	 * @param userId user identifier
	 * @return object with two list of projects/activities identifiers
	 * @throws CBusinessException in business error
	 * @throws CSecurityException in security error
	 */
	public CLastExternaProjectActivity findUserLastExternalProgramActivity(Long userId) throws CBusinessException;

	public List<Calendar> confirmTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException;

	public void cancelTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException;

	public List<CProjectDuration> getDataForGraphOfProjects(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException;

	public List<CAttendanceDuration> getDataForGraphOfAttendance(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException;

	public CGetInfoForMobileTimerResponseContent getInfoForMobileTimer(final Boolean countToday) throws CSecurityException;

	public CGetSumAndAverageTimeResponseContent getSumAndAverageTimeInTimeInterval(Calendar dateFrom, Calendar dateTo) throws CSecurityException;

	public CGetSumAndAverageTimeForUsersResponseContent getSumAndAverageTimeInTimeIntervalForUsers(CSubrodinateTimeStampBrwFilterCriteria filter) throws CSecurityException;

	public List<CStatsRecord> getDataForGraphOfStats(CStatsFilter filter) throws CSecurityException;

	public CLockRecord split(CTimeStampRecord record, Date splitTime) throws CBusinessException;
}
