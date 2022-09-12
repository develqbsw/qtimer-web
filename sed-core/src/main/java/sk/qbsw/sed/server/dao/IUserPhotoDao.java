package sk.qbsw.sed.server.dao;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.domain.CUserPhoto;

public interface IUserPhotoDao extends IDao<CUserPhoto> {

	/**
	 * Delete the record selected by identifier
	 * 
	 * @param recordId user identifier
	 */
	public void deleteById(final Long recordId);
}
