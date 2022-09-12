package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.brw.CUserProjectRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.service.brw.IBrwUserProjectService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.domain.CUser;

@Service(value = "brwUserProjectService")
public class CBrwUserProjectServiceImpl implements IBrwUserProjectService {

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IUserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public List<CUserProjectRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		CUser user = userDao.findById(loggedUser.getUserId());

		final List<CProject> projects = this.projectDao.findAllByCriteria(loggedUser.getClientInfo().getClientId(), criteria, user, startRow, endRow, sortProperty, sortAsc);

		final List<CUserProjectRecord> retVal = new ArrayList<>();

		for (final CProject project : projects) {
			CUserProjectRecord record = new CUserProjectRecord();

			record.setProjectId(project.getId());
			record.setProjectName(project.getName());
			record.setFlagMyProject(user.getUserProjects().contains(project));

			retVal.add(record);
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		CUser user = userDao.findById(loggedUser.getUserId());

		return this.projectDao.count(loggedUser.getClientInfo().getClientId(), criteria, user);
	}
}
