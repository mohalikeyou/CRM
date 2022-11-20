package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.mapper.ActivityRemarkMapper;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("activityRemarkServiceImpl")
public class ActivityRemarkServiceImpl implements ActivityRemarkService {

    @Autowired
    private ActivityRemarkMapper activityRemarkMapper;
    @Override
    public List<ActivityRemark> queryActivityRemarkByIdForDetail(String id) {
        return activityRemarkMapper.selectActivityRemarkByIdForDetail(id);
    }

    @Override
    public int saveActivityRemarkByActivityId(ActivityRemark activityRemark) {
        return activityRemarkMapper.insertActivityRemarkByActivityId(activityRemark);
    }

    @Override
    public int deleteActivityRemarkById(String id) {
        return activityRemarkMapper.deleteActivityRemarkById(id);
    }

    @Override
    public int updateActivityRemarkById(ActivityRemark activityRemark) {
        return activityRemarkMapper.updateActivityRemarkById(activityRemark);
    }
}
