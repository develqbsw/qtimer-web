package sk.qbsw.sed.server.model;

/**
 * Used as parameter value names in "src/main/resources/velocity/*.vm"
 * 
 * @author rosenberg
 * 
 */
public interface INotificationValue {
	
	// ../velocity/renew_password.vm
	static final String PIN = "PIN";

	// ../velocity/renew_password.vm
	// ../velocity/new_password_generated.vm
	static final String LOGIN = "LOGIN";
	static final String PASSWORD = "PASSWORD";

	// ../velocity/new_password_generated.vm
	static final String ALOGIN = "ALOGIN";
	static final String APASSWORD = "APASSWORD";

	// ../velocity/missing_employees.vm
	static final String CHECK_DATE = "CHECK_DATE";
	static final String LIST_EMPLOYEES = "LIST_EMPLOYEES";
	static final String LIST_EMPLOYEES_ON_HOLIDAYS = "LIST_EMPLOYEES_ON_HOLIDAYS";
	static final String LIST_EMPLOYEES_ON_FREE_PAID = "LIST_EMPLOYEES_ON_FREE_PAID";
	static final String LIST_EMPLOYEES_SICKED = "LIST_EMPLOYEES_SICKED";
	static final String LIST_EMPLOYEES_WORKBREAK = "LIST_EMPLOYEES_WORKBREAK";
	static final String LIST_EMPLOYEES_BUSTRIP = "LIST_EMPLOYEES_BUSTRIP";
	static final String LIST_EMPLOYEES_STAFF_TRAINING = "LIST_EMPLOYEES_STAFF_TRAINING";
	static final String LIST_EMPLOYEES_WORK_AT_HOME = "LIST_EMPLOYEES_WORK_AT_HOME";
	static final String LIST_EMPLOYEES_OUT_OF_OFFICE = "LIST_EMPLOYEES_OUT_OF_OFFICE";
	static final String LIST_EMPLOYEES_DOCTOR_VISIT = "LIST_EMPLOYEES_DOCTOR_VISIT";

	// ../velocity/user_request_notification.vm
	static final String REQUEST_TITLE = "REQUEST_TITLE";
	static final String REQUEST_TYPE = "REQUEST_TYPE";
	static final String REQUEST_TYPE_ID = "REQUEST_TYPE_ID";
	static final String NAME_SURNAME = "NAME_SURNAME";
	static final String DATE_FROM = "DATE_FROM";
	static final String DATE_TO = "DATE_TO";
	static final String WORK_DAYS_NUMBER = "WORK_DAYS_NUMBER";
	static final String PLACE = "PLACE";
	static final String REASON = "REASON";
	static final String NOTE = "NOTE";
	static final String REQUEST_STATUS_OLD = "REQUEST_STATUS_OLD";
	static final String REQUEST_STATUS_CURRENT = "REQUEST_STATUS_CURRENT";
	static final String RESPONSALIS_NAME = "RESPONSALIS_NAME";
	static final String HALF_DAY_REQUEST = "HALF_DAY_REQUEST";
	
}
