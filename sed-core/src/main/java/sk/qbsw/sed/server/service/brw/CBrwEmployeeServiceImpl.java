package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.brw.IBrwEmployeeService;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CUser;

@Service(value = "brwEmployeeService")
public class CBrwEmployeeServiceImpl implements IBrwEmployeeService {

	@Autowired
	private IUserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public List<CEmployeeRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, String name) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CUser> employees = this.userDao.findAllEmployeesForRestrictions(loggedUser.getClientInfo().getClientId(), true, IUserTypes.EMPLOYEE, startRow, endRow, sortProperty, sortAsc, name);

		List<CEmployeeRecord> retval = new ArrayList<>();
		for (CUser employee : employees) {
			retval.add(employee.toEmployeeRecord());
		}

		return retval;
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(String name) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.userDao.count(loggedUser.getClientInfo().getClientId(), true, IUserTypes.EMPLOYEE, name);
	}
}
