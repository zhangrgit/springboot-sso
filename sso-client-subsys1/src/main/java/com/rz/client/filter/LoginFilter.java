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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import com.rz.client.constant.AuthConst;
import com.rz.client.storage.SessionStorage;

/**
 * 客户端登录filter
 * 1、@Component 这个注解的目的是将LoginFilter交给容器来处理。也就是让LoginFilter起作用
 * 2、@ServletComponentScan 这个使用来扫描@WebFilter 的让@WebFilter起作用。这个@ServletComponentScan可以写在Apllication这个上面，通用配置。
 * 3、@WebFilter 这个用处显而易见，针对于什么链接做过滤，filter的名称是为什么。
 */
@Component
@ServletComponentScan
@WebFilter(filterName="loginFilter",urlPatterns="/*")
public class LoginFilter implements Filter {
	
//	private FilterConfig config;
	
	@Value("${loginUrl}")
	private String loginUrl;

	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		
		// 已经登录，放行
		System.out.println("子是否已登录:"+session.getAttribute(AuthConst.IS_LOGIN));
		if (session.getAttribute(AuthConst.IS_LOGIN) != null) {
			chain.doFilter(req, res);
			return;
		}
		// 从认证中心回跳的带有token的请求，有效则放行
		String token = request.getParameter(AuthConst.TOKEN);
		if (token != null) {
			//此处可加入token校验(到认证中心)
			session.setAttribute(AuthConst.IS_LOGIN, true);
			session.setAttribute(AuthConst.TOKEN, token);
			// 存储，用于注销
			SessionStorage.INSTANCE.set(token, session);
			chain.doFilter(req, res);
			return;
		}
		System.out.println("haha:"+request.getRequestURL());
		// 重定向至登录页面，并附带当前请求地址
		response.sendRedirect(loginUrl+"?" + AuthConst.CLIENT_URL + "=" + request.getRequestURL());
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
//		config = filterConfig;
	}
}