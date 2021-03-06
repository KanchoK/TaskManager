package taskManager.endpoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import taskManager.dao.ChangeDAO;
import taskManager.dao.CommentDAO;
import taskManager.dao.TaskDAO;
import taskManager.model.Change;
import taskManager.model.Comment;
import taskManager.model.Task;
import taskManager.model.Change.ChangeType;
import taskManager.services.UserContext;

@Stateless
@Path("comment")
public class CommentManager {

		@Inject
		private CommentDAO commentDAO;
		
		@Inject
		private TaskDAO taskDAO;
		
		@Inject
		private ChangeDAO changeDAO;
		
		@Inject
		private UserContext context;
		
		@GET
		@Path("allTaskComments")
		@Produces(MediaType.APPLICATION_JSON)
		public Collection<Comment> getAllTaskComments(@QueryParam("taskId") int taskId) {
			Task task = taskDAO.getTaskByID(taskId);
			ArrayList<Comment> comments = new ArrayList<Comment>(commentDAO.getAllTaskComments(task));
			return comments;
		}
		
		@POST
		@Path("create")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response createComment(String inputData) {
			JSONObject jsonObject;
			Integer taskId = -1;
			String content = "";
			try {
				jsonObject = new JSONObject(inputData);
				taskId = jsonObject.getInt("taskID");
				content = jsonObject.getString("content");
			} catch (JSONException e) {
				// TODO log the exc
				e.printStackTrace();
				return Response.status(Response.Status.NOT_FOUND).entity("Error with the input data.").build();
			}
			Comment comment = new Comment();
			comment.setContent(content);
			comment.setDate(Calendar.getInstance().getTime());
			comment.setAuthor(context.getCurrentUser());
			Task task = taskDAO.getTaskByID(taskId);
			task.addComment(comment);
			taskDAO.updateTask(task);
			Change change = new Change(task, context.getCurrentUser(), new Date(), "New comment was added", null, null, ChangeType.COMMENT);
			changeDAO.addChange(change);
			return Response.status(Response.Status.OK).entity("Comment added successfully.").build();
		}

}
