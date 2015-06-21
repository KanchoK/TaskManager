package taskManager.filters;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import taskManager.dao.ChangePasswordRequestDAO;
import taskManager.model.ChangePasswordRequest;
import taskManager.model.User;
import taskManager.services.UserContext;

@WebFilter("/*")
public class ProtectedResourcesFilter implements Filter {

	@Inject
	UserContext userContext;
	
	@Inject 
	ChangePasswordRequestDAO changePasswordRequestDAO;

	public void init(FilterConfig fConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (!isHttpCall(request, response)) {
			return;
		}
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		User currentUser = userContext.getCurrentUser();
		String loginUrl = httpServletRequest.getContextPath() + "/";
		String uri = httpServletRequest.getRequestURI();
		
		if (uri.endsWith("resetPassword.html")) {
			String email = httpServletRequest.getParameter("email");
			String code = httpServletRequest.getParameter("code");
			ChangePasswordRequest changePasswordRequest = changePasswordRequestDAO.getRequestByEmailAndCode(email, code);
			
			if(changePasswordRequest != null) {
				Date expiryDate = changePasswordRequest.getExpiryDate();				
				Date now = new Date();
				
				if(!now.after(expiryDate)) {
					String resetPasswordUrl = loginUrl + "resetPassword.html";
					httpServletResponse.sendRedirect(resetPasswordUrl);
					return;
				} else {
					httpServletResponse.sendError(Response.Status.CONFLICT.getStatusCode(), "Reset password link has expired!");
					httpServletResponse.sendRedirect(loginUrl);
					return;
				}
			} else {
				httpServletResponse.sendError(Response.Status.CONFLICT.getStatusCode(), "Invalid link!");
				httpServletResponse.sendRedirect(loginUrl);
				return;
			}
		}
		
		if (currentUser == null
				&& !(uri.endsWith(loginUrl) 
						|| uri.endsWith(".js")
						|| uri.endsWith(".css")
						|| uri.endsWith("rest/user/login")
						|| uri.endsWith("index.html") 
						|| uri.endsWith("rest/user/passwordforgotten"))) {
			httpServletResponse.sendRedirect(loginUrl);
			return;
		} else if (currentUser != null
				&& !(currentUser.isAdmin())
				&& (uri.endsWith("adminPanel.html")
						|| uri.endsWith("rest/task/create") 
						|| uri.endsWith("rest/user/register"))) {
			httpServletResponse.sendRedirect(loginUrl + "home.html");
			return;
		}
		chain.doFilter(request, response);
	}

	private boolean isHttpCall(ServletRequest request, ServletResponse response) {
		return (request instanceof HttpServletRequest)
				&& (response instanceof HttpServletResponse);
	}

	public void destroy() {
	}

}
