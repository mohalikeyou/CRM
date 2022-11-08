package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.User;

import java.util.Map;

public interface UserService {
    // 通过账号密码查询用户;
    User queryUserByloginActAndPwd(Map<String, Object> map);
}
