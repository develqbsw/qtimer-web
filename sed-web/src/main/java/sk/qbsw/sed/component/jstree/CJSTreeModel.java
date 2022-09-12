package sk.qbsw.sed.component.jstree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for the {@link CJSTreePanel}
 * 
 * @author Peter Bozik
 *
 */
public class CJSTreeModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * object id
	 */
	private Long id;
	
	/**
	 * text to be displayed
	 */
	private String text;
	
	/**
	 * hierarchical children
	 */
	private List<CJSTreeModel> children;

	private Boolean valid;

	private Map<String, ArrayList<String>> li_attr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<CJSTreeModel> getChildren() {
		return children;
	}

	public void setChildren(List<CJSTreeModel> children) {
		this.children = children;
	}

	public Map<String, ArrayList<String>> getLi_attr() {

		if (!this.valid) {
			Map<String, ArrayList<String>> map = new HashMap<>();
			ArrayList<String> values = new ArrayList<>();
			values.add("invalidUser");
			map.put("class", values);

			return map;
		}

		return li_attr;
	}

	public void setLi_attr(Map<String, ArrayList<String>> li_attr) {
		this.li_attr = li_attr;
	}
}
