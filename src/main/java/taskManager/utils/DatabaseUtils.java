package taskManager.utils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import taskManager.dao.UserDAO;
import taskManager.model.User;

@Stateless
public class DatabaseUtils {
    
    private static User[] USERS = {
            new User("kancho", "123456", "Kancho Kanev", "kk.user@mymail.com"),
            new User("pe6o", "123456", "Petar Ivanov", "pe6o.user@mymail.com")
            };

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private UserDAO userDAO;
    
    public void addTestDataToDB() {
        deleteData();
        addTestUsers();
    }

    private void deleteData() {
        em.createQuery("DELETE FROM User").executeUpdate();
   }

    private void addTestUsers() {
        for (User user : USERS) {
            userDAO.addUser(user);
        }
    }

}
