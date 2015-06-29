package taskManager.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Changes")
public class Changes {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int changeID;
	
	@ManyToOne
	private Task changedTask;
	
	@ManyToOne
	private User author;
	
	private String newDescription;
	
	private String oldDescription;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="change")
	private Comment newComment;
	
	private Date newEndDate;
	
	private Date oldEndDate;
	
	private User newExecutor;
	
	private User oldExecutor;
	
	private boolean isSent;
	
	public Changes(){
		
	}
	
	public Changes (User author,Task chngtask,String ndscr,String odscr,Comment cmnt,Date ndt,Date odt,User nextr,User oextr){
	this.author=author;
	this.newDescription=ndscr;
	this.oldDescription=odscr;
	this.newComment=cmnt;
	this.newEndDate=ndt;
	this.oldEndDate=odt;
	this.newExecutor=nextr;
	this.oldExecutor=oextr;
	}

	public Task getChangedTask() {
		return changedTask;
	}

	public void setChangedTask(Task changedTask) {
		this.changedTask = changedTask;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	public String getOldDescription() {
		return oldDescription;
	}

	public void setOldDescription(String oldDescription) {
		this.oldDescription = oldDescription;
	}

	public Comment getNewComment() {
		return newComment;
	}

	public void setNewComment(Comment newComment) {
		this.newComment = newComment;
	}

	public Date getNewEndDate() {
		return newEndDate;
	}

	public void setNewEndDate(Date newEndDate) {
		this.newEndDate = newEndDate;
	}

	public Date getOldEndDate() {
		return oldEndDate;
	}

	public void setOldEndDate(Date oldEndDate) {
		this.oldEndDate = oldEndDate;
	}

	public User getNewExecutor() {
		return newExecutor;
	}

	public void setNewExecutor(User newExecutor) {
		this.newExecutor = newExecutor;
	}

	public User getOldExecutor() {
		return oldExecutor;
	}

	public void setOldExecutor(User oldExecutor) {
		this.oldExecutor = oldExecutor;
	}

	//TODO override equals and hashcode!!!
	
	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

}
