package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.ILockDateDao;
import sk.qbsw.sed.server.model.params.CLockDate;

/**
 * DAO to object CLockDate
 * 
 * @author Ladislav Rosenberg
 * @version 1.0
 * @since 1.6.6.0
 */
@Repository
public class CLockDateDao extends AHibernateDao<CLockDate> implements ILockDateDao {

	@SuppressWarnings("unchecked")
	public List<CLockDate> findByUser(Long userId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CLockDate.class);
		criteria.add(Property.forName("owner.id").eq(userId));
		criteria.add(Property.forName("valid").eq(true));
		criteria.setFetchSize(1000);

		return (List<CLockDate>) criteria.list();
	}
}
