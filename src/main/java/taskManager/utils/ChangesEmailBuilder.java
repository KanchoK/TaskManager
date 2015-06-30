package taskManager.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Inject;

import taskManager.dao.ChangeDAO;
import taskManager.dao.UserDAO;
import taskManager.model.Change;
import taskManager.model.Task;
import taskManager.model.User;
import taskManager.services.EmailSender;

@Singleton
public class ChangesEmailBuilder {

	@Inject
	private UserDAO userDAO;

	@Inject
	private ChangeDAO changeDAO;

	@Inject
	private EmailSender emailSender;
	
	public void buildAndSendEmail() {
		
		Collection<User> users = userDAO.getAllUsers();
		for (User user : users) {
			ArrayList<Task> userTasks = new ArrayList<Task>(user.getUserTasks());
			for (Task task : userTasks) {
				List<Change> changes = changeDAO.getChanges(
						task.getTaskID(), user.getUserID());
				if (changes == null || changes.isEmpty()) {
					return;
				}
				String messageBody = "";
				for (Change change : changes) {
					messageBody += change + "\n";
					change.setSent(true);
					changeDAO.updateChange(change);
				}
				emailSender.sendEmail("taskmanager.ttest@gmail.com",
						user.getEmail(), "Task Manager Notifications",
						messageBody);
			}
		}
		
	}
	
}
