package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.brw.IBrwActivityService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CActivity;

@Service(value = "brwActivityService")
public class CBrwActivityServiceImpl implements IBrwActivityService {
	@Autowired
	private IActivityDao activityDao;

	@Override
	@Transactional(readOnly = true)
	public List<CActivityRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CActivity> activities = this.activityDao.findAll(loggedUser.getClientInfo().getClientId(), startRow, endRow, sortProperty, sortAsc);

		final List<CActivityRecord> retVal = new ArrayList<>();

		for (final CActivity cActivity : activities) {
			retVal.add(cActivity.convert());
		}

		return retVal;

	}

	@Override
	@Transactional(readOnly = true)
	public Long count() throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.activityDao.count(loggedUser.getClientInfo().getClientId());
	}
}
