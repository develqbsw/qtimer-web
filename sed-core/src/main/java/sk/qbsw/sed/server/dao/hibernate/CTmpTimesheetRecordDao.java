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
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.IMyTimesheetGenerateBrwFilterCriteria;
import sk.qbsw.sed.server.dao.ITmpTimesheetRecordDao;
import sk.qbsw.sed.server.model.domain.CTmpTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;

@Repository
public class CTmpTimesheetRecordDao extends AHibernateDao<CTmpTimeSheetRecord> implements ITmpTimesheetRecordDao {

	private static final String DATE_GENERATE_ACTION = "dateGenerateAction";
	private static final String OWNER_ID = "ownerId";
	
	/**
	 * {@link ITmpTimesheetRecordDao#findAll(CUser, IFilterCriteria, int, int)}
	 */
	@SuppressWarnings("unchecked")
	public List<CTmpTimeSheetRecord> findAll(final CUser user, final IFilterCriteria filter, int from, int count, String sortProperty, boolean sortAsc) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTmpTimeSheetRecord.class);
		this.prepareCriteria(user, (IMyTimesheetGenerateBrwFilterCriteria) filter, criteria);
		criteria.add(Property.forName(DATE_GENERATE_ACTION).isNull());

		if (sortProperty != null) {
			criteria.addOrder(sortAsc ? Order.asc(sortProperty) : Order.desc(sortProperty));
		}

		criteria.setFirstResult(from);
		criteria.setFetchSize(count);
		return criteria.list();
	}

	/**
	 * {@link ITmpTimesheetRecordDao#findPreparedDistributionsByUserId(Long)}
	 */
	@SuppressWarnings("unchecked")
	public List<CTmpTimeSheetRecord> findPreparedDistributionsByUserId(final Long userId) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTmpTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Property.forName(DATE_GENERATE_ACTION).isNull());
		return criteria.list();
	}

	/**
	 * {@link ITmpTimesheetRecordDao#deleteById(Long)}
	 */
	public void deleteById(final Long recordId) {
		CTmpTimeSheetRecord record = findById(recordId);
		sessionFactory.getCurrentSession().delete(record);
	}

	/**
	 * {@link ITmpTimesheetRecordDao#deletePreparedDistributionsByUser(Long)}
	 */
	public void deletePreparedDistributionsByUser(final Long userId) {
		List<CTmpTimeSheetRecord> records = findPreparedDistributionsByUserId(userId);
		for (CTmpTimeSheetRecord entity : records) {
			sessionFactory.getCurrentSession().delete(entity);
		}
	}

	/**
	 * Prepares criteria for Browser
	 * 
	 * @param filter   input filter
	 * @param criteria output criteria
	 */
	private void prepareCriteria(final CUser user, final IMyTimesheetGenerateBrwFilterCriteria filter, final Criteria criteria) {
		// date from, date to
		if ((filter.getDateFrom() != null) || (filter.getDateTo() != null)) {
			if ((filter.getDateFrom() != null) && (filter.getDateTo() != null)) {
				final Calendar startTime = Calendar.getInstance();
				startTime.setTime(filter.getDateFrom());
				CTimeUtils.convertToStartDate(startTime);

				final Calendar stopTime = Calendar.getInstance();
				stopTime.setTime(filter.getDateTo());
				CTimeUtils.convertToEndDate(stopTime);

				final Criterion criterionDateFrom = Restrictions.eq("dateFrom", startTime);
				criteria.add(criterionDateFrom);
				final Criterion criterionDateTo = Restrictions.eq("dateTo", stopTime);
				criteria.add(criterionDateTo);
			}
		}

		// user
		criteria.add(Property.forName(OWNER_ID).eq(user.getId()));
	}

	/**
	 * @see ITmpTimesheetRecordDao#findPreparedDistributionsByUserInDateInterval(Long,
	 *      Calendar, Calendar)
	 */
	@SuppressWarnings("unchecked")
	public List<CTmpTimeSheetRecord> findPreparedDistributionsByUserInDateInterval(Long userId, Calendar dateFrom, Calendar dateTo) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTmpTimeSheetRecord.class);

		final Criterion criterionDateFrom = Restrictions.eq("dateFrom", dateFrom);
		criteria.add(criterionDateFrom);
		criteria.add(Property.forName(DATE_GENERATE_ACTION).isNull());
		final Criterion criterionDateTo = Restrictions.eq("dateTo", dateTo);
		criteria.add(criterionDateTo);
		criteria.add(Property.forName(OWNER_ID).eq(userId));

		return criteria.list();
	}

	/**
	 * @see ITmpTimesheetRecordDao#findRealizedGenerationProcesses(Long, Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<CTmpTimeSheetRecord> findRealizedGenerationProcesses(Long userId, Integer count) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CTmpTimeSheetRecord.class);
		criteria.add(Property.forName(OWNER_ID).eq(userId));
		criteria.add(Property.forName(DATE_GENERATE_ACTION).isNotNull());
		criteria.addOrder(Order.desc("changeTime"));

		List<CTmpTimeSheetRecord> wholeResult = criteria.list();
		List<CTmpTimeSheetRecord> result = new ArrayList<>();
		if (wholeResult.size() > count.intValue()) {
			int maxIdx = count;
			if (wholeResult.size() < maxIdx) {
				maxIdx = wholeResult.size();
			}

			for (int i = 0; i < maxIdx; i++) {
				result.add(wholeResult.get(i));
			}
		} else {
			result = wholeResult;
		}

		return result;
	}
}
