package taskManager.endpoint;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.net.ssl.HttpsURLConnection;
import javax.resource.spi.ApplicationServerInternalException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import taskManager.dao.UserDAO;
import taskManager.model.Task;
import taskManager.model.User;
import taskManager.services.UserContext;

@Stateless
@Path("user")
public class UserManager {
	
	@Inject
	private UserDAO userDAO;
	
	@Inject
	private UserContext context;
	
	@POST
	@Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User newUser) {
		if (userDAO.isExistingUser(newUser)) {
			return Response.status(HttpURLConnection.HTTP_CONFLICT).entity("This username already exist.").build();
		}
		try {
			InternetAddress address = new InternetAddress(newUser.getEmail());
			address.validate();
		} catch (AddressException e) {
			return Response.status(HttpURLConnection.HTTP_CONFLICT).entity("Not a valid email.").build();
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
			return Response.status(HttpsURLConnection.HTTP_OK).entity("Email sent successfuly.").build();
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
		User user = userDAO.findUserById(userId);
		try {
			userDAO.resetUserPassword(user, newPassword);
			context.setCurrentUser(user);
			return Response.status(HttpsURLConnection.HTTP_OK).entity("Password changed successfuly.").build();
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
			oldPassowrd = jsonObject.getString("oldPassowrd");
		} catch (JSONException e) {
			// TODO log the exc
			e.printStackTrace();
		}
		try {
			userDAO.changeUserPassword(context.getCurrentUser(), oldPassowrd, newPassword);
			return Response.status(HttpsURLConnection.HTTP_OK).entity("Password changed successfuly.").build();
		} catch (ApplicationServerInternalException exc) {
			// TODO log the exc
			return Response.status(Response.Status.NOT_FOUND).entity(exc.getMessage()).build();
		}
	}
	
}
