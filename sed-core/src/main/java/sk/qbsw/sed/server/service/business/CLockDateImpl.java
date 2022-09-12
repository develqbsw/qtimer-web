package sk.qbsw.sed.server.service.business;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.lock.CLockDateParameters;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.ILockDateService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.ILockDateDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.params.CLockDate;

/**
 * Service for read/write additional lock date records
 * 
 * @author Ladislav Rosenberg
 * @version 1.0
 * @since 1.6.0
 */
@Service(value = "lockDateService")
public class CLockDateImpl implements ILockDateService {

	@Autowired
	IUserDao userDao;

	@Autowired
	ILockDateDao lockDateDao;

	/**
	 * @see ILockDateService#generateMissigLockInfoRecords(Long)
	 */
	@Transactional
	public Long generateMissigLockInfoRecords(Long clientId) throws CBusinessException, CSecurityException {
		Long newRecordCounter = new Long(0);

		Calendar lockDateValue = Calendar.getInstance();
		lockDateValue.set(Calendar.HOUR_OF_DAY, 23);
		lockDateValue.set(Calendar.MINUTE, 59);
		lockDateValue.set(Calendar.SECOND, 59);
		lockDateValue.set(Calendar.MILLISECOND, 999);
		lockDateValue.add(Calendar.DAY_OF_YEAR, -1);

		CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		List<CUser> selectedUsers = this.userDao.findClientUsersWithoutLockInfoRecords(clientId, IUserTypes.EMPLOYEE, null);
		for (CUser user : selectedUsers) {
			CLockDate newLockRecord = new CLockDate();

			newLockRecord.setClient(user.getClient());
			newLockRecord.setOwner(user);

			newLockRecord.setUserRequestValidTo(lockDateValue);
			newLockRecord.setUserTimestampValidTo(lockDateValue);

			newLockRecord.setRequestLockTo(lockDateValue);
			newLockRecord.setTimestampLockTo(lockDateValue);

			newLockRecord.setValid(Boolean.TRUE);
			newLockRecord.setChangedBy(this.userDao.findById(loggedUser.getUserId()));
			newLockRecord.setChangeTime(Calendar.getInstance());

			this.lockDateDao.saveOrUpdate(newLockRecord);
			newRecordCounter++;
		}

		return newRecordCounter;
	}

	@Transactional
	public Boolean updateLockDateToSelectedUsers(CLockDateParameters lockDateParameters) throws CBusinessException, CSecurityException {
		Boolean retVal = Boolean.TRUE;

		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();

		Calendar lockDateValue = Calendar.getInstance();
		lockDateValue.setTime(lockDateParameters.getLockDate());
		lockDateValue.set(Calendar.HOUR_OF_DAY, 23);
		lockDateValue.set(Calendar.MINUTE, 59);
		lockDateValue.set(Calendar.SECOND, 59);
		lockDateValue.set(Calendar.MILLISECOND, 999);

		int locRecordType = lockDateParameters.getRecordType().intValue();

		List<Long> recordsIds = lockDateParameters.getRecordIds();
		if (recordsIds != null && recordsIds.size() > 0) {
			for (Long id : recordsIds) {
				CLockDate updatedLockRecord = this.lockDateDao.findById(id);
				if (locRecordType == 0 || locRecordType == 1) {
					updatedLockRecord.setTimestampLockTo(lockDateValue);
				}
				if (locRecordType == 0 || locRecordType == 2) {
					updatedLockRecord.setRequestLockTo(lockDateValue);
				}
				updatedLockRecord.setChangeTime(Calendar.getInstance());
				updatedLockRecord.setChangedBy(this.userDao.findById(loggedUserRecord.getUserId()));
				this.lockDateDao.saveOrUpdate(updatedLockRecord);
			}
		} else {
			retVal = Boolean.FALSE;
		}

		return retVal;
	}
}
