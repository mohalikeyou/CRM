package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.Activity;

public interface ActivityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Tue Nov 08 22:30:32 CST 2022
     */
    int deleteByPrimaryKey(String id);



    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Tue Nov 08 22:30:32 CST 2022
     */
    int insertSelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Tue Nov 08 22:30:32 CST 2022
     */
    Activity selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Tue Nov 08 22:30:32 CST 2022
     */
    int updateByPrimaryKeySelective(Activity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_activity
     *
     * @mbggenerated Tue Nov 08 22:30:32 CST 2022
     */
    int updateByPrimaryKey(Activity record);

    /**
     * 新建一个活动
     * @yao
     */
    int insertActivity(Activity activity);
}