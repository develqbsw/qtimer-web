package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IRequestStateDao;
import sk.qbsw.sed.server.model.codelist.CRequestStatus;

/**
 * DAO implementation using hibernate
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CRequestStateDao extends AHibernateDao<CRequestStatus> implements IRequestStateDao {

	@SuppressWarnings("unchecked")
	public List<CRequestStatus> findAllValid() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequestStatus.class);
		return (List<CRequestStatus>) criteria.list();
	}
}