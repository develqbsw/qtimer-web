package sk.qbsw.sed.server.dao.hibernate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.IRequestTypes;
import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.request.IRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CRequestForEmail;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;
import sk.qbsw.sed.server.service.business.IRequestStatusConstant;
import sk.qbsw.sed.server.util.CDateServerUtils;

/**
 * DAO implementation using hibernate
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@Repository
public class CRequestDao extends AHibernateDao<CRequest> implements IRequestDao {

	private static final String DATE_FROM = "dateFrom";
	private static final String DATE_TO = "dateTo";

	/**
	 * @see IRequestDao#findAllNotCancelledInTimeInterval(Date, Date, CUser)
	 */
	@SuppressWarnings("unchecked")
	public List<CRequest> findAllNotCancelledInTimeInterval(final Date dateFrom, final Date dateTo, final CUser user) {
		// create query
		StringBuilder sq = new StringBuilder();
		sq.append("select * from t_request ");
		sq.append("where ");
		sq.append("(fk_user_owner = ?) and ");
		sq.append("(fk_status not in (?,?)) and ");
		sq.append("( ");
		sq.append(" (c_date_from <= ? and c_date_to >= ?) or "); // ~ request interval vs. start interval
		sq.append(" (c_date_from < ? and c_date_to > ?) or "); // around start interval
		sq.append(" (c_date_from  < ? and c_date_to  > ?) "); // around end interval
		sq.append(")");

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sq.toString());

		// sets parameters
		query.setParameter(0, user.getId());
		query.setParameter(1, IRequestStatusConstant.CANCELLED);
		query.setParameter(2, IRequestStatusConstant.DECLINED);
		query.setParameter(3, dateFrom);
		query.setParameter(4, dateFrom);
		query.setParameter(5, dateFrom);
		query.setParameter(6, dateFrom);
		query.setParameter(7, dateTo);
		query.setParameter(8, dateTo);

		query.addEntity(CRequest.class);

		return query.list();
	}

	/**
	 * @see IRequestDao#findAllApprovedInTimeInterval(Date, Date, CUser)
	 */
	@SuppressWarnings("unchecked")
	public List<CRequest> findAllApprovedInTimeInterval(final Date dateFrom, final Date dateTo, final CUser user) {
		// create query
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from t_request where (fk_user_owner = ?) and (fk_status = ?) and not(c_date_to < ? or c_date_from > ? )");

		// sets parameters
		query.setParameter(0, user.getId());
		query.setParameter(1, IRequestStatusConstant.APPROVED);
		query.setParameter(2, dateFrom);
		query.setParameter(3, dateTo);

		query.addEntity(CRequest.class);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CRequest> findAllApprovedUserRequestContainsDate(final Date date, final Long userId) {
		Calendar checkDay = Calendar.getInstance();
		checkDay.setTime(date);
		String sDay = CDateUtils.convertToTimestampString(checkDay);

		// create query
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select * from t_request where (fk_user_owner = ?) and (fk_status = ?) and (c_date_from <= date_trunc('day',timestamp '" + sDay
						+ "') and c_date_to >= date_trunc('day', timestamp '" + sDay + "') )");

		// sets parameters
		query.setParameter(0, userId);
		query.setParameter(1, IRequestStatusConstant.APPROVED);

		query.addEntity(CRequest.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CRequest> findAllCreatedUserRequestForHomeOfficeContainsDate(final Date date, final Long userId) {
		Calendar checkDay = Calendar.getInstance();
		checkDay.setTime(date);
		String sDay = CDateUtils.convertToTimestampString(checkDay);

		// create query
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select * from t_request where (fk_user_owner = ?) and (fk_status = ?) and (fk_request_type = ?) and (c_date_from <= date_trunc('day',timestamp '" + sDay
						+ "') and c_date_to >= date_trunc('day', timestamp '" + sDay + "') )");

		// sets parameters
		query.setParameter(0, userId);
		query.setParameter(1, IRequestStatusConstant.CREATED);
		query.setParameter(2, IRequestTypes.ID_WAH); // home office
		
		query.addEntity(CRequest.class);

		return query.list();
	}
	
	/**
	 * @see IRequestDao#findAllApprovedUserRequestContainsDate(Date, CUser)
	 */
	@SuppressWarnings("unchecked")
	public List<CRequest> findAllApprovedUserRequestContainsDate(final Date date, final CUser user) {
		Calendar checkDay = Calendar.getInstance();
		checkDay.setTime(date);
		String sDay = CDateUtils.convertToTimestampString(checkDay);

		// create query
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select * from t_request where (fk_user_owner = ?) and (fk_status = ?) and (c_date_from <= date_trunc('day',timestamp '" + sDay
						+ "') and c_date_to >= date_trunc('day', timestamp '" + sDay + "') )");

		// sets parameters
		query.setParameter(0, user.getId());
		query.setParameter(1, IRequestStatusConstant.APPROVED);

		query.addEntity(CRequest.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CRequest> findApprovedClientRequests4Day(Long clientId, Calendar checkDay) {
		// create query
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from t_request " + "where fk_status = 3 and fk_client= ? " + "and c_date_from <= ? "
				+ "and coalesce(c_date_last_gen_holiday,c_date_from-interval '1 day') <> c_date_to " + "and coalesce(c_date_last_gen_holiday,c_date_from-interval '1 day') < ? "
						+ "order by c_date_from asc");

		// sets parameters
		query.setParameter(0, clientId);
		query.setParameter(1, checkDay);
		query.setParameter(2, checkDay);

		query.addEntity(CRequest.class);

		return query.list();
	}

	/**
	 * metóda vráti TRUE ak existuje žiadosť na dovolenku pre konkrétny deň, FALSE
	 * ak neexistuje
	 */
	public Boolean existsClientRequestsHoliday4Day(final Long userId, Calendar checkDay) {
		String sDay = CDateUtils.convertToTimestampString(checkDay);

		// fk_request_type = 1 - dovolenka
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from t_request where fk_request_type = 1 and fk_status in (1 , 3) and fk_user_owner = "
				+ "? and date_trunc('day', timestamp '" + sDay + "') between c_date_from and c_date_to");

		// sets parameters
		query.setParameter(0, userId);

		query.addEntity(CRequest.class);

		if (query.list().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * metóda vráti TRUE ak existuje žiadosť na dovolenku pre konkrétny deň, FALSE
	 * ak neexistuje
	 */
	public Boolean existsClientRequestsHoliday4DayLastGen(final Long userId, Calendar checkDay) {
		String sDay = CDateUtils.convertToTimestampString(checkDay);

		// fk_request_type = 1 - dovolenka
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from t_request where fk_request_type = 1 and fk_status in (1 , 3) and fk_user_owner = "
				+ "? and date_trunc('day', timestamp '" + sDay + "') between c_date_from and c_date_to and c_date_last_gen_holiday >= date_trunc('day', timestamp '" + sDay + "')");

		// sets parameters
		query.setParameter(0, userId);

		query.addEntity(CRequest.class);

		if (query.list().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @see
	 */
	@SuppressWarnings("unchecked")
	public List<CRequest> findAll(final Long clientId, final Long userId, final IFilterCriteria filter, final int from, final int count) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequest.class);
		this.prepareCriteria(clientId, userId, (IRequestsBrwFilterCriteria) filter, criteria);

		// ordering
		criteria.createAlias("owner", "o");
		criteria.addOrder(Order.desc(DATE_FROM));
		criteria.addOrder(Order.asc("o.surname"));
		criteria.addOrder(Order.asc("o.name"));
		criteria.addOrder(Order.asc("type"));
		criteria.addOrder(Order.asc("status"));

		criteria.setFirstResult(from);
		criteria.setFetchSize(count);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CRequest> findAll(final Long clientId, final Long userId, final IFilterCriteria filter, final int from, final int count, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequest.class);
		this.prepareCriteria(clientId, userId, (IRequestsBrwFilterCriteria) filter, criteria);
		this.prepareOrderAndCount(criteria, from, count, sortProperty, sortAsc);
		return criteria.list();
	}

	public Long count(final Long clientId, final Long userId, final IFilterCriteria filter) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CRequest.class);
		this.prepareCriteria(clientId, userId, (IRequestsBrwFilterCriteria) filter, criteria);
		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	/**
	 * Prepares criteria for my requests brw
	 * 
	 * @param user
	 * @param filter
	 */
	private void prepareCriteria(final Long clientId, final Long userId, final IRequestsBrwFilterCriteria filter, final Criteria criteria) {
		criteria.add(Property.forName("client.id").eq(clientId));

		// filter pre stav
		if ((filter.getStateId() != null) && (filter.getStateId().longValue() >= 0)) {
			criteria.add(Property.forName("status.id").eq(filter.getStateId()));
		}

		// filter pre typ
		if ((filter.getTypeId() != null) && (filter.getTypeId().longValue() >= 0)) {
			criteria.add(Property.forName("type.id").eq(filter.getTypeId()));
		}

		// filtre pre date from a date to
		if ((filter.getDateFrom() != null) || (filter.getDateTo() != null)) {
			Criterion criterion = null;
			if ((filter.getDateFrom() != null) && (filter.getDateTo() != null)) {
				final Calendar startTime = Calendar.getInstance();
				startTime.setTime(filter.getDateFrom());
				CTimeUtils.convertToStartDate(startTime);

				final Calendar stopTime = Calendar.getInstance();
				stopTime.setTime(filter.getDateTo());
				CTimeUtils.convertToEndDate(stopTime);

				final Criterion criterionGE = Restrictions.ge(DATE_TO, startTime);
				final Criterion criterionLE = Restrictions.le(DATE_FROM, stopTime);

				criteria.add(criterionGE);
				criteria.add(criterionLE);

			} else if (filter.getDateFrom() != null) {
				// dateTo je prazdny
				final Calendar startTime = Calendar.getInstance();
				startTime.setTime(filter.getDateFrom());
				CTimeUtils.convertToStartDate(startTime);

				criterion = Restrictions.ge(DATE_TO, startTime);

				criteria.add(criterion);
			} else if (filter.getDateTo() != null) {
				// dateFrom je prazdny
				final Calendar stopTime = Calendar.getInstance();
				stopTime.setTime(filter.getDateTo());
				CTimeUtils.convertToEndDate(stopTime);

				final Criterion criterionLE = Restrictions.le(DATE_TO, stopTime);
				final Criterion criterionNull = Restrictions.isNull(DATE_TO);
				criterion = Restrictions.or(criterionLE, criterionNull);

				criteria.add(criterion);
			}
		}

		if (filter instanceof CSubordinateRequestsBrwFilterCriteria) {
			// filter len pre subordinate - strom podriadenych
			final CSubordinateRequestsBrwFilterCriteria subordinateFilter = (CSubordinateRequestsBrwFilterCriteria) filter;
			if (null != subordinateFilter.getEmplyees()) {
				final Set<Long> employees = subordinateFilter.getEmplyees();
				if (employees.isEmpty()) {
					// kedze nemam nic zaskrtnute, nezobrazim ziadny zaznam,
					// pretoze dateFrom nikdy nebude null...
					criteria.add(Property.forName(DATE_FROM).isNull());
				} else {
					criteria.add(Restrictions.in("owner.id", employees));
				}
			}
		} else {
			criteria.add(Property.forName("owner.id").eq(userId));
		}

	}

	@Override
	public List<CRequest> findAllApprovedAndCreatedHolidayInTimeInterval(Date dateFrom, Date dateTo, CUser user) {
		// SED-841- create query - request type 1 = Dovolenka, 2 = Práceneschopnosť, 3 =
		// Náhradné voľno, 5 = Prekážky v práci
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select * from t_request where (fk_user_owner = ?) and (fk_request_type in (1, 2, 3, 5)) and (fk_status in (?,?)) and not(c_date_to < ? or c_date_from > ? )");

		// sets parameters
		query.setParameter(0, user.getId());
		query.setParameter(1, IRequestStatusConstant.CREATED);
		query.setParameter(2, IRequestStatusConstant.APPROVED);
		query.setParameter(3, dateFrom);
		query.setParameter(4, dateTo);

		query.addEntity(CRequest.class);

		return query.list();
	}

	@Override
	public List<CRequest> findAllApprovedHolidayInTimeInterval(Date dateFrom, Date dateTo, CUser user) {
		// SED-841- create query - request type 1 = Dovolenka, 2 = Práceneschopnosť, 3 =
		// Náhradné voľno, 5 = Prekážky v práci
		final SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select * from t_request where (fk_user_owner = ?) and (fk_request_type in (1, 2, 3, 5)) and (fk_status in (?)) and not(c_date_to < ? or c_date_from > ? )");

		// sets parameters
		query.setParameter(0, user.getId());
		query.setParameter(1, IRequestStatusConstant.APPROVED);
		query.setParameter(2, dateFrom);
		query.setParameter(3, dateTo);

		query.addEntity(CRequest.class);

		return query.list();
	}

	@Override
	public Boolean existsClientRequestsHomeOffice4Today(Long userId, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		String checkDate = CDateUtils.convertToTimestampString(cal);

		// create query
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select * from t_request where date_trunc('day', timestamp '" + checkDate
				+ "') between c_date_from and c_date_to and fk_request_type = (select pk_id from t_ct_request_type where c_code = 'WAH') and fk_status = 3 and fk_user_owner = ? ");

		// sets parameters
		query.setParameter(0, userId);

		query.addEntity(CRequest.class);

		if (query.list().isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean existsClientRequestsHomeOfficeInInterval(Long userId, Long clientId, Date dateFrom, Date dateTo, List<CHoliday> holidays) {
		/*
		 * do selectu musím posielať len pracovné dni, preto si k dateFrom pripočítavam
		 * dni až kým nenájdem pracovný a od dateTo odpočítavam
		 */

		Date dateFromFirstWorkingDay = (Date) dateFrom.clone();

		while (!CDateServerUtils.isWorkingDayWithCheckHolidays(dateFromFirstWorkingDay, holidays)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFromFirstWorkingDay);
			cal.add(Calendar.DATE, 1);

			dateFromFirstWorkingDay = cal.getTime();
		}

		Calendar firstWorkingDay = Calendar.getInstance();
		firstWorkingDay.setTime(dateFromFirstWorkingDay);

		String firstWorkingDayStr = CDateUtils.convertToDateString(firstWorkingDay);

		// create query
		StringBuilder sq = new StringBuilder();

		sq.append("select COALESCE((select max(rq.c_date_to) ");
		sq.append("from t_request rq ");
		sq.append("where rq.fk_user_owner = r.fk_user_owner ");
		sq.append("and rq.fk_client = r.fk_client ");
		sq.append("and rq.fk_request_type = r.fk_request_type ");
		sq.append("and rq.fk_status = r.fk_status ");
		sq.append("and to_date(to_char(rq.c_date_from, 'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') > ");
		sq.append("to_date(to_char(r.c_date_to, 'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') ");
		sq.append("and date_part('day', ");
		sq.append("to_date(to_char(rq.c_date_from, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') - ");
		sq.append("(to_date(to_char(r.c_date_to, 'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') + interval '1 day')) = ");
		sq.append("COALESCE((select SUM(rs.c_number_of_working_days) ");
		sq.append("from t_request rs ");
		sq.append("where rs.fk_user_owner = r.fk_user_owner ");
		sq.append("and rs.fk_client = r.fk_client ");
		sq.append("and rs.fk_request_type = ");
		sq.append("r.fk_request_type ");
		sq.append("and rs.fk_status = r.fk_status ");
		sq.append("and to_date(to_char(rs.c_date_from, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') > ");
		sq.append("to_date(to_char(r.c_date_to, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') ");
		sq.append("and to_date(to_char(rq.c_date_from, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') > ");
		sq.append("to_date(to_char(rs.c_date_to, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY')), ");
		sq.append("0) + ");
		sq.append("(select count(vd.*) ");
		sq.append("from (select p.datum ");
		sq.append("from (select CAST(i AS date) datum ");
		sq.append("from generate_series(to_date(to_char(r.c_date_to, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') + ");
		sq.append("interval ");
		sq.append("'1 day', ");
		sq.append("to_date(to_char(rq.c_date_from, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') - ");
		sq.append("interval '1 day', ");
		sq.append("CAST('1 day' AS interval) ");
		sq.append(") i) p ");
		sq.append("where trim(to_char(p.datum, 'day')) in ");
		sq.append("('saturday', 'sunday') ");
		sq.append("UNION ALL ");
		sq.append("select to_date(to_char(h.c_day, 'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') datum ");
		sq.append("from t_ct_holiday h ");
		sq.append("where h.c_flag_valid = true ");
		sq.append("and to_date(to_char(h.c_day, 'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') between ");
		sq.append("to_date(to_char(r.c_date_to, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') + interval ");
		sq.append("'1 day' ");
		sq.append("and to_date(to_char(rq.c_date_from, ");
		sq.append("'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') - interval ");
		sq.append("'1 day' ");
		sq.append("and r.fk_client = h.fk_client) vd)), ");
		sq.append("to_date(to_char(r.c_date_to, 'DD.MM.YYYY'), 'DD.MM.YYYY')) c_date_to, ");
		sq.append("r.c_date_from, ");
		sq.append("r.pk_id, ");
		sq.append("r.fk_request_type, ");
		sq.append("r.fk_user_owner, ");
		sq.append("r.fk_status, ");
		sq.append("r.fk_client, ");
		sq.append("r.fk_reason ");
		sq.append("from t_request r ");
		sq.append("where (r.fk_client = ? ");
		sq.append(") ");
		sq.append("and (r.fk_status = ? ");
		sq.append(") ");
		sq.append("and r.fk_user_owner = ? ");
		sq.append("and to_date('" + firstWorkingDayStr + "', 'DD.MM.YYYY') ");
		sq.append("between to_date(to_char(r.c_date_from, 'DD.MM.YYYY'), ");
		sq.append("'DD.MM.YYYY') ");
		sq.append("and to_date(to_char(r.c_date_to, 'DD.MM.YYYY'), 'DD.MM.YYYY') ");
		sq.append("and r.fk_request_type = 7;");

		/*
		 * do selectu pošlem prvý pracovný deň a select mi v stĺpci c_date_to vráti
		 * posledný pracovný deň na ktorý je vytvorená žiadosť na prácu z domu, s tým že
		 * medzi c_date_from a c_date_to je na každý deň vytvorená žiadosť práca z domu
		 */
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sq.toString());

		query.setParameter(0, clientId);
		query.setParameter(1, IRequestStatusConstant.APPROVED);
		query.setParameter(2, userId);
		query.addEntity(CRequestForEmail.class);

		List<CRequestForEmail> data = query.list();

		if (data.isEmpty()) {
			return Boolean.FALSE;
		} else {
			CRequestForEmail entity = data.get(0);

			Calendar lastRequestDayFromSelect = entity.getDateTo();

			Date dateToLastWorkingDay = (Date) dateTo.clone();
			Calendar lastWorkingDay = Calendar.getInstance();
			lastWorkingDay.setTime(dateToLastWorkingDay);

			while (!CDateServerUtils.isWorkingDayWithCheckHolidays(dateToLastWorkingDay, holidays)) {
				lastWorkingDay = Calendar.getInstance();
				lastWorkingDay.setTime(dateToLastWorkingDay);
				lastWorkingDay.add(Calendar.DATE, -1);

				dateToLastWorkingDay = lastWorkingDay.getTime();
			}

			if (lastWorkingDay.getTimeInMillis() <= lastRequestDayFromSelect.getTimeInMillis()) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
	}
}