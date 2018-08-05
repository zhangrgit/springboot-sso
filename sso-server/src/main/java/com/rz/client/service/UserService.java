package com.rz.client.service;

import com.rz.client.domain.User;

public interface UserService {
	/**
	 * 根据username和password查找数据库中的用户并返回
	 */
	public User find(User user);
}