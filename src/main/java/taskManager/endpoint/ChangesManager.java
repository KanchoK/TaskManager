package taskManager.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import taskManager.services.UserContext;

@Stateless
@Path("changes")
public class ChangesManager {

	@Inject
	private UserContext userContext;
	/*
	@GET
	@Path("getchanges")
	@Produces("application/json")
	public Collection<Changes> getChanges(){
		
	}*/
}
