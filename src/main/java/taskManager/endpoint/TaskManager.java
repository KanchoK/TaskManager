package taskManager.endpoint;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import taskManager.dao.TaskDAO;
import taskManager.model.Task;
import taskManager.model.Task.Status;
import taskManager.model.User;
import taskManager.services.UserContext;

@Stateless
@Path("task")
public class TaskManager {

	@Inject
	private TaskDAO taskDAO;
	
	@Inject
	UserContext context; 
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }
	
	@POST
	@Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(Task newTask) {
		java.util.Date now = new java.util.Date();
		
		// format dates because we do not want to compare
		// hours, minutes, etc. 
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		if (fmt.format(now).compareTo(fmt.format(newTask.getEndDate())) > 0) {
			// the date is in the past
			return Response.status(HttpURLConnection.HTTP_CONFLICT).entity("Tasks with past date can not be created.").build();
		}
		newTask.setStatus(Status.NEW);
		taskDAO.addTask(newTask);
		return Response.status(HttpURLConnection.HTTP_OK).entity("Task created.").build();
    }

	@GET
	@Path("gettaskbyID")
	@Consumes(MediaType.APPLICATION_JSON)
	public Task getTaskByID(@QueryParam("taskId") int taskId){
		return taskDAO.getTaskByID(taskId);
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
