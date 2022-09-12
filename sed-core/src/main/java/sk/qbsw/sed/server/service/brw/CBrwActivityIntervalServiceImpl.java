package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.brw.IBrwActivityIntervalService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityIntervalDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@Service(value = "brwActivityIntervalService")
public class CBrwActivityIntervalServiceImpl implements IBrwActivityIntervalService {
	@Autowired
	private IActivityIntervalDao activityIntervalDao;

	@Transactional(readOnly = true)
	@Override
	public List<CActivityIntervalData> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		final List<CActivityInterval> intervals = this.activityIntervalDao.findForTable(loggedUser.getClientInfo().getClientId(), startRow, endRow, sortProperty, sortAsc);

		final List<CActivityIntervalData> retVal = new ArrayList<>();
		for (final CActivityInterval interval : intervals) {
			retVal.add(interval.convert());
		}

		return retVal;
	}

	@Transactional(readOnly = true)
	@Override
	public Long count() throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.activityIntervalDao.count(loggedUser.getClientInfo().getClientId());
	}
}
