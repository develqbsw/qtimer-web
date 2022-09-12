package sk.qbsw.sed.web.ui.components.panel;

import java.io.Serializable;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public class ChooseDefaultTimestampFormModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private CCodeListRecord activity;
	private CCodeListRecord project;
	private String phase;
	private String note;

	private Boolean activityChecked;
	private Boolean projectChecked;
	private Boolean phaseChecked;
	private Boolean noteChecked;

	public CCodeListRecord getActivity() {
		return activity;
	}

	public void setActivity(CCodeListRecord activity) {
		this.activity = activity;
	}

	public CCodeListRecord getProject() {
		return project;
	}

	public void setProject(CCodeListRecord project) {
		this.project = project;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getActivityChecked() {
		return activityChecked;
	}

	public void setActivityChecked(Boolean activityChecked) {
		this.activityChecked = activityChecked;
	}

	public Boolean getProjectChecked() {
		return projectChecked;
	}

	public void setProjectChecked(Boolean projectChecked) {
		this.projectChecked = projectChecked;
	}

	public Boolean getPhaseChecked() {
		return phaseChecked;
	}

	public void setPhaseChecked(Boolean phaseChecked) {
		this.phaseChecked = phaseChecked;
	}

	public Boolean getNoteChecked() {
		return noteChecked;
	}

	public void setNoteChecked(Boolean noteChecked) {
		this.noteChecked = noteChecked;
	}
}
