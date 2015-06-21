package taskManager.dao;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import taskManager.model.ChangePasswordRequest;

@Singleton
public class ChangePasswordRequestDAO {

    @PersistenceContext
    private EntityManager em;
    
    public ChangePasswordRequest getRequestByUserId(Integer userId) {
		   String txtQuery = "SELECT r FROM ChangePasswordRequest r WHERE r.userId=:userId";
			TypedQuery<ChangePasswordRequest> query = em.createQuery(txtQuery, ChangePasswordRequest.class);
			query.setParameter("userId", userId);
			return queryRequest(query);
    }
    
    public void add(ChangePasswordRequest request) {
    	em.persist(request);
    }
    
    public void update(ChangePasswordRequest request) {
    	em.merge(request);
    }
    
	private ChangePasswordRequest queryRequest(TypedQuery<ChangePasswordRequest> query) {
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
    
}
