package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.framework.generator.IGenerator;
import sk.qbsw.sed.server.dao.IMonthEmploeeWorksheetDao;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;

/**
 * DAO accessing v_timestap table
 * 
 * @author rosenberg
 * @since 1.6.3
 * @version 1.0
 */
@Repository
public class CMonthEmploeeWorksheetDao extends AHibernateDao<CViewTimeStamp> implements IMonthEmploeeWorksheetDao<CViewTimeStamp> {

	/**
	 * @see IMonthEmploeeWorksheetDao#findByUserAndPeriod(Long, Calendar, Calendar)
	 */
	@SuppressWarnings("unchecked")
	public List<CViewTimeStamp> findByUserAndPeriod(Long userId, Calendar dateFrom, Calendar dateTo, final boolean alsoNotConfirmed, final String screenType) {
		// vytiahnut zaznamy o platnych nepracovnych a pracovnych
		// aktivitach pre zamestnanca v danom casovom rozsahu
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewTimeStamp.class);
		criteria.add(Property.forName("userId").eq(userId));
		Criterion dateGE = Property.forName("timeFrom").ge(dateFrom);
		Criterion dateLE = Property.forName("timeTo").le(dateTo);
		final Criterion dateCriterion = Restrictions.and(dateGE, dateLE);
		criteria.add(dateCriterion);
		criteria.add(Restrictions.isNotNull("timeTo"));

		// vsetko okrem prestavky
		criteria.add(Property.forName("activityId").ne(-1l));

		if (!alsoNotConfirmed) {
			// ak je checkbox zaškrtnutý, tak brať pri generovaní záznamy ako doteraz
			// ak je checkbox nie zaškrtnutý, tak brať pri generovaní záznamy ako doteraz a
			// navyše pridať podmienku na stav

			List<Long> statusIds = new ArrayList<>();

			if (screenType.equals(IGenerator.MY_REPORT_SCREEN)) {
				// na obrazovke Prehľady/Export výkazu práce stav 2, 3 alebo 4
				statusIds.add(2L);
				statusIds.add(3L);
				statusIds.add(4L);
			} else if (screenType.equals(IGenerator.SUBORDINATE_REPORT_SCREEN)) {
				// na obrazovke Zamestanci/Export výkazu práce ak nie som prihlásený ako admin stav 3 alebo 4
				statusIds.add(3L);
				statusIds.add(4L);
			} else if (screenType.equals(IGenerator.EMPLOYEES_REPORT_SCREEN)) {
				// na obrazovke Zamestanci/Export výkazu práce ak som prihlásený ako admin stav 4
				statusIds.add(4L);
			}
			criteria.add(Restrictions.in("statusId", statusIds.toArray()));
		}

		criteria.addOrder(Order.asc("userId"));
		criteria.addOrder(Order.asc("timeFrom"));

		return (List<CViewTimeStamp>) criteria.list();
	}
}
