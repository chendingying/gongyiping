package com.glory.process.biz;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.glory.process.constants.CommonConstants;
import com.glory.process.jwt.UserAuthUtil;
import com.glory.process.mapper.admin.MenuMapper;
import com.glory.process.mapper.admin.UserMapper;
import com.glory.process.model.User;
import com.glory.process.util.BaseBiz;
import com.glory.process.util.Query;
import com.glory.process.util.TableResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by CDZ on 2018/11/27.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserBiz extends BaseBiz<UserMapper,User> {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private UserAuthUtil userAuthUtil;
    @Override
    public void insertSelective(User entity) {
        String password = new BCryptPasswordEncoder(CommonConstants .PW_ENCORDER_SALT).encode(entity.getPassword());
        entity.setPassword(password);
        super.insertSelective(entity);
    }

    @Override
//    @CacheClear(pre="user{1.username}")
    public void updateSelectiveById(User entity) {
        super.updateSelectiveById(entity);
    }

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
//    @Cache(key="user{1}")
    public User getUserByUsername(String username){
        User user = new User();
        user.setUsername(username);
        return mapper.selectOne(user);
    }

    public TableResultResponse<Map<String,Object>> selectUser(Query query, User user){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>> list  = mapper.selectUser(user.getUsername(),user.getName(),user.getTelPhone(),user.getStatus());
        return new TableResultResponse<Map<String,Object>>(result.getTotal(), list);
    }
}
