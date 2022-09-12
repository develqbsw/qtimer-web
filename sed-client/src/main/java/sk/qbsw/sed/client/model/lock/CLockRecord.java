package sk.qbsw.sed.client.model.lock;

import java.io.Serializable;
import java.util.Date;

/**
 * Information about locked object used on the client side
 * 
 * @author Dalibor Rak
 * 
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CLockRecord implements Serializable {
	private Date lastChangeDate;
	private Long id;

	public Date getLastChangeDate() {
		return lastChangeDate;
	}

	public void setLastChangeDate(Date lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
