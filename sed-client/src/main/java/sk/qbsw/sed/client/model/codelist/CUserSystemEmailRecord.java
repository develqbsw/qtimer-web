package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CUserSystemEmailRecord implements Serializable {

	private Long orderId;
	private String name;
	private Boolean selected;
	private Long userId;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
