package taskManager.services;

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

@Stateless
@Path("user")
public class UserManager {
	
	@Inject
	private UserDAO userDAO;
	
	@Inject
	private UserContext context;
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerUser(User newUser) {
        userDAO.addUser(newUser);
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
	
//	@GET
//    @Produces("application/json")
//    public Collection<User> getAllUsers() {
//        return userDAO.getAllUsers();
//    }
	
}
