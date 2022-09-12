package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IActivityInitialDao;
import sk.qbsw.sed.server.model.codelist.CActivityInitial;

/**
 * DAO to object CProject
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CActivityInitialDao extends AHibernateDao<CActivityInitial> implements IActivityInitialDao {
	@SuppressWarnings("unchecked")
	public List<CActivityInitial> findAll() {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CActivityInitial.class);
		return criteria.list();
	}
}
