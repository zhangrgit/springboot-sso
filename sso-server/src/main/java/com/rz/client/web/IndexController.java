package com.rz.client.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rz.client.constant.AuthConst;


/**
 * 认证中心页面显示控制器
 */
@Controller
public class IndexController {
	/**
	 * 登录页面
	 */
	@RequestMapping("/")
	public String index(HttpServletRequest request, Model model) {
		model.addAttribute(AuthConst.CLIENT_URL, request.getParameter(AuthConst.CLIENT_URL));
		return "index";
	}

	/**
	 * 登录成功页面
	 */
	@RequestMapping("/success")
	public String success() {
		return "success";
	}
	
}