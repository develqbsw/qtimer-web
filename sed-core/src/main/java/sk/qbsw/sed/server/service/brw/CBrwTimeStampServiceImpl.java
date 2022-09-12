package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CMyTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.request.CBrwTimeStampMassChangeRequest;
import sk.qbsw.sed.client.service.brw.IBrwTimeStampService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewTimeStampDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.business.IRequestTypeConstant;
import sk.qbsw.sed.server.service.business.ITimesheetBaseService;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;

/**
 * Service for loading data to browser
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Service(value = "brwTimeStampService")
public class CBrwTimeStampServiceImpl implements IBrwTimeStampService {

	@Autowired
	private IViewTimeStampDao timeStampDao;

	@Autowired
	private ITimesheetBaseService timeSheetBaseService;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IRequestDao requestDao;

	@Autowired
	private IActivityDao activityDao;

	@Transactional(readOnly = true)
	public List<CViewTimeStamp> loadData(final int startRow, final int endRow, final IFilterCriteria criteria) throws CBusinessException {
		final CMyTimeStampBrwFilterCriteria crit = (CMyTimeStampBrwFilterCriteria) criteria;
		return this.timeStampDao.findAll(this.userDao.findById(this.getUserId()), crit, startRow, endRow);
	}

	/**
	 * Gets id of logged user
	 * 
	 * @param crit
	 * @return
	 * @throws CSecurityException
	 */
	private Long getUserId() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return loggedUser.getUserId();
	}

	@Transactional(readOnly = false, rollbackForClassName = { "CBusinessException" })
	@Override
	public CTimeStampRecord add(final CTimeStampRecord timeStampRecord) throws CBusinessException {
		final CUser owner = this.userDao.findById(timeStampRecord.getEmployeeId());
		this.checkRequestExistence4NonWorkingRecord(timeStampRecord.getActivityId(), timeStampRecord.getRequestReasonId(), timeStampRecord.getDateFrom(), owner);

		this.checkBeforeExecute(timeStampRecord);

		return this.timeSheetBaseService.addBrwItem(timeStampRecord, false);
	}

	@Transactional(readOnly = false, rollbackForClassName = { "CBusinessException" })
	@Override
	public CTimeStampRecord update(final CTimeStampRecord timeStampRecord) throws CBusinessException {
		final CUser owner = this.userDao.findById(timeStampRecord.getEmployeeId());
		if (timeStampRecord.getId().longValue() < 0) {
			// add record
			this.checkRequestExistence4NonWorkingRecord(timeStampRecord.getActivityId(), timeStampRecord.getRequestReasonId(), timeStampRecord.getDateFrom(), owner);

			this.checkBeforeExecute(timeStampRecord);
			return this.timeSheetBaseService.addBrwItem(timeStampRecord, false);
		} else {
			// modify record
			this.checkBeforeExecute(timeStampRecord);
			this.checkRequestExistence4NonWorkingRecord(timeStampRecord.getActivityId(), timeStampRecord.getRequestReasonId(), timeStampRecord.getDateFrom(), owner);

			final CTimeSheetRecord newRecord = this.timeSheetBaseService.modifyBrwItem(timeStampRecord.getId(), timeStampRecord, Calendar.getInstance().getTime());
			return newRecord.convert();
		}
	}

	private void checkBeforeExecute(final CTimeStampRecord record) throws CBusinessException {
		final Long activityId = record.getActivityId();
		if (null != activityId) {
			final CActivity activity = this.activityDao.findById(activityId);
			if (null != activity && activity.getWorking() && (null == record.getProjectId()) && CServletSessionUtils.getLoggedUser().getClientInfo().getProjectRequired()) {
				throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_PROJECT_REQUIRED);
			}
		}
	}

	/**
	 * @see IBrwTimeStampService#getWorkTimeInInterval(Long, Date, Date)
	 */
	@Transactional(readOnly = true)
	@Override
	public long getWorkTimeInInterval(Long userId, Date dateFrom, Date dateTo) {
		CMyTimeStampBrwFilterCriteria criteria = new CMyTimeStampBrwFilterCriteria();
		criteria.setActivityId(ISearchConstants.ALL_WORKING);
		criteria.setDateFrom(dateFrom);
		criteria.setDateTo(dateTo);

		final Calendar tmp = Calendar.getInstance();
		tmp.setTimeInMillis(0l);
		long duration = 0l;

		List<CViewTimeStamp> stamps = this.timeStampDao.findAll(this.userDao.findById(userId), criteria);
		for (CViewTimeStamp stamp : stamps) {
			long intervalDuration = 0l;
			if (stamp.getTimeTo() != null) {
				intervalDuration = stamp.getTimeTo().getTime().getTime() - stamp.getTimeFrom().getTime().getTime();
				intervalDuration = intervalDuration + 1; // korekcia
			}
			if (intervalDuration > 0l) {
				duration += intervalDuration;
			}
		}

		return duration;
	}

	/**
	 * Check if user request (holiday, paid free/replwork, sickness, break in work)
	 * exists for input record with specific non work activity
	 * 
	 * @param timestamp checked record
	 * @throws CBusinessException if associated request is missing
	 */
	private void checkRequestExistence4NonWorkingRecord(Long activityId, Long reasonId, Date checkDate, CUser owner) throws CBusinessException {
		Boolean existsRequest = Boolean.FALSE;
		Boolean requiredRequest = Boolean.FALSE;

		List<CRequest> approvedUserRequests = this.requestDao.findAllApprovedUserRequestContainsDate(checkDate, owner);

		if (IActivityConstant.NOT_WORK_WORKBREAK.equals(activityId)) {
			if (reasonId != null) {
				requiredRequest = Boolean.TRUE;
			}
			if (requiredRequest) {

				// exists work break request?
				for (CRequest request : approvedUserRequests) {
					if (IRequestTypeConstant.RQTYPE_WORKBREAK_CODE.equalsIgnoreCase(request.getType().getCode())) {
						existsRequest = Boolean.TRUE;
						break;
					}
				}
			}
		} else if (IActivityConstant.NOT_WORK_HOLIDAY.equals(activityId)) {
			requiredRequest = Boolean.TRUE;
			// exists holiday request?
			for (CRequest request : approvedUserRequests) {
				if (IRequestTypeConstant.RQTYPE_VACATION_CODE.equalsIgnoreCase(request.getType().getCode())) {
					existsRequest = Boolean.TRUE;
					break;
				}
			}

		} else if (IActivityConstant.NOT_WORK_SICKNESS.equals(activityId)) {
			requiredRequest = Boolean.TRUE;
			// exists sickness request?
			for (CRequest request : approvedUserRequests) {
				if (IRequestTypeConstant.RQTYPE_SICKNESS_CODE.equalsIgnoreCase(request.getType().getCode())) {
					existsRequest = Boolean.TRUE;
					break;
				}
			}

		} else if (IActivityConstant.NOT_WORK_REPLWORK.equals(activityId)) {
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
			// sem by normalne patrila business exception, ale kod pre tabulky je spracuvany inak a k uzivatelovi sa dostanu 
			// prelozene len security exception - nebudem prerabat kod, lebo to moze mat dalsie vedlajsie ucinky
			throw new CBusinessException(CClientExceptionsMessages.MISSING_REQUEST_FOR_NON_WORK_ACTIVITY);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CTimeStampRecord> loadData(Integer first, Integer count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException {

		final CSubrodinateTimeStampBrwFilterCriteria crit = (CSubrodinateTimeStampBrwFilterCriteria) criteria;
		final List<CViewTimeStamp> timeStamps = this.timeStampDao.findAll(this.userDao.findById(this.getUserId()), crit, first, count, sortProperty, sortAsc);

		List<CTimeStampRecord> retVal = new ArrayList<>();

		for (CViewTimeStamp record : timeStamps) {
			retVal.add(record.convertToTimeStampRecord());
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(IFilterCriteria criteria) throws CBusinessException {

		final CSubrodinateTimeStampBrwFilterCriteria crit = (CSubrodinateTimeStampBrwFilterCriteria) criteria;
		return this.timeStampDao.count(this.userDao.findById(this.getUserId()), crit);
	}

	/**
	 * @see IBrwTimeStampService#getDetail(Long)
	 */
	@Override
	@Transactional(readOnly = true)
	public CTimeStampRecord getDetail(final Long timeStampId) {
		final CViewTimeStamp record = this.timeStampDao.findById(timeStampId);
		return record.convertToTimeStampRecord();
	}

	@Transactional(readOnly = false, rollbackForClassName = { "CBusinessException" })
	@Override
	public Long massChangeTimestamps(CBrwTimeStampMassChangeRequest request) throws CBusinessException {
		/*
		 * filtračné kritéria z browsáku výkazu práce na základe ktorých si dotiahnem
		 * rovnaké ČZ ako mám na obrazovke plus všetky podstránky, lebo chcem meniť aj
		 * tie ktoré sú na ďalších stranách
		 */
		final CSubrodinateTimeStampBrwFilterCriteria crit = request.getCriteria();

		// dotiahnem si všetky ČZ podľa filtra
		final List<CViewTimeStamp> timeStamps = this.timeStampDao.findAll(this.userDao.findById(this.getUserId()), crit);

		List<CTimeStampRecord> retVal = new ArrayList<>();

		for (CViewTimeStamp record : timeStamps) {
			retVal.add(record.convertToTimeStampRecord());
		}

		// prejdem si a zmením všetky ČZ
		for (CTimeStampRecord record : retVal) {

			// ak je zaškrtnutý checkbox aktivita tak nasetujem vybranú aktivitu
			if (request.getFormModel().getActivityChecked()) {
				record.setActivity(request.getFormModel().getActivity());
			}

			// ak je zaškrtnutý checkbox projekt tak nasetujem vybraný projekt
			if (request.getFormModel().getProjectChecked()) {
				record.setProject(request.getFormModel().getProject());
			}

			// ak je zaškrtnutý checkbox fáza tak nasetujem vyplnenú fázu
			if (request.getFormModel().getPhaseChecked()) {
				record.setPhase(request.getFormModel().getPhase());
			}

			// ak je zaškrtnutý checkbox poznámka tak nasetujem vyplnenú poznámku
			if (request.getFormModel().getNoteChecked()) {
				record.setNote(request.getFormModel().getNote());
			}

			update(record);
		}

		return Long.valueOf(retVal.size());
	}
}
