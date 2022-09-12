package sk.qbsw.sed.client.service.business;

import org.springframework.cache.annotation.Cacheable;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.domain.CUserPhoto;

public interface IUserPhotoService {

	/**
	 * Read user's photo
	 * 
	 * @param id
	 * @return
	 * @throws CBusinessException
	 */
	@Cacheable(value = "photoFindCache", key = "#id")
	public CUserPhoto getUserPhoto(final Long id) throws CBusinessException;
}
