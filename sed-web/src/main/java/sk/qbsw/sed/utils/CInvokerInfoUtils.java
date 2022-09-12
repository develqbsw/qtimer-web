package sk.qbsw.sed.utils;

import org.apache.wicket.Session;

import sk.qbsw.sed.communication.model.CInvokerInfo;
import sk.qbsw.sed.fw.ASession;
import sk.qbsw.sed.model.CLoggedUser;

public class CInvokerInfoUtils {

	private CInvokerInfoUtils() {
		// Auto-generated constructor stub
	}

	/**
	 * creates info needed for http request
	 * 
	 * @param session
	 * @return
	 */
	public static CInvokerInfo createInfo(Session session) {
		CInvokerInfo info = new CInvokerInfo();
		if (session instanceof ASession) {
			ASession asess = (ASession) session;
			CLoggedUser auser = asess.getUserModel();
			info.setAuthToken(auser.getToken());
			info.setCookies(auser.getCookies());
		}
		return info;
	}
}
