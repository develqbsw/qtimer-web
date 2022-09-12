package sk.qbsw.sed.fw.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * all pages that don't need authentication extends this page
 * 
 * @author Peter Bozik
 * @since 1.0.0
 *
 */
public abstract class ANonAuthenticatedPage extends ABasePage {

	private static final long serialVersionUID = 1L;

	public ANonAuthenticatedPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}
}
