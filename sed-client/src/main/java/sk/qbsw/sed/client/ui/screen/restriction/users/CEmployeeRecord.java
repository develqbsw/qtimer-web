package sk.qbsw.sed.client.ui.screen.restriction.users;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CEmployeeRecord implements Serializable {
	private Long orderId;
	private Long userId;
	private String name;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
