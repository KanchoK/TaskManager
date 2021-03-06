package taskManager.endpoint;

import java.util.Collection;
import java.util.LinkedList;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import taskManager.dao.ChangeDAO;
import taskManager.model.Change;
import taskManager.services.UserContext;
import taskManager.utils.ChangesEmailBuilder;

@Stateless
@Path("change")
public class ChangeManager {

	@Inject
	private UserContext userContext;
	
	@Inject
	private ChangeDAO changeDAO;
	
	@Inject
	private ChangesEmailBuilder emailBuilder;
	
	@GET
	@Path("getImportantChanges")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Change> getImportantChanges() {
		Collection<Change> allChanges = changeDAO.getAllChanges();
		Collection<Change> changes = new LinkedList<Change>();
		for(Change change:allChanges) {
			if (userContext.getCurrentUser().getImportantTasks().contains(change.getTask())) {
				changes.add(change);
			}
		}
		return changes;
	}
	
//	@Schedule(hour="8")
	@Schedule(hour="*", minute="*")
	public void sendEmailWithChanges() {
		emailBuilder.buildAndSendEmail();
	}
}
