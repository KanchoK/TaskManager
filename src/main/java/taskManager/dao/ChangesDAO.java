package taskManager.dao;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import taskManager.model.Changes;
import taskManager.model.User;

@Singleton
public class ChangesDAO {
	
	@PersistenceContext
	private EntityManager em;
	
	public Collection<Changes> getImportantChanges(User user){
		//TODO this method should return changes having flag 'important'
		return null;
	}

}
