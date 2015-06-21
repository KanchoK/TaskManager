package taskManager.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.resource.spi.ApplicationServerInternalException;

import org.apache.commons.lang3.RandomStringUtils;

import taskManager.model.ChangePasswordRequest;
import taskManager.model.User;
import taskManager.services.EmailSender;
import taskManager.utils.SecurityUtils;
import taskManager.utils.TemplateFIller;

@Singleton
public class UserDAO {
	private static final String MAIL_BODY_TEMPLATE_PATH = "/mail-body-template.vm";

	@Inject
	private EmailSender emailSender;

	@Inject
	private ChangePasswordRequestDAO changePasswordRequestDAO;

	@PersistenceContext
	private EntityManager em;

	public void addUser(User user) {
		user.setPassword(SecurityUtils.getHashedPassword(user.getPassword()));
		em.persist(user);
	}

	public void updateUser(User user) {
		user.setPassword(SecurityUtils.getHashedPassword(user.getPassword()));
		em.merge(user);
	}

	public void registerUser(User user) {
		user.setPassword(SecurityUtils.generatePassword());
		Map<String, Object> mailParams = putRegistrationMailParams(user);

		String registrationDetails = TemplateFIller.fillTemplate(
				MAIL_BODY_TEMPLATE_PATH, mailParams);

		// send an email to the user with username and password
		emailSender.sendEmail("taskmanager.ttest@gmail.com", user.getEmail(),
				"Успешна регистрация в TaskManager.", registrationDetails);

		addUser(user);
	}

	public boolean validateUserCredentials(String username, String password) {
		String txtQuery = "SELECT u FROM User u WHERE u.username=:username AND u.password=:password";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("username", username);
		query.setParameter("password",
				SecurityUtils.getHashedPassword(password));
		return queryUser(query) != null;
	}

	public Collection<User> getAllUsers() {
		String txtQuery = "SELECT u FROM User u";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		return query.getResultList();
	}

	public boolean isExistingUser(User user) {
		String txtQuery = "SELECT u FROM User u WHERE u.username=:username";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("username", user.getUsername());
		return queryUser(query) != null;
	}

	public User findUserByUsername(String username) {
		String txtQuery = "SELECT u FROM User u WHERE u.username = :username";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("username", username);
		return queryUser(query);
	}

	public User findUserById(Integer userID) {
		String txtQuery = "SELECT u FROM User u WHERE u.userID = :userID";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("userID", userID);
		return queryUser(query);
	}
	
	public void sendUserChangePasswordLink(String email) throws ApplicationServerInternalException {
		String code = RandomStringUtils.randomAlphanumeric(10);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 1);
		Date expiryDate = calendar.getTime();
		User user = findUserByEmail(email);

		if (user == null) {
			throw new ApplicationServerInternalException("There's no user with the given email!");
		}
		
		ChangePasswordRequest request = changePasswordRequestDAO
				.getRequestByUserId(user.getUserID());

		if (request != null) {
			request.setExpiryDate(expiryDate);
			request.setCode(code);
			changePasswordRequestDAO.update(request);
		} else {
			request = new ChangePasswordRequest(user.getUserID(), expiryDate,
					code);
			changePasswordRequestDAO.add(request);
		}

		String subject = "Forgotten Password in TaskManager";
		String link = "http://localhost:8080/TaskManager/resetPassword.html?email=" + email + "&code=" + code;
		String messageBody = "<h2>To change your password click the link below:</h2><br>"
				+ "<a href="+ link + ">Reset your password</a> <br><br> This link will expire in 24 hours from the moment that the email was sent or if you use it to reset your password.";

		// send an email to the user with link for changing their password
		emailSender.sendEmail("taskmanager.ttest@gmail.com", email, subject,
				messageBody);

	}

	public User findUserByEmail(String email) {
		String txtQuery = "SELECT u FROM User u WHERE u.email = :email";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("email", email);
		return queryUser(query);
	}

	public void resetUserPassword(User user, String newPassword) throws ApplicationServerInternalException {
		if (user == null) {
			throw new ApplicationServerInternalException("User not found!");
		}
		
		user.setPassword(newPassword);
		updateUser(user);
	}
	
	public void changeUserPassword(User user, String oldPassword, String newPassword) throws ApplicationServerInternalException {
		if (user == null) {
			throw new ApplicationServerInternalException("User not found!");
		}
		
		if (!user.getPassword().equals(SecurityUtils.getHashedPassword(oldPassword))) {
			throw new ApplicationServerInternalException("Old password is invalid!");
		}
		
		user.setPassword(newPassword);
		updateUser(user);
	}
	
	public void changeUserEmail(User user, String email) throws ApplicationServerInternalException {
		if (user == null) {
			throw new ApplicationServerInternalException("User not found!");
		}		
		
		user.setEmail(email);
		updateUser(user);
	}
	
	private User queryUser(TypedQuery<User> query) {
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, Object> putRegistrationMailParams(User user) {
		Map<String, Object> mailParams = new HashMap<>();
		// put data in it
		mailParams.put("username",
				user.getUsername() == null ? "" : user.getUsername());
		mailParams.put("password",
				user.getPassword() == null ? "" : user.getPassword());
		mailParams.put("email",
				user.getEmail() == null ? "" : user.getPassword());
		return mailParams;
	}
}