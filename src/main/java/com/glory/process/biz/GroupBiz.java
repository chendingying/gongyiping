package com.glory.process.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.glory.process.constants.CommonConstants;
import com.glory.process.mapper.admin.GroupMapper;
import com.glory.process.mapper.admin.MenuMapper;
import com.glory.process.mapper.admin.ResourceAuthorityMapper;
import com.glory.process.mapper.admin.UserMapper;
import com.glory.process.model.Group;
import com.glory.process.model.Menu;
import com.glory.process.model.ResourceAuthority;
import com.glory.process.model.User;
import com.glory.process.util.BaseBiz;
import com.glory.process.util.Query;
import com.glory.process.util.TableResultResponse;
import com.glory.process.vo.AuthorityMenuTree;
import com.glory.process.vo.GroupUsers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author cdy
 * @create 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupBiz extends BaseBiz<GroupMapper, Group> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ResourceAuthorityMapper resourceAuthorityMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public void insertSelective(Group entity) {
        if (CommonConstants.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Group parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.insertSelective(entity);
    }

    @Override
    public void updateById(Group entity) {
        if (CommonConstants.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Group parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.updateById(entity);
    }

    /**
     * 获取群组关联用户
     *
     * @param groupId
     * @return
     */
    public TableResultResponse<User> getGroupUsers(int groupId,Query query) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<User> list =  userMapper.selectLeaderByGroupId(groupId);
        return new TableResultResponse<User>(result.getTotal(), list);
    }

    /**
     * 变更群主所分配用户
     *
     * @param groupId
     * @param members
     * @param leaders
     */
//    @CacheClear(pre = "permission")
    public void modifyGroupUsers(int groupId, String members, String leaders) {
        mapper.deleteGroupLeadersById(groupId);
        mapper.deleteGroupMembersById(groupId);
        if (!StringUtils.isEmpty(members)) {
            String[] mem = members.split(",");
            for (String m : mem) {
                mapper.insertGroupMembersById(groupId, Integer.parseInt(m));
            }
        }
        if (!StringUtils.isEmpty(leaders)) {
            String[] mem = leaders.split(",");
            for (String m : mem) {
                mapper.insertGroupLeadersById(groupId, Integer.parseInt(m));
            }
        }
    }

    public void deleteGroupUsers(int groupId, Map<String,List> map) {
        mapper.deleteGroupLeaders(groupId,map.get("list"));
    }

    public void addGroupUsers(int groupId, String leaders) {
        if (!StringUtils.isEmpty(leaders)) {
            String[] mem = leaders.split(",");
            for (String m : mem) {
                Integer id = mapper.selectGroupLeadersByuserIdAndGroupId(groupId,Integer.parseInt(m));
                if(id == null){
                    mapper.insertGroupLeadersById(groupId,Integer.parseInt(m) );
                }
            }
        }
    }

    /**
     * 变更群组关联的菜单
     *
     * @param groupId
     * @param menus
     */
    public void modifyAuthorityMenu(int groupId, String[] menus) {
        resourceAuthorityMapper.deleteByAuthorityIdAndResourceType(groupId + "");
//        List<Menu> menuList = menuMapper.selectAll();
//        Map<String, String> map = new HashMap<String, String>();
//        for (Menu menu : menuList) {
//            map.put(menu.getId().toString(), menu.getParentId().toString());
//        }
//        Set<String> relationMenus = new HashSet<String>();
//        relationMenus.addAll(Arrays.asList(menus));
//
//        for (String menuId : menus) {
//            findParentID(map, relationMenus, menuId);
//        }
        ResourceAuthority authority = null;
        for (String menuId : menus) {
            Menu menu = menuMapper.selectMenuId(Integer.valueOf(menuId));
            if(menu != null && menu.getType() != null && menu.getType().trim().equals(CommonConstants.RESOURCE_TYPE_BTN)){
                authority = new ResourceAuthority(CommonConstants.AUTHORITY_TYPE_GROUP, CommonConstants.RESOURCE_TYPE_BTN);
            }else{
                authority = new ResourceAuthority(CommonConstants.AUTHORITY_TYPE_GROUP, CommonConstants.RESOURCE_TYPE_MENU);
            }
            authority.setAuthorityId(groupId + "");
            authority.setResourceId(menuId);
            authority.setParentId("-1");
            resourceAuthorityMapper.insertSelective(authority);
        }
    }

    private void findParentID(Map<String, String> map, Set<String> relationMenus, String id) {
        String parentId = map.get(id);
        if (String.valueOf(CommonConstants.ROOT).equals(id)) {
            return;
        }
        relationMenus.add(parentId);
        findParentID(map, relationMenus, parentId);
    }

    /**
     * 分配资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
//    @CacheClear(keys = {"permission:ele","permission:u"})
    public void modifyAuthorityElement(int groupId, int menuId, int elementId) {
        ResourceAuthority authority = new ResourceAuthority(CommonConstants.AUTHORITY_TYPE_GROUP, CommonConstants.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        resourceAuthorityMapper.insertSelective(authority);
    }

    /**
     * 移除资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
//    @CacheClear(keys = {"permission:ele","permission:u"})
    public void removeAuthorityElement(int groupId, int menuId, int elementId) {
        ResourceAuthority authority = new ResourceAuthority();
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        resourceAuthorityMapper.delete(authority);
    }


    /**
     * 获取群主关联的菜单
     *
     * @param groupId
     * @return
     */
    public List<AuthorityMenuTree> getAuthorityMenu(int groupId) {
        List<Menu> menus = menuMapper.selectMenuByAuthorityId(String.valueOf(groupId), CommonConstants.AUTHORITY_TYPE_GROUP);
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Menu menu : menus) {
            node = new AuthorityMenuTree();
            node.setText(menu.getTitle());
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return trees;
    }

    /**
     * 获取群组关联的资源
     *
     * @param groupId
     * @return
     */
    public List<Integer> getAuthorityElement(int groupId) {
        ResourceAuthority authority = new ResourceAuthority(CommonConstants.AUTHORITY_TYPE_GROUP, CommonConstants.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId + "");
        List<ResourceAuthority> authorities = resourceAuthorityMapper.select(authority);
        List<Integer> ids = new ArrayList<Integer>();
        for (ResourceAuthority auth : authorities) {
            ids.add(Integer.parseInt(auth.getResourceId()));
        }
        return ids;
    }
}
