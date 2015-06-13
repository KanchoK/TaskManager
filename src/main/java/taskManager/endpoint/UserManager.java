package taskManager.endpoint;

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
    public void registerUser(User newUser) {
        userDAO.registerUser(newUser);
        context.setCurrentUser(newUser);
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
	
    @Path("current")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public String getUser() {
        if (context.getCurrentUser() != null) {
        	return context.getCurrentUser().getUsername();
        }
        return null;
    }

	@Path("logout")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	public void logoutUser() {
		context.setCurrentUser(null);
	}
    
	@Path("all")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
	
}
