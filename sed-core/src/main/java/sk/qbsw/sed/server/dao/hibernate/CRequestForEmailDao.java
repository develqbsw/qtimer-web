package sk.qbsw.sed.server.dao.hibernate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.server.dao.IRequestForEmailDao;
import sk.qbsw.sed.server.model.domain.CRequestForEmail;
import sk.qbsw.sed.server.service.business.IRequestStatusConstant;

@Repository
public class CRequestForEmailDao extends AHibernateDao<CRequestForEmail> implements IRequestForEmailDao {
	
	@SuppressWarnings("unchecked")
	public List<CRequestForEmail> findAllNotCancelledInTimeInterval(final Date dateFrom, final Long clientId) {
		Calendar checkDay = Calendar.getInstance();
		checkDay.setTime(dateFrom);
		String checkDayStr = CDateUtils.convertToDateString(checkDay);

		// create query
		StringBuilder sq = new StringBuilder();

		// SED-779
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
		sq.append("r.fk_reason, ");
		sq.append("r.c_create_date ");
		sq.append("from t_request r ");
		sq.append("where (r.fk_client = ? ");
		sq.append(") ");
		sq.append("and (r.fk_status = ? ");
		sq.append(") ");
		sq.append("and to_date('" + checkDayStr + "', 'DD.MM.YYYY') between ");
		sq.append("to_date(to_char(r.c_date_from, 'DD.MM.YYYY'), 'DD.MM.YYYY') and ");
		sq.append("to_date(to_char(r.c_date_to, 'DD.MM.YYYY'), 'DD.MM.YYYY') ");
		sq.append("ORDER BY r.c_create_date DESC;");

		final SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sq.toString());

		query.setParameter(0, clientId);
		query.setParameter(1, IRequestStatusConstant.APPROVED);

		query.addEntity(CRequestForEmail.class);

		return query.list();
	}
}
