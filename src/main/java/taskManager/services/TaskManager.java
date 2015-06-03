package taskManager.services;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import taskManager.dao.TaskDAO;
import taskManager.model.Task;

@Stateless
@Path("task")
public class TaskManager {

	@Inject
	private TaskDAO taskDAO;
	
	@GET
    @Produces("application/json")
    public Collection<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }
	
}
