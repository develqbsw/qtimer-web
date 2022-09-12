package sk.qbsw.sed.fw.navigation;

import java.util.List;

/**
 * Menu item that is only gruping. Clicking on it will only expand the menu, no
 * redirection
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CSideMenuGroupItemModel extends CSideMenuItemModel {
	private static final long serialVersionUID = 1L;

	public CSideMenuGroupItemModel(String title, String icon, List<CSideMenuItemModel> subItems, CSideMenuItemModel parent) {
		super(title, icon, subItems, parent);
	}
}
