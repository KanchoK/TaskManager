package taskManager.endpoint;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.resource.spi.ApplicationServerInternalException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import taskManager.dao.TaskDAO;
import taskManager.dao.UserDAO;
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
	private UserDAO userDAO;
	
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
	public Task getTaskByID(@QueryParam("taskId") int taskId){
		return taskDAO.getTaskByID(taskId);
	}
	
	@POST
	@Path("setExecutor")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setExecutor(String inputData) {
		JSONObject jsonObject;
		Integer taskId = -1;
		Integer userId = -1;
		try {
			jsonObject = new JSONObject(inputData);
			taskId = jsonObject.getInt("taskID");
			userId = jsonObject.getInt("userID");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Error with the input data.").build();
		}
		Task task = taskDAO.getTaskByID(taskId);
		User oldExecutor = task.getExecutor();
		if (oldExecutor != null) {
			oldExecutor.getUserTasks().remove(task);
			userDAO.updateUser(oldExecutor);
		}
		User newExecutor = userDAO.findUserById(userId);
		newExecutor.addUserTask(task);
		userDAO.updateUser(newExecutor);
		return Response.status(Response.Status.OK).entity("Task executor changed successfuly.").build();
	}
	
	@POST
	@Path("setcurrenttask")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setCurrentTask(int taskID) {
	context.setCurrentTask(taskDAO.getTaskByID(taskID));	
	}
	
	@GET
	@Path("getcurrenttask")
	@Produces(MediaType.APPLICATION_JSON)
	public Task getCurrentTask(){
		return context.getCurrentTask();
	}
	
	@POST
	@Path("changeStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeTaskStatus(String inputData) {
		JSONObject jsonObject;
		Integer taskId = -1;
		String newStatus = "";
		try {
			jsonObject = new JSONObject(inputData);
			taskId = jsonObject.getInt("taskID");
			newStatus = jsonObject.getString("status");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Error with the input data.").build();
		}
		Task task = taskDAO.getTaskByID(taskId);
		User user = context.getCurrentUser();
		User executor = task.getExecutor();
		if(executor == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("You can't change the status of "
					+ "tasks with no executor").build();
		} else if(executor.equals(user)) {
			try {
				taskDAO.changeStatus(task, newStatus);
				return Response.status(Response.Status.OK).entity("Task status changed successfuly.").build();
			} catch (ApplicationServerInternalException exc) {
				// TODO log the exc
				return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
			}
		} else {
			return Response.status(Response.Status.NOT_FOUND).entity("You can't change the status of "
																	+ "tasks on which you are not executor").build();
		}
	}
	
	
}
