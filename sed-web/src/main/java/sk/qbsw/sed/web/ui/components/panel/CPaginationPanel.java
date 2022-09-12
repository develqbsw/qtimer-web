package sk.qbsw.sed.web.ui.components.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * pagination panel
 *
 * @author Podmajersky Lukas
 * @since 2.1.0
 * @version 2.1.0
 */
public class CPaginationPanel extends Panel {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/**
	 * create new pagination panel
	 *
	 * @param wicketId wicket id
	 * @param pageable
	 */
	public CPaginationPanel(String wicketId, IPageableItems pageable, final Component tableWrap) {
		super(wicketId);

		setOutputMarkupId(true);
		add(new AjaxPagingNavigator("navigator", pageable) {

			/** serial uid */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAjaxEvent(AjaxRequestTarget target) {
				super.onAjaxEvent(target);
				target.add(CPaginationPanel.this);
				target.add(tableWrap);
			}
		});
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
