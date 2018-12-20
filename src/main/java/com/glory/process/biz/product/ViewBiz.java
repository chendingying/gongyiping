package com.glory.process.biz.product;


import com.glory.process.mapper.process.ViewMapper;
import com.glory.process.model.product.View;
import com.glory.process.util.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by CDZ on 2018/11/22.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ViewBiz extends BaseBiz<ViewMapper,View> {
    public View selectViewU9Conding(String code){
       return mapper.selectViewU9Conding(code);
    }

    public List<Map<String,Object>> saveProcessU9Conding(){
        return mapper.saveProcessU9Conding();
    }
}
