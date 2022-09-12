package sk.qbsw.sed.fw.navigation;

import sk.qbsw.sed.fw.page.AAuthenticatedPage;

/**
 * Menu item is not displayed in menu, it is only for page hierarchy
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CSideMenuInvisibleDetailItemModel extends CSideMenuItemModel {
	private static final long serialVersionUID = 1L;

	public CSideMenuInvisibleDetailItemModel(Class<? extends AAuthenticatedPage> page) {
		super(CSideMenuItemModel.NONE_TILE, page, false);
	}

	public CSideMenuInvisibleDetailItemModel(Class<? extends AAuthenticatedPage> page, CSideMenuItemModel parent) {
		super(CSideMenuItemModel.NONE_TILE, page, false, parent);
	}
}
