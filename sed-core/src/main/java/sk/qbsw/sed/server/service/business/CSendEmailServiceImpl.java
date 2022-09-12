package sk.qbsw.sed.server.service.business;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CEmployeesStatusBrwFilterCriteria;
import sk.qbsw.sed.client.service.business.ISendEmailService;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.INotificationDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.IRequestForEmailDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewEmployeesStatusDao;
import sk.qbsw.sed.server.dao.IViewTimeStampDao;
import sk.qbsw.sed.server.dao.IZoneDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.brw.CViewEmployeesStatus;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;
import sk.qbsw.sed.server.model.codelist.CEmailWithLanguage;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CRequestForEmail;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.domain.CZone;
import sk.qbsw.sed.server.util.CLocaleUtils;

/**
 * 
 * @author rosenberg
 * 
 */
@Service(value = "sendEmailService")
public class CSendEmailServiceImpl implements ISendEmailService {

	private final Logger logger = Logger.getLogger(CSendEmailServiceImpl.class.getName());

	@Autowired
	private INotificationDao notificationDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IViewEmployeesStatusDao employeesStatusDao;

	@Autowired
	private IRequestForEmailDao requestforEmailDao;

	@Autowired
	private IViewTimeStampDao viewTimeStamp;

	@Autowired
	private IZoneDao zoneDao;
	
	@Autowired
	private IRequestDao requestDao;

	/**
	 * Messaging support
	 */
	@Autowired
	private MessageSource messages;

	@Transactional(readOnly = true)
	@Override
	public Long sendMissingEmployeesEmail(String email) {
		return sendMissingEmployeesEmailMethod(null, email);
	}

	@Transactional
	@Override
	public Long sendMissingEmployeesEmail(CUserSystemEmailContainer data) throws CSecurityException {
		Long retVal = Long.valueOf(0); // default OK
		final CLoggedUserRecord loggedUserInfo = CServletSessionUtils.getLoggedUser();
		CUser loggedUser = this.userDao.findById(loggedUserInfo.getUserId());

		List<CUserSystemEmailRecord> list = data.getSelectedUsers();
		// save selected
		for (CUserSystemEmailRecord record : list) {
			CUser user = this.userDao.findById(record.getUserId());
			retVal = retVal + sendMissingEmployeesEmailMethod(loggedUser, user.getEmail());
		}
		return retVal;
	}

	@Override
	public Long sendMissingEmployeesEmailMethod(CUser loggedUser, String email) {
		Long retVal = Long.valueOf(0); // default OK

		try {
			// select employees list
			List<CViewEmployeesStatus> employeesStatusList = this.employeesStatusDao.findAllMissing(loggedUser, Calendar.getInstance());

			if (!employeesStatusList.isEmpty()) {
				// create output structure
				Map<String, ArrayList<String>> outputAbsenceReasonEmployeeLists = new HashMap<>();
				// initialize the structure
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_VACATION, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_REPLWORK, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_SICKNESS, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_WORKBREAK, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_BUSTRIP, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_STAFF_TRAINING, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_WORK_AT_HOME, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_NO_REQUEST, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_OUT_OF_OFFICE, new ArrayList<String>());
				outputAbsenceReasonEmployeeLists.put(IRequestTypeConstant.RQTYPE_NO_REQUEST_DOCTOR_VISIT, new ArrayList<String>());

				Date dateFrom = CDateUtils.getCurrentDayAsStartDay();

				// current time
				final Calendar currentDateTime = Calendar.getInstance();
				// string
				String sCurrentDateTime = CDateUtils.convertToDateString(currentDateTime);
				sCurrentDateTime += ", " + CDateUtils.convertToTimeString(currentDateTime);

				// only approved requests!!!
				processMissingEmployeesRecords(employeesStatusList, dateFrom, true, outputAbsenceReasonEmployeeLists, loggedUser.getClient().getId());

				// sort list by locale
				Set<String> listKeys = outputAbsenceReasonEmployeeLists.keySet();
				for (String listKey : listKeys) {
					if (!outputAbsenceReasonEmployeeLists.get(listKey).isEmpty())
						sortByLocale(outputAbsenceReasonEmployeeLists.get(listKey));
				}
				
				// priradím poradové číslo každému zamestnancovi v sekcii
				Set<String> keySet = outputAbsenceReasonEmployeeLists.keySet();
				for (String keyList : keySet) {
					if (!outputAbsenceReasonEmployeeLists.get(keyList).isEmpty()) {
						for (int i = 0; i < outputAbsenceReasonEmployeeLists.get(keyList).size(); i++) {
							String employee = (i + 1) + ". " + outputAbsenceReasonEmployeeLists.get(keyList).get(i);
							outputAbsenceReasonEmployeeLists.get(keyList).set(i, employee);
						}
					}
				}

				// send email
				this.notificationDao.sendMissigEmployeesEmail(
						// email address
						email,
						// holidays
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_VACATION),
						// free paids
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_REPLWORK),
						// sickness
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_SICKNESS),
						// work break
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_WORKBREAK),
						// bus trip
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_BUSTRIP),
						// staff training
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_STAFF_TRAINING),
						// work at home
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_WORK_AT_HOME),
						// without request
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_NO_REQUEST),
						// date time information
						sCurrentDateTime, outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_OUT_OF_OFFICE),
						outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_NO_REQUEST_DOCTOR_VISIT));

			} else {
				// no mail should be send
				retVal = Long.valueOf(1);
			}
		} catch (CBusinessException e) {
			logger.info(e);
			// error in process
			retVal = Long.valueOf(2);
		}

		return retVal;
	}

	/**
	 * Updates output lists
	 * 
	 * @param requestStatusCode                input request status code
	 * @param employeesStatusList              input list of missing employee
	 *                                         records
	 * @param dateFrom                         input date start date interval
	 * @param dateTo                           input date end date interval
	 * @param processWithourRequest            input flag if the records without
	 *                                         requests should be process also
	 * @param outputAbsenceReasonEmployeeLists output map of list of employees
	 *                                         separated by absence reason
	 */
	private void processMissingEmployeesRecords(List<CViewEmployeesStatus> employeesStatusList, Date dateFrom, Boolean processWithoutRequestAlso,
			Map<String, ArrayList<String>> outputAbsenceReasonEmployeeLists, final Long clientId) {
		List<Long> processedUsersIds = new ArrayList<>();

		// check employee
		List<CRequestForEmail> requests = requestforEmailDao.findAllNotCancelledInTimeInterval(dateFrom, clientId);

		for (CRequestForEmail request : requests) {
			// uzivatel tam moze mat mnoho zaznamov,
			// ale v emaili sa bude zobrazovat len jeden
			if (!processedUsersIds.contains(request.getOwner().getId())) {

				String userInfo = request.getOwner().getSurname() + " " + request.getOwner().getName() + "   " + getRequestRange(request) + "  " + getRequestReason(request);
				String rqTypeCode = request.getType().getMsgCode();

				if (rqTypeCode.equals(IRequestTypeConstant.RQTYPE_VACATION)) {
					
					CRequest requestEntity = this.requestDao.findById(request.getId());
					
					if (requestEntity != null) {
						CRequestRecord requestRecord = requestEntity.convert();

						if (requestRecord != null) {
							if (requestRecord.getHalfday()) {
								userInfo = userInfo + " (pol dňa)";
							}
						}
					}
				}
				
				if (outputAbsenceReasonEmployeeLists.get(rqTypeCode) != null) {
					outputAbsenceReasonEmployeeLists.get(rqTypeCode).add(userInfo);
					processedUsersIds.add(request.getOwner().getId());
				}
			}
		}

		// check doctor visit timestamps
		List<CViewTimeStamp> doctorVisitTimeStamps = viewTimeStamp.getDoctorVisitTimeStamps(dateFrom, clientId);

		for (CViewTimeStamp timeStamp : doctorVisitTimeStamps) {
			// uzivatel tam moze mat mnoho zaznamov,
			// ale v emaili sa bude zobrazovat len jeden
			if (!processedUsersIds.contains(timeStamp.getUserId())) {
				outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_NO_REQUEST_DOCTOR_VISIT).add(timeStamp.getUserSurname() + " " + timeStamp.getUserName());
				processedUsersIds.add(timeStamp.getUserId());
			}
		}
		
		// check home office timestamps
		List<CViewTimeStamp> homeOfficeTimeStamps = viewTimeStamp.getHomeOfficeTimeStamps(dateFrom, clientId);

		for (CViewTimeStamp timeStamp : homeOfficeTimeStamps) {
			// uzivatel tam moze mat mnoho zaznamov,
			// ale v emaili sa bude zobrazovat len jeden
			if (!processedUsersIds.contains(timeStamp.getUserId())) {
				
				final List<CRequest> approvedUserRequests = this.requestDao.findAllApprovedUserRequestContainsDate(dateFrom, timeStamp.getUserId());

				if (approvedUserRequests.isEmpty()) {
					outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_WORK_AT_HOME).add(timeStamp.getUserSurname() + " " + timeStamp.getUserName() + " (bez zadanej žiadosti)");
					processedUsersIds.add(timeStamp.getUserId());
				}
			}
		}
		
		// check not home office timestamps
		List<CViewTimeStamp> notHomeOfficeTimeStamps = viewTimeStamp.getNotHomeOfficeTimeStamps(dateFrom, clientId);
		
		for (CViewTimeStamp timeStamp : notHomeOfficeTimeStamps) {
			// uzivatel tam moze mat mnoho zaznamov,
			// ale v emaili sa bude zobrazovat len jeden
			if (!processedUsersIds.contains(timeStamp.getUserId())) {
				
				final List<CRequest> createdUserRequestsForHomeOffice = this.requestDao.findAllCreatedUserRequestForHomeOfficeContainsDate(dateFrom, timeStamp.getUserId());

				if (!createdUserRequestsForHomeOffice.isEmpty()) {
					outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_WORK_AT_HOME).add(timeStamp.getUserSurname() + " " + timeStamp.getUserName() 
															+ "   " + getRequestRange(createdUserRequestsForHomeOffice.get(0)) + "  " + getRequestReason(createdUserRequestsForHomeOffice.get(0)) + " (bez schválenej žiadosti)");
					processedUsersIds.add(timeStamp.getUserId());
				}
			}
		}

		// process all missing employees - not approved request after
		for (CViewEmployeesStatus employeeStatus : employeesStatusList) {
			// uzivatel tam moze mat mnoho zaznamov,
			// ale v emaili sa bude zobrazovat len jeden
			if (!processedUsersIds.contains(employeeStatus.getId())) {
				if (processWithoutRequestAlso) {
					CUser user = userDao.findById(employeeStatus.getId());

					if (user != null) {
						// ako neprítomých posielame len používateľov, ktorí
						// majú príznak c_flag_absent_check = true
						if (user.getAbsentCheck()) {
							outputAbsenceReasonEmployeeLists.get(IRequestTypeConstant.RQTYPE_NO_REQUEST).add(employeeStatus.getSurname() + " " + employeeStatus.getName());
						}
					}
					processedUsersIds.add(employeeStatus.getId());
				}
			}
		}
	}

	private String getRequestRange(CRequestForEmail request) {
		String sRequestStart;
		String sRequestStop;
		String sRequestRange = "";
		Calendar rqStart = request.getDateFrom();
		Calendar rqStop = request.getDateTo();

		if (rqStart != null && rqStop != null) {
			sRequestStart = CDateUtils.convertToDateString(rqStart);
			sRequestStop = CDateUtils.convertToDateString(rqStop);
			sRequestRange = sRequestStart + " - " + sRequestStop;
		}

		return sRequestRange;
	}

	private String getRequestRange(CRequest request) {
		String sRequestStart;
		String sRequestStop;
		String sRequestRange = "";
		Calendar rqStart = request.getDateFrom();
		Calendar rqStop = request.getDateTo();

		if (rqStart != null && rqStop != null) {
			sRequestStart = CDateUtils.convertToDateString(rqStart);
			sRequestStop = CDateUtils.convertToDateString(rqStop);
			sRequestRange = sRequestStart + " - " + sRequestStop;
		}

		return sRequestRange;
	}
	
	private String getRequestReason(CRequestForEmail request) {
		if (request.getReason() != null) {
			return request.getReason().getReasonName();
		}
		return "";
	}
	
	private String getRequestReason(CRequest request) {
		if (request.getReason() != null) {
			return request.getReason().getReasonName();
		}
		return "";
	}

	private void sortByLocale(List<String> list) {
		Collator collator = Collator.getInstance(new Locale("sk"));
		collator.setStrength(Collator.TERTIARY);
		Collections.sort(list, collator);
	}

	@Transactional(readOnly = true)
	@Override
	public void sendWarningToPresentedEmployees(Long warned, Long zoneId, List<Long> userIds) throws CBusinessException {
		CUser warnedUser = userDao.findById(warned);

		CEmployeesStatusBrwFilterCriteria criteria = new CEmployeesStatusBrwFilterCriteria();
		criteria.setAtWorkplace(true);
		criteria.setDate(new Date());
		criteria.setZoneId(zoneId);
		criteria.setUserIds(userIds);

		List<CViewEmployeesStatus> list = employeesStatusDao.findAll(warnedUser.getClient().getId(), criteria, 0, Integer.MAX_VALUE);

		// Peto Pavlak chce aby bol email v jazyku odosielatela varovania
		Map<String, Object> emailModel = getModel(warnedUser.getLanguage(), warnedUser.getName() + " " + warnedUser.getSurname());

		List<CEmailWithLanguage> to = new ArrayList<>();

		String s = "";
		boolean first = true;
		for (CViewEmployeesStatus e : list) {
			if (!warnedUser.getId().equals(e.getId())) {
				if (first) {
					s = "<tr><td align=\"right\">" + this.messages.getMessage("label.employee", null, CLocaleUtils.getLocale(warnedUser.getLanguage())) + " </td><td>&nbsp;" + e.getName() + " "
							+ e.getSurname() + "</td></tr>";
					first = false;
				} else {
					s += "<tr><td align=\"right\"></td><td>&nbsp;" + e.getName() + " " + e.getSurname() + "</td></tr>";
				}

				CUser user = userDao.findById(e.getId());
				to.add(new CEmailWithLanguage(user.getEmail(), user.getLanguage()));
			}
		}

		emailModel.put("EMPLOYEES", s);

		String zoneHtml = "";
		if (zoneId != null) {
			CZone zone = zoneDao.findById(zoneId);
			zoneHtml = "<tr><td align=\"right\">" + this.messages.getMessage("label.zone", null, CLocaleUtils.getLocale(warnedUser.getLanguage())) + " </td><td>&nbsp;" + zone.getName() + "</td></tr>";
		}

		emailModel.put("ZONE", zoneHtml);

		notificationDao.sendWarningToEmployee(to, warnedUser, emailModel);
	}

	private Map<String, Object> getModel(String language, String warned) throws CSecurityException {
		Locale locale = CLocaleUtils.getLocale(language);
		final Map<String, Object> emailModel = new HashMap<>();

		emailModel.put("TITLE", this.messages.getMessage("messages.nonSignedOffEmployees", null, locale));
		emailModel.put("LABEL_WARNED", this.messages.getMessage("label.warned", null, locale));
		emailModel.put("WARNED", warned);

		return emailModel;
	}
}
