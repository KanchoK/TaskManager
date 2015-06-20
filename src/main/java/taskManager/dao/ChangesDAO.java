package taskManager.dao;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
public class ChangesDAO {
	
	@PersistenceContext
	private EntityManager em;
	
	public Collection<Changes> getImportantChanges(User user){
		
	}

}
