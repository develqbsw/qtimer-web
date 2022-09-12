package sk.qbsw.sed.fw.utils;

import java.util.List;
import java.util.Set;

import javax.swing.ListModel;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class CollectionUtils {

	private CollectionUtils() {
		// Auto-generated constructor stub
	}

	/**
	 * calculates size of the list
	 * 
	 * @param list
	 * @return 0 if list ==null, or the size is 0
	 */
	public static int listSize(List<?> list) {
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
	}

	/**
	 * calculates size of the set
	 * 
	 * @param set
	 * @return 0 if set ==null, or the size is 0
	 */
	public static int setSize(Set<?> set) {
		if (set == null) {
			return 0;
		} else {
			return set.size();
		}
	}

	public static <T> int listModelSize(ListModel<T> list) {
		if (list == null) {
			return 0;
		} else {
			return list.getSize();
		}
	}
}
