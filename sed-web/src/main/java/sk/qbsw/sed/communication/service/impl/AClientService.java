package sk.qbsw.sed.communication.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import sk.qbsw.sed.model.CSystemSettings;

/**
 * 
 * @author Peter Bozik
 *
 */
public abstract class AClientService {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final String controllerUrl;

	@Autowired
	private CSystemSettings settings;

	public AClientService(String controllerUrl) {
		super();
		this.controllerUrl = controllerUrl;
	}

	/**
	 * @deprecated
	 * should use {@link AClientService#AClientService(String)}
	 */
	@Deprecated
	public AClientService() {
		super();
		this.controllerUrl = null;
	}

	protected final String getUrl(String controller, String method) {
		return settings.getApiUrlLocalhost() + controller + method;
	}

	protected final String getUrl(String method) {
		return settings.getApiUrlLocalhost() + controllerUrl + method;
	}
}
