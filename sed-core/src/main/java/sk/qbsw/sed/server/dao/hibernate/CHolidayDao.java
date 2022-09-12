package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.codelist.CHolidayBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.model.codelist.CHoliday;

/**
 * DAO interface implementation to CHolidayEntity
 * 
 * @see CHoliday
 * 
 * @author Ladislav Rosenberg
 * @Version 1.0
 * @since 1.6.2
 * 
 */
@Repository
public class CHolidayDao extends AHibernateDao<CHoliday> implements IHolidayDao {
	
	private static final String CLIENT_ID = "client.id";

	@Override
	public Long count(Long clientId, IFilterCriteria filterCriteria) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CHoliday.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));

		CHolidayBrwFilterCriteria myFilterCriteria = (CHolidayBrwFilterCriteria) filterCriteria;

		if (myFilterCriteria != null) {
			setRestrictionsForFindAllClientsHolidays(criteria, myFilterCriteria);
		}
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CHoliday> findAllClientsHolidays(Long clientId, IFilterCriteria filterCriteria, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CHoliday.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));

		CHolidayBrwFilterCriteria myFilterCriteria = (CHolidayBrwFilterCriteria) filterCriteria;

		if (myFilterCriteria != null) {
			setRestrictionsForFindAllClientsHolidays(criteria, myFilterCriteria);
		}

		if (sortAsc) {
			criteria.addOrder(Order.asc(sortProperty));
		} else {
			criteria.addOrder(Order.desc(sortProperty));
		}

		criteria.setFirstResult(startRow);
		criteria.setFetchSize(endRow - startRow);

		return criteria.list();
	}

	private void setRestrictionsForFindAllClientsHolidays(Criteria criteria, CHolidayBrwFilterCriteria myFilterCriteria) {
		if (myFilterCriteria.getYear() != null) {
			criteria.add(Restrictions.sqlRestriction("(SELECT EXTRACT (YEAR FROM this_.c_day)) = " + myFilterCriteria.getYear()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CHoliday> findAllValidClientsHolidays(Long clientId, Integer selectedYear) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CHoliday.class);
		criteria.add(Property.forName(CLIENT_ID).eq(clientId));
		criteria.add(Property.forName("valid").eq(Boolean.TRUE));
		criteria.addOrder(Order.asc("day"));

		List<CHoliday> result = new ArrayList<>();
		List<CHoliday> tmpResult = criteria.list();
		if (selectedYear != null) {
			for (CHoliday h : tmpResult) {
				if (h.getDay().get(Calendar.YEAR) == selectedYear.intValue()) {
					result.add(h);
				}
			}
		} else {
			result = tmpResult;
		}
		return result;
	}

}
