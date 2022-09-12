package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.request.CUploadRequest;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IUploadClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CUploadClientService extends AClientService implements IUploadClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CUploadClientService() {
		super(CApiUrl.UPLOAD);
	}

	@Override
	public CUploadResponseContent uploadActivities(String[] fileRows) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CUploadRequest requestEntity = new CUploadRequest();
		requestEntity.setFileRows(fileRows);

		Type type = new TypeToken<CResponse<CUploadResponseContent>>() {
		}.getType();
		AServiceCall<CUploadRequest, CUploadResponseContent, CUploadResponseContent> call = new AServiceCall<CUploadRequest, CUploadResponseContent, CUploadResponseContent>() {

			@Override
			public CUploadResponseContent getContentObject(CUploadResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.UPLOAD_ACTIVITIES), requestEntity, type); // CUploadController.uploadActivities()
	}

	@Override
	public CUploadResponseContent uploadProjects(String[] fileRows) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CUploadRequest requestEntity = new CUploadRequest();
		requestEntity.setFileRows(fileRows);

		Type type = new TypeToken<CResponse<CUploadResponseContent>>() {
		}.getType();
		AServiceCall<CUploadRequest, CUploadResponseContent, CUploadResponseContent> call = new AServiceCall<CUploadRequest, CUploadResponseContent, CUploadResponseContent>() {

			@Override
			public CUploadResponseContent getContentObject(CUploadResponseContent content) {
				return content;
			}
		};

		/* 300 sekundový timeout pre importu číselníka projektov 
		 *	(na DEV by stačilo 30 sek, na TEST 60 sek, na PROD nestačilo ani 120 sek)
		 * !!! pri zmene tejto hodnoty musia admini zmeniť aj timeout na reverznom proxy !!!
		 */ 
		request.setTimeout(300000);

		return call.call(request, getUrl(CApiUrl.UPLOAD_PROJECTS), requestEntity, type); // CUploadController.uploadProjects()
	}

	@Override
	public CUploadResponseContent uploadEmployees(String[] fileRows) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CUploadRequest requestEntity = new CUploadRequest();
		requestEntity.setFileRows(fileRows);

		Type type = new TypeToken<CResponse<CUploadResponseContent>>() {
		}.getType();
		AServiceCall<CUploadRequest, CUploadResponseContent, CUploadResponseContent> call = new AServiceCall<CUploadRequest, CUploadResponseContent, CUploadResponseContent>() {

			@Override
			public CUploadResponseContent getContentObject(CUploadResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.UPLOAD_EMPLOYEES), requestEntity, type); // CUploadController.uploadEmployees()
	}
}
