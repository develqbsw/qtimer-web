package sk.qbsw.sed.api.rest.controller;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.client.response.CGenerateReportResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.framework.report.generator.IReportGenerator;
import sk.qbsw.sed.framework.report.generator.name.IReportNameGenerator;

@Controller
@RequestMapping(value = CApiUrl.GENERATE_REPORT)
public class CGenerateReportController extends AController {
	@Autowired(required = true)
	private IReportGenerator reportGenerator;

	@Autowired(required = true)
	private IReportNameGenerator reportNameGenerator;

	public CGenerateReportController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("generate")
	public void generate(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGenerateReportRequest request = fromJsonToObject(json, CGenerateReportRequest.class);

		CResponse<CGenerateReportResponseContent> responseData = new CResponse<>();
		CGenerateReportResponseContent content = new CGenerateReportResponseContent();

		String reportName = null;

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			reportName = this.reportNameGenerator.generateReportName(request);

			this.reportGenerator.generateReport(os, request);
			content.setByteArray(os.toByteArray());
			content.setFileName(reportName);
			responseData.setContent(content);
		} catch (Exception e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("generateEmployeesReport")
	public void generateEmployeesExport(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGenerateEmployeesReportRequest request = fromJsonToObject(json, CGenerateEmployeesReportRequest.class);

		CResponse<CGenerateReportResponseContent> responseData = new CResponse<>();
		CGenerateReportResponseContent content = new CGenerateReportResponseContent();

		String reportName = null;

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			reportName = "employees_report.xls";

			this.reportGenerator.generateEmployeesReport(os, request);
			content.setByteArray(os.toByteArray());
			content.setFileName(reportName);
			responseData.setContent(content);
		} catch (Exception e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("generateWorkplaceReport")
	public void generateWorkplaceExport(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGenerateEmployeesReportRequest request = fromJsonToObject(json, CGenerateEmployeesReportRequest.class);

		CResponse<CGenerateReportResponseContent> responseData = new CResponse<>();
		CGenerateReportResponseContent content = new CGenerateReportResponseContent();

		String reportName = null;

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			reportName = "employees_working_place.xls";

			this.reportGenerator.generateWorkplaceReport(os, request);
			content.setByteArray(os.toByteArray());
			content.setFileName(reportName);
			responseData.setContent(content);
		} catch (Exception e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
