package sk.qbsw.sed.server.dao.hibernate;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CActivityBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.dao.IActivityDao;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;

/**
 * DAO to object CProject
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CActivityDao extends AHibernateDao<CActivity> implements IActivityDao {

	private static final String VALID = "valid";
	private static final String ORDER = "order";
	private static final String CLIENT_ID = "client.id";
	
	/**
	 * @see IProjectDao#findAll(Long, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivity> findAll(final Long orgId, final boolean valid, final boolean withSystem) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);

		this.addSystemCriteriaIfNeeded(criteria, orgId, withSystem);
		this.addExcludeDefaultActivities(criteria);
		criteria.add(Property.forName(VALID).eq(valid));
		criteria.addOrder(Order.asc(ORDER));
		return criteria.list();
	}

	@Override
	public Long count(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		this.addExcludeDefaultActivities(criteria);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CActivity> findAll(final Long orgId, final boolean withSystem) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		this.addSystemCriteriaIfNeeded(criteria, orgId, withSystem);
		this.addExcludeDefaultActivities(criteria);
		criteria.addOrder(Order.asc(ORDER));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CActivity> findAll(final Long orgId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		criteria.add(Property.forName(CLIENT_ID).eq(orgId));
		if (sortAsc) {
			criteria.addOrder(Order.asc(sortProperty));
		} else {
			criteria.addOrder(Order.desc(sortProperty));
		}

		criteria.setFirstResult(startRow);
		criteria.setFetchSize(endRow - startRow);

		return criteria.list();
	}

	/**
	 * @see sk.qbsw.sed.server.dao.IActivityDao#findByValidityOrTimeSheet(java.lang.Long,
	 *      boolean, java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivity> findByValidityOrTimeSheet(final Long orgId, final boolean valid, final Long timesheetId) {
		// create query
		String timeSheetQuery = "";

		if (timesheetId != null) {
			timeSheetQuery = " or exists(select null from t_time_sheet_record where fk_activity = a.pk_id and pk_id = ? and a.pk_id != -101 and a.pk_id != -102)";
		}
		final String mainQUery = "select * from t_ct_activity a where (a.fk_client = 0 or a.fk_client = ?) and a.c_flag_valid = ? and a.pk_id != -101 and a.pk_id != -102 " + timeSheetQuery
				+ " order by a.c_client_order";

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(mainQUery);

		// sets parameters
		query.setParameter(0, orgId);
		query.setParameter(1, valid);

		if (timesheetId != null) {
			query.setParameter(2, timesheetId);
		}
		query.addEntity(CActivity.class);

		return query.list();
	}

	/**
	 * @see IActivityDao#findByName(Long, String)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivity> findByName(final Long orgId, final String name) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		this.addExcludeDefaultActivities(criteria);
		if (orgId != null) {
			criteria.add(Property.forName(CLIENT_ID).eq(orgId));
		}
		if (name != null) {
			criteria.add(Property.forName("name").eq(name));
		}

		criteria.addOrder(Order.asc(ORDER));

		return criteria.list();
	}

	/**
	 * @see IActivityDao#initialize(Long)
	 */
	public void initialize(final Long clientId) {
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select f_sed__insert_initial_activities(" + clientId + ")");

		final String retVal = query.list().get(0).toString();

		if ("0".equals(retVal)) {
			// do nothing
		} else {
			throw new CSystemFailureException("Unknown response from PLSQL", null);
		}
	}

	/**
	 * @see IActivityDao#findAll(Long, boolean, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivity> findAll(final Long orgId, final boolean valid, final boolean working, final boolean withSystem) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		this.addSystemCriteriaIfNeeded(criteria, orgId, withSystem);
		this.addExcludeDefaultActivities(criteria);
		criteria.add(Property.forName(VALID).eq(valid));
		criteria.add(Property.forName("working").eq(working));

		criteria.addOrder(Order.asc(ORDER));

		return criteria.list();
	}

	private void addSystemCriteriaIfNeeded(final Criteria criteria, final Long orgId, final boolean system) {
		final Criterion clientIdEqOrgId = Property.forName(CLIENT_ID).eq(orgId);
		final Criterion clientIdEqSystemClientId = Property.forName(CLIENT_ID).eq(new Long(0));

		if (system) {
			criteria.add(Restrictions.or(clientIdEqOrgId, clientIdEqSystemClientId));
		} else {
			criteria.add(clientIdEqOrgId);
		}
	}

	private void addExcludeDefaultActivities(final Criteria criteria) {
		final Criterion workingDefault = Property.forName("id").ne(new Long(-101));
		final Criterion nonWorkingDefault = Property.forName("id").ne(new Long(-102));
		criteria.add(Restrictions.and(workingDefault, nonWorkingDefault));
	}

	@SuppressWarnings("unchecked")
	public CActivity findDefaultActivity(Long orgId, final boolean withSystem) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);

		this.addSystemCriteriaIfNeeded(criteria, orgId, withSystem);
		criteria.add(Property.forName("flagDefault").eq(Boolean.TRUE));
		final List<CActivity> list = criteria.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CActivity> findAllForLimits(Long orgId, boolean valid, boolean withSystem) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);

		this.addSystemCriteriaIfNeeded(criteria, orgId, withSystem);
		this.addExcludeDefaultActivities(criteria);
		criteria.add(Property.forName(VALID).eq(valid));
		criteria.add(Restrictions.or(Restrictions.eq("id", IActivityConstant.NOT_WORK_ALERTNESSWORK), Restrictions.eq("id", IActivityConstant.NOT_WORK_INTERACTIVEWORK)));
		criteria.addOrder(Order.asc(ORDER));
		return criteria.list();
	}

	@Override
	public List<CActivity> findAllByCriteria(Long clientId, IFilterCriteria filterCriteria, CUser user, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));

		// process criteria
		CActivityBrwFilterCriteria myFilterCriteria = (CActivityBrwFilterCriteria) filterCriteria;
		if (myFilterCriteria != null) {
			this.prepareCriteria(myFilterCriteria, criteria, user);
		}

		if (sortAsc) {
			criteria.addOrder(Order.asc(sortProperty));
		} else {
			criteria.addOrder(Order.desc(sortProperty));
		}

		criteria.setFirstResult(startRow);
		criteria.setFetchSize(endRow - startRow);

		return criteria.list();
	}

	public Long count(Long orgId, IFilterCriteria filterCriteria, CUser user) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivity.class);
		criteria.add(Property.forName(CLIENT_ID).eq(orgId));

		// process criteria
		CActivityBrwFilterCriteria myFilterCriteria = (CActivityBrwFilterCriteria) filterCriteria;
		if (myFilterCriteria != null) {
			this.prepareCriteria(myFilterCriteria, criteria, user);
		}
		criteria.setProjection(Projections.count("id"));

		return (Long) criteria.uniqueResult();
	}

	private void prepareCriteria(final CActivityBrwFilterCriteria filter, final Criteria criteria, CUser user) {
		Long activityId = filter.getActivityId();
		String activityName = filter.getActivityName();
		Boolean working = filter.isWorking();

		// process filter
		if (activityId != null) {
			if (activityId.longValue() > ISearchConstants.ALL) {
				// add project id
				criteria.add(Property.forName("id").eq(activityId));
			} else if (activityId.longValue() == ISearchConstants.ALL_ACTIVE) {
				criteria.add(Property.forName(VALID).eq(true));
			} else if (activityId.longValue() == ISearchConstants.ALL_NOT_ACTIVE) {
				criteria.add(Property.forName(VALID).eq(false));
			} else if (activityId.longValue() == ISearchConstants.ALL_MY_ACTIVITIES && user != null) {
				criteria.add(Restrictions.sqlRestriction(
						"this_.pk_id IN (SELECT v.useractivities_pk_id FROM t_user_t_ct_activity v " + "JOIN t_user u ON v.t_user_pk_id = u.pk_id " + "WHERE u.pk_id = " + user.getId() + ")"));
				criteria.add(Property.forName(VALID).eq(true));
			}
		}
		if (activityName != null && !"".equals(activityName)) {
			// add activity name
			criteria.add(Restrictions.sqlRestriction("lower(unaccent(c_name)) like lower(unaccent('%" + activityName + "%')) "));
		}

		if (working != null) {
			criteria.add(Property.forName("working").eq(working));
		}
	}

	@SuppressWarnings("unchecked")
	public List<CActivity> findAllByLastUsed(final Long userId, final boolean valid) {
		final Query query = sessionFactory.getCurrentSession().createQuery(
				"from CActivity as p where p.valid = :valid and p.id in (select r.activity.id from CTimeSheetRecord r where r.owner.id = :userId and :timeFrom <= r.changeTime and :timeTo >= r.changeTime) order by p.name, p.order");
		final Calendar timeFrom = Calendar.getInstance();
		timeFrom.add(Calendar.DAY_OF_MONTH, -7);
		query.setLong("userId", userId);
		query.setBoolean(VALID, valid);
		query.setCalendar("timeFrom", timeFrom);
		query.setCalendar("timeTo", Calendar.getInstance());

		return query.list();
	}
}
