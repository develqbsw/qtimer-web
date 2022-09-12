package sk.qbsw.sed.client.model;

public interface IHomeOfficePermissionConstants {
	// 1 = Nepovolená
	public static final Long HO_PERMISSION_DISALLOWED = new Long(1);

	// 2 = Iba so žiadosťou
	public static final Long HO_PERMISSION_REQUEST = new Long(2);

	// 3 = Kedykoľvek
	public static final Long HO_PERMISSION_ALLOWED = new Long(3);
}
