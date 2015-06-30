package taskManager.utils;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class NotificationScheduler {

	@Inject
	private ChangesEmailBuilder emailBuilder;
	
	@PostConstruct
	public void sentMails() {
		Timer timer = new Timer();
		TimerTask dailyTask = new TimerTask() {
			@Override
			public void run() {
				emailBuilder.buildAndSendEmail();
			}
		};

//		schedule the task to run starting after 24 hours and then again every
//		24 hours...
		timer.schedule(dailyTask, 86400000, 86400000);
		
//		timer.schedule(dailyTask, 15000, 15000);
	}

}
