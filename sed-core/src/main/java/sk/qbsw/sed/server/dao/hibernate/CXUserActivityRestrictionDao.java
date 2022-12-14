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
			// V pr??pade, ??e v obmedzeniach nie je vyplnen?? skupina projektu, m????e t??to
			// pohotovos?? pou????va?? pre ??ubovo??n?? projekt.
			// Ak m?? pou????vate?? ale jedno obmedzenie so skupinou a jednu bez a zad??va
			// pohotovos?? s projektom, ktor?? do spad?? do skupiny
			// vyplnenej v obmedzen??, pri kontrole ??asov??ho ohrani??enia pou??i?? ohrani??enie
			// vyplnen?? v ohrani??en?? s vyplnenou skupinou.
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
