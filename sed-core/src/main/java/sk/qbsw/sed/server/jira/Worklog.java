package sk.qbsw.sed.server.jira;

import java.util.Date;

public class Worklog {

	private final Date started;
	private final Long timeSpentSeconds;
	private final WorkLogAuthor author;

	public Worklog(Date started, Long timeSpentSeconds, WorkLogAuthor author) {
		this.started = started;
		this.timeSpentSeconds = timeSpentSeconds;
		this.author = author;
	}

	public WorkLogAuthor getAuthor() {
		return author;
	}

	public Long getTimeSpentSeconds() {
		return timeSpentSeconds;
	}

	public Date getStarted() {
		return started;
	}
}