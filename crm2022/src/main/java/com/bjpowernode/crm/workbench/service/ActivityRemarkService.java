package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkService {

    List<ActivityRemark> queryActivityRemarkByIdForDetail(String id);

    int saveActivityRemarkByActivityId(ActivityRemark activityRemark);

    int deleteActivityRemarkById(String id);
}
