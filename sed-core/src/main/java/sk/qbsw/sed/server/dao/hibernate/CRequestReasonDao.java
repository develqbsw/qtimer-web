package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.model.codelist.CRequestReason;

/**
 * DAO for accessing request reason entities
 * 
 * @author rosenberg
 * @since 1.6.6.2
 * @version 1.6.6.2
 */
@Repository
public class CRequestReasonDao extends AHibernateDao<CRequestReason> implements IRequestReasonDao {

	private static final String CLIENT_ID = "client.id";
	private static final String SYSTEM = "system";
	private static final String VALID = "valid";
	
	/**
	 * @see IRequestReasonDao#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<CRequestReason> findAll() {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestReason.class);
		criteria.addOrder(Order.asc("id"));

		return criteria.list();
	}

	/**
	 * @see IRequestReasonDao#findValidByClient(Long, Boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<CRequestReason> findValidByClient(Long clientId, Boolean valid) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestReason.class);

		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName(VALID).eq(valid));
		criteria.add(Property.forName(SYSTEM).ne(Boolean.TRUE));
		criteria.addOrder(Order.asc("id"));

		return criteria.list();
	}

	/**
	 * @see IRequestReasonDao#findValidByClientRequestType(Long, Long, Boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<CRequestReason> findValidByClientRequestType(Long clientId, Long requestTypeId, Boolean valid) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestReason.class);

		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName("requestType.id").eq(requestTypeId));
		criteria.add(Property.forName(VALID).eq(valid));
		criteria.add(Property.forName(SYSTEM).ne(Boolean.TRUE));
		criteria.addOrder(Order.asc("id"));

		return criteria.list();
	}

	/**
	 * @see IRequestReasonDao#getActivityIdsByValidClientTrcords(Long, Boolean)
	 */
	public List<Long> getActivityIdsByValidClientRecords(Long clientId, Boolean valid) {
		List<CRequestReason> reasons = findValidByClient(clientId, valid);

		List<Long> retVal = new ArrayList<>();
		for (CRequestReason reason : reasons) {
			if (reason.getRequestType() != null) {
				Long activityId = reason.getRequestType().getActivityId();
				if (!retVal.contains(activityId)) {
					retVal.add(activityId);
				}
			}
		}

		return retVal;

	}

	@SuppressWarnings("unchecked")
	public List<CRequestReason> findClientOrSystemRecords(Long clientId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestReason.class);

		this.setClientOrSystemRestrictions(criteria, clientId);

		if (sortAsc) {
			criteria.addOrder(Order.asc(sortProperty));
		} else {
			criteria.addOrder(Order.desc(sortProperty));
		}

		criteria.setFirstResult(startRow);
		criteria.setFetchSize(endRow - startRow);

		return criteria.list();
	}

	public Long count(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestReason.class);
		this.setClientOrSystemRestrictions(criteria, clientId);
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	private void setClientOrSystemRestrictions(Criteria criteria, Long clientId) {
		Criterion clientRest = Restrictions.and(Restrictions.eq(CLIENT_ID, clientId), Restrictions.ne(SYSTEM, Boolean.TRUE));

		Criterion systemRest = Restrictions.and(Restrictions.eq(VALID, Boolean.TRUE), Restrictions.eq(SYSTEM, Boolean.TRUE));

		criteria.add(Restrictions.or(clientRest, systemRest));
	}

	/**
	 * @see IRequestReasonDao#findSystemRecords(Long)
	 */
	@SuppressWarnings("unchecked")
	public List<CRequestReason> findSystemRecords(Long requestTypeId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestReason.class);

		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName(SYSTEM).eq(Boolean.TRUE));
		criteria.add(Property.forName("requestType.id").eq(requestTypeId));

		return (List<CRequestReason>) criteria.list();
	}
}
