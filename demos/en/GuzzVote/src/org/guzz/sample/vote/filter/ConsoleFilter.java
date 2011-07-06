/**
 * ConsoleFilter.java created at 2009-10-10 下午05:36:23 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ConsoleFilter implements javax.servlet.Filter{

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request ;
		HttpServletResponse res = (HttpServletResponse) response ;
		
		if(req.getServletPath().equals("/console/login.jsp")){
			chain.doFilter(request, response) ;
			return ;
		}
		
		if(req.getSession().getAttribute("consoleUser") == null){
			res.sendRedirect("./login.jsp") ;
			return ;
		}
		
		chain.doFilter(request, response) ;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
