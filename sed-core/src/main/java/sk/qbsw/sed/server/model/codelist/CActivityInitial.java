package sk.qbsw.sed.server.model.codelist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model mapped to table public.t_ct_activity_initial
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "t_ct_activity_initial")
public class CActivityInitial implements Serializable {
	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_client_order", nullable = true)
	private Integer order;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_flag_working", nullable = false)
	private Boolean working;

	@Column(name = "c_flag_changeable", nullable = false)
	private Boolean changeable;

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getOrder() {
		return this.order;
	}

	public Boolean getValid() {
		return this.valid;
	}

	public Boolean getWorking() {
		return this.working;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setOrder(final Integer order) {
		this.order = order;
	}

	public void setValid(final Boolean valid) {
		this.valid = valid;
	}

	public void setWorking(final Boolean working) {
		this.working = working;
	}

	public Boolean getChangeable() {
		return this.changeable;
	}

	public void setChangeable(final Boolean changeable) {
		this.changeable = changeable;
	}
}
