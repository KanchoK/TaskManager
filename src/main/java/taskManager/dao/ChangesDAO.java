package taskManager.dao;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import taskManager.model.Changes;
import taskManager.model.User;

@Singleton
public class ChangesDAO {
	
	@PersistenceContext
	private EntityManager em;
	
	public Collection<Changes> getImportantChanges(User user){
		String txtQuery = "SELECT u.importantTasks FROM User u WHERE u.userID = :id";
		TypedQuery<Changes> query = em.createQuery(txtQuery, Changes.class);
		query.setParameter("id", user.getUserID());
		return query.getResultList();
	}
	public void addChangeToDB(Changes change){
		em.persist(change);
	}

}
