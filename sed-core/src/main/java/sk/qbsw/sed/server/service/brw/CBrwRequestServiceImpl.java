package sk.qbsw.sed.server.service.brw;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.request.CMyRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.service.brw.IBrwRequestService;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;

/**
 * Service for loading data to browser
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Service(value = "brwRequestService")
public class CBrwRequestServiceImpl implements IBrwRequestService {
	@Autowired
	private IRequestDao requestDao;

	@Autowired
	private IHolidayDao holidayDao;

	@Override
	@Transactional(readOnly = true)
	public List<CRequestRecord> fetch(final int startRow, final int endRow, final IFilterCriteria criteria) throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		List<CRequestRecord> retVal = new ArrayList<>();
		final List<CRequest> requests = this.requestDao.findAll(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), (CMyRequestsBrwFilterCriteria) criteria, startRow, endRow);
		for (final CRequest cRequest : requests) {
			retVal.add(cRequest.convert());
		}

		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CRequestRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		List<CRequestRecord> retVal = new ArrayList<>();
		final List<CRequest> requests = this.requestDao.findAll(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), (CMyRequestsBrwFilterCriteria) criteria, 
				startRow, endRow, sortProperty, sortAsc);
		for (final CRequest cRequest : requests) {
			retVal.add(cRequest.convert());
		}
		return retVal;
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.requestDao.count(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), criteria);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CRequestRecordForGraph> loadDataForGraph(IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		List<CRequestRecordForGraph> retVal = new ArrayList<>();
		ComparatorByRequestStatus comparator = new ComparatorByRequestStatus();
		CMyRequestsBrwFilterCriteria crit = (CMyRequestsBrwFilterCriteria) criteria;
		Calendar dateMinimum = Calendar.getInstance();
		dateMinimum.setTime(crit.getDateFrom());
		Calendar dateMaximum = Calendar.getInstance();
		dateMaximum.setTime(crit.getDateTo());

		// minimalny rozsah na grafe chcem aby bolo vidiet dva vikendy
		int range = daysBetween(dateMaximum, dateMinimum);
		if (range < 13) {
			dateMaximum.add(Calendar.DAY_OF_YEAR, 13 - range);
			crit.setDateTo(dateMaximum.getTime());
		}

		final List<CRequest> requests = this.requestDao.findAll(loggedUser.getClientInfo().getClientId(), loggedUser.getUserId(), crit, 0, Integer.MAX_VALUE);

		List<CHoliday> holidays = holidayDao.findAllValidClientsHolidays(loggedUser.getClientInfo().getClientId(), null);

		Set<CUser> users = new HashSet<>();

		for (final CRequest cRequest : requests) {
			CRequestRecordForGraph duplicityRecord = null;

			// skontrolujem ci na ten isty rozsah uz je ziadost
			for (CRequestRecordForGraph record : retVal) {

				// ak je zaciatok ziadosti pred minimalnym datumom vo filtri pre tuto podmienku ma zaujima minimum co sa zobrazi
				Calendar dateFrom1 = record.getDateFrom();
				if (dateFrom1.before(dateMinimum)) {
					dateFrom1 = dateMinimum;
				}
				Calendar dateFrom2 = cRequest.getDateFrom();
				if (dateFrom2.before(dateMinimum)) {
					dateFrom2 = dateMinimum;
				}

				if (record.getOwnerId().equals(cRequest.getOwner().getId()) && DateUtils.isSameDay(dateFrom1, dateFrom2) && DateUtils.isSameDay(record.getDateTo(), cRequest.getDateTo())) {
					duplicityRecord = record;
					break;
				}
			}

			CRequestRecordForGraph cRequestRecord = convertRecordForGraph(cRequest);

			if (duplicityRecord != null) {
				// ak je tak do zoznamu dam len tu s vyssou prioritou
				if (comparator.compare(duplicityRecord, cRequestRecord) < 0) {
					retVal.add(cRequestRecord);
					retVal.remove(duplicityRecord);
				}
			} else {
				retVal.add(cRequestRecord);
			}

			if (cRequest.getDateTo().getTimeInMillis() > dateMaximum.getTimeInMillis()) {
				dateMaximum = cRequest.getDateTo();
			}

			users.add(cRequest.getOwner());
		}

		// idem vytvorit vikendy a sviatky
		for (CUser user : users) {

			Date dateFrom = dateMinimum.getTime();

			Calendar from = Calendar.getInstance();

			while (dateFrom.getTime() <= dateMaximum.getTimeInMillis()) {
				if (!CDateUtils.isWorkingDay(dateFrom)) {
					// it is weekend
					from.setTime(dateFrom);
					if (from.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
						CDateUtils.addDayToDate(dateFrom);
					}
					retVal.add(createRecordForGraphForNonWorking(from, dateFrom, user, null));
				} else {
					CHoliday holiday = getHoliday(dateFrom, holidays);
					if (holiday != null) {
						from.setTime(dateFrom);
						retVal.add(createRecordForGraphForNonWorking(from, dateFrom, user, holiday.getDescription()));
					}
				}
				CDateUtils.addDayToDate(dateFrom);
			}
		}

		retVal.sort(new ComparatorByAlphabetical());

		return retVal;
	}

	/**
	 * 
	 * Vytvorenie ziadosti pre graf.
	 * 
	 * @param from
	 * @param dateFrom
	 * @param cRequest
	 * @return
	 */
	private CRequestRecordForGraph convertRecordForGraph(CRequest cRequest) {
		CTimeUtils.convertToEndDate(cRequest.getDateTo());

		CRequestRecordForGraph r = new CRequestRecordForGraph();
		r.setId(cRequest.getId());
		r.setDateFrom(cRequest.getDateFrom());
		r.setDateTo(cRequest.getDateTo());
		r.setStatusId(cRequest.getStatus().getId());
		r.setStatusDescription(cRequest.getStatus().getDescription());
		r.setFullday(cRequest.getHoursDateFrom() == null);
		r.setOwnerName(cRequest.getOwner().getName());
		r.setOwnerSurname(cRequest.getOwner().getSurname());
		r.setOwnerId(cRequest.getOwner().getId());
		r.setTypeDescription(cRequest.getType().getDescription());

		return r;
	}

	/**
	 * 
	 * Vytvorenie ziadosti pre graf pre sviatok alebo vikend.
	 * 
	 * @param from
	 * @param dateFrom
	 * @param cRequest
	 * @return
	 */
	private CRequestRecordForGraph createRecordForGraphForNonWorking(Calendar dateFrom, Date dateTo, CUser user, String holidayDescription) {
		Calendar from = (Calendar) dateFrom.clone();
		CTimeUtils.convertToStartDate(from);

		Calendar to = Calendar.getInstance();
		to.setTime(dateTo);
		CTimeUtils.convertToEndDate(to);

		CRequestRecordForGraph r = new CRequestRecordForGraph();
		r.setDateFrom(from);
		r.setDateTo(to);
		r.setStatusId(-1L); // -1 je pre sviatky a vikendy
		r.setFullday(true);
		r.setOwnerName(user.getName());
		r.setOwnerSurname(user.getSurname());
		r.setOwnerId(user.getId());
		r.setTypeDescription(holidayDescription);

		return r;
	}

	/**
	 * Komparator pre ziadosti. Ak je na ten isty den viac ziadosti chceme v grafe
	 * na vrchu vzdy volne dni, potom schvalene ziadosti...
	 *
	 */
	private class ComparatorByRequestStatus implements Comparator<CRequestRecordForGraph> {

		@Override
		public int compare(CRequestRecordForGraph o1, CRequestRecordForGraph o2) {

			int o1Priority = 0;
			int o2Priority = 0;

			switch (o1.getStatusId().intValue()) {
			case -1: // free day
				o1Priority = 1;
				break;
			case 3: // approved
				o1Priority = 2;
				break;
			case 1: // created
				o1Priority = 3;
				break;
			case 4: // declined
				o1Priority = 4;
				break;
			case 2: // cancelled
				o1Priority = 5;
				break;
			default:
				break;
			}

			switch (o2.getStatusId().intValue()) {
			case -1: // free day
				o2Priority = 1;
				break;
			case 3: // approved
				o2Priority = 2;
				break;
			case 1: // created
				o2Priority = 3;
				break;
			case 4: // declined
				o2Priority = 4;
				break;
			case 2: // cancelled
				o2Priority = 5;
				break;
			default:
				break;
			}

			return Integer.valueOf(o2Priority).compareTo(Integer.valueOf(o1Priority));
		}
	}

	private class ComparatorByAlphabetical implements Comparator<CRequestRecordForGraph> {

		private final Locale locale = new Locale("sk");
		private Collator collator = Collator.getInstance(locale);

		@Override
		public int compare(CRequestRecordForGraph o1, CRequestRecordForGraph o2) {
			int surname = collator.compare(o1.getOwnerSurname(), o2.getOwnerSurname());

			if (surname != 0) {
				return surname;
			}

			return collator.compare(o1.getOwnerName(), o2.getOwnerName());
		}
	}

	/**
	 * return holiday record if exists for checkDate, else return null
	 * 
	 * @param checkDate
	 * @param holidays
	 * @return
	 */
	private static CHoliday getHoliday(Date checkDate, List<CHoliday> holidays) {
		if (checkDate == null) {
			return null;
		}

		for (CHoliday holiday : holidays) {
			if (CDateUtils.isSameDay(checkDate, holiday.getDay())) {
				return holiday;
			}
		}

		return null;
	}

	/**
	 * Calculating days between two calendar instances
	 * 
	 * @param startDate
	 * @param endDate
	 * @return number of days between two calendar instances
	 */
	public static int daysBetween(Calendar startDate, Calendar endDate) {
		long end = endDate.getTimeInMillis();
		long start = startDate.getTimeInMillis();
		return Math.toIntExact(TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)));
	}
}