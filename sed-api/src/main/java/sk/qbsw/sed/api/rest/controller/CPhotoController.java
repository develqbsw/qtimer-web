package sk.qbsw.sed.api.rest.controller;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.service.business.IUserPhotoService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.domain.CUserPhoto;

@Controller
@RequestMapping(value = CApiUrl.USER_PHOTO)
public class CPhotoController {

	@Autowired
	private IUserPhotoService userPhotoService;

	@RequestMapping(value = "/{id}", headers = "Accept=image/jpeg, image/jpg, image/png, image/gif, image/bmp")
	@ResponseBody
	public HttpEntity<byte[]> getUserPhoto(@PathVariable String id) throws CApiException {
		Long userPhotoId = Long.parseLong(id);

		CUserPhoto photo = null;

		try {
			photo = userPhotoService.getUserPhoto(userPhotoId);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		String farFutureInMilis = Long.toString(Long.MAX_VALUE / 1000000);

		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl("max-age=" + farFutureInMilis);

		return new HttpEntity<>(ArrayUtils.toPrimitive(photo == null ? null : photo.getPhoto()), headers);
	}
}
