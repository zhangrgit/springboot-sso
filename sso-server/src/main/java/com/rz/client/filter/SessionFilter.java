package com.rz.client.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import com.rz.client.constant.AuthConst;
import com.rz.client.storage.ClientStorage;


/**
 * sso认证中心会话过滤
 */
@Component
@ServletComponentScan
@WebFilter(filterName="sessionFilter",urlPatterns="/*")
public class SessionFilter implements Filter{
	public void destroy() {}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		String uri = request.getRequestURI();
		// 注销请求，放行
		if ("/logout".equals(uri)) {
			chain.doFilter(req, res);
			return;
		}
		
		// 已经登录(当前请求客户端)，放行
		System.out.println("是否已登录："+session.getAttribute(AuthConst.IS_LOGIN));
		if (session.getAttribute(AuthConst.IS_LOGIN) != null) {
			// 如果是客户端发起的登录请求，跳转回客户端，并附带token
			String clientUrl = request.getParameter(AuthConst.CLIENT_URL);
			String token = (String) session.getAttribute(AuthConst.TOKEN);
			if (clientUrl != null && !"".equals(clientUrl)) {
				// 存储，用于注销
				ClientStorage.INSTANCE.set(token, clientUrl);
				response.sendRedirect(clientUrl + "?" + AuthConst.TOKEN + "=" + token);
				return;
			}
			if (!"/success".equals(uri)) {
				response.sendRedirect(request.getContextPath()+"/success");
				return;
			}
			chain.doFilter(req, res);
			return;
		}else {
			
		}
		// 登录请求，放行
		if ("/".equals(uri) || "/login".equals(uri)) {
			chain.doFilter(req, res);
			return;
		}
		// 其他请求，拦截
		response.sendRedirect(request.getContextPath());
	}

	public void init(FilterConfig config) throws ServletException {}
}