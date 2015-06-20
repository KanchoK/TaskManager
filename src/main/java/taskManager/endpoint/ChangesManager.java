package taskManager.endpoint;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import taskManager.dao.ChangesDAO;
import taskManager.model.Changes;
import taskManager.services.UserContext;

@Stateless
@Path("changes")
public class ChangesManager {

	@Inject
	private UserContext userContext;
	
	@Inject
	private ChangesDAO changesDAO;
	
	
	@GET
	@Path("getchanges")
	@Produces("application/json")
	public Collection<Changes> getChanges(){
		return changesDAO.getImportantChanges(userContext.getCurrentUser());
	}
}
