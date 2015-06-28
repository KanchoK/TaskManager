package taskManager.dao;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.resource.spi.ApplicationServerInternalException;

import taskManager.model.Task;
import taskManager.model.Task.Status;

@Singleton
public class TaskDAO {
	
	@PersistenceContext
    private EntityManager em;
	
	public void addTask(Task task) {
        em.persist(task);
    }
	
	public void updateTask(Task task) {
		em.merge(task);
	}
	
	public Collection<Task> getAllTasks() {
		String txtQuery = "SELECT t FROM Task t";
		TypedQuery<Task> query = em.createQuery(txtQuery, Task.class);
        return query.getResultList();
	}
	
	public Task getTaskByID(int taskID){
		String txtQuery = "SELECT t FROM Task t WHERE t.taskID = :taskId";
		TypedQuery<Task> query = em.createQuery(txtQuery, Task.class);
		query.setParameter("taskId", taskID);
		return query.getSingleResult();
	}
	
	public void changeStatus(Task task, String newStatus) throws ApplicationServerInternalException {
		if (task == null) {
			throw new ApplicationServerInternalException("Task not found!");
		}
		
		task.setStatus(Status.valueOf(newStatus));
		updateTask(task);
	}

}
