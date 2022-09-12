package sk.qbsw.sed.server.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.response.EErrorCode;
import sk.qbsw.sed.client.service.business.IOrganizationTreeService;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.ITimesheetRecordDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;

@Service
public class CAuthorizationUtils {

	@Autowired
	private ITimesheetRecordDao timesheetDao;

	@Autowired
	private IOrganizationTreeService organizationTreeService;

	@Autowired
	private IRequestDao requestDao;

	/**
	 * User dao
	 */
	@Autowired
	private IUserDao userDao;

	@Transactional(readOnly = true)
	public void authorizationForTimeStampId(Long timeStampId) throws CApiException, CBusinessException {

		CTimeSheetRecord timeSheetRecord = timesheetDao.findById(timeStampId);
		CUser owner = timeSheetRecord.getOwner();
		authorization(owner);
	}

	@Transactional(readOnly = true)
	public Boolean authorizationForShowEditButtonOnDetail(Long timeStampId) throws CApiException, CBusinessException {

		CTimeSheetRecord timeSheetRecord = timesheetDao.findById(timeStampId);
		CUser owner = timeSheetRecord.getOwner();
		return isAuthorized(owner);
	}

	@Transactional(readOnly = true)
	public void authorizationForEmployeeId(Long employeeId) throws CApiException, CBusinessException {

		CUser owner = userDao.findById(employeeId);
		authorization(owner);
	}

	@Transactional(readOnly = true)
	public void authorizationForRequestId(Long requestId) throws CApiException, CBusinessException {
		CRequest request = requestDao.findById(requestId);
		CUser owner = request.getOwner();
		authorization(owner);
	}

	@Transactional(readOnly = true)
	public void authorizationForTimeStampIdExtended(Long timeStampId) throws CApiException, CBusinessException {

		CTimeSheetRecord timeSheetRecord = timesheetDao.findById(timeStampId);
		CUser owner = timeSheetRecord.getOwner();
		authorizationExtended(owner);
	}

	private void authorization(CUser owner) throws CApiException, CBusinessException {
		// pridavať, modifikovať, mazať timestamp môžem len ak je moja, alebo
		// môjho podriadeneho alebo som admin

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		if (owner.getId().equals(loggedUser.getUserId())) {
			// ak je to moj zaznam tak som v pohode
			return;
		}

		if (IUserTypeCode.ORG_ADMIN.equals(loggedUser.getRoleCode()) && loggedUser.getClientInfo().getClientId().equals(owner.getClient().getId())) {
			// ak som admin z tej istej organizacie tak je to v pohode
			return;
		}

		if (IUserTypeCode.RECEPTION.equals(loggedUser.getRoleCode()) && loggedUser.getClientInfo().getClientId().equals(owner.getClient().getId())) {
			// ak som recepcia z tej istej organizacie tak je to v pohode
			return;
		}

		List<CViewOrganizationTreeNodeRecord> subordinates = this.organizationTreeService.loadTreeByClientUser(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), true, true);

		for (CViewOrganizationTreeNodeRecord subordinate : subordinates) {
			if (subordinate.getUserId().equals(owner.getId())) {
				// ak je to zaznam mojho otroka tak je to v pohode
				return;
			}
		}

		throw new CApiException(EErrorCode.NOT_AUTHORIZED);
	}

	private void authorizationExtended(CUser owner) throws CApiException, CBusinessException {
		// pridavať, modifikovať, mazať timestamp môžem len ak je moja, alebo
		// môjho podriadeneho alebo som admin

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		if (owner.getId().equals(loggedUser.getUserId())) {
			// ak je to moj zaznam tak som v pohode
			return;
		}

		if (IUserTypeCode.ORG_ADMIN.equals(loggedUser.getRoleCode()) && loggedUser.getClientInfo().getClientId().equals(owner.getClient().getId())) {
			// ak som admin z tej istej organizacie tak je to v pohode
			return;
		}

		if (IUserTypeCode.RECEPTION.equals(loggedUser.getRoleCode()) && loggedUser.getClientInfo().getClientId().equals(owner.getClient().getId())) {
			// ak som recepcia z tej istej organizacie tak je to v pohode
			return;
		}

		List<CViewOrganizationTreeNodeRecord> subordinates = this.organizationTreeService.loadTreeByClientUser(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), true, true);

		for (CViewOrganizationTreeNodeRecord subordinate : subordinates) {
			if (subordinate.getUserId().equals(owner.getId())) {
				// ak je to zaznam mojho podriadeného tak je to v pohode
				return;
			}
		}

		CUser user = this.userDao.findById(loggedUser.getUserId());

		for (CUser notifiedUser : user.getNotified()) {
			if (notifiedUser.getId().equals(owner.getId())) {
				// ak je to zaznam notifikovaného usera tak je to v pohode
				return;
			}
		}

		throw new CApiException(EErrorCode.NOT_AUTHORIZED);
	}

	private Boolean isAuthorized(CUser owner) throws CApiException, CBusinessException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		if (owner.getId().equals(loggedUser.getUserId())) {
			// ak je to moj zaznam tak som v pohode
			return Boolean.TRUE;
		}

		if (IUserTypeCode.ORG_ADMIN.equals(loggedUser.getRoleCode()) && loggedUser.getClientInfo().getClientId().equals(owner.getClient().getId())) {
			// ak som admin z tej istej organizacie tak je to v pohode
			return Boolean.TRUE;
		}

		if (IUserTypeCode.RECEPTION.equals(loggedUser.getRoleCode()) && loggedUser.getClientInfo().getClientId().equals(owner.getClient().getId())) {
			// ak som recepcia z tej istej organizacie tak je to v pohode
			return Boolean.TRUE;
		}

		List<CViewOrganizationTreeNodeRecord> subordinates = this.organizationTreeService.loadTreeByClientUser(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), true, true);

		for (CViewOrganizationTreeNodeRecord subordinate : subordinates) {
			if (subordinate.getUserId().equals(owner.getId())) {
				// ak je to zaznam mojho podriadeného tak je to v pohode
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	public static void authorizeReceptionUser() throws CApiException, CBusinessException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		if (IUserTypeCode.RECEPTION.equals(loggedUser.getRoleCode())) {
			return;
		}

		throw new CApiException(EErrorCode.NOT_AUTHORIZED);
	}
}
