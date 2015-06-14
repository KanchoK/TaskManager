package taskManager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Tasks")
public class Task {

	public enum Status {
		NEW,
		OPENED,
		DONE
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int taskID;
	
	private String taskName;
	
	private String description;
	
	private Date endDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userID")
	private User executor;
	
	@Enumerated(EnumType.STRING) 
	private Status status;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="task")
	private Collection<Comment> comments = new ArrayList<Comment>();
	
	public Task() {
		
	}
	
	public Task(String taskName, String description, Date endDate) {
		this.taskName = taskName;
		this.description = description;
		this.endDate = endDate;
		this.status = Status.NEW;
	}
	
	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public User getExecutor() {
		return executor;
	}

	public void setExecutor(User executor) {
		this.executor = executor;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}	
	
}
