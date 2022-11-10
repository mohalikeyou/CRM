package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.mapper.UserMapper;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Override
    public int saveActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    public List<Activity> queryActivitiesByConditionsForPage(Map<String, Object> map) {
        return activityMapper.selectActivitiesByConditionsForPage(map);
    }

    @Override
    public int queryCountOfActivitiesByConditions(Map<String, Object> map) {
        return activityMapper.selectCountOfActivitiesByConditions(map);
    }
}
