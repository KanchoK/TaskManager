package taskManager.endpoint;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

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

import taskManager.dao.ChangeDAO;
import taskManager.dao.TaskDAO;
import taskManager.dao.UserDAO;
import taskManager.model.Change;
import taskManager.model.Change.ChangeType;
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
	private ChangeDAO changeDAO;
	
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
		User user = userDAO.getUserById(newTask.getExecutor().getUserID());
		user.getUserTasks().add(newTask);
		userDAO.updateUser(user);
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
		User newExecutor = userDAO.getUserById(userId);
		if (oldExecutor != null) {
			if (!oldExecutor.equals(newExecutor)) {
				oldExecutor.getUserTasks().remove(task);
				userDAO.updateUser(oldExecutor);
				Change change = new Change(task, context.getCurrentUser(),
						new Date(), "Executor change",
						oldExecutor.getUsername(), newExecutor.getUsername(),
						ChangeType.EXECUTOR);
				changeDAO.addChange(change);
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("The executor of this task is already " 
											+ oldExecutor.getFullName() + ".").build();
			}
		}
		newExecutor.addUserTask(task);
		userDAO.updateUser(newExecutor);
		return Response.status(Response.Status.OK).entity("Task executor changed successfuly.").build();
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
			if(!task.getStatus().toString().equals(newStatus)) {
				try {
					Status oldStatus = task.getStatus();
					Change change = new Change(task, user, new Date(),
							"Status change", oldStatus.toString(),
							newStatus.toString(), ChangeType.STATUS);
					changeDAO.addChange(change);
					taskDAO.changeStatus(task, newStatus);
					return Response.status(Response.Status.OK).entity("Task status changed successfuly.").build();
				} catch (ApplicationServerInternalException exc) {
					// TODO log the exc
					return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
				}
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("The task status is already " + newStatus + ".").build();
			}
		} else {
			return Response.status(Response.Status.NOT_FOUND).entity("You can't change the status of "
																	+ "tasks on which you are not executor").build();
		}
	}
	
	@POST
	@Path("changeDescription")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeTaskDescription(String inputData) {
		JSONObject jsonObject;
		Integer taskId = -1;
		String newDescription = "";
		try {
			jsonObject = new JSONObject(inputData);
			taskId = jsonObject.getInt("taskID");
			newDescription = jsonObject.getString("description");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Error with the input data.").build();
		}
		Task task = taskDAO.getTaskByID(taskId);
		String oldDescription = task.getDescription();
		if (oldDescription.equals(newDescription)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("The task description is already the same.").build();
		}
		task.setDescription(newDescription);
		taskDAO.updateTask(task);
		Change change = new Change(task, context.getCurrentUser(), new Date(),
				"Description change", oldDescription, newDescription,
				ChangeType.DESCRIPTION);
		changeDAO.addChange(change);
		return Response.status(Response.Status.OK).entity("Task description was updated successfully").build();
	}
	
	@POST
	@Path("changeEndDate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeTaskEndDate(String inputData) throws ParseException {
		JSONObject jsonObject;
		Integer taskId = -1;
		String newEndDateString = null;
		try {
			jsonObject = new JSONObject(inputData);
			taskId = jsonObject.getInt("taskID");
			newEndDateString = jsonObject.getString("endDate");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Error with the input data.").build();
		}
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date newEndDate = format.parse(newEndDateString);
		Task task = taskDAO.getTaskByID(taskId);
		Date oldEndDate = task.getEndDate();
		if (oldEndDate.equals(newEndDate)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("The task end date is already the same.").build();
		}
		if (format.format(new Date()).compareTo(format.format(newEndDate)) > 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Task end date can't be set to a past date.").build();
		}
		task.setEndDate(newEndDate);
		taskDAO.updateTask(task);
		Change change = new Change(task, context.getCurrentUser(), new Date(),
				"End date change", format.format(oldEndDate),
				format.format(newEndDate), ChangeType.END_DATE);
		changeDAO.addChange(change);
		return Response.status(Response.Status.OK).entity("Task end date was updated successfully").build();
	}
	
}