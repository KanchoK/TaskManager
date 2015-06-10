package taskManager.endpoint;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import taskManager.dao.TaskDAO;
import taskManager.model.Task;
import taskManager.model.Task.Status;

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
	
	@POST
	@Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createTask(Task newTask) {
		newTask.setStatus(Status.NEW);
		taskDAO.addTask(newTask);
    }
	
}
