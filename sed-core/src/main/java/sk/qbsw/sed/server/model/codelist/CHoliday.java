package sk.qbsw.sed.server.model.codelist;

import java.io.Serializable;
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

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.server.model.domain.CClient;

/**
 * Model mapped to table public.t_holiday
 * 
 * @author Ladislav Rosenberg
 * @version 0.1
 * @since 1.6.2
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_ct_holiday", sequenceName = "s_ct_holiday", allocationSize = 1)
@Table(schema = "public", name = "t_ct_holiday")
public class CHoliday implements Serializable {

	@Id
	@GeneratedValue(generator = "s_ct_holiday", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@Column(name = "c_day", nullable = false)
	private Calendar day;

	@Column(name = "c_description", nullable = false)
	private String description;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CClient getClient() {
		return client;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

	public Calendar getDay() {
		return day;
	}

	public void setDay(Calendar day) {
		this.day = day;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public CHolidayRecord convert() {
		CHolidayRecord record = new CHolidayRecord();

		record.setId(this.getId());
		record.setClientId(this.getClient().getId());
		record.setDay(this.getDay().getTime());
		record.setDescription(this.getDescription());
		record.setActive(this.getValid());

		return record;
	}
}
