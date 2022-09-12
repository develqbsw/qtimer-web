package sk.qbsw.sed.panel.appinfo;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.communication.service.ISystemInfoClientService;
import sk.qbsw.sed.fw.component.EDataType;
import sk.qbsw.sed.fw.component.form.input.CLabel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /appInfo SubPage title: O aplikácii
 * 
 * Panel AppInfoPanel
 */
public class CAppInfoPanel extends CPanel {

	private static final long serialVersionUID = 1L;

	private final Form<Void> form;

	@SpringBean
	private ISystemInfoClientService service;

	public CAppInfoPanel(String id, Label pageTitleSmall) {
		super(id);
		setOutputMarkupId(true);
		pageTitleSmall.setDefaultModelObject(CStringResourceReader.read("appinfo.title"));

		form = new Form<>("appInfoForm");
		form.setOutputMarkupId(true);

		// version
		String version = null;
		try {
			version = service.getVersion();
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
		}
		form.add(new CLabel("version", new Model<String>(version), EDataType.TEXT));

		// other
		form.add(new CLabel("func", new Model<String>(getString("appinfo.functionalities.label")), EDataType.TEXT));
		// functions defined are in the CAppInfoPanel.html

		form.add(new CLabel("contact", new Model<String>(getString("appinfo.contact.label")), EDataType.TEXT));

		ExternalLink email = new ExternalLink("email", "mailto:info@qbsw.sk", "info@qbsw.sk");
		form.add(email);

		form.add(new CLabel("links", new Model<String>(getString("appinfo.links.label")), EDataType.TEXT));

		add(form);
	}
}
