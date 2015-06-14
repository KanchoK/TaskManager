package taskManager.dao;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import taskManager.model.User;
import taskManager.services.EmailSender;
import taskManager.utils.SecurityUtils;
import taskManager.utils.TemplateFIller;

@Singleton
public class UserDAO {
	private static final String MAIL_BODY_TEMPLATE_PATH = "/mail-body-template.vm";
	
	@Inject
	private EmailSender emailSender;
	
    @PersistenceContext
    private EntityManager em;

    public void addUser(User user) {
        user.setPassword(SecurityUtils.getHashedPassword(user.getPassword()));
        em.persist(user);
    }
    
	public void registerUser(User user) {
		user.setPassword(SecurityUtils.generatePassword());
		Map<String, Object> mailParams = putMailParams(user);
		em.persist(user);

		String registrationDetails = TemplateFIller.fillTemplate(MAIL_BODY_TEMPLATE_PATH, mailParams);
		
		// send an email to the user with username and password
		emailSender.sendEmail("taskmanager.ttest@gmail.com", user.getEmail(),
				"Успешна регистрация в TaskManager.", registrationDetails);

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
    
    private Map<String, Object> putMailParams(User user) {
        Map<String, Object> mailParams = new HashMap<>();
        // put data in it
        mailParams.put("username", user.getUsername() == null ? "" : user.getUsername());
        mailParams.put("password", user.getPassword() == null ? "" : user.getPassword());
        mailParams.put("email", user.getEmail() == null ? "" : user.getPassword());
        return mailParams;
    }
}