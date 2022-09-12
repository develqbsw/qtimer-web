package sk.qbsw.sed.server.dao.hibernate;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CEmployeesStatusBrwFilterCriteria;
import sk.qbsw.sed.server.dao.IViewEmployeesStatusDao;
import sk.qbsw.sed.server.model.brw.CViewEmployeesStatus;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;

/**
 * Dao for accessing view v_emploeyees_status.
 * 
 * @author rosenberg
 * @version 0.1
 * @since 1.6.0
 */
@Repository
public class CViewEmployeesStatusDao extends AHibernateDao<CViewEmployeesStatus> implements IViewEmployeesStatusDao {

	private static final String CLIENT_ID = "clientId";
	
	private static final String TMPDAY = "tmpday";

	/**
	 * @see IViewEmployeesStatusDao#findAll(CUser, IFilterCriteria, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<CViewEmployeesStatus> findAll(Long clientId, IFilterCriteria filter, int from, int count) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewEmployeesStatus.class);

		criteria.add(Property.forName(CLIENT_ID).eq(clientId));

		prepareCriteria(filter, criteria);
		criteria.setFirstResult(from);
		criteria.setFetchSize(count);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CViewEmployeesStatus> findAll(Long clientId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewEmployeesStatus.class);

		criteria.add(Property.forName(CLIENT_ID).eq(clientId));

		final Calendar currentday = Calendar.getInstance();
		CTimeUtils.convertToStartDate(currentday);
		criteria.add(Restrictions.eq(TMPDAY, currentday));

		criteria.addOrder(Order.asc("surname"));
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(0);
		criteria.setFetchSize(Integer.MAX_VALUE);
		return criteria.list();
	}

	/**
	 * Prepares criteria for browser
	 * 
	 */
	private void prepareCriteria(final IFilterCriteria filter, final Criteria criteria) {
		CEmployeesStatusBrwFilterCriteria employeesStatusBrwFilterCriteria = (CEmployeesStatusBrwFilterCriteria) filter;

		final Calendar currentday = Calendar.getInstance();
		currentday.setTime((employeesStatusBrwFilterCriteria).getDate());
		CTimeUtils.convertToStartDate(currentday);

		criteria.add(Restrictions.eq(TMPDAY, currentday));
		// Ak je flagWorkplace false, zobrazení sú všetci tak ako teraz.
		// Ak je true, zobrazení sú tí čo sú v práci ale nie sú mimo pracoviska,
		// a tí čo odišli z práce na prestávku/obed/k lekárovi.
		if (employeesStatusBrwFilterCriteria.getAtWorkplace()) {
			criteria.add(Restrictions.eq("flagWorkplace", employeesStatusBrwFilterCriteria.getAtWorkplace()));
		}

		if (employeesStatusBrwFilterCriteria.getZoneId() != null) {
			criteria.add(Restrictions.eq("zoneId", employeesStatusBrwFilterCriteria.getZoneId()));
		}

		if (employeesStatusBrwFilterCriteria.getUserIds() != null && !employeesStatusBrwFilterCriteria.getUserIds().isEmpty()) {
			criteria.add(Restrictions.in("id", employeesStatusBrwFilterCriteria.getUserIds()));
		}

		criteria.addOrder(Order.asc("surname"));
		criteria.addOrder(Order.asc("id"));
	}

	/**
	 * @see IViewEmployeesStatusDao#findAllMissing(CUser, Calendar)
	 */
	@SuppressWarnings("unchecked")
	public List<CViewEmployeesStatus> findAllMissing(CUser user, Calendar checkDatetime) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewEmployeesStatus.class);
		criteria.add(Property.forName(CLIENT_ID).eq(user.getClient().getId()));

		CTimeUtils.convertToStartDate(checkDatetime);

		criteria.add(Restrictions.eq(TMPDAY, checkDatetime));

		criteria.add(Property.forName("status").eq("NOT_IN_WORK")); // see target view definition!

		return criteria.list();
	}
}
