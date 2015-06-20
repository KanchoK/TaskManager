package taskManager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Users")
public class User implements Serializable{

	private static final long serialVersionUID = -6161179323294573705L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userID;
	
	// TODO username should be unique
	private String username;
	
	// TODO password validation
	private String password;
	
	private String fullName;
	
	// TODO email validation
	private String email;
	
	private boolean isAdmin;

	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "executor")
	private Collection<Task> userTasks = new ArrayList<Task>();
	
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "author")
	private Collection<Comment> comments = new ArrayList<>();
	
	public User() {
		
	}
	
	public User(String username, String password, String fullName, String email) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
	}
	
	public User(String username, String password, String fullName, String email, boolean isAdmin) {
		this(username, password, fullName, email);
		this.isAdmin = isAdmin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Task> getUserTasks() {
		return userTasks;
	}

	public void setUserTasks(Collection<Task> userTasks) {
	for (Task task : userTasks) {
		addUserTask(task);
	}
		this.userTasks = userTasks;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public void addUserTask(Task task){
		task.setExecutor(this);
		this.userTasks.add(task);
	}

	public Collection<Comment> getComments() {
		return comments;
	}

	public void setComments(Collection<Comment> comments) {
		this.comments = comments;
	}
	
	public void addComment(Comment comment) {
		this.comments.add(comment);
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getUserID() == null) ? 0 : this.getUserID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (this.getUserID() == null) {
			if (other.getUserID() != null)
				return false;
		} else if (!this.getUserID().equals(other.getUserID()))
			return false;
		return true;
	}
}
