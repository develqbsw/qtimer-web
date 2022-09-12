package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.request.CLoadTreeByClientRequest;
import sk.qbsw.sed.client.request.CLoadTreeByClientUserRequest;
import sk.qbsw.sed.client.request.CMoveOrganizationTreeRequest;
import sk.qbsw.sed.client.response.CLoadTreeByClientResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.IOrganizationTreeService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.ORGANIZATION_TREE)
public class COrganizationTreeController extends AController {

	@Autowired
	private IOrganizationTreeService organizationTreeService;

	public COrganizationTreeController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(value = CApiUrl.ORGANIZATION_TREE_LOAD_TREE_BY_CLIENT, method = RequestMethod.POST, consumes = { "application/json; charset=UTF-8" }, produces = {"application/json; charset=UTF-8" })
	public void loadTreeByClient(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLoadTreeByClientRequest request = fromJsonToObject(json, CLoadTreeByClientRequest.class);

		CResponse<CLoadTreeByClientResponseContent> responseData = new CResponse<>();
		CLoadTreeByClientResponseContent content = new CLoadTreeByClientResponseContent();

		List<CViewOrganizationTreeNodeRecord> list = organizationTreeService.loadTreeByClient(request.getClientId(), request.getOnlyValid());

		content.setList(list);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("loadTreeByClientUser")
	public void loadTreeByClientUser(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLoadTreeByClientUserRequest request = fromJsonToObject(json, CLoadTreeByClientUserRequest.class);

		CResponse<CLoadTreeByClientResponseContent> responseData = new CResponse<>();
		CLoadTreeByClientResponseContent content = new CLoadTreeByClientResponseContent();

		List<CViewOrganizationTreeNodeRecord> list = organizationTreeService.loadTreeByClientUser(request.getClientId(), request.getUserId(), request.getOnlyValid(), request.getWithoutMe());

		content.setList(list);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.ORGANIZATION_TREE_MOVE)
	public void move(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CMoveOrganizationTreeRequest request = fromJsonToObject(json, CMoveOrganizationTreeRequest.class);

		try {
			organizationTreeService.move(request.getTreeNodeFrom(), request.getTreeNodeTo(), request.getMode(), request.getTimestamp());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
