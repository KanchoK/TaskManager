package taskManager.utils;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import taskManager.dao.TaskDAO;
import taskManager.dao.UserDAO;
import taskManager.model.Task;
import taskManager.model.User;

@Stateless
public class DatabaseUtils {
    
    private static User[] USERS = {
    	new User("kancho", "123456", "Kancho Kanev", "kk.user@mymail.com"),
    	new User("a", "a", "Petar Ivanov", "pe6o.user@mymail.com"),
    };
    
    private static Task[] TASKS = {
    	new Task("Login form","Create a login form for the application",new Date()),
    	new Task("Welcome screen","Create a welcome screen with the list of all tasks in the app",new Date())
    };

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private UserDAO userDAO;
    
    @EJB
    private TaskDAO taskDAO;
    
    public void addTestDataToDB() {
        deleteData();
        addTestUsers();
        addTestTasks();
    }

    private void deleteData() {
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Task").executeUpdate();
    }

    private void addTestUsers() {
        for (User user : USERS) {
            userDAO.addUser(user);
        }
    }
    
    private void addTestTasks() {
    	for (Task task : TASKS) {
            taskDAO.addTask(task);
        }
    }

}
