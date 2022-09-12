package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.brw.IBrwSubordinateEmployeeService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CUser;

@Service(value = "brwSubordinateEmployeeService")
public class CBrwSubordinateEmployeeServiceImpl implements IBrwSubordinateEmployeeService {

	@Autowired
	private IUserDao userDao;

	@Transactional(readOnly = true)
	public List<CUserDetailRecord> fetch() throws CBusinessException, CSecurityException {
		final List<CUser> subordinate = this.getData();
		final List<CUserDetailRecord> retVal = new ArrayList<>();
		for (final CUser user : subordinate) {
			retVal.add(user.convert());
		}
		return retVal;
	}

	private List<CUser> getData() throws CSecurityException {
		// takto isto sa dohladava aj strom period pre podriadenych na obrazovke
		// casovych znaciek...
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final CUser user = this.userDao.findById(loggedUser.getUserId());
		final CUser admin = this.userDao.findClientAdministratorAccount(loggedUser.getClientInfo().getClientId());

		List<CUser> subordinates = null;
		if (admin.getId().equals(user.getId())) {
			subordinates = this.userDao.findAllEmployees(user, IUserTypes.EMPLOYEE, true);
		} else {
			subordinates = this.userDao.findSubordinate(user, true, true, IUserTypes.EMPLOYEE);
		}
		return subordinates;
	}
}
