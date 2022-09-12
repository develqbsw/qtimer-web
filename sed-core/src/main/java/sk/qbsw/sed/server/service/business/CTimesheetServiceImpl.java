package sk.qbsw.sed.server.service.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import sk.qbsw.sed.client.service.business.ITimesheetService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;

/**
 * Service for managing timestamps
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@Service(value = "timestampService")
public class CTimesheetServiceImpl implements ITimesheetService {

	@Autowired
	private ITimesheetBaseService timesheetBaseService;

	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public CLockRecord add(final CTimeStampRecord record) throws CBusinessException {
		return this.timesheetBaseService.add(record, false);
	}

	@Transactional(readOnly = true)
	@Override
	public CTimeStampRecord getDetail(final Long timeStampId) {
		return this.timesheetBaseService.getDetail(timeStampId);
	}

	/**
	 * @throws CSecurityException
	 * @see ITimesheetService#loadPredefinedInteligentValueForUserTimerPanel(Long, Date)
	 */
	@Transactional(readOnly = true)
	@Override
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForUserTimerPanel(final Long userId, final Date timeToPredefine) throws CSecurityException {
		return this.timesheetBaseService.loadPredefinedInteligentValueForUserTimerPanel(userId, timeToPredefine);
	}

	/**
	 * @throws CSecurityException
	 * @see ITimesheetService#loadPredefinedInteligentValueForReceptionPanel(Long, Date)
	 */
	@Transactional(readOnly = true)
	@Override
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForReceptionPanel(final Long userId, final Date timeToPredefine) throws CSecurityException {
		return this.timesheetBaseService.loadPredefinedInteligentValueForReceptionPanel(userId, timeToPredefine);
	}

	@Transactional(readOnly = true)
	@Override
	public CPredefinedTimeStamp loadPredefinedValue(final Long userId, Boolean forSubordinateEmployee) throws CBusinessException {

		return this.timesheetBaseService.loadPredefinedValue(userId, forSubordinateEmployee);
	}

	/**
	 * @see ITimesheetService#modify(Long, CTimeStampRecord, Date)
	 */
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public CLockRecord modify(final CTimeStampRecord newRecord) throws CBusinessException {
		// ak editujem v tabulke tak tam ChangeTime nemam
		return this.timesheetBaseService.modify(newRecord.getId(), newRecord, newRecord.getChangeTime() == null ? Calendar.getInstance().getTime() : newRecord.getChangeTime());
	}

	/**
	 * @see ITimesheetService#delete(Long, CTimeStampRecord, Date)
	 */
	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public CLockRecord delete(final Long id) throws CBusinessException {
		return this.timesheetBaseService.delete(id);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public Long startNonWorking(final CTimeStampAddRecord record) throws CBusinessException {
		return this.timesheetBaseService.startNonWorking(record);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void startWorking(final CTimeStampAddRecord record) throws CBusinessException {
		this.timesheetBaseService.startWorking(record);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void modifyWorking(final Long id, final CTimeStampAddRecord record) throws CBusinessException {
		this.timesheetBaseService.modifyWorking(id, record);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void stopNonWorking(final CTimeStampAddRecord record, final boolean continueWork) throws CBusinessException {
		this.timesheetBaseService.stopNonWorking(record, continueWork);
	}

	/**
	 * Ukoncenie nepracovnej aktivity a zacatie novej pracovnej aktivity bez ohladu
	 * na predchadzajucu pracovnu aktivitu... Najprv sa zastavi nepracovna aktivita
	 * s tym, ze nepokracujem v praci a nasledne na to vlozim novu pracovnu
	 * aktivitu. Ak neurobim ziadne zmeny na formulari je stav rovnaky ako keby som
	 * zavolal stopNonWorking s boolean true...
	 * 
	 * @param record
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void stopNonWorking(final CTimeStampAddRecord record) throws CBusinessException {
		this.timesheetBaseService.stopNonWorking(record);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void stopInteractiveWork(final CTimeStampAddRecord record) throws CBusinessException {
		this.timesheetBaseService.stopInteractiveWork(record);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void stopWorking(final CTimeStampAddRecord record) throws CBusinessException {
		this.timesheetBaseService.stopWorking(record);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<Long, Long> getLastProjectToActivityRelationMap() throws CSecurityException {
		return this.timesheetBaseService.getLastProjectToActivityRelationMap();
	}

	@Transactional(readOnly = true)
	public CTimeStampAddRecord getUnclosedTimesheet(final Long userId) throws CBusinessException {
		return this.timesheetBaseService.getUnclosedTimesheet(userId);
	}

	/**
	 * @see ITimesheetService#getListOfUsersWithCorruptedSummaryReport(Long, Date,
	 *      Date)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> getListOfUsersWithCorruptedSummaryReport(Long requestorUserId, Date from, Date to) throws CBusinessException {
		return this.timesheetBaseService.getListOfUsersWithCorruptedSummaryReport(requestorUserId, from, to);
	}

	/**
	 * @see ITimesheetService#generateApprovedEmployeesAbsenceRecords(Long, Date)
	 */
	@Transactional
	@Override
	public Boolean generateApprovedEmployeesAbsenceRecords(Long requestedUserId, Date dateFrom, Date dateTo) throws CBusinessException {
		return this.timesheetBaseService.generateApprovedEmployeesAbsenceRecords(requestedUserId, dateFrom, dateTo);
	}

	/**
	 * @see ITimesheetService#generateUserTimestampsFromPreparedItems(Long, Date,
	 *      Date)
	 */
	@Override
	@Transactional
	public String generateUserTimestampsFromPreparedItems(Long userId, Date dateFrom, Date dateTo, Long summaryWorkDurationInMinutes) throws CBusinessException {
		return this.timesheetBaseService.generateUserTimestampsFromPreparedItems(userId, dateFrom, dateTo, summaryWorkDurationInMinutes);
	}

	/**
	 * @see ITimesheetService#findUserLastExternalProgramActivity(Long)
	 */
	@Override
	@Transactional(readOnly = true)
	public CLastExternaProjectActivity findUserLastExternalProgramActivity(Long userId) throws CBusinessException {
		return this.timesheetBaseService.findUserLastExternalProgramActivity(userId);
	}

	@Override
	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	public void executeActivity(CLoggedUserRecord loggedUserRecord, String activity) throws CBusinessException {
		if (activity.equals(IActivityConstant.ACTIVITY_WORK_START)) {
			CTimeStampAddRecord model = getModel(null, loggedUserRecord, activity);
			model.setOutside(false);
			this.startWorking(model);
		} else if (activity.equals(IActivityConstant.ACTIVITY_WORK_STOP)) {
			this.stopWorking(getModel(null, loggedUserRecord, activity));
		} else if (activity.equals(IActivityConstant.ACTIVITY_LUNCH_START)) {
			this.startNonWorking(getModel(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK), loggedUserRecord, activity));
		} else if (activity.equals(IActivityConstant.ACTIVITY_LUNCH_STOP)) {
			this.stopNonWorking(getModel(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK), loggedUserRecord, activity));
		} else if (activity.equals(IActivityConstant.ACTIVITY_BREAK_START)) {
			this.startNonWorking(getModel(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK), loggedUserRecord, activity));
		} else if (activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)) {
			this.stopNonWorking(getModel(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK), loggedUserRecord, activity));
		} else if (activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_START)) {
			CTimeStampAddRecord model = getModel(null, loggedUserRecord, activity);
			model.setOutside(true);
			this.startWorking(model);
		} else if (activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
			CTimeStampAddRecord model = getModel(null, loggedUserRecord, activity);
			model.setOutside(false);
			this.startWorking(model);
		}
	}

	/**
	 * Returns true if activity is allowed
	 */
	private boolean checkAllowedActivity(int mode, String activity, int nonWorkingActivity, boolean outside) {

		switch (mode) {
		case CPredefinedInteligentTimeStamp.MODE_NOTHING:
			if (activity.equals(IActivityConstant.ACTIVITY_WORK_STOP) || activity.equals(IActivityConstant.ACTIVITY_LUNCH_STOP) || activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)
					|| activity.equals(IActivityConstant.ACTIVITY_LUNCH_START) || activity.equals(IActivityConstant.ACTIVITY_BREAK_START)
					|| activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
				return false;
			}

			break;
		case CPredefinedInteligentTimeStamp.MODE_WORK_STARTED:
			if (activity.equals(IActivityConstant.ACTIVITY_WORK_START) || activity.equals(IActivityConstant.ACTIVITY_LUNCH_STOP) || activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)) {
				return false;
			}

			if (!outside && activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
				return false;
			}

			if (outside && activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_START)) {
				return false;
			}

			break;
		case CPredefinedInteligentTimeStamp.MODE_WORK_FINISHED:
			if (activity.equals(IActivityConstant.ACTIVITY_WORK_STOP) || activity.equals(IActivityConstant.ACTIVITY_LUNCH_STOP) || activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)
					|| activity.equals(IActivityConstant.ACTIVITY_LUNCH_START) || activity.equals(IActivityConstant.ACTIVITY_BREAK_START)
					|| activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
				return false;
			}
			break;
		case CPredefinedInteligentTimeStamp.MODE_WORKBREAK_STARTED:
			if (activity.equals(IActivityConstant.ACTIVITY_WORK_START) || activity.equals(IActivityConstant.ACTIVITY_WORK_STOP) || activity.equals(IActivityConstant.ACTIVITY_BREAK_START)
					|| activity.equals(IActivityConstant.ACTIVITY_LUNCH_START) || activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_START)
					|| activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
				return false;
			}

			if (nonWorkingActivity == CPredefinedInteligentTimeStamp.ACTIVITY_PARAGRAPH) {
				if (activity.equals(IActivityConstant.ACTIVITY_LUNCH_STOP) || activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)) {
					return false;
				}
			}

			break;
		case CPredefinedInteligentTimeStamp.MODE_WORKBREAK_FINISHED:
			if (activity.equals(IActivityConstant.ACTIVITY_WORK_STOP) || activity.equals(IActivityConstant.ACTIVITY_LUNCH_STOP) || activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)
					|| activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
				return false;
			}

			break;
		}

		return true;
	}

	private CTimeStampAddRecord getModel(final Long nonWorkingAactivityId, CLoggedUserRecord user, String activity) throws CBusinessException {
		Long selectedProjectId = null;
		Long selectedActivityId = null;
		CPredefinedInteligentTimeStamp storedTimestamp;

		CPredefinedInteligentTimeStamp predefinedValues = this.loadPredefinedInteligentValueForReceptionPanel(user.getUserId(), new Date());

		if (!checkAllowedActivity(predefinedValues.getMode(), activity,
				predefinedValues.getModel().getNonWorkingActivityId() != null ? predefinedValues.getModel().getNonWorkingActivityId().intValue() : 0, predefinedValues.getModel().getOutside())) {
			throw new CBusinessException("");
		}

		final CTimeStampAddRecord model = predefinedValues.getModel();

		if (CPredefinedInteligentTimeStamp.MODE_INVALID_PROJECT_OR_ACTIVITY == predefinedValues.getMode()) {
			// ak je projekt alebo aktivita neplatna nenastavime nic
			throw new CBusinessException("ak je projekt alebo aktivita neplatna nenastavime nic");
		}

		if ((model.getActivityId() == null) || ((model.getActivityId() != null) && (model.getActivityId().intValue() == CPredefinedInteligentTimeStamp.ACTIVITY_WORKING_DEFAULT))) {
			// do nothing
		} else {
			selectedActivityId = model.getActivityId();
		}
		if (model.getProjectId() == null) {
			// do nothing
		} else {
			selectedProjectId = model.getProjectId();
		}

		// store previous model
		storedTimestamp = predefinedValues;

		// ------------------------------------------------------------------

		final CTimeStampAddRecord resultModel = new CTimeStampAddRecord();

		// common
		resultModel.setEmployeeId(user.getUserId());

		// take current time
		resultModel.setTime(new Date());

		resultModel.setActivityId(selectedActivityId);
		resultModel.setProjectId(selectedProjectId);
		if (storedTimestamp.getModel() != null) {
			resultModel.setNote(storedTimestamp.getModel().getNote());
			resultModel.setPhase(storedTimestamp.getModel().getPhase());
			if (storedTimestamp.getModel().getOutside() != null) {
				resultModel.setOutside(storedTimestamp.getModel().getOutside());
			}
		} else {
			resultModel.setOutside(Boolean.FALSE);
		}

		// non working
		resultModel.setNonWorkingActivityId(nonWorkingAactivityId);

		return resultModel;
	}

	@Override
	public List<Calendar> confirmTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException {
		return timesheetBaseService.confirmTimesheetRecords(screenType, users, dateFrom, dateTo, userId, alsoEmployees, alsoSuperiors);
	}

	@Override
	public void cancelTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException {
		timesheetBaseService.cancelTimesheetRecords(screenType, users, dateFrom, dateTo, userId, alsoEmployees, alsoSuperiors);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CProjectDuration> getDataForGraphOfProjects(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException {
		return timesheetBaseService.getDataForGraphOfProjects(calendarFrom, calendarTo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CAttendanceDuration> getDataForGraphOfAttendance(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException {
		return timesheetBaseService.getDataForGraphOfAttendance(calendarFrom, calendarTo);
	}

	@Override
	@Transactional(readOnly = true)
	public CGetInfoForMobileTimerResponseContent getInfoForMobileTimer(final Boolean countToday) throws CSecurityException {
		return timesheetBaseService.getInfoForMobileTimer(countToday);
	}

	@Override
	@Transactional(readOnly = true)
	public CGetSumAndAverageTimeResponseContent getSumAndAverageTimeInTimeInterval(Calendar dateFrom, Calendar dateTo) throws CSecurityException {
		return timesheetBaseService.getSumAndAverageTimeInTimeInterval(dateFrom, dateTo);
	}

	@Override
	@Transactional(readOnly = true)
	public CGetSumAndAverageTimeForUsersResponseContent getSumAndAverageTimeInTimeIntervalForUsers(CSubrodinateTimeStampBrwFilterCriteria filter) throws CSecurityException {
		return timesheetBaseService.getSumAndAverageTimeInTimeIntervalForUsers(filter);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CStatsRecord> getDataForGraphOfStats(CStatsFilter filter) throws CSecurityException {
		return timesheetBaseService.getDataForGraphOfStats(filter);
	}

	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public CLockRecord split(CTimeStampRecord record, Date splitTime) throws CBusinessException {
		return this.timesheetBaseService.split(record, splitTime);
	}
}
