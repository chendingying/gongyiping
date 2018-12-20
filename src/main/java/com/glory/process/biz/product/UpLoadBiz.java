package com.glory.process.biz.product;


import com.glory.process.vo.FtpDownLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by CDZ on 2018/11/19.
 */
@Service
public class UpLoadBiz {

    @Autowired
    FtpDownLoad ftpDownLoad;

    public boolean upLoadImg(MultipartFile file) throws UnsupportedEncodingException {
        return ftpDownLoad.uploadFile(file,null);
    }

    public  InputStream  Ftpdownload(String fileName) throws IOException {
        return ftpDownLoad.downloadFile(fileName);
    }
}
