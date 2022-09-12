package sk.qbsw.sed.server.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.service.business.IUserPhotoService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IUserPhotoDao;
import sk.qbsw.sed.server.model.domain.CUserPhoto;

@Service(value = "userPhotoService")
public class CUserPhotoServiceImpl implements IUserPhotoService {
	
	@Autowired
	private IUserPhotoDao userPhotoDao;

	/**
	 * Gets user's photo
	 */
	@Transactional(readOnly = true)
	public CUserPhoto getUserPhoto(final Long id) throws CBusinessException {
		return this.userPhotoDao.findById(id);
	}
}
