package com.glory.process.mapper.process;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import com.glory.process.model.product.Process;

import java.util.List;
import java.util.Map;

/**
 * Created by CDZ on 2018/11/15.
 */
public interface ProcessMapper extends Mapper<Process> {
    List<Map<String,Object>> selectProcessForMaxVersion(@Param("u9Coding") String u9Coding, @Param("customer") String customer, @Param("status") Integer status, @Param("version") String versopm);
    List<Map<String,Object>> selectProcess(@Param("u9Coding") String u9Coding, @Param("customer") String customer, @Param("version") String version);
    List<Map<String,Object>> selectProcessU9Conding(@Param("u9Coding") String u9Coding);
    String selectMaxVersionForU9Coding(@Param("u9Coding") String u9Coding, @Param("id") Integer id);
    void updateInvalid(@Param("list") List<Map<String, Integer>> list);
    void updateRegainVersion(@Param("u9Coding") String u9Coding, @Param("version") String version);
    void updateInvalidVersion(@Param("id") Integer id, @Param("u9Coding") String u9Coding);
    int insertProcessList(List<Process> list);
    Integer insertProcess(@Param("process") Process process);
    List<Map<String,Object>> historyVersion(@Param("u9Coding") String u9Coding);
    void updateProcess(@Param("process") Process process);
    Process selectTopProcess(@Param("u9Coding") String u9Coding);
}
