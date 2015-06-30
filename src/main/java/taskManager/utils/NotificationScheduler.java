package taskManager.utils;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import taskManager.dao.ChangeDAO;
import taskManager.dao.UserDAO;
import taskManager.model.Change;
import taskManager.model.User;
import taskManager.services.EmailSender;

@Singleton
@Startup
public class NotificationScheduler {

	@Inject
	private UserDAO userDAO;

	@Inject
	private ChangeDAO changeDAO;

	@Inject
	private EmailSender emailSender;

	@PostConstruct
	public void sentMails() {
		Timer timer = new Timer();
		TimerTask dailyTask = new TimerTask() {
			@Override
			public void run() {
				Collection<User> users = userDAO.getAllUsers();
				for (User user : users) {
					List<Change> changes = changeDAO.getChangesByUserID(user
							.getUserID());
					if (changes == null || changes.isEmpty()) {
						return;
					}
					String messageBody = "";
					for (Change change : changes) {
						messageBody += change;
					}
					emailSender.sendEmail("taskmanager.ttest@gmail.com",
							user.getEmail(), "Task Manager Notifications",
							messageBody);
				}

			}
		};

		// schedule the task to run starting after 24 hours and then again every
		// 24 hours...
		timer.schedule(dailyTask, 86400000, 86400000);
		
//		timer.schedule(dailyTask, 15000, 15000);
	}

}
