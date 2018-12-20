package com.glory.process.biz;

import com.glory.process.mapper.admin.ResourceAuthorityMapper;
import com.glory.process.model.ResourceAuthority;
import com.glory.process.util.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ace on 2017/6/19.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceAuthorityBiz extends BaseBiz<ResourceAuthorityMapper, ResourceAuthority> {
}
