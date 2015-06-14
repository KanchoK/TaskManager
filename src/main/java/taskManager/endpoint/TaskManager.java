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

import taskManager.model.User;
import taskManager.dao.TaskDAO;
import taskManager.model.Task;
import taskManager.model.Task.Status;
import taskManager.services.UserContext;

@Stateless
@Path("task")
public class TaskManager {

	@Inject
	private TaskDAO taskDAO;
	
	@Inject
	UserContext context; 
	
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

	@GET
	@Path("gettaskbyID")
	@Consumes(MediaType.APPLICATION_JSON)
	public Task getTaskByID(int TaskID){
		return taskDAO.getTaskByID(TaskID);
	}
	
	@POST
	@Path("setexecutor")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setExecutor(User executor, Task task){
		task.setExecutor(executor);
	}
	
	@POST
	@Path("setcurrenttask")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setCurrentTask(int taskID){
	context.setCurrentTask(taskDAO.getTaskByID(taskID));	
	}
	
	@GET
	@Path("getcurrenttask")
	@Produces(MediaType.APPLICATION_JSON)
	public Task getCurrentTask(){
		return context.getCurrentTask();
	}
	
	
}
