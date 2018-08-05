package com.rz.client.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rz.client.constant.AuthConst;
import com.rz.client.storage.ClientStorage;
import com.rz.client.storage.SessionStorage;
import com.rz.client.util.HTTPUtil;
import com.rz.client.util.MD5Util;

/**
 * 登录和注销控制器
 */
@Controller
public class LoginController {
	/**
	 * 登录
	 */
	@RequestMapping("/login")
	public String login(HttpServletRequest request,@RequestParam("username") String username,Model model) {
		// 验证用户
		if (!"rz".equals(username)) {
			model.addAttribute("error", "user not exist.");
			return "redirect:/";
		}

		// 授权
		String token = UUID.randomUUID().toString();
		request.getSession().setAttribute(AuthConst.IS_LOGIN, true);
		request.getSession().setAttribute(AuthConst.TOKEN, token);
		
		// 存储，用于注销
		SessionStorage.INSTANCE.set(token, request.getSession());
//		SessionStorage.INSTANCE.setVal(MD5Util.encode(token), token);

		System.out.println("是否已登录2："+request.getSession().getAttribute(AuthConst.IS_LOGIN));

		// 子系统跳转过来的登录请求，授权、存储后，跳转回去
		String clientUrl = request.getParameter(AuthConst.CLIENT_URL);
		System.out.println("clientUrl"+clientUrl);
		if (clientUrl != null && !"".equals(clientUrl)) {
			// 存储，用于注销
			ClientStorage.INSTANCE.set(token, clientUrl);
			System.out.println("客戶端存儲"+ClientStorage.INSTANCE.get(token));
			return "redirect:" + clientUrl + "?" + AuthConst.TOKEN + "=" + token;
		}

		return "redirect:/";
	}

	/**
	 * 注销
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String token = request.getParameter(AuthConst.LOGOUT_REQUEST);
		
		// token存在于请求中，表示从客户端发起的注销；token存在于会话中，表示从认证中心发起的注销
		if (token != null && !"".equals(token)) {
			session = (HttpSession) SessionStorage.INSTANCE.get(token);
		} else {
			token = (String) session.getAttribute(AuthConst.TOKEN);
		}
		
		if (session != null) {
			session.invalidate();
		}
		
		// 注销子系统
		List<String> list = ClientStorage.INSTANCE.get(token);
		if(list!=null && list.size()>0) {
			System.out.println("要注銷的子系統"+Arrays.toString(list.toArray(new String[list.size()])));
		}
		if (list != null && list.size() > 0) {
			Map<String, String> params = new HashMap<String, String>();
			params.put(AuthConst.LOGOUT_REQUEST, token);
			for (String url : list) {
				HTTPUtil.post(url, params);
			}
		}
		
		return "redirect:/";
	}
}