package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IHomeOfficePermissionConstants;
import sk.qbsw.sed.client.model.IRequestStates;
import sk.qbsw.sed.client.model.IRequestTypes;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IRequestService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.ILockDateDao;
import sk.qbsw.sed.server.dao.INotificationDao;
import sk.qbsw.sed.server.dao.IOrganizationTreeDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.dao.IRequestStateDao;
import sk.qbsw.sed.server.dao.IRequestTypeDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewTimeStampDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.INotificationValue;
import sk.qbsw.sed.server.model.IUserRequestStatus;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;
import sk.qbsw.sed.server.model.codelist.CEmailWithLanguage;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.codelist.CRequestStatus;
import sk.qbsw.sed.server.model.domain.COrganizationTree;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.params.CLockDate;
import sk.qbsw.sed.server.service.CTimeUtils;
import sk.qbsw.sed.server.service.system.IAppCodeGenerator;
import sk.qbsw.sed.server.util.CDateServerUtils;
import sk.qbsw.sed.server.util.CLocaleUtils;

/**
 * Service for managing requests
 * 
 * @author Dalibor Rak
 * @since 0.1
 * @version 0.1
 * 
 */
@Service(value = "requestServiceImpl")
public class CRequestServiceImpl implements IRequestService {

	@Autowired
	private IRequestDao requestDao;

	@Autowired
	private IRequestStateDao stateDao;

	@Autowired
	private IRequestTypeDao typeDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private INotificationDao notificationDao;

	@Autowired
	private IOrganizationTreeDao organizationTreeDao;

	@Autowired
	private ILockDateDao lockDateDao;

	@Autowired
	private IRequestReasonDao requestReasonDao;

	@Autowired
	private IAppCodeGenerator appCodeGenerator;

	@Autowired
	private IHolidayDao holidayDao;

	@Autowired
	private IViewTimeStampDao timeStampDao;

	/**
	 * OPERATION_ADD = 0
	 */
	private static final int OPERATION_ADD = 0;

	/**
	 * OPERATION_MODIFY_CANCEL = 1
	 */
	private static final int OPERATION_MODIFY_CANCEL = 1;

	/**
	 * OPERATION_APPROVE = 2
	 */
	private static final int OPERATION_APPROVE = 2;

	/**
	 * OPERATION_REJECT = 3
	 */
	private static final int OPERATION_REJECT = 3;

	/**
	 * Messaging support
	 */
	@Autowired
	private MessageSource messages;

	/**
	 * @see IRequestService#add(Long, CRequestRecord, boolean)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public String add(final Long type, final CRequestRecord model, final boolean ignoreDuplicity) throws CBusinessException {

		String retVal = "";
		final CUser owner = this.userDao.findById(model.getOwnerId());

		// SED-841 - overím, či existuje žiadosť na Dovolenku, Práceneschopnosť,
		// Náhradné voľno alebo Prekážky v práci
		final List<CRequest> crApRequests = this.requestDao.findAllApprovedAndCreatedHolidayInTimeInterval(model.getDateFrom(), model.getDateTo(), owner);
		if (!crApRequests.isEmpty()) {
			// SED-841 - ak je typ žiadosti Dovolenka, Práceneschopnosť, Náhradné voľno
			// alebo Prekážky v práci, nedovolím vytvoriť ďalšiu
			if (type == IRequestTypes.ID_H || type == IRequestTypes.ID_SD || type == IRequestTypes.ID_FP || type == IRequestTypes.ID_WB) {
				throw new CBusinessException(CClientExceptionsMessages.REQUEST_ALREADY_EXISTS);
			}
		}

		// checks
		boolean existsApprovedRequest = false;
		if (!ignoreDuplicity) {
			// do nothing
		} else {
			// do check - for all approved requests
			final List<CRequest> requests = this.requestDao.findAllApprovedInTimeInterval(model.getDateFrom(), model.getDateTo(), owner);
			if (!requests.isEmpty()) {
				existsApprovedRequest = true;
			}
		}

		// logic
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		final CRequest newRequest = new CRequest();

		newRequest.setChangedBy(changedBy);
		newRequest.setChangeTime(Calendar.getInstance());

		final Calendar createDate = Calendar.getInstance();
		createDate.setTime(model.getCreateDate());
		newRequest.setCreateDate(createDate);

		newRequest.setClient(owner.getClient());
		newRequest.setCreatedBy(changedBy);
		newRequest.setOwner(owner);

		if (userHasRightToSelfApproveRequest(owner)) {
			if (existsApprovedRequest) {
				// don't allow to create a new approved request, leave one as
				// created - send response about incident
				retVal = CClientExceptionsMessages.REQUEST_EXIST_APPROVED_IN_TIME;
				newRequest.setStatus(this.stateDao.findById(IRequestStates.ID_CREATED));
			} else {
				newRequest.setStatus(this.stateDao.findById(IRequestStates.ID_APPROVED));
			}
			newRequest.setType(this.typeDao.findById(type));
		} else {
			CUser parentUser = getParentUser(owner);

			if (parentUser != null && loggedUser.getUserId().equals(parentUser.getId())) {

				if (existsApprovedRequest) {
					// don't allow to create a new approved request, leave one as
					// created - send response about incident
					retVal = CClientExceptionsMessages.REQUEST_EXIST_APPROVED_IN_TIME;
					newRequest.setStatus(this.stateDao.findById(IRequestStates.ID_CREATED));
				} else {
					// ak ziadost vytvoril nadriadeny tak ju dame rovno schvalenu
					newRequest.setStatus(this.stateDao.findById(IRequestStates.ID_APPROVED));
				}
			} else {
				newRequest.setStatus(this.stateDao.findById(IRequestStates.ID_CREATED));
			}
			newRequest.setType(this.typeDao.findById(type));
		}

		newRequest.setNote(model.getNote());
		newRequest.setPlace(model.getPlace());

		if (model.getRequestReasonId() != null) {
			CRequestReason reason = this.requestReasonDao.findById(model.getRequestReasonId());
			newRequest.setReason(reason);
		}

		if (model.isFullday()) {
			List<CHoliday> holidays = holidayDao.findAllValidClientsHolidays(loggedUser.getClientInfo().getClientId(), null);
			float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(model.getDateFrom(), model.getDateTo(), holidays);
			newRequest.setNumberWorkDays(numberOfWorkingDays);
		} else {
			// pre istotu to pocitam, moze to byt 0.5 alebo 0
			newRequest.setNumberWorkDays((float) 0.5
					* CDateServerUtils.getWorkingDaysCheckHolidays(model.getDateFrom(), model.getDateTo(), holidayDao.findAllValidClientsHolidays(loggedUser.getClientInfo().getClientId(), null)));
		}

		final Calendar from = Calendar.getInstance();
		from.setTime(model.getDateFrom());
		CTimeUtils.convertToStartDate(from);
		newRequest.setDateFrom(from);

		// full day mode
		if (model.isFullday()) {
			final Calendar to = Calendar.getInstance();

			to.setTime(model.getDateTo());
			CTimeUtils.convertToStartDate(to);
			newRequest.setDateTo(to);

			newRequest.setHoursDateFrom(null);
			newRequest.setHoursDateTo(null);
		}
		// day part mode
		else {
			newRequest.setDateTo(from);
			newRequest.setHoursDateFrom((float) model.getHours());
			newRequest.setHoursDateTo(null);
		}

		if (model.getResponsalisName() != null) {
			newRequest.setResponsalisName(model.getResponsalisName());
		}

		// check lock condition
		this.checkLockRecordsConditions(newRequest);

		newRequest.setCode(this.appCodeGenerator.generateMessage(6, 3));

		// check request year
		this.checkRequestYear(newRequest);

		// check remaining days and update remaining days in user
		this.updateRemainingDaysAfterAdd(newRequest);

		this.requestDao.saveOrUpdate(newRequest);

		// pre add neusim poslat mail notified preto je to false
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(owner, IRequestStates.ID_APPROVED.equals(newRequest.getStatus().getId()));
		final List<String> subordinatesWithApprovedRequest = this.getSubordinatesWithApprovedRequest(owner, model.getDateFrom(), model.getDateTo());
		Map<String, Object> emailModel;

		// SED 364
		for (CEmailWithLanguage email : emails) {
			// prepare notification data
			emailModel = this.getNotificationEmailModel(-1, newRequest, OPERATION_ADD, email.getLanguage());

			if (IRequestStates.ID_APPROVED.equals(newRequest.getStatus().getId())) {
				if (newRequest.getNumberWorkDays() >= 1) {
					if (email.getEmail().equals(owner.getEmail())) {
						String subject = (String) emailModel.get(INotificationValue.REQUEST_TYPE);
						this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, createAttachmentWithOutOfOffice(subject, newRequest), subject + ".ics");
					} else {
						String subject = newRequest.getOwner().getName() + " " + newRequest.getOwner().getSurname() + " - " + emailModel.get(INotificationValue.REQUEST_TYPE);
						this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, createAttachmentStatusFree(subject, newRequest, getReplacement(email.getLanguage(), newRequest)),
								subject + ".ics");
					}
				} else {
					this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
				}
			} else {
				if (owner.getEmail().equals(email.getEmail())) {
					// ownerovi ziadosti poslem normalny email
					this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
				} else {
					// nadriadenemu poslem email s linkom na schvalenie alebo zamietnutie ziadosti
					this.notificationDao.sendNotificationAboutUserRequestWithLinks(email, emailModel, newRequest.getId().toString(), newRequest.getCode(), subordinatesWithApprovedRequest);
				}
			}
		}

		return retVal;
	}

	/**
	 * @see IRequestService#approve(Long)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public void approve(final Long requestId) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		final CRequest request = this.requestDao.findById(requestId);

		// 4 = status zamietnutá, 1 = typ dovolenka
		if (request.getStatus().getId().equals(Long.valueOf(IRequestStates.ID_REJECTED)) && request.getType().getId().equals(Long.valueOf(1))) {
			throw new CBusinessException(CClientExceptionsMessages.HOLIDAY_REQUEST_CANOT_APPROVE_REJECTED);
		}

		// check self approving
		if (request.getOwner().getId().equals(loggedUser.getUserId())) {
			// don't allow to create a new approved request
			throw new CBusinessException(CClientExceptionsMessages.REQUEST_APPROVING_BY_SUPERIOR_ONLY);
		}

		// SED-841 - overím, či existuje žiadosť na Dovolenku, Práceneschopnosť,
		// Náhradné voľno alebo Prekážky v práci
		final List<CRequest> crApRequests = this.requestDao.findAllApprovedHolidayInTimeInterval(request.getDateFrom().getTime(), request.getDateTo().getTime(), request.getOwner());
		if (!crApRequests.isEmpty()) {
			// SED-841 - ak je typ žiadosti Dovolenka, Práceneschopnosť,
			// Náhradné voľno alebo Prekážky v práci, nedovolím vytvoriť ďalšiu
			if (request.getType().getId() == IRequestTypes.ID_H || request.getType().getId() == IRequestTypes.ID_SD || request.getType().getId() == IRequestTypes.ID_FP
					|| request.getType().getId() == IRequestTypes.ID_WB) {
				throw new CBusinessException(CClientExceptionsMessages.REQUEST_EXIST_APPROVED_IN_TIME);
			}
		}

		int oldRequestStausId = request.getStatus().getId().intValue();

		request.setChangedBy(changedBy);
		request.setChangeTime(Calendar.getInstance());

		// get appropriate state
		final CRequestStatus status = this.stateDao.findById(request.getStatus().approve());
		request.setStatus(status);

		// check lock condition
		this.checkLockRecordsConditions(request);

		if (IRequestStates.ID_REJECTED.intValue() == oldRequestStausId) {
			// ak bola zamietnuta musime odratat zostavajuci pocet dni dovolenky ako keby sa
			// vytvorila nova
			// check remaining days and update remaining days in user
			this.updateRemainingDaysAfterAdd(request);
		}

		this.requestDao.saveOrUpdate(request);

		// prepare notification data
		// pre approve neusim poslat mail notified preto je to true
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(request.getOwner(), true);
		Map<String, Object> emailModel;

		// send notification
		for (CEmailWithLanguage email : emails) {
			emailModel = this.getNotificationEmailModel(oldRequestStausId, request, OPERATION_APPROVE, email.getLanguage());

			if (request.getNumberWorkDays() >= 1) {
				if (email.getEmail().equals(request.getOwner().getEmail())) {
					String subject = (String) emailModel.get(INotificationValue.REQUEST_TYPE);
					this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, createAttachmentWithOutOfOffice(subject, request), subject + ".ics");
				} else {
					String subject = request.getOwner().getName() + " " + request.getOwner().getSurname() + " - " + emailModel.get(INotificationValue.REQUEST_TYPE);
					this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, createAttachmentStatusFree(subject, request, getReplacement(email.getLanguage(), request)),
							subject + ".ics");
				}
			} else {
				this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
			}
		}
	}

	private String createAttachmentWithOutOfOffice(String summary, CRequest request) {

		String location = request.getPlace() == null ? "" : request.getPlace();

		Calendar dateTo = (Calendar) request.getDateTo().clone();
		dateTo.add(Calendar.DATE, 1);

		return "BEGIN:VCALENDAR\n" + "VERSION:2.0\n" + "BEGIN:VEVENT\n" + "DTSTART;VALUE=DATE:" + CDateUtils.convertToDateStringYYYYMMDD(request.getDateFrom()) + "\n" + "DTEND;VALUE=DATE:"
				+ CDateUtils.convertToDateStringYYYYMMDD(dateTo) + "\n" + "SUMMARY:" + summary + "\n" + "LOCATION:" + location + "\n" + "DESCRIPTION:\n" + "PRIORITY:3\n"
				+ "X-MICROSOFT-CDO-BUSYSTATUS:OOF\n" + "END:VEVENT\n" + "END:VCALENDAR";
	}

	private String createAttachmentStatusFree(String summary, CRequest request, String description) {

		String location = request.getPlace() == null ? "" : request.getPlace();

		Calendar dateTo = (Calendar) request.getDateTo().clone();
		dateTo.add(Calendar.DATE, 1);

		return "BEGIN:VCALENDAR\n" + "VERSION:2.0\n" + "BEGIN:VEVENT\n" + "DTSTART;VALUE=DATE:" + CDateUtils.convertToDateStringYYYYMMDD(request.getDateFrom()) + "\n" + "DTEND;VALUE=DATE:"
				+ CDateUtils.convertToDateStringYYYYMMDD(dateTo) + "\n" + "SUMMARY:" + summary + "\n" + "LOCATION:" + location + "\n" + "DESCRIPTION: " + description + "\n" + "PRIORITY:3\n"
				+ "X-MICROSOFT-CDO-BUSYSTATUS:FREE\n" + "END:VEVENT\n" + "END:VCALENDAR";
	}

	/**
	 * @see IRequestService#cancel(Long)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public void cancel(final Long requestId) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		final CRequest request = this.requestDao.findById(requestId);

		int oldRequestStatusId = request.getStatus().getId().intValue();

		request.setChangedBy(changedBy);
		request.setChangeTime(Calendar.getInstance());

		// get appropriate state
		final CRequestStatus status = this.stateDao.findById(request.getStatus().cancel());
		request.setStatus(status);

		// don't check lock condition!

		this.requestDao.saveOrUpdate(request);

		if (IRequestStates.ID_REJECTED.intValue() != oldRequestStatusId) {
			// ak nebola zamietnuta upravime pocet zostavajucich dni dovolenky
			this.updateRemainingDaysAfterCancel(request);
		}

		Map<String, Object> emailModel;
		// pre cancel neusim poslat mail notified preto je to true
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(request.getOwner(), true);

		// send notification
		for (CEmailWithLanguage email : emails) {
			// prepare notification data
			emailModel = this.getNotificationEmailModel(oldRequestStatusId, request, OPERATION_MODIFY_CANCEL, email.getLanguage());
			this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
		}
	}

	/**
	 * Retuirns detail of the request
	 */
	@Override
	@Transactional(readOnly = true)
	public CRequestRecord getDetail(final Long requestId) throws CBusinessException {
		final CRequest request = this.requestDao.findById(requestId);

		return request.convert();
	}

	/**
	 * @see IRequestService#modify(Long, CRequestRecord, boolean)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public void modify(final Long id, final CRequestRecord model, final boolean ignoreDuplicity) throws CBusinessException {
		final CUser owner = this.userDao.findById(model.getOwnerId());

		final CRequest newRequest = this.requestDao.findById(id);

		final Long type = model.getTypeId();

		// ak je typ pôvodnej žiadosti dovolenka tak chyba
		if (newRequest.getType().getId().equals(Long.valueOf(1))) {
			throw new CBusinessException(CClientExceptionsMessages.HOLIDAY_REQUEST_CANOT_CHANGE);
		}

		// SED-841 - overím, či existuje žiadosť na Dovolenku, Práceneschopnosť,
		// Náhradné voľno alebo Prekážky v práci
		final List<CRequest> crApRequests = this.requestDao.findAllApprovedAndCreatedHolidayInTimeInterval(model.getDateFrom(), model.getDateTo(), owner);
		if (!crApRequests.isEmpty()) {
			// SED-841 - ak je typ žiadosti Dovolenka, Práceneschopnosť,
			// Náhradné voľno alebo Prekážky v práci, nedovolím vytvoriť ďalšiu
			if (type == IRequestTypes.ID_H || type == IRequestTypes.ID_SD || type == IRequestTypes.ID_FP || type == IRequestTypes.ID_WB) {
				throw new CBusinessException(CClientExceptionsMessages.REQUEST_ALREADY_EXISTS);
			}
		}

		// checks
		if (!ignoreDuplicity) {

			final List<CRequest> requests = this.requestDao.findAllNotCancelledInTimeInterval(model.getDateFrom(), model.getDateTo(), owner);
			for (final CRequest cRequest : requests) {
				if (!cRequest.getId().equals(id)) {
					throw new CBusinessException(CClientExceptionsMessages.REQUEST_DUPLICITY);
				}
			}
		}

		// logic
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		int oldRequestStatusId = newRequest.getStatus().getId().intValue();
		final Float oldWorkDays = newRequest.getNumberWorkDays();
		final Calendar oldDateFrom = (Calendar) newRequest.getDateFrom().clone();
		final Calendar oldDateTo = (Calendar) newRequest.getDateTo().clone();

		// ---
		if (userHasRightToSelfApproveRequest(owner)) {
			boolean existsApprovedRequest = false;
			// do check - for all approved requests
			final List<CRequest> requests = this.requestDao.findAllApprovedInTimeInterval(model.getDateFrom(), model.getDateTo(), owner);
			if (!requests.isEmpty()) {
				existsApprovedRequest = true;
			}
			if (!existsApprovedRequest) {
				// doesn't exists approved request - approve this one
				final CRequestStatus status = this.stateDao.findById(newRequest.getStatus().approve());
				newRequest.setStatus(status);
			}
		} else {
			newRequest.getStatus().modify();
		}

		newRequest.setChangedBy(changedBy);
		newRequest.setChangeTime(Calendar.getInstance());

		final Calendar createDate = Calendar.getInstance();
		createDate.setTime(model.getCreateDate());
		newRequest.setCreateDate(createDate);

		newRequest.setOwner(owner);
		newRequest.setNote(model.getNote());
		newRequest.setPlace(model.getPlace());
		newRequest.setNumberWorkDays(model.getWorkDays());

		final Calendar from = Calendar.getInstance();
		from.setTime(model.getDateFrom());
		CTimeUtils.convertToStartDate(from); // SED-761
		newRequest.setDateFrom(from);

		// full day mode
		if (model.isFullday()) {
			final Calendar to = Calendar.getInstance();
			to.setTime(model.getDateTo());
			CTimeUtils.convertToStartDate(to); // SED-761
			newRequest.setDateTo(to);

			newRequest.setHoursDateFrom(null);
			newRequest.setHoursDateTo(null);
		}
		// day part mode
		else {
			newRequest.setDateTo(from);
			newRequest.setHoursDateFrom((float) model.getHours());
			newRequest.setHoursDateTo(null);
		}

		if (model.getResponsalisName() != null) {
			newRequest.setResponsalisName(model.getResponsalisName());
		}

		if (model.getRequestReasonId() != null) {
			CRequestReason reason = this.requestReasonDao.findById(model.getRequestReasonId());
			newRequest.setReason(reason);
		}

		// check lock condition
		this.checkLockRecordsConditions(newRequest);

		// check request year
		this.checkRequestYear(newRequest);

		// check remaining days and update remaining days in user
		this.updateRemainingDaysAfterModify(newRequest, oldWorkDays, oldDateFrom, oldDateTo);

		// after every request modification we generate new code
		newRequest.setCode(this.appCodeGenerator.generateMessage(6, 3));

		this.requestDao.saveOrUpdate(newRequest);

		Map<String, Object> emailModel;
		// pre modify neusim poslat mail notified preto je to false
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(owner, false);
		final List<String> subordinatesWithApprovedRequest = this.getSubordinatesWithApprovedRequest(owner, model.getDateFrom(), model.getDateTo());

		// SED 364
		for (CEmailWithLanguage s : emails) {
			if (owner.getEmail().equals(s.getEmail())) {
				// prepare notification data
				emailModel = this.getNotificationEmailModel(oldRequestStatusId, newRequest, OPERATION_MODIFY_CANCEL, s.getLanguage());
				// ownerovi ziadosti poslem normalny email
				this.notificationDao.sendNotificationAboutUserRequest(s, emailModel, null, null);
			} else {
				// prepare notification data
				emailModel = this.getNotificationEmailModel(oldRequestStatusId, newRequest, OPERATION_MODIFY_CANCEL, s.getLanguage());
				// nadriadenemu poslem email s linkom na schvalenie alebo
				// zamietnutie ziadosti
				// send notification
				this.notificationDao.sendNotificationAboutUserRequestWithLinks(s, emailModel, newRequest.getId().toString(), newRequest.getCode(), subordinatesWithApprovedRequest);
			}
		}
	}

	/**
	 * @see IRequestService#reject(Long)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public void reject(final Long requestId) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser changedBy = this.userDao.findById(loggedUser.getUserId());

		final CRequest request = this.requestDao.findById(requestId);
		// check self rejecting
		if (request.getOwner().getId().equals(loggedUser.getUserId())) {
			// don't allow to create a new approved request
			throw new CBusinessException(CClientExceptionsMessages.REQUEST_APPROVING_BY_SUPERIOR_ONLY);
		}

		int oldRequestStatusId = request.getStatus().getId().intValue();

		request.setChangedBy(changedBy);
		request.setChangeTime(Calendar.getInstance());

		// get appropriate state
		final CRequestStatus status = this.stateDao.findById(request.getStatus().reject());
		request.setStatus(status);

		// don't check lock condition!

		this.requestDao.saveOrUpdate(request);

		// po uspesnom ulozeni ziadosti upravime pocet zostavajucich dni dovolenky
		this.updateRemainingDaysAfterCancel(request);

		Map<String, Object> emailModel;
		// pri reject musim poslat mail notified, preto je to true
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(request.getOwner(), true);

		// send notification
		for (CEmailWithLanguage email : emails) {
			// prepare notification data
			emailModel = this.getNotificationEmailModel(oldRequestStatusId, request, OPERATION_REJECT, email.getLanguage());
			this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
		}
	}

	/**
	 * Checks if employee request is newer when compared with locked time. Throws
	 * exception if not.
	 * 
	 * @param record input checked record
	 * @throws CBusinessException when employee records are locked for the required
	 *                            time
	 */
	private void checkLockRecordsConditions(final CRequest record) throws CBusinessException {
		if (record.getOwner() != null) {
			List<CLockDate> listEmployeeLockParams = this.lockDateDao.findByUser(record.getOwner().getId());
			if (listEmployeeLockParams != null && !listEmployeeLockParams.isEmpty()) {
				CLockDate elp = listEmployeeLockParams.get(0);
				if (!elp.getRequestLockTo().before(record.getDateFrom())) {
					throw new CBusinessException(CClientExceptionsMessages.REQUESTS_EMPLOYEES_RECORDS_ARE_LOCKED);
				}
			}
		}
	}

	/**
	 * Sets parameter values for the notification message.
	 * 
	 * @param requestClientData data from client
	 * @param requestServerData data of entity created
	 * @param operation         type of the request operation
	 * @return map of the parameter values
	 */
	private Map<String, Object> getNotificationEmailModel(int oldStatusId, CRequest requestServerData, int operation, String language) {

		Locale locale = CLocaleUtils.getLocale(language);
		final Map<String, Object> emailModel = new HashMap<>();

		CUser owner = this.userDao.findById(requestServerData.getOwner().getId());

		// request operation
		switch (operation) {
		case 0: // add
			emailModel.put(INotificationValue.REQUEST_TITLE, this.messages.getMessage("messages.user_request_operation.add", null, locale));
			break;
		case 1: // modify, cancel
			emailModel.put(INotificationValue.REQUEST_TITLE, this.messages.getMessage("messages.user_request_operation.modify_cancel", null, locale));
			break;
		case 2: // approve
			emailModel.put(INotificationValue.REQUEST_TITLE, this.messages.getMessage("messages.user_request_operation.approve", null, locale));
			break;
		case 3: // reject
			emailModel.put(INotificationValue.REQUEST_TITLE, this.messages.getMessage("messages.user_request_operation.reject", null, locale));
			break;
		default:
			emailModel.put(INotificationValue.REQUEST_TITLE, this.messages.getMessage("messages.user_request_operation.unknown", null, locale));
			break;
		}

		// request type
		emailModel.put(INotificationValue.REQUEST_TYPE_ID, requestServerData.getType().getId().intValue());
		switch (requestServerData.getType().getId().intValue()) {
		case IRequestTypeConstant.RQTYPE_VACATION_ID: // dovolenka
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.holiday", null, locale));
			emailModel.put(INotificationValue.HALF_DAY_REQUEST, (requestServerData.getNumberWorkDays() == null ? false : requestServerData.getNumberWorkDays().compareTo(1.0f) < 0 ));
			emailModel.put(INotificationValue.NOTE, requestServerData.getNote() == null ? "" : requestServerData.getNote());
			break;
		case IRequestTypeConstant.RQTYPE_SICKNESS_ID: // navsteva lekara
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.sickness", null, locale));
			emailModel.put(INotificationValue.REASON, requestServerData.getReason() == null ? "" : requestServerData.getReason().getReasonName());
			break;
		case IRequestTypeConstant.RQTYPE_REPLWORK_ID: // nahradne volno
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.replwork", null, locale));
			break;
		case IRequestTypeConstant.RQTYPE_BUSTRIP_ID: // pracovna cesta
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.bustrip", null, locale));
			break;
		case IRequestTypeConstant.RQTYPE_WORKBREAK_ID: // prekazky v praci
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.workbreak", null, locale));
			emailModel.put(INotificationValue.REASON, requestServerData.getReason() == null ? "" : requestServerData.getReason().getReasonName());
			break;
		case IRequestTypeConstant.RQTYPE_STAFF_TRAINING_ID: // skolenie
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.staff_training", null, locale));
			emailModel.put(INotificationValue.NOTE, requestServerData.getNote() == null ? "" : requestServerData.getNote());
			break;
		case IRequestTypeConstant.RQTYPE_WORK_AT_HOME_ID: // praca z domu
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.work_at_home", null, locale));

			emailModel.put(INotificationValue.REASON, requestServerData.getReason() == null ? "" : requestServerData.getReason().getReasonName());
			emailModel.put(INotificationValue.NOTE, requestServerData.getNote() == null ? "" : requestServerData.getNote());

			break;
		case IRequestTypeConstant.RQTYPE_OUT_OF_OFFICE_ID: // praca mimo
			// pracoviska
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("label.out_of_office", null, locale));
			break;

		default: // unknown
			emailModel.put(INotificationValue.REQUEST_TYPE, this.messages.getMessage("messages.user_request_type.unknown", null, locale));
			break;
		}

		emailModel.put(INotificationValue.NAME_SURNAME, owner.getName() + " " + owner.getSurname());

		emailModel.put(INotificationValue.DATE_FROM, CDateUtils.convertToDateString(requestServerData.getDateFrom()));

		emailModel.put(INotificationValue.DATE_TO, CDateUtils.convertToDateString(requestServerData.getDateTo()));

		emailModel.put(INotificationValue.WORK_DAYS_NUMBER, "" + (requestServerData.getNumberWorkDays() == null ? "" : requestServerData.getNumberWorkDays().floatValue()));

		emailModel.put(INotificationValue.PLACE, requestServerData.getPlace() == null ? "" : requestServerData.getPlace());

		emailModel.put(INotificationValue.RESPONSALIS_NAME, requestServerData.getResponsalisName() == null ? "" : requestServerData.getResponsalisName());

		// old request status
		switch (oldStatusId) {
		case IUserRequestStatus.RQSTATE_CREATED_ID:
			emailModel.put(INotificationValue.REQUEST_STATUS_OLD, messages.getMessage("messages.notification_state.created", null, locale));
			break;
		case IUserRequestStatus.RQSTATE_CANCELLED_ID:
			emailModel.put(INotificationValue.REQUEST_STATUS_OLD, messages.getMessage("messages.notification_state.cancelled", null, locale));
			break;
		case IUserRequestStatus.RQSTATE_APPROVED_ID:
			emailModel.put(INotificationValue.REQUEST_STATUS_OLD, messages.getMessage("messages.notification_state.approved", null, locale));
			break;
		case IUserRequestStatus.RQSTATE_DECLINED_ID:
			emailModel.put(INotificationValue.REQUEST_STATUS_OLD, messages.getMessage("messages.notification_state.declined", null, locale));
			break;
		default:
			emailModel.put(INotificationValue.REQUEST_STATUS_OLD, messages.getMessage("messages.notification_state.unknown", null, locale));
			break;
		}

		// current request status
		if (requestServerData.getStatus().getId() == null) {
			emailModel.put(INotificationValue.REQUEST_STATUS_CURRENT, messages.getMessage("messages.notification_state.unknown", null, locale));
		} else {
			switch (requestServerData.getStatus().getId().intValue()) {
			case IUserRequestStatus.RQSTATE_CREATED_ID:
				emailModel.put(INotificationValue.REQUEST_STATUS_CURRENT, messages.getMessage("messages.notification_state.created", null, locale));
				break;
			case IUserRequestStatus.RQSTATE_CANCELLED_ID:
				emailModel.put(INotificationValue.REQUEST_STATUS_CURRENT, messages.getMessage("messages.notification_state.cancelled", null, locale));
				break;
			case IUserRequestStatus.RQSTATE_APPROVED_ID:
				emailModel.put(INotificationValue.REQUEST_STATUS_CURRENT, messages.getMessage("messages.notification_state.approved", null, locale));
				break;
			case IUserRequestStatus.RQSTATE_DECLINED_ID:
				emailModel.put(INotificationValue.REQUEST_STATUS_CURRENT, messages.getMessage("messages.notification_state.declined", null, locale));
				break;
			default:
				emailModel.put(INotificationValue.REQUEST_STATUS_CURRENT, messages.getMessage("messages.notification_state.unknown", null, locale));
				break;
			}
		}

		return emailModel;
	}

	/**
	 * Sets list of target email addresses
	 * 
	 * @param owner user that "sends" notification
	 * @return list of email addresses
	 * @throws CBusinessException in error case
	 */
	private List<CEmailWithLanguage> getNotificationEmails(CUser owner, CUser parentUser, boolean notifyOtherUsers) throws CBusinessException {
		List<CEmailWithLanguage> emails = new ArrayList<>();

		emails.add(new CEmailWithLanguage(owner.getEmail(), owner.getLanguage()));

		if (parentUser != null) {
			emails.add(new CEmailWithLanguage(parentUser.getEmail(), parentUser.getLanguage()));
		}
		if (notifyOtherUsers) {
			List<CUser> list = this.userDao.findEmloyeesToNotified(owner);
			for (CUser u : list) {
				if (!(owner.getId().equals(u.getId())) && (parentUser != null ? !(parentUser.getId().equals(u.getId())) : true)) {
					emails.add(new CEmailWithLanguage(u.getEmail(), u.getLanguage()));
				}
			}
		}

		return emails;
	}

	private List<CEmailWithLanguage> getNotificationEmails(CUser owner, boolean notifyOtherUsers) throws CBusinessException {
		return getNotificationEmails(owner, getParentUser(owner), notifyOtherUsers);
	}

	private CUser getParentUser(CUser owner) throws CBusinessException {
		CUser parentUser = null;

		COrganizationTree parentOrgTree = this.organizationTreeDao.findParentTree(owner.getId());
		if (parentOrgTree != null) {
			if (parentOrgTree.getSuperior() != null) {
				parentUser = parentOrgTree.getSuperior().getOwner();
			}
		}

		return parentUser;
	}

	private Boolean userHasRightToSelfApproveRequest(CUser owner) throws CBusinessException {
		COrganizationTree parentOrgTree = this.organizationTreeDao.findParentTree(owner.getId());
		if (parentOrgTree != null) {
			if (parentOrgTree.getSuperior() != null) {
				CUser parentUser = parentOrgTree.getSuperior().getOwner();
				if (parentUser != null) {
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * @see IRequestService#approveRequestFromEmail(String, String)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public Boolean approveRequestFromEmail(final String requestId, final String requestCode) {

		final CRequest request = this.requestDao.findById(Long.valueOf(requestId));
		// 4 = status zamietnutá, 1 = typ dovolenka
		if (request.getStatus().getId().equals(Long.valueOf(IRequestStates.ID_REJECTED)) && request.getType().getId().equals(Long.valueOf(1))) {
			return false;
		}

		try {
			if (checkRequestCode(Long.valueOf(requestId), requestCode)) {
				this.approveFromEmailLink(Long.valueOf(requestId));
				return true;
			}
		} catch (CBusinessException e) {
			Logger.getLogger(this.getClass()).info(e);
		}

		return false;
	}

	private Boolean checkRequestCode(final Long requestId, final String requestCode) {
		final CRequest request = this.requestDao.findById(requestId);

		if (requestCode.equals(request.getCode())) {
			return true;
		}

		return false;
	}

	/**
	 * Approves request
	 * 
	 * @param requestId
	 * @throws CBusinessException
	 */
	private void approveFromEmailLink(final Long requestId) throws CBusinessException {
		final CRequest request = this.requestDao.findById(requestId);
		CUser parentUser = getParentUser(request.getOwner());

		// SED-841 - overím, či existuje žiadosť na Dovolenku, Práceneschopnosť, Náhradné voľno alebo Prekážky v práci
		final List<CRequest> requests = this.requestDao.findAllApprovedHolidayInTimeInterval(request.getDateFrom().getTime(), request.getDateTo().getTime(), request.getOwner());
		if (!requests.isEmpty()) {
			// SED-841 - ak je typ žiadosti Dovolenka, Práceneschopnosť, Náhradné voľno alebo Prekážky v práci, nedovolím vytvoriť ďalšiu
			if (request.getType().getId() == IRequestTypes.ID_H || request.getType().getId() == IRequestTypes.ID_SD || request.getType().getId() == IRequestTypes.ID_FP
					|| request.getType().getId() == IRequestTypes.ID_WB) {
			throw new CBusinessException(CClientExceptionsMessages.REQUEST_EXIST_APPROVED_IN_TIME);
			}
		}

		int oldRequestStausId = request.getStatus().getId().intValue();

		request.setChangeTime(Calendar.getInstance());
		request.setChangedBy(parentUser);

		// get appropriate state
		final CRequestStatus status = this.stateDao.findById(request.getStatus().approve());
		request.setStatus(status);

		// check lock condition
		this.checkLockRecordsConditions(request);

		if (IRequestStates.ID_REJECTED.intValue() == oldRequestStausId) {
			// ak bola zamietnuta musime odratat zostavajuci pocet dni dovolenky ako keby sa
			// vytvorila nova
			// check remaining days and update remaining days in user
			this.updateRemainingDaysAfterAdd(request);
		}

		this.requestDao.saveOrUpdate(request);

		Map<String, Object> emailModel;

		// pre schvalenie cez mail neusim poslat mail notified preto je to true
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(request.getOwner(), parentUser, true);

		for (CEmailWithLanguage email : emails) {
			// prepare notification data
			emailModel = this.getNotificationEmailModel(oldRequestStausId, request, OPERATION_APPROVE, email.getLanguage());

			// send notification
			if (request.getNumberWorkDays() >= 1) {
				if (email.getEmail().equals(request.getOwner().getEmail())) {
					this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, createAttachmentWithOutOfOffice((String) emailModel.get(INotificationValue.REQUEST_TYPE), request),
							emailModel.get(INotificationValue.REQUEST_TYPE) + ".ics");
				} else {
					String subject = request.getOwner().getName() + " " + request.getOwner().getSurname() + " - " + emailModel.get(INotificationValue.REQUEST_TYPE);
					this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, createAttachmentStatusFree(subject, request, getReplacement(email.getLanguage(), request)),
							subject + ".ics");
				}
			} else {
				this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
			}
		}
	}

	/**
	 * @see IRequestService#rejectRequestFromEmail(String, String)
	 */
	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public Boolean rejectRequestFromEmail(final String requestId, final String requestCode) {
		try {
			if (checkRequestCode(Long.valueOf(requestId), requestCode)) {
				this.rejectFromEmialLink(Long.valueOf(requestId));
				return true;
			}
		} catch (CBusinessException e) {
			Logger.getLogger(this.getClass()).info(e);
		}

		return false;
	}

	/**
	 * Rejects request
	 * 
	 * @param requestId
	 * @throws CBusinessException
	 */
	private void rejectFromEmialLink(final Long requestId) throws CBusinessException {
		final CRequest request = this.requestDao.findById(requestId);
		CUser parentUser = getParentUser(request.getOwner());

		int oldRequestStatusId = request.getStatus().getId().intValue();

		request.setChangeTime(Calendar.getInstance());
		request.setChangedBy(parentUser);

		// get appropriate state
		final CRequestStatus status = this.stateDao.findById(request.getStatus().reject());
		request.setStatus(status);

		// don't check lock condition!

		this.requestDao.saveOrUpdate(request);

		// po uspesnom ulozeni ziadosti upravime pocet zostavajucich dni
		// dovolenky
		this.updateRemainingDaysAfterCancel(request);

		Map<String, Object> emailModel;
		// pre reject z emailu neusim poslat mail notified preto je to true
		final List<CEmailWithLanguage> emails = this.getNotificationEmails(request.getOwner(), parentUser, true);

		for (CEmailWithLanguage email : emails) {
			// prepare notification data
			emailModel = this.getNotificationEmailModel(oldRequestStatusId, request, OPERATION_REJECT, email.getLanguage());
			// send notification
			this.notificationDao.sendNotificationAboutUserRequest(email, emailModel, null, null);
		}
	}

	/**
	 * @param owner
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 * @throws CBusinessException
	 */
	private List<String> getSubordinatesWithApprovedRequest(final CUser owner, final Date dateFrom, final Date dateTo) throws CBusinessException {
		List<String> result = new ArrayList<>();

		COrganizationTree parentOrgTree = this.organizationTreeDao.findParentTree(owner.getId());
		if (parentOrgTree != null) {
			// najskor skontrolujem podriadenych ziadatela
			List<COrganizationTree> subordinates = parentOrgTree.getOrganizationTreesSubordinate();

			for (COrganizationTree subordinate : subordinates) {
				CUser user = subordinate.getOwner();
				for (CRequest request : requestDao.findAllApprovedInTimeInterval(dateFrom, dateTo, user)) {
					result.add("<tr><td>" + user.getName() + " " + user.getSurname() + "</td><td>&nbsp; &nbsp; &nbsp; &nbsp;</td><td>" + CDateUtils.convertToDateString(request.getDateFrom()) + " - "
							+ CDateUtils.convertToDateString(request.getDateTo()) + "</td></tr>");
				}
			}

			// potom skontrolujem zamestnancov na rovnakej urovni ako ziadatel,
			// teda najskor najdem jeho nadriadeneho a potom najdem nadriadeneho
			// podriadenych
			if (parentOrgTree.getSuperior() != null) {
				parentOrgTree = this.organizationTreeDao.findParentTree(parentOrgTree.getSuperior().getOwner().getId());

				if (parentOrgTree != null) {
					List<COrganizationTree> subordinates2 = parentOrgTree.getOrganizationTreesSubordinate();

					for (COrganizationTree subordinate : subordinates2) {
						CUser user2 = subordinate.getOwner();

						for (CRequest request : requestDao.findAllApprovedInTimeInterval(dateFrom, dateTo, user2)) {
							result.add("<tr><td>" + user2.getName() + " " + user2.getSurname() + "</td><td>&nbsp; &nbsp; &nbsp; &nbsp;</td><td>" + CDateUtils.convertToDateString(request.getDateFrom())
									+ " - " + CDateUtils.convertToDateString(request.getDateTo()) + "</td></tr>");
						}
					}
				}
			}
		}

		return result;
	}

	private String getReplacement(String language, CRequest request) {
		Locale locale = CLocaleUtils.getLocale(language);
		return request.getResponsalisName() == null ? "" : messages.getMessage("label.replacement", null, locale) + " " + request.getResponsalisName();
	}

	/**
	 * po vytvorení novej žiadosti s typom "Dovolenka" odpočítať z t_user.c_vacation
	 * počet pracovných dní, na ktoré bola dovolenka zadaná dovolenka zadaná na
	 * budúci rok odpočítala/pripočítala zo stĺpca dostupných dní dovolenky na
	 * budúci rok (t_user.c_vacation_next_year) a nie na aktuálny.
	 * 
	 * ak rozdiel medzi t_user.c_vacation a workDays bude menší ako 0, nedovoliť
	 * Pridať/Editovať žiadosť s chybovou hláškou „Nie je možné zadať žiadosť typu
	 * dovolenka na viac dní, ako je zostávajúci počet dní dovolenky.“,
	 * 
	 * @param request to create
	 * @throws CBusinessException
	 */
	private void updateRemainingDaysAfterAdd(final CRequest request) throws CBusinessException {
		Calendar dateFrom = (Calendar) request.getDateFrom().clone();
		Calendar dateTo = (Calendar) request.getDateTo().clone();

		final CUser changedBy = this.userDao.findById(0L);

		final CUser owner = request.getOwner();
		Double remainingDays = owner.getVacation();
		Double remainingDaysNextYear = owner.getVacationNextYear();

		// SED-787 - D - zistím si počet časových značiek dovolenky v intervale a spočítam ich, súčet odpočítam od počtu dni žiadosti
		List<CViewTimeStamp> timestamps = timeStampDao.getHolidayRecordsFromInterval(owner, dateFrom, dateTo);
		Double holidayRecordsInInterval = 0.0;

		if (timestamps != null) {
			for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
				CViewTimeStamp cViewTimeStamp = iterator.next();
				long hours4duration = 240;

				if (cViewTimeStamp.getDuration() == hours4duration) {
					holidayRecordsInInterval += 0.5;
				} else {
					holidayRecordsInInterval += 1.0;
				}
			}
		}

		if (remainingDays != null && IRequestTypeConstant.RQTYPE_VACATION_CODE.equalsIgnoreCase(request.getType().getCode())) {
			if (isRequestOnlyInNextYear(dateFrom, dateTo)) {
				if (remainingDaysNextYear != null) {

					Double newRemainingDays = remainingDaysNextYear - (request.getNumberWorkDays() - holidayRecordsInInterval); // SED-787 D
					if (newRemainingDays < 0) {
						throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ENOUGH_REMAINING_DAYS_NEXT_YEAR);
					}

					owner.setVacationNextYear(newRemainingDays, changedBy);
				}
			} else if (isRequestInThisAndNextYear(dateFrom, dateTo)) {
				int actualYear = Calendar.getInstance().get(Calendar.YEAR);

				Calendar calendarStart = (Calendar) request.getDateFrom().clone();
				calendarStart.set(Calendar.YEAR, actualYear + 1);
				calendarStart.set(Calendar.MONTH, 0);
				calendarStart.set(Calendar.DAY_OF_MONTH, 1);

				Calendar calendarEnd = (Calendar) request.getDateFrom().clone();
				calendarEnd.set(Calendar.YEAR, actualYear);
				calendarEnd.set(Calendar.MONTH, 11);
				calendarEnd.set(Calendar.DAY_OF_MONTH, 31);

				List<CViewTimeStamp> timestampsThisYear = timeStampDao.getHolidayRecordsFromInterval(owner, dateFrom, calendarEnd); // časové značky dovolenka tento rok
				List<CViewTimeStamp> timestampsNextYear = timeStampDao.getHolidayRecordsFromInterval(owner, calendarStart, dateTo);// časové značky dovolenka budúci
				// rok

				Double holidayRecordsInThisYear = 0.0;

				if (timestampsThisYear != null) {
					for (Iterator<CViewTimeStamp> iterator = timestampsThisYear.iterator(); iterator.hasNext();) {
						CViewTimeStamp cViewTimeStamp = iterator.next();
						long hours4duration = 240;

						if (cViewTimeStamp.getDuration() == hours4duration) {
							holidayRecordsInThisYear += 0.5;
						} else {
							holidayRecordsInThisYear += 1.0;
						}
					}
				}

				Double holidayRecordsInNextYear = 0.0;

				if (timestampsNextYear != null) {
					for (Iterator<CViewTimeStamp> iterator = timestampsNextYear.iterator(); iterator.hasNext();) {
						CViewTimeStamp cViewTimeStamp = iterator.next();
						long hours4duration = 240;

						if (cViewTimeStamp.getDuration() == hours4duration) {
							holidayRecordsInNextYear += 0.5;
						} else {
							holidayRecordsInNextYear += 1.0;
						}
					}
				}

				int daysActualYear = getNumberOfDays(request.getClient().getId(), actualYear, dateFrom, calendarEnd); // počet pracovných dní tento rok
				int daysNextYear = getNumberOfDays(request.getClient().getId(), actualYear + 1, calendarStart, dateTo); // počet pracovných dní nasledujúci rok

				Double newRemainingDays = remainingDays - (daysActualYear - holidayRecordsInThisYear); // SED-787 D
				if (newRemainingDays < 0) {
					throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ENOUGH_REMAINING_DAYS);
				}
				owner.setVacation(newRemainingDays, changedBy);

				if (remainingDaysNextYear != null) {
					Double newRemainingDaysNextYear = remainingDaysNextYear - (daysNextYear - holidayRecordsInNextYear);
					if (newRemainingDaysNextYear < 0) {
						throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ENOUGH_REMAINING_DAYS_NEXT_YEAR);
					}

					owner.setVacationNextYear(newRemainingDaysNextYear, changedBy);
				}

			} else {

				Double newRemainingDays = remainingDays - (request.getNumberWorkDays() - holidayRecordsInInterval); // SED-787 D
				if (newRemainingDays < 0) {
					throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ENOUGH_REMAINING_DAYS);
				}

				owner.setVacation(newRemainingDays, changedBy);
			}
			userDao.saveOrUpdate(owner);
		}
	}

	/**
	 * po zmene žiadosti s typom „Dovolenka“ pripočítať " pripočítať v
	 * t_user.c_vacation počet pracovných dní, na ktoré bola dovolenka povodne
	 * zadaná a odpočítať počet pracovných dní, na ktoré bola žiadosť na dovolenku
	 * zmenená, dovolenka zadaná na budúci rok odpočítala/pripočítala zo stĺpca
	 * dostupných dní dovolenky na budúci rok (t_user.c_vacation_next_year) a nie na
	 * aktuálny.
	 * 
	 * ak rozdiel medzi t_user.c_vacation a workDays bude menší ako 0, nedovoliť
	 * Pridať/Editovať žiadosť s chybovou hláškou „Nie je možné zadať žiadosť typu
	 * dovolenka na viac dní, ako je zostávajúci počet dní dovolenky.“,
	 * 
	 * @param request to update
	 * @throws CBusinessException
	 */
	private void updateRemainingDaysAfterModify(final CRequest request, final Float oldWorkDays, final Calendar oldDateFrom, final Calendar oldDateTo) throws CBusinessException {
		final CUser owner = request.getOwner();
		Double remainingDays = owner.getVacation();
		Double remainingDaysNextYear = owner.getVacationNextYear();
		final CUser changedBy = this.userDao.findById(0L);

		if (remainingDays != null && IRequestTypeConstant.RQTYPE_VACATION_CODE.equalsIgnoreCase(request.getType().getCode())) {

			float daysActualYear = 0;
			float daysNextYear = 0;
			float oldDaysActualYear = 0;
			float oldDaysNextYear = 0;

			if (isRequestOnlyInNextYear(request.getDateFrom(), request.getDateTo())) {
				daysNextYear = request.getNumberWorkDays();
			} else if (isRequestInThisAndNextYear(request.getDateFrom(), request.getDateTo())) {
				int actualYear = Calendar.getInstance().get(Calendar.YEAR);

				Calendar calendarStart = (Calendar) request.getDateFrom().clone();
				calendarStart.set(Calendar.YEAR, actualYear + 1);
				calendarStart.set(Calendar.MONTH, 0);
				calendarStart.set(Calendar.DAY_OF_MONTH, 1);

				Calendar calendarEnd = (Calendar) request.getDateFrom().clone();
				calendarEnd.set(Calendar.YEAR, actualYear);
				calendarEnd.set(Calendar.MONTH, 11);
				calendarEnd.set(Calendar.DAY_OF_MONTH, 31);

				daysActualYear = getNumberOfDays(request.getClient().getId(), actualYear, request.getDateFrom(), calendarEnd);
				daysNextYear = getNumberOfDays(request.getClient().getId(), actualYear + 1, calendarStart, request.getDateTo());
			} else {
				daysActualYear = request.getNumberWorkDays();
			}

			if (isRequestOnlyInNextYear(oldDateFrom, oldDateTo)) {
				oldDaysNextYear = oldWorkDays;
			} else if (isRequestInThisAndNextYear(oldDateFrom, oldDateTo)) {
				int actualYear = oldDateFrom.get(Calendar.YEAR);

				Calendar calendarStart = (Calendar) oldDateFrom.clone();
				calendarStart.set(Calendar.YEAR, actualYear + 1);
				calendarStart.set(Calendar.MONTH, 0);
				calendarStart.set(Calendar.DAY_OF_MONTH, 1);

				Calendar calendarEnd = (Calendar) oldDateFrom.clone();
				calendarEnd.set(Calendar.YEAR, actualYear);
				calendarEnd.set(Calendar.MONTH, 11);
				calendarEnd.set(Calendar.DAY_OF_MONTH, 31);

				oldDaysActualYear = getNumberOfDays(request.getClient().getId(), actualYear, oldDateFrom, calendarEnd);
				oldDaysNextYear = getNumberOfDays(request.getClient().getId(), actualYear + 1, calendarStart, oldDateTo);
			} else {
				oldDaysActualYear = oldWorkDays;
			}

			Double newRemainingDays = remainingDays - daysActualYear + oldDaysActualYear;
			if (newRemainingDays < 0) {
				throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ENOUGH_REMAINING_DAYS);
			}

			owner.setVacation(newRemainingDays, changedBy);

			if (remainingDaysNextYear != null) {

				Double newRemainingDaysNextYear = remainingDaysNextYear - daysNextYear + oldDaysNextYear;
				if (newRemainingDaysNextYear < 0) {
					throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ENOUGH_REMAINING_DAYS_NEXT_YEAR);
				}

				owner.setVacationNextYear(newRemainingDaysNextYear, changedBy);
			}

			userDao.saveOrUpdate(owner);
		}
	}

	/**
	 * po zamietnutí/zrušení žiadosti s typom "Dovolenka" pripočítať v
	 * t_user.c_vacation počet pracovných dní, na ktoré bola dovolenka zadaná.
	 * dovolenka zadaná na budúci rok odpočítala/pripočítala zo stĺpca dostupných
	 * dní dovolenky na budúci rok (t_user.c_vacation_next_year) a nie na aktuálny.
	 * 
	 * @param request to reject / cancel
	 * @throws CBusinessException
	 */
	private void updateRemainingDaysAfterCancel(final CRequest request) throws CBusinessException {
		final CUser owner = request.getOwner();
		Double remainingDays = owner.getVacation();
		Double remainingDaysNextYear = owner.getVacationNextYear();
		List<CHoliday> holidays = holidayDao.findAllValidClientsHolidays(owner.getClient().getId(), null);

		if (remainingDays != null && IRequestTypeConstant.RQTYPE_VACATION_CODE.equalsIgnoreCase(request.getType().getCode())) {
			if (isRequestOnlyInNextYear(request.getDateFrom(), request.getDateTo())) {
				if (remainingDaysNextYear != null) {
					this.updateVacationDaysThisORNextYear(request, owner, remainingDays, remainingDaysNextYear, holidays, Boolean.TRUE);
				}
			} else if (isRequestInThisAndNextYear(request.getDateFrom(), request.getDateTo())) {
				this.updateVacationDaysThisANDNextYear(request, owner, remainingDays, remainingDaysNextYear, holidays);
			} else {
				this.updateVacationDaysThisORNextYear(request, owner, remainingDays, remainingDaysNextYear, holidays, Boolean.FALSE);
			}
		}
		userDao.saveOrUpdate(owner);
	}

	/**
	 * Dovolenku je mozne zadat len na tento a buduci rok
	 * 
	 * @param request
	 * @throws CBusinessException
	 */
	private void checkRequestYear(final CRequest request) throws CBusinessException {
		if (IRequestTypeConstant.RQTYPE_VACATION_CODE.equalsIgnoreCase(request.getType().getCode())) {
			int actualYear = Calendar.getInstance().get(Calendar.YEAR);

			if (request.getDateTo().get(Calendar.YEAR) > actualYear + 1) {
				throw new CBusinessException(CClientExceptionsMessages.REQUESTS_NOT_ALLOWED_IN_FUTURE);
			}
		}
	}

	/**
	 * 
	 * @return true if request starts in this year and ends in next year
	 */
	private boolean isRequestInThisAndNextYear(Calendar dateFrom, Calendar dateTo) {
		int actualYear = Calendar.getInstance().get(Calendar.YEAR);
		return dateFrom.get(Calendar.YEAR) == actualYear && dateTo.get(Calendar.YEAR) == actualYear + 1;
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
	 * 
	 * @param clientId
	 * @param selectedYear
	 * @param from
	 * @param to
	 * @return
	 */
	private int getNumberOfDays(Long clientId, Integer selectedYear, Calendar from, Calendar to) {
		List<CHoliday> holidays = holidayDao.findAllValidClientsHolidays(clientId, selectedYear);
		return CDateServerUtils.getWorkingDaysCheckHolidays(from.getTime(), to.getTime(), holidays);
	}

	/**
	 * metóda na pridanie dní dovolenky pre tento alebo nasledujúci rok
	 */
	private void updateVacationDaysThisORNextYear(CRequest request, CUser owner, Double remainingDays, Double remainingDaysNextYear, List<CHoliday> holidays, Boolean isNextYear) {

		Boolean halfDayVacation = request.getNumberWorkDays().equals(Float.valueOf(0.5f)) ? Boolean.TRUE : Boolean.FALSE;
		final CUser changedBy = this.userDao.findById(0L);

		// ak je dovolenka na pol dňa a vo výkaze na tento deň nie je žiadny
		// záznam o dovolenke tak pripočítam pol dňa
		if (halfDayVacation && request.getDateLastGenHoliday() == null && !timeStampDao.existHolidayRecordForDay(request.getOwner(), request.getDateFrom(), null)) {
			if (isNextYear) {
				owner.setVacationNextYear(remainingDaysNextYear + 0.5, changedBy);
			} else {
				owner.setVacation(remainingDays + 0.5, changedBy);
			}
		} else if (!halfDayVacation) {
			if (request.getDateLastGenHoliday() == null) {
				float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(request.getDateFrom().getTime(), request.getDateTo().getTime(), holidays);
				float timeStampWorkingDays = (float) 0;

				List<CViewTimeStamp> timestamps = this.timeStampDao.getHolidayRecordsFromInterval(owner, request.getDateFrom(), request.getDateTo());

				if (timestamps != null) {
					for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
						CViewTimeStamp cViewTimeStamp = iterator.next();

						if (CDateServerUtils.isWorkingDayWithCheckHolidays(cViewTimeStamp.getTimeFrom().getTime(), holidays)) {
							long hours4duration = 240;

							if (cViewTimeStamp.getDuration() == hours4duration) {
								timeStampWorkingDays += 0.5;
							} else {
								timeStampWorkingDays += 1.0;
							}
						}
					}
				}
				if (isNextYear) {
					owner.setVacationNextYear(remainingDaysNextYear + numberOfWorkingDays - timeStampWorkingDays, changedBy);
				} else {
					owner.setVacation(remainingDays + numberOfWorkingDays - timeStampWorkingDays, changedBy);
				}
			}

			else if (request.getDateTo().getTimeInMillis() > request.getDateLastGenHoliday().getTimeInMillis()) {

				Calendar dateFrom = (Calendar) request.getDateLastGenHoliday();
				// vyhľadám záznamy až od nasledujúce dňa, pretože pri automatickom generovaní
				// sa pri nevytvorení záznamu o dovolenke už 1 deň k zostávajúcim dňom dovolenky pripočítal
				dateFrom.add(Calendar.DATE, 1);

				float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(dateFrom.getTime(), request.getDateTo().getTime(), holidays);
				float timeStampWorkingDays = (float) 0;

				List<CViewTimeStamp> timestamps = this.timeStampDao.getHolidayRecordsFromInterval(owner, dateFrom, request.getDateTo());
				if (timestamps != null) {
					for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
						CViewTimeStamp cViewTimeStamp = iterator.next();

						if (CDateServerUtils.isWorkingDayWithCheckHolidays(cViewTimeStamp.getTimeFrom().getTime(), holidays)) {
							long hours4duration = 240;

							if (cViewTimeStamp.getDuration() == hours4duration) {
								timeStampWorkingDays += 0.5;
							} else {
								timeStampWorkingDays += 1.0;
							}
						}
					}
				}

				if (isNextYear) {
					owner.setVacationNextYear(remainingDaysNextYear + numberOfWorkingDays - timeStampWorkingDays, changedBy);
				} else {
					owner.setVacation(remainingDays + numberOfWorkingDays - timeStampWorkingDays, changedBy);
				}
			} else if (request.getDateTo().getTimeInMillis() == request.getDateLastGenHoliday().getTimeInMillis()) {
				// nepripočítavam žiadne dni dovolenky
			}
		}
	}

	/**
	 * metóda na pridanie dní dovolenky pre tento a nasledujúci rok
	 */
	private void updateVacationDaysThisANDNextYear(CRequest request, CUser owner, Double remainingDays, Double remainingDaysNextYear, List<CHoliday> holidays) {

		int actualYear = Calendar.getInstance().get(Calendar.YEAR);

		Calendar calendarStart = (Calendar) request.getDateFrom().clone();
		calendarStart.set(Calendar.YEAR, actualYear + 1);
		calendarStart.set(Calendar.MONTH, 0);
		calendarStart.set(Calendar.DAY_OF_MONTH, 1);

		Calendar calendarEnd = (Calendar) request.getDateFrom().clone();
		calendarEnd.set(Calendar.YEAR, actualYear);
		calendarEnd.set(Calendar.MONTH, 11);
		calendarEnd.set(Calendar.DAY_OF_MONTH, 31);

		final CUser changedBy = this.userDao.findById(0L);

		// aktuálny rok
		if (request.getDateLastGenHoliday() == null) {
			float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(request.getDateFrom().getTime(), calendarEnd.getTime(), holidays);
			float timeStampWorkingDays = (float) 0;

			List<CViewTimeStamp> timestamps = this.timeStampDao.getHolidayRecordsFromInterval(owner, request.getDateFrom(), calendarEnd);

			if (timestamps != null) {
				for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
					CViewTimeStamp cViewTimeStamp = iterator.next();

					if (CDateServerUtils.isWorkingDayWithCheckHolidays(cViewTimeStamp.getTimeFrom().getTime(), holidays)) {
						long hours4duration = 240;

						if (cViewTimeStamp.getDuration() == hours4duration) {
							timeStampWorkingDays += 0.5;
						} else {
							timeStampWorkingDays += 1.0;
						}
					}
				}
			}
			owner.setVacation(remainingDays + numberOfWorkingDays - timeStampWorkingDays, changedBy);
		}

		else if (request.getDateTo().getTimeInMillis() > request.getDateLastGenHoliday().getTimeInMillis()) {
			Calendar dateFrom = (Calendar) request.getDateLastGenHoliday();
			// vyhľadám záznamy až od nasledujúce dňa, pretože pri automatickom
			// generovaní
			// sa pri nevytvorení záznamu o dovolenke už 1 deň k zostávajúcim
			// dňom dovolenky pripočítal
			dateFrom.add(Calendar.DATE, 1);

			float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(dateFrom.getTime(), calendarEnd.getTime(), holidays);
			float timeStampWorkingDays = (float) 0;

			List<CViewTimeStamp> timestamps = this.timeStampDao.getHolidayRecordsFromInterval(owner, dateFrom, calendarEnd);
			if (timestamps != null) {
				for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
					CViewTimeStamp cViewTimeStamp = iterator.next();

					if (CDateServerUtils.isWorkingDayWithCheckHolidays(cViewTimeStamp.getTimeFrom().getTime(), holidays)) {
						long hours4duration = 240;

						if (cViewTimeStamp.getDuration() == hours4duration) {
							timeStampWorkingDays += 0.5;
						} else {
							timeStampWorkingDays += 1.0;
						}
					}
				}
			}
			owner.setVacation(remainingDays + numberOfWorkingDays - timeStampWorkingDays, changedBy);
		} else if (request.getDateTo().getTimeInMillis() == request.getDateLastGenHoliday().getTimeInMillis()) {
			// nepripočítavam žiadne dni dovolenky
		}

		// nasledujúci rok
		if (remainingDaysNextYear != null) {

			if (request.getDateLastGenHoliday() == null) {

				float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(calendarStart.getTime(), request.getDateTo().getTime(), holidays);
				float timeStampWorkingDays = (float) 0;

				List<CViewTimeStamp> timestamps = this.timeStampDao.getHolidayRecordsFromInterval(owner, calendarStart, request.getDateTo());

				if (timestamps != null) {
					for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
						CViewTimeStamp cViewTimeStamp = iterator.next();

						if (CDateServerUtils.isWorkingDayWithCheckHolidays(cViewTimeStamp.getTimeFrom().getTime(), holidays)) {
							long hours4duration = 240;

							if (cViewTimeStamp.getDuration() == hours4duration) {
								timeStampWorkingDays += 0.5;
							} else {
								timeStampWorkingDays += 1.0;
							}
						}
					}
				}
				owner.setVacationNextYear(remainingDaysNextYear + numberOfWorkingDays - timeStampWorkingDays, changedBy);
			}

			else if (request.getDateTo().getTimeInMillis() > request.getDateLastGenHoliday().getTimeInMillis()) {
				Calendar dateFrom = (Calendar) request.getDateLastGenHoliday();
				// vyhľadám záznamy až od nasledujúce dňa, pretože pri automatickom generovaní
				// sa pri nevytvorení záznamu o dovolenke už 1 deň k zostávajúcim dňom dovolenky
				// pripočítal
				dateFrom.add(Calendar.DATE, 1);

				float numberOfWorkingDays = (float) CDateServerUtils.getWorkingDaysCheckHolidays(dateFrom.getTime(), request.getDateTo().getTime(), holidays);
				float timeStampWorkingDays = (float) 0;

				List<CViewTimeStamp> timestamps = this.timeStampDao.getHolidayRecordsFromInterval(owner, dateFrom, request.getDateTo());
				if (timestamps != null) {
					for (Iterator<CViewTimeStamp> iterator = timestamps.iterator(); iterator.hasNext();) {
						CViewTimeStamp cViewTimeStamp = iterator.next();

						if (CDateServerUtils.isWorkingDayWithCheckHolidays(cViewTimeStamp.getTimeFrom().getTime(), holidays)) {
							long hours4duration = 240;

							if (cViewTimeStamp.getDuration() == hours4duration) {
								timeStampWorkingDays += 0.5;
							} else {
								timeStampWorkingDays += 1.0;
							}
						}
					}
				}
				owner.setVacationNextYear(remainingDaysNextYear + numberOfWorkingDays - timeStampWorkingDays, changedBy);
			} else if (request.getDateTo().getTimeInMillis() == request.getDateLastGenHoliday().getTimeInMillis()) {
				// nepripočítavam žiadne dni dovolenky
			}
		}
	}

	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public Boolean isAllowedHomeOfficeForToday(Long userId, Date date) throws CBusinessException {
		Boolean retVal = new Boolean(false);

		CUser user = this.userDao.findById(userId);

		if ((user.getHomeOfficePermission().getId()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
			retVal = Boolean.FALSE;
		} else if ((user.getHomeOfficePermission().getId()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
			retVal = this.requestDao.existsClientRequestsHomeOffice4Today(userId, date);
		} else if ((user.getHomeOfficePermission().getId()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
			retVal = Boolean.TRUE;
		}

		return retVal;
	}

	@Override
	@Transactional(rollbackForClassName = "CBusinessException")
	public Boolean isAllowedHomeOfficeInInterval(Long userId, Long clientId, Date dateFrom, Date dateTo) throws CBusinessException {
		Boolean retVal = new Boolean(false);

		CUser user = this.userDao.findById(userId);

		if ((user.getHomeOfficePermission().getId()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_DISALLOWED)) {
			retVal = Boolean.FALSE;
		} else if ((user.getHomeOfficePermission().getId()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_REQUEST)) {
			List<CHoliday> holidays = this.holidayDao.findAllValidClientsHolidays(clientId, null);
			retVal = this.requestDao.existsClientRequestsHomeOfficeInInterval(userId, clientId, dateFrom, dateTo, holidays);
		} else if ((user.getHomeOfficePermission().getId()).equals(IHomeOfficePermissionConstants.HO_PERMISSION_ALLOWED)) {
			retVal = Boolean.TRUE;
		}

		return retVal;
	}
}
