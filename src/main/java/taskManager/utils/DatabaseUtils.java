package taskManager.utils;

import java.util.ArrayList;
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

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private UserDAO userDAO;
    
    @EJB
    private TaskDAO taskDAO;
	   
    private static User[] USERS = {
    	new User("kancho", "123456", "Kancho Kanev", "kk.user@mymail.com", true),
    	new User("a", "a", "Petar Ivanov", "pe6o.user@mymail.com"),
    };
    
    private static Task[] TASKS = {
    	new Task("Login form","Create a login form for the application",new Date()),
    	new Task("Welcome screen","Create a welcome screen with the list of all tasks in the app",new Date())
    };
	
	private static ArrayList<User> userList = new ArrayList<>();
    

    
    public void addTestDataToDB() {
        deleteData();
        addTestUsers();
//        addTestTasks();
    }

    private void deleteData() {
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Task").executeUpdate();
    }

    private void addTestUsers() {

    	userList.add(USERS[0]);
    	userList.add(USERS[1]);
    	userList.get(0).addUserTask(TASKS[0]);
    	userList.get(1).addUserTask(TASKS[1]);
        for (User user : userList) {
            userDAO.addUser(user);
        }
    }
    
    private void addTestTasks() {
    	for (Task task : TASKS) {
            taskDAO.addTask(task);
        }
    }

}
