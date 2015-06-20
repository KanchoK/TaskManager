package taskManager.endpoint;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import taskManager.services.UserContext;
import taskManager.model.Changes;

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
