package taskManager.dao;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.resource.spi.ApplicationServerInternalException;

import taskManager.model.Task;
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

	public void updateUser(User user) {
		em.merge(user);
	}
	
	public void updateUserAndHashPassword(User user) {
		user.setPassword(SecurityUtils.getHashedPassword(user.getPassword()));
		updateUser(user);
	}
	
	public void registerUser(User user) {
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
	
	public boolean isEmailTaken(User user) {
		String txtQuery = "SELECT u FROM User u WHERE u.email=:email";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("email", user.getEmail());
		return queryUser(query) != null;
	}

	public User findUserByUsername(String username) {
		String txtQuery = "SELECT u FROM User u WHERE u.username = :username";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("username", username);
		return queryUser(query);
	}

	public User getUserById(Integer userID) {
		String txtQuery = "SELECT u FROM User u WHERE u.userID = :userID";
		TypedQuery<User> query = em.createQuery(txtQuery, User.class);
		query.setParameter("userID", userID);
		return queryUser(query);
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
		updateUserAndHashPassword(user);
	}
	
	public void changeUserPassword(User user, String oldPassword, String newPassword) throws ApplicationServerInternalException {
		if (user == null) {
			throw new ApplicationServerInternalException("User not found!");
		}
		
		if (!user.getPassword().equals(SecurityUtils.getHashedPassword(oldPassword))) {
			throw new ApplicationServerInternalException("Old password is invalid!");
		}
		
		user.setPassword(newPassword);
		updateUserAndHashPassword(user);
	}
	
	public void changeUserEmail(User user, String password, String email) throws ApplicationServerInternalException {
		if (user == null) {
			throw new ApplicationServerInternalException("User not found!");
		}		
		
		if (!user.getPassword().equals(SecurityUtils.getHashedPassword(password))) {
			throw new ApplicationServerInternalException("Password is invalid!");
		}
		
		user.setEmail(email);
		updateUserAndHashPassword(user);
	}
	
	public int getActiveTasksCount(Integer userID) {
		String txtQuery = "SELECT COUNT(t) FROM User u JOIN u.userTasks t WHERE u.userID = :userID "
							+ "AND (t.status = :statusNew OR t.status = :statusOpened)";
		TypedQuery<Long> query = em.createQuery(txtQuery, Long.class);
		query.setParameter("userID", userID);
		query.setParameter("statusNew", Task.Status.NEW);
		query.setParameter("statusOpened", Task.Status.OPENED);
		return query.getSingleResult().intValue();
	}
	
	private User queryUser(TypedQuery<User> query) {
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}