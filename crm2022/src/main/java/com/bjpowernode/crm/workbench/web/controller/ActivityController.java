package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.ExcelUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import javafx.beans.binding.ObjectBinding;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

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
    public @ResponseBody Object queryActivitiesByConditionsForPage(String name, String owner, String startDate, String endDate, int pageNo, int pageSize) {
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
            } else {
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
    public void ExportActivitiesInBulk(boolean selected, String[] id, HttpServletResponse response) throws IOException {
        List<Activity> activities;
        if (!selected) {
            activities = activityService.selectAllActivities();
        } else {
            activities = activityService.selectAllActivitiesByIds(id);
        }


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

    // 批量导入市场活动
    @RequestMapping("/workbench/activity/ImportActivitiesInBulk.do")
    public @ResponseBody Object ImportActivitiesInBulk(MultipartFile myfile, HttpSession session) throws IOException {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        ReturnObject returnObject = new ReturnObject();
        try {
//            // 把提交的文件，保存到硬盘中;
//            String originalFilename = myfile.getOriginalFilename();
//            File targetFile = new File("d:\\syncfiles\\master2\\" + originalFilename);
//            myfile.transferTo(targetFile); // 把上传的文件复制为硬盘中targetFile中;

            // 解析数据：读取excel文件;
            HSSFWorkbook workbook = new HSSFWorkbook(myfile.getInputStream());// 表，从内存中读到内存中
            HSSFSheet sheet = workbook.getSheetAt(0); // 页
            ArrayList<Activity> activities = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 读取每一行的数据，注意sheet中的getLast恰好是最后一行
                HSSFRow row = sheet.getRow(i);
                Activity activity = new Activity();
                activity.setId(UUIDUtils.getUUID()); // 自己定义的
                activity.setOwner(user.getId()); // 定义：导入者就是项目拥有者
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                activity.setCreateBy(user.getId()); // 当前登录用户就是创建者
                for (int j = 0; j < row.getLastCellNum(); j++) { // 读取每一个单元格格的数据； 注意row中的getLast，不是最后一个单元格， 而是 加一
                    HSSFCell cell = row.getCell(j); // 按顺序读取方格;
                    String cellValue = ExcelUtils.getCellValue(cell); // 获取cell的值;

                    // 每一列的数据，实现已经用模板定义好了，因为我们直接用下标插入对应的数据
                    if (j == 0) {
                        activity.setName(cellValue);
                    } else if (j == 1) {
                        activity.setStartDate(cellValue);
                    } else if (j == 2) {
                        activity.setEndDate(cellValue);
                    } else if (j == 3) {
                        activity.setCost(cellValue);
                    } else if (j == 4) {
                        activity.setDescription(cellValue);
                    }
                }
                activities.add(activity);
            }

            // 插入数据，返回响应信息；
            int code = activityService.saveAllActivitiesByList(activities);
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setMessage("成功批量导入" + code + "条市场活动！");
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("系统忙。。。。请稍后再试");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityForDetail.do")
    public String queryActivityForDetail(String id, HttpServletRequest request) {
        Activity activity = activityService.queryActivityByIdForDetail(id);
        List<ActivityRemark> activityRemarks = activityRemarkService.queryActivityRemarkByIdForDetail(id);

        // 把数据放到请求域中;
        request.setAttribute("activity", activity);
        request.setAttribute("activityRemarks", activityRemarks);

        return "workbench/activity/detail";
    }
    @RequestMapping("/workbench/activity/saveActivityRemark.do")
    public @ResponseBody Object saveActivityRemark(ActivityRemark activityRemark, HttpSession session) {
        User user = (User) (session.getAttribute(Constants.SESSION_USER));
        activityRemark.setId(UUIDUtils.getUUID());
        activityRemark.setCreateTime(DateUtils.formatDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag(Constants.ACTIVITY_NO_EDITED);

        ReturnObject returnObject = new ReturnObject();
        try {
            int code = activityRemarkService.saveActivityRemarkByActivityId(activityRemark);
            if (code > 0) {
                returnObject.setRetData(activityRemark);
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
                returnObject.setMessage("系统忙。。。。请稍后再试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("系统忙。。。。请稍后再试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    public @ResponseBody Object deleteActivityRemarkById(String id) {
        ReturnObject object = new ReturnObject();
        try {
            int code = activityRemarkService.deleteActivityRemarkById(id);
            if (code > 0) {
                object.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                object.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
                object.setMessage("系统忙。。。。。请稍后再试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            object.setMessage("系统忙。。。。。请稍后再试");
        }
       return object;
    }

    @RequestMapping("/workbench/activity/editActivityRemarkById.do")
    public @ResponseBody Object editActivityRemarkById(ActivityRemark activityRemark, HttpSession session) {
        User user = (User) (session.getAttribute(Constants.SESSION_USER));
        activityRemark.setEditTime(DateUtils.formatDateTime(new Date()));
        activityRemark.setEditBy(user.getId());
        activityRemark.setEditFlag(Constants.ACTIVITY_YES_EDITED);
        ReturnObject returnObject = new ReturnObject();
        try {
            int code = activityRemarkService.updateActivityRemarkById(activityRemark);
            if (code > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage("设置成功！");
                returnObject.setRetData(activityRemark);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
                returnObject.setMessage("系统正忙，请稍后。。。。。。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAILURE);
            returnObject.setMessage("系统正忙，请稍后。。。。。。");
        }
        return returnObject;
    }
}
