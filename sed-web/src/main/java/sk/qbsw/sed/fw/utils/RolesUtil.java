package sk.qbsw.sed.fw.utils;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

public class RolesUtil {
	
	private RolesUtil() {
		// Auto-generated constructor stub
	}

	public static boolean hasRole(CLoggedUserRecord user, Long role) {
		if (user == null || user.getRoles() == null) {
			return false;
		} else {
			return user.getRoles().contains(role);
		}
	}
}
