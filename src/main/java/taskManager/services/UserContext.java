package taskManager.services;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import taskManager.model.Task;
import taskManager.model.User;

@SessionScoped
public class UserContext implements Serializable {

	private static final long serialVersionUID = -8579296245582613102L;
	
	private User currentUser;
	
	private Task currentTask;
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Task getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Task currentTask) {
		this.currentTask = currentTask;
	}
	
}