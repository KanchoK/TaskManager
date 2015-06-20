package taskManager.filters;

import java.io.IOException;

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

import taskManager.model.User;
import taskManager.services.UserContext;

@WebFilter("/*")
public class ProtectedResourcesFilter implements Filter {

	@Inject
	UserContext userContext;

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
		if (currentUser == null && 
				!(uri.endsWith(loginUrl) || uri.endsWith(".js") || uri.endsWith(".css") || 
						uri.endsWith("rest/user/login") || uri.endsWith("index.html"))) {
			httpServletResponse.sendRedirect(loginUrl);
			return;
		} else if(currentUser != null && !(currentUser.isAdmin()) && 
				(uri.endsWith("createTask.html") || uri.endsWith("createUser.html") 
						|| uri.endsWith("rest/task/create") || uri.endsWith("rest/user/register"))) {
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