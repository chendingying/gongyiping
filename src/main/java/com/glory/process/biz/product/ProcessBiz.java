package com.glory.process.biz.product;


import com.github.pagehelper.Page;
import com.glory.process.exception.product.ProcessInvalidException;
import com.glory.process.model.product.Process;
import com.github.pagehelper.PageHelper;
import com.glory.process.mapper.process.ProcessMapper;
import com.glory.process.model.product.ProcessPicture;
import com.glory.process.model.product.View;
import com.glory.process.util.*;
import com.glory.process.vo.ExcelTool;
import com.glory.process.vo.FtpDownLoad;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by CDZ on 2018/11/15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessBiz extends BaseBiz<ProcessMapper,Process> {

    @Autowired
    protected ViewBiz viewBiz;

    @Autowired
    FtpDownLoad ftpDownLoad;

    @Autowired
    ProcessPictureBiz processPictureBiz;

    public ObjectRestResponse<Process> get(Process process) throws IOException {
        ObjectRestResponse<Process> entityObjectRestResponse = new ObjectRestResponse<>();
        List<ProcessPicture> processPictures = processPictureBiz.selectProcessPictureList(process.getId().toString());
        View view = viewBiz.selectViewU9Conding(process.getU9Coding());
        if(view != null){
            process.setBoxNumber(view.getValue3_53());
            process.setCaseNumber(view.getValue3_52());
            process.setBedCharge(view.getValue2_30());
            process.setChildThingNumber(view.getValue_else());
        }
        for(ProcessPicture processPicture : processPictures){
            if(processPicture.getType().equals("1")){
                process.setProcess1PictureName(processPicture.getPath());
            }if(processPicture.getType().equals("2")){
                process.setProcess2PictureName(processPicture.getPath());
            }if(processPicture.getType().equals("3")){
                process.setProcess3PictureName(processPicture.getPath());
            }if(processPicture.getType().equals("4")){
                process.setProcess4PictureName(processPicture.getPath());
            }
        }
        entityObjectRestResponse.data((Process)process);
        return entityObjectRestResponse;
    }

    public TableResultResponse<Map<String,Object>> historyVersion(Query query, Process process){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>> list  =mapper.historyVersion(process.getU9Coding());
        return new TableResultResponse<Map<String,Object>>(result.getTotal(), list);
    }

    public TableResultResponse<Map<String,Object>> selectProcessForMaxVersion(Query query,Process process){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>> list  = mapper.selectProcessForMaxVersion(process.getU9Coding(),process.getCustomer(),process.getStatus(),process.getVersion());
        return new TableResultResponse<Map<String,Object>>(result.getTotal(), list);
    }

    public TableResultResponse<Map<String,Object>> selectProcess(Query query,Process process){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Map<String,Object>> list  = mapper.selectProcess(process.getU9Coding(),process.getCustomer(),process.getVersion());
        return new TableResultResponse<Map<String,Object>>(result.getTotal(), list);
    }

    public ObjectRestResponse selectProcessU9Conding(String u9Coding){
        ObjectRestResponse<Map<String,Object>> entityObjectRestResponse = new ObjectRestResponse<>();
        List<Map<String,Object>> list = mapper.selectProcessU9Conding(u9Coding);
        Map<String,Object> map = new HashedMap();
        map.put("dataList",list);
        return  entityObjectRestResponse.data(map);
    }

    public ObjectRestResponse saveProcessU9Conding(){
        ObjectRestResponse<Map<String,Object>> entityObjectRestResponse = new ObjectRestResponse<>();
        List<Map<String,Object>> list = viewBiz.saveProcessU9Conding();
        Map<String,Object> map = new HashedMap();
        map.put("dataList",list);
        return  entityObjectRestResponse.data(map);
    }

    public Boolean updateProcess(Process process,Integer id){
        String version = mapper.selectMaxVersionForU9Coding(process.getU9Coding(),null);
        if(version == null || version.equals("")){
            version = "1.0";
        }
        if(process.getVersion() == null || process.getVersion().equals("") ) {
            process.setVersion("1.0");
        }
        Integer compare = VersionUtil.compareVersion(version,process.getVersion());
        if(compare == 1) {
            throw new ProcessInvalidException("产品编码：'"+ process.getU9Coding() +"' 的最新版本不能小于当前版本,当前最高版本为："+ version);
        }if(compare == -1){
            List<Map<String,Object>> list = mapper.selectProcessForMaxVersion(process.getU9Coding(),null,1,null);
            for(Map map : list){
                mapper.updateInvalidVersion(Integer.valueOf(map.get("id").toString()),map.get("u9Coding").toString());
            }
            mapper.insertProcess(process);
            return true;
        }
        return false;
    }
    public ObjectRestResponse saveProcess(Process process){
        String version = mapper.selectMaxVersionForU9Coding(process.getU9Coding(),null);
        if(version == null || version.equals("")){
            process.setVersion("1.0");
            this.insertSelective(process);
            return new ObjectRestResponse<Process>();
        }
        //判断版本
        compareVersion(process);
        //查询出最新的信息
        Process TopProcess =  mapper.selectTopProcess(process.getU9Coding());
        //修改最新版本状态
        mapper.updateInvalidVersion(TopProcess.getId(),TopProcess.getU9Coding());
        EntityUtils.setCreateInfo(process);
        EntityUtils.setUpdatedInfo(process);
        mapper.insertProcess(process);
        List<ProcessPicture> processPictures = processPictureBiz.selectProcessPictureList(TopProcess.getId().toString());
        for(ProcessPicture processPicture : processPictures){
            processPicture.setProcessId(process.getId());
            processPicture.setVersion(process.getVersion());
            processPictureBiz.insertProcessPicture(processPicture);
            if(processPicture.getType().equals("1")){
                process.setProcess1PictureId(processPicture.getId().toString());
            }if(processPicture.getType().equals("2")){
                process.setProcess2PictureId(processPicture.getId().toString());
            }if(processPicture.getType().equals("3")){
                process.setProcess3PictureId(processPicture.getId().toString());
            }if(processPicture.getType().equals("4")){
                process.setProcess4PictureId(processPicture.getId().toString());
            }
        }
        mapper.updateByPrimaryKeySelective(process);
        return new ObjectRestResponse<Process>();
    }

    public ObjectRestResponse updateInvalid(Map<String,List> map) {
        mapper.updateInvalid(map.get("list"));
        return new ObjectRestResponse<Process>();
    }

    public ObjectRestResponse updateRegain(Process process){
        String version = mapper.selectMaxVersionForU9Coding(process.getU9Coding(),process.getId());
        if(version == null || version.equals("")) {
            throw new ProcessInvalidException("当前是最新版本");
        }
        mapper.updateRegainVersion(process.getU9Coding(),version);
        mapper.updateInvalidVersion(process.getId(),process.getU9Coding());
        return new ObjectRestResponse<Process>();
    }



    public ObjectRestResponse ExcelInport(MultipartFile file) throws IOException, InvalidFormatException {
        List<String> strings = new ArrayList<>();
        ObjectRestResponse  objectRestResponse = new ObjectRestResponse<Process>();
        JSONArray jsonArray =  ExcelTool.readExcel(file);
        List<Process> processList = new ArrayList<>();
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject object = jsonArray.getJSONObject(i);
            View view = viewBiz.selectViewU9Conding(object.get("u9Coding").toString());
            if(view == null){
                strings.add(object.get("u9Coding").toString()+"编码不存在，非法数据，添加失败");
                continue;
            }
            Process process = (Process) JSONObject.toBean(object,Process.class);
            List<Map<String,Object>> mapList = mapper.selectProcess(process.getU9Coding(),null,process.getVersion());
            if(mapList.size() == 1){
                List<Process> updateProcessList = new ArrayList<>();
                process.setId(Integer.valueOf(mapList.get(0).get("id").toString()));
                mapper.updateProcess(process);
                updateProcessList.add(process);
                this.insertProcessPicture(updateProcessList);
                strings.add(object.get("u9Coding").toString()+"编码存在，修改成功");
                continue;
            }
            processList.add(process);
            List<Map<String,Object>> list = mapper.selectProcessForMaxVersion(process.getU9Coding(),null,1,null);
            for(Map map : list){
                mapper.updateInvalidVersion(Integer.valueOf(map.get("id").toString()),map.get("u9Coding").toString());
            }
            strings.add(object.get("u9Coding").toString()+"编码存在，添加成功");

        }
        for(Process process : processList){
            EntityUtils.setCreatAndUpdatInfo(process);
            mapper.insertProcess(process);
        }
        this.insertProcessPicture(processList);
        objectRestResponse.setData(strings);
        ftpDownLoad.closeServer();
        return objectRestResponse;
    }

    public void insertProcessPicture(List<Process> processList) throws IOException {
        for(Process process : processList){
            if(process.getProcess1PictureName() != null && !process.getProcess1PictureName().equals("")){
                ProcessPicture processPicture = new ProcessPicture();
                processPicture.setVersion(process.getVersion());
                processPicture.setProcessId(process.getId());
                processPicture.setType("1");
                String[] dirs = process.getProcess1PictureName().split("/");
                String tempPath ="";
                String fileName = dirs[dirs.length -1];
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    if(dir.equals(fileName)) continue;
                    tempPath += "/" + dir;
                }
                InputStream inputStream = ftpDownLoad.downloadFile(tempPath,fileName);
                byte[] byt = input2byte(inputStream);
                processPicture.setPath(process.getProcess1PictureName());
                processPicture.setText(byt);
                ProcessPicture processPictureId = processPictureBiz.selectProcessPictureForProcessId(process.getId().toString(),"1",process.getVersion(),null);
                if(processPictureId == null){
                    List<ProcessPicture> processPictures = new ArrayList<>();
                    processPictures.add(processPicture);
                    processPictureBiz.insertProcessPictureList(processPictures);
                    process.setProcess1PictureId(processPicture.getId().toString());
                }else{
                    processPicture.setId(processPictureId.getId());
                    processPictureBiz.updateByProcessPicture(processPicture);
                }
                inputStream.close();
            }if(process.getProcess2PictureName() != null && !process.getProcess2PictureName().equals("")){
                ProcessPicture processPicture = new ProcessPicture();
                processPicture.setVersion(process.getVersion());
                processPicture.setProcessId(process.getId());
                processPicture.setType("2");
                String[] dirs = process.getProcess2PictureName().split("/");
                String tempPath ="";
                String fileName = dirs[dirs.length -1];
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    if(dir.equals(fileName)) continue;
                    tempPath += "/" + dir;
                }
                InputStream inputStream = ftpDownLoad.downloadFile(tempPath,fileName);
                byte[] byt = input2byte(inputStream);
                processPicture.setPath(process.getProcess2PictureName());
                processPicture.setText(byt);
                ProcessPicture processPictureId = processPictureBiz.selectProcessPictureForProcessId(process.getId().toString(),"2",process.getVersion(),null);
                if(processPictureId == null){
                    List<ProcessPicture> processPictures = new ArrayList<>();
                    processPictures.add(processPicture);
                    processPictureBiz.insertProcessPictureList(processPictures);
                    process.setProcess2PictureId(processPicture.getId().toString());
                }else{
                    processPicture.setId(processPictureId.getId());
                    processPictureBiz.updateByProcessPicture(processPicture);
                }
                inputStream.close();
            }if(process.getProcess3PictureName() != null && !process.getProcess3PictureName().equals("")){
                ProcessPicture processPicture = new ProcessPicture();
                processPicture.setVersion(process.getVersion());
                processPicture.setProcessId(process.getId());
                processPicture.setType("3");
                String[] dirs = process.getProcess3PictureName().split("/");
                String tempPath ="";
                String fileName = dirs[dirs.length -1];
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    if(dir.equals(fileName)) continue;
                    tempPath += "/" + dir;
                }
                InputStream inputStream = ftpDownLoad.downloadFile(tempPath,fileName);
                byte[] byt = input2byte(inputStream);
                processPicture.setPath(process.getProcess3PictureName());
                processPicture.setText(byt);
                ProcessPicture processPictureId = processPictureBiz.selectProcessPictureForProcessId(process.getId().toString(),"3",process.getVersion(),null);
                if(processPictureId == null){
                    List<ProcessPicture> processPictures = new ArrayList<>();
                    processPictures.add(processPicture);
                    processPictureBiz.insertProcessPictureList(processPictures);
                    process.setProcess3PictureId(processPicture.getId().toString());
                }else{
                    processPicture.setId(processPictureId.getId());
                    processPictureBiz.updateByProcessPicture(processPicture);
                }
                inputStream.close();
            }if(process.getProcess4PictureName() != null && !process.getProcess4PictureName().equals("")){
                ProcessPicture processPicture = new ProcessPicture();
                processPicture.setVersion(process.getVersion());
                processPicture.setProcessId(process.getId());
                processPicture.setType("4");
                String[] dirs = process.getProcess4PictureName().split("/");
                String tempPath ="";
                String fileName = dirs[dirs.length -1];
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    if(dir.equals(fileName)) continue;
                    tempPath += "/" + dir;
                }
                InputStream inputStream = ftpDownLoad.downloadFile(tempPath,fileName);
                byte[] byt = input2byte(inputStream);
                processPicture.setPath(process.getProcess4PictureName());
                processPicture.setText(byt);
                ProcessPicture processPictureId = processPictureBiz.selectProcessPictureForProcessId(process.getId().toString(),"4",process.getVersion(),null);
                if(processPictureId == null){
                    List<ProcessPicture> processPictures = new ArrayList<>();
                    processPictures.add(processPicture);
                    processPictureBiz.insertProcessPictureList(processPictures);
                    process.setProcess4PictureId(processPicture.getId().toString());
                }else{
                    processPicture.setId(processPictureId.getId());
                    processPictureBiz.updateByProcessPicture(processPicture);
                }
                inputStream.close();
            }
            mapper.updateByPrimaryKeySelective(process);
        }
    }
    // 版本判断
    public void compareVersion(Process process){
        String version = mapper.selectMaxVersionForU9Coding(process.getU9Coding(),null);
        if(version == null || version.equals("")){
            version = "1.0";
        }
        if(process.getVersion() == null || process.getVersion().equals("") ) {
            process.setVersion("1.0");
        }
        Integer compare = VersionUtil.compareVersion(version,process.getVersion());
        if(compare != -1) {
            throw new ProcessInvalidException("产品编码：'"+ process.getU9Coding() +"' 的最新版本不能小于或等于当前版本,当前最高版本为："+ version);
        }
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public ProcessPicture upLoadImg(Process process,String type,MultipartFile file) throws IOException {
        String flieName = file.getOriginalFilename();
        if (!flieName.endsWith(".jpg") ) {
            if(!flieName.endsWith(".png")){
                throw new ProcessInvalidException("上传的文件必须是.jpg或.png格式");
            }
        } if (file.getSize() >= 10*1024*1024)
        {
            throw new ProcessInvalidException("文件不能大于10M");
        }
        String u9Coding = process.getU9Coding().substring(0,2) + "/";
        u9Coding += process.getProductModel()+"/"+process.getCustomer()+"/"+process.getVersion() + "/";
        ProcessPicture processPictureId = processPictureBiz.selectProcessPictureForProcessId(process.getId().toString(),type,process.getVersion(),null);
        boolean blog = ftpDownLoad.uploadFile(file,u9Coding);
        if(blog){
            String filePath =  "/"+u9Coding + file.getOriginalFilename();
            ProcessPicture processPicture = new ProcessPicture();
            processPicture.setPath(filePath);
            processPicture.setVersion(process.getVersion());
            processPicture.setProcessId(process.getId());
            processPicture.setType(type);
            processPicture.setText(file.getBytes());
            List<ProcessPicture> processPictures = new ArrayList<>();
            if(processPictureId != null){
                processPicture.setId(processPictureId.getId());
                processPictureBiz.updateByProcessPicture(processPicture);
                return processPicture;
            }
            processPictures.add(processPicture);
            processPictureBiz.insertProcessPictureList(processPictures);
            if(type.equals("1")){
                process.setProcess1PictureId(processPicture.getId().toString());
            }if(type.equals("2")){
                process.setProcess2PictureId(processPicture.getId().toString());
            }if(type.equals("3")){
                process.setProcess3PictureId(processPicture.getId().toString());
            }if(type.equals("4")){
                process.setProcess4PictureId(processPicture.getId().toString());
            }
            mapper.updateByPrimaryKeySelective(process);

            return processPicture;
        }
        return null;
    }
}
