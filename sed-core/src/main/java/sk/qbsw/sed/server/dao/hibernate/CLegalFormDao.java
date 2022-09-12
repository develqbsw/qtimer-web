package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.ILegalFormDao;
import sk.qbsw.sed.server.model.codelist.CLegalForm;

/**
 * Legal form DAO.
 * 
 * @author Dalibor Rak
 * @Version 0.1
 * @since 0.1
 */
@Repository
public class CLegalFormDao extends AHibernateDao<CLegalForm> implements ILegalFormDao {

	@SuppressWarnings("unchecked")
	public List<CLegalForm> getAllValid() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CLegalForm.class);
		criteria.add(Property.forName("valid").eq(Boolean.TRUE));

		criteria.addOrder(Order.asc("description"));

		return (List<CLegalForm>) criteria.list();
	}
}
