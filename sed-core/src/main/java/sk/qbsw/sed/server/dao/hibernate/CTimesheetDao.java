package sk.qbsw.sed.server.dao.hibernate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.client.model.CStatsFilter;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.ISearchConstants;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.server.dao.ITimesheetRecordDao;
import sk.qbsw.sed.server.model.codelist.CTimeSheetRecordStatus;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.service.CTimeUtils;

@Repository
public class CTimesheetDao extends AHibernateDao<CTimeSheetRecord> implements ITimesheetRecordDao {

	private static final String OWNER_ID = "owner.id";
	private static final String VALID = "valid";
	private static final String TIME_FROM = "timeFrom";
	private static final String USER_ID = "userId";
	private static final String WORKING = "working";
	private static final String LAST_WORKING = "lastWorking";
	private static final String TIME_TO = "timeTo";

	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findAllOverlapping(final Calendar timeFrom, final Calendar timeTo, final Long userId) {
		// create query
		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(
				"select * from t_time_sheet_record where  c_flag_valid = true and fk_user_owner = ? and (? between c_time_from and coalesce(c_time_to,to_timestamp(to_char(c_time_from,'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI')) or ? between c_time_from and coalesce(c_time_to,to_timestamp(to_char(c_time_from,'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI')) or (?<c_time_from and ?>coalesce(c_time_to,to_timestamp(to_char(c_time_from,'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI'))))");

		// sets parameters
		query.setParameter(0, userId);
		query.setParameter(1, timeFrom);
		query.setParameter(2, timeTo);
		query.setParameter(3, timeFrom);
		query.setParameter(4, timeTo);

		query.addEntity(CTimeSheetRecord.class);

		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLast(final Long userId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName("last").eq(Boolean.TRUE));

		criteria.setFirstResult(0);
		criteria.setFetchSize(1);

		final List<CTimeSheetRecord> list = criteria.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			return findLastWithoutUsingFlag(userId);
		}
	}

	/**
	 * tato metodka sa vzdy pouzivala pred optimalizaciou a select je strasne
	 * pomaly. stale sa pouziva vo vynimocnych pripadoch.
	 */
	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastWithoutUsingFlag(final Long userId) {
		Logger.getLogger(this.getClass()).info("findLastWithoutUsingFlag userId: " + userId);
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.addOrder(Order.desc(TIME_FROM));

		criteria.setFirstResult(0);
		criteria.setFetchSize(1);

		final List<CTimeSheetRecord> list = criteria.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLast(final Long userId, Calendar date) {
		CTimeSheetRecord retVal;

		// ak si nastavim praca zaciatok na dnes o hodinu a prihlasim sa a dam teraz praca koniec tak mi to nenajde posledny zaznam
		date = CTimeUtils.convertToEndDate((Calendar) date.clone());

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName("last").eq(Boolean.TRUE));

		criteria.setFirstResult(0);
		criteria.setFetchSize(1);

		final List<CTimeSheetRecord> list = criteria.list();

		if (!list.isEmpty()) {
			retVal = list.get(0);

			Calendar timeFrom = retVal.getTimeFrom();

			// kontrola na porovnanie s datumom, ktory je vstupnym parametrom
			// ak nesedi robime select po starom tento pripad moze nastat ak si niekto robil vykaz dopredu
			if (timeFrom.after(date)) {
				Logger.getLogger(this.getClass()).debug("Check timesheet record for userId: " + userId + " --> " + timeFrom.getTime() + " after " + date.getTime());
				retVal = findLastWithoutUsingFlag(userId, date);
			}
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			retVal = findLastWithoutUsingFlag(userId, date);
		}

		return retVal;
	}

	/**
	 * tato metodka sa vzdy pouzivala pred optimalizaciou a select je strasne
	 * pomaly. stale sa pouziva vo vynimocnych pripadoch.
	 */
	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastWithoutUsingFlag(final Long userId, Calendar date) {
		Logger.getLogger(this.getClass()).info("findLastWithoutUsingFlag userId: " + userId + " date: " + date.getTime());
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Property.forName(TIME_FROM).le(date));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.addOrder(Order.desc(TIME_FROM));
		criteria.addOrder(Order.desc("changeTime"));

		criteria.setFirstResult(0);
		criteria.setFetchSize(1);

		final List<CTimeSheetRecord> list = criteria.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLastNonWorking(final Long userId) {
		CTimeSheetRecord retVal;

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts where  ts.valid = true and ts.owner.id=:userId and ts.lastNonWorking=:lastNonWorking ");
		q.setLong(USER_ID, userId);
		q.setBoolean("lastNonWorking", Boolean.TRUE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			retVal = list.get(0);
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			retVal = findLastNonWorkingWithoutUsingFlag(userId);
		}

		return retVal;
	}

	/**
	 * tato metodka sa vzdy pouzivala pred optimalizaciou a select je strasne
	 * pomaly. stale sa pouziva vo vynimocnych pripadoch.
	 */
	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastNonWorkingWithoutUsingFlag(final Long userId) {
		final Query q = sessionFactory.getCurrentSession()
				.createQuery("from CTimeSheetRecord as ts where  ts.valid = true and ts.owner.id=:userId and ts.activity.working=:working order by ts.timeFrom desc, ts.changeTime desc");
		q.setLong(USER_ID, userId);
		q.setBoolean(WORKING, Boolean.FALSE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLastNonWorking(final Long userId, Calendar date) {
		CTimeSheetRecord retVal;

		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts " + "where  ts.valid = true and ts.owner.id=:userId " + "and ts.lastNonWorking=:lastNonWorking ");
		q.setLong(USER_ID, userId);
		q.setBoolean("lastNonWorking", Boolean.TRUE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			retVal = list.get(0);

			Calendar timeFrom = retVal.getTimeFrom();

			// kontrola na porovnanie s datumom, ktory je vstupnym parametrom ak nesedi
			// robime select po starom tento pripad moze nastat ak si niekto robil vykaz dopredu
			if (timeFrom.after(date)) {
				Logger.getLogger(this.getClass()).debug("Check timesheet record for userId: " + userId + " --> " + timeFrom.getTime() + " after " + date.getTime());
				retVal = findLastNonWorkingWhitoutUsingFlag(userId, date);
			}
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			retVal = findLastNonWorkingWhitoutUsingFlag(userId, date);
		}

		return retVal;
	}

	/**
	 * tato metodka sa vzdy pouzivala pred optimalizaciou a select je strasne
	 * pomaly. stale sa pouziva vo vynimocnych pripadoch.
	 */
	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastNonWorkingWhitoutUsingFlag(final Long userId, Calendar date) {
		Logger.getLogger(this.getClass()).info("findLastNonWorkingWhitoutUsingFlag userId: " + userId + " date: " + date.getTime());
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts " + "where  ts.valid = true and ts.owner.id=:userId " + "and ts.activity.working=:working "
				+ "and ts.timeFrom<=:time " + "order by ts.timeFrom desc, ts.changeTime desc");
		q.setLong(USER_ID, userId);
		q.setBoolean(WORKING, Boolean.FALSE);
		q.setCalendar("time", date);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLastWorking(final Long userId) {
		CTimeSheetRecord retVal;

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts where  ts.valid = true and ts.owner.id=:userId and ts.lastWorking=:lastWorking ");
		q.setLong(USER_ID, userId);
		q.setBoolean(LAST_WORKING, Boolean.TRUE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			retVal = list.get(0);
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			retVal = findLastWorkingWithoutUsingFlag(userId);
		}

		return retVal;
	}

	/*
	 * tato metodka sa vzdy pouzivala pred optimalizaciou a select je strasne
	 * pomaly. stale sa pouziva vo vynimocnych pripadoch.
	 */
	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastWorkingWithoutUsingFlag(final Long userId) {
		final Query q = sessionFactory.getCurrentSession()
				.createQuery("from CTimeSheetRecord as ts where  ts.valid = true and ts.owner.id=:userId and ts.activity.working=:working order by ts.timeFrom desc, ts.changeTime desc");
		q.setLong(USER_ID, userId);
		q.setBoolean(WORKING, Boolean.TRUE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLastWorking(final Long userId, Calendar date) {
		CTimeSheetRecord retVal;
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts " + "where  ts.valid = true and ts.owner.id=:userId " + "and ts.lastWorking=:lastWorking ");
		q.setLong(USER_ID, userId);
		q.setBoolean(LAST_WORKING, Boolean.TRUE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			retVal = list.get(0);

			Calendar timeFrom = retVal.getTimeFrom();

			// kontrola na porovnanie s datumom, ktory je vstupnym parametrom ak nesedi
			// robime select po starom tento pripad moze nastat ak si niekto robil vykaz dopredu
			if (timeFrom.after(date)) {
				Logger.getLogger(this.getClass()).debug("Check timesheet record for userId: " + userId + " --> " + timeFrom.getTime() + " after " + date.getTime());
				retVal = findLastWorkingWithoutUsingFlag(userId, date);
			}
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			retVal = findLastWorkingWithoutUsingFlag(userId, date);
		}

		return retVal;
	}

	/**
	 * tato metodka sa vzdy pouzivala pred optimalizaciou a select je strasne
	 * pomaly. stale sa pouziva vo vynimocnych pripadoch.
	 */
	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastWorkingWithoutUsingFlag(final Long userId, Calendar date) {
		Logger.getLogger(this.getClass()).info("findLastWorkingWithoutUsingFlag userId: " + userId + " date: " + date.getTime());
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts " + "where  ts.valid = true and ts.owner.id=:userId " + "and ts.activity.working=:working "
				+ "and ts.timeFrom<=:time " + "order by ts.timeFrom desc, ts.changeTime desc");
		q.setLong(USER_ID, userId);
		q.setBoolean(WORKING, Boolean.TRUE);
		q.setCalendar("time", date);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findLastWorkingNonExternal(final Long userId, Calendar date) {
		CTimeSheetRecord retVal;
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Query q = sessionFactory.getCurrentSession()
				.createQuery("from CTimeSheetRecord as ts " + "where ts.outside = false and ts.valid = true and ts.owner.id=:userId " + "and ts.lastWorking=:lastWorking ");
		q.setLong(USER_ID, userId);
		q.setBoolean(LAST_WORKING, Boolean.TRUE);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			retVal = list.get(0);

			Calendar timeFrom = retVal.getTimeFrom();

			// kontrola na porovnanie s datumom, ktory je vstupnym parametrom ak nesedi
			// robime select po starom tento pripad moze nastat ak si niekto robil vykaz dopredu
			if (timeFrom.after(date)) {
				Logger.getLogger(this.getClass()).debug("Check timesheet record for userId: " + userId + " --> " + timeFrom.getTime() + " after " + date.getTime());
				retVal = findLastWorkingNonExternalWithoutUsingFlag(userId, date);
			}
		} else {
			// ak sa zaznam nenasiel asi dani pouzivatel je prvy krat v systeme od nasadenia verzie
			retVal = findLastWorkingNonExternalWithoutUsingFlag(userId, date);
		}

		return retVal;
	}

	@SuppressWarnings("unchecked")
	private CTimeSheetRecord findLastWorkingNonExternalWithoutUsingFlag(final Long userId, Calendar date) {
		Logger.getLogger(this.getClass()).info("findLastWorkingNonExternalWithoutUsingFlag userId: " + userId + " date: " + date.getTime());
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final Query q = sessionFactory.getCurrentSession().createQuery("from CTimeSheetRecord as ts " + "where ts.outside = false and ts.valid = true and ts.owner.id=:userId "
				+ "and ts.activity.working=:working " + "and ts.timeFrom<=:time " + "order by ts.timeFrom desc, ts.changeTime desc");
		q.setLong(USER_ID, userId);
		q.setBoolean(WORKING, Boolean.TRUE);
		q.setCalendar("time", date);

		q.setFirstResult(0);
		q.setFetchSize(1);

		final List<CTimeSheetRecord> list = q.list();

		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findLastWorkingExternal(final Long userId, Calendar date, final Integer count) {
		date = CTimeUtils.convertToEndTime((Calendar) date.clone());

		final SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(
				"select ts.* from t_time_sheet_record as ts, t_ct_activity as ac where ts.fk_activity = ac.pk_id and ts.c_flag_outside = true and ts.c_flag_valid = true and ts.fk_user_owner=:userId "
						+ "and ac.c_flag_working=:working " + "and ts.c_time_from<=:time " + "order by ts.c_time_from desc, ts.c_datetime_changed desc limit :count");
		q.setLong(USER_ID, userId);
		q.setBoolean(WORKING, Boolean.TRUE);
		q.setCalendar("time", date);
		q.setInteger("count", count);

		q.addEntity(CTimeSheetRecord.class);

		return q.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findLast(final Long userId, Calendar date, final Integer count) {
		final SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(
				"select ts.* from t_time_sheet_record ts where ts.c_flag_valid = true and ts.fk_user_owner=:userId " + "and ts.c_time_from<=:time " + "order by ts.c_time_from desc limit :count");
		q.setLong(USER_ID, userId);
		q.setCalendar("time", date);
		q.setInteger("count", count);

		q.addEntity(CTimeSheetRecord.class);

		return q.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> findLastActivityForProjectMap(final Long userId) {
		final Query query = sessionFactory.getCurrentSession()
				.createSQLQuery("select distinct on (fk_project) fk_project, fk_activity from t_time_sheet_record where  c_flag_valid = true and  fk_user_owner = :userId and fk_project is not null");
		query.setLong(USER_ID, userId);
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CTimeSheetRecord findUnclosedRecord(final Long userId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Restrictions.isNull(TIME_TO));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		final List<CTimeSheetRecord> list = criteria.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> findListUsersIdsWithCorruptedSummaryReportForClient(final Long clientId, Calendar timeFrom, Calendar timeTo) {

		String sQuery = "select DISTINCT ON (fk_user_owner) fk_user_owner from t_time_sheet_record t, t_user u where t.c_flag_valid = true and u.pk_id = t.fk_user_owner and t.fk_client = :clientId and t.fk_project is not null and t.c_time_to is null and t.c_time_from >= :timeFrom and t.c_time_from < :timeTo";
		// fk_client = 96
		// and c_time_to is null
		// and t.c_time_from >= timestamp '2011-02-14 00:00'
		// and t.c_time_from < timestamp '2011-02-21 00:00'

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		query.setLong("clientId", clientId);
		query.setCalendarDate(TIME_FROM, timeFrom);
		query.setCalendarDate(TIME_TO, timeTo);

		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Boolean hasUserUnclosedTimstempsInDateInterval(final Long userId, Calendar timeFrom, Calendar timeTo) {
		String sQuery = "select DISTINCT ON (fk_user_owner) fk_user_owner from t_time_sheet_record t, t_user u where t.c_flag_valid = true and u.pk_id = t.fk_user_owner and t.fk_user_owner = :userId and t.fk_project is not null and t.c_time_to is null and t.c_time_from >= :timeFrom and t.c_time_from < :timeTo";
		// fk_client = 96
		// and c_time_to is null
		// and t.c_time_from >= timestamp '2011-02-14 00:00'
		// and t.c_time_from < timestamp '2011-02-21 00:00'

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		query.setLong(USER_ID, userId);
		query.setCalendarDate(TIME_FROM, timeFrom);
		query.setCalendarDate(TIME_TO, timeTo);

		List<Object> list = query.list();

		return list != null && !list.isEmpty();
	}

	/**
	 * @see sk.qbsw.sed.server.dao.ITimesheetRecordDao#findUserRecords4Day(java.lang.Long,
	 *      java.util.Calendar)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findUserRecords4Day(Long userId, Calendar checkDate) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		Calendar dayAfterCheckDay = (Calendar) checkDate.clone();
		dayAfterCheckDay.add(Calendar.DATE, 1);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		Criterion c1 = Property.forName(TIME_FROM).ge(checkDate);
		Criterion c2 = Property.forName(TIME_FROM).lt(dayAfterCheckDay);
		criteria.add(Restrictions.and(c1, c2));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));

		criteria.addOrder(Order.asc(TIME_FROM));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findUserWorkRecords4Day(Long userId, Calendar checkDate) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		Calendar dayAfterCheckDay = (Calendar) checkDate.clone();
		dayAfterCheckDay.add(Calendar.DATE, 1);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		Criterion c1 = Property.forName(TIME_FROM).ge(checkDate);
		Criterion c2 = Property.forName(TIME_FROM).lt(dayAfterCheckDay);
		criteria.add(Restrictions.and(c1, c2));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName("project").isNotNull());
		criteria.createCriteria("activity").add(Property.forName(WORKING).eq(Boolean.TRUE));

		criteria.addOrder(Order.asc(TIME_FROM));
		return criteria.list();
	}

	/**
	 * @see sk.qbsw.sed.server.dao.ITimesheetRecordDao#findUserWorkRecordsInDateInterval(java.lang.Long,
	 *      java.util.Calendar, java.util.Calendar)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findUserWorkRecordsInDateInterval(Long userId, Calendar dateFom, Calendar dateTo) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);

		criteria.add(Property.forName(OWNER_ID).eq(userId));
		Criterion c1 = Property.forName(TIME_FROM).ge(dateFom);
		Criterion c2 = Property.forName(TIME_TO).le(dateTo);
		criteria.add(Restrictions.and(c1, c2));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName("project").isNotNull());
		criteria.createCriteria("activity").add(Property.forName(WORKING).eq(Boolean.TRUE));

		criteria.addOrder(Order.asc(TIME_FROM));
		return criteria.list();
	}

	@Override
	public Long countRecordsInDateIntervalByStatus(Long userId, Calendar dateFom, Calendar dateTo, CTimeSheetRecordStatus status) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);

		criteria.add(Property.forName(OWNER_ID).eq(userId));
		Criterion c1 = Property.forName(TIME_FROM).ge(dateFom);
		Criterion c2 = Property.forName(TIME_TO).le(dateTo);
		criteria.add(Restrictions.and(c1, c2));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));
		criteria.add(Property.forName("status").eq(status));

		criteria.setProjection(Projections.count("id"));
		return (Long) criteria.uniqueResult();
	}

	/**
	 * ak má zamestnanec aspoň jeden platný záznam vo výkaze práce, ktorý je v stave
	 * "potvrdený zamestnancom" a okrem toho nemá v predhádzajúcom týždni žiadny
	 * záznam vo výkaze práce v stave "nový" (fk_status = 1).
	 */
	@Override
	public boolean existsSubordinateRecordsForConfirmation(List<Long> userIds, Calendar dateFrom, Calendar dateTo, CTimeSheetRecordStatus status) {
		String select = "select count(pk_id) from t_time_sheet_record t " + "where t.fk_user_owner in :userIds and t.c_time_from >= :dateFrom and t.c_time_to <= :dateTo and t.c_flag_valid=true "
				+ "and t.fk_status = 2 and not exists (select null from t_time_sheet_record t2 where t2.fk_user_owner=t.fk_user_owner "
				+ "and t2.c_time_from >= :dateFrom and t2.c_time_to <= :dateTo and t2.c_flag_valid=true and t2.fk_status = 1)";

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(select);
		query.setParameterList("userIds", userIds);
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("dateTo", dateTo);

		return !BigInteger.ZERO.equals(query.list().get(0));
	}

	/**
	 * @see sk.qbsw.sed.server.dao.ITimesheetRecordDao#existsUserRecords4Day(java.lang.Long,
	 *      java.util.Calendar)
	 */
	@Override
	public Boolean existsUserRecords4Day(Long userId, Calendar checkDate) {
		List<CTimeSheetRecord> tmpList = findUserRecords4Day(userId, checkDate);
		return tmpList.size() > 0l;
	}

	@Override
	public Boolean existsUserWorkRecords4Day(Long userId, Calendar checkDate) {
		List<CTimeSheetRecord> tmpList = findUserWorkRecords4Day(userId, checkDate);
		return tmpList.size() > 0l;
	}

	@Override
	public String getSumAndAverageTimeInTimeInterval(final Long userId, final Calendar dateFrom, final Calendar dateTo) {
		SQLQuery query = sessionFactory.getCurrentSession()
				.createSQLQuery("select f_sed_get_working_time(" + userId + ", to_date('" + dateFormatter(dateFrom) + "','dd.mm.yyyy'), to_date('" + dateFormatter(dateTo) + "','dd.mm.yyyy'))");

		return query.list().get(0).toString();
	}

	private String dateFormatter(Calendar date) {
		return date.get(Calendar.DAY_OF_MONTH) + "." + (date.get(Calendar.MONTH) + 1) + "." + date.get(Calendar.YEAR);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findAll(final Collection<Long> users, Calendar dateFrom, Calendar dateTo) {
		Logger.getLogger(this.getClass()).info("Find all timesheet records start. users: " + users.toString());

		dateTo.set(Calendar.HOUR_OF_DAY, 23);
		dateTo.set(Calendar.MINUTE, 59);
		dateTo.set(Calendar.SECOND, 59);
		dateTo.set(Calendar.MILLISECOND, 999);

		dateFrom.set(Calendar.HOUR_OF_DAY, 0);
		dateFrom.set(Calendar.MINUTE, 0);
		dateFrom.set(Calendar.SECOND, 0);
		dateFrom.set(Calendar.MILLISECOND, 0);

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).in(users));
		criteria.add(Property.forName(TIME_FROM).le(dateTo));
		criteria.add(Property.forName(TIME_FROM).ge(dateFrom));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));

		final List<CTimeSheetRecord> list = criteria.list();

		Logger.getLogger(this.getClass()).info("Find all timesheet records finish. users: " + users.toString());
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CTimeSheetRecord> findSameUserRecords4Day(Long userId, Calendar checkDate, Long projectId, Long activityId, String note, String phase, Boolean outside, Boolean homeOffice) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTimeSheetRecord.class);
		Calendar startOfTheCheckDate = (Calendar) checkDate.clone();

		startOfTheCheckDate.set(Calendar.HOUR_OF_DAY, 0);
		startOfTheCheckDate.set(Calendar.MINUTE, 0);
		startOfTheCheckDate.set(Calendar.SECOND, 0);
		startOfTheCheckDate.set(Calendar.MILLISECOND, 0);

		criteria.add(Property.forName(OWNER_ID).eq(userId));
		Criterion c1 = Property.forName(TIME_FROM).ge(startOfTheCheckDate);
		Criterion c2 = Property.forName(TIME_FROM).lt(checkDate);
		criteria.add(Restrictions.and(c1, c2));
		criteria.add(Property.forName(VALID).eq(Boolean.TRUE));

		criteria.add(Property.forName("project.id").eq(projectId));
		criteria.add(Property.forName("activity.id").eq(activityId));
		criteria.add(Property.forName("outside").eq(outside));
		criteria.add(Property.forName("homeOffice").eq(homeOffice));

		if (note == null) {
			criteria.add(Property.forName("note").isNull());
		} else {
			criteria.add(Property.forName("note").eq(note));
		}

		if (phase == null) {
			criteria.add(Property.forName("phase").isNull());
		} else {
			criteria.add(Property.forName("phase").eq(phase));
		}

		criteria.addOrder(Order.asc(TIME_FROM));
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CProjectDuration> getDataForGraphOfProjects(final Long userId, Calendar timeFrom, Calendar timeTo) {
		String sQuery = "SELECT fk_project, p.c_name, extract (epoch from ( sum(date_trunc('minute',coalesce(c_time_to,now())-c_time_from)+interval '1 minute'))) "
				+ "FROM t_time_sheet_record r, t_ct_project p, t_ct_activity a " + "where r.fk_project = p.pk_id and fk_user_owner = :userId and "
				+ "c_time_from >= :timeFrom and coalesce(r.c_time_to,now())<= :timeTo and "
				+ "(r.c_time_to is not null or date_trunc('day',r.c_time_from)=current_date) and date_trunc('day',r.c_time_from)<=now() and "
				+ "r.c_flag_valid = true and fk_project is not null and r.fk_activity = a.pk_id and a.c_flag_sum = true " + "group by fk_project, p.c_name";

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		query.setLong(USER_ID, userId);
		query.setCalendar(TIME_FROM, timeFrom);
		query.setCalendar(TIME_TO, timeTo);

		final List<CProjectDuration> model = new ArrayList<>();
		final List<Object[]> data = query.list();
		for (final Object[] record : data) {
			final Long projectId = ((BigDecimal) record[0]).longValue();
			final String projectName = record[1].toString();
			final Long duration = (long) ((Double) record[2]).doubleValue() * 1000; // aby to bolo v milisekundach

			CProjectDuration projectDuration = new CProjectDuration();
			projectDuration.setProjectId(projectId);
			projectDuration.setProjectName(projectName);
			projectDuration.setDuration(duration);

			model.add(projectDuration);
		}

		return model;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CAttendanceDuration> getDataForGraphOfAttendance(final Long userId, Calendar timeFrom, Calendar timeTo) {
		String sQuery = "SELECT date_trunc('day', c_time_from ) , extract (epoch from ( sum(date_trunc('minute',coalesce(c_time_to,now())-c_time_from)+interval '1 minute'))) "
				+ "FROM t_time_sheet_record r, t_ct_activity a " + "where fk_user_owner = :userId and c_time_from >= :timeFrom and coalesce(r.c_time_to,now())<= :timeTo and"
				+ "(r.c_time_to is not null or date_trunc('day',r.c_time_from)=current_date) and date_trunc('day',r.c_time_from)<=now() and " + "r.c_flag_valid = true and "
				+ "r.fk_activity = a.pk_id and a.c_flag_sum = true " + "group by date_trunc('day', c_time_from ) " + "order by date_trunc('day', c_time_from )";

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		query.setLong(USER_ID, userId);
		query.setCalendar(TIME_FROM, timeFrom);
		query.setCalendar(TIME_TO, timeTo);

		final List<CAttendanceDuration> model = new ArrayList<>();
		final List<Object[]> data = query.list();
		for (final Object[] record : data) {

			final Calendar day = Calendar.getInstance();
			day.setTimeInMillis(((java.sql.Timestamp) record[0]).getTime());
			final Long duration = (long) ((Double) record[1]).doubleValue() * 1000; // aby to bolo v milisekundach

			CAttendanceDuration attendanceDuration = new CAttendanceDuration();

			attendanceDuration.setDay(day);
			attendanceDuration.setDuration(duration);

			model.add(attendanceDuration);
		}

		return model;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void skracovaniePoznamok(final Long timesheetId, final Long userId, Calendar timeFrom, Calendar timeTo) {
		if (timeTo == null) {
			timeTo = (Calendar) timeFrom.clone();
			timeTo.set(Calendar.HOUR_OF_DAY, 23);
			timeTo.set(Calendar.MINUTE, 59);
		}

		// zneplatnis prestavky, vsetky ktore su cele v intervale OD DO
		String selectQuery = "select pk_id from t_time_sheet_record where fk_user_owner = ? " + "and c_flag_valid = true and fk_activity = -1 and c_time_from >= ? "
				+ "and coalesce(c_time_to,to_timestamp(to_char(c_time_from,'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI')) <= "
				+ "coalesce(?,to_timestamp(to_char(to_timestamp(?, 'DD.MM.YYYY hh24:MI:SS'),'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI'))";

		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectQuery);
		query.setParameter(0, userId);
		query.setParameter(1, timeFrom);
		query.setParameter(2, timeTo);
		query.setParameter(3, timeFrom);

		final List<Object> data = query.list();

		for (Object id : data) {
			String updateQuery = "update t_time_sheet_record set c_flag_valid = false where pk_id = ? ";

			query = sessionFactory.getCurrentSession().createSQLQuery(updateQuery);
			query.setParameter(0, id);

			query.executeUpdate();
		}

		// skratis cas do pre tu prestavku, ktora je okolo OD
		String updateQuery = "update t_time_sheet_record set c_time_to = ? " + "where fk_user_owner = ? and c_flag_valid = true and fk_activity = -1 "
				+ "and c_time_from < ? and coalesce(c_time_to,to_timestamp(to_char(c_time_from,'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI')) > ?";

		Calendar timeFromMinusOneMinute = (Calendar) timeFrom.clone();
		timeFromMinusOneMinute.add(Calendar.MILLISECOND, -1);

		query = sessionFactory.getCurrentSession().createSQLQuery(updateQuery);
		query.setParameter(0, timeFromMinusOneMinute);
		query.setParameter(1, userId);
		query.setParameter(2, timeFrom);
		query.setParameter(3, timeFrom);

		query.executeUpdate();

		// skratis cas do pre tu prestavku, ktora je okolo DO
		updateQuery = "update t_time_sheet_record set c_time_from = ? " + "where fk_user_owner = ? and c_flag_valid = true and fk_activity = -1 "
				+ "and c_time_from < coalesce(?,to_timestamp(to_char(to_timestamp(?, 'DD.MM.YYYY hh24:MI:SS'),'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI')) "
				+ "and coalesce(c_time_to,to_timestamp(to_char(c_time_from,'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI')) > "
				+ "coalesce(?,to_timestamp(to_char(to_timestamp(?, 'DD.MM.YYYY hh24:MI:SS'),'DD.MM.YYYY')||' 23:59','DD.MM.YYYY HH24:MI'))";

		Calendar timeToPlusOneMinute = (Calendar) timeTo.clone();
		timeToPlusOneMinute.add(Calendar.MILLISECOND, 1);

		query = sessionFactory.getCurrentSession().createSQLQuery(updateQuery);
		query.setParameter(0, timeToPlusOneMinute);
		query.setParameter(1, userId);
		query.setParameter(2, timeTo);
		query.setParameter(3, timeFrom);
		query.setParameter(4, timeTo);
		query.setParameter(5, timeFrom);

		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CStatsRecord> getDataForGraphOfStats(final CStatsFilter filter) {
		String sQuery = "SELECT fk_project, p.c_name, fk_activity, a.c_name as activity_name, fk_user_owner, u.c_surname, extract (epoch from ( sum(date_trunc('minute',coalesce(c_time_to,now())-c_time_from)+interval '1 minute'))) "
				+ "FROM t_time_sheet_record r, t_ct_project p, t_user u, t_ct_activity a " + "where r.fk_project = p.pk_id and "
				+ "c_time_from >= :timeFrom and coalesce(r.c_time_to,now())<= :timeTo and "
				+ "(r.c_time_to is not null or date_trunc('day',r.c_time_from)=current_date) and date_trunc('day',r.c_time_from)<=now() and "
				+ "r.c_flag_valid = true and fk_project is not null and u.pk_id = r.fk_user_owner and a.pk_id = fk_activity ";

		String userIds = "(-1";
		for (Long userId : filter.getEmplyees()) {
			userIds += "," + userId;
		}
		userIds += ")";

		sQuery += "and fk_user_owner IN " + userIds;

		if (filter.getProjectId() != null && !ISearchConstants.ALL.equals(filter.getProjectId())) {
			sQuery += " and fk_project = " + filter.getProjectId();
		}

		if (filter.getActivityId() != null && !ISearchConstants.ALL.equals(filter.getActivityId())) {
			sQuery += " and fk_activity = " + filter.getActivityId();
		}

		sQuery += " group by fk_project, p.c_name, fk_activity, activity_name, fk_user_owner, u.c_surname ";

		final Query query = sessionFactory.getCurrentSession().createSQLQuery(sQuery);
		query.setCalendar(TIME_FROM, filter.getDateFrom());
		query.setCalendar(TIME_TO, filter.getDateTo());

		final List<CStatsRecord> model = new ArrayList<>();
		final List<Object[]> data = query.list();
		for (final Object[] record : data) {
			final Long projectId = ((BigDecimal) record[0]).longValue();
			final String projectName = record[1].toString();
			final Long activityId = ((BigDecimal) record[2]).longValue();
			final String activityName = record[3].toString();
			final Long employeeId = ((BigDecimal) record[4]).longValue();
			final String employeeName = record[5].toString();
			final Long duration = (long) ((Double) record[6]).doubleValue() * 1000; // aby to bolo v milisekundach

			CStatsRecord statsRecord = new CStatsRecord();
			statsRecord.setEmployeeName(employeeName);
			statsRecord.setEmployeeId(employeeId);
			statsRecord.setProjectName(projectName);
			statsRecord.setProjectId(projectId);
			statsRecord.setDuration(duration);
			statsRecord.setActivityId(activityId);
			statsRecord.setActivityName(activityName);

			model.add(statsRecord);
		}

		return model;
	}
}
