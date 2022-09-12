package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.service.brw.IBrwProjectService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CProject;

@Service(value = "brwProjectService")
public class CBrwProjectServiceImpl implements IBrwProjectService {

	@Autowired
	private IProjectDao projectDao;

	@Override
	@Transactional(readOnly = true)
	public List<CProjectRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, final IFilterCriteria criteria) throws CBusinessException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CProject> projects = this.projectDao.findAllByCriteria(loggedUser.getClientInfo().getClientId(), criteria, null, startRow, endRow, sortProperty, sortAsc);

		final List<CProjectRecord> retVal = new ArrayList<>();

		for (final CProject cProject : projects) {
			retVal.add(cProject.convert());
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(final IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.projectDao.count(loggedUser.getClientInfo().getClientId(), criteria, null);
	}
}
