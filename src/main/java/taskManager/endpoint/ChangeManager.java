package taskManager.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import taskManager.dao.ChangeDAO;
import taskManager.services.UserContext;

@Stateless
@Path("changes")
public class ChangeManager {

	@Inject
	private UserContext userContext;
	
	@Inject
	private ChangeDAO changesDAO;
	
	
//	@GET
//	@Path("getchanges")
//	@Produces("application/json")
//	public Collection<Change> getChanges(){
//		return changesDAO.getImportantChanges(userContext.getCurrentUser());
//	}
}
