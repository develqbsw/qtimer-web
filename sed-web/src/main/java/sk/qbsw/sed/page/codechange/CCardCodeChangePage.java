package sk.qbsw.sed.page.codechange;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.codechange.CCodeChangeContentPanel;

/**
 * SubPage: /cardCodeChange SubPage title: Zmena kódu karty
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CCardCodeChangePage.PATH_SEGMENT)
public class CCardCodeChangePage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "cardCodeChange";

	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public CCardCodeChangePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CCodeChangeContentPanel(id, "card"));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
