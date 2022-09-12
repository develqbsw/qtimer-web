package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IParameterDao;
import sk.qbsw.sed.server.model.params.CParameterEntity;

/**
 * Legal form DAO.
 * 
 * @author Ladislav Rosenberg
 * @Version 1.0
 * @since 1.6.0
 */
@Repository
public class CParameterDao extends AHibernateDao<CParameterEntity> implements IParameterDao {

	@SuppressWarnings("unchecked")
	public List<CParameterEntity> findAll(Long clientId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CParameterEntity.class);
		criteria.add(Property.forName("client.id").eq(clientId));
		return (List<CParameterEntity>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CParameterEntity> findByNameForClient(Long clientId, String name) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CParameterEntity.class);
		
		if (clientId != null) {
			criteria.add(Property.forName("client.id").eq(clientId));
		}
		
		criteria.add(Property.forName("name").eq(name));
		return (List<CParameterEntity>) criteria.list();
	}
}
