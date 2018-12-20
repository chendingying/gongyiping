package com.glory.process.controller.product;


import com.glory.process.biz.product.ProcessBiz;
import com.glory.process.biz.product.ProcessPictureBiz;
import com.glory.process.biz.product.UpLoadBiz;
import com.glory.process.model.product.ProcessPicture;
import com.glory.process.util.BaseController;
import com.glory.process.util.ObjectRestResponse;
import com.glory.process.util.Query;
import com.glory.process.util.TableResultResponse;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.glory.process.model.product.Process;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 工艺模块
 * Created by CDZ on 2018/11/15.
 */
@RestController
@RequestMapping("/api/product/process")
public class ProcessController extends BaseController<ProcessBiz,Process> {
    @Autowired
    ProcessBiz processBiz;

    @Autowired
    UpLoadBiz upLoadBiz;

    @Value("${ftp.fileName}")
    private String fileName;

    @Autowired
    ProcessPictureBiz processPictureBiz;


    /**
     * 工艺明细
     * @param id
     * @return
     */
    @RequestMapping(value = "/getProcess/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse getProcess(@PathVariable int id) throws IOException {
        Process process = baseBiz.selectById(id);
        return baseBiz.get(process);
    }

    /**
     * 查看历史版本
     * @param
     * @return
     */
    @GetMapping("/historyVersion/page")
    public TableResultResponse<Map<String,Object>> historyVersion(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        Process process = new Process();
        if(params.get("u9Coding") != null && !params.get("u9Coding").equals("")){
            process.setU9Coding(params.get("u9Coding").toString());
        }
        return baseBiz.historyVersion(query,process);
    }

    /**
     * 工艺信息查询（仅列出最新版本的列表）
     * @param params
     * @return
     */
    @GetMapping("/maxVersion/page")
    public TableResultResponse<Map<String,Object>> selectProcessForMaxVersion(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        Process process = new Process();
        if(params.get("customer") != null && !params.get("customer").equals("")){
            process.setCustomer(params.get("customer").toString());
        }if(params.get("u9Coding") != null && !params.get("u9Coding").equals("")){
            process.setU9Coding(params.get("u9Coding").toString());
        }if(params.get("status") != null && !params.get("status").equals("")){
            process.setStatus(Integer.valueOf(params.get("status").toString()));
        }
        if(params.get("version") != null && !params.get("version").equals("")){
            process.setVersion(params.get("version").toString());
        }
        return processBiz.selectProcessForMaxVersion(query,process);
    }

    @GetMapping("/historyProcess/page")
    public TableResultResponse<Map<String,Object>> selectProcessn(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        Process process = new Process();
        if(params.get("customer") != null && !params.get("customer").equals("")){
            process.setCustomer(params.get("customer").toString());
        }if(params.get("u9Coding") != null && !params.get("u9Coding").equals("")){
            process.setU9Coding(params.get("u9Coding").toString());
        }if(params.get("version") != null && !params.get("version").equals("")){
            process.setVersion(params.get("version").toString());
        }
        return processBiz.selectProcess(query,process);
    }

    /**
     * 工艺信息编码模糊查询
     * @param
     * @return
     */
    @ApiOperation("工艺信息编码模糊查询")
    @GetMapping("/u9Conding/{u9Coding}")
    public ObjectRestResponse selectProcessU9Conding(@PathVariable("u9Coding") String u9Coding){
       return processBiz.selectProcessU9Conding(u9Coding);
    }

    /**
     * 新增u9编码模糊查询
     * @return
     */
    @ApiOperation("新增u9编码模糊查询")
    @GetMapping("/saveU9Conding")
    public ObjectRestResponse saveProcessU9Conding(){
        return processBiz.saveProcessU9Conding();
    }

    /**
     * 手动新增工艺信息
     * @param process
     * @return
     */
    @ApiOperation("手动新增工艺信息")
    @PostMapping("/save")
    @Transactional
    @ResponseBody
    public ObjectRestResponse saveProcess(@RequestBody Process process){
        return baseBiz.saveProcess(process);
    }

    /**
     * 手动编辑工艺信息
     * @param process
     * @return
     */
    @ApiOperation("手动编辑工艺信息")
    @PutMapping("/update/{id}")
    @Transactional
    @ResponseBody
    public ObjectRestResponse updateProcess(@RequestBody Process process,@PathVariable Integer id){
        Boolean bool = baseBiz.updateProcess(process,id);
        if(!bool){
            process.setId(id);
            baseBiz.updateById(process);
        }
        return new ObjectRestResponse<Process>();
    }

    /**
     *
     *
     * 前端格式
     {
        "list":[
                    {
                        "id":"1",
                        "status":0
                    },
                   {
                        "id":"2",
                        "status":0
                    }
              ]

     }
     批量作废
     * @param
     * @return
     */
    @ApiOperation("批量作废")
    @PutMapping("/updateInvalid")
    @ResponseBody
    @Transactional
    public ObjectRestResponse updateInvalid(@RequestBody Map<String,List> map) {
        return processBiz.updateInvalid(map);
    }

    /**
     * 恢复上一个版本
     * @param id
     * @return
     */
    @ApiOperation("恢复上一个版本")
    @PutMapping("/updateRegain/{id}")
    @Transactional
    @ResponseBody
    public ObjectRestResponse updateRegain(@PathVariable("id") Integer id) {
        Process proces = baseBiz.selectById(id);
        return processBiz.updateRegain(proces);
    }

    /**
     * Excel 导入
     * @return
     */
    @ApiOperation("Excel 导入")
    @PostMapping("/excelInport")
    @ResponseBody
    @Transactional
    public ObjectRestResponse ExcelInport(MultipartFile file) throws IOException, InvalidFormatException {
        return processBiz.ExcelInport(file);
    }

    // ftp下载模板
    @ApiOperation("ftp下载模板")
    @RequestMapping(value="/ftpDownload", method = RequestMethod.GET)
    @Transactional
    public void Ftpdownload( HttpServletResponse response) throws IOException {
        String name = new String(fileName.getBytes("ISO8859-1"),"UTF-8");
        InputStream inputStream = upLoadBiz.Ftpdownload(name);
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/x-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" +  fileName );   // 设置文件名
        if(inputStream == null){
            return;
        }
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    }

    //ftp处理文件上传
    @ApiOperation("ftp处理文件上传")
    @RequestMapping(value="/ftpUploadImg/{id}/{type}", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody ProcessPicture uploadImg(@PathVariable("id") Integer id,@PathVariable("type") String type,MultipartFile file) throws IOException {
       Process process =  baseBiz.selectById(id);
        if(process == null){
            return null;
        }
       return baseBiz.upLoadImg(process,type,file);
//       return upLoadBiz.upLoadImg(file);
    }
    @ApiOperation("图片查看")
    @GetMapping("/photo/{id}/{type}/{version}")
    public void getCode(@PathVariable("id") String id,@PathVariable("type") String type,@PathVariable("version") String version, HttpServletResponse response, HttpServletRequest request) throws Exception{
        ProcessPicture processPicture = processPictureBiz.selectProcessPictureForProcessId(id,type,version,null);
        if(processPicture != null){
            ByteArrayInputStream in = new ByteArrayInputStream(processPicture.getText());    //将b作为输入流；
            BufferedImage image = ImageIO.read(in);
            response.setContentType("image/jpg");
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "jpg", os);
            in.close();
        }
    }

    @ApiOperation("图片删除")
    @DeleteMapping("/photo/{id}")
    public ObjectRestResponse deleteProcessPicture(@PathVariable("id") Integer id) throws UnsupportedEncodingException {
        ObjectRestResponse objectRestResponse = new ObjectRestResponse();
        Boolean b = processPictureBiz.delteByProcessPicture(id);
        objectRestResponse.setRel(b);
        return objectRestResponse;
    }
}
