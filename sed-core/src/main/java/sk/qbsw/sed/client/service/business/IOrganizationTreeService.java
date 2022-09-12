package sk.qbsw.sed.client.service.business;

import java.util.Date;
import java.util.List;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IOrganizationTreeService {

	/**
	 * Reads the tree
	 * 
	 * @param clientId
	 * @param onlyValid if the tree nodes should be valid
	 * @return list of tree nodes
	 */
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClient(Long clientId, Boolean onlyValid);

	/**
	 * Reads the tree
	 * 
	 * @param clientId
	 * @param userId
	 * @param withoutMe
	 * @param onlyValid if the tree nodes should be valid
	 * @return list of tree nodes
	 */
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClientUser(Long clientId, Long userId, Boolean onlyValid, Boolean withoutMe);

	/**
	 * Method responsible for changing tree structure. Method calls DB procedure
	 * whitch is responsible for doing changes in the structure.
	 * 
	 * @param treeNodeFrom
	 * @param treeNodeTo
	 * @param mode
	 * @param timestamp    Timestamp of generated data (used for checking if the client has the latest data)
	 * @throws CBusinessException
	 */
	public void move(Long treeNodeFrom, Long treeNodeTo, String mode, Date timestamp) throws CBusinessException;
}
