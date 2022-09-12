package sk.qbsw.sed.client.model.params;

import java.io.Serializable;

/**
 * Client version of the server entity CParameterEntity
 * 
 * @author Ladislav Rosenberg
 * @Version 1.0
 * @since 1.6.0
 */
@SuppressWarnings("serial")
public class CParameter implements Serializable {
	Long id;

	String name;

	String stringValue;

	Long clientId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
}
