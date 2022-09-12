package sk.qbsw.sed.server.model.domain;

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

@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_zone", sequenceName = "s_zone", allocationSize = 1)
@Table(schema = "public", name = "t_zone")
public class CZone implements Serializable {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@Id
	@GeneratedValue(generator = "s_zone", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	public CClient getClient() {
		return client;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

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

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
}
