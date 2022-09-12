package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IHomeOfficePermissionDao;
import sk.qbsw.sed.server.model.codelist.CHomeOfficePermission;

/**
 * DAO implementation using hibernate
 * 
 * @author Ľudovít Kováč
 * 
 */
@Repository
public class CHomeOfficePersmissionDao extends AHibernateDao<CHomeOfficePermission> implements IHomeOfficePermissionDao {
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CHomeOfficePermission> findAllPermissionTypes() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CHomeOfficePermission.class);
		return (List<CHomeOfficePermission>) criteria.list();
	}
}
