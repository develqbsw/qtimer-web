package sk.qbsw.sed.page;

import org.apache.wicket.markup.html.link.Link;
import org.wicketstuff.annotation.mount.MountPath;

import sk.qbsw.sed.fw.page.ABasePage;

/**
 * SubPage: /expiredpage
 */
@MountPath(CExpiredPage.PATH_SEGMENT)
public class CExpiredPage extends ABasePage {
	
	/** serial uid */
	private static final long serialVersionUID = 1L;
	public static final String PATH_SEGMENT = "expiredpage";

	@Override
	protected void onInitialize() {
		super.onInitialize();

		Link<Void> link = new Link<Void>("home") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(getApplication().getHomePage());
			}
		};
		add(link);
	}

	@Override
	public String getPageKey() {
		return PATH_SEGMENT;
	}
}
