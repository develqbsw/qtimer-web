package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.params.CParameterEntity;

/**
 * DAO interface to CHolidayEntity
 * 
 * @see CParameterEntity
 * 
 * @author Ladislav Rosenberg
 * @Version 1.0
 * @since 1.6.2
 * 
 */
public interface IHolidayDao extends IDao<CHoliday> {

	/**
	 * 
	 * @param clientId
	 * @param filterCriteria
	 * @param startRow
	 * @param endRow
	 * @param sortProperty
	 * @param sortAsc
	 * @return
	 */
	public List<CHoliday> findAllClientsHolidays(Long clientId, IFilterCriteria filterCriteria, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	/**
	 * Finds all avlid holidays for organization and selected year
	 * 
	 * @param orgId
	 * @return list of parameters
	 */
	public List<CHoliday> findAllValidClientsHolidays(Long clientId, Integer selectedYear);

	/**
	 * 
	 * @param clientId
	 * @param criteria
	 * @return
	 */
	public Long count(Long clientId, IFilterCriteria criteria);

}
