package taskManager.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="Comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer commentID;
	
	private String content;
	
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="userID")
	private User author;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="taskID")
	private Task task;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "newComment")
	private Changes change;
	
	public Comment() {
		
	}
	
	public Comment(String content, User author, Task task) {
		this.content = content;
		this.date = new Date();
		this.author = author;
		this.task = task;
	}
	
	public Integer getCommentID() {
		return commentID;
	}

	public void setCommentID(Integer commentID) {
		this.commentID = commentID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getCommentID() == null) ? 0 : this.getCommentID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Comment))
			return false;
		Comment other = (Comment) obj;
		if (this.getCommentID() == null) {
			if (other.getCommentID() != null)
				return false;
		} else if (!this.getCommentID().equals(other.getCommentID()))
			return false;
		return true;
	}
	
}
