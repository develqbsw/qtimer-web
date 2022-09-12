package sk.qbsw.sed.server.model.domain;

import java.util.Calendar;

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

import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.codelist.CRequestStatus;
import sk.qbsw.sed.server.model.codelist.CRequestType;

/**
 * Model mapped to table public.t_request
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_request", sequenceName = "s_request", allocationSize = 1)
@Table(schema = "public", name = "t_request")
public class CRequestForEmail {
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@Column(name = "c_date_from", nullable = false)
	private Calendar dateFrom;

	@Column(name = "c_date_to", nullable = false)
	private Calendar dateTo;

	@Id
	@GeneratedValue(generator = "s_request", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_owner", nullable = false)
	private CUser owner;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_status", nullable = false)
	private CRequestStatus status;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_request_type", nullable = false)
	private CRequestType type;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_reason", nullable = true)
	private CRequestReason reason;

	public CClient getClient() {
		return this.client;
	}

	public Calendar getDateFrom() {
		return this.dateFrom;
	}

	public Calendar getDateTo() {
		return this.dateTo;
	}

	public Long getId() {
		return this.id;
	}

	public CUser getOwner() {
		return this.owner;
	}

	public CRequestStatus getStatus() {
		return this.status;
	}

	public CRequestType getType() {
		return this.type;
	}

	public void setClient(final CClient client) {
		this.client = client;
	}

	public void setDateFrom(final Calendar dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(final Calendar dateTo) {
		this.dateTo = dateTo;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setOwner(final CUser owner) {
		this.owner = owner;
	}

	public void setStatus(final CRequestStatus status) {
		this.status = status;
	}

	public void setType(final CRequestType type) {
		this.type = type;
	}

	public CRequestReason getReason() {
		return reason;
	}

	public void setReason(CRequestReason reason) {
		this.reason = reason;
	}
}
