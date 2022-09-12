package sk.qbsw.sed.fw.page;

/**
 * back navigation is done by
 * session.getPageManager().getPage(getPreviousPageId())
 * 
 * @author Peter Božík
 * @version 0.1
 * @since 0.1
 *
 */
public interface IBackNavigatableByPageId {
	public Integer getPreviousPageId();
}
