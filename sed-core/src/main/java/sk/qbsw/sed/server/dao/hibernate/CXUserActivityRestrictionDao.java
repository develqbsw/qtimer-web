package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.IXUserActivityRestrictionDao;
import sk.qbsw.sed.server.model.restriction.CXUserActivityRestriction;

/**
 * Cross table mapping object
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@Repository
public class CXUserActivityRestrictionDao extends AHibernateDao<CXUserActivityRestriction> implements IXUserActivityRestrictionDao {

	/**
	 * @see IXUserActivityRestrictionDao#findByUserId(Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CXUserActivityRestriction> findByUserId(Long userId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CXUserActivityRestriction.class);
		criteria.add(Property.forName("user.id").eq(userId));
		criteria.addOrder(Order.asc("id"));

		return criteria.list();
	}

	/**
	 * @see IXUserActivityRestrictionDao#findByProjectGroupAndUser(Long, String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CXUserActivityRestriction> findByProjectGroupAndUser(Long userId, String projectGroup) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CXUserActivityRestriction.class);
		criteria.add(Property.forName("user.id").eq(userId));
		criteria.addOrder(Order.asc("id"));

		List<CXUserActivityRestriction> retVal = new ArrayList<>();
		List<CXUserActivityRestriction> records = criteria.list();
		List<CXUserActivityRestriction> recordsNoProjectGroup = new ArrayList<>();

		if (projectGroup != null) {
			for (CXUserActivityRestriction record : records) {
				if (record.getRestrictionGroup().getProjectGroup() == null) {
					recordsNoProjectGroup.add(record);
				} else if (record.getRestrictionGroup().getProjectGroup().equals(projectGroup)) {
					retVal.add(record);
				}
			}
		} else {
			retVal = records;
		}

		if (retVal.isEmpty()) {
			// V prípade, že v obmedzeniach nie je vyplnená skupina projektu, môže túto
			// pohotovosť používať pre ľubovoľný projekt.
			// Ak má používateľ ale jedno obmedzenie so skupinou a jednu bez a zadáva
			// pohotovosť s projektom, ktorý do spadá do skupiny
			// vyplnenej v obmedzení, pri kontrole časového ohraničenia použiť ohraničenie
			// vyplnené v ohraničení s vyplnenou skupinou.
			retVal = recordsNoProjectGroup;
		}

		return retVal;
	}

	/**
	 * @see IXUserActivityRestrictionDao#deleteByUserId(Long)
	 */
	@Override
	public void deleteByUserId(Long userId) {
		List<CXUserActivityRestriction> records = findByUserId(userId);
		if (records != null && !records.isEmpty()) {
			for (CXUserActivityRestriction record : records) {
				deleteById(record.getId());
			}
		}
	}

	/**
	 * @see IXUserActivityRestrictionDao#deleteById(Long)
	 */
	@Override
	public void deleteById(Long id) {
		CXUserActivityRestriction record = findById(id);
		if (record != null) {
			sessionFactory.getCurrentSession().delete(record);
		}
	}
}
