package sk.qbsw.sed.server.dao.hibernate;

import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IUserPhotoDao;
import sk.qbsw.sed.server.model.domain.CUserPhoto;

@Repository
public class CUserPhotoDao extends AHibernateDao<CUserPhoto> implements IUserPhotoDao {
	
	public void deleteById(final Long recordId) {
		CUserPhoto record = findById(recordId);
		sessionFactory.getCurrentSession().delete(record);
	}
}
