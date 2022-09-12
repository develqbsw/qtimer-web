package sk.qbsw.sed.server.service.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.CStatsFilter;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.IHomeOfficePermissionConstants;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.IShiftDayConstant;
import sk.qbsw.sed.client.model.ITimeSheetRecordStates;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.params.IParameter;
import sk.qbsw.sed.client.model.restriction.IDayTypeConstant;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CGetSumAndAverageTimeForUser;
import sk.qbsw.sed.client.model.timestamp.CLastExternaProjectActivity;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CPredefinedTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.model.timestamp.ITimestampScreenType;
import sk.qbsw.sed.client.response.CGetInfoForMobileTimerResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeForUsersResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeResponseContent;
import sk.qbsw.sed.client.service.business.IRequestService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.client.ui.screen.IScreenMode;
import sk.qbsw.sed.common.utils.CDateRangeUtils;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.ILockDateDao;
import sk.qbsw.sed.server.dao.IOrganizationTreeDao;
import sk.qbsw.sed.server.dao.IParameterDao;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.dao.ITimesheetRecordDao;
import sk.qbsw.sed.server.dao.ITimesheetStateDao;
import sk.qbsw.sed.server.dao.ITmpTimesheetRecordDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.hibernate.CViewTimeStampDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.jira.CJiraAuthenticationConfigurator;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.codelist.CTimeSheetRecordStatus;
import sk.qbsw.sed.server.model.domain.COrganizationTree;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CTmpTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.params.CLockDate;
import sk.qbsw.sed.server.model.params.CParameterEntity;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;
import sk.qbsw.sed.server.service.CTimeUtils;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;
import sk.qbsw.sed.server.util.CDateServerUtils;
import sk.qbsw.sed.server.util.CTimesheetUtils;

@Service(value = "timestampServiceBase")
public class CTimesheetBaseServiceImpl implements ITimesheetBaseService {
	private static final long MAX_DAY_INTERVAL_LENGTH = 31l;

	/**
	 * Activity dao
	 */
	@Autowired
	private IActivityDao activityDao;

	/**
	 * project dao
	 */
	@Autowired
	private IProjectDao projectDao;

	/**
	 * timesheet dao
	 */
	@Autowired
	private ITimesheetRecordDao timesheetDao;

	@Autowired
	private ITimesheetStateDao stateDao;

	/**
	 * request dao
	 */
	@Autowired
	private IRequestDao requestDao;

	@Autowired
	private IHolidayDao holidayDao;

	@Autowired
	private IParameterDao parameterDao;

	@Autowired
	private ITmpTimesheetRecordDao tmpTimesheetDao;

	@Autowired
	private ILockDateDao lockDateDao;

	@Autowired
	private IActivityTimeLimitCheckBaseService activityTimeLimitCheckService;

	@Autowired
	private IOrganizationTreeDao organizationTreeDao;

	/**
	 * User dao
	 */
	@Autowired
	private IUserDao userDao;

	@Autowired
	IRequestReasonDao requestReasonDao;

	@Autowired
	private CJiraAuthenticationConfigurator jiraAuthenticationConfigurator;

	@Autowired
	private CViewTimeStampDao timestampDao;

	@Autowired
	private IRequestService requestService;

	private final Logger logger = Logger.getLogger(CTimesheetBaseServiceImpl.class.getName());

	@Override
	public CLockRecord add(final CTimeStampRecord record, boolean alertnessworkAlreadyGenerated) throws CBusinessException {

		final CTimeStampRecord add = addBrwItem(record, alertnessworkAlreadyGenerated);

		// prepares return value
		final CLockRecord retVal = new CLockRecord();
		retVal.setId(add.getId());
		retVal.setLastChangeDate(add.getChangeTime());

		return retVal;
	}

	@Override
	public CTimeStampRecord addBrwItem(final CTimeStampRecord record, boolean alertnessworkAlreadyGenerated) throws CBusinessException {
		// staré záznamy nemusia mať vyplnený home office
		if (record.getHomeOffice() == null) {
			record.setHomeOffice(Boolean.FALSE);
		}

		// checks
		this.checkTimeOrder(record);
		this.checkHomeOffice(record);
		this.checkOutsideAndHomeOffice(record);
		this.checkExistsConfirmedTimestampForCurrentDay(record);

		// logic
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		final CTimeSheetRecord add = new CTimeSheetRecord();
		// ak aktivita je null, znamena to, ze uzivatel nevybral ziadnu working aktivitu... takze vlozime defaultnu aktivitu...
		add.setActivity(null != record.getActivityId() ? this.activityDao.findById(record.getActivityId()) : this.activityDao.findById(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_WORKING_DEFAULT)));
		add.setChangedBy(changedBy);
		add.setChangeTime(Calendar.getInstance());
		add.setClient(changedBy.getClient());
		add.setCreatedBy(changedBy);
		add.setOwner(this.userDao.findById(record.getEmployeeId()));
		add.setNote(record.getNote());
		add.setOutside(record.getOutsideWorkplace() == null ? false : record.getOutsideWorkplace());
		add.setHomeOffice(record.getHomeOffice() == null ? false : record.getHomeOffice());

		// SED-333 - etapa môže mať max. 30 znakov
		if (record.getPhase() != null ? record.getPhase().length() > 29 : false) {
			add.setPhase(record.getPhase().substring(0, 29));
		} else {
			add.setPhase(record.getPhase());
		}

		add.setReason(null != record.getRequestReasonId() ? this.requestReasonDao.findById(record.getRequestReasonId()) : null);

		if ((record.getProjectId() != null) && (record.getProjectId() != ISearchConstants.NONE)) {
			add.setProject(this.projectDao.findById(record.getProjectId()));
		} else {
			add.setProject(null);
		}

		add.setValid(Boolean.TRUE);

		final Calendar dateFrom = Calendar.getInstance();
		dateFrom.setTime(record.getDateFrom());
		CTimeUtils.convertToStartTime(dateFrom);

		add.setTimeFrom(dateFrom);

		if (record.getDateTo() != null) {
			final Calendar dateTo = Calendar.getInstance();
			dateTo.setTime(record.getDateTo());
			CTimeUtils.convertToEndTime(dateTo);
			add.setTimeTo(dateTo);
		}

		this.checkTimeContinuing(add, changedBy);

		// check lock conditions
		this.checkLockRecordsConditions(add);

		// check: request for the activity
		this.checkRequestExistence4NonWorkingRecord(add);

		// check: add nonworking activity for nonworking day
		checkAddNonWorkingRecordForNonWorkingDay(add);

		// check: user has role for alertness work
		this.checkUserAlertnessWorkRight(add);

		// check: time/user restriction of the activity (SED-290)
		this.checkUserTimeRestrictionOfActivity(add);

		// check time overlapping
		this.checkTimesheetOverlapping(add);

		// check: timestamp reason
		this.checkTimestampReason(add);

		// check: activity times
		this.checkActivityTimes(add);

		add.setStatus(this.stateDao.findById(ITimeSheetRecordStates.ID_NEW));

		CTimesheetUtils.setIssueSummaryFromJira(add, jiraAuthenticationConfigurator, userDao);

		if (add.getTimeTo() != null) {
			Calendar date = (Calendar) add.getTimeFrom().clone();
			long hours4 = 1000l * 3600l * 4l;
			long lDuration = add.getTimeTo().getTimeInMillis() - add.getTimeFrom().getTimeInMillis() + 1l;

			Boolean existsHolidayRecordForDayLastGen = this.requestDao.existsClientRequestsHoliday4DayLastGen(add.getOwner().getId(), date);

			Boolean halfDayVacation = Boolean.FALSE;

			if (lDuration == hours4) {
				halfDayVacation = Boolean.TRUE;
			}

			final Boolean isHoliday = add.getActivity().getId() == -3 ? true : false;

			if (isHoliday && existsHolidayRecordForDayLastGen) {
				this.updateRemainingDaysAfterBrwItemAdd(add.getOwner(), date, halfDayVacation);
			}
		}

		final Calendar timeOfEnd = Calendar.getInstance();
		timeOfEnd.setTime(record.getTimeFrom());
		
		if (!alertnessworkAlreadyGenerated) {
			
			final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(record.getEmployeeId(), timeOfEnd);
			
			boolean isLastRecordClosed = lastActivity.getTimeTo() != null;

			if (this.closePreviousActivity(record.getEmployeeId(), record.getTimeFrom(), true)) {
				CTimeUtils.addMinute(record.getTimeFrom());
			}

			if (!isLastRecordClosed) {
				this.addContinuationOfLastRecord(record.getEmployeeId(), record.getTimeFrom(), true, null);
			}
		}

		this.timesheetDao.saveOrUpdate(add);

		return add.convert();
	}

	/**
	 * Check if user request (holiday, paid free/replwork, sickness, break in work)
	 * exists for input record with specific non work activity
	 * 
	 * @param record checked record
	 * @throws CBusinessException if associated request is missing
	 */
	private void checkRequestExistence4NonWorkingRecord(CTimeSheetRecord record) throws CBusinessException {
		Boolean existsRequest = Boolean.FALSE;
		Boolean requiredRequest = Boolean.FALSE;

		List<CRequest> approvedUserRequests = this.requestDao.findAllApprovedUserRequestContainsDate(record.getTimeFrom().getTime(), record.getOwner());

		Long recordActivityId = record.getActivity().getId();

		if (IActivityConstant.NOT_WORK_WORKBREAK.equals(recordActivityId)) {
			requiredRequest = Boolean.TRUE;

			// exists work break request?
			for (CRequest request : approvedUserRequests) {
				if (IRequestTypeConstant.RQTYPE_WORKBREAK_CODE.equalsIgnoreCase(request.getType().getCode())) {
					existsRequest = Boolean.TRUE;
					break;
				}
			}
		} else if (IActivityConstant.NOT_WORK_HOLIDAY.equals(recordActivityId)) {
			requiredRequest = Boolean.TRUE;

			final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
			final CUser user = this.userDao.findById(loggedUser.getUserId());
			Boolean existHolidayRecord = this.timestampDao.existHolidayRecordForDay(user, record.getTimeFrom(), record.getId());

			if (existHolidayRecord) {
				throw new CBusinessException(CClientExceptionsMessages.HOLIDAY_RECORD_ALREADY_EXISTS);
			}

			// exists holiday request?
			for (CRequest request : approvedUserRequests) {
				if (IRequestTypeConstant.RQTYPE_VACATION_CODE.equalsIgnoreCase(request.getType().getCode())) {
					existsRequest = Boolean.TRUE;

					long hours4 = 1000l * 3600l * 4l;
					long lDuration = record.getTimeTo().getTimeInMillis() - record.getTimeFrom().getTimeInMillis() + 1l;

					if (BigDecimal.valueOf(0.5).equals(BigDecimal.valueOf(request.getNumberWorkDays()))) {
						// ziadost na pol dna, ci ma casova znacka 4h alebo 8h sa kontroluje uz na klientovi
						// tu skontrolujeme uz len ci su to 4h
						if (hours4 != lDuration) {
							throw new CBusinessException(CClientExceptionsMessages.MISSING_REQUEST_FOR_DAY_HOLIDAY);
						}
					}
					break;
				}
			}

		} else if (IActivityConstant.NOT_WORK_SICKNESS.equals(recordActivityId)) {
			requiredRequest = Boolean.TRUE;

			// exists sickness request?
			for (CRequest request : approvedUserRequests) {
				if (IRequestTypeConstant.RQTYPE_SICKNESS_CODE.equalsIgnoreCase(request.getType().getCode())) {
					existsRequest = Boolean.TRUE;
					break;
				}
			}

		} else if (IActivityConstant.NOT_WORK_REPLWORK.equals(recordActivityId)) {
			requiredRequest = Boolean.TRUE;

			// exists paid free request?
			for (CRequest request : approvedUserRequests) {
				if (IRequestTypeConstant.RQTYPE_REPLWORK_CODE.equalsIgnoreCase(request.getType().getCode())) {
					existsRequest = Boolean.TRUE;
					break;
				}
			}
		}

		if (requiredRequest && !existsRequest) {
			throw new CBusinessException(CClientExceptionsMessages.MISSING_REQUEST_FOR_NON_WORK_ACTIVITY);
		}
	}

	/**
	 * Adds record from intelligent data
	 * 
	 * @param record
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	private Long addNonWorkingRecord(final CTimeStampAddRecord recordToAdd, boolean alertnessworkAlreadyGenerated) throws CBusinessException {
		final CTimeStampRecord record = new CTimeStampRecord();

		record.setActivityId(recordToAdd.getNonWorkingActivityId());
		record.setEmployeeId(recordToAdd.getEmployeeId());
		record.setOutsideWorkplace(Boolean.FALSE);
		record.setHomeOffice(Boolean.FALSE);
		record.setDateFrom(recordToAdd.getTime());

		if (IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(recordToAdd.getNonWorkingActivityId()) || IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(recordToAdd.getNonWorkingActivityId())) {
			// SED-406 Priradenie projektu k pohotovosti
			record.setProjectId(recordToAdd.getProjectId());
			record.setNote(recordToAdd.getNote());
			record.setPhase(recordToAdd.getPhase());
		} else {
			record.setProjectId(null);
			record.setNote("");
			record.setPhase("");
		}

		// SED-290: kontrola na najblizsi mozny zaciatok ativity
		Long message = this.updateRecordTimeFromValueByUserActivityLimits(record);

		this.add(record, alertnessworkAlreadyGenerated);
		
		return message;
	}

	/***
	 * Check time_from value by user activity limits, update time from if necessary
	 * 
	 * @param recordToAdd input/output object
	 * @throws CBusinessException in error case
	 * @throws CSecurityException in error case
	 */
	@SuppressWarnings("deprecation")
	private Long updateRecordTimeFromValueByUserActivityLimits(final CTimeStampRecord record) throws CBusinessException {

		Long message = null;
		
		// prepare input values for some helpful methods create temporary object
		CTimeSheetRecord tsRecord = new CTimeSheetRecord();

		final CUser employee = this.userDao.findById(record.getEmployeeId());
		tsRecord.setClient(employee.getClient());
		tsRecord.setOwner(employee);
		tsRecord.setActivity(this.activityDao.findById(record.getActivityId()));

		if (record.getDateFrom() != null) {
			final Calendar calendarDateFrom = Calendar.getInstance();
			calendarDateFrom.setTime(record.getDateFrom());
			CTimeUtils.convertToStartTime(calendarDateFrom);
			tsRecord.setTimeFrom(calendarDateFrom);
		}
		if (record.getDateTo() != null) {
			final Calendar calendarDateTo = Calendar.getInstance();
			calendarDateTo.setTime(record.getDateFrom());
			CTimeUtils.convertToStartTime(calendarDateTo);
			tsRecord.setTimeTo(calendarDateTo);
		}

		Boolean isInWorkDay = this.activityTimeLimitCheckService.timestampFallsToWorkingDay(tsRecord);
		Long dayType = isInWorkDay ? IDayTypeConstant.DAY_TYPE_WORKDAY : IDayTypeConstant.DAY_TYPE_FREEDAY;

		// record start time in minutes
		long recordTimeFrom = record.getDateFrom().getHours() * 60L + record.getDateFrom().getMinutes();

		String projectGroup = null;
		if (record.getProjectId() != null) {
			CProject project = projectDao.findById(record.getProjectId());
			projectGroup = project.getGroup();
		}

		// finds user activity limits
		List<CActivityInterval> limits = this.activityTimeLimitCheckService.getAllUserActivityLimitIntervals(record.getEmployeeId(), record.getActivityId(), dayType, projectGroup);
		if (limits != null && !limits.isEmpty()) {
			// vyhladaj najblizsi interval do buducnosti
			// AK NEEXISTUJE INTERVAL DO KTOREHO ZNACKA PADNE
			Boolean fallsIntoLimit = this.activityTimeLimitCheckService.getCheckActivityLimitBooleanValue(record.getEmployeeId(), tsRecord);
			if (!fallsIntoLimit) {
				CActivityInterval closestLimit = null;

				for (CActivityInterval limit : limits) {
					if (limit.getTime_from() != null) {
						// interval start time in minutes
						long intervalTimeFrom = limit.getTime_from().get(Calendar.HOUR_OF_DAY) * 60L + limit.getTime_from().get(Calendar.MINUTE);
						if (recordTimeFrom <= intervalTimeFrom) {
							// only candidate
							if (closestLimit == null) {
								closestLimit = limit;
							} else {
								long closestIntervalTimeFrom = closestLimit.getTime_from().get(Calendar.HOUR_OF_DAY) * 60L + closestLimit.getTime_from().get(Calendar.MINUTE);
								if (closestIntervalTimeFrom > intervalTimeFrom) {
									closestLimit = limit;
								}
							}
						}
					}
				}

				if (closestLimit != null) {
					// nastav record.timeFrom na zaciatok tohoto intervalu
					Date tmpDate = record.getDateFrom();
					tmpDate.setHours(closestLimit.getTime_from().getTime().getHours());
					tmpDate.setMinutes(closestLimit.getTime_from().getTime().getMinutes());
					tmpDate.setSeconds(closestLimit.getTime_from().getTime().getSeconds());
					record.setDateFrom(tmpDate);
					message = 0L; // hláška: Čas bol posunutý.
				}
			}
		}
		
		return message;
	}

	/**
	 * Adds record from intelligent data
	 * 
	 * @param record
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	private void addWorkingRecord(final CTimeStampAddRecord recordToAdd, boolean alertnessworkAlreadyGenerated) throws CBusinessException {
		final CTimeStampRecord record = new CTimeStampRecord();

		record.setProjectId(recordToAdd.getProjectId());
		record.setActivityId(recordToAdd.getActivityId());
		record.setEmployeeId(recordToAdd.getEmployeeId());
		record.setNote(recordToAdd.getNote());
		record.setOutsideWorkplace(recordToAdd.getOutside());
		record.setDateFrom(recordToAdd.getTime());
		record.setPhase(recordToAdd.getPhase());
		record.setHomeOffice(recordToAdd.getHomeOffice());
		this.add(record, alertnessworkAlreadyGenerated);
	}

	@Override
	public void modifyWorking(final Long id, final CTimeStampAddRecord recordToAdd) throws CBusinessException {
		final CTimeStampRecord record = new CTimeStampRecord();

		CTimeSheetRecord originalRecord = timesheetDao.findById(id);

		record.setProjectId(recordToAdd.getProjectId());
		record.setActivityId(recordToAdd.getActivityId());
		record.setEmployeeId(recordToAdd.getEmployeeId());
		record.setNote(recordToAdd.getNote());
		record.setOutsideWorkplace(recordToAdd.getOutside());
		record.setHomeOffice(recordToAdd.getHomeOffice());
		record.setPhase(recordToAdd.getPhase());

		// pri zmene poslednej nechceme dovolit menit cas, nasetujeme ho z
		// originalneho zaznamu z db
		record.setDateFrom(originalRecord.getTimeFrom().getTime());

		this.modify(id, record, Calendar.getInstance().getTime());
	}

	/**
	 * Closes last activity
	 * 
	 * @throws CBusinessException
	 */
	private void cloneLastWorkActivity(final Long userId, final Date from, CTimeStampAddRecord prevWorkButModifiedRecord) throws CBusinessException {
		final Calendar timeFrom = Calendar.getInstance();
		timeFrom.setTime(from);
		CTimeUtils.addMinute(timeFrom);

		// last working activity
		final CTimeSheetRecord workActivity = this.timesheetDao.findLastWorking(userId, timeFrom);

		final CTimeStampRecord record = new CTimeStampRecord();

		boolean projectChanged = projectChanged(prevWorkButModifiedRecord, workActivity);
		boolean activityChanged = activityChanged(prevWorkButModifiedRecord, workActivity);

		if (prevWorkButModifiedRecord != null) {
			record.setProjectId(prevWorkButModifiedRecord.getProjectId());
			record.setActivityId(prevWorkButModifiedRecord.getActivityId());
		}
		if (projectChanged || activityChanged) {
			record.setNote(prevWorkButModifiedRecord != null ? prevWorkButModifiedRecord.getNote() : null);
			record.setPhase(prevWorkButModifiedRecord != null ? prevWorkButModifiedRecord.getPhase() : null);
		} else {
			if (workActivity != null) {
				record.setNote(workActivity.getNote());
				record.setPhase(workActivity.getPhase());
			}
		}
		if (workActivity != null) {
			record.setEmployeeId(workActivity.getOwner().getId());
			record.setOutsideWorkplace(workActivity.getOutside());
			record.setHomeOffice(workActivity.getHomeOffice());
		}

		record.setDateFrom(timeFrom.getTime());

		this.add(record, false);
	}

	private boolean projectChanged(CTimeStampAddRecord prevWorkButModifiedRecord, CTimeSheetRecord workActivity) {
		boolean retVal = false;

		if ((prevWorkButModifiedRecord != null && prevWorkButModifiedRecord.getProjectId() != null) && (workActivity != null && workActivity.getProject() != null)) {
			retVal = !prevWorkButModifiedRecord.getProjectId().equals(workActivity.getProject().getId());
		}
		return retVal;
	}

	private boolean activityChanged(CTimeStampAddRecord prevWorkButModifiedRecord, CTimeSheetRecord workActivity) {
		boolean retVal = false;

		if ((prevWorkButModifiedRecord != null && prevWorkButModifiedRecord.getActivityId() != null) && (workActivity != null && workActivity.getActivity() != null)) {
			retVal = !prevWorkButModifiedRecord.getActivityId().equals(workActivity.getActivity().getId());
		}
		return retVal;
	}

	private boolean closePreviousActivity(Long employeeId, Date time, boolean closeAlertnessWorkOnly) throws CBusinessException {
		// return value
		boolean retVal = false;

		final Calendar timeOfEnd = Calendar.getInstance();
		timeOfEnd.setTime(time);
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(employeeId, timeOfEnd);

		// if there is something to close
		if ((lastActivity != null) && (lastActivity.getTimeTo() == null)) {
			
			// ak posledná aktivita nie je typu neukončená pohotovosť
			if (closeAlertnessWorkOnly && !(lastActivity.getActivity() != null && (lastActivity.getActivity().getId().equals(IActivityConstant.NOT_WORK_ALERTNESSWORK) || 
					lastActivity.getActivity().getId().equals(IActivityConstant.NOT_WORK_INTERACTIVEWORK)))) {
				return false;
			}
			
			// starting time
			final Calendar timeFrom = lastActivity.getTimeFrom();

			Calendar timeTo;
			if (timeFrom.get(Calendar.DAY_OF_YEAR) != timeOfEnd.get(Calendar.DAY_OF_YEAR)) {
				CActivity activity = lastActivity.getActivity();

				if (activity.getTimeMax() != null) {
					timeTo = (Calendar) timeFrom.clone();
					timeTo.set(Calendar.HOUR_OF_DAY, activity.getTimeMax().get(Calendar.HOUR_OF_DAY));
					timeTo.set(Calendar.MINUTE, activity.getTimeMax().get(Calendar.MINUTE));
					timeTo.set(Calendar.SECOND, 59);
					timeTo.set(Calendar.MILLISECOND, 999);

					if (activity.getHoursMax() != null) {
						// výkaz práce za predchádzajúci deň ukončiť v čase menšom z týchto 2 časov:
						// hodnota c_time_max v tabuľke aktivít pre danú aktivitu ku času c_time_from
						// pripočítať počet zostávajúcich
						// možních hodín danej aktivity za aktuálny rok (zostávajúce hodiny pre danú
						// aktivitu sa vypočítajú odpočítaním trvania všetkých platných záznamov s danou 
						// aktivitou za posledný rok od hodnoty c_hours_max pre danú aktivitu).

						Long actualDuration = timestampDao.getDurationOfActivityInMinutes(lastActivity.getOwner().getId(), activity.getId(), timeFrom.get(Calendar.YEAR), lastActivity.getId());
						long remainingDuration = activity.getHoursMax() * 60 - actualDuration;

						long maxDuration = (timeTo.getTimeInMillis() - timeFrom.getTimeInMillis()) / 60000;

						if (remainingDuration < maxDuration) {
							timeTo.setTimeInMillis(timeFrom.getTimeInMillis() + remainingDuration * 60000);
							CTimeUtils.convertToEndTime(timeTo);
							CTimeUtils.decreaseMinute(timeTo);
						}
					}
				} else {
					timeTo = CTimeUtils.convertToEOD((Calendar) timeFrom.clone());
				}
			} else
			// close actual activity
			{
				timeTo = CTimeUtils.convertToEndTime((Calendar) timeOfEnd.clone());

				// we can decrease time to
				if (!((timeTo.get(Calendar.MINUTE) == timeFrom.get(Calendar.MINUTE)) && (timeTo.get(Calendar.HOUR_OF_DAY) == timeFrom.get(Calendar.HOUR_OF_DAY)))) {
					CTimeUtils.decreaseMinute(timeTo);
				} else {
					// if hour matches and minute matches, we need to increase minute of the start
					// time
					// of the next activity by one
					retVal = true;
				}
			}
			lastActivity.setChangeTime(Calendar.getInstance());
			lastActivity.setTimeTo(timeTo);

			// SED-290 check user/activity/time limits and update if necessary
			this.activityTimeLimitCheckService.updateClosingTimestampTimeToValueByUserActivityLimits(lastActivity, Boolean.FALSE);

			// check: timestamp reason
			this.checkTimestampReason(lastActivity);

			this.checkActivityTimes(lastActivity);

			this.timesheetDao.saveOrUpdate(lastActivity);
		}
		return retVal;
	}
	
	/**
	 * Closes previous activity if it is not closed, and set parameters activity and
	 * phase from last activity object
	 * 
	 * @param record
	 * @return true if needed increase start time of next activity by 1 minute
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	// SED-290
	private boolean closePreviousActivity(CTimeStampAddRecord record) throws CBusinessException {
		return this.closePreviousActivity(record.getEmployeeId(), record.getTime(), false);
	}

	@Override
	public CTimeStampRecord getDetail(final Long timeStampId) {
		final CTimeSheetRecord record = this.timesheetDao.findById(timeStampId);
		return record.convert();
	}

	/**
	 * Checks if children users are active (checks only 1.lower level)
	 * 
	 * @param userToCheck
	 * @throws CBusinessException
	 */
	private void checkTimeOrder(final CTimeStampRecord record) throws CBusinessException {
		if ((record.getDateTo() != null) && record.getDateFrom().after(record.getDateTo())) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_FROM_AFTER_TO);
		}
	}

	/***
	 * 
	 * Checks if employee time sheet record is newer when compared with locked time.
	 * Throws exception if not.
	 * 
	 * @param record input checked record
	 * @throws CBusinessException when employee records are locked for the required
	 *                            time
	 */
	private void checkLockRecordsConditions(final CTimeSheetRecord record) throws CBusinessException {
		if (record.getOwner() != null) {
			List<CLockDate> listEmployeeLockParams = this.lockDateDao.findByUser(record.getOwner().getId());
			if (listEmployeeLockParams != null && !listEmployeeLockParams.isEmpty()) {
				CLockDate elp = listEmployeeLockParams.get(0);
				if (!elp.getTimestampLockTo().before(record.getTimeFrom())) {
					throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_EMPLOYEES_RECORDS_ARE_LOCKED);
				}
			}
		}
	}

	/**
	 * Checks user right to use the timestamp record with alertness and interactive
	 * work activities
	 * 
	 * @param record input record
	 * @throws CBusinessException in error case
	 */
	private void checkUserAlertnessWorkRight(final CTimeSheetRecord record) throws CBusinessException {
		// alertness of interactive work? - check user rights
		if (IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(record.getActivity().getId()) || IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(record.getActivity().getId())) {
			CUser employee = this.userDao.findById(record.getOwner().getId());
			if (!employee.getAllowedAlertnessWork()) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_MISSING_USER_RIGHT_FOR_ALERTNESS_WORK);
			}
		}

	}

	/**
	 * Checks time restrictions for the activity in the timestamp
	 * 
	 * @param record input record
	 * @throws CBusinessException in error case
	 */
	private void checkUserTimeRestrictionOfActivity(final CTimeSheetRecord tsRecord) throws CBusinessException {
		this.activityTimeLimitCheckService.checkActivityLimit(tsRecord.getOwner().getId(), tsRecord);
	}

	/**
	 * Checks time overlapping of entered record and data in DB
	 * 
	 * @param recordToCheck
	 * @throws CBusinessException
	 */
	private void checkTimesheetOverlapping(final CTimeSheetRecord recordToCheck) throws CBusinessException {
		final Calendar timeFrom = Calendar.getInstance();
		timeFrom.setTimeInMillis(recordToCheck.getTimeFrom().getTimeInMillis());
		CTimeUtils.convertToStartTime(timeFrom);

		Calendar timeTo = recordToCheck.getTimeTo();

		if (timeTo == null) {
			timeTo = Calendar.getInstance();
			timeTo.setTimeInMillis(timeFrom.getTimeInMillis());
			CTimeUtils.convertToEOD(timeTo);
		}

		final List<CTimeSheetRecord> records = this.timesheetDao.findAllOverlapping(timeFrom, timeTo, recordToCheck.getOwner().getId());

		boolean skracovaniePrestavokPrebehlo = false;

		for (final CTimeSheetRecord cTimeSheetRecord : records) {
			if (!cTimeSheetRecord.getId().equals(recordToCheck.getId())) {
				if (IActivityConstant.BREAK.equals(cTimeSheetRecord.getActivity().getId()) && recordToCheck.getActivity().getWorking()) {
					// SED-428 Umožniť zmenu a pridávanie časovej značky pracovnej aktiviny v prípade,
					// že sa zadaný časový interval bude prekrývať iba s časovými značkami prestávok.

					if (!skracovaniePrestavokPrebehlo) {
						// toto staci len raz zavolat aj ked sa prekryva s viac znackami
						timesheetDao.skracovaniePoznamok(recordToCheck.getId(), recordToCheck.getOwner().getId(), timeFrom, timeTo);
						skracovaniePrestavokPrebehlo = true;
					}

				} else {
					throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_OVERLAPPING);
				}
			}
		}
	}

	/**
	 * Checks if time record is
	 * 
	 * @param recordToCheck
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	private void checkTimeContinuing(final CTimeSheetRecord recordToCheck, final CUser changedBy) throws CBusinessException {
		final CTimeSheetRecord previous = this.timesheetDao.findLast(recordToCheck.getOwner().getId(), recordToCheck.getTimeFrom());
		if (previous != null) {
			if (previous.getTimeTo() == null) {
				return;
			}
			final Calendar dateTo = (Calendar) previous.getTimeTo().clone();
			final Calendar dateFrom = (Calendar) recordToCheck.getTimeFrom().clone();
			final int day = dateFrom.get(Calendar.DAY_OF_YEAR);
			final int day2 = dateTo.get(Calendar.DAY_OF_YEAR);

			if (day == day2) {
				CTimeUtils.convertToStartTime(dateFrom);
				CTimeUtils.convertToEndTime(dateTo);

				dateTo.add(Calendar.MILLISECOND, 1);
				if (dateFrom.getTimeInMillis() != dateTo.getTimeInMillis() && dateFrom.after(dateTo)) {

					// prepare "prestavka" record
					final CTimeSheetRecord add = new CTimeSheetRecord();
					add.setActivity(this.activityDao.findById(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_BREAK)));
					add.setChangedBy(changedBy);
					add.setChangeTime(Calendar.getInstance());
					add.setClient(changedBy.getClient());
					add.setCreatedBy(changedBy);
					add.setOwner(recordToCheck.getOwner());
					add.setNote(null);
					add.setOutside(false);
					add.setHomeOffice(false);
					add.setPhase(null);
					add.setProject(null);
					add.setValid(Boolean.TRUE);

					final Calendar breakDateFrom = (Calendar) previous.getTimeTo().clone();
					final Calendar breakDateTo = (Calendar) recordToCheck.getTimeFrom().clone();

					// correction -> next minute interval
					breakDateFrom.add(Calendar.MILLISECOND, 1);

					// correction -> previous minute interval
					CTimeUtils.convertToEndTime(breakDateTo);
					breakDateTo.add(Calendar.MINUTE, -1);

					add.setTimeFrom(breakDateFrom);
					add.setTimeTo(breakDateTo);

					// check: timestamp reason
					this.checkTimestampReason(add);

					add.setStatus(this.stateDao.findById(ITimeSheetRecordStates.ID_NEW));

					this.timesheetDao.saveOrUpdate(add);
				}
			}
		}
	}

	/**
	 * Return timestamp for add new timestamp screen and user timer panel or for
	 * reception panel - with respect to flag <code>calledFromUserTimerPanel</code>
	 * 
	 * @param userId                   user identifier of the selected user
	 * @param timeToPredefine          the date for which the timestamp will be
	 *                                 finding
	 * @param calledFromUserTimerPanel flag if the method is called from user timer
	 *                                 panel (or also for add timestamp screen)
	 * @return timestamp object
	 * @throws CSecurityException
	 */
	private CPredefinedInteligentTimeStamp loadPredefinedInteligentValueCoreMethod(final Long userId, final Date timeToPredefine, final Boolean calledFromUserTimerPanel) throws CSecurityException {
		Boolean isOldRecordClosed;
		final CPredefinedInteligentTimeStamp retVal = new CPredefinedInteligentTimeStamp();
		final Calendar time = Calendar.getInstance();
		if (timeToPredefine != null) {
			time.setTime(timeToPredefine);
		}

		final Calendar dateToSearch = CTimeUtils.convertToEndDate(time);
		final CTimeSheetRecord last = this.timesheetDao.findLast(userId, dateToSearch);
		final CTimeSheetRecord lastWorking = this.timesheetDao.findLastWorking(userId, dateToSearch);
		final Calendar date = CTimeUtils.convertToEndTime(last == null ? Calendar.getInstance() : (Calendar) last.getTimeFrom().clone());

		if (last == null) {
			// no record up today !
			retVal.setMode(CPredefinedInteligentTimeStamp.MODE_NOTHING);
			retVal.setModel(this.predefineInteligentNone(date));
		} else {

			isOldRecordClosed = this.checkOldRecordClosed(last, dateToSearch);
			if (!isOldRecordClosed) {
				// akoze som ukoncil predchadzajucu aktivitu, aj ked v skutocnosti este nie,
				// ale na obrazovke zobrazim iba tlacidlo start novy den...
				// pri starte sa mi uzavrie tato neuzavreta aktivita...
				retVal.setMode(CPredefinedInteligentTimeStamp.MODE_WORK_FINISHED);
				retVal.setModel(this.predefineInteligentValues(userId, date));
				retVal.setUnclosedDate(last.getTimeFrom().getTime());
			} else {
				if (last.getActivity().getWorking()) {

					// work finished
					if (last.getTimeTo() != null) {
						retVal.setMode(CPredefinedInteligentTimeStamp.MODE_WORK_FINISHED);
						retVal.setModel(this.predefineInteligentValues(userId, date));
					}
					// work started
					else {
						retVal.setMode(CPredefinedInteligentTimeStamp.MODE_WORK_STARTED);
						if (calledFromUserTimerPanel) {
							retVal.setModel(this.predefineInteligentValues(userId, date));
							retVal.setTimeStampId(last.getId());
						} else {
							retVal.setModel(this.predefineInteligentValues(userId, date, last.getOutside()));

							// zistim kolko hodin mam dnes odpracovanych
							String sumAndAverageTime = timesheetDao.getSumAndAverageTimeInTimeInterval(userId, Calendar.getInstance(), Calendar.getInstance());
							String sum = sumAndAverageTime.split("#")[0];
							String hours = sum.split(":")[0];
							String minutes = sum.split(":")[1];
							Integer odpracovaneMinuty = Integer.valueOf(hours) * 60 + Integer.valueOf(minutes);

							// zistim odprac. čas pre koniec práce na recepcii
							CUser user = this.userDao.findById(userId);
							Calendar intervalStopWorkRec = user.getClient().getIntervalStopWorkRec();
							if (intervalStopWorkRec != null) {
								Integer potrebneMinuty = intervalStopWorkRec.get(Calendar.HOUR_OF_DAY) * 60 + intervalStopWorkRec.get(Calendar.MINUTE);

								// porovnam
								Boolean inteligentStopWorkFlag = odpracovaneMinuty >= potrebneMinuty;

								// nastavim
								retVal.setInteligentStopWorkFlag(inteligentStopWorkFlag);
							}
						}
					}
				} else {
					// break finished
					if (last.getTimeTo() != null) {
						retVal.setMode(CPredefinedInteligentTimeStamp.MODE_WORKBREAK_FINISHED);
					}
					// work started
					else {
						retVal.setMode(CPredefinedInteligentTimeStamp.MODE_WORKBREAK_STARTED);
					}
					retVal.setModel(this.predefineInteligentValues(userId, date));
				}
			}
		}

		boolean notExistsPreviousWorkingActivity = lastWorking == null;
		boolean lastWorkingIsIvalid = (lastWorking != null)
				&& ((lastWorking.getProject() != null && !lastWorking.getProject().getValid()) || (lastWorking.getActivity() != null && !lastWorking.getActivity().getValid()));
		if (notExistsPreviousWorkingActivity || lastWorkingIsIvalid) {
			// posledny projekt/aktivita uz nie je platny/a alebo je uzivatel v systeme prvy raz
			// => SED-345 Doplnenie preddefinovaneho projektu a cinnosti
			// pouzije sa defaultny projekt/aktivita organizacie a len ak nie je pre organizaciu 
			// definovany defaultny projekt/aktivita, tak sa zobrazi obrazovka na vyber projektu/aktivity
			final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
			final CActivity defaultActivity = this.activityDao.findDefaultActivity(loggedUser.getClientInfo().getClientId(), false);
			final CProject defaultProject = this.projectDao.findDefaultProject(loggedUser.getClientInfo().getClientId());

			if (defaultActivity == null || defaultProject == null || Boolean.FALSE.equals(defaultActivity.getValid()) || Boolean.FALSE.equals(defaultProject.getValid())) {
				retVal.setOriginalMode(retVal.getMode());
				retVal.setMode(CPredefinedInteligentTimeStamp.MODE_INVALID_PROJECT_OR_ACTIVITY);
			} else {
				retVal.getModel().setActivityId(defaultActivity.getId());
				retVal.getModel().setProjectId(defaultProject.getId());
			}

		}

		return retVal;
	}

	/**
	 * @throws CSecurityException
	 * @see ITimesheetBaseService#loadPredefinedInteligentValueForUserTimerPanel(Long,
	 *      Date)
	 */
	@Override
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForUserTimerPanel(final Long userId, final Date timeToPredefine) throws CSecurityException {
		CPredefinedInteligentTimeStamp retVal = this.loadPredefinedInteligentValueCoreMethod(userId, timeToPredefine, Boolean.TRUE);

		CTimeStampAddRecord model = retVal.getModel();
		long durationOfLastTimestamp = 0;

		if (model.getProjectId() != null) {
			List<CTimeSheetRecord> userRecords4Day = timesheetDao.findSameUserRecords4Day(userId, Calendar.getInstance(), model.getProjectId(), model.getActivityId(), model.getNote(),
					model.getPhase(), model.getOutside(), model.getHomeOffice());

			for (CTimeSheetRecord record : userRecords4Day) {
				durationOfLastTimestamp += (record.getTimeTo() == null ? Calendar.getInstance().getTimeInMillis() : record.getTimeTo().getTimeInMillis()) - record.getTimeFrom().getTimeInMillis();
			}

			if (!userRecords4Day.isEmpty()) {
				durationOfLastTimestamp += 1000;
			}

			retVal.setDurationOfLastTimestamp(durationOfLastTimestamp);
		}

		return retVal;
	}

	/**
	 * @throws CSecurityException
	 * @see ITimesheetBaseService#loadPredefinedInteligentValueForReceptionPanel(Long,
	 *      Date)
	 */
	@Override
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForReceptionPanel(final Long userId, final Date timeToPredefine) throws CSecurityException {
		return this.loadPredefinedInteligentValueCoreMethod(userId, timeToPredefine, Boolean.FALSE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public CPredefinedTimeStamp loadPredefinedValue(final Long userId, Boolean forSubordinateEmployee) throws CBusinessException {
		CPredefinedTimeStamp retVal;

		final CTimeSheetRecord lastRecord = this.timesheetDao.findLast(userId);
		if (lastRecord != null) {
			// last record is fully filled
			if (lastRecord.isFullyFilled()) {
				// is on same day
				if (lastRecord.isOnSameDay(Calendar.getInstance())) {
					// last activity was working
					if (lastRecord.getActivity().getWorking()) {
						final CTimeSheetRecord lastNonWorkingRecord = this.timesheetDao.findLastNonWorking(userId);
						if (lastNonWorkingRecord != null) {
							final CTimeStampRecord record = lastNonWorkingRecord.convert();
							record.setDateFrom(Calendar.getInstance().getTime());
							record.setDateTo(null);
							record.setProjectId(null);

							retVal = new CPredefinedTimeStamp(null, IScreenMode.MODE_ADD, record);
						} else {
							retVal = null;
						}
					}
					// last activity is not working
					else {
						final CTimeSheetRecord lastWorkingRecord = this.timesheetDao.findLastWorking(userId);
						if (lastWorkingRecord != null) {
							final CTimeStampRecord record = lastWorkingRecord.convert();
							record.setDateFrom(Calendar.getInstance().getTime());
							record.setDateTo(null);

							retVal = new CPredefinedTimeStamp(null, IScreenMode.MODE_ADD, record);
						} else {
							retVal = null;
						}
					}
				}
				// is not on same day
				else {
					// timestamp record
					final CTimeSheetRecord lastWorkingRecord = this.timesheetDao.findLastWorking(userId);
					if (lastWorkingRecord != null) {
						final CTimeStampRecord record = lastWorkingRecord.convert();
						record.setDateFrom(Calendar.getInstance().getTime());
						record.setDateTo(null);

						retVal = new CPredefinedTimeStamp(null, IScreenMode.MODE_ADD, record);
					} else {
						retVal = null;
					}
				}
			}
			// last record is not fully filled
			else {
				// is on same day
				if (lastRecord.isOnSameDay(Calendar.getInstance())) {
					// timestamp record
					final CTimeStampRecord record = lastRecord.convert();
					final Date dateTo = new Date(record.getDateFrom().getTime());

					final Calendar now = Calendar.getInstance();
					dateTo.setHours(now.get(Calendar.HOUR_OF_DAY));
					dateTo.setMinutes(now.get(Calendar.MINUTE));
					dateTo.setSeconds(0);

					if (!forSubordinateEmployee.booleanValue()) {
						// user works with own data - conditions for modification own time stamp
						record.setDateTo(dateTo);
					}

					retVal = new CPredefinedTimeStamp(lastRecord.getId(), IScreenMode.MODE_MODIFY, record);
				}
				// is not on same day
				else {
					// timestamp record
					final CTimeStampRecord record = lastRecord.convert();
					final Date dateTo = new Date(record.getDateFrom().getTime());
					dateTo.setHours(23);
					dateTo.setMinutes(59);
					dateTo.setSeconds(0);
					record.setDateTo(dateTo);

					retVal = new CPredefinedTimeStamp(lastRecord.getId(), IScreenMode.MODE_MODIFY, record);
				}
			}
		} else {
			retVal = null;
		}
		return retVal;
	}

	/**
	 * @see ITimesheetBaseService#modify(Long, CTimeStampRecord, Date)
	 */
	@Override
	public CLockRecord modify(final Long id, final CTimeStampRecord newRecord, final Date timestamp) throws CBusinessException {
		final CTimeSheetRecord modify = modifyBrwItem(id, newRecord, timestamp);

		// prepares return value
		final CLockRecord retVal = new CLockRecord();
		retVal.setId(modify.getId());
		retVal.setLastChangeDate(modify.getChangeTime().getTime());

		return retVal;
	}

	/**
	 * @see ITimesheetBaseService#delete(Long, CTimeStampRecord, Date)
	 */
	@Override
	public CLockRecord delete(final Long id) throws CBusinessException {
		final CTimeSheetRecord modify = deleteBrwItem(id);

		// prepares return value
		final CLockRecord retVal = new CLockRecord();
		retVal.setId(modify.getId());
		retVal.setLastChangeDate(modify.getChangeTime().getTime());

		return retVal;
	}

	/**
	 * @see ITimesheetBaseService#modifyBrwItem(Long, CTimeStampRecord, Date)
	 */
	@Override
	public CTimeSheetRecord modifyBrwItem(final Long id, final CTimeStampRecord newRecord, final Date timestamp) throws CBusinessException {
		// staré záznamy nemusia mať vyplnený home office
		if (newRecord.getHomeOffice() == null) {
			newRecord.setHomeOffice(Boolean.FALSE);
		}

		// ---- check block ----
		this.checkTimeOrder(newRecord);
		this.checkOutsideAndHomeOffice(newRecord);
		this.checkHomeOffice(newRecord);
		final CTimeSheetRecord modify = this.timesheetDao.findById(id);

		long modifyDuration = 0;
		long newRecordDuration = 0;

		// SED-796
		if (IActivityConstant.NOT_WORK_HOLIDAY.equals(modify.getActivity().getId())) {

			Calendar cloneDateToFromNewRecord = Calendar.getInstance();
			cloneDateToFromNewRecord.setTime((Date) newRecord.getDateTo().clone());
			CTimeUtils.convertToEndTime(cloneDateToFromNewRecord);
			// pre porovnanie dĺžky aktivít
			modifyDuration = modify.getTimeTo().getTimeInMillis() - modify.getTimeFrom().getTimeInMillis() + 1l;
			newRecordDuration = cloneDateToFromNewRecord.getTimeInMillis() - newRecord.getTimeFrom().getTime() + 1l;
		}

		// SED-787 - G - kontrola aby nebolo možné zmeniť aktivitu dovolenka na iný typ aktivity
		if (IActivityConstant.NOT_WORK_HOLIDAY.equals(modify.getActivity().getId()) && !(IActivityConstant.NOT_WORK_HOLIDAY.equals(newRecord.getActivityId()))) {
			throw new CBusinessException(CClientExceptionsMessages.CANNOT_CHANGE_HOLIDAY_ACTIVITY_TO_ANOTHER);
		}
		// SED-787 - G - kontrola aby nebolo možné zmeniť inú aktivitu ako dovolenka na aktivitu dovolenka
		else if (IActivityConstant.NOT_WORK_HOLIDAY.equals(newRecord.getActivityId()) && !(IActivityConstant.NOT_WORK_HOLIDAY.equals(modify.getActivity().getId()))) {
			throw new CBusinessException(CClientExceptionsMessages.CANNOT_CHANGE_ANOTHER_TO_HOLIDAY_ACTIVITY);
		}

		// check status
		modify.getStatus().modify();

		this.checkExistsConfirmedTimestampForCurrentDay(newRecord);

		// check timestamp validity
		if (timestamp.getTime() < modify.getChangeTime().getTimeInMillis()) {
			throw new CBusinessException(CClientExceptionsMessages.OLD_RECORD_SHOWN);
		}

		// overlaping check
		final Calendar dateFrom = Calendar.getInstance();
		Calendar dateTo;
		dateFrom.setTime(newRecord.getDateFrom());
		CTimeUtils.convertToStartTime(dateFrom);

		if (newRecord.getDateTo() != null) {
			dateTo = Calendar.getInstance();
			dateTo.setTime(newRecord.getDateTo());
			CTimeUtils.convertToEndTime(dateTo);
		} else {
			// ak som nevyplnil cas do... (napriklad som ho zmazal...)
			dateTo = null;
		}
		// check lock conditions
		this.checkLockRecordsConditions(modify);

		// let's do it
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// tento cikus je tu kvoli tomu, ze pri pridavani/modifikovani poloziek v tabulke sa odchytavaju vynimky 
		// a dalej sa posielaju medzi normalnymi datami :-( takze o "rollback" sa musime starat sami 
		// toto bude treba prekodovat!!! (teraz zial tlaci cas)
		modify.setTimeFrom(dateFrom);
		modify.setTimeTo(dateTo);

		modify.setChangedBy(changedBy);
		modify.setChangeTime(Calendar.getInstance());
		modify.setClient(changedBy.getClient());
		modify.setOwner(this.userDao.findById(newRecord.getEmployeeId()));

		// ak aktivita je null, znamena to, ze uzivatel nevybral ziadnu working aktivitu... takze vlozime defaultnu aktivitu...
		modify.setActivity(null != newRecord.getActivityId() ? this.activityDao.findById(newRecord.getActivityId())
				: this.activityDao.findById(new Long(CPredefinedInteligentTimeStamp.ACTIVITY_WORKING_DEFAULT)));

		modify.setNote(newRecord.getNote());

		// SED-333 - etapa môže mať max. 30 znakov
		if (newRecord.getPhase() != null ? newRecord.getPhase().length() > 29 : false) {
			modify.setPhase(newRecord.getPhase().substring(0, 29));
		} else {
			modify.setPhase(newRecord.getPhase());
		}

		modify.setReason(null != newRecord.getRequestReasonId() ? this.requestReasonDao.findById(newRecord.getRequestReasonId()) : null);

		modify.setOutside(newRecord.getOutsideWorkplace() == null ? false : newRecord.getOutsideWorkplace());
		modify.setHomeOffice(newRecord.getHomeOffice() == null ? false : newRecord.getHomeOffice());

		// not mandatory field
		if ((newRecord.getProjectId() != null) && (newRecord.getProjectId() != ISearchConstants.NONE)) {
			modify.setProject(this.projectDao.findById(newRecord.getProjectId()));
		} else {
			modify.setProject(null);
		}

		modify.setValid(Boolean.TRUE);

		// check: time/user restriction of the activity (SED-290)
		this.checkUserTimeRestrictionOfActivity(modify);

		// check: user has role for alertness work
		this.checkUserAlertnessWorkRight(modify);

		this.checkRequestExistence4NonWorkingRecord(modify);

		// check lock condition
		this.checkLockRecordsConditions(modify);

		// check: timestamp reason
		this.checkTimestampReason(modify);

		// check: activity times
		this.checkActivityTimes(modify);

		CTimesheetUtils.setIssueSummaryFromJira(modify, jiraAuthenticationConfigurator, userDao);

		// check time overlapping
		this.checkTimesheetOverlapping(modify);

		// check: add nonworking activity for nonworking day
		checkAddNonWorkingRecordForNonWorkingDay(modify);

		// SED-787 - G - ak bola aktivita typu dovolenka zmenená na aktivitu typu dovolenka
		if (IActivityConstant.NOT_WORK_HOLIDAY.equals(modify.getActivity().getId()) && IActivityConstant.NOT_WORK_HOLIDAY.equals(newRecord.getActivityId())) {
			Boolean existsHolidayRecordForDayLastGen = this.requestDao.existsClientRequestsHoliday4DayLastGen(modify.getOwner().getId(), modify.getTimeFrom());

			if (existsHolidayRecordForDayLastGen) {
				if (modifyDuration > newRecordDuration) {
					// pripočítam pol dňa dovolenky
					updateRemainingDaysAfterBrwItemModify(modify.getOwner(), modify.getTimeFrom(), true);
				} else if (modifyDuration < newRecordDuration) {
					// odpočítam pol dňa dovolenky
					updateRemainingDaysAfterBrwItemModify(modify.getOwner(), modify.getTimeFrom(), false);
				}
			}
		}

		return modify;
	}

	/**
	 * @see ITimesheetBaseService#deleteBrwItem(Long, CTimeStampRecord, Date)
	 */
	private CTimeSheetRecord deleteBrwItem(final Long id) throws CBusinessException {
		final CTimeSheetRecord modify = this.timesheetDao.findById(id);

		final Boolean isHoliday = modify.getActivity().getId() == -3 ? true : false;

		Calendar date = (Calendar) modify.getTimeFrom().clone();

		// check status
		modify.getStatus().delete();

		// check lock conditions
		this.checkLockRecordsConditions(modify);

		// let's do it
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		modify.setChangedBy(changedBy);
		modify.setChangeTime(Calendar.getInstance());

		// zaznamy nemazeme len nastavime priznak na false
		modify.setValid(Boolean.FALSE);

		Boolean existsHolidayRecordForDay = this.requestDao.existsClientRequestsHoliday4Day(modify.getOwner().getId(), date);
		Boolean existsHolidayRecordForDayLastGen = this.requestDao.existsClientRequestsHoliday4DayLastGen(modify.getOwner().getId(), date);

		if ((!existsHolidayRecordForDay && isHoliday) || (existsHolidayRecordForDayLastGen && isHoliday)) {
			long hours4 = 1000l * 3600l * 4l;
			long lDuration = modify.getTimeTo().getTimeInMillis() - modify.getTimeFrom().getTimeInMillis() + 1l;

			Boolean halfDayVacation = Boolean.FALSE;

			if (lDuration == hours4) {
				halfDayVacation = Boolean.TRUE;
			}
			this.updateRemainingDaysAfterBrwItemDelete(modify.getOwner(), date, halfDayVacation);
		}

		this.timesheetDao.saveOrUpdate(modify);

		return modify;
	}

	/**
	 * Nothing to predefine
	 * 
	 * @return
	 */
	private CTimeStampAddRecord predefineInteligentNone(final Calendar date) {
		final CTimeStampAddRecord retVal = new CTimeStampAddRecord();
		retVal.setTime(date.getTime());

		return retVal;
	}

	/**
	 * 
	 * @return
	 */
	private CTimeStampAddRecord predefineInteligentValues(final Long userId, final Calendar date) {
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(userId, date);

		final CTimeSheetRecord lastWorking = this.timesheetDao.findLastWorking(userId, date);

		final CTimeStampAddRecord retVal = new CTimeStampAddRecord();

		// retVal.time
		if (lastActivity.getTimeFrom().get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {

			Calendar dateNew;

			final Calendar lastFrom = lastActivity.getTimeFrom();
			dateNew = Calendar.getInstance();

			if (lastFrom.getTimeInMillis() > dateNew.getTimeInMillis()) {
				dateNew = (Calendar) lastFrom.clone();
			}

			retVal.setTime(dateNew.getTime());
		} else {
			retVal.setTime(Calendar.getInstance().getTime());
		}

		retVal.setOutside(false);
		retVal.setHomeOffice(false);

		if (lastWorking != null) {
			// activity is mandatory
			retVal.setActivityId(lastWorking.getActivity().getId());
			retVal.setActivityName(lastWorking.getActivity().getName());

			// optional fields
			retVal.setOutside(lastWorking.getOutside());

			// oprava systémovej chyby
			retVal.setHomeOffice(lastWorking.getHomeOffice() == null ? Boolean.FALSE : lastWorking.getHomeOffice());

			if (IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(lastActivity.getActivity().getId()) || IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(lastActivity.getActivity().getId())) {
				// Projekt, etapa a poznámka sú aktuálne v časovači na dashboarde preberané z
				// posedného záznamu výkazu práce s pracovnou aktivitou. Po novom ich preberať 
				// zo záznamu výkazu práce s aktivitou pracovnú alebo s aktivitou pohotovosť a zásah.

				retVal.setNote(lastActivity.getNote());
				retVal.setPhase(lastActivity.getPhase());

				if (lastActivity.getProject() != null) {
					retVal.setProjectId(lastActivity.getProject().getId());
					retVal.setProjectName(lastActivity.getProject().getName());
				} else {
					retVal.setProjectId(null);
				}
			} else {
				retVal.setNote(lastWorking.getNote());
				retVal.setPhase(lastWorking.getPhase());
				if (lastWorking.getProject() != null) {
					retVal.setProjectId(lastWorking.getProject().getId());
					retVal.setProjectName(lastWorking.getProject().getName());
				} else {
					CUser user = this.userDao.findById(userId);
					final CProject defaultProject = this.projectDao.findDefaultProject(user.getClient().getId());
					if (defaultProject != null) {
						retVal.setProjectId(defaultProject.getId());
						retVal.setProjectName(defaultProject.getName());
					}
				}
			}
		} else {
			retVal.setProjectId(null);
		}

		final CTimeSheetRecord lastNonWorking = this.timesheetDao.findLastNonWorking(userId, date);
		if ((lastNonWorking != null) && (lastNonWorking.getActivity() != null)) {
			retVal.setNonWorkingActivityId(lastNonWorking.getActivity().getId());
			if (lastNonWorking.getReason() != null) {
				retVal.setReasonId(lastNonWorking.getReason().getId());
			}
		}

		return retVal;
	}

	private CTimeStampAddRecord predefineInteligentValues(final Long userId, final Calendar date, final Boolean outsideWork) {
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(userId, date);

		// proposed that ousideFlag = false
		final CTimeSheetRecord lastWorking = this.timesheetDao.findLastWorking(userId, date);

		CTimeSheetRecord lastWorkingNonExternal = this.timesheetDao.findLastWorkingNonExternal(userId, date);

		final CTimeStampAddRecord retVal = new CTimeStampAddRecord();

		// retVal.time
		if (lastActivity.getTimeFrom().get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
			final Calendar lastTo = lastActivity.getTimeTo();

			Calendar dateNew;
			// to fill time from
			if (lastTo != null) {
				dateNew = (Calendar) lastTo.clone();
				dateNew.add(Calendar.MILLISECOND, 1);
			}
			// to fill time to
			else {
				final Calendar lastFrom = lastActivity.getTimeFrom();
				dateNew = Calendar.getInstance();

				if (lastFrom.getTimeInMillis() > dateNew.getTimeInMillis()) {
					dateNew = (Calendar) lastFrom.clone();
				}
			}

			retVal.setTime(dateNew.getTime());
		} else {
			retVal.setTime(Calendar.getInstance().getTime());
		}

		retVal.setOutside(false);
		retVal.setHomeOffice(false);

		if (lastWorking != null) {
			// activity is mandatory
			retVal.setActivityId(lastWorking.getActivity().getId());

			// optional fields
			retVal.setOutside(lastWorking.getOutside());
			retVal.setHomeOffice(lastWorking.getHomeOffice());
			retVal.setNote(lastWorking.getNote());
			retVal.setPhase(lastWorking.getPhase());

			if (lastWorking.getProject() != null) {
				retVal.setProjectId(lastWorking.getProject().getId());
			} else {
				retVal.setProjectId(null);
			}

			if (outsideWork) {
				// we have opened outside work - return previous inside work project/activity
				if (lastWorkingNonExternal != null) {
					retVal.setActivityId(lastWorkingNonExternal.getActivity().getId());
					retVal.setProjectId(lastWorkingNonExternal.getProject().getId());
					retVal.setNote(lastWorkingNonExternal.getNote());
					retVal.setPhase(lastWorkingNonExternal.getPhase());
				}
			}
		} else {
			retVal.setProjectId(null);
		}

		final CTimeSheetRecord lastNonWorking = this.timesheetDao.findLastNonWorking(userId, date);
		if ((lastNonWorking != null) && (lastNonWorking.getActivity() != null)) {
			retVal.setNonWorkingActivityId(lastNonWorking.getActivity().getId());
		}

		return retVal;
	}

	@Override
	public Long startNonWorking(final CTimeStampAddRecord record) throws CBusinessException {
		final Calendar timeOfEnd = Calendar.getInstance();
		timeOfEnd.setTime(record.getTime());
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(record.getEmployeeId(), timeOfEnd);

		boolean isLastRecordClosed = lastActivity.getTimeTo() != null;

		if (this.closePreviousActivity(record)) {
			CTimeUtils.addMinute(record.getTime());
		}

		if (!isLastRecordClosed) {
			this.addContinuationOfLastRecord(record);
		}

		return this.addNonWorkingRecord(record, true);
	}

	@Override
	public void startWorking(final CTimeStampAddRecord record) throws CBusinessException {
		final Calendar timeOfEnd = Calendar.getInstance();
		timeOfEnd.setTime(record.getTime());
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(record.getEmployeeId(), timeOfEnd);

		// SED-854 - pri stlačení Začať novú posielam Home Office null tak si
		// musím zistiť hodnotu a nasetovať ju tu
		if (record.getHomeOffice() == null) {
			final CTimeSheetRecord lastWorkingActivity = this.timesheetDao.findLastWorking(record.getEmployeeId(), timeOfEnd);

			/*
			 * SED-856 b - ak dostanem home office null (napr. z tabletu, lebo neposiela
			 * príznak home office), dotiahnem príznak z predchádzajúcej ČZ len v prípade,
			 * že ho mám na tento aktuálny deň povolený, inak nastavím false. Ak by som
			 * prebral z poslednej časovej značky príznak true a home office na dnes nemám
			 * povolený, tak by spadla chyba na kontrole, či môžem mať home office, tablet
			 * ale túto chybu nevyhodí, ale nevytvorí novú ČZ
			 */
			Boolean allowedHomeOffice = requestService.isAllowedHomeOfficeForToday(record.getEmployeeId(), timeOfEnd.getTime());
			if (lastWorkingActivity != null && allowedHomeOffice) {
				record.setHomeOffice(lastWorkingActivity.getHomeOffice() == null ? Boolean.FALSE : lastWorkingActivity.getHomeOffice());
			} else {
				record.setHomeOffice(Boolean.FALSE);
			}
		}

		boolean isLastRecordClosed = true;

		if (lastActivity != null) {
			isLastRecordClosed = lastActivity.getTimeTo() != null;
		}

		if (this.closePreviousActivity(record)) {
			CTimeUtils.addMinute(record.getTime());
		}

		if (!isLastRecordClosed) {
			this.addContinuationOfLastRecord(record);
		}

		this.addWorkingRecord(record, true);
	}

	// SED-290
	@Override
	public void stopNonWorking(final CTimeStampAddRecord record, final boolean continueWork) throws CBusinessException {
		Calendar endDate = Calendar.getInstance();
		CTimeUtils.convertToEndTime(endDate);

		// count ending time
		final Calendar dateToSearch = CTimeUtils.convertToEndDate(Calendar.getInstance());
		final CTimeSheetRecord lastRecord = this.timesheetDao.findLast(record.getEmployeeId(), dateToSearch);
		if ((lastRecord.getTimeTo() == null) && lastRecord.getTimeFrom().after(endDate)) {
			endDate = (Calendar) lastRecord.getTimeFrom().clone();
			CTimeUtils.convertToEndTime(endDate);
		}

		final CTimeSheetRecord timeRecord = this.timesheetDao.findLastNonWorking(record.getEmployeeId(), endDate);

		// data for modification old record
		final CTimeStampRecord newRecord = new CTimeStampRecord();
		newRecord.setOutsideWorkplace(false);
		newRecord.setHomeOffice(false);
		newRecord.setDateFrom(timeRecord.getTimeFrom().getTime());
		newRecord.setNote(timeRecord.getNote() != null ? timeRecord.getNote() : "");
		newRecord.setDateTo(record.getTime());
		newRecord.setActivityId(record.getNonWorkingActivityId());
		newRecord.setEmployeeId(record.getEmployeeId());

		if (IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(record.getNonWorkingActivityId()) || IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(record.getNonWorkingActivityId())) {
			// SED-406 Priradenie projektu k pohotovosti
			// pri ukonceni zasahu by sa nemal projekt,etapa,poznamka zasahu
			// zmenit
			newRecord.setProjectId(timeRecord.getProject().getId());
			newRecord.setPhase(timeRecord.getPhase());
		} else {
			newRecord.setProjectId(null);
			newRecord.setPhase("");
		}

		this.modify(timeRecord.getId(), newRecord, Calendar.getInstance().getTime());

		// if new work should continue
		if (continueWork) {
			this.cloneLastWorkActivity(record.getEmployeeId(), record.getTime(), record);
		}
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
	// SED-290
	@Override
	public void stopNonWorking(final CTimeStampAddRecord record) throws CBusinessException {
		// zastavim nepracovnu aktivitu s tym, ze nepokracujem v praci...
		this.stopNonWorking(record, false);

		// vlozim si novu casovu znacku so zmenou, ktoru som vykonal v ramci
		// nepracovnej aktivity... ako cas od bude cas ukoncenia nepracovnej
		// aktivity + 1 minuta...
		final Date timeFrom = record.getTime();
		CTimeUtils.addMinute(timeFrom);
		final CTimeStampRecord newRecord = new CTimeStampRecord();
		newRecord.setProjectId(record.getProjectId());
		newRecord.setActivityId(record.getActivityId());
		newRecord.setEmployeeId(record.getEmployeeId());
		newRecord.setNote(record.getNote());
		newRecord.setPhase(record.getPhase());
		newRecord.setOutsideWorkplace(record.getOutside());
		newRecord.setHomeOffice(record.getHomeOffice());
		newRecord.setDateFrom(timeFrom);

		this.add(newRecord, false);
	}

	@Override
	public void stopInteractiveWork(final CTimeStampAddRecord record) throws CBusinessException {
		// zastavim nepracovnu aktivitu s tym, ze nepokracujem v praci...
		this.stopNonWorking(record, false);

		final Date timeFrom = record.getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(timeFrom);
		List<CTimeSheetRecord> last2 = timesheetDao.findLast(record.getEmployeeId(), cal, 2);

		CTimeSheetRecord previous = last2.get(1);

		if (IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(previous.getActivity().getId())) {
			// zacni pohotovost ak pred zasahom som mal pohotovost
			// a projekt pohotovosti sa ma nastavit na projekt predchadzajucej
			// pohotovosti (pred zasahom).
			CTimeUtils.addMinute(timeFrom);
			final CTimeStampRecord newRecord = new CTimeStampRecord();
			newRecord.setProjectId(previous.getProject().getId());
			newRecord.setActivityId(IActivityConstant.NOT_WORK_ALERTNESSWORK);
			newRecord.setEmployeeId(record.getEmployeeId());
			newRecord.setOutsideWorkplace(Boolean.FALSE);
			newRecord.setHomeOffice(Boolean.FALSE);
			newRecord.setDateFrom(timeFrom);
			newRecord.setNote(previous.getNote());
			newRecord.setPhase(previous.getPhase());

			try {
				this.add(newRecord, false);
			} catch (CBusinessException ex) {
				if (!CClientExceptionsMessages.TIMESTAMP_USER_ACTIVITY_RESTRICTION_ERROR.equals(ex.getMessage())) {
					// ak zaciatok pohotovosti nesplna obmedzenia tak ju nezacnem, ale zasah chcem
					// korektne ukoncit
					throw ex;
				}
			}
		}
	}

	// SED-290
	@Override
	public void stopWorking(final CTimeStampAddRecord record) throws CBusinessException {
		final CTimeSheetRecord timeRecord = this.timesheetDao.findLast(record.getEmployeeId(), Calendar.getInstance());

		// transferdata for modification
		final CTimeStampRecord newRecord = new CTimeStampRecord();
		newRecord.setProjectId(record.getProjectId());
		newRecord.setActivityId(record.getActivityId());
		newRecord.setEmployeeId(record.getEmployeeId());
		newRecord.setNote(record.getNote());
		newRecord.setPhase(record.getPhase());
		newRecord.setOutsideWorkplace(record.getOutside());
		newRecord.setHomeOffice(record.getHomeOffice());
		newRecord.setDateFrom(timeRecord.getTimeFrom().getTime());

		// datum chceme z datumu od aby sa nestalo ze sa vytvori znacka cez dva dni
		// cez clone sa naklonuje dátum, nemôžeme ho priradiť priamo, pretože by
		// sa cez dateTo.set() modifikovala entita timeRecord
		Calendar dateTo = (Calendar) timeRecord.getTimeFrom().clone();
		dateTo.set(Calendar.HOUR_OF_DAY, record.getTime().getHours());
		dateTo.set(Calendar.MINUTE, record.getTime().getMinutes());

		newRecord.setDateTo(dateTo.getTime());

		this.modify(timeRecord.getId(), newRecord, Calendar.getInstance().getTime());
	}

	@Override
	public Map<Long, Long> getLastProjectToActivityRelationMap() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final Map<Long, Long> map = new HashMap<>();
		final List<Object[]> records = this.timesheetDao.findLastActivityForProjectMap(loggedUser.getUserId());
		for (final Object[] record : records) {
			final BigDecimal projectId = (BigDecimal) record[0];
			final BigDecimal activityId = (BigDecimal) record[1];
			map.put(projectId.longValue(), null == activityId ? null : activityId.longValue());
		}
		return map;
	}

	@Override
	public CTimeStampAddRecord getUnclosedTimesheet(final Long userId) {
		final CTimeSheetRecord record = this.timesheetDao.findUnclosedRecord(userId);
		if (null != record) {
			return this.convertToScreenModel(record);
		}
		return null;
	}

	private Boolean checkOldRecordClosed(final CTimeSheetRecord lastRecord, final Calendar dateToSearch) {
		if ((null != lastRecord) && (null == lastRecord.getTimeTo())) {
			return DateUtils.isSameDay((Calendar) lastRecord.getTimeFrom().clone(), dateToSearch);
		}
		return Boolean.TRUE;
	}

	private CTimeStampAddRecord convertToScreenModel(final CTimeSheetRecord record) {
		final CTimeStampAddRecord model = new CTimeStampAddRecord();
		model.setEmployeeId(record.getOwner().getId());
		model.setNonWorkingActivityId(null);
		model.setNote(record.getNote());
		model.setOutside(record.getOutside());
		model.setPhase(record.getPhase());
		model.setTime(record.getTimeFrom().getTime());
		if (null != record.getActivity()) {
			model.setActivityId(record.getActivity().getId());
		}
		if (null != record.getProject()) {
			model.setProjectId(record.getProject().getId());
		}
		return model;
	}

	/**
	 * @see ITimesheetBaseService#getListOfUsersWithCorruptedSummaryReport(Long,
	 *      Date, Date)
	 */
	@Override
	public List<String> getListOfUsersWithCorruptedSummaryReport(Long requestorUserId, Date from, Date to) throws CBusinessException {
		List<String> list = new ArrayList<>();

		CUser requestorUser = this.userDao.findById(requestorUserId);

		Calendar timeFrom = Calendar.getInstance();
		timeFrom.setTime(from);

		Calendar timeTo = Calendar.getInstance();
		timeTo.setTime(to);

		final List<Object> records = this.timesheetDao.findListUsersIdsWithCorruptedSummaryReportForClient(requestorUser.getClient().getId(), timeFrom, timeTo);
		for (final Object record : records) {
			final BigDecimal userId = (BigDecimal) record;
			CUser user = this.userDao.findById(userId.longValue());
			String name = user.getName();
			String surName = user.getSurname();

			list.add(name + " " + surName);
		}
		list = new ArrayList<>();

		return list;
	}

	/**
	 * @throws CBusinessException
	 * @see ITimesheetBaseService#generateApprovedEmployeesAbsenceRecords(Long, Date)
	 * 
	 * AUTOMATICKÉ GENEROVANIE ČASOVÝCH ZNAČIEK NA ZÁKLADE ŽIADOSTÍ
	 */
	@Override
	public Boolean generateApprovedEmployeesAbsenceRecords(Long requestedUserId, Date dateFrom, Date dateTo) throws CBusinessException {
		Boolean retVal = Boolean.TRUE;

		if (requestedUserId == null) {
			return retVal;
		}

		final CUser requestedUser = this.userDao.findById(requestedUserId);

		Date intervalDate = dateFrom;
		Calendar checkDate = Calendar.getInstance();
		checkDate.setTime(intervalDate);

		Calendar endDate = Calendar.getInstance();
		endDate.setTime(dateTo);

		long lengthDateIntervalInMs = endDate.getTimeInMillis() - checkDate.getTimeInMillis();
		long lengthDateIntervalInDays = lengthDateIntervalInMs / (24 * 3600000);

		if (lengthDateIntervalInDays > MAX_DAY_INTERVAL_LENGTH) {
			throw new CBusinessException(CClientExceptionsMessages.MAX_INTERVAL_DATE_GENERATE_EMPLOYEE_ABSENCE_ERROR);
		}

		// find approved requests for the day/organization
		List<CRequest> requests = this.requestDao.findApprovedClientRequests4Day(requestedUser.getClient().getId(), checkDate);

		// for all check:
		for (CRequest request : requests) {
			logger.info("Generujem pre " + request.getOwner().getSurname() + " od " + request.getDateFrom().getTime() + " do " + request.getDateTo().getTime());
			Calendar newDateLastGenHoliday = (Calendar) request.getDateTo().clone();

			// určuje či generujem časovú značku pre pol dňa
			Boolean generateHalfDay = Boolean.FALSE;
			// určuje či generujem časovú značku pre celý deň
			Boolean generateDay = Boolean.FALSE;
			// určuje či bola vygenerovaná časová značka pre celý deň
			Boolean timestampForDayWasGenerated = Boolean.FALSE;
			// určuje či bola vygenerovaná časová značka pre pol dňa
			Boolean timestampForHalfDayWasGenerated = Boolean.FALSE;

			// generate timestamps only if request type allows it.
			if (request.getType().getAllowsGenerateTimestamps()) {
				CTimeSheetRecord addRecord = getNewRecordForGeneration(request, requestedUser);

				if (BigDecimal.valueOf(0.5).equals(BigDecimal.valueOf(request.getNumberWorkDays()))) {

					// if checkday is holiday don't generate timestamps
					if (generateTimestamps(requestedUser.getClient().getId(), request.getDateFrom())) {

						logger.info("Generujem pol dna dovolenky na den " + request.getDateFrom().getTime());

						// pokus o generovanie dovolenky na pol dňa, TRUE = generovanie dovolenky
						generateHalfDay = Boolean.TRUE;

						// if user hasn't any time record for the day
						Long userId = request.getOwner().getId();
						Boolean existsUserRecords = this.timesheetDao.existsUserRecords4Day(userId, request.getDateFrom());

						if (!existsUserRecords) {
							// then add temp record!!!

							// c_time_from = 8:01
							Calendar timeFrom = Calendar.getInstance();
							timeFrom.setTime(request.getDateFrom().getTime());
							timeFrom.set(Calendar.MILLISECOND, 0);
							timeFrom.set(Calendar.SECOND, 0);
							timeFrom.set(Calendar.MINUTE, 0);
							timeFrom.set(Calendar.HOUR_OF_DAY, 8);
							addRecord.setTimeFrom(timeFrom);
							// c_time_to = 12:00
							Calendar timeTo = Calendar.getInstance();
							timeTo.setTime(request.getDateFrom().getTime());
							timeTo.set(Calendar.MINUTE, 59);
							timeTo.set(Calendar.HOUR_OF_DAY, 11);
							timeTo.set(Calendar.SECOND, 59);
							timeTo.set(Calendar.MILLISECOND, 999);
							addRecord.setTimeTo(timeTo);

							try {
								// check lock conditions
								this.checkLockRecordsConditions(addRecord);

								final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(userId, addRecord.getTimeFrom());
								
								boolean isLastRecordClosed = lastActivity.getTimeTo() != null;

								if (this.closePreviousActivity(userId, addRecord.getTimeFrom().getTime(), true)) {
									CTimeUtils.addMinute(addRecord.getTimeFrom());
								}

								if (!isLastRecordClosed) {
									this.addContinuationOfLastRecord(userId, addRecord.getTimeFrom().getTime(), true, requestedUser);
								}

								this.timesheetDao.saveOrUpdate(addRecord);

								// záznam o dovolenke sa vygeneroval, nastavím TRUE aby sa nepripočítalo pol dňa dovolenky
								timestampForHalfDayWasGenerated = Boolean.TRUE;
							} catch (CBusinessException e) {
								// ok, pokracujem dalej
								logger.info("Nevygenerovane: " + e.getMessage(), e);
							}
						} else {
							List<CTimeSheetRecord> records = this.timesheetDao.findUserRecords4Day(userId, request.getDateFrom());
							boolean pokracuj = true;

							for (CTimeSheetRecord record : records) {
								// Ak tam je aspon jedna dovolenka tak koncim
								if (record.getActivity().getId().equals(IActivityConstant.NOT_WORK_HOLIDAY)) {
									pokracuj = false;
									// záznam o dovolenke už existuje, nastavím TRUE aby sa nepripočítalo pol dňa dovolenky
									timestampForHalfDayWasGenerated = Boolean.TRUE;
								}
							}

							if (pokracuj) {
								// aspon jeden zaznam tam musi byt
								Calendar timeFromPrvejCasovejZnacky = records.get(0).getTimeFrom();

								Calendar skoroStyriHodinyRano = Calendar.getInstance();
								skoroStyriHodinyRano.set(Calendar.MILLISECOND, 999);
								skoroStyriHodinyRano.set(Calendar.SECOND, 59);
								skoroStyriHodinyRano.set(Calendar.MINUTE, 59);
								skoroStyriHodinyRano.set(Calendar.HOUR_OF_DAY, 3);
								skoroStyriHodinyRano.set(Calendar.DAY_OF_YEAR, timeFromPrvejCasovejZnacky.get(Calendar.DAY_OF_YEAR));

								if (timeFromPrvejCasovejZnacky.after(skoroStyriHodinyRano)) {
									// Systém pri 0,5 dni dovolenky (ak existuje časová značka) 
									// nájde prvú platnú časovú značku zamestnanca za daný deň
									// a pokúsi sa vytvoriť časovú značku pre dovolenku 
									// na presne 4 hod. pred touto časovou značkou.

									// then add temp record!!!

									// c_time_from = timeFromPrvejCasovejZnacky
									// minus 4 hodiny
									Calendar timeFrom = (Calendar) timeFromPrvejCasovejZnacky.clone();
									timeFrom.add(Calendar.HOUR_OF_DAY, -4);
									addRecord.setTimeFrom(timeFrom);
									// c_time_to = timeFromPrvejCasovejZnacky
									// minus 1 minuta
									Calendar timeTo = (Calendar) timeFromPrvejCasovejZnacky.clone();
									timeTo.add(Calendar.MINUTE, -1);
									timeTo.set(Calendar.SECOND, 59);
									timeTo.set(Calendar.MILLISECOND, 999);
									addRecord.setTimeTo(timeTo);

									try {
										// check lock conditions
										this.checkLockRecordsConditions(addRecord);

										this.checkExistsConfirmedTimestampForCurrentDay(addRecord);
										
										final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(userId, addRecord.getTimeFrom());
										
										boolean isLastRecordClosed = lastActivity.getTimeTo() != null;

										if (this.closePreviousActivity(userId, addRecord.getTimeFrom().getTime(), true)) {
											CTimeUtils.addMinute(addRecord.getTimeFrom());
										}

										if (!isLastRecordClosed) {
											this.addContinuationOfLastRecord(userId, addRecord.getTimeFrom().getTime(), true, requestedUser);
										}
										
										this.timesheetDao.saveOrUpdate(addRecord);

										// záznam o dovolenke sa vygeneroval, nastavím TRUE aby sa nepripočítalo
										// pol dňa dovolenky
										timestampForHalfDayWasGenerated = Boolean.TRUE;
									} catch (CBusinessException e) {
										// ok, pokracujem dalej
										logger.info("Nevygenerovane: " + e.getMessage(), e);
									}
								} else {
									// Ak sa to nepodarí (prvá časová značka začína skôr ako v čase 4:00), pokúsi sa
									// ešte nájsť poslednú časovú značku za daný deň a vygenerovať to na 4 hod. 
									// po tejto časovej značke. Ak sa nepodarí ani to, časovú značku si bude 
									// musieť zamestnanec vytvoriť ručne tak ako doteraz.

									// aspon jeden zaznam tam musi byt
									Calendar timeToPoslednejCasovejZnacky = records.get(records.size() - 1).getTimeTo();

									Calendar osemHodinVecer = Calendar.getInstance();
									osemHodinVecer.set(Calendar.MILLISECOND, 0);
									osemHodinVecer.set(Calendar.SECOND, 0);
									osemHodinVecer.set(Calendar.MINUTE, 0);
									osemHodinVecer.set(Calendar.HOUR_OF_DAY, 20);
									osemHodinVecer.set(Calendar.DAY_OF_YEAR, timeToPoslednejCasovejZnacky.get(Calendar.DAY_OF_YEAR));

									if (osemHodinVecer.after(timeToPoslednejCasovejZnacky)) {
										// then add temp record!!!

										// c_time_from = timeFromPrvejCasovejZnacky plus 1 minuta
										Calendar timeFrom = (Calendar) timeToPoslednejCasovejZnacky.clone();
										timeFrom.add(Calendar.MINUTE, 1);
										timeFrom.set(Calendar.SECOND, 0);
										timeFrom.set(Calendar.MILLISECOND, 0);
										addRecord.setTimeFrom(timeFrom);
										// c_time_to = timeFromPrvejCasovejZnacky plus 4 hodiny
										Calendar timeTo = (Calendar) timeToPoslednejCasovejZnacky.clone();
										timeTo.add(Calendar.HOUR_OF_DAY, 4);
										addRecord.setTimeTo(timeTo);

										try {
											// check lock conditions
											this.checkLockRecordsConditions(addRecord);

											this.checkExistsConfirmedTimestampForCurrentDay(addRecord);
											
											final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(userId, addRecord.getTimeFrom());
											
											boolean isLastRecordClosed = lastActivity.getTimeTo() != null;

											if (this.closePreviousActivity(userId, addRecord.getTimeFrom().getTime(), true)) {
												CTimeUtils.addMinute(addRecord.getTimeFrom());
											}

											if (!isLastRecordClosed) {
												this.addContinuationOfLastRecord(userId, addRecord.getTimeFrom().getTime(), true, requestedUser);
											}
											
											this.timesheetDao.saveOrUpdate(addRecord);

											// záznam o dovolenke sa vygeneroval, nastavím TRUE aby sa
											// nepripočítalo pol dňa dovolenky
											timestampForHalfDayWasGenerated = Boolean.TRUE;
										} catch (CBusinessException e) {
											// ok, pokracujem dalej
											logger.info("Nevygenerovane: " + e.getMessage(), e);
										}
									}
								}
							}
						}
					}
				} else {
					// SED-412

					// časové značky generovať: od dňa po t_request.c_date_last_gen_holiday.
					// V prípade, že nebude vyplnený miesto neho generovanie
					// vykonávať od dňa t_request.c_date_from po menší z
					// aktuálneho dátum alebo t_request.c_date_to

					Calendar generateFrom = request.getDateLastGenHoliday();

					if (generateFrom == null) {
						generateFrom = (Calendar) request.getDateFrom().clone();
					}

					Calendar generateTo = Calendar.getInstance();
					CTimeUtils.convertToStartDate(generateTo);

					if (generateTo.after(request.getDateTo())) {
						generateTo = (Calendar) request.getDateTo().clone();
					} else {
						Long targetDayShift = requestedUser.getClient().getDayShiftAutoGenerateTimestamps();
						if (IShiftDayConstant.YESTERADAY.equals(targetDayShift)) {
							generateTo.add(Calendar.DAY_OF_MONTH, -1);
						}
					}

					logger.info("Generujem od " + generateFrom.getTime() + " do " + generateTo.getTime());

					newDateLastGenHoliday.setTime(generateTo.getTime());
					for (Date date = generateFrom.getTime(); !generateFrom.after(generateTo); generateFrom.add(Calendar.DATE, 1), date = generateFrom.getTime()) {

						checkDate.setTime(date);

						// na začiatku cyklu si nastavím premenné používané pri pridávaní dní dovolenky na false
						generateDay = Boolean.FALSE;
						timestampForDayWasGenerated = Boolean.FALSE;

						// if checkday is holiday don't generate timestamps
						if (generateTimestamps(requestedUser.getClient().getId(), checkDate)) {
							logger.info("Generujem pre den " + date);

							// pokus o generovanie dovolenky, TRUE = generovanie dovolenky bolo spustené
							generateDay = Boolean.TRUE;

							// if user hasn't any time record for the day
							Long userId = request.getOwner().getId();
							Boolean existsUserRecords = this.timesheetDao.existsUserRecords4Day(userId, checkDate);

							if (!existsUserRecords) {
								// then add temp record!!!
								addRecord = getNewRecordForGeneration(request, requestedUser);

								// c_time_from = 8:00
								Calendar timeFrom = (Calendar) checkDate.clone();
								timeFrom.set(Calendar.MILLISECOND, 0);
								timeFrom.set(Calendar.SECOND, 0);
								timeFrom.set(Calendar.MINUTE, 0);
								timeFrom.set(Calendar.HOUR_OF_DAY, 8);
								addRecord.setTimeFrom(timeFrom);
								// c_time_to = 15:59
								Calendar timeTo = (Calendar) checkDate.clone();
								timeTo.set(Calendar.MILLISECOND, 0);
								timeTo.set(Calendar.SECOND, 0);
								timeTo.set(Calendar.MINUTE, 59);
								timeTo.set(Calendar.HOUR_OF_DAY, 15);
								timeTo.set(Calendar.SECOND, 59);
								timeTo.set(Calendar.MILLISECOND, 999);
								addRecord.setTimeTo(timeTo);

								try {

									// ak je poslená aktivita neukončená pohotovosť tak vrátim záznam inak null
									CTimeSheetRecord lastActivityUnfinishedAlertnessWork = this.getLastActivityUnfinishedAlertnessWork(userId, addRecord);
									
									// check lock conditions
									this.checkLockRecordsConditions(addRecord);
									// check: timestamp reason
									this.checkTimestampReason(addRecord);

									// check closed last timestamp
									boolean isOldRecordClosed = this.checkClosedLastTimestamp(userId, checkDate.getTime());

									if (!isOldRecordClosed) {
										// tieto riadky ukončia/vygenerujú pohotovosť
										this.closePreviousActivity(userId, addRecord.getTimeFrom().getTime(), true);
										this.addContinuationOfLastRecord(userId, addRecord.getTimeFrom().getTime(), true, requestedUser);
									}

									this.timesheetDao.saveOrUpdate(addRecord);
									
									// ak je predchádzajúca ČZ neukončená pohotovosť, po vygenerovaní dovolenky/náhradného voľna vygenerujem novú ČZ otvorenej pohotovosti 
									if (lastActivityUnfinishedAlertnessWork != null && addRecord.getActivity() != null && (addRecord.getActivity().getId().equals(IActivityConstant.NOT_WORK_HOLIDAY) || 
											addRecord.getActivity().getId().equals(IActivityConstant.NOT_WORK_REPLWORK))) {
										
										Calendar startDate = (Calendar) addRecord.getTimeTo().clone();
										CTimeUtils.addMinute(startDate);
										
										CTimeSheetRecord newAlertnessWorkRecord = new CTimeSheetRecord();
										
										newAlertnessWorkRecord.setActivity(lastActivityUnfinishedAlertnessWork.getActivity());
										newAlertnessWorkRecord.setClient(lastActivityUnfinishedAlertnessWork.getClient());
										newAlertnessWorkRecord.setProject(lastActivityUnfinishedAlertnessWork.getProject());
										newAlertnessWorkRecord.setChangeTime(Calendar.getInstance());
										newAlertnessWorkRecord.setTimeFrom(startDate);
										newAlertnessWorkRecord.setTimeTo(null);
										newAlertnessWorkRecord.setCreatedBy(requestedUser);
										newAlertnessWorkRecord.setChangedBy(requestedUser);
										newAlertnessWorkRecord.setOwner(lastActivityUnfinishedAlertnessWork.getOwner());
										newAlertnessWorkRecord.setValid(lastActivityUnfinishedAlertnessWork.getValid());
										newAlertnessWorkRecord.setNote(lastActivityUnfinishedAlertnessWork.getNote());
										newAlertnessWorkRecord.setPhase(lastActivityUnfinishedAlertnessWork.getPhase());
										newAlertnessWorkRecord.setOutside(lastActivityUnfinishedAlertnessWork.getOutside());
										newAlertnessWorkRecord.setReason(lastActivityUnfinishedAlertnessWork.getReason());
										newAlertnessWorkRecord.setStatus(lastActivityUnfinishedAlertnessWork.getStatus());
										newAlertnessWorkRecord.setHomeOffice(lastActivityUnfinishedAlertnessWork.getHomeOffice());
										
										CTimeStampRecord tsRecord = newAlertnessWorkRecord.convert();
										// posun času začiatku pohotovosti podľa systémových nastavení
										this.updateRecordTimeFromValueByUserActivityLimits(tsRecord);
										
										Calendar shiftedTimeFrom = Calendar.getInstance();
										shiftedTimeFrom.setTime(tsRecord.getDateFrom());
										
										newAlertnessWorkRecord.setTimeFrom(shiftedTimeFrom);

										this.timesheetDao.saveOrUpdate(newAlertnessWorkRecord);

									}
									
									// vygenerovala sa dovolenka, TRUE = generovanie dovolenky bolo úspešné
									timestampForDayWasGenerated = Boolean.TRUE;
									logger.info("Vygenerovane.");
								} catch (CBusinessException e) {
									// ok, pokracujem dalej
									logger.info("Nevygenerovane: " + e.getMessage(), e);
								}

								newDateLastGenHoliday.setTime(checkDate.getTime());
							} else {

								List<CTimeSheetRecord> records = this.timesheetDao.findUserRecords4Day(userId, checkDate);

								/*
								 * SED-806 - ak existuje medzi inými časovými značkami aj dovolenka, ktorá
								 * timestampForDayWasGenerated nastaví na TRUE, tak si nebudem všímať ostatné
								 * časové značky, ktoré mi timestampForDayWasGenerated nastavia na FALSE,
								 * vplyvom čoho sa následne vráti + 1 deň dovolenky, čo by bola chyba, lebo pri
								 * prechádzaní dovolenkových časových značiek som už vrátil používateľovi
								 * požadovaný počet dní dovolenky
								 */
								Boolean existHolidayRecord4Day = Boolean.FALSE;

								for (CTimeSheetRecord record : records) {
									if (record.getActivity().getId().equals(IActivityConstant.NOT_WORK_HOLIDAY)) {
										existHolidayRecord4Day = Boolean.TRUE;
									}
								}

								for (CTimeSheetRecord record : records) {
									// ak na daný deň vo výkaze práce existuje záznam na 4 hod. dovolenky a žiadosť
									// bola na celý deň, pripočítam pol dňa k zostávajúcim dňom dovolenky,
									if (record.getActivity().getId().equals(IActivityConstant.NOT_WORK_HOLIDAY)) {

										long hours4 = 1000l * 3600l * 4l;
										long hours8 = 1000l * 3600l * 8l;

										long lDuration = record.getTimeTo().getTimeInMillis() - record.getTimeFrom().getTimeInMillis() + 1l;

										if (lDuration == hours4) {
											// pripočítam len pol dňa dovolenky, lebo žiadosť je na 8h a časová
											// značka je vytvorená len na 4h
											this.updateRemainingDaysForGenerateRecordHalfDay(request, Boolean.TRUE);
											// záznam dovolenka existuje, stavím TRUE aby sa nepripočítal deň dovolenky
											timestampForDayWasGenerated = Boolean.TRUE;
											// SED-787 - A,B ak už existuje časová značka na pol dňa, nastavím dátum posledného generovania
											newDateLastGenHoliday.setTime(checkDate.getTime());
										} else if (lDuration == hours8) {
											// záznam dovolenka existuje, stavím TRUE aby sa nepripočítal deň dovolenky
											timestampForDayWasGenerated = Boolean.TRUE;
											// SED-787 - A,B ak už existuje časová značka na 8h, nastavím dátum posledného generovania
											newDateLastGenHoliday.setTime(checkDate.getTime());
										}
									} else if (!existHolidayRecord4Day) {
										// existuje iný záznam ako dovolenka, z toho dôvodu sa dovolenka sa nevygenerovala nastavím FALSE aby sa pripočítal deň dovolenky
										timestampForDayWasGenerated = Boolean.FALSE;
										// SED-787 - A,B ak už existuje časová značka okrem dovolenky, nastavím dátum posledného generovania
										newDateLastGenHoliday.setTime(checkDate.getTime());
									}
								}
							}
						} else {
							/*
							 * SED-954 - ak je spracúvaný deň nepracovný tak nastavím dátum posledného generovania na tento nepracovný deň,
							 * aby sa pri ďalšom generovaní nepripočítavali dni dovolenky navyše v prípade, že posledné dni na ktoré 
							 * je zadaná žiadosť sú nepracovné
							 */
							newDateLastGenHoliday.setTime(checkDate.getTime());
						}

						// ak sa generovala časová značka dovolenky pre celý deň, ale nebola vygenerovaná tak pripočítam dovolenku
						if (Boolean.TRUE.equals(generateDay)) {
							// SED-801 - dni dovolenky upravujem len ak je žiadosť dovolenka t.j.
							// fk_activity = -3, záznam nebol vygenerovaný
							if (Boolean.FALSE.equals(timestampForDayWasGenerated) && IActivityConstant.NOT_WORK_HOLIDAY.equals(request.getType().getActivityId())) {
								this.updateRemainingDaysForGenerateRecordHalfDay(request, Boolean.FALSE);
							}
						}
					}
				}

				// ak sa generovala časová značka dovolenky pre pol dňa, ale nebola vygenerovaná tak pripočítam dovolenku
				if (Boolean.TRUE.equals(generateHalfDay)) {
					// SED-801 - dni dovolenky upravujem len ak je žiadosť dovolenka t.j. fk_activity = -3, záznam nebol vygenerovaný
					if (Boolean.FALSE.equals(timestampForHalfDayWasGenerated) && IActivityConstant.NOT_WORK_HOLIDAY.equals(request.getType().getActivityId())) {
						this.updateRemainingDaysForGenerateRecordHalfDay(request, Boolean.TRUE);
					}
				}
			}

			request.setDateLastGenHoliday(newDateLastGenHoliday);
			request.setChangeTime(Calendar.getInstance());
			request.setChangedBy(requestedUser);
			this.requestDao.saveOrUpdate(request);

		} // for (CRequest request : requests)

		CParameterEntity clientUpdateParameter;
		List<CParameterEntity> clientUpdateParameters = this.parameterDao.findByNameForClient(requestedUser.getClient().getId(), IParameter.LAST_USER_ABSENCE_PROCESSING);
		if (!clientUpdateParameters.isEmpty()) {
			clientUpdateParameter = clientUpdateParameters.get(0);
		} else {
			clientUpdateParameter = new CParameterEntity();
			clientUpdateParameter.setClient(requestedUser.getClient());
			clientUpdateParameter.setName(IParameter.LAST_USER_ABSENCE_PROCESSING);
		}
		String sDate = CDateUtils.convertToDateString(endDate);
		clientUpdateParameter.setStringValue(sDate);
		this.parameterDao.saveOrUpdate(clientUpdateParameter);

		return retVal;
	}

	private CActivity getActivityByRequestType(CRequest request) throws CBusinessException {
		// fk_activity = -3 ak D || -4 ak NV || -5 ak PN || -6 aj PVP
		CActivity retVal = null;

		if (request.getType().getAllowsGenerateTimestamps()) {
			if (request.getType().getActivityId() != null) {
				retVal = this.activityDao.findById(request.getType().getActivityId());
			} else {
				Logger.getLogger(this.getClass()).error("Request hasnt associated activity, but in this case is required!. Request id =" + request.getId());
				throw new CBusinessException(CClientExceptionsMessages.ACTIVITY_UNKNOWN_TYPE);
			}
		}
		return retVal;
	}

	/**
	 * @see ITimesheetBaseService#generateUserTimestampsFromPreparedItems(Long,
	 *      Date, Date)
	 */
	@Override
	public String generateUserTimestampsFromPreparedItems(Long userId, Date dateFrom, Date dateTo, Long summaryWorkDurationInMinutes) throws CBusinessException {

		String retVal = "USER_TIMESTAMPS_GENRATING_FINISHED_WITH_SUCCESS";

		Calendar cdateFrom = Calendar.getInstance();
		cdateFrom.setTime(dateFrom);
		CTimeUtils.convertToStartDate(cdateFrom);

		Calendar cdateTo = Calendar.getInstance();
		cdateTo.setTime(dateTo);
		CTimeUtils.convertToEndDate(cdateTo);

		// check user timestamps in date interval
		final Boolean userHasUnclosedTimestamps = this.timesheetDao.hasUserUnclosedTimstempsInDateInterval(userId, cdateFrom, cdateTo);
		if (userHasUnclosedTimestamps) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_UNCLOSED_RECORD_IN_DATE_INTERVAL);
		}

		// take prepared temporary records for timestemps generating
		List<CTmpTimeSheetRecord> tmpRecords = this.tmpTimesheetDao.findPreparedDistributionsByUserInDateInterval(userId, cdateFrom, cdateTo);

		// take old user timestamps list
		List<CTimeSheetRecord> userOldTimestamps = this.timesheetDao.findUserWorkRecordsInDateInterval(userId, cdateFrom, cdateTo);

		Long tmpRecordsDuration = 0L;

		for (CTmpTimeSheetRecord tmpRecord : tmpRecords) {
			tmpRecordsDuration += tmpRecord.getDurationInMinutes();
		}

		Long difference = summaryWorkDurationInMinutes - tmpRecordsDuration;
		if (difference > 0) {
			// kvoli zaokruhlovaniu sa moze stat ze nejaka ta minutka chyba musime ju dat poslednemu zaznamu
			CTmpTimeSheetRecord last = tmpRecords.get(tmpRecords.size() - 1);
			Long lastDuration = last.getDurationInMinutes();
			last.setDurationInMinutes(lastDuration + difference);
		}

		// generate timestamps
		generateTimestamps(tmpRecords, userOldTimestamps);

		// save info about generation process
		saveGenerateParameters(userId, cdateFrom, cdateTo, summaryWorkDurationInMinutes, tmpRecords.get(0));

		// clear already processed items
		this.tmpTimesheetDao.deletePreparedDistributionsByUser(userId);

		return retVal;
	}

	/**
	 * Generates new timestamps in old timestamps frame.
	 * 
	 * @param userId            selected user identifier
	 * @param tmpRecords        input user required timestamp distribution
	 * @param userOldTimestamps list of old user timestamps
	 * @throws CSecurityException if user is not logged
	 * @throws CBusinessException
	 */
	private void generateTimestamps(List<CTmpTimeSheetRecord> tmpRecords, List<CTimeSheetRecord> userOldTimestamps) throws CBusinessException {
		// take first user timestamp
		int tsIdx = 0;
		CTimeSheetRecord ts = userOldTimestamps.get(tsIdx);

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		// timestamp size
		long tsSize = getTimestampDuration(ts);

		// take first temporary record
		int tmpIdx = 0;
		CTmpTimeSheetRecord tmp = tmpRecords.get(tmpIdx);

		// true, ak má niektorý záznam z temp. tabuľky home office
		boolean anyTmpRecordHasHomeOffice = false;

		// predjdem si záznamy z temp. tabuľky
		for (Iterator<CTmpTimeSheetRecord> iterator = tmpRecords.iterator(); iterator.hasNext();) {

			CTmpTimeSheetRecord cTmpTimeSheetRecord = iterator.next();

			// kontrolujem, či nie je zakliknutý home office aj outside
			this.checkOutsideAndHomeOffice(cTmpTimeSheetRecord);

			// ak má záznam nastavený home office tak nastavím na true
			if (cTmpTimeSheetRecord.getHomeOffice().equals(Boolean.TRUE)) {
				anyTmpRecordHasHomeOffice = true;
			}
		}

		/*
		 * našiel som aspoň jeden záznam, ktorý má zakliknutý home office, tak musí mať
		 * povolený home office na všetky dni, pre ktoré idem generovať výkaz práce
		 * (môže mať zakázaný home office, povolený home office alebo povolený so
		 * žiadosťou - v tomto prípade musí mať schválenú žiadosť na home office pre
		 * všetky dni v intervale !!!)
		 */
		if (anyTmpRecordHasHomeOffice) {
			Calendar cDateFrom = (Calendar) tmp.getDateFrom().clone();
			Calendar cDateTo = (Calendar) tmp.getDateTo().clone();

			Date dateFrom = (Date) cDateFrom.getTime();
			Date dateTo = (Date) cDateTo.getTime();

			while (dateFrom.getTime() <= dateTo.getTime()) {
				if (this.timesheetDao.existsUserWorkRecords4Day(loggedUser.getUserId(), cDateFrom)) // SED-821
				{
					// našiel som jeden home office tak kontrolujem, či je home office
					// povolený pre všetky dni, preto Boolean.TRUE
					this.checkHomeOffice(tmp.getOwnerId(), Boolean.TRUE, dateFrom);
				}
				CDateUtils.addDayToDate(dateFrom);
				cDateFrom.add(Calendar.DATE, 1); // SED-821 - plus 1 deň aj k objektu Calendar, pretože Date je vytvorený ako nový
				// objekt resp. nemá referenciu na Calendar
			}
		}

		// remains - set as first tmp entry size
		long tmpRemains = tmp.getDurationInMinutes();

		// process all prepared project/activity entries
		while ((tmpRemains > 0) && (tmp != null) && (ts != null)) {

			// check status
			ts.getStatus().modify();

			if ((tsSize - tmpRemains) == 0l) {
				// user tmp entry has the same size as processed timestamp size

				// only set project, activity, phase, note
				ts.setProject(tmp.getProject());
				ts.setActivity(tmp.getActivity());
				ts.setPhase(tmp.getPhase());
				ts.setNote(tmp.getNote());
				ts.setOutside(tmp.getOutside());
				ts.setHomeOffice(tmp.getHomeOffice() == null ? Boolean.FALSE : tmp.getHomeOffice());
				ts.setChangeTime(Calendar.getInstance());
				ts.setChangedBy(changedBy);
				this.timesheetDao.saveOrUpdate(ts);

				// calculate new remains value
				// take/set next tmp entry
				if (tmpIdx < tmpRecords.size() - 1) {
					tmpIdx = tmpIdx + 1;
					tmp = tmpRecords.get(tmpIdx);
					tmpRemains = new Long(tmp.getDurationInMinutes());
				} else {
					tmp = null;
					tmpRemains = 0l;
				}

				// take next timestamp
				if (tsIdx < userOldTimestamps.size() - 1) {
					tsIdx = tsIdx + 1;
					ts = userOldTimestamps.get(tsIdx);
					tsSize = getTimestampDuration(ts);
				} else {
					ts = null;
					tsSize = 0l;
				}
			} else if (tsSize < tmpRemains) {
				// processed timestamp size is smaller as remains duration

				// only set project, activity, phase, note
				ts.setProject(tmp.getProject());
				ts.setActivity(tmp.getActivity());
				ts.setPhase(tmp.getPhase());
				ts.setNote(tmp.getNote());
				ts.setOutside(tmp.getOutside());
				ts.setHomeOffice(tmp.getHomeOffice() == null ? Boolean.FALSE : tmp.getHomeOffice());
				ts.setChangeTime(Calendar.getInstance());
				ts.setChangedBy(changedBy);
				this.timesheetDao.saveOrUpdate(ts);

				// calculate new remains value
				tmpRemains = tmpRemains - tsSize;

				// take next timestamp
				if (tsIdx < userOldTimestamps.size() - 1) {
					tsIdx = tsIdx + 1;
					ts = userOldTimestamps.get(tsIdx);
					tsSize = getTimestampDuration(ts);
				} else {
					ts = null;
					tsSize = 0l;
				}

			} else if (tsSize > tmpRemains) {
				// processed timestamp size is greather as remains split one and process the
				// parst as usually! its mean: - create new ones ...
				List<CTimeSheetRecord> newTimestamps = createParts(ts, tmpRemains);

				// - store second part for next ...
				if (tsIdx + 1 < userOldTimestamps.size()) {
					userOldTimestamps.add(tsIdx + 1, newTimestamps.get(1));
				} else {
					userOldTimestamps.add(newTimestamps.get(1));
				}

				// ... invalidate old one!
				ts.setValid(Boolean.FALSE);
				ts.setChangeTime(Calendar.getInstance());
				ts.setChangedBy(changedBy);
				this.timesheetDao.saveOrUpdate(ts);

				// - process the first part as original one ...
				ts = newTimestamps.get(0);
				tsSize = getTimestampDuration(ts);
			}
		}
	}

	private long getTimestampDuration(CTimeSheetRecord record) {
		long intervalDuration = 0l;
		if (record.getTimeTo() != null) {
			intervalDuration = record.getTimeTo().getTime().getTime() - record.getTimeFrom().getTime().getTime();
			intervalDuration = intervalDuration + 1; // korekcia
		}
		intervalDuration = intervalDuration / 60000;

		return intervalDuration;
	}

	/**
	 * Returns splitted timestamp (copy of original one), where the first part has
	 * duration equals to value "remains".
	 * 
	 * @param record  input timestamp
	 * @param remains input duration value
	 * @return list of two copies of the original
	 * @throws CSecurityException if any user is not logged
	 */
	private List<CTimeSheetRecord> createParts(CTimeSheetRecord record, Long remains) throws CSecurityException {
		Calendar tmpCalAsTo = (Calendar) record.getTimeFrom().clone();
		tmpCalAsTo.add(Calendar.MINUTE, remains.intValue() - 1);
		CTimeUtils.convertToEndTime(tmpCalAsTo);

		Calendar tmpCalAsFrom = (Calendar) record.getTimeFrom().clone();
		tmpCalAsFrom.add(Calendar.MINUTE, remains.intValue());
		CTimeUtils.convertToStartTime(tmpCalAsFrom);

		List<CTimeSheetRecord> retVal = new ArrayList<>();

		CTimeSheetRecord r1 = tsClone(record);
		r1.setTimeFrom(record.getTimeFrom());
		r1.setTimeTo(tmpCalAsTo);
		retVal.add(r1);

		CTimeSheetRecord r2 = tsClone(record);
		r2.setTimeFrom(tmpCalAsFrom);
		r2.setTimeTo(record.getTimeTo());
		retVal.add(r2);

		return retVal;
	}

	/**
	 * Returns a "copy" of the input object but without the attributes: id,
	 * timeFrom, timeTo
	 * 
	 * @param record input object
	 * @return copy of the input object
	 * @throws CSecurityException if user not logged
	 */
	private CTimeSheetRecord tsClone(CTimeSheetRecord record) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		CTimeSheetRecord tsClone = new CTimeSheetRecord();

		tsClone.setActivity(record.getActivity());
		tsClone.setChangedBy(changedBy);
		tsClone.setChangeTime(Calendar.getInstance());
		tsClone.setClient(record.getClient());
		tsClone.setCreatedBy(changedBy);
		// tsClone.setId(); - later!
		tsClone.setNote(record.getNote());
		tsClone.setHomeOffice(record.getHomeOffice() == null ? Boolean.FALSE : record.getHomeOffice());
		tsClone.setOutside(record.getOutside());
		tsClone.setOwner(record.getOwner());
		tsClone.setPhase(record.getPhase());
		tsClone.setProject(record.getProject());
		// tsClone.setTimeFrom(); - later!
		// tsClone.setTimeTo(); - later!
		tsClone.setValid(Boolean.TRUE);
		tsClone.setStatus(record.getStatus());

		return tsClone;
	}

	private void saveGenerateParameters(Long userId, Calendar dateFrom, Calendar dateTo, Long summaryDurationInMinutes, CTmpTimeSheetRecord dummyRecord) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		CTmpTimeSheetRecord generated = new CTmpTimeSheetRecord();

		// fk_client numeric(10) NOT NULL,
		generated.setClientId(loggedUser.getClientInfo().getClientId());
		// fk_user_owner numeric(10) NOT NULL,
		generated.setOwnerId(userId);

		// fk_user_createdby numeric(10) NOT NULL,
		generated.setChangedById(changedBy.getId());
		// fk_user_changedby numeric(10) NOT NULL,
		generated.setChangeTime(Calendar.getInstance());
		// c_datetime_changed timestamp without time zone NOT NULL,
		generated.setCreatedById(changedBy.getId());

		// c_date_from timestamp without time zone,
		generated.setDateFrom(dateFrom);
		// c_date_to timestamp without time zone,
		generated.setDateTo(dateTo);
		// c_date_generate_action timestamp without time zone,
		generated.setDateGenerateAction(Calendar.getInstance());

		// fk_project numeric(10) NOT NULL,
		generated.setProject(dummyRecord.getProject());
		// fk_activity numeric(10) NOT NULL,
		generated.setActivity(dummyRecord.getActivity());

		// c_summary_minutes_value numeric(10),
		generated.setSummaryDurationInMinutes(summaryDurationInMinutes);

		// c_flag_outside boolean NOT NULL,
		generated.setOutside(Boolean.FALSE);
		// c_flag_valid boolean NOT NULL,
		generated.setValid(Boolean.TRUE);
		// c_flag_generated boolean NOT NULL,
		generated.setGenerated(Boolean.TRUE);

		this.tmpTimesheetDao.saveOrUpdate(generated);
	}

	/**
	 * @see ITimesheetBaseService#findUserLastExternalProgramActivity(Long)
	 */
	@Override
	public CLastExternaProjectActivity findUserLastExternalProgramActivity(Long userId) throws CBusinessException {
		final Integer count = new Integer(5);

		CLastExternaProjectActivity retVal = new CLastExternaProjectActivity();

		Calendar date = Calendar.getInstance();
		List<CTimeSheetRecord> records = this.timesheetDao.findLastWorkingExternal(userId, date, count);
		for (CTimeSheetRecord record : records) {
			if (record.getActivity() != null && record.getProject() != null) {
				if (retVal.getActivities().size() < count.intValue()) {
					if (!retVal.getActivities().contains(record.getActivity().getId())) {
						retVal.getActivities().add(record.getActivity().getId());
					}
					if (!retVal.getProjects().contains(record.getProject().getId())) {
						retVal.getProjects().add(record.getProject().getId());
					}
				} else {
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * Checks reason for selected timestamps (activities: {@link IActivityConstant}
	 * sickness, workbreak}, writes missing reason when available.
	 * 
	 * @param record input timestamp record
	 * @throws CBusinessException when records (in required case) hasn't associated
	 *                            reason
	 */
	private void checkTimestampReason(CTimeSheetRecord record) throws CBusinessException {
		Long clientId;

		if (record.getOwner() != null && record.getOwner().getClient() != null) {
			clientId = record.getOwner().getClient().getId();
		} else {
			final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
			clientId = loggedUser.getClientInfo().getClientId();
		}

		List<Long> activityIds = this.requestReasonDao.getActivityIdsByValidClientRecords(clientId, Boolean.TRUE);
		if (activityIds.contains(record.getActivity().getId())) {
			// reason required!

			// 1.check: exists link to reason?
			// (from process generation timestamps from requests)
			if (record.getReason() != null) {
				// YES:
				// check association: activity ->reason
				Long activityId = record.getActivity().getId();
				Long requestTypeId = getRequestTypeIdForActivityId(activityId, clientId);
				if (requestTypeId != null) {
					List<CRequestReason> availableReasons = this.requestReasonDao.findValidByClientRequestType(clientId, requestTypeId, Boolean.TRUE);
					availableReasons.addAll(this.requestReasonDao.findSystemRecords(requestTypeId));
					for (CRequestReason reason : availableReasons) {
						// find reason
						if (record.getReason().getId().equals(reason.getId())) {
							// correct reason appended to record
							return;
						}
					}
				}
				Logger.getLogger(this.getClass()).error(
						"AUTOMATIC GENERATED TIMESTAMPS: Timestamp record requred activity reason! Record  owner id = " + (record.getOwner().getId() == null ? "" : "" + record.getOwner().getId()));
				// if we are here: missing/wrong reason for the activity
				throw new CBusinessException(CClientExceptionsMessages.TIMESTAMP_REQUIRE_ACTIVITY_REASON);

			}
		}
	}

	/**
	 * Returns identifier of the request type for selected activity idnetifier or
	 * null when not available
	 * 
	 * @param activityId input activity identifier
	 * @return Long value
	 * @throws CSecurityException in error case
	 */
	private Long getRequestTypeIdForActivityId(Long activityId, Long clientId) throws CSecurityException {
		List<CRequestReason> reasonEntities = this.requestReasonDao.findValidByClient(clientId, Boolean.TRUE);
		for (CRequestReason reasonEntity : reasonEntities) {
			if (activityId.equals(reasonEntity.getRequestType().getActivityId())) {
				return reasonEntity.getRequestType().getId();
			}
		}

		return null;
	}

	@Override
	public CGetSumAndAverageTimeResponseContent getSumAndAverageTimeInTimeInterval(Calendar dateFrom, Calendar dateTo) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		CGetSumAndAverageTimeResponseContent retVal = new CGetSumAndAverageTimeResponseContent();

		String sumAndAvgTime = timesheetDao.getSumAndAverageTimeInTimeInterval(loggedUser.getUserId(), dateFrom, dateTo);

		String sum = sumAndAvgTime.split("#")[0]; // vo formate "08:00"
		String avgTime = sumAndAvgTime.split("#")[1];

		retVal.setSumOfWorkHours(Long.valueOf(sum.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(sum.split(":")[1]) * 60 * 1000);
		retVal.setAverageDuration(Long.valueOf(avgTime.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(avgTime.split(":")[1]) * 60 * 1000);

		return retVal;
	}

	@Override
	public CGetSumAndAverageTimeForUsersResponseContent getSumAndAverageTimeInTimeIntervalForUsers(CSubrodinateTimeStampBrwFilterCriteria filter) throws CSecurityException {

		CGetSumAndAverageTimeForUsersResponseContent response = new CGetSumAndAverageTimeForUsersResponseContent();

		List<CGetSumAndAverageTimeForUser> sumAndAverageList = timestampDao.getSumAndAvgTimeInTimeInterval(filter);
		Long totalDays = 0l;
		Long totalSums = 0l;

		for (CGetSumAndAverageTimeForUser user : sumAndAverageList) {
			totalSums += user.getSumOfWorkHours();
			totalDays += user.getCountOfDays();
		}

		if (filter.getEmplyees().size() > 1) {
			totalDays = timestampDao.geCountOfWorkedDaysInTimeInterval(filter);
		}

		response.setTotalSums(totalSums);
		response.setTotalAvg(totalDays == 0 ? 0 : totalSums / totalDays);
		response.setList(sumAndAverageList);

		return response;
	}

	@Override
	public CGetInfoForMobileTimerResponseContent getInfoForMobileTimer(final Boolean countToday) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		CGetInfoForMobileTimerResponseContent retVal = new CGetInfoForMobileTimerResponseContent();

		Calendar actualTimeCalendar = Calendar.getInstance();
		Calendar dateTo = (Calendar) actualTimeCalendar.clone();

		if (!countToday) {
			dateTo.add(Calendar.DATE, -1);
		}

		String sumAndAvgTimePerMonth = timesheetDao.getSumAndAverageTimeInTimeInterval(loggedUser.getUserId(), CDateRangeUtils.getFirstDayOfThisMonth(), dateTo);
		String sumAndAvgTimePerWeek = timesheetDao.getSumAndAverageTimeInTimeInterval(loggedUser.getUserId(), CDateRangeUtils.getFirstDayOfThisWeek(), dateTo);
		String sumAndAvgTimePerDay = timesheetDao.getSumAndAverageTimeInTimeInterval(loggedUser.getUserId(), actualTimeCalendar, actualTimeCalendar);

		String sumPerMonth = sumAndAvgTimePerMonth.split("#")[0]; // vo formate "08:00"
		String avgTimePerMonth = sumAndAvgTimePerMonth.split("#")[1];

		String sumPerWeek = sumAndAvgTimePerWeek.split("#")[0];
		String avgTimePerWeek = sumAndAvgTimePerWeek.split("#")[1];

		String sumPerDay = sumAndAvgTimePerDay.split("#")[0];

		retVal.setSumOfWorkHoursPerMonth(Long.valueOf(sumPerMonth.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(sumPerMonth.split(":")[1]) * 60 * 1000);
		retVal.setSumOfWorkHoursPerWeek(Long.valueOf(sumPerWeek.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(sumPerWeek.split(":")[1]) * 60 * 1000);
		retVal.setSumOfWorkHoursPerDay(Long.valueOf(sumPerDay.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(sumPerDay.split(":")[1]) * 60 * 1000);

		retVal.setAverageDurationPerMonth(Long.valueOf(avgTimePerMonth.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(avgTimePerMonth.split(":")[1]) * 60 * 1000);
		retVal.setAverageDurationPerWeek(Long.valueOf(avgTimePerWeek.split(":")[0]) * 60 * 60 * 1000 + Long.valueOf(avgTimePerWeek.split(":")[1]) * 60 * 1000);

		Integer workingDaysInActualWeek = 5;
		Integer workingDaysInActualMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
				- CDateUtils.countWeekendDays(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));

		List<CHoliday> holidays = this.holidayDao.findAllValidClientsHolidays(loggedUser.getClientInfo().getClientId(), Calendar.getInstance().get(Calendar.YEAR));

		for (CHoliday holiday : holidays) {
			Calendar hDay = (Calendar) holiday.getDay().clone();

			// naratam si kolko sviatkov je v danom mesiaci mimo vikendov
			if (hDay.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
				if (hDay.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && hDay.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
					workingDaysInActualMonth--;
				}
			}

			// naratam si kolko sviatkov je v danom tyzdni mimo vikendov
			if (hDay.get(Calendar.WEEK_OF_YEAR) == Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)) {
				if (hDay.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && hDay.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
					workingDaysInActualWeek--;
				}
			}
		}

		retVal.setWorkingDaysInActualMonth(workingDaysInActualMonth);
		retVal.setWorkingDaysInActualWeek(workingDaysInActualWeek);

		return retVal;
	}

	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public List<Calendar> confirmTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException {
		Logger.getLogger(this.getClass()).info("Confirm timesheet records start. userId: " + userId);
		CUser user = userDao.findById(userId);

		final Calendar dateFromCalendar = Calendar.getInstance();
		dateFromCalendar.setTime(dateFrom);

		final Calendar dateToCalendar = Calendar.getInstance();
		dateToCalendar.setTime(dateTo);

		if (screenType.equals(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN) && users.contains(userId)) {
			// Ak idem potvrdzovat podriadeným skontrolujem pre istotu ci
			// neschvalujem aj sam sebe, lebo to nemozem
			users.remove(userId);
		}

		if (users.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_NOTHING_TO_CONFIRM);
		}

		List<CTimeSheetRecord> recordsForUpdate = timesheetDao.findAll(users, dateFromCalendar, dateToCalendar);
		List<CTimeSheetRecord> records = new ArrayList<>();
		List<Calendar> dniSNeukoncenouZnackou = new ArrayList<>();

		// prejdem vsetky zaznamy a ak je tam aj neukonceny tak ho vyradim a aj vsetky z toho dna
		for (CTimeSheetRecord record : recordsForUpdate) {
			if (record.getTimeTo() == null) {
				dniSNeukoncenouZnackou.add(record.getTimeFrom());
			}
			records.add(record);
		}

		for (CTimeSheetRecord record : recordsForUpdate) {

			for (Calendar cal : dniSNeukoncenouZnackou) {
				if (cal.get(Calendar.DAY_OF_YEAR) == record.getTimeFrom().get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.MONTH) == record.getTimeFrom().get(Calendar.MONTH)
						&& cal.get(Calendar.YEAR) == record.getTimeFrom().get(Calendar.YEAR)) {
					records.remove(record);
				}
			}
		}

		if (records.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_NOTHING_TO_CONFIRM);
		}

		CTimeSheetRecordStatus confirmedBySuperior = stateDao.findById(ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR);

		if (screenType.equals(ITimestampScreenType.MY_TIMESHEET_SCREEN)) {
			// na obrazovke Prehľad mojich časových update stavu na 2 pre všetky vyfiltrované záznamy, ktoré sú ukončené a sú v stave 1
			CTimeSheetRecordStatus confirmedByEmployee = stateDao.findById(ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE);
			Boolean userHasRightToSelfConfirm = null;

			for (CTimeSheetRecord record : records) {
				if (userHasRightToSelfConfirm == null) {
					userHasRightToSelfConfirm = userHasRightToSelfConfirm(record.getOwner().getId());
				}

				if (ITimeSheetRecordStates.ID_NEW.equals(record.getStatus().getId())) {

					record.setStatus(confirmedByEmployee);

					if (userHasRightToSelfConfirm) {
						// ak nemam nadriadeneho, idem z nova rovno na potvrdena nadriadenym
						record.setStatus(confirmedBySuperior);
					}

					record.setChangedBy(user);
					record.setChangeTime(Calendar.getInstance());
					timesheetDao.saveOrUpdate(record);
				}
			}
		} else if (screenType.equals(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN)) {
			// na obrazovke Zamestnanci/Prehľad časových značiek podriadených ak nie som prihlásený ako admin

			if (alsoEmployees) {
				// ak je zaškrtnuté "Aj zamestnanci" update stavu na 3 pre všetky vyfiltrované
				// záznamy, ktoré sú ukončené a sú v stave 2 alebo 1
				for (CTimeSheetRecord record : records) {
					if (ITimeSheetRecordStates.ID_NEW.equals(record.getStatus().getId()) || ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE.equals(record.getStatus().getId())) {
						record.setStatus(confirmedBySuperior);
						record.setChangedBy(user);
						record.setChangeTime(Calendar.getInstance());
						timesheetDao.saveOrUpdate(record);
					}
				}
			} else {
				// ak nie je zaškrtnuté "Aj zamestnanci" update stavu na 3 pre všetky
				// vyfiltrované záznamy, ktoré sú ukončené a sú v stave 2
				for (CTimeSheetRecord record : records) {
					if (ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE.equals(record.getStatus().getId())) {
						record.setStatus(confirmedBySuperior);
						record.setChangedBy(user);
						record.setChangeTime(Calendar.getInstance());
						timesheetDao.saveOrUpdate(record);
					}
				}
			}
		} else if (screenType.equals(ITimestampScreenType.SUBORDINATE_TIMESHEET_ADMIN_SCREEN)) {

			// na obrazovke Zamestnanci/Prehľad časových značiek podriadených ak som prihlásený ako admin
			CTimeSheetRecordStatus confirmedByAdmin = stateDao.findById(ITimeSheetRecordStates.ID_CONFIRMED_BY_ADMIN);

			if (alsoEmployees) {
				if (!alsoSuperiors) {
					// nebude sa dať dosiahnuť stav kedy je zškrtnuté "Aj zamestnanci" a nie je zaškrtnuté "Aj nadriadení"
					throw new CBusinessException("nebude sa dať dosiahnuť stav kedy je zškrtnuté #Aj zamestnanci# a nie je zaškrtnuté #Aj nadriadení#");
				}

				// ak je zaškrtnuté "Aj zamestnanci" update stavu na 4 pre všetky vyfiltrované
				// záznamy, ktoré sú ukončené a sú v stave 3, 2 alebo 1
				for (CTimeSheetRecord record : records) {
					record.setStatus(confirmedByAdmin);
					record.setChangedBy(user);
					record.setChangeTime(Calendar.getInstance());
					timesheetDao.saveOrUpdate(record);
				}
			} else {
				if (alsoSuperiors) {
					// ak je zaškrtnuté "Aj nadriadení" a nie je zaškrtnuté "Aj zamestnanci" update
					// stavu na 4 pre všetky vyfiltrované záznamy, ktoré sú ukončené a sú v stave 3 alebo 2
					for (CTimeSheetRecord record : records) {
						if (ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE.equals(record.getStatus().getId()) || ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(record.getStatus().getId())) {
							record.setStatus(confirmedByAdmin);
							record.setChangedBy(user);
							record.setChangeTime(Calendar.getInstance());
							timesheetDao.saveOrUpdate(record);
						}
					}
				} else {
					// ak nie je zaškrtnuté "Aj nadriadení" update stavu na 4 pre všetky
					// vyfiltrované záznamy, ktoré sú ukončené a sú v stave 3
					for (CTimeSheetRecord record : records) {
						if (ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(record.getStatus().getId())) {
							record.setStatus(confirmedByAdmin);
							record.setChangedBy(user);
							record.setChangeTime(Calendar.getInstance());
							timesheetDao.saveOrUpdate(record);
						}
					}
				}
			}
		}

		Logger.getLogger(this.getClass()).info("Confirm timesheet records finish. userId: " + userId);

		return dniSNeukoncenouZnackou;
	}

	@Transactional(readOnly = false, rollbackForClassName = "CBusinessException")
	@Override
	public void cancelTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBusinessException {
		Logger.getLogger(this.getClass()).info("Cancel timesheet records start. userId: " + userId);
		CUser user = userDao.findById(userId);

		final Calendar dateFromCalendar = Calendar.getInstance();
		dateFromCalendar.setTime(dateFrom);

		final Calendar dateToCalendar = Calendar.getInstance();
		dateToCalendar.setTime(dateTo);

		if (screenType.equals(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN) && users.contains(userId)) {
			// Ak idem zrusit podriadeným skontrolujem pre istotu ci nerusim aj sam sebe,
			// lebo to nemozem users.remove(userId)
		}

		if (users.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_NOTHING_TO_CONFIRM);
		}

		List<CTimeSheetRecord> records = timesheetDao.findAll(users, dateFromCalendar, dateToCalendar);

		if (records.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_NOTHING_TO_CANCEL);
		}

		CTimeSheetRecordStatus newStatus = stateDao.findById(ITimeSheetRecordStates.ID_NEW);
		CTimeSheetRecordStatus confirmedByEmployee = stateDao.findById(ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE);

		if (screenType.equals(ITimestampScreenType.MY_TIMESHEET_SCREEN)) {
			// na obrazovke Prehľad mojich časových update stavu na 1 pre všetky vyfiltrované záznamy, ktoré sú ukončené a sú v stave 2

			Boolean userHasRightToSelfConfirm = null;

			for (CTimeSheetRecord record : records) {
				if (userHasRightToSelfConfirm == null) {
					userHasRightToSelfConfirm = userHasRightToSelfConfirm(record.getOwner().getId());
				}

				if (ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE.equals(record.getStatus().getId())) {
					record.setStatus(newStatus);
					record.setChangedBy(user);
					record.setChangeTime(Calendar.getInstance());
					timesheetDao.saveOrUpdate(record);
				} else if (ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(record.getStatus().getId()) && userHasRightToSelfConfirm) {
					// ak nemam nadriadeneho, idem z potvrdena nadriadenym rovno na nova
					record.setStatus(newStatus);
					record.setChangedBy(user);
					record.setChangeTime(Calendar.getInstance());
					timesheetDao.saveOrUpdate(record);
				}
			}
		} else if (screenType.equals(ITimestampScreenType.SUBORDINATE_TIMESHEET_SCREEN)) {
			// na obrazovke Zamestnanci/Prehľad časových značiek podriadených ak nie som prihlásený ako admin

			if (alsoEmployees) {
				// ak je zaškrtnuté "Aj zamestnanci" update stavu na 1 pre všetky vyfiltrované
				// záznamy, ktoré sú ukončené a sú v stave 3 alebo 2
				for (CTimeSheetRecord record : records) {
					if ((ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(record.getStatus().getId()) && (!userId.equals(record.getOwner().getId())))
							|| ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE.equals(record.getStatus().getId())) {

						record.setStatus(newStatus);
						record.setChangedBy(user);
						record.setChangeTime(Calendar.getInstance());
						timesheetDao.saveOrUpdate(record);

					}
				}
			} else {
				// ak nie je zaškrtnuté "Aj zamestnanci" update stavu na 2 pre všetky
				// vyfiltrované záznamy, ktoré sú ukončené a sú v stave 3
				for (CTimeSheetRecord record : records) {
					if (ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(record.getStatus().getId())) {
						record.setStatus(confirmedByEmployee);
						record.setChangedBy(user);
						record.setChangeTime(Calendar.getInstance());
						timesheetDao.saveOrUpdate(record);
					}
				}
			}
		} else if (screenType.equals(ITimestampScreenType.SUBORDINATE_TIMESHEET_ADMIN_SCREEN)) {
			// na obrazovke Zamestnanci/Prehľad časových značiek podriadených ak som prihlásený ako admin
			if (alsoEmployees) {
				if (!alsoSuperiors) {
					throw new CBusinessException("");
				}

				// ak je zaškrtnuté "Aj zamestnanci" update stavu na 1 pre všetky vyfiltrované
				// záznamy, ktoré sú ukončené a sú v stave 4, 3 alebo 2
				for (CTimeSheetRecord record : records) {
					record.setStatus(newStatus);
					record.setChangedBy(user);
					record.setChangeTime(Calendar.getInstance());
					timesheetDao.saveOrUpdate(record);
				}
			} else {
				CTimeSheetRecordStatus confirmedBySuperior = stateDao.findById(ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR);

				if (alsoSuperiors) {
					// ak je zaškrtnuté "Aj nadriadení" a nie je zaškrtnuté "Aj zamestnanci" update
					// stavu na 2 pre všetky vyfiltrované záznamy, ktoré sú ukončené a sú v stave 4 alebo 3
					for (CTimeSheetRecord record : records) {
						if (ITimeSheetRecordStates.ID_CONFIRMED_BY_ADMIN.equals(record.getStatus().getId()) || ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(record.getStatus().getId())) {
							record.setStatus(confirmedByEmployee);

							if (userHasRightToSelfConfirm(record.getOwner().getId())) {
								// ak nemam nadriadeneho, idem rovno na potvrdena nadriadenym
								record.setStatus(confirmedBySuperior);
							}

							record.setChangedBy(user);
							record.setChangeTime(Calendar.getInstance());
							timesheetDao.saveOrUpdate(record);
						}
					}
				} else {
					// ak nie je zaškrtnuté "Aj nadriadení" update stavu na 3 pre všetky
					// vyfiltrované záznamy, ktoré sú ukončené a sú v stave 4
					for (CTimeSheetRecord record : records) {
						if (ITimeSheetRecordStates.ID_CONFIRMED_BY_ADMIN.equals(record.getStatus().getId())) {
							record.setStatus(confirmedBySuperior);
							record.setChangedBy(user);
							record.setChangeTime(Calendar.getInstance());
							timesheetDao.saveOrUpdate(record);
						}
					}
				}
			}
		}

		Logger.getLogger(this.getClass()).info("Cancel timesheet records finish. userId: " + userId);
	}

	private Boolean userHasRightToSelfConfirm(Long ownerId) throws CBusinessException {
		Logger.getLogger(this.getClass()).info("userHasRightToSelfConfirm start. ownerId: " + ownerId);

		COrganizationTree parentOrgTree = this.organizationTreeDao.findParentTree(ownerId);
		if (parentOrgTree != null) {
			if (parentOrgTree.getSuperior() != null) {
				CUser parentUser = parentOrgTree.getSuperior().getOwner();
				if (parentUser != null) {
					Logger.getLogger(this.getClass()).info("userHasRightToSelfConfirm finish. result: false");
					return Boolean.FALSE;
				}
			}
		}

		Logger.getLogger(this.getClass()).info("userHasRightToSelfConfirm finish. result: true");
		return Boolean.TRUE;
	}

	/**
	 * kontrola pri vytváraní a modifikovani časovej značky, či na zadaný deň u
	 * zamestnanca existuje platná časová značka v stave potvrdená (zamestnancom,
	 * nadriadeným, administrátorom) a ak áno, nedovoliť časovú značku vložiť
	 * 
	 * @throws CBusinessException
	 */
	private void checkExistsConfirmedTimestampForCurrentDay(final CTimeStampRecord record) throws CBusinessException {
		List<Long> users = new ArrayList<>();
		users.add(record.getEmployeeId());

		final Calendar dateFromCalendar = Calendar.getInstance();
		dateFromCalendar.setTime(record.getDateFrom());

		final Calendar dateToCalendar = Calendar.getInstance();
		dateToCalendar.setTime(record.getDateFrom());

		List<CTimeSheetRecord> records = this.timesheetDao.findAll(users, dateFromCalendar, dateToCalendar);

		CTimeSheetRecordStatus newStatus = stateDao.findById(ITimeSheetRecordStates.ID_NEW);

		for (CTimeSheetRecord r : records) {
			if (r.getStatus() != newStatus) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_RECORD_CANNOT_ADD);
			}
		}

	}

	/**
	 * kontrola pri generovani časovej značky, či na zadaný deň u zamestnanca
	 * existuje platná časová značka v stave potvrdená (zamestnancom, nadriadeným,
	 * administrátorom) a ak áno, nedovoliť časovú značku vložiť
	 * 
	 * @throws CBusinessException
	 */
	private void checkExistsConfirmedTimestampForCurrentDay(final CTimeSheetRecord record) throws CBusinessException {
		List<Long> users = new ArrayList<>();
		users.add(record.getOwner().getId());

		final Calendar dateFromCalendar = (Calendar) record.getTimeFrom().clone();

		final Calendar dateToCalendar = record.getTimeTo() == null ? Calendar.getInstance() : (Calendar) record.getTimeTo().clone();

		List<CTimeSheetRecord> records = this.timesheetDao.findAll(users, dateFromCalendar, dateToCalendar);

		CTimeSheetRecordStatus newStatus = stateDao.findById(ITimeSheetRecordStates.ID_NEW);

		for (CTimeSheetRecord r : records) {
			if (r.getStatus() != newStatus) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_RECORD_CANNOT_ADD);
			}
		}

	}

	@Override
	public List<CProjectDuration> getDataForGraphOfProjects(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
		calendarFrom.set(Calendar.MINUTE, 0);
		calendarFrom.set(Calendar.SECOND, 0);
		calendarFrom.set(Calendar.MILLISECOND, 0);

		CTimeUtils.convertToEOD(calendarTo);

		return timesheetDao.getDataForGraphOfProjects(loggedUser.getUserId(), calendarFrom, calendarTo);
	}

	@Override
	public List<CAttendanceDuration> getDataForGraphOfAttendance(Calendar calendarFrom, Calendar calendarTo) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
		calendarFrom.set(Calendar.MINUTE, 0);
		calendarFrom.set(Calendar.SECOND, 0);
		calendarFrom.set(Calendar.MILLISECOND, 0);

		CTimeUtils.convertToEOD(calendarTo);

		List<CAttendanceDuration> data = timesheetDao.getDataForGraphOfAttendance(loggedUser.getUserId(), calendarFrom, calendarTo);

		List<CHoliday> holidays = this.holidayDao.findAllValidClientsHolidays(loggedUser.getClientInfo().getClientId(), Calendar.getInstance().get(Calendar.YEAR));

		// copy of parameters used for iteration
		Calendar startOfPeriod = (Calendar) calendarFrom.clone();
		final Calendar endOfPeriod = (Calendar) calendarTo.clone();

		while (!startOfPeriod.after(endOfPeriod)) {

			boolean alreadyInList = false;

			final Calendar checkDate = (Calendar) startOfPeriod.clone();
			checkDate.set(Calendar.HOUR_OF_DAY, 0);
			checkDate.set(Calendar.MINUTE, 0);
			checkDate.set(Calendar.SECOND, 0);
			checkDate.set(Calendar.MILLISECOND, 0);

			for (CAttendanceDuration attendanceDuration : data) {
				if (attendanceDuration.getDay().get(Calendar.DAY_OF_MONTH) == checkDate.get(Calendar.DAY_OF_MONTH) && // if attendanceDuration and checkDate is same date
						attendanceDuration.getDay().get(Calendar.MONTH) == checkDate.get(Calendar.MONTH) && attendanceDuration.getDay().get(Calendar.YEAR) == checkDate.get(Calendar.YEAR)) {
					alreadyInList = true;
					break;
				}
			}

			if (!alreadyInList) {
				boolean isHoliady = false;
				boolean isWeekend = (checkDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) || (checkDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);

				for (CHoliday holiday : holidays) {
					Calendar hDay = (Calendar) holiday.getDay().clone();

					if (hDay.get(Calendar.DAY_OF_MONTH) == checkDate.get(Calendar.DAY_OF_MONTH) && hDay.get(Calendar.MONTH) == checkDate.get(Calendar.MONTH)) {
						isHoliady = true;
						break;
					}
				}

				if (!isHoliady && !isWeekend) {

					CAttendanceDuration attendanceDuration = new CAttendanceDuration();
					attendanceDuration.setDay(checkDate);
					attendanceDuration.setDuration(0L);

					data.add(attendanceDuration);
				}
			}
			// next day
			startOfPeriod.add(Calendar.DATE, 1);
		}

		// Sorting
		data.sort((CAttendanceDuration first, CAttendanceDuration second) -> first.getDay().compareTo(second.getDay()));

		return data;
	}

	private CTimeSheetRecord getNewRecordForGeneration(CRequest request, CUser requestedUser) throws CBusinessException {
		// pk_id = sekvencer
		CTimeSheetRecord addRecord = new CTimeSheetRecord();
		// fk_client = prebrate zo ziadosti -
		// t_request.fk_client
		addRecord.setClient(request.getClient());
		// fk_user_owner = prebrate do ziadosti - t_request
		addRecord.setOwner(request.getOwner());
		// fk_user_createdby = id recepcie danej organizacie
		addRecord.setCreatedBy(requestedUser);
		// fk_user_changedby = id recepcie danej organizacie
		addRecord.setChangedBy(requestedUser);
		// c_datetime_changed = sysdate
		addRecord.setChangeTime(Calendar.getInstance());
		// fk_activity = -3 ak D
		// fk_activity = -4 ak NV
		// fk_activity = -5 ak PN
		// fk_activity = -6 ak PVP
		addRecord.setActivity(getActivityByRequestType(request));

		// fk_project = null
		addRecord.setProject(null);
		// c_flag_valid = 1
		addRecord.setValid(Boolean.TRUE);
		// c_note = vlozi sa c_descrition pre typ ziadosti
		String note = request.getType().getDescription();
		if (request.getType().getId().equals(new Long(IRequestTypeConstant.RQTYPE_SICKNESS_ID)) || request.getType().getId().equals(new Long(IRequestTypeConstant.RQTYPE_WORKBREAK_ID))) {
			// ak je to PvP alebo PN s vyplnenym dovodom,
			// pridame aj dovod
			if (request.getReason() != null) {
				String reason = request.getReason() != null ? request.getReason().getReasonName() : "";
				note = note + " " + reason;

				addRecord.setReason(request.getReason());
			}
		}
		addRecord.setNote(note);
		// c_phase = null
		addRecord.setPhase(null);
		// c_flag_outside = false
		addRecord.setOutside(Boolean.FALSE);

		// SED-855 - c_flag_home_office = false
		addRecord.setHomeOffice(Boolean.FALSE);

		addRecord.setStatus(this.stateDao.findById(ITimeSheetRecordStates.ID_NEW));

		return addRecord;
	}

	private boolean generateTimestamps(Long clientId, Calendar checkDate) {
		boolean generateTimestamps = true;
		Calendar tmpCheckDate = (Calendar) checkDate.clone();

		Integer selectedYear = checkDate.get(Calendar.YEAR);
		List<CHoliday> holidays = this.holidayDao.findAllValidClientsHolidays(clientId, selectedYear);

		tmpCheckDate.set(Calendar.HOUR_OF_DAY, 0);
		tmpCheckDate.set(Calendar.MINUTE, 0);
		tmpCheckDate.set(Calendar.SECOND, 0);
		tmpCheckDate.set(Calendar.MILLISECOND, 0);

		for (CHoliday holiday : holidays) {
			// don't generate timestamps for holiday
			Calendar hDay = (Calendar) holiday.getDay().clone();
			hDay.set(Calendar.HOUR_OF_DAY, 0);
			hDay.set(Calendar.MINUTE, 0);
			hDay.set(Calendar.SECOND, 0);
			hDay.set(Calendar.MILLISECOND, 0);
			if (hDay.equals(tmpCheckDate)) {
				generateTimestamps = false;
				break;
			}
		}
		if (generateTimestamps && !CDateUtils.isWorkingDay(tmpCheckDate.getTime())) {
			// don't generate timestamps for non work days
			generateTimestamps = false;
		}

		return generateTimestamps;
	}

	/**
	 * Pri generovaní časovej značky typu dovolenka je preto potrebné robiť podobnú
	 * kontrolu ako pri prihlásení a v prípade, že posledná časová značka nie je
	 * ukončená, tak ju ukončiť s časom 23:59.
	 * @return 
	 */
	private boolean checkClosedLastTimestamp(final Long userId, final Date timeToCheck) {
		
		boolean isOldRecordClosed = true;
		
		final Calendar time = Calendar.getInstance();
		
		if (timeToCheck != null) {
			time.setTime(timeToCheck);
		}

		final Calendar dateToSearch = CTimeUtils.convertToEndDate(time);
		final CTimeSheetRecord last = this.timesheetDao.findLast(userId, dateToSearch);

		if (last != null) {
			isOldRecordClosed = this.checkOldRecordClosed(last, dateToSearch);
			
			if (!isOldRecordClosed) {
				Calendar timeTo = CTimeUtils.convertToEOD((Calendar) last.getTimeFrom().clone());
				last.setTimeTo(timeTo);
				timesheetDao.saveOrUpdate(last);
			}
		}
		
		return isOldRecordClosed;
	}

	@Override
	public List<CStatsRecord> getDataForGraphOfStats(CStatsFilter filter) throws CSecurityException {
		filter.getDateFrom().set(Calendar.HOUR_OF_DAY, 0);
		filter.getDateFrom().set(Calendar.MINUTE, 0);
		filter.getDateFrom().set(Calendar.SECOND, 0);
		filter.getDateFrom().set(Calendar.MILLISECOND, 0);

		CTimeUtils.convertToEOD(filter.getDateTo());

		return timesheetDao.getDataForGraphOfStats(filter);
	}

	/**
	 * skontrolovať či pre danú aktivitu vyhovuje hraničný čas stanovený v tabuľke
	 * číselníku aktivít (t_ct_activity). Záznam výkazu nemôže začínať skôr ako je v
	 * číselníku stanovené v "Min. čas" a končiť neskôr ako je v číselníku stanovené
	 * v "Max. čas". Ak sa v tabuľke t_ct_activity žiadne hraničné časy
	 * nenachádzajú, kontrolu nerobiť. skontrolovať či pre danú aktivitu od začiatku
	 * roka nebol presiahnutý maximálny počet povolených hodín za rok podľa pola
	 * "Max. počet hodín". Čiže spočítať počet hodín z platných záznamov výkazu
	 * práce vykázaných na túto aktivitu od začiatku aktuálneho roka a nedovoliť
	 * pridať záznam ak je presiahnutá hodnota z "Max. počet hodín". Ak pole v
	 * tabuľke t_ct_activity nie je vyplnené, kontrolu nerobiť.
	 * 
	 * @param record
	 * @throws CBusinessException
	 */
	private void checkActivityTimes(CTimeSheetRecord record) throws CBusinessException {
		CActivity activity = record.getActivity();
		if (activity.getTimeMin() != null) {
			int time = activity.getTimeMin().get(Calendar.HOUR_OF_DAY) * 60 + activity.getTimeMin().get(Calendar.MINUTE);
			int timeFrom = record.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + record.getTimeFrom().get(Calendar.MINUTE);
			if (timeFrom < time) {
				// specialny charakter # je ak chcem zo servra poslat argumenty pre chybovu hlasku
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_ACTIVITY_TIME_MIN + CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + activity.getName()
						+ CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + CDateUtils.formatTime(activity.getTimeMin().getTime()));
			}
		}
		if (activity.getTimeMax() != null) {
			int time = activity.getTimeMax().get(Calendar.HOUR_OF_DAY) * 60 + activity.getTimeMax().get(Calendar.MINUTE);

			if (record.getTimeTo() != null) {
				int timeTo = record.getTimeTo().get(Calendar.HOUR_OF_DAY) * 60 + record.getTimeTo().get(Calendar.MINUTE);
				if (timeTo > time) {
					// specialny charakter # je ak chcem zo servra poslat argumenty pre chybovu hlasku
					throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_ACTIVITY_TIME_MAX + CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + activity.getName()
							+ CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + CDateUtils.formatTime(activity.getTimeMax().getTime()));
				}
			}

			int timeFrom = record.getTimeFrom().get(Calendar.HOUR_OF_DAY) * 60 + record.getTimeFrom().get(Calendar.MINUTE);
			if (timeFrom > time) {
				// specialny charakter # je ak chcem zo servra poslat argumenty pre chybovu hlasku
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_ACTIVITY_TIME_MAX + CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + activity.getName()
						+ CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + CDateUtils.formatTime(activity.getTimeMax().getTime()));
			}
		}
		if (activity.getHoursMax() != null && record.getTimeTo() != null) {
			Long duration = timestampDao.getDurationOfActivityInMinutes(record.getOwner().getId(), activity.getId(), record.getTimeFrom().get(Calendar.YEAR), record.getId());
			long actualRecordDuration = (record.getTimeTo().getTimeInMillis() - record.getTimeFrom().getTimeInMillis()) / 60000;

			duration += actualRecordDuration + 1;

			if (duration > activity.getHoursMax() * 60) {
				// specialny charakter # je ak chcem zo servra poslat argumenty pre chybovu hlasku
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_ACTIVITY_HOURS_MAX + CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + activity.getName()
						+ CClientExceptionsMessages.MESSAGE_ARGUMENTS_SEPARATOR + activity.getHoursMax());
			}
		}
	}
	
	private void addContinuationOfLastRecord(final Long getEmployeeId, final Date time, boolean checkLastRecordIsClosed, CUser userFromRequest) throws CBusinessException {

		final Calendar timeOfEnd = Calendar.getInstance();
		timeOfEnd.setTime(time);
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(getEmployeeId, timeOfEnd);

		if (lastActivity != null) {
			
			if (checkLastRecordIsClosed) {
				if (!(lastActivity.getActivity() != null && (lastActivity.getActivity().getId().equals(IActivityConstant.NOT_WORK_ALERTNESSWORK) || 
						lastActivity.getActivity().getId().equals(IActivityConstant.NOT_WORK_INTERACTIVEWORK)))) {
					return;				
				}
			}
			
			boolean isWorkAlertnessOrInteractive = lastActivity.getActivity().getId().longValue() == IActivityConstant.NOT_WORK_ALERTNESSWORK.longValue()
					|| lastActivity.getActivity().getId().longValue() == IActivityConstant.NOT_WORK_INTERACTIVEWORK.longValue();

			// add continuation timestamp only in this case!
			if (isWorkAlertnessOrInteractive) {
				// lastActivity should be closed already!
				Calendar lastActivityTimeFrom = lastActivity.getTimeFrom();

				Calendar newActivityTimeFrom = Calendar.getInstance();
				newActivityTimeFrom.setTime(time);

				// lastActivity MUST be from direct previous day
				int laYear = lastActivityTimeFrom.get(Calendar.YEAR);
				int laMonth = lastActivityTimeFrom.get(Calendar.MONTH);
				int laDay = lastActivityTimeFrom.get(Calendar.DAY_OF_MONTH);

				newActivityTimeFrom.add(Calendar.DAY_OF_YEAR, -1);
				int naYear = newActivityTimeFrom.get(Calendar.YEAR);
				int naMonth = newActivityTimeFrom.get(Calendar.MONTH);
				int naDay = newActivityTimeFrom.get(Calendar.DAY_OF_MONTH);

				Calendar today = Calendar.getInstance();
				int today_Year = today.get(Calendar.YEAR);
				int today_Month = today.get(Calendar.MONTH);
				int today_Day = today.get(Calendar.DAY_OF_MONTH);

				// is available to add continuation timestamp: required two following days (date of last activity, today)!
				boolean doit = (laYear == naYear) && (laMonth == naMonth) && (laDay == naDay);

				if (!doit) {
					// V prípade, že sa používateľ prihlasuje na pohotovosť/zásah v piatok večer,
					// do práce prichádza v pondelok a časové ohraničenie pohotovosti mu to umožňuje,
					// vygenerovať záznamy vo výkaze práce aj na celý víkend (tj
					// keď má časové ohraničenie cez nepracovné dni od polnoci do polnoci).
					// To isté aj v prípade, že je medzi prihlásení na 
					// pohotovosť/zásah a prihlásením do práce je sviatok.

					List<CHoliday> holidays = this.holidayDao.findAllValidClientsHolidays(lastActivity.getClient().getId(), Calendar.getInstance().get(Calendar.YEAR));

					while (!CDateServerUtils.isWorkingDayWithCheckHolidays(newActivityTimeFrom.getTime(), holidays) && laDay != newActivityTimeFrom.get(Calendar.DAY_OF_MONTH)
					// musime sa pozriet ci nie je posledna aktivita v tej isty den ako aktualny datum
							&& !(laYear == today_Year && laMonth == today_Month && laDay == today_Day)) {
						final Calendar startTime = Calendar.getInstance();
						startTime.setTime(newActivityTimeFrom.getTime());
						CTimeUtils.convertToStartDate(startTime);

						final Calendar stopTime = Calendar.getInstance();
						stopTime.setTime(newActivityTimeFrom.getTime());
						CTimeUtils.convertToEndDate(stopTime);

						addContinuationOfLastRecord(lastActivity, startTime, stopTime, userFromRequest);

						doit = true;
						newActivityTimeFrom.add(Calendar.DAY_OF_YEAR, -1);
					}
				}

				if (doit) {
					final Calendar startTime = Calendar.getInstance();
					startTime.setTime(time);
					CTimeUtils.convertToStartDate(startTime);

					final Calendar stopTime = Calendar.getInstance();
					stopTime.setTime(time);
					stopTime.add(Calendar.MINUTE, -1);
					CTimeUtils.convertToEndTime(stopTime);

					// is more as 59 seconds? (one minute)
					if (stopTime.getTimeInMillis() - startTime.getTimeInMillis() >= 59000) {
						// yes - try to add continuation record
						addContinuationOfLastRecord(lastActivity, startTime, stopTime, userFromRequest);
					}
					// inak - nevkladame nic
				}
			}
		}
	}

	/**
	 * Adds continuation of the previous record if the activity of the one is "work
	 * alertness" or "work interactive" and starts in previous day!
	 * 
	 * @param record input - new record
	 * @throws CSecurityException
	 * @throws CBusinessException
	 */
	private void addContinuationOfLastRecord(final CTimeStampAddRecord record) throws CBusinessException {
		this.addContinuationOfLastRecord(record.getEmployeeId(), record.getTime(), false, null);
	}

	private void addContinuationOfLastRecord(final CTimeSheetRecord lastActivity, final Calendar startTime, final Calendar stopTime, CUser userFromRequest) throws CBusinessException {
		
		CUser changedBy = null;
				
		if (userFromRequest != null) {
			changedBy = userFromRequest;
		} else {
			final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
			changedBy = this.userDao.findById(loggedUser.getUserId());
		}

		// prepare a continuation record
		final CTimeSheetRecord continuationActivity = new CTimeSheetRecord();
		continuationActivity.setActivity(lastActivity.getActivity());
		continuationActivity.setClient(lastActivity.getClient());
		continuationActivity.setNote(lastActivity.getNote());
		continuationActivity.setOutside(lastActivity.getOutside());
		if (lastActivity.getHomeOffice() != null) {
			continuationActivity.setHomeOffice(lastActivity.getHomeOffice());
		}
		continuationActivity.setOwner(lastActivity.getOwner());
		continuationActivity.setPhase(lastActivity.getPhase());
		continuationActivity.setProject(lastActivity.getProject());
		continuationActivity.setValid(Boolean.TRUE);

		continuationActivity.setTimeFrom(startTime);
		continuationActivity.setTimeTo(stopTime);

		continuationActivity.setChangedBy(changedBy);
		continuationActivity.setChangeTime(Calendar.getInstance());
		continuationActivity.setCreatedBy(changedBy);
		continuationActivity.setStatus(this.stateDao.findById(ITimeSheetRecordStates.ID_NEW));

		// SED-290 check user/activity/time limits and update if necessary
		this.activityTimeLimitCheckService.updateClosingTimestampTimeToValueByUserActivityLimits(continuationActivity, Boolean.TRUE);

		// check: timestamp reason
		this.checkTimestampReason(continuationActivity);

		this.timesheetDao.saveOrUpdate(continuationActivity);
	}

	private void updateRemainingDaysAfterBrwItemDelete(final CUser owner, Calendar date, Boolean halfDayVacation) throws CBusinessException {
		Boolean nextYear = isRequestOnlyInNextYear(date, date);

		Double remainingDays = owner.getVacation();
		Double remainingDaysNextYear = owner.getVacationNextYear();
		final CUser changedBy = this.userDao.findById(0L);

		if (remainingDays != null) {

			if (halfDayVacation) {
				if (nextYear) {
					if (remainingDaysNextYear != null) {
						owner.setVacationNextYear(remainingDaysNextYear + 0.5, changedBy);
					}
				} else {
					if (remainingDays != null) {
						owner.setVacation(remainingDays + 0.5, changedBy);
					}
				}
			} else {
				if (nextYear) {
					if (remainingDaysNextYear != null) {
						owner.setVacationNextYear(remainingDaysNextYear + 1.0, changedBy);
					}
				} else {
					if (remainingDays != null) {
						owner.setVacation(remainingDays + 1.0, changedBy);
					}
				}
			}

			this.userDao.saveOrUpdate(owner);
		}
	}

	private void updateRemainingDaysAfterBrwItemAdd(final CUser owner, Calendar date, Boolean halfDayVacation) throws CBusinessException {
		Boolean nextYear = isRequestOnlyInNextYear(date, date);

		Double remainingDays = owner.getVacation();
		Double remainingDaysNextYear = owner.getVacationNextYear();
		final CUser changedBy = this.userDao.findById(0L);

		if (remainingDays != null) {

			if (halfDayVacation) {
				if (nextYear) {
					if (remainingDaysNextYear != null) {
						owner.setVacationNextYear(remainingDaysNextYear - 0.5, changedBy);
					}
				} else {
					if (remainingDays != null) {
						owner.setVacation(remainingDays - 0.5, changedBy);
					}
				}
			} else {
				if (nextYear) {
					if (remainingDaysNextYear != null) {
						owner.setVacationNextYear(remainingDaysNextYear - 1.0, changedBy);
					}
				} else {
					if (remainingDays != null) {
						owner.setVacation(remainingDays - 1.0, changedBy);
					}
				}
			}

			this.userDao.saveOrUpdate(owner);
		}
	}

	private void updateRemainingDaysAfterBrwItemModify(final CUser owner, Calendar date, Boolean add) throws CBusinessException {
		Boolean nextYear = isRequestOnlyInNextYear(date, date);

		Double remainingDays = owner.getVacation();
		Double remainingDaysNextYear = owner.getVacationNextYear();
		final CUser changedBy = this.userDao.findById(0L);

		if (nextYear) {
			if (remainingDaysNextYear != null) {
				if (add) {
					owner.setVacationNextYear(remainingDaysNextYear + 0.5, changedBy);
				} else {
					owner.setVacationNextYear(remainingDaysNextYear - 0.5, changedBy);
				}

			}
		} else {
			if (remainingDays != null) {
				if (add) {
					owner.setVacation(remainingDays + 0.5, changedBy);
				} else {
					owner.setVacation(remainingDays - 0.5, changedBy);
				}
			}
		}

		this.userDao.saveOrUpdate(owner);
	}

	/**
	 * 
	 * @return true if request is only in next year
	 */
	private boolean isRequestOnlyInNextYear(Calendar dateFrom, Calendar dateTo) {
		int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
		return dateFrom.get(Calendar.YEAR) == nextYear && dateTo.get(Calendar.YEAR) == nextYear;
	}

	/**
	 * metóda pripočíta dni dovolenky, ak sa nepodarí vygenerovať časová značka
	 * automaticky
	 */
	public void updateRemainingDaysForGenerateRecordHalfDay(CRequest request, Boolean isHalfDay) {

		Boolean nextYear = isRequestOnlyInNextYear(request.getDateFrom(), request.getDateFrom());
		Double remainingDays = request.getOwner().getVacation();
		Double remainingDaysNextYear = request.getOwner().getVacationNextYear();
		final CUser changedBy = this.userDao.findById(0L);

		if (isHalfDay) {
			if (nextYear) {
				if (remainingDaysNextYear != null) {
					request.getOwner().setVacationNextYear(remainingDaysNextYear + 0.5, changedBy);
				}
			} else {
				if (remainingDays != null) {
					request.getOwner().setVacation(remainingDays + 0.5, changedBy);
				}
			}
		} else {
			if (nextYear) {
				if (remainingDaysNextYear != null) {
					request.getOwner().setVacationNextYear(remainingDaysNextYear + 1.0, changedBy);
				}
			} else {
				if (remainingDays != null) {
					request.getOwner().setVacation(remainingDays + 1.0, changedBy);
				}
			}
		}

		this.userDao.saveOrUpdate(request.getOwner());
	}

	/**
	 * metóda kontroluje či je nepracovná aktivita pridávaná na pracovný deň, ak nie
	 * vyhodí chybu
	 */
	private void checkAddNonWorkingRecordForNonWorkingDay(CTimeSheetRecord record) throws CBusinessException {

		Long activityId = record.getActivity().getId();
		List<CHoliday> holidays = holidayDao.findAllValidClientsHolidays(record.getClient().getId(), null);
		boolean isWorkingDay = CDateServerUtils.isWorkingDayWithCheckHolidays(record.getTimeFrom().getTime(), holidays);

		// ak to nie je pracovný deň a je to nepracovná aktivita (-3 = Dovolenka, -4 =
		// Náhradné voľno, -5 = Práceneschopnosť, -6 = Prekážky v práci)
		if (!isWorkingDay && (Long.valueOf(-3).equals(activityId) || Long.valueOf(-4).equals(activityId) || Long.valueOf(-5).equals(activityId) || Long.valueOf(-6).equals(activityId))) {
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_CANT_ADD_NON_WORKING_ACTIVITY);
		}
	}

	/**
	 * 
	 * @param CTimeStampRecord
	 * @throws CBusinessException
	 */
	private void checkOutsideAndHomeOffice(final CTimeStampRecord record) throws CBusinessException {
		this.checkOutsideAndHomeOffice(record.getHomeOffice(), record.getOutsideWorkplace());
	}

	/**
	 * 
	 * @param CTimeStampRecord
	 * @throws CBusinessException
	 */
	private void checkOutsideAndHomeOffice(final CTmpTimeSheetRecord record) throws CBusinessException {
		this.checkOutsideAndHomeOffice(record.getHomeOffice(), record.getOutside());
	}

	/**
	 * 
	 * @param CTimeStampRecord
	 * @throws CBusinessException
	 */
	private void checkOutsideAndHomeOffice(Boolean homeOffice, Boolean outsideWorkplace) throws CBusinessException {
		if ((Boolean.TRUE.equals(homeOffice)) && (Boolean.TRUE.equals(outsideWorkplace))) {
			throw new CBusinessException(CClientExceptionsMessages.OUTSIDE_HOMEOFFICE_CANNOT_BE_MARKED_TOGETHER);
		}
	}

	/**
	 * 
	 * @param CTimeStampRecord
	 * @throws CBusinessException
	 */
	public void checkHomeOffice(final CTimeStampRecord record) throws CBusinessException {

		this.checkHomeOffice(record.getEmployeeId(), record.getHomeOffice(), record.getDate());
	}

	/**
	 * 
	 * @param CTimeStampRecord
	 * @throws CBusinessException
	 */
	public void checkHomeOffice(final CTmpTimeSheetRecord record) throws CBusinessException {
		this.checkHomeOffice(record.getOwnerId(), record.getHomeOffice(), record.getDateFrom().getTime());
	}

	/**
	 * 
	 * @param CTimeStampRecord
	 * @throws CBusinessException
	 */
	public void checkHomeOffice(Long userId, Boolean homeOffice, Date date) throws CBusinessException {

		final CUser user = this.userDao.findById(userId);

		if (homeOffice != null) {
			if (user.getHomeOfficePermission().getId().equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
				// ak je označená práca z domu keď používateľ nemá povolenie
				if (Boolean.TRUE.equals(homeOffice)) {
					throw new CBusinessException(CClientExceptionsMessages.NO_PERMISSION_FOR_HOME_OFFICE);
				}
			} else if (user.getHomeOfficePermission().getId().equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
				// ak pre dátum časovej značky nie je vytvorená žiadosť na prácu z domu a práca z domu je označená tak chyba
				if ((!this.requestDao.existsClientRequestsHomeOffice4Today(userId, date)) && homeOffice) {
					throw new CBusinessException(CClientExceptionsMessages.NO_REQUEST_FOR_HOME_OFFICE);
				}

			} else if (user.getHomeOfficePermission().getId().equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
				// nič
			}
		}
	}

	@Override
	public CLockRecord split(CTimeStampRecord record, Date splitTime) throws CBusinessException {
		// na základe pôvodného záznamu si vytvorím dva nové objekty pre pridanie a zmenu
		CTimeStampRecord recordToAdd = new CTimeStampRecord(record);
		CTimeStampRecord recordToModify = new CTimeStampRecord(record);

		// pôvodný záznam ukončím v čase, ktorý som zadal na obrazovke
		Date splitTimeForModify = (Date) splitTime.clone();

		// potrebujem pridat kontrolu, ze ci pri modifikovanej znacke nie je cas mensi 
		// ako povodna cas od v povodnej znacke
		checkSplitTimeIsBetween(recordToModify, splitTimeForModify);

		// az po kontrole nasetovat
		recordToModify.setTimeTo(splitTimeForModify);

		// nový záznam začnem v čase, ktorý som zadal na obrazovke + 1 minúta
		Date splitTimeForAdd = (Date) splitTime.clone();
		CTimeUtils.addMinute(splitTimeForAdd);

		recordToAdd.setTimeFrom(splitTimeForAdd);

		// modifikujem pôvodný záznam
		modifyBrwItem(recordToModify.getId(), recordToModify, Calendar.getInstance().getTime());

		// pridám nový záznam
		addBrwItem(recordToAdd, false);

		// prepares return value
		final CLockRecord retVal = new CLockRecord();
		retVal.setId(recordToModify.getId());
		retVal.setLastChangeDate(recordToModify.getChangeTime());

		return retVal;
	}

	private void checkSplitTimeIsBetween(CTimeStampRecord record, Date splitTimeForModify) throws CBusinessException {
		if ((record.getTimeFrom() != null) && (splitTimeForModify != null) && splitTimeForModify.before(record.getTimeFrom())) {
			// Čas rozdelenia nesmie byť menší ako čas od.
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_SPLIT_TIME_IS_LESS);
		}

		if ((record.getTimeTo() != null) && (splitTimeForModify != null) && splitTimeForModify.after(record.getTimeTo())) {
			// Čas rozdelenia nesmie byť väčší ako čas do.
			throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_SPLIT_TIME_IS_GREATER);
		}
	}

	private CTimeSheetRecord getLastActivityUnfinishedAlertnessWork(Long userId, CTimeSheetRecord record) throws CBusinessException {
		
		final Calendar timeOfEnd = Calendar.getInstance();
		timeOfEnd.setTime(record.getTimeFrom().getTime());
		final CTimeSheetRecord lastActivity = this.timesheetDao.findLast(userId, timeOfEnd);
		if ((lastActivity != null) && (lastActivity.getTimeTo() == null)) {
			// posledná aktivita je neukončená pohotovosť
			if (lastActivity.getActivity() != null && (lastActivity.getActivity().getId().equals(IActivityConstant.NOT_WORK_ALERTNESSWORK))) {
				return lastActivity;
			}
		}
		return null;
	}
}