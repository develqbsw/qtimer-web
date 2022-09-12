package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IActivityRestrictionGroupDao;
import sk.qbsw.sed.server.model.restriction.CActivityRestrictionGroup;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 * 
 */
@Repository
public class CActivityRestrictionGroupDao extends AHibernateDao<CActivityRestrictionGroup> implements IActivityRestrictionGroupDao {

	private static final String CLIENT_ID = "client.id";
	
	/**
	 * @see IActivityRestrictionGroupDao#findByClient(Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivityRestrictionGroup> findByClient(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityRestrictionGroup.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public Long count(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityRestrictionGroup.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	/**
	 * @see IActivityRestrictionGroupDao#findByClientActivityValidFlag(Long, Long,
	 *      Boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<CActivityRestrictionGroup> findByClientActivityValidFlag(Long clientId, Long activityId, Boolean validFlag) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityRestrictionGroup.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		if (activityId != null) {
			criteria.add(Property.forName("activity.id").eq(activityId));
		}
		if (validFlag != null) {
			criteria.add(Property.forName("valid").eq(validFlag));
		}
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	/**
	 * @see IActivityRestrictionGroupDao#delete(Long)
	 */
	public void delete(Long groupId) {
		CActivityRestrictionGroup group = findById(groupId);
		if (group != null) {
			sessionFactory.getCurrentSession().delete(group);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CActivityRestrictionGroup> findAllForTable(Long clientId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityRestrictionGroup.class);

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
}
