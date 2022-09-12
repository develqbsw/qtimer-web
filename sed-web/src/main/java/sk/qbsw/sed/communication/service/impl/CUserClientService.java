package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.request.CAddUserRequest;
import sk.qbsw.sed.client.request.CChangeCardCodeRequest;
import sk.qbsw.sed.client.request.CChangePasswordRequest;
import sk.qbsw.sed.client.request.CChangePin4EmpolyeesRequest;
import sk.qbsw.sed.client.request.CChangePinRequest;
import sk.qbsw.sed.client.request.CLongRequest;
import sk.qbsw.sed.client.request.CModifyMyActivitiesRequest;
import sk.qbsw.sed.client.request.CModifyMyProjectsRequest;
import sk.qbsw.sed.client.request.COnlyValidIncludeMeRequest;
import sk.qbsw.sed.client.request.CRenewPasswordRequest;
import sk.qbsw.sed.client.response.CAccounts4SystemEmailContentResponse;
import sk.qbsw.sed.client.response.CAddUserResponseContent;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CEmptyResponseContent;
import sk.qbsw.sed.client.response.CGetUserDetailsResponseContent;
import sk.qbsw.sed.client.response.CLoadTreeByClientResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IUserClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.utils.CCacheUtils;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CUserClientService extends AClientService implements IUserClientService {
	@Autowired
	private CCacheUtils cache;

	/**
	 * @deprecated
	 */
	@Deprecated
	public CUserClientService() {
		super(CApiUrl.USER);
	}

	@Override
	public Long add(CUserDetailRecord userToAdd) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CAddUserRequest requestEntity = new CAddUserRequest(userToAdd, null);
		Type type = new TypeToken<CResponse<CAddUserResponseContent>>() {
		}.getType();
		AServiceCall<CAddUserRequest, CAddUserResponseContent, Long> call = new AServiceCall<CAddUserRequest, CAddUserResponseContent, Long>() {

			@Override
			public Long getContentObject(CAddUserResponseContent content) {
				return content.getUserId();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_ADD), requestEntity, type); // CUserController.add()
	}

	@Override
	public CUserDetailRecord getUserDetails(Long id) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CLongRequest requestEntity = new CLongRequest(id);

		Type type = new TypeToken<CResponse<CGetUserDetailsResponseContent>>() {
		}.getType();
		AServiceCall<CLongRequest, CGetUserDetailsResponseContent, CUserDetailRecord> call = new AServiceCall<CLongRequest, CGetUserDetailsResponseContent, CUserDetailRecord>() {

			@Override
			public CUserDetailRecord getContentObject(CGetUserDetailsResponseContent content) {
				return content.getUser();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_GET_USER_DETAILS), requestEntity, type); // CUserController.getUserDetails()
	}

	@Override
	public CLockRecord modify(CUserDetailRecord toModify) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CUserDetailRecord, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CUserDetailRecord, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_MODIFY), toModify, type); // CUserController.modify()
	}

	@Override
	public List<CCodeListRecord> listSubordinateUsers(Boolean showOnlyValid, Boolean includeMe) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		COnlyValidIncludeMeRequest cOnlyValidIncludeMeRequest = new COnlyValidIncludeMeRequest(showOnlyValid, includeMe);

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<COnlyValidIncludeMeRequest, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<COnlyValidIncludeMeRequest, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_SUBORDINATE_USERS), cOnlyValidIncludeMeRequest, type); // CUserController.listSubordinateUsers()
	}

	@Override
	public List<CCodeListRecord> getAllValidEmployees() throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_GET_ALL_VALID_EMPLOYEES), null, type); // CUserController.getAllValidEmployees()
	}

	@Override
	public List<CUserSystemEmailRecord> getAccounts4SystemEmail() throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CAccounts4SystemEmailContentResponse>>() {
		}.getType();
		AServiceCall<Void, CAccounts4SystemEmailContentResponse, List<CUserSystemEmailRecord>> call = new AServiceCall<Void, CAccounts4SystemEmailContentResponse, List<CUserSystemEmailRecord>>() {

			@Override
			public List<CUserSystemEmailRecord> getContentObject(CAccounts4SystemEmailContentResponse content) {
				return content.getAccounts4SystemEmail();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_GET_ACCOUNTS_FOR_SYSTEM_EMAIL), null, type); // CUserController.getAccounts4SystemEmail()
	}

	@Override
	public List<CUserSystemEmailRecord> getAccounts4Notification(Long userRequestID) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CLongRequest requestEntity = new CLongRequest(userRequestID);

		Type type = new TypeToken<CResponse<CAccounts4SystemEmailContentResponse>>() {
		}.getType();
		AServiceCall<CLongRequest, CAccounts4SystemEmailContentResponse, List<CUserSystemEmailRecord>> call = new AServiceCall<CLongRequest, CAccounts4SystemEmailContentResponse, List<CUserSystemEmailRecord>>() {

			@Override
			public List<CUserSystemEmailRecord> getContentObject(CAccounts4SystemEmailContentResponse content) {
				return content.getAccounts4SystemEmail();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_GET_ACCOUNTS_FOR_APPROVED_NOTIFICATION), requestEntity, type); // CUserController.getAccounts4Notification()
	}

	@Override
	public void saveSystemEmailAccounts(CUserSystemEmailContainer data) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CUserSystemEmailContainer, CEmptyResponseContent, Void> call = new AServiceCall<CUserSystemEmailContainer, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_SAVE_ACCOUNTS_FOR_SYSTEM_EMAIL), data, type); // CUserController.saveSystemEmailAccounts()
	}

	@Override
	public void saveNotifyOfApprovedRequest(CNotifyOfApprovedRequestContainer data) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CNotifyOfApprovedRequestContainer, CEmptyResponseContent, Void> call = new AServiceCall<CNotifyOfApprovedRequestContainer, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_SAVE_ACCOUNTS_FOR_NOFITY_REQUEST), data, type); // CUserController.saveNotifyOfApprovedRequest()
	}

	@Override
	public void modifyMyProjects(Long projectId, boolean flagMyProject, Long userId) throws CBussinessDataException {
		cache.deleteCacheForOrg(CCacheUtils.CACHE_PROJECT);

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyMyProjectsRequest requestEntity = new CModifyMyProjectsRequest(projectId, flagMyProject, userId);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CModifyMyProjectsRequest, CEmptyResponseContent, Void> call = new AServiceCall<CModifyMyProjectsRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_MODIFY_MY_PROJECTS), requestEntity, type); // CUserController.modifyMyProjects()
	}

	@Override
	public void modifyMyActivities(Long activityId, boolean flagMyActivty, Long userId) throws CBussinessDataException {

		cache.deleteCacheForOrg(CCacheUtils.CACHE_ACTIVITY);
		cache.deleteCacheForOrg(CCacheUtils.CACHE_ACTIVITY_WORKING);

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyMyActivitiesRequest requestEntity = new CModifyMyActivitiesRequest(activityId, flagMyActivty, userId);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CModifyMyActivitiesRequest, CEmptyResponseContent, Void> call = new AServiceCall<CModifyMyActivitiesRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_MODIFY_MY_ACTIVITIES), requestEntity, type); // CUserController.modifyMyActivities()

	}

	@Override
	public void modifyMyFavorites(Long favouriteUserId, boolean flagMyFavourite, Long userId) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyMyProjectsRequest requestEntity = new CModifyMyProjectsRequest(favouriteUserId, flagMyFavourite, userId);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CModifyMyProjectsRequest, CEmptyResponseContent, Void> call = new AServiceCall<CModifyMyProjectsRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_MODIFY_MY_FAVORITES), requestEntity, type); // CUserController.modifyMyFavorites()
	}

	@Override
	public void renewPassword(String login, String email) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(null);
		CRenewPasswordRequest requestEntity = new CRenewPasswordRequest(login, email);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CRenewPasswordRequest, CEmptyResponseContent, Void> call = new AServiceCall<CRenewPasswordRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_RENEW_PASSWORD), requestEntity, type); // CUserController.renewPassword()
	}

	@Override
	public void changePassword(String login, String originalPwd, String newPwd) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CChangePasswordRequest requestEntity = new CChangePasswordRequest(login, originalPwd, newPwd);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CChangePasswordRequest, CEmptyResponseContent, Void> call = new AServiceCall<CChangePasswordRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_CHANGE_PASSWORD), requestEntity, type); // CUserController.changePassword()
	}

	@Override
	public void changePin(String login, String newPin) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CChangePinRequest requestEntity = new CChangePinRequest(login, newPin);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CChangePinRequest, CEmptyResponseContent, Void> call = new AServiceCall<CChangePinRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_CHANGE_PIN), requestEntity, type); // CUserController.changePin()
	}

	@Override
	public void changePin(Long userId, String newPin) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CChangePin4EmpolyeesRequest requestEntity = new CChangePin4EmpolyeesRequest(userId, newPin);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CChangePin4EmpolyeesRequest, CEmptyResponseContent, Void> call = new AServiceCall<CChangePin4EmpolyeesRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_CHANGE_PIN_4_EMPLOYEES), requestEntity, type); // CUserController.changePin4Empolyees()
	}

	@Override
	public void changeCardCode(Long userId, String cardCode) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CChangeCardCodeRequest requestEntity = new CChangeCardCodeRequest(userId, cardCode);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CChangeCardCodeRequest, CEmptyResponseContent, Void> call = new AServiceCall<CChangeCardCodeRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.USER_CHANGE_CARD_CODE), requestEntity, type); // CUserController.changeCardCode()
	}

	@Override
	public List<CCodeListRecord> getAllTypesOfEmployment() throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_ALL_TYPES_EMPLOYMENT), null, type); // CUserController.getAllTypeEmployment()
	}

	@Override
	public List<CCodeListRecord> getAllTypesOfHomeOfficePermission() throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_ALL_TYPES_OF_HO_PERMISSION), null, type); // CUserController.getAllTypesOfHomeOfficePermission()
	}

	@Override
	public List<CViewOrganizationTreeNodeRecord> listNotifiedUsers(Boolean withSubordiantes) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CLoadTreeByClientResponseContent>>() {
		}.getType();
		AServiceCall<Boolean, CLoadTreeByClientResponseContent, List<CViewOrganizationTreeNodeRecord>> call = new AServiceCall<Boolean, CLoadTreeByClientResponseContent, List<CViewOrganizationTreeNodeRecord>>() {

			@Override
			public List<CViewOrganizationTreeNodeRecord> getContentObject(CLoadTreeByClientResponseContent content) {
				return content.getList();
			}
		};
		return call.call(request, getUrl(CApiUrl.USER_NOTIFIED_USERS), withSubordiantes, type); // CUserController.listNotifiedUsers()
	}
}
