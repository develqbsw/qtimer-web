package sk.qbsw.sed.web.ui;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.component.CMenu;
import sk.qbsw.sed.component.form.CApproveRequestPage;
import sk.qbsw.sed.component.form.CRejectRequestPage;
import sk.qbsw.sed.page.CAccessDeniedPage;
import sk.qbsw.sed.page.CErrorPage;
import sk.qbsw.sed.page.CExpiredPage;
import sk.qbsw.sed.page.home.CHomePage;
import sk.qbsw.sed.page.login.CLoginPage;
import sk.qbsw.sed.page.timesheet.CTimesheetPage;
import sk.qbsw.sed.utils.ICookiesConstants;

public class CWebApplication extends AuthenticatedWebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return getDefaultPageForRoles(CSedSession.get().getRoles());
	}

	@Override
	public Session newSession(Request request, Response response) {
		Session session = super.newSession(request, response);

		if (!((CSedSession) session).isSignedIn()) {
			WebRequest webRequest = (WebRequest) request;
			Cookie cookie = webRequest.getCookie(ICookiesConstants.COOKIE_NAME_AUTO_LOGIN);
			if (cookie != null) {
				((CSedSession) session).signIn(cookie.getValue());
			}
		}

		return session;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return CLoginPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return CSedSession.class;
	}

	@Override
	public void init() {
		super.init();
		CMenu.create();
		// for spring beans
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		// for removint strip tags in dev mode
		getMarkupSettings().setStripWicketTags(true);
		// mount additional stuff
		mountPages();
		mountResources();
		mountErrorManagement();
	}

	/**
	 * error management pages
	 */
	private void mountErrorManagement() {
		getApplicationSettings().setInternalErrorPage(CErrorPage.class);
		getApplicationSettings().setAccessDeniedPage(CAccessDeniedPage.class);
		getApplicationSettings().setPageExpiredErrorPage(CExpiredPage.class);
		getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
	}

	/**
	 * Mount pages.
	 */
	private void mountPages() {
		mountPage("/login", CLoginPage.class);
		mountPage("/rejectRequest", CRejectRequestPage.class);
		mountPage("/approveRequest", CApproveRequestPage.class);
		new AnnotatedMountScanner().scanPackage("sk.qbsw.sed.page").mount(this);
	}

	/**
	 * mount resources.
	 */
	private void mountResources() {
		// do nothing
	}

	private static Class<? extends Page> getDefaultPageForRoles(Roles roles) {
		if (roles != null) {
			if (roles.contains(IUserTypeCode.EMPLOYEE)) {
				return CHomePage.class;
			} else if (roles.contains(IUserTypeCode.ORG_ADMIN)) {
				return CTimesheetPage.class;
			}
		}
		return CLoginPage.class;
	}

	/**
	 * Removing The jessionid
	 */
	@Override
	protected WebResponse newWebResponse(final WebRequest webRequest, final HttpServletResponse httpServletResponse) {
		return new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse) {

			@Override
			public String encodeURL(CharSequence url) {
				return url.toString();
			}

			@Override
			public String encodeRedirectURL(CharSequence url) {
				return url.toString();
			}
		};
	}
}
