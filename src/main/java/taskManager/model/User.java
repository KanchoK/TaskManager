package taskManager.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	private int userID;
	
	// TODO username should be unique
	private String username;
	
	private String password;
	
	private String fullName;
	
	private String email;
	
	private boolean isAdmin;
	
	@OneToMany
    private Collection<Task> userTasks;
	
	public User() {
		
	}
	
	public User(String username, String password, String fullName, String email) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
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
		this.userTasks = userTasks;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
}
