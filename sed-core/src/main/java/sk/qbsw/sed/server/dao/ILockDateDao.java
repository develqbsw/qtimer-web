package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.params.CLockDate;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.0
 */
public interface ILockDateDao extends IDao<CLockDate> {

	/**
	 * Finds entity by user
	 * 
	 * @param userId user identifier
	 */
	public List<CLockDate> findByUser(Long userId);
}
