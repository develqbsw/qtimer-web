package sk.qbsw.sed.fw.utils;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Reader for String values from wicket property file
 * 
 * @author Dalibor Rak
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CStringResourceReader {

	private CStringResourceReader() {
		// Auto-generated constructor stub
	}

	/**
	 * Reads string label
	 * 
	 * @param key   key to read
	 * @param model model to use as parameters
	 * @return found message
	 */
	public static String read(String key, IModel<?> model) {
		return new StringResourceModel(key, model).getObject();
	}

	/**
	 * Reads string label
	 * 
	 * @param key key to read
	 * @return found message
	 */
	public static String read(String key) {
		return new StringResourceModel(key, null).getObject();
	}

	/**
	 * Gets string resource model
	 * 
	 * @param key   key to read
	 * @param model model to use as parameters
	 * @return string resource model
	 */
	public static StringResourceModel getModel(String key, IModel<?> model) {
		return new StringResourceModel(key, model);
	}

	/**
	 * Gets string resource model
	 * 
	 * @param key key to read
	 * @return found message
	 */
	public static StringResourceModel getModel(String key) {
		return new StringResourceModel(key, null);
	}
}
