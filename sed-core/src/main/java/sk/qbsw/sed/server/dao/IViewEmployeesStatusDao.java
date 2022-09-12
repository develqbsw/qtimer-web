package sk.qbsw.sed.server.dao;

import java.util.Calendar;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.server.model.brw.CViewEmployeesStatus;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * DAO accessing model for brw employees
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.0
 */
public interface IViewEmployeesStatusDao extends IDao<CViewEmployeesStatus> {

	/**
	 * Finds data for browser of employees status for actual user
	 * 
	 * @param clientId logged user clientId
	 * @param criteria criteria from screen
	 * @return list of data model
	 */
	public List<CViewEmployeesStatus> findAll(Long clientId, IFilterCriteria criteria, int from, int count);

	public List<CViewEmployeesStatus> findAll(Long clientId);

	/**
	 * Returns all missing employees for the selected day
	 * 
	 * @param user          user of the target client
	 * @param checkDatetime target day
	 * @return list of records
	 */
	public List<CViewEmployeesStatus> findAllMissing(CUser user, Calendar checkDatetime);
}
