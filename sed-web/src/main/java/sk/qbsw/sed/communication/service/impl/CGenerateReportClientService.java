package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.client.response.CGenerateReportResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IGenerateReportClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CGenerateReportClientService extends AClientService implements IGenerateReportClientService {
	
	/**
	 * @deprecated
	 */
	@Deprecated
	public CGenerateReportClientService() {
		super(CApiUrl.GENERATE_REPORT);
	}

	@Override
	public CGenerateReportResponseContent generate(CGenerateReportRequest requestEntity) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CGenerateReportResponseContent>>() {
		}.getType();
		AServiceCall<CGenerateReportRequest, CGenerateReportResponseContent, CGenerateReportResponseContent> call = new AServiceCall<CGenerateReportRequest, CGenerateReportResponseContent, CGenerateReportResponseContent>() {

			@Override
			public CGenerateReportResponseContent getContentObject(CGenerateReportResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.GENERATE_REPORT_GENERATE), requestEntity, type); // CGenerateReportController.generate()

	}

	@Override
	public CGenerateReportResponseContent generateEmployeesExport(CGenerateEmployeesReportRequest requestEntity) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CGenerateReportResponseContent>>() {
		}.getType();
		AServiceCall<CGenerateEmployeesReportRequest, CGenerateReportResponseContent, CGenerateReportResponseContent> call = new AServiceCall<CGenerateEmployeesReportRequest, CGenerateReportResponseContent, CGenerateReportResponseContent>() {

			@Override
			public CGenerateReportResponseContent getContentObject(CGenerateReportResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.GENERATE_REPORT_EMPLOYEES), requestEntity, type); // CGenerateReportController.generateEmployeesExport()
	}

	@Override
	public CGenerateReportResponseContent generateWorkplaceExport(CGenerateEmployeesReportRequest requestEntity) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CGenerateReportResponseContent>>() {
		}.getType();
		AServiceCall<CGenerateEmployeesReportRequest, CGenerateReportResponseContent, CGenerateReportResponseContent> call = new AServiceCall<CGenerateEmployeesReportRequest, CGenerateReportResponseContent, CGenerateReportResponseContent>() {

			@Override
			public CGenerateReportResponseContent getContentObject(CGenerateReportResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.GENERATE_REPORT_WORKPLACE), requestEntity, type); // CGenerateReportController.generateWorkplaceExport()
	}
}
