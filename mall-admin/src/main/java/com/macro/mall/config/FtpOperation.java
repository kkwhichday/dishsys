package com.macro.mall.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;


/**
 * 类说明：文件上传下载工具类
 *
 */
//@Component
public class FtpOperation{
    public static final int imageCutSize=300;
    private static final Logger log= LoggerFactory.getLogger(FtpOperation.class);

    @Value("${ftp.username}")
    private String userName;

    @Value("${ftp.password}")
    private String passWord;

    @Value("${ftp.host}")
    private String ip;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.filepath}")
    private String CURRENT_DIR;     // 文件存放的目录

    public static final String DIRSPLIT="/";

    public String getCURRENT_DIR() {
        return CURRENT_DIR;
    }


    public void setCURRENT_DIR(String cURRENTDIR) {
        CURRENT_DIR = cURRENTDIR;
    }

    // 下载的文件目录
    private String DOWNLOAD_DIR;



    /**
     *
     * 功能：上传文件附件到文件服务器
     * @param buffIn:上传文件流
     * @param fileName：保存文件名称
     * @param needDelete：是否同时删除
     * @return
     * @throws IOException
     */
    public boolean uploadToFtp(InputStream buffIn,String path, String fileName,boolean needDelete)
            throws FTPConnectionClosedException, IOException,Exception {
        boolean returnValue = false;
        FTPClient ftpClient = null;
        // 上传文件
        try {

            // 建立连接
            ftpClient =connectToServer();
            // 设置传输二进制文件
            setFileType(ftpClient,FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply))
            {
                ftpClient.disconnect();
                throw new IOException("failed to connect to the FTP Server:"+ip);
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(CURRENT_DIR);
            if(!StringUtils.isEmpty(path)){
                if(!existDirectory(ftpClient,path)){
                    this.createDirectory(ftpClient,path);
                }
                ftpClient.changeWorkingDirectory(path);
            }
            // 上传文件到ftp
            returnValue = ftpClient.storeFile(fileName, buffIn);
            if(needDelete){
                ftpClient.deleteFile(fileName);
            }
            // 输出操作结果信息
            if (returnValue) {
                log.info("uploadToFtp INFO: upload file  to ftp : succeed!");
            } else {
                log.info("uploadToFtp INFO: upload file  to ftp : failed!");
            }
            buffIn.close();
            // 关闭连接
            closeConnect(ftpClient);
        } catch (FTPConnectionClosedException e) {
            log.error("ftp连接被关闭！", e);
            throw e;
        } catch (Exception e) {
            returnValue = false;
            log.error("ERR : upload file  to ftp : failed! ", e);
            throw e;
        } finally {
            try {
                if (buffIn != null) {
                    buffIn.close();
                }
            } catch (Exception e) {
                log.error("ftp关闭输入流时失败！", e);
            }

            closeConnect(ftpClient);
        }
        return returnValue;
    }


    /**
     *
     * 功能：根据文件名称，下载文件流
     * @param filename
     * @return
     * @throws IOException
     */
    public InputStream  downloadFile(String path,String filename)
            throws IOException {
        InputStream in=null;
        FTPClient ftpClient=null;
        try {

            // 建立连接
            ftpClient =connectToServer();
            ftpClient.enterLocalPassiveMode();
            // 设置传输二进制文件
            setFileType(ftpClient,FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply))
            {
                ftpClient.disconnect();
                throw new IOException("failed to connect to the FTP Server:"+ip);
            }
//            ftpClient.changeWorkingDirectory(path);

            // ftp文件获取文件
            in=ftpClient.retrieveFileStream(path+"/"+filename);

        } catch (FTPConnectionClosedException e) {
            log.error("ftp连接被关闭！", e);
            throw e;
        } catch (Exception e) {
            log.error("ERR : upload file "+ filename+ " from ftp : failed!", e);
        }finally {
           closeConnect(ftpClient);
        }
        return in;
    }

    /**
     * 转码[GBK -> ISO-8859-1] 不同的平台需要不同的转码
     *
     * @param obj
     * @return
     */
//    private String gbkToIso8859(Object obj) {
//        try {
//            if (obj == null)
//                return "";
//            else
//                return new String(obj.toString().getBytes("GBK"), "iso-8859-1");
//        } catch (Exception e) {
//            return "";
//        }
//    }

    /**
     * 设置传输文件的类型[文本文件或者二进制文件]
     *
     * @param fileType
     *            --BINARY_FILE_TYPE、ASCII_FILE_TYPE
     */
    private void setFileType(FTPClient ftpClient,int fileType) {
        try {
            ftpClient.setFileType(fileType);
        } catch (Exception e) {
            log.error("ftp设置传输文件的类型时失败！", e);
        }
    }

    /**
     *
     * 功能：关闭连接
     */
    public void closeConnect(FTPClient ftpClient) {
        try {
            if (ftpClient != null) {
                System.out.println(ftpClient);
//                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            log.error("ftp连接关闭失败！", e);
        }
    }

    /**
     * 连接到ftp服务器
     */
    private FTPClient connectToServer() throws FTPConnectionClosedException,Exception {
        FTPClient ftpClient;
            int reply;
            try {
                ftpClient=new FTPClient();
                ftpClient.connect(ip,port);
                ftpClient.login(userName,passWord);
                reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    log.info("connectToServer FTP server refused connection.");
                }

            }catch(FTPConnectionClosedException ex){
                log.error("服务器:IP："+ip+"没有连接数！there are too many connected users,please try later", ex);
                throw ex;
            }catch (Exception e) {
                log.error("登录ftp服务器【"+ip+"】失败", e);
                throw e;
            }
            return ftpClient;

    }
    // Check the path is exist; exist return true, else false.
    public boolean existDirectory(FTPClient ftpClient, String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory()
                    && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    /**
     * 创建FTP文件夹目录
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean createDirectory(FTPClient ftpClient,String pathName) throws IOException {
        boolean isSuccess=false;
        try{
            isSuccess=ftpClient.makeDirectory(pathName);
        }catch(Exception e){
            e.printStackTrace();
        }
        return isSuccess;
    }
    /**
     * 带点的
     * @param fileName
     * @return
     */
    public static String getExtention(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }
    /**
     * 不带点
     * @param fileName
     * @return
     */
    public static String getNoPointExtention(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos+1);
    }
    /**
     *
     * 功能：根据当前时间获取文件目录
     * @return String
     */
    public static String getDateDir(Date dateParam){
        Calendar cal = Calendar.getInstance();
        if(null!=dateParam){
            cal.setTime(dateParam);
        }
        int currentYear = cal.get(Calendar.YEAR);
        int currentMouth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH) ;
        //int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        //return currentYear+FtpOperation.DIRSPLIT+currentMouth+FtpOperation.DIRSPLIT+currentDay+FtpOperation.DIRSPLIT+currentHour;
        return currentYear+FtpOperation.DIRSPLIT+currentMouth+FtpOperation.DIRSPLIT+currentDay;
    }


}