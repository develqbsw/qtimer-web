package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.params.CParameterEntity;

/**
 * DAO interface to CParameterEntity
 * 
 * @see CParameterEntity
 * 
 * @author Ladislav Rosenberg
 * @Version 1.0
 * @since 1.6.0
 * 
 */
public interface IParameterDao extends IDao<CParameterEntity> {

	/**
	 * Finds all parameters for organization
	 * 
	 * @param orgId
	 * @return list of parameters
	 */
	public List<CParameterEntity> findAll(Long clientId);

	/**
	 * Finds parameters by organization and parameter name
	 * 
	 * @param name parameter name
	 * @return parameter object
	 */
	public List<CParameterEntity> findByNameForClient(Long clientId, String name);
}
