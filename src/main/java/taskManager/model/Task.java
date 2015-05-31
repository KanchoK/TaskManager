package taskManager.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	private String discription;
	
	private Date endDate;
	
	private int executor;
	
	@Enumerated(EnumType.STRING) 
	private Status status;
	
	public Task() {
		
	}
	
	public Task(String taskName, String discription, Date endDate) {
		this.taskName = taskName;
		this.discription = discription;
		this.endDate = endDate;
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

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getExecutor() {
		return executor;
	}

	public void setExecutor(int executor) {
		this.executor = executor;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}	
	
}
