package taskManager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
	private Integer taskID;
	
	private String taskName;
	
	@Column(length = 255)
	private String description;
	
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userID")
	private User executor;
	
	@Enumerated(EnumType.STRING) 
	private Status status;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="task")
	private Collection<Comment> comments = new ArrayList<Comment>();

//	@ManyToMany(cascade = CascadeType.ALL)
//	@JoinTable(name="ImportantTasks", 
//    joinColumns={@JoinColumn(name="taskID")}, 
//    inverseJoinColumns={@JoinColumn(name="userID")})
//	private Collection<User> interestedUsers = new ArrayList<User>();
	
//	@OneToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name="changedTask")
//	private Collection<Change> changes = new ArrayList<Change>();
	
	public Task() {
		
	}
	
	public Task(String taskName, String description, Date endDate) {
		this.taskName = taskName;
		this.description = description;
		this.endDate = endDate;
		this.status = Status.NEW;
	}
	
	public Integer getTaskID() {
		return taskID;
	}

	public void setTaskID(Integer taskID) {
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
	
	@XmlTransient
	public Collection<Comment> getComments() {
		return comments;
	}

	public void setComments(Collection<Comment> comments) {
		for (Comment taskComments : comments) {
			addComment(taskComments);
		}
			this.comments = comments;
	}
	
	public void addComment(Comment comment) {
		comment.setTask(this);
		if (comments == null) {
			comments = new ArrayList<>();
		}
		this.comments.add(comment);
	}	
	
//	public Collection<User> getInterestedUsers() {
//		return interestedUsers;
//	}
//
//	public void setInterestedUsers(Collection<User> interestedUsers) {
//		this.interestedUsers = interestedUsers;
//	}
//	
//	public void addInterestedUser(User user) {
//		interestedUsers.add(user);
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getTaskID() == null) ? 0 : this.getTaskID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Task))
			return false;
		Task other = (Task) obj;
		if (this.getTaskID() == null) {
			if (other.getTaskID() != null)
				return false;
		} else if (!this.getTaskID().equals(other.getTaskID()))
			return false;
		return true;
	}
	
}
