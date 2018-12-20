package com.glory.process.biz.product;

import com.glory.process.mapper.process.ProcessPictureMapper;
import com.glory.process.model.product.ProcessPicture;
import com.glory.process.util.BaseBiz;
import com.glory.process.vo.FtpDownLoad;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 工艺图片管理
 * Created by CDZ on 2018/12/1.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessPictureBiz extends BaseBiz<ProcessPictureMapper,ProcessPicture> {

    @Autowired
    FtpDownLoad ftpDownLoad;

    public void insertProcessPictureList(List<ProcessPicture> processPictureList){
        mapper.insertProcessPictureList(processPictureList);
    }

    public ProcessPicture selectProcessPictureForProcessId(String processId,String type,String version,String processPictureName){
        return mapper.selectProcessPictureForProcessId(processId,type,version,processPictureName);
    }
    public List<ProcessPicture> selectProcessPictureList(String processId){
        return mapper.selectProcessPictureList(processId);
    }
    public void updateByProcessPicture(ProcessPicture processPicture){
       mapper.updateByProcessPicture(processPicture);
    }

    /**
     * 删除图片，同步删除ftp上的图片
     * @param id
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean delteByProcessPicture(int id) throws UnsupportedEncodingException {
        ProcessPicture processPicture = mapper.selectProcessPicture(id);
        if(processPicture == null){
            return false;
        }
        String path =  processPicture.getPath();
        if(path == null){
            return false;
        }
        //获取图片Path
        String filePath = path.substring(0,path.lastIndexOf("/"));
        //获取图片Name
        String fileName = path.substring(path.lastIndexOf("/")+1);
        //如果远程图片删除成功则删除数据库中的图片
        if(ftpDownLoad.deleteFile(filePath,fileName)){
            mapper.deleteProcessPicture(id);
        }else{
            return false;
        }
        return true;

    }

    public Integer insertProcessPicture(ProcessPicture processPicture){
       return mapper.insertProcessPicture(processPicture);
    }
}
