package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.codelist.CRequestReason;

/**
 * DAO interface for accessing request reason entities
 * 
 * @author rosenberg
 * @since 1.6.6.2
 * @version 1.6.6.2
 */
public interface IRequestReasonDao extends IDao<CRequestReason> {

	/**
	 * Returns all entities in system
	 * 
	 * @return list of entities
	 */
	List<CRequestReason> findAll();

	/**
	 * Returns all in/valid entities owned by client
	 * 
	 * @param clientId input client identifier
	 * @param valid    input value of valid flag
	 * @return list of entities
	 */
	List<CRequestReason> findValidByClient(Long clientId, Boolean valid);

	/**
	 * Returns all in/valid entities owned by client and associated to selected
	 * request type
	 * 
	 * @param clientId      input client identifier
	 * @param requestTypeId input request type identifier
	 * @param valid         input value of valid flag
	 * @return list of entities
	 */
	List<CRequestReason> findValidByClientRequestType(Long clientId, Long requestTypeId, Boolean valid);

	/**
	 * Returns list of activity identifiers that appears in valid clients reason
	 * definitions
	 * 
	 * @param clientId input client identifier
	 * @param valid    input value of valid flag
	 * @return list of identifiers
	 */
	public List<Long> getActivityIdsByValidClientRecords(Long clientId, Boolean valid);

	/**
	 * Returns all valid system records, available for all clients and associated to
	 * selected request type Modifiable only by data intervention only.
	 * 
	 * @param requestTypeId input request type identifier
	 * @return list of entities
	 */
	public List<CRequestReason> findSystemRecords(Long requestTypeId);

	public List<CRequestReason> findClientOrSystemRecords(Long clientId, Integer startRow, Integer endRow, String sortProperty, boolean sortAsc);

	Long count(Long clientId);
}
