package taskManager.dao;

import java.security.MessageDigest;
import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import taskManager.model.User;
import taskManager.utils.SecurityUtils;

@Singleton
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    public void addUser(User user) {
        user.setPassword(SecurityUtils.getHashedPassword(user.getPassword()));
        em.persist(user);
    }
    
    public void registerUser(User user) {
        user.setPassword(SecurityUtils.generatePassword());
        em.persist(user);
    }
    
    public boolean validateUserCredentials(String username, String password) {
        String txtQuery = "SELECT u FROM User u WHERE u.username=:username AND u.password=:password";
        TypedQuery<User> query = em.createQuery(txtQuery, User.class);
        query.setParameter("username", username);
        query.setParameter("password", SecurityUtils.getHashedPassword(password));
        return queryUser(query) != null;
    }

    public Collection<User> getAllUsers() {
    	  String txtQuery = "SELECT u FROM User u";
          TypedQuery<User> query = em.createQuery(txtQuery, User.class);
          return query.getResultList();
    }
    
    private User queryUser(TypedQuery<User> query) {
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}