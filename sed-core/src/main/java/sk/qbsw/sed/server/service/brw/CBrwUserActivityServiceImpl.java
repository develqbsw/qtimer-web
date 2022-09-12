package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.brw.CUserActivityRecord;
import sk.qbsw.sed.client.model.codelist.CActivityBrwFilterCriteria;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.service.brw.IBrwUserActivityService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * 
 * @author bado
 *
 */
@Service(value = "brwUserActivityService")
public class CBrwUserActivityServiceImpl implements IBrwUserActivityService {

	@Autowired
	private IActivityDao activityDao;

	@Autowired
	private IUserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public List<CUserActivityRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		CUser user = userDao.findById(loggedUser.getUserId());

		// Zoznam aktivít na výber na obrazovke "Moje aktivity" obmedziť na "t_ct_activity.c_flag_working = true"
		((CActivityBrwFilterCriteria) criteria).setWorking(true);

		final List<CActivity> activities = this.activityDao.findAllByCriteria(loggedUser.getClientInfo().getClientId(), criteria, user, startRow, endRow, sortProperty, sortAsc);

		final List<CUserActivityRecord> retVal = new ArrayList<>();

		for (final CActivity activity : activities) {
			CUserActivityRecord record = new CUserActivityRecord();

			record.setActivityId(activity.getId());
			record.setActivityName(activity.getName());
			record.setFlagMyActivity(user.getUserActivities().contains(activity));

			retVal.add(record);
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		CUser user = userDao.findById(loggedUser.getUserId());

		// Zoznam aktivít na výber na obrazovke "Moje aktivity" obmedziť na "t_ct_activity.c_flag_working = true"
		((CActivityBrwFilterCriteria) criteria).setWorking(true);

		return this.activityDao.count(loggedUser.getClientInfo().getClientId(), criteria, user);
	}
}
