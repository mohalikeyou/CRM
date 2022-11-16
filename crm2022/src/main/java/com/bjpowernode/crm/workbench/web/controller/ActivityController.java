package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.sun.xml.internal.ws.api.message.Attachment;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @RequestMapping("/workbench/activity/selectActivityById.do")
    public @ResponseBody Object selectActivityById(String id) {
        return activityService.selectActivityById(id);
    }

    @RequestMapping("/workbench/activity/updateActivityById.do")
    public @ResponseBody Object updateActivityById(Activity activity, HttpSession session) {
        ReturnObject returnObject = new ReturnObject();
        User user = (User) (session.getAttribute(Constants.SESSION_USER));
        activity.setEditBy(user.getName());
        activity.setEditTime(DateUtils.formatDateTime(new Date()));

        try {
            int code = activityService.updateActivityById(activity);
            if (code == 1) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
                returnObject.setMessage("系统忙，请稍后.......");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("系统忙，请稍后.......");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/ExportActivitiesInBulk.do")
    public void ExportActivitiesInBulk(HttpServletResponse response) throws IOException {
        List<Activity> activities = activityService.selectAllActivities();

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {// 创建xls格式的文件;
            HSSFSheet sheet = workbook.createSheet("市场活动列表");
            HSSFRow row = sheet.createRow(0);
            // 设置文件头
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("Id");
            cell = row.createCell(1);
            cell.setCellValue("Owner");
            cell = row.createCell(2);
            cell.setCellValue("name");
            cell = row.createCell(3);
            cell.setCellValue("start_date");
            cell = row.createCell(4);
            cell.setCellValue("end_date");
            cell = row.createCell(5);
            cell.setCellValue("cost");
            cell = row.createCell(6);
            cell.setCellValue("description");
            cell = row.createCell(7);
            cell.setCellValue("create_time");
            cell = row.createCell(8);
            cell.setCellValue("create_by");
            cell = row.createCell(9);
            cell.setCellValue("edit_time");
            cell = row.createCell(10);
            cell.setCellValue("edit_by");

            if (activities != null && activities.size() > 0) {
                for (int i = 0; i < activities.size(); ++i) { // 遍历活动列表写入数据;
                    row = sheet.createRow(i + 1);
                    Activity activity = activities.get(i);
                    cell = row.createCell(0);
                    cell.setCellValue(activity.getId());
                    cell = row.createCell(1);
                    cell.setCellValue(activity.getOwner());
                    cell = row.createCell(2);
                    cell.setCellValue(activity.getName());
                    cell = row.createCell(3);
                    cell.setCellValue(activity.getStartDate());
                    cell = row.createCell(4);
                    cell.setCellValue(activity.getEndDate());
                    cell = row.createCell(5);
                    cell.setCellValue(activity.getCost());
                    cell = row.createCell(6);
                    cell.setCellValue(activity.getDescription());
                    cell = row.createCell(7);
                    cell.setCellValue(activity.getCreateTime());
                    cell = row.createCell(8);
                    cell.setCellValue(activity.getCreateBy());
                    cell = row.createCell(9);
                    cell.setCellValue(activity.getEditTime());
                    cell = row.createCell(10);
                    cell.setCellValue(activity.getEditBy());
                }

            }

        // 把服务器端的文件发送给浏览器端;
        response.setContentType("application/octet-stream;charset=UTF-8"); // 设置相应信息，是一个文件
        response.addHeader("Content-Disposition", "attachment;filename=Activity.xls"); // 告诉浏览器下载窗口激活，并设置文件名：
        // 把硬盘中的文件传给浏览器;

        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        }
    }
}
