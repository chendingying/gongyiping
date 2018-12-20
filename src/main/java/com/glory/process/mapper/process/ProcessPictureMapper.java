package com.glory.process.mapper.process;

import com.glory.process.model.product.ProcessPicture;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by CDZ on 2018/12/1.
 */
public interface ProcessPictureMapper  extends Mapper<ProcessPicture> {
    void insertProcessPictureList(List<ProcessPicture> list);
    List<ProcessPicture> selectProcessPictureList(@Param("processId") String processId);
    ProcessPicture selectProcessPictureForProcessId( @Param("processId") String processId,@Param("type") String type,@Param("version") String version,@Param("path") String processPictureName);
    void updateByProcessPicture(@Param("processPicture") ProcessPicture processPicture);
    void deleteProcessPicture(@Param("id") Integer id);
    ProcessPicture selectProcessPicture(@Param("id") Integer id);
    Integer insertProcessPicture(@Param("processPicture") ProcessPicture processPicture);
}
