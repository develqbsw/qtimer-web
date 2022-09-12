package sk.qbsw.sed.web.ui.components;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * update menu paylod for event
 *
 * @author Podmajersky Lukas
 * @since 2.1.0
 * @version 2.1.0
 */
public class CEventActionPayload {
	/** ajax request target */
	private AjaxRequestTarget target;
	private String event;
	private Object data;

	/**
	 * create update menu payload for event
	 * 
	 * @param target
	 * @param newContentPanel
	 */
	public CEventActionPayload(AjaxRequestTarget target, String event, Object data) {
		this.target = target;
		this.event = event;
		this.data = data;
	}

	/**
	 * @return the target
	 */
	public AjaxRequestTarget getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(AjaxRequestTarget target) {
		this.target = target;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
