package sk.qbsw.sed.client;

public class CApiUrl {
	public static final String SECURITY = "/security";
	public static final String SECURITY_LOGIN = "/login";
	public static final String SECURITY_AUTO_LOGIN = "/loginByAutoLoginToken";
	public static final String SECURITY_LOGOUT = "/logout";

	public static final String TIMESHEET = "/timesheet";
	public static final String TIMESHEET_DATA_FOR_GRAPH_OF_PROJECTS = "/getDataForGraphOfProjects";
	public static final String TIMESHEET_DATA_FOR_GRAPH_OF_STATS = "/getDataForGraphOfStats";
	public static final String TIMESHEET_DATA_FOR_ATTENDANCE = "/getDataForGraphOfAttendance";
	public static final String TIMESHEET_GET_DETAIL = "/getDetail";
	public static final String TIMESHEET_ADD = "/add";
	public static final String TIMESHEET_MODIFY = "/modify";
	public static final String TIMESHEET_DELETE = "/delete";
	public static final String TIMESHEET_SPLIT = "/split";
	public static final String TIMESHEET_LOAD_PREDEFINED_VALUE = "/loadPredefinedTimestamp";
	public static final String TIMESHEET_LOAD_PREDEFINED_TIMESTAMP_FOR_TIMER_PANEL = "/loadPredefinedTimestampForTimerPanel";
	public static final String TIMESHEET_LOAD_PREDEFINED_TIMESTAMP_FOR_RECEPTION_PANEL = "/loadPredefinedTimestampForReceptionPanel";
	public static final String TIMESHEET_LOCK = "/lock";
	public static final String TIMESHEET_UNLOCK = "/unlock";
	public static final String TIMESHEET_LAST_PROJECT_TO_ACTIVITY_RELATION_MAP = "/getLastProjectToActivityRelationMap";
	public static final String TIMESHEET_START_NON_WORKING = "/startNonWorking";
	public static final String TIMESHEET_START_WORKING = "/startWorking";
	public static final String TIMESHEET_MODIFY_WORKING = "/modifyWorking";
	public static final String TIMESHEET_STOP_WORKING = "/stopWorking";
	public static final String TIMESHEET_STOP_NON_WORKING = "/stopNonWorking";
	public static final String TIMESHEET_STOP_NON_WORKING_WITH_CONTINUE_WORK_FLAG = "/stopNonWorkingWithContinueWorkFlag";
	public static final String TIMESHEET_STOP_INTERACTIVE_WORK = "/stopInteractiveWork";
	public static final String TIMESHEET_MULTIPLE_ACTIVITY = "/multipleActivity";
	public static final String TIMESHEET_CONFIRM_RECORDS = "/confirmTimesheetRecords";
	public static final String TIMESHEET_CANCEL_RECORDS = "/cancelTimesheetRecords";
	public static final String TIMESHEET_APPROVED_EMPLOYEES_ABSENCE_RECORDS = "/generateApprovedEmployeesAbsenceRecords";
	public static final String TIMESHEET_LIST_OF_USERS_WITH_CORRUPTED_SUMMARY_REPORT = "/getListOfUsersWithCorruptedSummaryReport";
	public static final String TIMESHEET_USER_TIMESTAMPS_FROM_PREPARED_ITEMS = "/generateUserTimestampsFromPreparedItems";
	public static final String TIMESHEET_USER_LAST_EXTERNAL_PROGRAM_ACTIVITY = "/findUserLastExternalProgramActivity";
	public static final String TIMESHEET_INFO_FOR_MOBILE_TIMER = "/getInfoForMobileTimer";
	public static final String TIMESHEET_SUM_AND_AVERAGE_TIME = "/getSumAndAverageTime";
	public static final String TIMESHEET_SUM_AND_AVERAGE_TIME_USERS = "/getSumAndAverageTimeForUsers";
	public static final String TIMESHEET_SHOW_EDIT_BUTTON_ON_DETAIL = "/showEditButtonOnDetail";

	public static final String ORGANIZATION_TREE = "/organizationTree";
	public static final String ORGANIZATION_TREE_LOAD_TREE_BY_CLIENT = "/loadTreeByClient";
	public static final String ORGANIZATION_TREE_LOAD_TREE_BY_CLIENT_USER = "/loadTreeByClientUser";
	public static final String ORGANIZATION_TREE_MOVE = "/move";
	public static final String BRW_MY_TIME_STAMP = "/brwMyTimeStamp";
	public static final String BRW_MY_TIME_STAMP_UPDATE = "/update";
	public static final String BRW_MY_TIME_STAMP_LOAD_DATA = "/loadData";
	public static final String BRW_MY_TIME_STAMP_COUNT = "/count";
	public static final String BRW_MY_TIME_STAMP_GET_TIME_INTERVAL = "/getWorkTimeInInterval";
	public static final String BRW_MY_TIME_STAMP_MASS_CHANGE = "/massChangeTimestamps";

	public static final String BRW_MY_TIME_STAMP_GENERATE = "/brwMyTimeStampGenerate";
	public static final String BRW_MY_TIME_STAMP_GENERATE_UPDATE = "/update";
	public static final String BRW_MY_TIME_STAMP_GENERATE_ADD = "/add";
	public static final String BRW_MY_TIME_STAMP_GENERATE_FETCH = "/fetch";
	public static final String BRW_MY_TIME_STAMP_GENERATE_DELETE = "/delete";
	public static final String BRW_MY_TIME_STAMP_GENERATE_DELETE_ALL = "/deleteAll";

	public static final String ACTIVITY = "/activity";
	public static final String ACTIVITY_GET_VALID_RECORDS = "/getValidRecords";
	public static final String ACTIVITY_GET_VALID_WORKING_RECORDS_FOR_USER = "/getValidWorkingRecordsForUser";
	public static final String ACTIVITY_GET_VALID_WORKING_RECORDS = "/getValidWorkingRecords";
	public static final String ACTIVITY_GET_ALL_RECORDS = "/getAllRecords";
	public static final String ACTIVITY_GET_DETAIL = "/getDetail";
	public static final String ACTIVITY_MODIFY = "/modify";
	public static final String ACTIVITY_ADD = "/add";
	public static final String ACTIVITY_GET_VALID_RECORDS_FOR_LIMITS = "/getValidRecordsForLimits";

	public static final String ACTIVITY_TIME_LIMITS = "/activityTimeLimits";
	public static final String ACTIVITY_TIME_LIMITS_GET_VALID_ACTIVITY_GROUPS = "/getValidActivityGroups";

	public static final String BRW_ACTIVITY = "/brwActivity";
	public static final String BRW_ACTIVITY_LOAD_DATA = "/loadData";
	public static final String BRW_ACTIVITY_COUNT = "/count";

	public static final String PROJECT = "/project";
	public static final String PROJECT_GET_VALID_RECORDS = "/getValidRecords";
	public static final String PROJECT_GET_ALL_RECORDS = "/getAllRecords";
	public static final String PROJECT_ALL_RECORDS_WITH_GROUPS = "/getAllRecordsWithGroups";
	public static final String PROJECT_GET_DETAIL = "/getDetail";
	public static final String PROJECT_MODIFY = "/modify";
	public static final String PROJECT_ADD = "/add";

	public static final String BRW_PROJECT = "/brwProject";
	public static final String BRW_PROJECT_LOAD_DATA = "/loadData";
	public static final String BRW_PROJECT_COUNT = "/count";

	public static final String BRW_USER_PROJECT = "/brwUserProject";
	public static final String BRW_USER_PROJECT_LOAD_DATA = "/loadData";
	public static final String BRW_USER_PROJECT_COUNT = "/count";

	public static final String BRW_USER_ACTIVITY = "/brwUserActivity";
	public static final String BRW_USER_ACTIVITY_LOAD_DATA = "/loadData";
	public static final String BRW_USER_ACTIVITY_COUNT = "/count";

	public static final String HOLIDAY = "/holiday";
	public static final String HOLIDAY_GET_DETAIL = "/getDetail";
	public static final String HOLIDAY_MODIFY = "/modify";
	public static final String HOLIDAY_ADD = "/add";
	public static final String HOLIDAY_CLONE_FOR_NEXT_YEAR = "/cloneCurrentYearClientRecordsForNextYear";
	public static final String HOLIDAY_GET_CLIENT_RECORDS_FOR_THE_YEAR = "/getClientRecordsForTheYear";

	public static final String BRW_HOLIDAY = "/brwHoliday";
	public static final String BRW_HOLIDAY_LOAD_DATA = "/loadData";
	public static final String BRW_HOLIDAY_COUNT = "/count";

	public static final String REQUEST_REASON = "/requestReason";
	public static final String REQUEST_REASON_GET_DETAIL = "/getDetail";
	public static final String REQUEST_REASON_SAVE = "/save";
	public static final String REQUEST_REASON_LIST_FOR_LISTBOX = "/getReasonListsForListbox";
	public static final String REQUEST_REASON_LIST = "/getReasonLists";

	public static final String BRW_REQUEST_REASON = "/brwRequestReason";
	public static final String BRW_REQUEST_REASON_LOAD_DATA = "/loadData";
	public static final String BRW_REQUEST_REASON_COUNT = "/count";

	public static final String REQUEST_TYPE = "/requestType";
	public static final String REQUEST_TYPE_RECORDS_FOR_REQUEST_REASON = "/getValidRecordsForRequestReason";
	public static final String REQUEST_TYPE_GET_VALID_RECORDS = "/getValidRecords";

	public static final String ACTIVITY_RESTRICTION = "/activityTimeLimits";
	public static final String ACTIVITY_RESTRICTION_GROUP_GET_DETAIL = "/loadGroupDetail";
	public static final String ACTIVITY_RESTRICTION_GROUP_SAVE = "/saveGroup";

	public static final String ACTIVITY_RESTRICTION_INTERVAL_GET_DETAIL = "/loadIntervalDetail";
	public static final String ACTIVITY_RESTRICTION_INTERVAL_SAVE = "/saveInterval";

	public static final String ACTIVITY_RESTRICTION_LOAD_EMPLOYEE_LIMITS_DETAIL = "/loadEmployeeLimitsDetail";
	public static final String ACTIVITY_RESTRICTION_SAVE_EMPLOYEE_LIMITS = "/saveEmployeeLimits";

	public static final String ACTIVITY_RESTRICTION_GET_VALID_ACTIVITY_GROUPS = "/getValidActivityGroups";

	public static final String BRW_ACTIVITY_RESTRICTION_GROUP = "/brwGroupsAI";
	public static final String BRW_ACTIVITY_RESTRICTION_GROUP_LOAD_DATA = "/loadData";
	public static final String BRW_ACTIVITY_RESTRICTION_GROUP_COUNT = "/count";

	public static final String BRW_ACTIVITY_RESTRICTION_INTERVAL = "/brwActivityInterval";
	public static final String BRW_ACTIVITY_RESTRICTION_INTERVAL_LOAD_DATA = "/loadData";
	public static final String BRW_ACTIVITY_RESTRICTION_INTERVAL_COUNT = "/count";

	public static final String BRW_EMPLOYEE = "/brwEmployee";
	public static final String BRW_EMPLOYEE_LOAD_DATA = "/loadData";
	public static final String BRW_EMPLOYEE_COUNT = "/count";

	public static final String USER = "/user";
	public static final String USER_SUBORDINATE_USERS = "/listSubordinateUsers";
	public static final String USER_NOTIFIED_USERS = "/listNotifiedUsers";
	public static final String USER__LIST_FOR_LOGGED_USER = "/listLoggedUser";
	public static final String USER_GET_USER_DETAILS = "/getUserDetails";
	public static final String USER_PHOTO = "/photo";
	public static final String USER_ADD = "/add";
	public static final String USER_MODIFY = "/modify";
	public static final String USER_CHANGE_PIN_4_EMPLOYEES = "/changePin4Empolyees";
	public static final String USER_CHANGE_PIN = "/changePin";
	public static final String USER_CHANGE_CARD_CODE = "/changeCardCode";
	public static final String USER_MODIFY_MY_PROJECTS = "/modifyMyProjects";
	public static final String USER_MODIFY_MY_ACTIVITIES = "/modifyMyActivities";
	public static final String USER_MODIFY_MY_FAVORITES = "/modifyMyFavorites";
	public static final String USER_RENEW_PASSWORD = "/renewPassword";
	public static final String USER_CHANGE_PASSWORD = "/changePassword";
	public static final String USER_GET_ACCOUNTS_FOR_SYSTEM_EMAIL = "/getAccounts4SystemEmail";
	public static final String USER_SAVE_ACCOUNTS_FOR_SYSTEM_EMAIL = "/saveSystemEmailAccounts";
	public static final String USER_SAVE_ACCOUNTS_FOR_NOFITY_REQUEST = "/saveNotifyOfRequest";
	public static final String USER_GET_ACCOUNTS_FOR_APPROVED_NOTIFICATION = "/getAccounts4Notification";
	public static final String USER_GET_ALL_VALID_EMPLOYEES = "/getAllValidEmployees";
	public static final String USER_GET_ALL_VALID_EMLOYEES_LIST = "/getAllValidEmployeesList";
	public static final String USER_ALL_TYPES_EMPLOYMENT = "/getAllTypeEmployment";
	public static final String USER_ALL_TYPES_OF_HO_PERMISSION = "/getAllTypesOfHomeOfficePermission";

	public static final String BRW_EMPLOYEES_STATUS = "/brwEmployeesStatus";
	public static final String BRW_EMPLOYEES_STATUS_FETCH = "/fetch";
	public static final String BRW_EMPLOYEES_STATUS_FETCH_NEW = "/fetchNew";

	public static final String BRW_SUBORDINATE_REQUEST = "/brwSubordinateRequest";
	public static final String BRW_SUBORDINATE_REQUEST_FETCH = "/fetch";

	public static final String UPLOAD = "/upload";
	public static final String UPLOAD_ACTIVITIES = "/activities";
	public static final String UPLOAD_PROJECTS = "/projects";
	public static final String UPLOAD_EMPLOYEES = "/employees";

	public static final String CLIENT_DETAIL = "/client";
	public static final String CLIENT_DETAIL_GET_DETAIL = "/getDetail";
	public static final String CLIENT_DETAIL_UPDATE_DETAIL = "/updateDetail";

	public static final String LEGAL_FORM = "/legalForm";
	public static final String LEGAL_FORM_GET_VALID_RECORDS = "/getValidRecords";

	public static final String SYSTEM_INFO = "/systemInfo";
	public static final String SYSTEM_INFO_GET_VERSION = "/getVersion";

	public static final String CLIENT_PARAMETER = "/clientParameter";
	public static final String CLIENT_PARAMETER_GET = "/getClientParameter";

	public static final String REQUEST = "/request";
	public static final String REQUEST_ADD = "/add";
	public static final String REQUEST_GET_DETAIL = "/getDetail";
	public static final String REQUEST_MODIFY = "/modify";
	public static final String REQUEST_CANCEL = "/cancel";
	public static final String REQUEST_APPROVE = "/approve";
	public static final String REQUEST_REJECT = "/reject";
	public static final String REQUEST_APPROVE_FROM_EMAIL = "/approveRequestFromEmail";
	public static final String REQUEST_REJECT_FROM_EMAIL = "/rejectRequestFromEmail";
	public static final String REQUEST_HAS_USER_ALLOWED_HOME_OFFICE_FOR_TODAY = "/isAllowedHomeOfficeForToday";
	public static final String REQUEST_HAS_USER_ALLOWED_HOME_OFFICE_IN_INTERVAL = "/isAllowedHomeOfficeInInterval";

	public static final String BRW_REQUEST = "/brwSubordinateRequest";
	public static final String BRW_REQUEST_LOAD_DATA = "/loadData";
	public static final String BRW_REQUEST_LOAD_DATA_FOR_GRAPH = "/loadDataForGraph";
	public static final String BRW_REQUEST_COUNT = "/count";

	public static final String REQUEST_STATUS = "/requestStatus";
	public static final String REQUEST_STATUS_GET_VALID_RECORDS = "/getValidRecords";

	public static final String REGISTRATION = "/registration";
	public static final String REGISTER = "/register";

	public static final String PIN_CODE_GENERATOR = "/pinCodeGenerator";
	public static final String PIN_CODE_GENERATOR_GET_GENERATED_PIN = "/getGeneratedPIN";

	public static final String SEND_EMAIL = "/sendEmail";
	public static final String SEND_MISSING_EMPLOYEES_EMAILS = "/sendMissingEmployeesEmails";

	public static final String GENERATE_REPORT = "/generateReport";
	public static final String GENERATE_REPORT_GENERATE = "/generate";
	public static final String GENERATE_REPORT_EMPLOYEES = "/generateEmployeesReport";
	public static final String GENERATE_REPORT_WORKPLACE = "/generateWorkplaceReport";

	public static final String MESSAGE = "/message";
	public static final String MESSAGE_GET_MESSAGES = "/getMessages";
	public static final String MESSAGE_GET_NAMESDAY = "/getNamesDay";

	public static final String BRW_NOTIFYOFAPPROVEDREQUEST = "/brwnotifyofapprovedrequest";
	public static final String BRW_NOTIFYOFAPPROVEDREQUEST_LOAD_DATA = "/loadData";
	public static final String BRW_NOTIFYOFAPPROVEDREQUEST_COUNT = "/count";
	
	public static final String JIRA_TOKEN_GENERATION = "/jiraTokenGeneration";
	public static final String JIRA_TOKEN_GENERATION_LINK = "/jiraTokenGenerationLink";
	public static final String JIRA_GENERATE_ACCESS_TOKEN = "/jiraGenerateAccessToken";
	
	private CApiUrl() {
		// Auto-generated constructor stub
	}
}
