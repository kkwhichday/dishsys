package com.macro.mall.controller;

import com.macro.mall.config.FtpOperation;
import com.macro.mall.dto.CommonResult;
import com.macro.mall.dto.OssPolicyResult;
import com.macro.mall.dto.UploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

//@Controller
/**
 * 利用ftp上传所有资源文件，比如商品图片,防止oss不能用，ftp可以在ecs上搭建使用
 */
public class FileUploadController {
    @Autowired
    FtpOperation ftpOperation;
    @RequestMapping(value="/upload" , method = RequestMethod.POST)
    @ResponseBody
    public CommonResult  fileUpload(HttpServletRequest request) throws IOException {

        //解决MultipartFile序列化报错问题,采用request获取multifile的方式
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = files.get(0);
        //用来检测程序运行时间
        long  startTime=System.currentTimeMillis();
        System.out.println("fileName："+file.getOriginalFilename());

        UploadResult result = new UploadResult();
        try {
              //获取输入流 CommonsMultipartFile 中可以直接得到文件的流
            InputStream is=file.getInputStream();
            String filename = UUID.randomUUID().toString();
            result.setDir("mypic");
            result.setHost("localhost:8080");
            result.setFilename(filename);
            ftpOperation.uploadToFtp(is,result.getDir(),filename,false);
            is.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long  endTime=System.currentTimeMillis();
        System.out.println("方法一的运行时间："+String.valueOf(endTime-startTime)+"ms");


        return new CommonResult().success(result);
    }


    @RequestMapping(value="/download/{path}/{file}")
    @ResponseBody
    public CommonResult download(HttpServletResponse response, @PathVariable String path, @PathVariable String file){


        System.out.println("path="+path+" file="+file);
        try {
            InputStream ism =ftpOperation.downloadFile(path,file);

            int a;
            while((a=ism.read())!=-1){
                response.getOutputStream().write(a);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CommonResult().success("success");
    }
}
