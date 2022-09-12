package sk.qbsw.sed.page.home;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.communication.service.impl.CMessageClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.panel.home.CHomeContentPanel;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /home SubPage title: Dashboard
 */
@AuthorizeInstantiation({ IUserTypeCode.EMPLOYEE })
@MountPath(CHomePage.PATH_SEGMENT)
public class CHomePage extends AAuthenticatedPage {
	
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "home";

	@SpringBean
	CMessageClientService messageService;

	public CHomePage(PageParameters parameters) {
		super(parameters, true);
	}

	@Override
	public void addContents(String id, List<WebMarkupContainer> contents) {
		getPageTitleSmall().setDefaultModelObject(CSedSession.get().getUser().getClientInfo().getClientName());
		addNamesDay();
		contents.add(new CHomeContentPanel(id));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}

	private void addNamesDay() {
		try {
			getPageAdditionalData().setDefaultModel(Model.of(messageService.getNamesday(CSedSession.get().getLocale())));
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, CHomePage.this.getSession(), null, CHomePage.this);
		}
	}
}
