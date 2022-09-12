package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.Date;

/**
 * Client model
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CProjectRecord implements Serializable {
	private Long id;
	private Integer order;
	private String name;
	private Boolean active;
	private String note;
	private String changeName;
	private String changeSurname;
	private Date changeTime;
	private String group;
	private String code;
	private Boolean flagDefault;
	private String eviproCode;

	public String getChangedBy() {
		StringBuilder sb = new StringBuilder();
		if (changeName != null) {
			sb.append(changeName);
			if (changeSurname != null)
				sb.append(" ");
		}
		if (changeSurname != null)
			sb.append(changeSurname);
		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getChangeName() {
		return changeName;
	}

	public void setChangeName(String changeName) {
		this.changeName = changeName;
	}

	public String getChangeSurname() {
		return changeSurname;
	}

	public void setChangeSurname(String changeSurname) {
		this.changeSurname = changeSurname;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getFlagDefault() {
		return flagDefault;
	}

	public void setFlagDefault(Boolean flagDefault) {
		this.flagDefault = flagDefault;
	}

	public String getEviproCode() {
		return eviproCode;
	}

	public void setEviproCode(String eviproCode) {
		this.eviproCode = eviproCode;
	}

}
