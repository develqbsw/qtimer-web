package sk.qbsw.sed.fw.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * abstract for detail pages that have and reference to previous page. You have
 * to implement both constructors
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class ADetailNavigateableBackPage extends ADetailPage implements IBackNavigatableByPageId {

	private static final long serialVersionUID = 1L;
	private Integer pageId;

	/**
	 * 
	 * @param parameters
	 * @param entityParameter null if create page
	 */
	protected ADetailNavigateableBackPage(PageParameters parameters, String entityParameter) {
		super(parameters, entityParameter);
	}

	protected ADetailNavigateableBackPage(PageParameters parameters, String entityParameter, Integer pageId) {
		super(parameters, entityParameter);
		this.pageId = pageId;
	}

	@Override
	public Integer getPreviousPageId() {
		return pageId;
	}
}
