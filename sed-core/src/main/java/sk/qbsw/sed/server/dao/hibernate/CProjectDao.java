package sk.qbsw.sed.server.dao.hibernate;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CProjectBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.dao.IProjectDao;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * DAO to object CProject
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CProjectDao extends AHibernateDao<CProject> implements IProjectDao {
	
	private static final String VALID = "valid";
	private static final String ORDER = "order";
	private static final String CLIENT_ID = "client.id";
	
	/**
	 * @see IProjectDao#findAllValid(Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CProject> findAll(final Long orgId, final boolean valid) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);

		criteria.add(Property.forName(CLIENT_ID).eq(orgId));
		criteria.add(Property.forName(VALID).eq(valid));
		criteria.addOrder(Order.asc("name"));
		criteria.addOrder(Order.asc(ORDER));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CProject> findAllByLastUsed(final Long userId, final boolean valid) {
		final Query query = sessionFactory.getCurrentSession().createQuery(
				"from CProject as p where p.valid = :valid and p.id in (select r.project.id from CTimeSheetRecord r where r.owner.id = :userId and :timeFrom <= r.changeTime and :timeTo >= r.changeTime) order by p.name, p.order");
		final Calendar timeFrom = Calendar.getInstance();
		timeFrom.add(Calendar.DAY_OF_MONTH, -7);
		query.setLong("userId", userId);
		query.setBoolean(VALID, valid);
		query.setCalendar("timeFrom", timeFrom);
		query.setCalendar("timeTo", Calendar.getInstance());

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CProject> findAll(final Long orgId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);
		criteria.add(Property.forName(CLIENT_ID).eq(orgId));

		criteria.addOrder(Order.asc("name"));
		criteria.addOrder(Order.asc(ORDER));

		return criteria.list();
	}

	public Long count(Long orgId, IFilterCriteria filterCriteria, CUser user) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);
		criteria.add(Property.forName(CLIENT_ID).eq(orgId));

		// process criteria
		CProjectBrwFilterCriteria myFilterCriteria = (CProjectBrwFilterCriteria) filterCriteria;
		if (myFilterCriteria != null) {
			this.prepareCriteria(myFilterCriteria, criteria, user);
		}
		criteria.setProjection(Projections.count("id"));

		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<CProject> findAllByCriteria(Long orgId, IFilterCriteria filterCriteria, CUser user, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);
		criteria.add(Property.forName(CLIENT_ID).eq(orgId));

		// process criteria
		CProjectBrwFilterCriteria myFilterCriteria = (CProjectBrwFilterCriteria) filterCriteria;
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

	private void prepareCriteria(final CProjectBrwFilterCriteria filter, final Criteria criteria, CUser user) {
		Long projectId = filter.getProjectId();
		String projectCode = filter.getProjectCode();
		String projectGroup = filter.getProjectGroup();
		String projectName = filter.getProjectName();

		// process filter
		if (projectId != null) {
			if (projectId.longValue() > ISearchConstants.ALL) {
				// add project id
				criteria.add(Property.forName("id").eq(projectId));
			} else if (projectId.longValue() == ISearchConstants.ALL_ACTIVE) {
				criteria.add(Property.forName(VALID).eq(true));
			} else if (projectId.longValue() == ISearchConstants.ALL_NOT_ACTIVE) {
				criteria.add(Property.forName(VALID).eq(false));
			} else if (projectId.longValue() == ISearchConstants.ALL_MY_PROJECTS && user != null) {
				criteria.add(Restrictions.sqlRestriction(
						"this_.pk_id IN (SELECT v.userprojects_pk_id FROM t_user_t_ct_project v " + "JOIN t_user u ON v.t_user_pk_id = u.pk_id " + "WHERE u.pk_id = " + user.getId() + ")"));
				criteria.add(Property.forName(VALID).eq(true));
			}
		}
		if (projectCode != null && !"".equals(projectCode)) {
			// add project code
			criteria.add(Restrictions.like("eviproCode", "%" + projectCode + "%"));
		}
		if (projectName != null && !"".equals(projectName)) {
			// add project name
			criteria.add(Restrictions.sqlRestriction("lower(unaccent(c_name)) like lower(unaccent('%" + projectName + "%')) "));
		}
		if (projectGroup != null && !"".equals(projectGroup)) {
			// only selected?
			if (!ISearchConstants.ALL.toString().equals(projectGroup)) {
				// add project group
				criteria.add(Restrictions.eq("group", projectGroup));
			}
			// in other case its mean : all
		}
	}

	@SuppressWarnings("unchecked")
	public List<CProject> findByValidityOrTimeSheet(final Long orgId, final boolean valid, final Long timesheetId) {
		String timeSheetQuery = "";
		if (timesheetId != null) {
			timeSheetQuery = " or exists(select null from t_time_sheet_record where fk_project = p.pk_id and pk_id = ?)";
		}
		final String mainQuery = "select distinct * from t_ct_project p where p.fk_client = ? and p.c_flag_valid = ? " + timeSheetQuery + " order by p.c_client_order";

		// create query
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(mainQuery);

		// sets parameters
		query.setParameter(0, orgId);
		query.setParameter(1, valid);

		if (timesheetId != null) {
			query.setParameter(2, timesheetId);
		}

		query.addEntity(CProject.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CProject> findByName(final Long orgId, final String name) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);
		if (orgId != null) {
			criteria.add(Property.forName(CLIENT_ID).eq(orgId));
		}
		if (name != null) {
			criteria.add(Property.forName("name").eq(name));
		}

		criteria.addOrder(Order.asc(ORDER));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CProject> findByGroupAndProjectId(final Long orgId, final String group, final String projectId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);
		if (orgId != null) {
			criteria.add(Property.forName(CLIENT_ID).eq(orgId));
		}
		if (group != null) {
			criteria.add(Property.forName("group").eq(group));
		}
		if (projectId != null) {
			criteria.add(Property.forName("eviproCode").eq(projectId));
		}

		criteria.addOrder(Order.asc(ORDER));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public CProject findDefaultProject(Long orgId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CProject.class);

		criteria.add(Property.forName(CLIENT_ID).eq(orgId));
		criteria.add(Property.forName("flagDefault").eq(Boolean.TRUE));
		final List<CProject> list = criteria.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
