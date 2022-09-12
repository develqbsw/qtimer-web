package sk.qbsw.sed.model;

public class CSystemSettings {
	
	private String apiUrl;
	private String apiUrlLocalhost;
	private String wsUrl;
	private Integer photoMaxSize;
	private String systemVersion;

	public Integer getPhotoMaxSize() {
		return photoMaxSize;
	}

	/**
	 * @deprecated
	 * @param photoMaxSize
	 */
	@Deprecated
	public void setPhotoMaxSize(Integer photoMaxSize) {
		this.photoMaxSize = photoMaxSize;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	/**
	 * set by spring
	 * @deprecated
	 * @param apiUrl
	 */
	@Deprecated
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getWsUrl() {
		return wsUrl;
	}

	/**
	 * @deprecated
	 * @param wsUrl
	 */
	@Deprecated
	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}

	public String getApiUrlLocalhost() {
		return apiUrlLocalhost;
	}

	public void setApiUrlLocalhost(String apiUrlLocalhost) {
		this.apiUrlLocalhost = apiUrlLocalhost;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	/**
	 * @deprecated
	 * @param systemVersion
	 */
	@Deprecated
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
}
