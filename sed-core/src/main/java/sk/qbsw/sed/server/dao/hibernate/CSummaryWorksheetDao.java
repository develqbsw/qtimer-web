package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.generator.IGenerator;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.server.dao.ISummaryWorksheetDao;
import sk.qbsw.sed.server.model.report.CViewSummaryWeekSheet;

@Repository
@SuppressWarnings("unchecked")
public class CSummaryWorksheetDao extends AHibernateDao<CReportModel> implements ISummaryWorksheetDao<CReportModel> {

	// OK - done
	public List<CReportModel> findByUsersAndPeriod(final Set<Long> userIds, final Calendar dateFrom, final Calendar dateTo, final boolean alsoNotConfirmed, final String screenType) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewSummaryWeekSheet.class);
		final Criterion dateGE = Property.forName("cDate").ge(dateFrom);
		final Criterion dateLE = Property.forName("cDate").le(dateTo);
		final Criterion dateCriterion = Restrictions.and(dateGE, dateLE);
		criteria.add(dateCriterion);
		criteria.add(Restrictions.in("cUserId", userIds.toArray()));

		if (!alsoNotConfirmed) {
			// ak je checkbox zaškrtnutý, tak brať pri generovaní záznamy ako doteraz
			// ak je checkbox nie zaškrtnutý, tak brať pri generovaní záznamy
			// ako doteraz a navyše pridať podmienku na stav

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
			criteria.add(Restrictions.in("cStatusId", statusIds.toArray()));
		}

		return this.convertModel(this.groupModelRecords(criteria.list()));
	}

	private List<CViewSummaryWeekSheet> groupModelRecords(final List<CViewSummaryWeekSheet> data) {
		// prepare grouped values
		Map<String, CViewSummaryWeekSheet> tmp = new LinkedHashMap<>();
		for (CViewSummaryWeekSheet record : data) {
			String hash = getRecordHash(record);
			CViewSummaryWeekSheet oldValue = tmp.get(hash);
			if (oldValue != null) {
				// exists
				String sOldDuration = oldValue.getcDuration();
				String sDuration = record.getcDuration();
				if (sDuration != null && !"".equals(sDuration)) {
					String sNewDuration = addition(sOldDuration, sDuration);
					oldValue.setcDuration(sNewDuration);
				}
			} else {
				// not exists yet
				if (record.getcDuration() != null && !"".equals(record.getcDuration())) {
					tmp.put(hash, record);
				}
			}
		}

		// prepare list of output values
		List<CViewSummaryWeekSheet> outData = new ArrayList<>();
		Set<String> lSet = tmp.keySet();
		Iterator<String> itr2 = lSet.iterator();
		while (itr2.hasNext()) {
			String key = itr2.next();
			outData.add(tmp.get(key));
		}

		return outData;
	}

	private String addition(String time1, String time2) {

		String retVal = "";
		String[] p1 = time1.split(":");
		String[] p2 = time2.split(":");

		Integer it1hour = new Integer(Integer.parseInt(p1[0].trim()));
		Integer it1minute = new Integer(Integer.parseInt(p1[1].trim()));

		Integer it2hour = new Integer(Integer.parseInt(p2[0].trim()));
		Integer it2minute = new Integer(Integer.parseInt(p2[1].trim()));

		Integer minutes = it1minute + it2minute;
		Integer hours = it1hour + it2hour;

		Integer newMinutes = minutes % 60;
		Integer newHours = hours + (minutes / 60);

		retVal = "" + CDateUtils.getLeadingZero(newHours, 2) + ":" + CDateUtils.getLeadingZero(newMinutes, 2);

		return retVal;
	}

	/**
	 * hash for: user, date, project, activity, phase, notes
	 * 
	 * @param record input record
	 * @return hash string
	 */
	private String getRecordHash(CViewSummaryWeekSheet record) {
		String retVal = "";
		// userId
		retVal = retVal + record.getcUserId().toString();
		// date
		retVal = retVal + "" + record.getcDate().get(Calendar.YEAR);
		retVal = retVal + "" + record.getcDate().get(Calendar.MONTH);
		retVal = retVal + "" + record.getcDate().get(Calendar.DAY_OF_MONTH);
		// project
		retVal = retVal + ((record.getcProjectName() == null) ? "" : record.getcProjectName().trim());
		// activity
		retVal = retVal + ((record.getcActivityName() == null) ? "" : record.getcActivityName().trim());
		// phase
		retVal = retVal + ((record.getcEtapaId() == null) ? "" : record.getcEtapaId().trim());
		// note
		retVal = retVal + ((record.getcNote() == null) ? "" : record.getcNote().trim());

		return retVal;
	}

	private List<CReportModel> convertModel(final List<CViewSummaryWeekSheet> data) {
		final List<CReportModel> model = new ArrayList<>(data.size());
		for (final CViewSummaryWeekSheet row : data) {
			final CReportModel rowModel = new CReportModel();
			rowModel.setValue(0, row.getcYear());
			rowModel.setValue(1, row.getcYearMonth());
			rowModel.setValue(2, row.getcYearWeek());
			rowModel.setValue(3, row.getcUser());
			rowModel.setValue(4, row.getcProjectGroup() == null ? "" : row.getcProjectGroup());
			rowModel.setValue(5, row.getcProjectCode() == null ? "" : row.getcProjectCode());
			rowModel.setValue(6, row.getcUserCode() == null ? "" : row.getcUserCode());
			rowModel.setValue(7, row.getcDate());
			rowModel.setValue(8, row.getcDuration());
			rowModel.setValue(9, row.getcActivityName());
			rowModel.setValue(10, row.getcNote() == null ? "" : row.getcNote());
			rowModel.setValue(11, row.getcEtapaId() == null ? "" : row.getcEtapaId());
			rowModel.setValue(12, ""); // dummy hodnota - stlpec M je momentalne nevyplneny, cakam na info od Z.B.
			rowModel.setValue(13, row.getcProjectName());
			model.add(rowModel);
		}
		return model;
	}
}
