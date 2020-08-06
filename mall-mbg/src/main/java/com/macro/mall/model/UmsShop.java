package com.macro.mall.model;

import java.io.Serializable;
import java.util.Date;

public class UmsShop implements Serializable {
    private Long id;

    private String name;

    /**
     * 头像
     *
     * @mbg.generated
     */
    private String pic;

    /**
     * 店铺介绍
     *
     * @mbg.generated
     */
    private String info;

    /**
     * 店铺公告
     *
     * @mbg.generated
     */
    private String notice;

    /**
     * 邮箱
     *
     * @mbg.generated
     */
    private String email;

    /**
     * 店铺是否开业：0->停业；1->开业
     *
     * @mbg.generated
     */
    private Integer open;

    /**
     * 创建时间
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     * 店铺状态：0->禁用；1->启用
     *
     * @mbg.generated
     */
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", pic=").append(pic);
        sb.append(", info=").append(info);
        sb.append(", notice=").append(notice);
        sb.append(", email=").append(email);
        sb.append(", open=").append(open);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}