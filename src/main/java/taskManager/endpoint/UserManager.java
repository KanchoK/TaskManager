package taskManager.endpoint;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
			return Response.status(HttpURLConnection.HTTP_CONFLICT).build();
		}
        userDAO.registerUser(newUser);
        return Response.status(HttpsURLConnection.HTTP_OK).build();
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
    @Path("current")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getUser() {
        if (context.getCurrentUser() != null) {
        	return context.getCurrentUser().getUsername();
        }
        return null;
    }

	@GET
	@Path("logout")
	@Consumes(MediaType.TEXT_PLAIN)
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
	
}
