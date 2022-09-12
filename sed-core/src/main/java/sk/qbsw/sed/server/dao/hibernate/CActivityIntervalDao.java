package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IActivityIntervalDao;
import sk.qbsw.sed.server.model.restriction.CActivityInterval;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@Repository
public class CActivityIntervalDao extends AHibernateDao<CActivityInterval> implements IActivityIntervalDao {

	private static final String VALID = "valid";
	private static final String CLIENT_ID = "client.id";
	
	/**
	 * @see IActivityIntervalDao#findByGroupRestriction(Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivityInterval> findByGroupRestriction(Long groupRestrictionId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityInterval.class);
		criteria.add(Property.forName("group.id").eq(groupRestrictionId));
		criteria.add(Property.forName(VALID).eq(true));
		criteria.addOrder(Order.asc("id"));

		return criteria.list();
	}

	public Long count(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityInterval.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CActivityInterval> findForTable(Long clientId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityInterval.class);

		criteria.add(Restrictions.eq(CLIENT_ID, clientId));

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
	 * @see IActivityIntervalDao#delete(Long)
	 */
	public void delete(Long groupId) {
		CActivityInterval group = findById(groupId);
		if (group != null) {
			sessionFactory.getCurrentSession().delete(group);
		}
	}
}
