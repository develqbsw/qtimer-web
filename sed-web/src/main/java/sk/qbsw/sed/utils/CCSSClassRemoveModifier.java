package sk.qbsw.sed.utils;

/**
 * Class to remove specific CSS class from HTML component Usage:
 * Component.add(new CssClassRemover("myStyle"));
 * 
 * @author Dalibor Rak
 */
public class CCSSClassRemoveModifier extends CHTMLAttributeRemoveModifier {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new cCSS class remove modifier.
	 *
	 * @param valueToRemove the value to remove
	 */
	public CCSSClassRemoveModifier(String valueToRemove) {
		super("class", valueToRemove);
	}
}