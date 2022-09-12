package sk.qbsw.sed.utils;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

/**
 * Attribute modifier, which removes attribute value from HTML component
 * 
 * @author Lobb
 */
public class CHTMLAttributeRemoveModifier extends AttributeModifier {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new cCSS class remove modifier.
	 *
	 * @param attributeName the attribute modifier
	 * @param valueToRemove the value to remove
	 */
	public CHTMLAttributeRemoveModifier(String attributeName, String valueToRemove) {
		super(attributeName, new Model<String>(valueToRemove));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.AttributeModifier#newValue(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	protected String newValue(String currentValue, String valueToRemove) {
		if (currentValue == null) {
			return "";
		}
		return currentValue.replaceAll(valueToRemove, "");
	}
}
