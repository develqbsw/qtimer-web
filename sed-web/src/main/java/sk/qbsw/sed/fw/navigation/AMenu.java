package sk.qbsw.sed.fw.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.fw.utils.CollectionUtils;

/**
 * Abstract menu that holds information about menu items and page hierarchy
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class AMenu {

	private static final Logger LOG = LoggerFactory.getLogger(AMenu.class);
	private static AMenu instance;
	private final List<CSideMenuItemModel> topMenus = new ArrayList<>();
	private final Set<Class<? extends AAuthenticatedPage>> notMenuPages = new HashSet<>();
	private final Map<Class<? extends AAuthenticatedPage>, CSideMenuItemModel> topClassMap = new HashMap<>();
	private final Map<Class<? extends AAuthenticatedPage>, CSideMenuItemModel> pageClassMap = new HashMap<>();

	protected AMenu() {
		onInitialize(topMenus, notMenuPages);
		postInitialize();
	}

	protected abstract void onInitialize(List<CSideMenuItemModel> topMenus, Set<Class<? extends AAuthenticatedPage>> notMenuPages);

	public List<CSideMenuItemModel> getTopMenus() {
		return topMenus;
	}

	public List<IModel<CSideMenuItemModel>> getTopMenusModelList() {
		List<IModel<CSideMenuItemModel>> modelList = new ArrayList<>();
		for (CSideMenuItemModel model : topMenus) {
			if (model.isInMenuVisible()) {
				modelList.add(Model.of(model));
			}
		}
		return modelList;
	}

	private void postInitialize() {
		for (CSideMenuItemModel model : topMenus) {
			if (model.getPage() != null) {
				assignTopPage(model, model.getPage(), model);
			}
			topPageAssignmentRecirsuve(model, model.getSubItems());

		}
	}

	private void topPageAssignmentRecirsuve(CSideMenuItemModel topModel, List<CSideMenuItemModel> items) {
		if (CollectionUtils.listSize(items) > 0) {
			for (CSideMenuItemModel model : items) {
				if (model.getPage() != null) {
					assignTopPage(topModel, model.getPage(), model);
				}
				topPageAssignmentRecirsuve(topModel, model.getSubItems());
			}
		}
	}

	private void assignTopPage(CSideMenuItemModel topModel, Class<? extends AAuthenticatedPage> assignedPage, CSideMenuItemModel currentModel) {
		if (topClassMap.containsKey(assignedPage)) {
			throw new CSystemFailureException("multiple menu instance for page: " + assignedPage);
		}
		pageClassMap.put(assignedPage, currentModel);
		topClassMap.put(assignedPage, topModel);
	}

	public CSideMenuItemModel getTopModel(Class<? extends Page> pageClass) {
		if (notMenuPages.contains(pageClass)) {
			return null;
		}
		if (!topClassMap.containsKey(pageClass)) {
			throw new CSystemFailureException("given class is not registered in menu: " + pageClass);
		}
		return topClassMap.get(pageClass);
	}

	public CSideMenuItemModel getModel(Class<? extends Page> pageClass) {
		if (notMenuPages.contains(pageClass)) {
			return null;
		}
		if (!pageClassMap.containsKey(pageClass)) {
			throw new CSystemFailureException("given class is not registered in menu: " + pageClass);
		}
		return pageClassMap.get(pageClass);
	}

	public boolean isChildItemPage(Class<? extends Page> pageClass, Class<? extends Page> currentPage) {
		if (!pageClassMap.containsKey(pageClass)) {
			throw new CSystemFailureException("given class is not registered in menu: " + pageClass);
		}
		CSideMenuItemModel model = pageClassMap.get(pageClass);
		return isChildItemPage(model, currentPage);

	}

	private boolean isChildItemPage(CSideMenuItemModel model, Class<? extends Page> currentPage) {
		if (currentPage.equals(model.getPage())) {
			return true;
		}
		if (CollectionUtils.listSize(model.getSubItems()) > 0) {
			for (CSideMenuItemModel inModel : model.getSubItems()) {
				if (currentPage.equals(inModel.getPage()) || isChildItemPage(inModel, currentPage)) {
					return true;
				}

			}
		}
		return false;

	}

	protected static void setInstance(AMenu ins) {
		if (instance != null) {
			LOG.error("The menu can be instanted only once");
		}
		instance = ins;

	}

	public static AMenu getInstance() {
		if (instance == null) {
			throw new CSystemFailureException("The menu has not been instantiated");
		}
		return instance;
	}
}
