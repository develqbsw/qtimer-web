package sk.qbsw.sed.server.dao.hibernate;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CGetSumAndAverageTimeForUser;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampCreateBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.ITimeStampBrwFilterCriteria;
import sk.qbsw.sed.server.dao.IViewTimeStampDao;
import sk.qbsw.sed.server.model.brw.CViewTimeStamp;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;

/**
 * Dao for accessing view V_ORGANIZATION_TREE.
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Repository
public class CViewTimeStampDao extends AHibernateDao<CViewTimeStamp> implements IViewTimeStampDao {

	private static final String USER_ID = "userId";
	private static final String TIME_FROM = "timeFrom";
	private static final String TIME_TO = "timeTo";
	private static final String ACTIVITY_ID = "activityId";
	private static final String FLAG_WORKING = "flagWorking";
	
	private static final String DD_MM_YYYY = "dd.MM.yyyy";

	@SuppressWarnings("unchecked")
	@Override
	public List<CViewTimeStamp> findAll(final CUser user, final IFilterCriteria filter, final int from, final int count) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewTimeStamp.class);
		this.prepareCriteria(user, (ITimeStampBrwFilterCriteria) filter, criteria);
		criteria.addOrder(Order.asc(TIME_FROM));
		criteria.addOrder(Order.asc(TIME_TO));
		criteria.addOrder(Order.asc("userName"));
		criteria.addOrder(Order.asc("userSurname"));
		criteria.setFirstResult(from);
		criteria.setFetchSize(count);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CViewTimeStamp> findAll(final CUser user, final IFilterCriteria filter, final int from, final int count, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewTimeStamp.class);
		this.prepareCriteria(user, (ITimeStampBrwFilterCriteria) filter, criteria);

		if (sortProperty != null) {
			criteria.addOrder(sortAsc ? Order.asc(sortProperty) : Order.desc(sortProperty));
		}

		criteria.setFirstResult(from);
		criteria.setMaxResults(count);
		criteria.setFetchSize(count);
		return criteria.list();
	}

	@Override
	public Long count(final CUser user, final IFilterCriteria filter) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewTimeStamp.class);
		this.prepareCriteria(user, (ITimeStampBrwFilterCriteria) filter, criteria);
		criteria.setProjection(Projections.count("id"));

		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CViewTimeStamp> findAll(final CUser user, final IFilterCriteria filter) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewTimeStamp.class);
		this.prepareCriteria(user, (ITimeStampBrwFilterCriteria) filter, criteria);
		criteria.addOrder(Order.asc(TIME_FROM));
		criteria.addOrder(Order.asc(TIME_TO));
		return criteria.list();
	}

	@Override
	public List<CGetSumAndAverageTimeForUser> getSumAndAvgTimeInTimeInterval(final CSubrodinateTimeStampBrwFilterCriteria filter) {

		String sQuery = "SELECT s_s.c_user_name,s_s.c_user_surname,s_s.sum,a_s.days " + "FROM "
				+ "(SELECT  c_user_name, c_user_surname, coalesce(extract (epoch from ( sum(date_trunc('minute',coalesce(c_time_to,now())-c_time_from)+interval '1 minute'))),0) sum "
				+ "FROM v_timestamps r " + WHERE(filter) + "GROUP BY c_user_name, c_user_surname) AS s_s " + "JOIN " + "(SELECT c_user_name, c_user_surname, count(datum) days " + "FROM "
				+ "(SELECT c_user_name, c_user_surname,  (c_date_day||'-'||c_date_month||'-'||c_date_year)datum " + "FROM v_timestamps r " + WHERE(filter)
				+ "GROUP BY c_user_name, c_user_surname,datum )avg " + "GROUP BY c_user_name, c_user_surname) AS a_s "
				+ "ON s_s.c_user_name = a_s.c_user_name AND s_s.c_user_surname = a_s.c_user_surname " + "ORDER BY  s_s.c_user_surname,s_s.c_user_name;";

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		setParams(filter, query);

		final List<Object[]> data = query.list();

		List<CGetSumAndAverageTimeForUser> result = new ArrayList<>();

		for (Object[] o : data) {
			String name = (String) o[0];
			String surname = (String) o[1];
			Long sum = (long) ((Double) o[2]).doubleValue() * 1000; // in milis
			Long count = ((BigInteger) o[3]).longValue();

			if (count != 0) {

				CGetSumAndAverageTimeForUser element = new CGetSumAndAverageTimeForUser(name, surname, sum, count);

				result.add(element);
			}
		}
		return result;
	}

	@Override
	public Long geCountOfWorkedDaysInTimeInterval(final CSubrodinateTimeStampBrwFilterCriteria filter) {

		String sQuery = " SELECT count(datum) days FROM( " + " SELECT  (c_date_day||'-'||c_date_month||'-'||c_date_year)datum " + " FROM v_timestamps r " + WHERE(filter) + " group by datum "
				+ " )avg ";

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		setParams(filter, query);

		Object data = null;
		try {
			data = query.uniqueResult();

			return ((BigInteger) data).longValue();
		} catch (HibernateException e) { // ak selekt nic nevrati, tak funkcia vrati 0
			Logger.getLogger(this.getClass()).info(e);
			return 0l;
		}
	}

	/**
	 * Prepares criteria for browser
	 * 
	 */
	private void prepareCriteria(final CTimeStampCreateBrwFilterCriteria filter, final Criteria criteria) {
		final Calendar from = Calendar.getInstance();
		from.setTime(filter.getDateFrom());
		CTimeUtils.convertToStartTime(from);

		final Calendar to = Calendar.getInstance();
		to.setTime(filter.getDateTo());
		CTimeUtils.convertToEndTime(to);

		criteria.add(Property.forName(TIME_FROM).ge(from));
		criteria.add(Restrictions.or(Restrictions.le(TIME_TO, to), Restrictions.isNull(TIME_TO)));
		criteria.add(Property.forName(USER_ID).eq(filter.getUserId()));
	}

	/**
	 * Prepares criteria for Browser
	 * 
	 * @param filter
	 * @param criteria
	 */
	private void prepareCriteria(final CUser user, final ITimeStampBrwFilterCriteria filter, final Criteria criteria) {
		// filter activity
		if (filter.getActivityId() != null) {
			if (filter.getActivityId().longValue() >= IActivityConstant.NOT_WORK_MINCHECK_VALUE.longValue()) {
				criteria.add(Property.forName(ACTIVITY_ID).eq(filter.getActivityId()));
			} else {
				if (ISearchConstants.ALL.equals(filter.getActivityId())) {
					// no filter for activity
				} else if (ISearchConstants.ALL_NON_WORKING.equals(filter.getActivityId())) {
					criteria.add(Property.forName(FLAG_WORKING).eq(Boolean.FALSE));
				} else if (ISearchConstants.ALL_WORKING.equals(filter.getActivityId())) {
					criteria.add(Property.forName(FLAG_WORKING).eq(Boolean.TRUE));
				} else if (ISearchConstants.ALL_ALERTNESS_INTERACT_WORK.equals(filter.getActivityId())) { // ak používateľ vybral vo filtri Pohotovosť/Zásah
					final Criterion activityCriterion = Restrictions.or(
							Property.forName(ACTIVITY_ID).eq(IActivityConstant.NOT_WORK_ALERTNESSWORK), // Pohotovosť
							Property.forName(ACTIVITY_ID).eq(IActivityConstant.NOT_WORK_INTERACTIVEWORK)); // Zásah
					criteria.add(activityCriterion);
				}
			}
		}
		// filter project
		if (filter.getProjectId() != null) {
			if (filter.getProjectId().longValue() >= 0) {
				criteria.add(Property.forName("projectId").eq(filter.getProjectId()));
			}
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

				final Criterion criterionBetweenFrom = Restrictions.between(TIME_FROM, startTime, stopTime);
				criterion = criterionBetweenFrom;
			} else if (filter.getDateFrom() != null) {
				// dateTo je prazdny
				final Calendar startTime = Calendar.getInstance();
				startTime.setTime(filter.getDateFrom());
				CTimeUtils.convertToStartDate(startTime);

				criterion = Restrictions.ge(TIME_FROM, startTime);
			} else if (filter.getDateTo() != null) {
				// dateFrom je prazdny
				final Calendar stopTime = Calendar.getInstance();
				stopTime.setTime(filter.getDateTo());
				CTimeUtils.convertToEndDate(stopTime);

				final Criterion criterionLE = Restrictions.le(TIME_TO, stopTime);
				final Criterion criterionNull = Restrictions.isNull(TIME_TO);
				criterion = Restrictions.or(criterionLE, criterionNull);
			}

			criteria.add(criterion);
		}

		// filter pre search text
		if (StringUtils.isNotBlank(filter.getSearchText())) {
			final Criterion activity = Restrictions.sqlRestriction("lower(unaccent(c_activity_name)) like lower(unaccent('%" + filter.getSearchText() + "%')) ");
			final Criterion project = Restrictions.sqlRestriction("lower(unaccent(c_project_name)) like lower(unaccent('%" + filter.getSearchText() + "%')) ");
			final Criterion phase = Restrictions.sqlRestriction("lower(unaccent(c_phase)) like lower(unaccent('%" + filter.getSearchText() + "%')) ");
			final Criterion note = Restrictions.sqlRestriction("lower(unaccent(c_note)) like lower(unaccent('%" + filter.getSearchText() + "%')) ");

			final Criterion criterion1 = Restrictions.or(phase, note);
			final Criterion criterion2 = Restrictions.or(activity, project);
			final Criterion criterion3 = Restrictions.or(criterion1, criterion2);

			criteria.add(criterion3);
		}

		// filter len pre subordinate - strom podriadenych
		if (filter instanceof CSubrodinateTimeStampBrwFilterCriteria) {
			final CSubrodinateTimeStampBrwFilterCriteria subordinateFilter = (CSubrodinateTimeStampBrwFilterCriteria) filter;
			if (null != subordinateFilter.getEmplyees()) {
				final Set<Long> employees = subordinateFilter.getEmplyees();
				if (employees.isEmpty()) {
					// kedze nemam nic zaskrtnute, nezobrazim ziadny zaznam, pretoze timeFrom nikdy nebude null...
					criteria.add(Property.forName(TIME_FROM).isNull());
				} else {
					if (!employees.isEmpty()) {
						criteria.add(Restrictions.in(USER_ID, employees));
					} else {
						// kedze nemam ziadnych podriadenych, nezobrazim nic pretoze timeFrom nikdy nebude null...
						criteria.add(Property.forName(TIME_FROM).isNull());
					}
				}
			}
		} else {
			criteria.add(Property.forName(USER_ID).eq(user.getId()));
		}
	}

	private void setParams(final CSubrodinateTimeStampBrwFilterCriteria filter, Query query) {
		// filter activity
		if (filter.getActivityId() != null) {
			if (filter.getActivityId().longValue() >= IActivityConstant.NOT_WORK_MINCHECK_VALUE.longValue()) {
				query.setLong(ACTIVITY_ID, filter.getActivityId());
			} else {
				if (ISearchConstants.ALL.equals(filter.getActivityId())) {
					// no filter for activity
				} else if (ISearchConstants.ALL_NON_WORKING.equals(filter.getActivityId())) {
					query.setBoolean(FLAG_WORKING, false);
				} else if (ISearchConstants.ALL_WORKING.equals(filter.getActivityId())) {
					query.setBoolean(FLAG_WORKING, true);
				}
			}
		}
		// filter project
		if (filter.getProjectId() != null) {
			if (filter.getProjectId().longValue() >= 0) {
				query.setLong("projectId", filter.getProjectId());
			}
		}

		// filtre pre date from a date to
		if ((filter.getDateFrom() != null) || (filter.getDateTo() != null)) {

			if ((filter.getDateFrom() != null) && (filter.getDateTo() != null)) {
				final Calendar startTime = Calendar.getInstance();
				startTime.setTime(filter.getDateFrom());
				CTimeUtils.convertToStartDate(startTime);

				final Calendar stopTime = Calendar.getInstance();
				stopTime.setTime(filter.getDateTo());
				CTimeUtils.convertToEndDate(stopTime);

				query.setCalendar(TIME_FROM, startTime);
				query.setCalendar(TIME_TO, stopTime);

			} else if (filter.getDateFrom() != null) {
				// dateTo je prazdny
				final Calendar startTime = Calendar.getInstance();
				startTime.setTime(filter.getDateFrom());
				CTimeUtils.convertToStartDate(startTime);

				// now()
				final Calendar stopTime = Calendar.getInstance();
				CTimeUtils.convertToEndDate(stopTime);

				query.setCalendar(TIME_FROM, startTime);
				query.setCalendar(TIME_TO, stopTime);
			} else if (filter.getDateTo() != null) {

				// dateFrom je prazdny, vlozim now()
				final Calendar startTime = Calendar.getInstance();
				CTimeUtils.convertToStartDate(startTime);

				//
				final Calendar stopTime = Calendar.getInstance();
				stopTime.setTime(filter.getDateTo());
				CTimeUtils.convertToEndDate(stopTime);

				query.setCalendar(TIME_FROM, startTime);
				query.setCalendar(TIME_TO, stopTime);
			}

		}

		// filter pre search text
		if (StringUtils.isNotBlank(filter.getSearchText())) {

			// where activityName OR projectName OR phase OR note LIKE %filter.getSearchText()%

			query.setString("searchText", "%" + filter.getSearchText().toLowerCase() + "%");

		}

		query.setParameterList("users", filter.getEmplyees());
	}

	private String WHERE(final CSubrodinateTimeStampBrwFilterCriteria filter) {

		String WHERE = " WHERE r.fk_user IN (:users) " + " AND c_time_from >= :timeFrom and coalesce(r.c_time_to,now())<= :timeTo "
				+ " AND (r.c_time_to is not null or date_trunc('day',r.c_time_from)=current_date) and date_trunc('day',r.c_time_from)<=now() ";

		String ACTIVITY = " AND fk_activity_numeric = :activityId ";
		String FLAG = " AND c_flag_working = :flagWorking ";
		String PROJECT = " AND fk_project_numeric = :projectId ";
		String SEARCH = " AND (lower(c_activity_name) LIKE :searchText OR lower(c_project_name) LIKE :searchText OR lower(c_phase) LIKE :searchText OR lower(c_note) LIKE :searchText) ";

		// filter activity
		if (filter.getActivityId() != null) {
			if (filter.getActivityId().longValue() >= IActivityConstant.NOT_WORK_MINCHECK_VALUE.longValue()) {

				WHERE = WHERE + ACTIVITY;
			} else {
				if (ISearchConstants.ALL.equals(filter.getActivityId())) {
					// no filter for activity
				} else {
					WHERE = WHERE + FLAG;
				}
			}
		}
		// filter project
		if (filter.getProjectId() != null) {
			if (filter.getProjectId().longValue() >= 0) {
				WHERE = WHERE + PROJECT;
			}
		}

		// filter pre search text
		if (StringUtils.isNotBlank(filter.getSearchText())) {

			// where activityName OR projectName OR phase OR note LIKE %filter.getSearchText()%

			WHERE = WHERE + SEARCH;

		}

		return WHERE;

	}

	/**
	 * @see IViewTimeStampDao#getDurationOfActivityInMinutes
	 */
	@Override
	public Long getDurationOfActivityInMinutes(final Long userId, final Long activityId, final int year, final Long timestampId) {
		String sQuery = "select sum(c_duration_minutes) from v_timestamps where fk_user = " + userId + " and fk_activity = " + activityId + " AND c_date_year = " + year;

		if (timestampId != null) {
			sQuery += " AND pk_id != " + timestampId;
		}

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);

		Object data = null;
		try {
			data = query.uniqueResult();

			if (data == null) {
				return 0l;
			}

			return ((Double) data).longValue();
		} catch (HibernateException e) { // ak selekt nic nevrati, tak funkcia vrati 0
			Logger.getLogger(this.getClass()).info(e);
			return 0l;
		}
	}

	/**
	 * metóda overí, či existuje pre používateľa záznam dovolenky pre konkrétny deň
	 */
	public Boolean existHolidayRecordForDay(final CUser user, final Calendar cal, final Long id) {
		// id -3L = dovolenka
		final Long activityId = -3L;

		String sQuery;

		if (id == null) // pridanie
		{
			sQuery = "select * from v_timestamps where fk_user = " + user.getId() + " and fk_activity = " + activityId + " AND c_date_year = " + cal.get(Calendar.YEAR) + " AND c_date_month = "
					+ (cal.get(Calendar.MONTH) + 1) + " AND c_date_day = " + cal.get(Calendar.DAY_OF_MONTH);
		} else // editácia
		{
			sQuery = "select * from v_timestamps where fk_user = " + user.getId() + " and fk_activity = " + activityId + " AND c_date_year = " + cal.get(Calendar.YEAR) + " AND c_date_month = "
					+ (cal.get(Calendar.MONTH) + 1) + " AND c_date_day = " + cal.get(Calendar.DAY_OF_MONTH) + " AND pk_id != " + id;
		}

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);

		Object data = null;
		try {
			data = query.uniqueResult();

			if (data == null) {
				return Boolean.FALSE;
			}

		} catch (HibernateException e) {
			Logger.getLogger(this.getClass()).info(e);
		}
		return Boolean.TRUE;
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public List<CViewTimeStamp> getHolidayRecordsFromInterval(final CUser user, final Calendar dateFrom, Calendar dateTo) {
		// id -3L = dovolenka
		final Long activityId = -3L;

		Calendar dateFromClone = (Calendar) dateFrom.clone();
		Calendar dateToClone = (Calendar) dateTo.clone();

		CTimeUtils.convertToStartDate(dateFromClone);
		CTimeUtils.convertToEndDate(dateToClone);

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewTimeStamp.class);
		criteria.add(Property.forName(USER_ID).eq(user.getId()));
		criteria.add(Property.forName(ACTIVITY_ID).eq(activityId));

		Criterion criterionTf = null;
		Criterion criterionTt = null;

		criterionTf = Restrictions.ge(TIME_FROM, dateFromClone);
		criterionTt = Restrictions.le(TIME_TO, dateToClone);

		criteria.add(criterionTf);
		criteria.add(criterionTt);

		List<CViewTimeStamp> data = null;
		try {
			data = criteria.list();

		} catch (HibernateException e) {
			Logger.getLogger(this.getClass()).info(e);
		}

		return data;

	}

	@SuppressWarnings("unchecked")
	public List<CViewTimeStamp> getDoctorVisitTimeStamps(final Date dateFrom, final Long clientId) {
		SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
		String date = sdf.format(dateFrom);

		// -10 = návšteva lekára, -11 = sprevádzanie rodinného príslušníka, -12 = prenatálna lekárska starostlivosť create query
		StringBuilder sq = new StringBuilder();

		sq.append("select * from public.v_timestamps ");
		sq.append("where fk_activity in (-10, -11, -12) ");
		sq.append("and to_date(to_char(c_time_from, 'DD.MM.YYYY'), 'DD.MM.YYYY') = to_date('" + date + "', 'DD.MM.YYYY') ");
		sq.append("and fk_client = " + clientId);

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sq.toString());

		query.addEntity(CViewTimeStamp.class);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CViewTimeStamp> getHomeOfficeTimeStamps(final Date dateFrom, final Long clientId) {
		SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
		String date = sdf.format(dateFrom);

		StringBuilder sq = new StringBuilder();

		sq.append("select * from public.v_timestamps ");
		sq.append("where c_flag_home_office = true ");
		sq.append("and to_date(to_char(c_time_from, 'DD.MM.YYYY'), 'DD.MM.YYYY') = to_date('" + date + "', 'DD.MM.YYYY') ");
		sq.append("and fk_client = " + clientId);

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sq.toString());

		query.addEntity(CViewTimeStamp.class);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CViewTimeStamp> getNotHomeOfficeTimeStamps(final Date dateFrom, final Long clientId) {
		SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
		String date = sdf.format(dateFrom);

		StringBuilder sq = new StringBuilder();

		sq.append("select * from public.v_timestamps ");
		sq.append("where c_flag_home_office = false and c_flag_working = true ");
		sq.append("and to_date(to_char(c_time_from, 'DD.MM.YYYY'), 'DD.MM.YYYY') = to_date('" + date + "', 'DD.MM.YYYY') ");
		sq.append("and fk_client = " + clientId);

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sq.toString());

		query.addEntity(CViewTimeStamp.class);

		return query.list();
	}
	
}
