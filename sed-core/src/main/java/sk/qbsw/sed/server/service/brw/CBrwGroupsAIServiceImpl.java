package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.brw.IBrwGroupsAIService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityRestrictionGroupDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.restriction.CActivityRestrictionGroup;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@Service(value = "brwGroupsAIService")
public class CBrwGroupsAIServiceImpl implements IBrwGroupsAIService {
	@Autowired
	private IActivityRestrictionGroupDao activityResctrictionGroupDao;

	@Transactional(readOnly = true)
	@Override
	public List<CGroupsAIData> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException {

		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CActivityRestrictionGroup> groups = this.activityResctrictionGroupDao.findAllForTable(loggedUser.getClientInfo().getClientId(), startRow, endRow, sortProperty, sortAsc);

		final List<CGroupsAIData> retVal = new ArrayList<>();
		for (final CActivityRestrictionGroup group : groups) {
			retVal.add(group.convert());
		}

		return retVal;
	}

	@Transactional(readOnly = true)
	@Override
	public Long count() throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.activityResctrictionGroupDao.count(loggedUser.getClientInfo().getClientId());
	}
}
