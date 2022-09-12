package sk.qbsw.sed.fw.navigation;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
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
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.fw.utils.CollectionUtils;

/**
 * Menu subitems panel
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CSideSubMenuItemPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer subMenu;
	private final List<IModel<CSideMenuItemModel>> subItems;

	public CSideSubMenuItemPanel(String id, final List<IModel<CSideMenuItemModel>> subItems) {
		super(id);
		this.subItems = subItems;

	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		subMenu = new WebMarkupContainer("submenu");
		add(subMenu);
		Iterator<IModel<CSideMenuItemModel>> iter = subItems.iterator();
		boolean isVisible = false;
		while (iter.hasNext()) {
			CSideMenuItemModel model = iter.next().getObject();
			if (!model.hasRights() && !model.childrenHasRights()) {
				iter.remove();
			}
			if (isSelected(model)) {
				isVisible = true;
			}
		}
		if (isVisible) {
			subMenu.add(AttributeModifier.append("style", "display:block"));
		} else {
			subMenu.add(AttributeModifier.append("style", "display:none"));
		}
		subMenu.add(new RefreshingView<CSideMenuItemModel>("sideMenuSubItems") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<CSideMenuItemModel> item) {
				final CSideMenuItemModel object = item.getModel().getObject();
				Link<WebPage> link = initLink(item);
				String title = object.getTitle();
				if (title == null) {
					ComponentStringResourceLoader loader = new ComponentStringResourceLoader();
					link.add(new Label("title", loader.loadStringResource(object.getPage(), "label.page.title", getSession().getLocale(), getSession().getStyle(), "")));
				} else {
					link.add(new Label("title", CStringResourceReader.read(object.getTitle())));
				}
				if (isSelected(object)) {
					item.add(new AttributeAppender("class", " active "));
				}
				if (!object.isInMenuVisible()) {
					link.add(AttributeModifier.append("style", "display:none"));
				}
				String page = object.getPage().getName();
				String[] splitPage = page.split("\\.");
				if (splitPage.length > 1) {
					link.add(AttributeModifier.append("name", splitPage[splitPage.length - 1]));
				}
			}

			@Override
			protected Iterator<IModel<CSideMenuItemModel>> getItemModels() {
				return subItems.iterator();
			}
		});
	}

	private boolean isSelected(CSideMenuItemModel item) {
		return AMenu.getInstance().isChildItemPage(item.getPage(), getPage().getClass());
	}

	private Link<WebPage> initLink(Item<CSideMenuItemModel> item) {
		final CSideMenuItemModel object = item.getModel().getObject();
		Link<WebPage> link = new BookmarkablePageLink<>("link", object.getPage());

		if (object.getPage() != null && CollectionUtils.listSize(object.getSubItems()) > 0) {
			for (CSideMenuItemModel model : object.getSubItems()) {
				if (model.isInMenuVisible()) {
					throw new CSystemFailureException("Wrong menu initialization");
				}
			}

		}
		if (object.getPage() == null && CollectionUtils.listSize(object.getSubItems()) == 0) {
			throw new CSystemFailureException("Wrong menu initialization");
		}
		item.add(link);
		return link;
	}
}
