package sk.qbsw.sed.fw.utils;

import java.lang.reflect.Constructor;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.qbsw.sed.fw.component.IComponentContainer;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.fw.page.ADetailNavigateableBackPage;

public class CWicketUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CWicketUtils.class);

	private CWicketUtils() {
		// Auto-generated constructor stub
	}

	/**
	 * if the page is an instance of {@link AAuthenticatedPage} its feedbackpanel
	 * will be added to the {@link AjaxRequestTarget}
	 * 
	 * @param target
	 * @param page
	 */
	public static void refreshFeedback(AjaxRequestTarget target, Page page) {
		if (page != null && page instanceof AAuthenticatedPage) {
			target.add(((AAuthenticatedPage) page).getFeedbackPanel());
		}
	}

	public static void refreshFeedback(AjaxRequestTarget target, IComponentContainer container) {
		if (container == null) {
			return;
		}
		if (container.getFeedbackPanel() != null) {
			target.add(container.getFeedbackPanel());
		}
		refreshFeedback(target, container.getPage());
	}

	public static void setResponsePage(Component component, Class<? extends Page> cls, PageParameters params) {
		boolean succ = false;
		if (ADetailNavigateableBackPage.class.isAssignableFrom(cls)) {

			try {
				Constructor<? extends Page> constr = cls.getConstructor(PageParameters.class, Integer.class);
				if (constr != null) {
					constr.setAccessible(true);
					Page page = constr.newInstance(params, component.getPage().getPageId());
					if (page != null) {
						component.setResponsePage(page);
						succ = true;
					}
				}
			} catch (Exception e) {
				LOGGER.error("set response page", e);
			}
		}
		if (!succ) {
			component.setResponsePage(cls, params);
		}
	}

	public static Page navigateBackToNewInstance(Component component, Page pageToNavigate) {
		boolean succ = false;
		Page result = null;
		if (pageToNavigate instanceof ADetailNavigateableBackPage) {
			ADetailNavigateableBackPage backPage = (ADetailNavigateableBackPage) pageToNavigate;
			try {
				Constructor<? extends Page> constr = pageToNavigate.getClass().getConstructor(PageParameters.class, Integer.class);
				if (constr != null) {
					constr.setAccessible(true);
					Page page = constr.newInstance(pageToNavigate.getPageParameters(), backPage.getPreviousPageId());
					if (page != null && backPage.getPreviousPageId() != null) {
						component.setResponsePage(page);
						result = page;
						succ = true;
					}
				}
			} catch (Exception e) {
				LOGGER.error("navigate page to new instance", e);
			}

		}
		if (!succ) {
			component.setResponsePage(pageToNavigate.getClass(), pageToNavigate.getPageParameters());
		}
		return result;
	}

	public static void addPageLoad(AbstractLink link) {
		link.add(AttributeModifier.append("class", "pageLoad"));
	}

	public static void addPageLoad(Button link) {
		link.add(AttributeModifier.append("class", "pageLoad"));
	}
}
