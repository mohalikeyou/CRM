package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserLogin {

    @Autowired
    private UserService userService;
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin() {
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    public @ResponseBody Object login(String loginAct, String loginPwd, String isRemembered, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();

        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        User user = userService.queryUserByloginActAndPwd(map);
        ReturnObject returnObject = new ReturnObject(); // 封装返回信息


        if (user == null) {
            // 查询失败， 用户名或密码错误;
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("用户名或密码错误");
        } else if (user.getExpireTime().compareTo(DateUtils.formatDateTime(new Date())) > 0) {
            // 查询失败， 用户已过期
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("用户已过期");
        } else if ("0".equals(user.getLockState())) {
            // 查询失败， 用户状态被锁定
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("用户状态被锁定");
        } else if (user.getAllowIps().contains(request.getRemoteAddr())) {
          // 查询失败， 用户 IP 受限
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("用户IP受限");
        } else {
            // 登录成功
            // 保存会话信息;
            session.setAttribute(Constants.SESSION_USER, user);
            // 设置成功标记
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);

            // 设置十天内记录密码
            if ("true".equals(isRemembered)) { // 如果选择了记住密码
                Cookie cookieLoginAct = new Cookie("loginAct", loginAct);
                Cookie cookieLoginPwd = new Cookie("loginPwd", loginPwd);
                cookieLoginAct.setMaxAge(10 * 24 * 60 * 60);
                cookieLoginPwd.setMaxAge(10 * 24 * 60 * 60);
                response.addCookie(cookieLoginAct);
                response.addCookie(cookieLoginPwd);
            }
            else { // 如果没有选择记住密码，即使之前有cookie也要设置失效
                Cookie cookieLoginAct = new Cookie("loginAct", "0");
                Cookie cookieLoginPwd = new Cookie("loginPwd", "0");
                cookieLoginAct.setMaxAge(0);
                cookieLoginPwd.setMaxAge(0);
                response.addCookie(cookieLoginAct);
                response.addCookie(cookieLoginPwd);
            }

        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session) {

        // 删除cookie
        Cookie cookieLoginAct = new Cookie("loginAct", "0");
        Cookie cookieLoginPwd = new Cookie("loginPwd", "0");
        cookieLoginAct.setMaxAge(0);
        cookieLoginPwd.setMaxAge(0);
        response.addCookie(cookieLoginAct);
        response.addCookie(cookieLoginPwd);

        // 删除会话
        session.invalidate();

        //跳转到首页
        return "redirect:/";
    }

}
