package sk.qbsw.sed.fw.navigation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import sk.qbsw.sed.fw.page.AAuthenticatedPage;
import sk.qbsw.sed.fw.utils.CollectionUtils;

/**
 * Menu item model
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CSideMenuItemModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NONE_TILE = "none";

	private String title;
	private String icon;
	private Class<? extends AAuthenticatedPage> page;
	private List<CSideMenuItemModel> subItems;
	private CSideMenuItemModel parent = null;
	private boolean inMenuVisible = true;

	public CSideMenuItemModel(String title, String icon, Class<? extends AAuthenticatedPage> page) {
		super();
		this.title = title;
		this.icon = icon;
		this.page = page;
	}

	public CSideMenuItemModel(String icon, Class<? extends AAuthenticatedPage> page) {
		super();
		this.icon = icon;
		this.page = page;
	}

	public CSideMenuItemModel(String title, Class<? extends AAuthenticatedPage> page, boolean inMenuVisible) {
		super();
		this.title = title;
		this.page = page;
		this.inMenuVisible = inMenuVisible;
	}

	public CSideMenuItemModel(String title, Class<? extends AAuthenticatedPage> page, boolean inMenuVisible, CSideMenuItemModel parent) {
		super();
		this.title = title;
		this.page = page;
		this.inMenuVisible = inMenuVisible;
		this.parent = parent;
	}

	public CSideMenuItemModel(Class<? extends AAuthenticatedPage> page, boolean inMenuVisible, List<CSideMenuItemModel> subItems) {
		super();
		this.page = page;
		this.inMenuVisible = inMenuVisible;
		setSubItems(subItems);
	}

	public CSideMenuItemModel(String title, Class<? extends AAuthenticatedPage> page, boolean inMenuVisible, List<CSideMenuItemModel> subItems) {
		super();
		this.title = title;
		this.page = page;
		this.inMenuVisible = inMenuVisible;
		setSubItems(subItems);
	}

	public CSideMenuItemModel(String title, String icon, Class<? extends AAuthenticatedPage> page, List<CSideMenuItemModel> subItems) {
		super();
		this.title = title;
		this.icon = icon;
		this.page = page;
		setSubItems(subItems);
	}

	public CSideMenuItemModel(String icon, Class<? extends AAuthenticatedPage> page, List<CSideMenuItemModel> subItems) {
		super();
		this.icon = icon;
		this.page = page;
		setSubItems(subItems);
	}

	public CSideMenuItemModel(String title, String icon, Class<? extends AAuthenticatedPage> page, CSideMenuItemModel parent, List<CSideMenuItemModel> subItems) {
		super();
		this.title = title;
		this.icon = icon;
		this.page = page;
		setSubItems(subItems);
		this.parent = parent;
	}

	public CSideMenuItemModel(String icon, Class<? extends AAuthenticatedPage> page, CSideMenuItemModel parent, List<CSideMenuItemModel> subItems) {
		super();
		this.icon = icon;
		this.page = page;
		setSubItems(subItems);
		this.parent = parent;
	}

	public CSideMenuItemModel(String title, String icon, List<CSideMenuItemModel> subItems) {
		super();
		this.title = title;
		this.icon = icon;
		this.page = null;
		setSubItems(subItems);
	}

	public CSideMenuItemModel(String title, String icon, List<CSideMenuItemModel> subItems, CSideMenuItemModel parent) {
		super();
		this.title = title;
		this.icon = icon;
		this.page = null;
		setSubItems(subItems);
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Class<? extends AAuthenticatedPage> getPage() {
		return page;
	}

	public void setPage(Class<? extends AAuthenticatedPage> page) {
		this.page = page;
	}

	public List<CSideMenuItemModel> getSubItems() {
		return subItems;
	}

	public List<IModel<CSideMenuItemModel>> getSubItemsModel() {
		List<IModel<CSideMenuItemModel>> modelList = new ArrayList<>();
		for (CSideMenuItemModel model : subItems) {
			modelList.add(Model.of(model));
		}
		return modelList;
	}

	/**
	 * subitems should be set before this method is called
	 * 
	 * @param subItems
	 */
	public void setSubItems(List<CSideMenuItemModel> subItems) {
		this.subItems = subItems;
		for (CSideMenuItemModel model : subItems) {
			model.setParent(this);
		}
	}

	private void setParent(CSideMenuItemModel parent) {
		this.parent = parent;
	}

	public CSideMenuItemModel getParent() {
		return parent;
	}

	public boolean isInMenuVisible() {
		return inMenuVisible;
	}

	public boolean hasRights() {
		return this.page == null ? true : Session.get().getAuthorizationStrategy().isInstantiationAuthorized(this.page);
	}

	public boolean hasChildren() {
		return CollectionUtils.listSize(subItems) > 0;
	}

	public boolean childrenHasRights() {
		if (subItems != null) {
			for (CSideMenuItemModel model : subItems) {
				if (model.hasRights()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((page == null) ? 0 : page.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CSideMenuItemModel other = (CSideMenuItemModel) obj;
		if (icon == null) {
			if (other.icon != null) {
				return false;
			}
		} else if (!icon.equals(other.icon)) {
			return false;
		}
		if (page == null) {
			if (other.page != null) {
				return false;
			}
		} else if (!page.equals(other.page)) {
			return false;
		}
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}
}
