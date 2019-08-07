package com.macro.mall.dto;

public class UploadResult {
    private String dir;
    private String host;

    public String getDir() {
        return dir;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    private String filename;
}
