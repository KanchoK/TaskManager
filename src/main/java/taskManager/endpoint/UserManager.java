package taskManager.endpoint;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.net.ssl.HttpsURLConnection;
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
import taskManager.model.User;
import taskManager.services.UserContext;

@Stateless
@Path("user")
public class UserManager {
	
	private static final int TASK_COUNT_WARNING_LIMIT = 2;

	@Inject
	private UserDAO userDAO;
	
	@Inject
	private TaskDAO taskDAO;
	
	@Inject
	private UserContext context;
	
	@POST
	@Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User newUser) {
		if (userDAO.isExistingUser(newUser)) {
			return Response.status(Response.Status.CONFLICT).entity("This username already exist.").build();
		}
		if (userDAO.isEmailTaken(newUser)) {
			return Response.status(Response.Status.CONFLICT).entity("This email is already taken.").build();
		}
		try {
			InternetAddress address = new InternetAddress(newUser.getEmail());
			address.validate();
		} catch (AddressException e) {
			return Response.status(Response.Status.CONFLICT).entity("Not a valid email.").build();
		}

		userDAO.registerUser(newUser);
        return Response.status(HttpsURLConnection.HTTP_OK).entity("User created successfuly.").build();
    }
	
	@POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(User user) {
    	boolean isAuth = userDAO.validateUserCredentials(user.getUsername(), user.getPassword());
    	if (isAuth) {
    		context.setCurrentUser(user);
    		return Response.status(HttpsURLConnection.HTTP_OK).build();
    	}
    	
    	return Response.status(HttpsURLConnection.HTTP_UNAUTHORIZED).build();
    }
	
	@GET
	@Path("authorization")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean authorization() {
		if (context.getCurrentUser() != null) {
			return context.getCurrentUser().isAdmin();
        }
		return false;
	}

	@POST
	@Path("logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public void logoutUser() {
		context.setCurrentUser(null);
	}
    
	@GET
	@Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getAllUsers() {
		ArrayList<User> users = new ArrayList<User>(userDAO.getAllUsers());
        return users;
    }

	@POST
	@Path("addtask")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addTask(Task task,User user){
		user.addUserTask(task);
	}
	
	@GET
	@Path("isSelectedUserAdmin")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isSelectedUserAdmin(@QueryParam("userID") int userID) {
		return userDAO.isUserAdminById(userID);
	}
	
	@POST
	@Path("setAdmin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setAdmin(String inputData) {
		JSONObject jsonObject;
		Integer userID = -1;
		boolean isAdmin = false;
		try {
			jsonObject = new JSONObject(inputData);
			userID = jsonObject.getInt("userID");
			isAdmin = jsonObject.getBoolean("isAdmin");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Error with the input data.").build();
		}
		User user = userDAO.getUserById(userID);
		if (user.isAdmin() == isAdmin) {
			return Response.status(Response.Status.BAD_REQUEST).entity("This user is already with the role you set.").build();
		}
		user.setAdmin(isAdmin);
		userDAO.updateUser(user);
		return Response.status(Response.Status.OK).entity("The user role was changed successfully.").build();
	}
	
	@GET
	@Path("isCurrentTaskImportant")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isCurrentTaskImportant(@QueryParam("taskID") int taskID) {
		Task task = taskDAO.getTaskByID(taskID);
		if (context.getCurrentUser().getImportantTasks().contains(task)) {
			return true;
		}
		return false;
	}
	
	@POST
	@Path("addImportantTask")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addImportantTask(Task task) {
		User user = context.getCurrentUser();
		if (user.getImportantTasks() == null) {
			user.setImportantTasks(new ArrayList<Task>());
		} else if (user.getImportantTasks().contains(task)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("This task is already set as important to you.").build();
		}
		user.getImportantTasks().add(task);
		userDAO.updateUser(user);
		return Response.status(Response.Status.OK).entity("This task was set successfully as important to you.").build();
	}
	
	@POST
	@Path("removeImportantTask")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeImportantTask(Task task) {
		User user = context.getCurrentUser();
		if (user.getImportantTasks().contains(task)) {
			user.getImportantTasks().remove(task);
			userDAO.updateUser(user);
			return Response.status(Response.Status.OK).entity("This task was set successfully as NOT important to you.").build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("This task is already set as NOT important to you.").build();
		}
	}
	
	@GET
	@Path("activeTasksCount")
	public Response getActiveTasksCount(@QueryParam("userID") int userId) {
//		userId is -1 when the user select No executor for a task
		if(userId == -1) {
			return Response.status(Response.Status.NOT_FOUND).entity("You can't set the executor to no one."
					+ "You must choose one of the existing users.").build();
		}
		int taskCount = userDAO.getActiveTasksCount(userId);
		if (taskCount >= TASK_COUNT_WARNING_LIMIT) {
			return Response.status(Response.Status.CONFLICT).entity("This user has " + taskCount + " active tasks. "
											+ "Are you sure you want to asign this task to him?").build();
		}
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Path("passwordforgotten")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendUserChangePasswordLink(String inputData) {
		JSONObject jsonObject;
		String email = "";
		try {
			jsonObject = new JSONObject(inputData);
			email = jsonObject.getString("email");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
		}

		try {
			userDAO.sendUserChangePasswordLink(email);
			return Response.status(Response.Status.OK).entity("Email sent successfuly.").build();
		} catch (ApplicationServerInternalException exc) {
			// TODO log the exc
			return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
		}
	}
	
	@POST
	@Path("resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetUserPassword(String inputData) {
		JSONObject jsonObject;
		String newPassword = "";
		Integer userId = -1;
		try {
			jsonObject = new JSONObject(inputData);
			
			newPassword = jsonObject.getString("newPassword");
			userId = jsonObject.getInt("userId");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
		}
		User user = userDAO.getUserById(userId);
		try {
			userDAO.resetUserPassword(user, newPassword);
			context.setCurrentUser(user);
			return Response.status(Response.Status.OK).entity("Password changed successfuly.").build();
		} catch (ApplicationServerInternalException exc) {
			// TODO log the exc
			return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
		}
	}
	
	@POST
	@Path("changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeUserPassword(String inputData) {
		JSONObject jsonObject;
		String newPassword = "";
		String oldPassowrd = "";
		try {
			jsonObject = new JSONObject(inputData);
			
			newPassword = jsonObject.getString("newPassword");
			oldPassowrd = jsonObject.getString("oldPassword");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
		}
		try {
			userDAO.changeUserPassword(context.getCurrentUser(), oldPassowrd, newPassword);
			return Response.status(Response.Status.OK).entity("Password changed successfuly.").build();
		} catch (ApplicationServerInternalException exc) {
			// TODO log the exc
			return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
		}
	}
	
	@POST
	@Path("changeEmail")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeEmail(String inputData) {
		JSONObject jsonObject;
		String password = "";
		String email = "";
		try {
			jsonObject = new JSONObject(inputData);
			
			password = jsonObject.getString("password");
			email = jsonObject.getString("email");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
		}
		
		try {
			InternetAddress address = new InternetAddress(email);
			address.validate();
		} catch (AddressException e) {
			return Response.status(Response.Status.CONFLICT).entity("Invalid email.").build();
		}		
		
		try {
			userDAO.changeUserEmail(context.getCurrentUser(), password, email);
			return Response.status(HttpsURLConnection.HTTP_OK).entity("Email changed successfuly.").build();
		} catch (ApplicationServerInternalException exc) {
			// TODO log the exc
			return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
		}
	}
}