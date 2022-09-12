package sk.qbsw.sed.web.ui.pages.base;

import java.io.Serializable;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

/**
 *
 * @author Marek Martinkovic
 * @since 2.1.0
 * @version 2.1.0
 */
public class ABlankPage extends WebPage implements Serializable {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected WebMarkupContainer bodyTag;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		bodyTag = new TransparentWebMarkupContainer("body");
		bodyTag.setOutputMarkupId(true);
		add(bodyTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Component#renderHead(org.apache.wicket.markup.head.
	 * IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}
}
