package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

/**
 * Model used for transfering codelists to client side
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CCodeListRecord implements Serializable {

	/**
	 * ID of the record
	 */
	private Long id;

	/**
	 * Name
	 */
	private String name;

	/**
	 * Description
	 */
	private String description;

	/**
	 * short code
	 */
	private String code;

	/**
	 * Type used to identify grouping eg. icon
	 */
	private String type;

	/**
	 * Prefix ID
	 */
	private String prefixId;

	public CCodeListRecord() {
		// default constructor
	}

	public CCodeListRecord(final Long id, final String name, final String description, final String type) {
		this();
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public CCodeListRecord(final Long id, final String name, final String code) {
		this();
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public CCodeListRecord(final Long id, final String name) {
		this();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getPrefixId() {
		return this.prefixId;
	}

	public void setPrefixId(final String prefixId) {
		this.prefixId = prefixId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		CCodeListRecord other = (CCodeListRecord) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
