package taskManager.services;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import taskManager.dao.UserDAO;
import taskManager.model.User;

@SessionScoped
public class UserContext implements Serializable {

	private static final long serialVersionUID = -8579296245582613102L;
	
	private User currentUser;
	
	@Inject
	private UserDAO userDAO;
	
	public User getCurrentUser() {
		if (currentUser != null) {
			return userDAO.getUserById(currentUser.getUserID());
		}
		return null;
	}
	
	public void setCurrentUser(User currentUser) {
		if (currentUser != null) {
			this.currentUser = userDAO.findUserByUsername(currentUser.getUsername());
		} else {
			this.currentUser = null;
		}
	}
}