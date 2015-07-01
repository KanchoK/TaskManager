package taskManager.endpoint;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import taskManager.dao.ChangeDAO;
import taskManager.model.Change;
import taskManager.services.UserContext;

@Stateless
@Path("changes")
public class ChangeManager {

	@Inject
	private UserContext userContext;
	
	@Inject
	private ChangeDAO changesDAO;
	
	@GET
	@Path("getImportantChanges")
	@Produces("application/json")
	public Collection<Change> getimportantchanges(){
		if(userContext.getCurrentUser().isAdmin())
			return changesDAO.importantChange(userContext.getCurrentUser());
		else 
			return null;
	}
}
