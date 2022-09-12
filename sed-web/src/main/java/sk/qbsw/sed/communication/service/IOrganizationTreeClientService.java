package sk.qbsw.sed.communication.service;

import java.util.Date;
import java.util.List;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IOrganizationTreeClientService {

	/**
	 * Reads the tree
	 * 
	 * @param clientId
	 * @param onlyValid if the tree nodes should be valid
	 * @return list of tree nodes
	 */
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClient(Long clientId, Boolean onlyValid) throws CBussinessDataException;

	/**
	 * Reads the tree
	 * 
	 * @param clientId
	 * @param userId
	 * @param onlyValid if the tree nodes should be valid
	 * @param withoutMe
	 * @return list of tree nodes
	 */
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClientUser(Long clientId, Long userId, Boolean onlyValid, Boolean withoutMe) throws CBussinessDataException;

	/**
	 * Method responsible for changing tree structure. Method calls DB procedure
	 * whitch is responsible for doing changes in the structure.
	 * 
	 * @param treeNodeFrom
	 * @param treeNodeTo
	 * @param mode
	 * @param timestamp    Timestamp of generated data (used for checking if the
	 *                     client has the latest data)
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public void move(Long treeNodeFrom, Long treeNodeTo, String mode, Date timestamp) throws CBussinessDataException;

	/**
	 * 
	 * @param clientId
	 * @return
	 * @throws CBusinessException
	 * @throws CBussinessDataException
	 */
}
