package sk.qbsw.sed.server.model;

/**
 * Used as label names in "src/main/resources/velocity/*.vm"
 * 
 * @author rosenberg
 *
 */
public interface INotificationLabels {
	
	// common
	static final String LABEL_BEST_REGARDS = "LABEL_BEST_REGARDS"; // S pozdravom
	static final String LABEL_SYSTEM_EES = "LABEL_SYSTEM_EES";
	static final String LABEL_YEAR = "LABEL_YEAR";
	static final String LABEL_LOGIN = "LABEL_LOGIN";
	static final String LABEL_PASSWORD = "LABEL_PASSWORD";
	static final String LABEL_PIN = "LABEL_PIN";

	// missing_employees_by_reason.vm
	static final String LABEL_LIST_ABSENTEES_DATE_TIME = "LABEL_LIST_ABSENTEES_DATE_TIME";
	static final String LABEL_HOLIDAY = "LABEL_HOLIDAY"; // Dovolenka
	static final String LABEL_REPLWORK = "LABEL_REPLWORK";
	static final String LABEL_SICKLEAVE = "LABEL_SICKLEAVE";
	static final String LABEL_BARRIERS_TO_WORK = "LABEL_BARRIERS_TO_WORK";
	static final String LABEL_BUSINESS_TRIP = "LABEL_BUSINESS_TRIP";
	static final String LABEL_WORK_FROM_HOME = "LABEL_WORK_FROM_HOME";
	static final String LABEL_TRAINING = "LABEL_TRAINING";
	static final String LABEL_ABSENTEES_NO_REQUEST = "LABEL_ABSENTEES_NO_REQUEST";
	static final String LABEL_REPLACEMENT = "LABEL_REPLACEMENT"; // Zastupca"
	static final String LABEL_OUT_OF_OFFICE = "LABEL_OUT_OF_OFFICE";
	static final String LABEL_DOCTOR_VISIT = "LABEL_DOCTOR_VISIT";

	// missing_employees.vm
	static final String LABEL_LIST_ABSENTEES = "LABEL_LIST_ABSENTEES";
	static final String LABEL_DATE = "LABEL_DATE";

	// new_password_generated.vm
	static final String LABEL_LOGIN_DATA_ADMIN = "LABEL_LOGIN_DATA_ADMIN";
	static final String LABEL_LOGIN_DATA_RECEPTION = "LABEL_LOGIN_DATA_RECEPTION";

	// renew_password.vm
	static final String LABEL_USER_PASSWORD_PIN_IN_EES = "LABEL_USER_PASSWORD_PIN_IN_EES";

	static final String LABEL_USER_PIN_IN_EES = "LABEL_PIN_IN_EES";

	// user_request_notification.vm
	static final String LABEL_RQ_TYPE = "LABEL_RQ_TYPE";
	static final String LABEL_EMPLOYEE = "LABEL_EMPLOYEE"; // Zamestnanec: 
	static final String LABEL_DATES = "LABEL_DATES";
	static final String LABEL_NUM_WORK_DAYS = "LABEL_NUM_WORK_DAYS";
	static final String LABEL_PLACE = "LABEL_PLACE"; // Miesto: 
	static final String LABEL_REASON = "LABEL_REASON"; // Dôvod: 
	static final String LABEL_NOTE = "LABEL_NOTE"; // Poznámka: 
	static final String LABEL_RQ_OLD_STATUS = "LABEL_RQ_OLD_STATUS";
	static final String LABEL_RQ_NEW_STATUS = "LABEL_RQ_NEW_STATUS";
	static final String LABEL_HALFDAY = "LABEL_HALFDAY";
	
	static final String VALUE_YES = "VALUE_YES";
	static final String VALUE_NO = "VALUE_NO";

}
