package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface ActivityService {
    int saveActivity(Activity activity);

    List<Activity> queryActivitiesByConditionsForPage(Map<String, Object> map);

    int queryCountOfActivitiesByConditions(Map<String, Object> map);

    int deleteActivitiesByIds(String[] ids);

    Activity selectActivityById(String id);

    int updateActivityById(Activity activity);

    List<Activity> selectAllActivities();

    List<Activity> selectAllActivitiesByIds(String[] ids);
}
