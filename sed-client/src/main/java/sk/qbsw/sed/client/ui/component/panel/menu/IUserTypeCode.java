package sk.qbsw.sed.client.ui.component.panel.menu;

public interface IUserTypeCode {
	// USERTYPE_EMPLOYEE
	public static String EMPLOYEE = "E";
	// USERTYPE_ACCOUNTANT
	public static String ACCOUNTANT = "AA";
	// USERTYPE_RECEPTION
	public static String RECEPTION = "RE";
	// USERTYPE_SYSTEMMANAGER
	public static String SYSTEM_ADMIN = "SA";
	// USERTYPE_ORGMANAGER
	public static String ORG_ADMIN = "OA";

	public static final Long ID_SYSTEM_ADMIN = 1l;
	public static final Long ID_ORG_ADMIN = 2l;
	public static final Long ID_EMPLOYEE = 3l;
	public static final Long ID_ACCOUNTANT = 4l;
	public static final Long ID_RECEPTION = 5l;
	public static final Long ID_EMPLOYEE_WITH_SUB = 1001l;
	public static final Long ID_EMPLOYEE_WITHOUT_SUB = 1002l;
	public static final Long ID_CHANGE_PASSWORD = 1003l;
}
