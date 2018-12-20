package com.glory.process.vo;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 *  * @param host FTP服务器hostname
 * @param port FTP服务器端口
 * @param username FTP登录账号
 * @param password FTP登录密码
 * @param remotePath FTP服务器上的相对路径
 * @param fileName 要下载的文件名
 * @param localPath 下载后保存到本地的路径
 */

/**
 * Created by CDZ on 2018/11/20.
 */
@Component
@PropertySource({"classpath:ftp.properties"})
public class FtpDownLoad {

    Logger log = LoggerFactory.getLogger(FtpDownLoad.class);
//    /**
//     * Description: 向FTP服务器上传文件
//     * @param host FTP服务器hostname
//     * @param port FTP服务器端口
//     * @param username FTP登录账号
//     * @param password FTP登录密码
//     * @param basePath FTP服务器基础目录
//     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath
//     * @param filename 上传到FTP服务器上的文件名
//     * @param input 输入流
//     * @return 成功返回true，否则返回false
//     */

    @Value("${ftp.host}")
    private  String host;

    @Value("${ftp.port}")
    private  Integer port;

    @Value("${ftp.username}")
    private  String username;

    @Value("${ftp.password}")
    private  String password;

    @Value("${ftp.basePath}")
    private  String basePath;

    @Value("${ftp.filePath}")
    private  String filePath;

    @Value("${ftp.fileDownLoad}")
    private String fileDownLoad;

    private FTPClient ftpClient = null;

    /**
     * 关闭FTP服务器
     */
    public void closeServer(){
        try{
            if(ftpClient!=null){
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public  boolean uploadFile(MultipartFile file,String filePath) throws UnsupportedEncodingException {
        boolean result = false;
        filePath=new String(filePath.getBytes("UTF-8"),"ISO-8859-1");
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            ftp.enterLocalPassiveMode(); //在这里加上这行代码。重要,要不在listfiles时候会卡住不动,同时不会报错
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(basePath+filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                String tempPath = basePath;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {
                        if (!ftp.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }

            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
            String filename=new String(file.getOriginalFilename().getBytes("UTF-8"),"ISO-8859-1");
            if (!ftp.storeFile(filename, file.getInputStream())) {
                return result;
            }
            file.getInputStream().close();
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

        return result;
    }

    public boolean deleteFile(String filePath,String fileName) throws UnsupportedEncodingException {
        filePath=new String(filePath.getBytes("UTF-8"),"ISO-8859-1");
        fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
        boolean flag = false;
        FTPClient ftp = new FTPClient();
        try{
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("failed to connect to the FTP Server:");
            }
            String path = basePath+filePath;
            ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
            ftp.dele(fileName);
            ftp.logout();
            flag = true;
            System.out.println("删除文件成功");
        }catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        } finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public InputStream  downloadFile(String filename)
            throws IOException {
        InputStream in=null;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("failed to connect to the FTP Server:");
            }
            ftp.setControlEncoding("utf-8");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(fileDownLoad);// 转移到FTP服务器目录
            ftp.enterLocalPassiveMode(); //在这里加上这行代码。重要,要不在listfiles时候会卡住不动,同时不会报错
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String localFileName=new String(ff.getName().getBytes("UTF-8"),"UTF-8");
                if (localFileName.equals(filename)) {
                    in = ftp.retrieveFileStream(new String(ff.getName().getBytes("UTF-8"),"ISO-8859-1"));
                }
            }
        } catch (FTPConnectionClosedException e) {

            log.error("ftp连接被关闭！", e);
            throw e;
        } catch (Exception e) {
            log.error("ERR : upload file "+ filename+ " from ftp : failed!", e);
        }
        return in;
    }

    public InputStream  downloadFile(String path,String filename)
            throws IOException {
        InputStream in=null;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("failed to connect to the FTP Server:");
            }
            ftp.setControlEncoding("utf-8");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
            ftp.enterLocalPassiveMode(); //在这里加上这行代码。重要,要不在listfiles时候会卡住不动,同时不会报错
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String localFileName=new String(ff.getName().getBytes("UTF-8"),"UTF-8");
                if (localFileName.equals(filename)) {
                    in = ftp.retrieveFileStream(new String(ff.getName().getBytes("UTF-8"),"ISO-8859-1"));
                }
            }
        } catch (FTPConnectionClosedException e) {

            log.error("ftp连接被关闭！", e);
            throw e;
        } catch (Exception e) {
            log.error("ERR : upload file "+ filename+ " from ftp : failed!", e);
        }
        return in;
    }



    public static void main(String[] args) {
        try {
//            FileInputStream in=new FileInputStream(new File("D:\\马.jpg"));
//            boolean flag = uploadFile("马.jpg",in);
//            System.out.println(flag);
//            downloadFile("D:\\");
//            FtpDownLoad ftpDownLoad = new FtpDownLoad();
//            InputStream inputStream = ftpDownLoad.downloadFile("/D0/1.0/","马.jpg");
//            BufferedImage bi = ImageIO.read(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
