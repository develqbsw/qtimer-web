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
import sk.qbsw.sed.server.dao.IViewWeekSheetDao;
import sk.qbsw.sed.server.model.report.CViewWeekSheet;

@Repository
@SuppressWarnings("unchecked")
public class CViewWeekSheetDao extends AHibernateDao<CReportModel> implements IViewWeekSheetDao<CReportModel> {

	public List<CReportModel> findByUsersAndPeriod(final Set<Long> userIds, final Calendar dateFrom, final Calendar dateTo, final boolean alsoNotConfirmed, final String screenType) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewWeekSheet.class);
		final Criterion dateGE = Property.forName("cDate").ge(dateFrom);
		final Criterion dateLE = Property.forName("cDate").le(dateTo);
		final Criterion dateCriterion = Restrictions.and(dateGE, dateLE);
		criteria.add(dateCriterion);
		criteria.add(Restrictions.in("cUserId", userIds.toArray()));
		criteria.add(Restrictions.isNotNull("cTimeTo"));

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
				// na obrazovke Zamestanci/Export výkazu práce ak som prihlásený ako admin stav
				// 4
				statusIds.add(4L);
			}
			criteria.add(Restrictions.in("cStatusId", statusIds.toArray()));
		}

		return this.convertModel(this.groupModelRecords(criteria.list()));
	}

	private List<CViewWeekSheet> groupModelRecords(final List<CViewWeekSheet> data) {
		// prepare grouped values
		Map<String, CViewWeekSheet> tmp = new LinkedHashMap<>();
		for (CViewWeekSheet record : data) {
			String hash = getRecordHash(record);
			CViewWeekSheet oldValue = tmp.get(hash);
			if (oldValue != null) {
				// exists
				String sOldDuration = oldValue.getCDuration();
				String sDuration = record.getCDuration();
				if (sDuration != null && !"".equals(sDuration)) {
					String sNewDuration = addition(sOldDuration, sDuration);
					oldValue.setCDuration(sNewDuration);
				}
			} else {
				// not exists yet
				if (record.getCDuration() != null && !"".equals(record.getCDuration())) {
					tmp.put(hash, record);
				}
			}
		}

		// prepare list of output values
		List<CViewWeekSheet> outData = new ArrayList<>();
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
	private String getRecordHash(CViewWeekSheet record) {
		String retVal = "";
		// userId
		retVal = retVal + record.getcUserId().toString();
		// date
		retVal = retVal + "" + record.getCDate().get(Calendar.YEAR);
		retVal = retVal + "" + record.getCDate().get(Calendar.MONTH);
		retVal = retVal + "" + record.getCDate().get(Calendar.DAY_OF_MONTH);
		// project
		retVal = retVal + ((record.getCProjectName() == null) ? "" : record.getCProjectName().trim());
		// activity
		retVal = retVal + ((record.getCActivityName() == null) ? "" : record.getCActivityName().trim());
		// phase
		retVal = retVal + ((record.getCEtapaId() == null) ? "" : record.getCEtapaId().trim());
		// note
		retVal = retVal + ((record.getCNote() == null) ? "" : record.getCNote().trim());

		return retVal;
	}

	private List<CReportModel> convertModel(final List<CViewWeekSheet> data) {
		final List<CReportModel> model = new ArrayList<>(data.size());
		for (final CViewWeekSheet row : data) {
			final CReportModel rowModel = new CReportModel();
			rowModel.setValue(0, row.getCDate());
			rowModel.setValue(1, row.getCUser());
			rowModel.setValue(2, row.getCProjectName());
			rowModel.setValue(3, row.getCActivityName());
			rowModel.setValue(4, row.getCEtapaId());
			rowModel.setValue(5, row.getCDuration());
			rowModel.setValue(6, row.getCNote());
			model.add(rowModel);
		}
		return model;
	}
}
