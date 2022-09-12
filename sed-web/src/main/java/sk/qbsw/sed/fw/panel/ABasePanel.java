package sk.qbsw.sed.fw.panel;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.fw.utils.RolesUtil;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * id contentPanel is reserved
 * 
 * @author Peter Božík
 * @version 0.1
 * @since 0.1
 *
 */
public class ABasePanel extends Panel {
	private static final long serialVersionUID = 1L;

	public ABasePanel(String id) {
		super(id);
	}

	public ABasePanel(String id, IModel<?> model) {
		super(id, model);
	}

	public void navigateBack() {
		Page page = getPage();
		if (page instanceof AAuthenticatedPage) {
			((AAuthenticatedPage) page).navigateBack();
		}
	}

	protected boolean hasRole(Long role) {
		CLoggedUserRecord user = null;
		if (getSession() instanceof CSedSession) {
			user = ((CSedSession) getSession()).getUser();
		}
		return RolesUtil.hasRole(user, role);
	}

	protected final CLoggedUserRecord getuser() {
		CLoggedUserRecord user = null;
		if (getSession() instanceof CSedSession) {
			user = CSedSession.get().getUser();
		}
		return user;
	}

	/**
	 * Defines title model for the panel
	 * 
	 * @return
	 */
	protected StringResourceModel getPanelTitleModel() {
		return null;
	}
}
