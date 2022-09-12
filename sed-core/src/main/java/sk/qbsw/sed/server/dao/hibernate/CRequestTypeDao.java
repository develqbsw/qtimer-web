package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IRequestTypeDao;
import sk.qbsw.sed.server.model.codelist.CRequestType;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;

/**
 * DAO implementation using hibernate
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@Repository
public class CRequestTypeDao extends AHibernateDao<CRequestType> implements IRequestTypeDao {

	private static final String ACTIVITY_ID = "activityId";
	
	@SuppressWarnings("unchecked")
	public List<CRequestType> findAllValid() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestType.class);
		return (List<CRequestType>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CRequestType> findRecordsForRequestReason() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestType.class);
		Criterion WS = Restrictions.eq(ACTIVITY_ID, IActivityConstant.NOT_WORK_SICKNESS);
		Criterion WB = Restrictions.eq(ACTIVITY_ID, IActivityConstant.NOT_WORK_WORKBREAK);
		Criterion WAH = Restrictions.eq("id", 7L);
		criteria.add(Restrictions.or(WS, WB, WAH));
		return (List<CRequestType>) criteria.list();
	}

	public CRequestType findRecordById(Long id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestType.class);
		criteria.add(Restrictions.eq("id", id));
		return (CRequestType) criteria.list().get(0);
	}

	/**
	 * @see IRequestTypeDao#findRecordByActivity(Long)
	 */
	@Override
	public CRequestType findRecordByActivity(Long activityId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestType.class);
		criteria.add(Restrictions.eq(ACTIVITY_ID, activityId));
		return (CRequestType) criteria.list().get(0);
	}
}