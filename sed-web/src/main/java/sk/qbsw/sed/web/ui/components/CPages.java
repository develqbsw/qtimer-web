package sk.qbsw.sed.web.ui.components;

/**
 * class for store pages infos.
 * 
 * @author Marek Martinkovic
 * @since 2.1.0
 * @version 2.1.0
 */
public abstract class CPages {
	public static final CPageMeta STATISTICS = new CPageMeta("application.menu.statistics");

	public static final CPageMeta HOME = new CPageMeta("application.menu.home");

	public static final CPageMeta TIMESHEET = new CPageMeta("application.menu.workReport");

	public static final CPageMeta USERS = new CPageMeta("users.title");
	public static final CPageMeta USERS_ADD = new CPageMeta("users.add");
	public static final CPageMeta USERS_EDIT = new CPageMeta("users.edit");

	public static final CPageMeta ORGANIZATIONS = new CPageMeta("application.menu.organizations");
	public static final CPageMeta CREATE_ORGANIZATIONS = new CPageMeta("application.menu.organizations.create");
	public static final CPageMeta EDIT_ORGANIZATIONS = new CPageMeta("application.menu.organizations.edit");

	private CPages() {
		super();
	}
}
