package taskManager.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Changes")
public class Change {

	public enum ChangeType {
		COMMENT, STATUS, END_DATE, EXECUTOR, DESCRIPTION
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int changeID;

	@ManyToOne
	@JoinColumn(name = "taskID")
	private Task task;

	@ManyToOne
	@JoinColumn(name = "userID")
	private User author;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private String description;

	private String oldValue;

	private String newValue;
	
	@Enumerated(EnumType.STRING) 
	private ChangeType changeType;

	// @OneToOne(cascade = CascadeType.ALL)
	// @JoinColumn(name="change")
	// private Comment newComment;
	//
	// private Date newEndDate;
	//
	// private Date oldEndDate;
	//
	// private User newExecutor;
	//
	// private User oldExecutor;

	private boolean isSent;

	public Change() {

	}

	public Change(Task changedTask, User author, Date changeDate,
			String changeDescription, String oldValue, String newValue, ChangeType changeType) {
		super();
		this.task = changedTask;
		this.author = author;
		this.date = changeDate;
		this.description = changeDescription;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.changeType = changeType;
		this.isSent = false;
	}

	public int getChangeID() {
		return changeID;
	}

	public void setChangeID(int changeID) {
		this.changeID = changeID;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	@Override
	public String toString() {
		String result = String.format("Task %s has been changed from %s (%s):",
				task.getTaskName(), author.getUsername(), date.toString());
		if (changeType.equals(ChangeType.COMMENT)) {
			return result + description;
		}
		return result
				+ description
				+ String.format("\nOld value:%s, New value:%s", oldValue,
						newValue);
	}
	
}
