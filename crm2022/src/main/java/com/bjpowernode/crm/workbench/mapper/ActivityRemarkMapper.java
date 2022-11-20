package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Sat Nov 19 19:28:47 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Sat Nov 19 19:28:47 CST 2022
     */
    int insert(ActivityRemark record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Sat Nov 19 19:28:47 CST 2022
     */
    int insertSelective(ActivityRemark record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Sat Nov 19 19:28:47 CST 2022
     */
    ActivityRemark selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Sat Nov 19 19:28:47 CST 2022
     */
    int updateByPrimaryKeySelective(ActivityRemark record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity_remark
     *
     * @mbggenerated Sat Nov 19 19:28:47 CST 2022
     */
    int updateByPrimaryKey(ActivityRemark record);

    /**
     * 根据市场活动id查询市场活动的详情
     * @author yao
     */
    List<ActivityRemark> selectActivityRemarkByIdForDetail(String id);

    /**
     * 把remark保存到对应的市场活动下
     * @author yao
     */
    int insertActivityRemarkByActivityId(ActivityRemark activityRemark);

    /**
     * 根据市场活动备注ID删除备注
     * @author yao
     */
    int deleteActivityRemarkById(String id);

    /**
     * 根据活动备注ID更新备注
     * @author yao
     */
    int updateActivityRemarkById(ActivityRemark activityRemark);
}