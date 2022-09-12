package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.model.domain.CClient;

/**
 * DAO accessing T_CLIENT table
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CClientDao extends AHibernateDao<CClient> implements IClientDao {

	/**
	 * @see IClientDao
	 */
	public CClient findByIdNo(String id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CClient.class);
		criteria.add(Property.forName("identificationNumber").eq(id));

		return (CClient) criteria.uniqueResult();
	}

	/**
	 * @see IClientDao
	 */
	public CClient findByTaxNo(String tax) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CClient.class);
		criteria.add(Property.forName("taxNumber").eq(tax));

		return (CClient) criteria.uniqueResult();
	}

	/**
	 * @see IClientDao
	 */
	public CClient findByVatNo(String vat) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CClient.class);
		criteria.add(Property.forName("taxVatNumber").eq(vat));

		return (CClient) criteria.uniqueResult();
	}

	public CClient findClientById(Long id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CClient.class);
		criteria.add(Property.forName("id").eq(id));

		return (CClient) criteria.uniqueResult();
	}

	/**
	 * @see IClientDao#getApplicationClients()
	 */
	@SuppressWarnings("unchecked")
	public List<CClient> getApplicationClients() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CClient.class);
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
}
