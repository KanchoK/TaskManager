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
    	new User("kancho", "123456", "Kancho Kanev", "pldboy@abv.bg", true),
    	new User("a", "a", "Petar Ivanov", "plamen1994@abv.bg"),
    };
    
    private static Task[] TASKS = {
    	new Task("Login form","Create a login form for the application",new Date()),
    	new Task("Welcome screen","Create a welcome screen with the list of all tasks in the app",new Date())
    };
	
	private static ArrayList<User> userList = new ArrayList<>();
    

    
    public void addTestDataToDB() {
        deleteData();
        addTestUsers();
    }

    private void deleteData() {
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Task").executeUpdate();
        em.createQuery("DELETE FROM Change").executeUpdate();
        em.createQuery("DELETE FROM Comment").executeUpdate();
        em.createQuery("DELETE FROM ChangePasswordRequest").executeUpdate();
    }

    private void addTestUsers() {

    	userList.add(USERS[0]);
    	userList.add(USERS[1]);
    	userList.get(0).addUserTask(TASKS[0]);
    	userList.get(0).addImportantTask(TASKS[1]);
    	userList.get(1).addUserTask(TASKS[1]);
        for (User user : userList) {
            userDAO.addUser(user);
        }
    }
}
