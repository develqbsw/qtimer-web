package sk.qbsw.sed.page;

import java.util.Date;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.fw.page.ABasePage;
import sk.qbsw.sed.fw.utils.CStringResourceReader;

/**
 * SubPage: /errorpage
 */
@MountPath(CErrorPage.PATH_SEGMENT)
public class CErrorPage extends ABasePage {
	
	/** serial uid */
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "errorpage";

	public CErrorPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		String errorText = CStringResourceReader.read("error.page.text1");
		String date = DateLabel.forDateStyle("errorTime", new Model<Date>(new Date()), "SS").getDefaultModelObjectAsString();

		add(new Label("errorText", errorText.replace("$timeString$", date)));
		add(homePageLink("home"));
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
