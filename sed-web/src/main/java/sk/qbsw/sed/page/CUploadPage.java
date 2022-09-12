package sk.qbsw.sed.page;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.upload.CUploadPanel;

/**
 * SubPage: /upload SubPage title: Upload číselníkov
 */
@AuthorizeInstantiation({ IUserTypeCode.ORG_ADMIN })
@MountPath(CUploadPage.PATH_SEGMENT)
public class CUploadPage extends AAuthenticatedPage {
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "upload";

	public CUploadPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		contents.add(new CUploadPanel(id));

	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
