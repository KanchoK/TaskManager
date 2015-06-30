package taskManager.dao;

import java.util.List;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import taskManager.model.Change;

@Singleton
public class ChangeDAO {

	@PersistenceContext
	private EntityManager em;

	public void addChange(Change change) {
		em.persist(change);
	}
	
	public void updateChange(Change change) {
		em.merge(change);
	}

	public List<Change> getChangesMadeByOterUsers(Integer taskID, Integer userID) {
		String txtQuery = "SELECT ch FROM Change ch JOIN ch.task t JOIN ch.author a "
						+ "WHERE t.taskID = :taskID AND a.userID <> :userID AND ch.isSent = false";
		TypedQuery<Change> query = em.createQuery(txtQuery, Change.class);
		query.setParameter("taskID", taskID);
		query.setParameter("userID", userID);
		return query.getResultList();
	}

	public List<Change> getChangesByUserID(Integer userID) {
		String txtQuery = "SELECT ch FROM Change ch JOIN ch.author a WHERE a.userID = :userID";
		TypedQuery<Change> query = em.createQuery(txtQuery, Change.class);
		query.setParameter("userID", userID);
		return query.getResultList();
	}

}
