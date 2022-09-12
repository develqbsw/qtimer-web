package sk.qbsw.sed.fw.navigation;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.fw.component.CImage;
import sk.qbsw.sed.fw.panel.CEmptyPanel;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CollectionUtils;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * Panel to render side menu items
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CSideMenu extends Panel {
	private static final long serialVersionUID = 1L;

	public CSideMenu(String id) {
		super(id);

		final List<IModel<CSideMenuItemModel>> modelList = AMenu.getInstance().getTopMenusModelList();
		removeItems(modelList);
		add(new CImage("img", ((CSedSession) getSession()).getUser().getUserPhotoId()));
		add(new Label("userName", getUserName()));
		add(new RefreshingView<CSideMenuItemModel>("sideMenuItems") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<CSideMenuItemModel> item) {
				final CSideMenuItemModel object = item.getModel().getObject();
				Link<WebPage> link = initLink(item);
				link.add(initImage(item));
				String title = object.getTitle();
				if (title == null) {
					ComponentStringResourceLoader loader = new ComponentStringResourceLoader();
					link.add(new Label("title", loader.loadStringResource(object.getPage(), "label.page.title", getSession().getLocale(), getSession().getStyle(), "")));
				} else {
					link.add(new Label("title", CStringResourceReader.read(object.getTitle())));
				}
				WebMarkupContainer selectedEl = new WebMarkupContainer("selected");
				link.add(selectedEl);
				Label arrow = new Label("arrow");
				link.add(arrow);
				boolean visibleSubItems = hasVisibleChildItems(object);
				boolean selected = isSelected(object);
				if (selected) {
					item.add(AttributeAppender.append("class", " active "));
				}
				selectedEl.setVisible(selected);
				Panel subMenu;

				if (CollectionUtils.listSize(object.getSubItems()) > 0 && visibleSubItems) {
					subMenu = new CSideSubMenuItemPanel("subMenu", object.getSubItemsModel());
				} else {
					subMenu = new CEmptyPanel("subMenu");
					subMenu.setVisible(false);
				}
				if (!visibleSubItems) {
					arrow.setVisible(false);
				}
				subMenu.setRenderBodyOnly(true);
				item.add(subMenu);

			}

			@Override
			protected Iterator<IModel<CSideMenuItemModel>> getItemModels() {
				return modelList.iterator();
			}
		});
	}

	private Label initImage(Item<CSideMenuItemModel> item) {
		Label image = new Label("image");

		if (StringUtils.isBlank(item.getModel().getObject().getIcon())) {
			image.setVisible(false);
		} else {
			image.add(new AttributeAppender("class", item.getModel().getObject().getIcon()));
		}
		return image;
	}

	private Link<WebPage> initLink(Item<CSideMenuItemModel> item) {
		final CSideMenuItemModel object = item.getModel().getObject();
		Link<WebPage> link;
		if (object.getPage() != null) {
			link = new BookmarkablePageLink<>("link", object.getPage());
			String page = object.getPage().getName();
			String[] splitPage = page.split("\\.");
			if (splitPage.length > 1) {
				link.add(AttributeModifier.append("name", splitPage[splitPage.length - 1]));
			}
		} else {
			link = new AjaxFallbackLink("link") {

				@Override
				public void onClick(AjaxRequestTarget target) {
					// do nothing
				}
			};
		}

		if (object.getPage() == null && CollectionUtils.listSize(object.getSubItems()) == 0) {
			throw new CSystemFailureException("Wrong menu initialization");
		}
		
		if (object.getPage() == null) {
			link.add(new AttributeModifier("href", "javascript:;"));
		}

		item.add(link);
		return link;
	}

	private boolean isSelected(CSideMenuItemModel object) {
		CSideMenuItemModel model = AMenu.getInstance().getTopModel(getPage().getClass());
		if (model != null && model.equals(object)) {
			return true;
		}
		return false;
	}

	private boolean hasVisibleChildItems(CSideMenuItemModel object) {
		if (object == null || CollectionUtils.listSize(object.getSubItems()) == 0) {
			return false;
		}
		
		for (CSideMenuItemModel child : object.getSubItems()) {
			if (child.isInMenuVisible()) {
				return true;
			}
			if (hasVisibleChildItems(child)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * items with no rights to display for the current user are removed
	 * 
	 * @param modelList
	 */
	private void removeItems(List<IModel<CSideMenuItemModel>> modelList) {
		Iterator<IModel<CSideMenuItemModel>> iter = modelList.iterator();
		while (iter.hasNext()) {
			CSideMenuItemModel model = iter.next().getObject();
			if (!model.hasRights()) {
				iter.remove();
			} else {
				if (model.hasChildren() && model.getPage() == null && !model.childrenHasRights()) {
					iter.remove();
				}
			}
		}
	}

	private String getUserName() {
		Session session = getSession();
		if (session instanceof CSedSession) {
			String loggedUserName = ((CSedSession) getSession()).getLoggedUserName();
			if (loggedUserName.length() > 15) {
				loggedUserName = ((CSedSession) getSession()).getUser().getName();
			}
			return loggedUserName;
		}
		return "";
	}
}
