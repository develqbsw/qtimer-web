package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.model.domain.CTmpTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * 
 * @author rosenberg
 *
 */
public interface ITmpTimesheetRecordDao extends IDao<CTmpTimeSheetRecord> {

	/**
	 * Returns records for browser table
	 * 
	 * @param user     selected user
	 * @param criteria additional selected criteria
	 * @param from     start table record number
	 * @param count    number selection records
	 * @return list of records
	 */
	public List<CTmpTimeSheetRecord> findAll(CUser user, IFilterCriteria criteria, int from, int count, String sortProperty, boolean sortAsc);

	/**
	 * Return all user records
	 * 
	 * @param userId user identifier
	 * @return list of records
	 */
	public List<CTmpTimeSheetRecord> findPreparedDistributionsByUserId(final Long userId);

	/**
	 * Return all user records for selected date interval
	 * 
	 * @param userId   selected user
	 * @param dateFrom start date interval
	 * @param dateTo   end date interval
	 * @return list of records
	 */
	public List<CTmpTimeSheetRecord> findPreparedDistributionsByUserInDateInterval(final Long userId, final Calendar dateFrom, final Calendar dateTo);

	/**
	 * Delete the record selected by identifier
	 * 
	 * @param recordId user identifier
	 */
	public void deleteById(final Long recordId);

	/**
	 * Deletes all user records
	 * 
	 * @param userId user identifier
	 */
	public void deletePreparedDistributionsByUser(final Long userId);

	/**
	 * Returns items about realized generation user timestamps
	 * 
	 * @param userId user identifier
	 * @param count  max available counts
	 * @return list
	 */
	public List<CTmpTimeSheetRecord> findRealizedGenerationProcesses(Long userId, Integer count);
}
