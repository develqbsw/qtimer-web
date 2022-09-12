package sk.qbsw.sed.page;

import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.fw.page.ABasePage;

/**
 * SubPage: /accessdeniedpage
 */
@MountPath(CAccessDeniedPage.PATH_SEGMENT)
public class CAccessDeniedPage extends ABasePage {
	/** serial uid */
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "accessdeniedpage";

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(homePageLink("home"));
	}

	@Override
	public String getPageKey() {

		return PATH_SEGMENT;
	}
}
