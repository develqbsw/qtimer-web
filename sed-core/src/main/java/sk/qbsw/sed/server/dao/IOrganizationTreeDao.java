package sk.qbsw.sed.server.dao;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.domain.COrganizationTree;

public interface IOrganizationTreeDao extends IDao<COrganizationTree> {
	
	/**
	 * Method moves tree node according implementation in DB
	 * 
	 * @param source      id of source organization hierarchy node
	 * @param destination id of destination organization hierarchy node
	 * @param mode        mode of move
	 * @param userId      id of user
	 */
	public void move(Long source, Long destination, String mode, Long userId) throws CBusinessException;

	/**
	 * 
	 * @param ownerId
	 * @return
	 * @throws CBusinessException
	 */
	public COrganizationTree findParentTree(Long ownerId) throws CBusinessException;
}
