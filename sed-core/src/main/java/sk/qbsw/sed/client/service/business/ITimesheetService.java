package sk.qbsw.sed.client.service.business;

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
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
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

public interface ITimesheetService {

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
	 * @throws CSecurityException
	 */
	public CLockRecord add(CTimeStampRecord record) throws CBusinessException;

	/**
	 * Modifies timestamp record
	 * 
	 * @param id        record identifier
	 * @param newRecord new record data
	 * @param timestamp time for timestamp start
	 * @return output record contains some data from created persistent entity
	 * @throws CSecurityException
	 */
	public CLockRecord modify(CTimeStampRecord newRecord) throws CBusinessException;

	/**
	 * Deletes timestamp record defined by id.
	 * 
	 * @param id - timestamp identifier
	 * @return lock record
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public CLockRecord delete(Long id) throws CBusinessException;

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

	/**
	 * Closes previous timestamp and starts new non working timestamp.
	 * 
	 * @param record - data for new timestamp
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public Long startNonWorking(CTimeStampAddRecord record) throws CBusinessException;

	/**
	 * Closes previous timestamp and starts new working timestamp.
	 * 
	 * @param record - data for new timestamp
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void startWorking(CTimeStampAddRecord record) throws CBusinessException;

	/**
	 * Modifies timestamp defined by id.
	 * 
	 * @param id          - timestamp identifier
	 * @param recordToAdd - data to be modified
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void modifyWorking(final Long id, final CTimeStampAddRecord recordToAdd) throws CBusinessException;

	/**
	 * Stops working timestamp and modifies its data.
	 * 
	 * @param record - timestamp data to be modified.
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void stopWorking(CTimeStampAddRecord record) throws CBusinessException;

	/**
	 * Stops non working timestamp.
	 * 
	 * @param record       - timestamp data
	 * @param continueWork - flag to make new working timestamp
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void stopNonWorking(CTimeStampAddRecord record, boolean continueWork) throws CBusinessException;

	/**
	 * Stops non working timestamp.
	 * 
	 * @param record - timestamp data
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void stopNonWorking(CTimeStampAddRecord record) throws CBusinessException;

	/**
	 * Stops non working timestamp and starts alertness/emergency timestamp.
	 * 
	 * @param record - timestamp data
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void stopInteractiveWork(CTimeStampAddRecord record) throws CBusinessException;

	/**
	 * Returns map of project id keys and activity id values.
	 * 
	 * @return map of ids
	 * @throws CSecurityException
	 */
	public Map<Long, Long> getLastProjectToActivityRelationMap() throws CSecurityException;

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

	public void executeActivity(CLoggedUserRecord user, String activity) throws CBusinessException;

	/**
	 * Confirms timestamps records.
	 * 
	 * @param screenType    - type of confirmation screen
	 * @param users         - set of ids of subordinate users to have timestamps
	 *                      confirmed
	 * @param dateFrom      - first date of date interval to have timestamps
	 *                      confirmed
	 * @param dateTo        - last date of date interval to have timestamps
	 *                      confirmed
	 * @param userId        - id of user conrifming timestamps
	 * @param alsoEmployees - employees flag
	 * @param alsoSuperiors - superiors flag
	 * @return
	 * @throws CBusinessException
	 */
	List<Calendar> confirmTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException;

	/**
	 * Cancels timestamps records.
	 * 
	 * @param screenType    - type of confirmation screen
	 * @param users         - set of ids of subordinate users to have timestamps
	 *                      cancelled
	 * @param dateFrom      - first date of date interval to have timestamps
	 *                      cancelled
	 * @param dateTo        - last date of date interval to have timestamps
	 *                      cancelled
	 * @param userId        - id of user cancelling timestamps
	 * @param alsoEmployees - employees flag
	 * @param alsoSuperiors - superiors flag
	 * @throws CBusinessException
	 */
	void cancelTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException;

	/**
	 * Returns list of project durations in specified date interval for graph of
	 * project participation.
	 * 
	 * @param calendarFrom - first date of date interval
	 * @param calendarTo   - last date of date interval
	 * @return list of project duration objects
	 * @throws CSecurityException
	 */
	public List<CProjectDuration> getDataForGraphOfProjects(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException;

	/**
	 * Returns list of attendance durations in specified date interval for graph of
	 * attendance.
	 * 
	 * @param calendarFrom - first date of date interval
	 * @param calendarTo   - last date of date interval
	 * @return list of attendance duration objects
	 * @throws CSecurityException
	 */
	public List<CAttendanceDuration> getDataForGraphOfAttendance(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException;

	/**
	 * Returns data for bar chart in stats panel at dashboard.
	 * 
	 * @param countToday - flag to include actual day
	 * @return data object
	 * @throws CSecurityException
	 */
	public CGetInfoForMobileTimerResponseContent getInfoForMobileTimer(final Boolean countToday) throws CSecurityException;

	/**
	 * Returns data for bar chart in stats panel at dashboard.
	 * 
	 * @param dateFrom - first date of date interval
	 * @param dateTo   - last date of date interval
	 * @return data object
	 * @throws CSecurityException
	 */
	public CGetSumAndAverageTimeResponseContent getSumAndAverageTimeInTimeInterval(Calendar dateFrom, Calendar dateTo) throws CSecurityException;

	/**
	 * Returns data for paging toolbar at timesheet table.
	 * 
	 * @param filter - filter criteria
	 * @return data object
	 * @throws CSecurityException
	 */
	public CGetSumAndAverageTimeForUsersResponseContent getSumAndAverageTimeInTimeIntervalForUsers(final CSubrodinateTimeStampBrwFilterCriteria filter) throws CSecurityException;

	/**
	 * Returns list of stats records for graph at stats page.
	 * 
	 * @param filter - filter criteria
	 * @return list of stats records
	 * @throws CSecurityException
	 */
	public List<CStatsRecord> getDataForGraphOfStats(CStatsFilter filter) throws CSecurityException;

	public CLockRecord split(CTimeStampRecord record, Date splitTime) throws CBusinessException;
}
