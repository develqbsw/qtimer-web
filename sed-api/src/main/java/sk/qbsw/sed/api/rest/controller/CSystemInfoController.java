package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.service.system.ISystemInfoService;

@Controller
@RequestMapping(value = "/systemInfo")
public class CSystemInfoController extends AController {

	@Autowired
	private ISystemInfoService systemInfoService;

	public CSystemInfoController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/getVersion")
	public void getVersion(@RequestBody String json, HttpServletResponse response) {
		CResponse<CStringResponseContent> responseData = new CResponse<>();
		CStringResponseContent content = new CStringResponseContent();

		String value = systemInfoService.getVersion();

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
