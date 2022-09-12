package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IEmploymentTypeDao;
import sk.qbsw.sed.server.model.domain.CEmploymentType;

/**
 * DAO implementation using hibernate
 * 
 * @author Ľudovít Kováč
 * 
 */
@Repository
public class CEmploymentTypeDao extends AHibernateDao<CEmploymentType> implements IEmploymentTypeDao {
	@Override
	@SuppressWarnings("unchecked")
	public List<CEmploymentType> findAllEmploymentTypes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CEmploymentType.class);
		return (List<CEmploymentType>) criteria.list();
	}
}
