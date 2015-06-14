package taskManager.dao;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import taskManager.model.Task;

@Singleton
public class TaskDAO {
	
	@PersistenceContext
    private EntityManager em;
	
	public void addTask(Task task) {
        em.persist(task);
    }
	
	public Collection<Task> getAllTasks() {
		String txtQuery = "SELECT t FROM Task t";
		TypedQuery<Task> query = em.createQuery(txtQuery, Task.class);
        return query.getResultList();
	}
	
	public Task getTaskByID(int TaskID){
		String txtQuery = "SELECT t FROM Task t WHERE t.taskID=" + TaskID;
		TypedQuery<Task> query = em.createQuery(txtQuery, Task.class);
		return query.getSingleResult();
	}
	
}
