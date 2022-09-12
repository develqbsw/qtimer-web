package sk.qbsw.sed.fw.utils;

import org.apache.commons.lang3.StringUtils;

public class CJavascriptUtils {

	private CJavascriptUtils() {
		// Auto-generated constructor stub
	}

	public static String scriptForModal(String id, String callBackClosingUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append("beforeModalShow('" + id + "');");
		sb.append("$('#" + id + "').modal('toggle');");
		sb.append("$('#" + id + "').on('hidden', function () {" + callBackClosingUrl + ";});");
		return sb.toString();
	}

	public static String scriptForModalClose(String id) {
		return "closeModalWindow('" + id + "');";
	}

	public static String scriptForModalFocus(String id, String idCloseLink) {
		return "$('#" + id + "').on('shown',function(){$('#" + idCloseLink + "').focus();});";
	}

	public static String hideExpecterOverlay(String id) {
		return "hideOverlayDivExpecter('" + id + "');";
	}

	public static String showExpecterOverlay(String id) {
		return "$('#" + id + "').click(function(){showOverlay('" + id + "')});";
	}

	/**
	 * callback is called only if the new window is succesfully opened
	 * 
	 * @param url
	 * @param message
	 * @param callback
	 * @return
	 */
	public static String showWindowOpen(String url, String message, String callback, String callbackFaliure) {
		String callbackTmp = "";
		if (StringUtils.isNotBlank(callback)) {
			callbackTmp = callback;
		}
		String callbackFaliureTmp = "";
		if (StringUtils.isNotBlank(callbackFaliure)) {
			callbackFaliureTmp = callbackFaliure;
		}
		return "var win=window.open('" + url + "');if(win == null || typeof(win )=='undefined'){alert('" + message + "'); " + callbackFaliureTmp + "}else{" + callbackTmp + "};";
	}

	public static String callCallback(String callback) {
		return "" + callback + ";";
	}

	public static String onEnterBehaviour(String searchComponent, String submitComponent) {
		return "$('input').keypress(function (e) { if (this.name != '" + searchComponent + "' && e.which == 13) {$(\"[name='" + submitComponent + "']\").click(); return false; } });";
	}

	public static String goToTop() {
		return "$('.footer .go-top').click();";
	}
}
