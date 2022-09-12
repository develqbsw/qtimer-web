package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.request.CLoadTreeByClientRequest;
import sk.qbsw.sed.client.request.CLoadTreeByClientUserRequest;
import sk.qbsw.sed.client.request.CMoveOrganizationTreeRequest;
import sk.qbsw.sed.client.response.CLoadTreeByClientResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.model.CServiceCallEmptyResponse;
import sk.qbsw.sed.communication.service.IOrganizationTreeClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class COrganizationTreeClientService extends AClientService implements IOrganizationTreeClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public COrganizationTreeClientService() {
		super(CApiUrl.ORGANIZATION_TREE);
	}

	@Override
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClient(Long clientId, Boolean onlyValid) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CLoadTreeByClientRequest requestEntity = new CLoadTreeByClientRequest(clientId, onlyValid);
		Type type = new TypeToken<CResponse<CLoadTreeByClientResponseContent>>() {
		}.getType();
		AServiceCall<CLoadTreeByClientRequest, CLoadTreeByClientResponseContent, List<CViewOrganizationTreeNodeRecord>> call = new AServiceCall<CLoadTreeByClientRequest, CLoadTreeByClientResponseContent, List<CViewOrganizationTreeNodeRecord>>() {

			@Override
			public List<CViewOrganizationTreeNodeRecord> getContentObject(CLoadTreeByClientResponseContent content) {
				return content.getList();
			}
		};
		return call.call(request, getUrl(CApiUrl.ORGANIZATION_TREE_LOAD_TREE_BY_CLIENT), requestEntity, type); // COrganizationTreeController.loadTreeByClient()

	}

	@Override
	public List<CViewOrganizationTreeNodeRecord> loadTreeByClientUser(Long clientId, Long userId, Boolean onlyValid, Boolean withoutMe) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CLoadTreeByClientUserRequest requestEntity = new CLoadTreeByClientUserRequest(clientId, userId, onlyValid, withoutMe);
		Type type = new TypeToken<CResponse<CLoadTreeByClientResponseContent>>() {
		}.getType();
		AServiceCall<CLoadTreeByClientUserRequest, CLoadTreeByClientResponseContent, List<CViewOrganizationTreeNodeRecord>> call = new AServiceCall<CLoadTreeByClientUserRequest, CLoadTreeByClientResponseContent, List<CViewOrganizationTreeNodeRecord>>() {

			@Override
			public List<CViewOrganizationTreeNodeRecord> getContentObject(CLoadTreeByClientResponseContent content) {
				return content.getList();
			}
		};
		return call.call(request, getUrl(CApiUrl.ORGANIZATION_TREE_LOAD_TREE_BY_CLIENT_USER), requestEntity, type); // COrganizationTreeController.loadTreeByClientUser()
	}

	@Override
	public void move(Long treeNodeFrom, Long treeNodeTo, String mode, Date timestamp) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CMoveOrganizationTreeRequest requestEntity = new CMoveOrganizationTreeRequest(treeNodeFrom, treeNodeTo, mode, timestamp);
		CServiceCallEmptyResponse<CMoveOrganizationTreeRequest> call = new CServiceCallEmptyResponse<>();
		call.call(request, getUrl(CApiUrl.ORGANIZATION_TREE_MOVE), requestEntity); // COrganizationTreeController.move()
	}
}
