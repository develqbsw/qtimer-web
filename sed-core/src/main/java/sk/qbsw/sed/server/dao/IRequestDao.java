package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CRequest;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * DAO for accessing requests
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IRequestDao extends IDao<CRequest> {

	/**
	 * Finds all records not canceled and matching time interval for selected user
	 * 
	 * @param dateFrom start date interval
	 * @param dateTo   end date interval
	 * @param user     selected user
	 * @return list of requests
	 */
	public List<CRequest> findAllNotCancelledInTimeInterval(Date dateFrom, Date dateTo, CUser user);

	/**
	 * Finds all approved records and matching time interval for selected user
	 * 
	 * @param dateFrom start date interval
	 * @param dateTo   end date interval
	 * @param user     selected user
	 * @return list of requests
	 */
	public List<CRequest> findAllApprovedInTimeInterval(final Date dateFrom, final Date dateTo, final CUser user);

	public List<CRequest> findAllApprovedAndCreatedHolidayInTimeInterval(final Date dateFrom, final Date dateTo, final CUser user);

	public List<CRequest> findAllApprovedHolidayInTimeInterval(Date dateFrom, Date dateTo, CUser user);

	/**
	 * For selected user finds all approved records with date interval that contains
	 * selected date
	 * 
	 * @param date selected date
	 * @param user selected user
	 * @return list of requests
	 */
	public List<CRequest> findAllApprovedUserRequestContainsDate(final Date date, final CUser user);

	public List<CRequest> findAllApprovedUserRequestContainsDate(final Date date, final Long userId);

	public List<CRequest> findAllCreatedUserRequestForHomeOfficeContainsDate(final Date date, final Long userId);

	/**
	 * Returns list of approved requests of the selected client and selected day
	 * 
	 * @param clientId target client identifier
	 * @param checkDay checked date
	 * @return list of requests
	 */
	public List<CRequest> findApprovedClientRequests4Day(Long clientId, Calendar checkDay);

	/**
	 * Returns list of selected record for browser
	 * 
	 * @param user   target user
	 * @param filter filter values
	 * @param from   start row
	 * @param count  rows number value
	 * @return list of records
	 */
	public List<CRequest> findAll(final Long clientId, final Long id, final IFilterCriteria filter, final int from, final int count);

	public List<CRequest> findAll(final Long clientId, final Long userId, final IFilterCriteria filter, final int from, final int count, String sortProperty, boolean sortAsc);

	public Long count(final Long clientId, final Long id, final IFilterCriteria filter);

	public Boolean existsClientRequestsHoliday4Day(final Long userId, Calendar checkDay);

	public Boolean existsClientRequestsHoliday4DayLastGen(final Long userId, Calendar checkDay);

	public Boolean existsClientRequestsHomeOffice4Today(final Long userId, Date date);

	public Boolean existsClientRequestsHomeOfficeInInterval(final Long userId, Long clientId, Date dateFrom, Date dateTo, List<CHoliday> holidays);
}
