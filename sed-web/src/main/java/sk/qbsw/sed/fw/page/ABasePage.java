package sk.qbsw.sed.fw.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class ABasePage extends WebPage {

	private static final long serialVersionUID = 1L;

	public ABasePage(PageParameters parameters) {
		super(parameters);
	}

	public ABasePage() {
		super();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

	}

	@Override
	protected void setHeaders(WebResponse response) {
		response.setHeader("X-UA-Compatible", "IE=edge");
		super.setHeaders(response);
	}

	public abstract String getPageKey();
}
