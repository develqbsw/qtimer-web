package sk.qbsw.sed.api.rest.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CNotifyOfApprovedRequestContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.request.CAddUserRequest;
import sk.qbsw.sed.client.request.CChangeCardCodeRequest;
import sk.qbsw.sed.client.request.CChangePasswordRequest;
import sk.qbsw.sed.client.request.CChangePin4EmpolyeesRequest;
import sk.qbsw.sed.client.request.CChangePinRequest;
import sk.qbsw.sed.client.request.CGetUserInfoRequest;
import sk.qbsw.sed.client.request.CGetUsersInfoByCardCodeRequest;
import sk.qbsw.sed.client.request.CGetUsersInfoRequest;
import sk.qbsw.sed.client.request.CLongRequest;
import sk.qbsw.sed.client.request.CModifyMyActivitiesRequest;
import sk.qbsw.sed.client.request.CModifyMyProjectsRequest;
import sk.qbsw.sed.client.request.COnlyValidIncludeMeRequest;
import sk.qbsw.sed.client.request.CRenewPasswordRequest;
import sk.qbsw.sed.client.response.CAccounts4SystemEmailContentResponse;
import sk.qbsw.sed.client.response.CAddUserResponseContent;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CEmployeeRecordListResponseContent;
import sk.qbsw.sed.client.response.CGetUserDetailsResponseContent;
import sk.qbsw.sed.client.response.CGetUserInfoResponseContent;
import sk.qbsw.sed.client.response.CGetUsersInfoResponseContent;
import sk.qbsw.sed.client.response.CLoadTreeByClientResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.ITimesheetService;
import sk.qbsw.sed.client.service.business.IUserService;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.USER)
public class CUserController extends AController {

	@Autowired
	private IUserService userService;

	@Autowired
	private ITimesheetService timesheetService;

	public CUserController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CAddUserRequest request = fromJsonToObject(json, CAddUserRequest.class);

		CResponse<CAddUserResponseContent> responseData = new CResponse<>();
		CAddUserResponseContent content = new CAddUserResponseContent();

		Long userId = null;

		try {
			userId = userService.add(request.getUserToAdd());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setUserId(userId);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/changeCardCode")
	public void changeCardCode(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CChangeCardCodeRequest request = fromJsonToObject(json, CChangeCardCodeRequest.class);

		try {
			userService.changeCardCode(request.getUserId(), request.getCardCode());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/changePassword")
	public void changePassword(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CChangePasswordRequest request = fromJsonToObject(json, CChangePasswordRequest.class);

		try {
			userService.changePassword(request.getLogin(), request.getOriginalPwd(), request.getNewPwd());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/changePin")
	public void changePin(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CChangePinRequest request = fromJsonToObject(json, CChangePinRequest.class);

		try {
			userService.changePin(request.getLogin(), request.getNewPin());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/changePin4Empolyees")
	public void changePin4Empolyees(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CChangePin4EmpolyeesRequest request = fromJsonToObject(json, CChangePin4EmpolyeesRequest.class);

		try {
			userService.changePin(request.getUserId(), request.getNewPin());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/getAccounts4Notification")
	public void getAccounts4Notification(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLongRequest request = fromJsonToObject(json, CLongRequest.class);
		CResponse<CAccounts4SystemEmailContentResponse> responseData = new CResponse<>();
		CAccounts4SystemEmailContentResponse content = new CAccounts4SystemEmailContentResponse();

		List<CUserSystemEmailRecord> accounts4SystemEmail = null;

		try {
			accounts4SystemEmail = userService.getAccounts4Notification(request.getId());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setAccounts4SystemEmail(accounts4SystemEmail);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getAccounts4SystemEmail")
	public void getAccounts4SystemEmail(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CAccounts4SystemEmailContentResponse> responseData = new CResponse<>();
		CAccounts4SystemEmailContentResponse content = new CAccounts4SystemEmailContentResponse();

		List<CUserSystemEmailRecord> accounts4SystemEmail = null;

		try {
			accounts4SystemEmail = userService.getAccounts4SystemEmail();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setAccounts4SystemEmail(accounts4SystemEmail);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getAllTypeEmployment")
	public void getAllTypeEmployment(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = userService.getAllTypeEmployment();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getAllTypesOfHomeOfficePermission")
	public void getAllTypesOfHomeOfficePermission(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = userService.getAllTypesOfHomeOfficePermission();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getAllValidEmployees")
	public void getAllValidEmployees(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = userService.getAllValidEmployees();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getAllValidEmployeesList")
	public void getAllValidEmployeesList(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CEmployeeRecordListResponseContent> responseData = new CResponse<>();
		CEmployeeRecordListResponseContent content = new CEmployeeRecordListResponseContent();

		List<CEmployeeRecord> employeeRecordList = null;

		try {
			employeeRecordList = userService.getAllValidEmployeesList();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setEmployeeRecordList(employeeRecordList);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getUserDetails")
	public void getUserDetails(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLongRequest request = fromJsonToObject(json, CLongRequest.class);

		CResponse<CGetUserDetailsResponseContent> responseData = new CResponse<>();
		CGetUserDetailsResponseContent content = new CGetUserDetailsResponseContent();

		CUserDetailRecord user = null;

		try {
			user = userService.getUserDetails(request.getId());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setUser(user);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getUserInfo")
	public void getUserInfo(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetUserInfoRequest request = fromJsonToObject(json, CGetUserInfoRequest.class);

		CResponse<CGetUserInfoResponseContent> responseData = new CResponse<>();
		CGetUserInfoResponseContent content = new CGetUserInfoResponseContent();

		CLoggedUserRecord user = null;
		CPredefinedInteligentTimeStamp predefinedTimeStamp = null;

		try {
			user = userService.getUserInfo(request.getPin());
			predefinedTimeStamp = timesheetService.loadPredefinedInteligentValueForReceptionPanel(user.getUserId(), new Date());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setUser(user);
		content.setPredefinedInteligentTimeStamp(predefinedTimeStamp);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getUsersInfo")
	public void getUsersInfo(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetUsersInfoRequest request = fromJsonToObject(json, CGetUsersInfoRequest.class);

		CResponse<CGetUsersInfoResponseContent> responseData = new CResponse<>();
		CGetUsersInfoResponseContent content = new CGetUsersInfoResponseContent();

		List<CLoggedUserRecord> users = null;
		CPredefinedInteligentTimeStamp predefinedTimeStamp = null;

		try {
			users = userService.getUsersInfo(request.getPins());

			if (!users.isEmpty()) {
				predefinedTimeStamp = timesheetService.loadPredefinedInteligentValueForReceptionPanel(users.get(0).getUserId(), new Date());
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setUsers(users);
		content.setPredefinedInteligentTimeStamp(predefinedTimeStamp);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getUsersInfoByCardCode")
	public void getUsersInfoByCardCode(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetUsersInfoByCardCodeRequest request = fromJsonToObject(json, CGetUsersInfoByCardCodeRequest.class);

		CResponse<CGetUsersInfoResponseContent> responseData = new CResponse<>();
		CGetUsersInfoResponseContent content = new CGetUsersInfoResponseContent();

		List<CLoggedUserRecord> users = null;
		CPredefinedInteligentTimeStamp predefinedTimeStamp = null;

		try {
			users = userService.getUsersInfoByCardCodes(request.getCardCodes());

			if (!users.isEmpty()) {
				predefinedTimeStamp = timesheetService.loadPredefinedInteligentValueForReceptionPanel(users.get(0).getUserId(), new Date());
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setUsers(users);
		content.setPredefinedInteligentTimeStamp(predefinedTimeStamp);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.USER__LIST_FOR_LOGGED_USER)
	public void listLoggedUser(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = userService.listLoggedUser();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.USER_NOTIFIED_USERS)
	public void listNotifiedUsers(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Boolean request = fromJsonToObject(json, Boolean.class);

		CResponse<CLoadTreeByClientResponseContent> responseData = new CResponse<>();
		CLoadTreeByClientResponseContent content = new CLoadTreeByClientResponseContent();

		List<CViewOrganizationTreeNodeRecord> list = null;

		try {
			list = userService.listNotifiedUsers(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setList(list);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.USER_SUBORDINATE_USERS)
	public void listSubordinateUsers(@RequestBody String json, HttpServletResponse response) throws CApiException {
		COnlyValidIncludeMeRequest request = fromJsonToObject(json, COnlyValidIncludeMeRequest.class);

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = userService.listSubordinateUsers(request.getOnlyValid().booleanValue(), request.getIncludeMe().booleanValue());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/modify")
	public void modify(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CUserDetailRecord request = fromJsonToObject(json, CUserDetailRecord.class);

		try {
			userService.modify(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping(value = CApiUrl.USER_MODIFY_MY_ACTIVITIES)
	public void modifyMyActivities(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyMyActivitiesRequest request = fromJsonToObject(json, CModifyMyActivitiesRequest.class);

		userService.modifyMyActivities(request.getActivityId(), request.isFlagMyActivity(), request.getUserId());

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping(value = CApiUrl.USER_MODIFY_MY_FAVORITES)
	public void modifyMyFavorites(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyMyProjectsRequest request = fromJsonToObject(json, CModifyMyProjectsRequest.class);

		userService.modifyMyFavourites(request.getProjectId(), request.isFlagMyProject(), request.getUserId());

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping(value = CApiUrl.USER_MODIFY_MY_PROJECTS)
	public void modifyMyProjects(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyMyProjectsRequest request = fromJsonToObject(json, CModifyMyProjectsRequest.class);

		userService.modifyMyProjects(request.getProjectId(), request.isFlagMyProject(), request.getUserId());

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/renewPassword")
	public void renewPassword(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRenewPasswordRequest request = fromJsonToObject(json, CRenewPasswordRequest.class);

		try {
			userService.renewPassword(request.getLogin(), request.getEmail());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/saveNotifyOfRequest")
	public void saveNotifyOfApprovedRequest(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CNotifyOfApprovedRequestContainer request = fromJsonToObject(json, CNotifyOfApprovedRequestContainer.class);

		try {
			userService.saveNotifyOfApprovedRequest(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/saveSystemEmailAccounts")
	public void saveSystemEmailAccounts(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CUserSystemEmailContainer request = fromJsonToObject(json, CUserSystemEmailContainer.class);

		try {
			userService.saveSystemEmailAccounts(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
