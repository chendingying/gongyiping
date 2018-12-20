package com.glory.process.mapper.admin;



import com.glory.process.model.Group;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface GroupMapper extends Mapper<Group> {
    void deleteGroupMembersById(@Param("groupId") int groupId);
    void deleteGroupLeadersById(@Param("groupId") int groupId);
    void insertGroupMembersById(@Param("groupId") int groupId, @Param("userId") int userId);
    void insertGroupLeadersById(@Param("groupId") int groupId, @Param("userId") int userId);
    void deleteGroupLeaders(@Param("groupId")int groupId,@Param("list") List<Map<String, Integer>> list);
    Integer selectGroupLeadersByuserIdAndGroupId(@Param("groupId") Integer groupId,@Param("userId") Integer userId);
}