package taskManager.dao;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import taskManager.model.Comment;
import taskManager.model.Task;


@Singleton
public class CommentDAO {
	
	   @PersistenceContext
	    private EntityManager em;
	  
	   public Collection<Comment> getAllTaskComments(Task task) {
		   String txtQuery = "SELECT c FROM Comment c WHERE c.task=:task ORDER BY c.date DESC";
		   TypedQuery<Comment> query = em.createQuery(txtQuery, Comment.class);
		   query.setParameter("task",task);
		   return query.getResultList();
	   }
}
