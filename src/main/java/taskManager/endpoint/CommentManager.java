package taskManager.endpoint;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import taskManager.dao.CommentDAO;
import taskManager.model.Comment;
import taskManager.services.UserContext;

@Stateless
@Path("comment")
public class CommentManager {

		@Inject
		private CommentDAO commentDAO;
		
		@Inject
		private UserContext context;
		
		@GET
		@Path("getallcomments")
		@Produces("application/json")
		public Collection<Comment> getAllComments(){
		   return commentDAO.getAllComments(context.getCurrentTask());
		}
		@POST
		@Path("create")
		@Consumes(MediaType.APPLICATION_JSON)
		public void createComment(Comment comment){
			commentDAO.createComment(comment);
		}

}
