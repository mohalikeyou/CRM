package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request) {
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("userList", userList);
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/save.do")
    public @ResponseBody Object save(Activity activity, HttpSession session) {
        User user = (User) (session.getAttribute(Constants.SESSION_USER));
        activity.setCreateBy(user.getId()); // 我们要保存创建者的ID（因为名字可能会重名）
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setId(UUIDUtils.getUUID());

        ReturnObject returnObject = new ReturnObject();
        try {
            int act = activityService.saveActivity(activity);
            if (act > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage("创建成功！");
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
                returnObject.setMessage("系统忙......请稍后重试");
            }
        } catch (Exception e) {
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("系统忙......请稍后重试");
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivitiesByConditionsForPage.do")
    public @ResponseBody Object  queryActivitiesByConditionsForPage(String name, String owner, String startDate, String endDate, int pageNo, int pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        paramMap.put("owner", owner);
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        paramMap.put("beginNo", (pageNo - 1) * pageSize);
        paramMap.put("pageSize", pageSize);

        List<Activity> activitiesList = activityService.queryActivitiesByConditionsForPage(paramMap);
        int totalRows = activityService.queryCountOfActivitiesByConditions(paramMap);

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("activitiesList", activitiesList);
        returnMap.put("totalRows", totalRows);
        return returnMap;
    }
    @RequestMapping("/workbench/activity/removeActivitiesByIds.do")
    public @ResponseBody Object removeActivitiesByIds(String[] id) {
        ReturnObject returnObject = new ReturnObject();

        try {
            int code = activityService.deleteActivitiesByIds(id);
            if (code > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            }
            else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
                returnObject.setMessage("删除活动失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("系统忙，请稍后。。。。。");

        }


        return returnObject;
    }
}
