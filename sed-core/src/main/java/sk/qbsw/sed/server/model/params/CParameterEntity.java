package sk.qbsw.sed.server.model.params;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import sk.qbsw.sed.server.model.domain.CClient;

/**
 * Model mapped to table public.t_param
 * 
 * @author Ladislav Rosenberg
 * @Version 1.0
 * @since 1.6.0
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_param", sequenceName = "s_param", allocationSize = 1)
@Table(schema = "public", name = "t_param")
public class CParameterEntity implements Serializable {
	
	@Id
	@GeneratedValue(generator = "s_param", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_string_value", nullable = false)
	private String stringValue;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

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

	public CClient getClient() {
		return client;
	}

	public void setClient(CClient client) {
		this.client = client;
	}
}
