package sk.qbsw.sed.server.dao.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.framework.generator.IGenerator;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.server.dao.IViewMonthSheetDao;
import sk.qbsw.sed.server.model.report.CShortlyReportDataModel;
import sk.qbsw.sed.server.model.report.CViewMonthSheet;
import sk.qbsw.sed.server.service.CTimeUtils;

@Repository
@SuppressWarnings("unchecked")
public class CViewMonthSheetDao extends AHibernateDao<CReportModel> implements IViewMonthSheetDao<CReportModel> {

	public List<CReportModel> findByUsersAndPeriod(final Set<Long> userIds, final Calendar dateFrom, final Calendar dateTo, final boolean alsoNotConfirmed, final String screenType) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewMonthSheet.class);
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
				// na obrazovke Zamestanci/Export výkazu práce ak som prihlásený ako admin stav 4
				statusIds.add(4L);
			}
			criteria.add(Restrictions.in("cStatusId", statusIds.toArray()));
		}

		return this.convertModel(this.groupModel(criteria.list()));
	}

	private List<CReportModel> convertModel(final Map<Long, LinkedList<CShortlyReportDataModel>> group) {
		final List<CReportModel> reportModel = new ArrayList<>(group.size());
		for (final Long id : group.keySet()) {
			for (final CShortlyReportDataModel model : group.get(id)) {
				final CReportModel rowModel = new CReportModel();
				rowModel.setValue(0, model.getProjectName());
				rowModel.setValue(1, model.getEtapaId());
				rowModel.setValue(2, model.getUserName());
				rowModel.setValue(3, model.getTimeInPercent());
				rowModel.setValue(4, model.getActivityName());
				rowModel.setValue(5, model.getDate());
				rowModel.setValue(6, model.getTime());
				reportModel.add(rowModel);
			}
		}
		return reportModel;
	}

	private Map<Long, LinkedList<CShortlyReportDataModel>> groupModel(final List<CViewMonthSheet> data) {
		BigDecimal sumTime = BigDecimal.ZERO;
		final Map<Long, LinkedList<CShortlyReportDataModel>> group = new LinkedHashMap<>();
		for (final CViewMonthSheet record : data) {
			LinkedList<CShortlyReportDataModel> model = group.get(record.getcUserId());
			if (null == model) {
				model = new LinkedList<>();
				final CShortlyReportDataModel singleModel = this.getSingleModel(record);
				model.add(singleModel);
				group.put(record.getcUserId(), model);
				sumTime = sumTime.add(singleModel.getTime());
			} else {
				final CShortlyReportDataModel temp = this.getSingleModel(record);
				boolean found = false;
				for (final CShortlyReportDataModel singleModel : model) {
					if (temp.equals(singleModel)) {
						// nasiel som rovnaky zaznam ale s inym casom... cas pripocitam...
						singleModel.setTime(singleModel.getTime().add(temp.getTime()));
						found = true;
						break;
					}
				}
				if (!found) {
					// nenasiel sa ziadny zaznam, takze pridam si tento temp...
					model.add(temp);
				}
				sumTime = sumTime.add(temp.getTime());
			}
		}

		// vypocitam percentualny cas...
		for (final Long id : group.keySet()) {
			for (final CShortlyReportDataModel model : group.get(id)) {
				model.setTimeInPercent(CTimeUtils.getPercentTime(model.getTime(), sumTime).divide(CTimeUtils.BIGDECIMAL_HUNDRED));
			}
		}
		return group;
	}

	private CShortlyReportDataModel getSingleModel(final CViewMonthSheet record) {
		final CShortlyReportDataModel singleModel = new CShortlyReportDataModel();
		singleModel.setActivityName(record.getCActivityName());
		singleModel.setEtapaId(record.getCEtapaId());
		singleModel.setUserId(record.getcUserId());
		singleModel.setProjectName(record.getCProjectName());
		singleModel.setUserName(record.getCUser());
		singleModel.setDate(record.getCDate());
		singleModel.setTime(CTimeUtils.convertToMinutes(record.getCDuration()));

		return singleModel;
	}
}
