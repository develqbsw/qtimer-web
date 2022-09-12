package sk.qbsw.sed.fw.model;

import java.io.Serializable;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Used in the {@link ATPanel}. Represents a link that redirects to the page
 * specified as parameter
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CActionLink implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String title;
	private final String tooltip;
	private final String imageClass;
	private final Class<? extends WebPage> pageClass;
	private final PageParameters params;
	private final Roles roles;

	/**
	 * 
	 * @param title      - tooltip
	 * @param imageClass - for rendering the link image
	 * @param pageClass  - the target class
	 * @param params     - parameters for the redirection
	 */
	public CActionLink(String title, String tooltip, String imageClass, Class<? extends WebPage> pageClass, PageParameters params) {
		super();
		this.title = title;
		this.imageClass = imageClass;
		this.pageClass = pageClass;
		this.params = params;
		this.roles = null;
		this.tooltip = tooltip;
	}

	/**
	 * 
	 * @param title      - title
	 * @param tooltip    - tooltip
	 * @param imageClass - for rendering the link image
	 * @param pageClass  - the target class
	 * @param params     - parameters for the redirection
	 * @param roles      - for these roles is the link visible
	 */
	public CActionLink(String title, String tooltip, String imageClass, Class<? extends WebPage> pageClass, PageParameters params, Roles roles) {
		super();
		this.title = title;
		this.tooltip = tooltip;
		this.imageClass = imageClass;
		this.pageClass = pageClass;
		this.params = params;
		this.roles = roles;
	}

	public String getTitle() {
		return title;
	}

	public String getImageClass() {
		return imageClass;
	}

	public Class<? extends WebPage> getPageClass() {
		return pageClass;
	}

	public PageParameters getParams() {
		return params;
	}

	public Roles getRoles() {
		return roles;
	}

	public String getTooltip() {
		return tooltip;
	}
}
