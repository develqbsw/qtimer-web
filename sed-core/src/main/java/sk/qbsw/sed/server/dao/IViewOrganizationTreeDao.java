package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.tree.org.CViewOrganizationTreeNode;

/**
 * Dao for accessing tree node hierarchy
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IViewOrganizationTreeDao extends IDao<CViewOrganizationTreeNode> {
	
	/**
	 * Finds all tree nodes relative to client and validity
	 * 
	 * @param clientId  id of client
	 * @param onlyValid shows only valid tree nodes
	 * @return list of tree nodes
	 */
	public List<CViewOrganizationTreeNode> findByClientValidity(Long clientId, Boolean onlyValid);

	/**
	 * Finds all tre nodes relative to client, user and validity
	 * 
	 * @param clientId  id of client
	 * @param userId    id of user to which should be the tree loaded
	 * @param onlyValid valid
	 * @return list of tree nodes
	 */
	public List<CViewOrganizationTreeNode> findByClientUserValidity(Long clientId, Long userId, Boolean onlyValid, Boolean withoutMe);

	public List<CViewOrganizationTreeNode> findByClientUserValidity(Long clientId, Long userId, Boolean onlyValid, Boolean withoutMe, Boolean adminFlag);

	/**
	 * For current userId returns his direct subordinates
	 * 
	 * @param userId
	 * @return
	 */
	public List<CViewOrganizationTreeNode> findDirectSubordinates(final Long userId);

	/**
	 * Najde vsetkych ktory chcu byt notifikovany o novej ziadost konrektneho
	 * zamestanca
	 * 
	 * @param newListNotified zoznam vsetkych Userov
	 * @return
	 */

	public List<CViewOrganizationTreeNode> findNotified(List<CUser> newListNotified);
}
