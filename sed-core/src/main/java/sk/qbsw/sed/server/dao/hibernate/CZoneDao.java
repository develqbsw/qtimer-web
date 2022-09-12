package sk.qbsw.sed.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IZoneDao;
import sk.qbsw.sed.server.model.domain.CZone;

@Repository
public class CZoneDao extends AHibernateDao<CZone> implements IZoneDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<CZone> getZones(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CZone.class);
		criteria.add(Property.forName("client.id").eq(clientId));
		criteria.add(Property.forName("valid").eq(Boolean.TRUE));

		return criteria.list();
	}
}
