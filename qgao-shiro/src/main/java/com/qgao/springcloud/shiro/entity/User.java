package com.qgao.springcloud.shiro.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2021-06-08 17:28:08
 */
public class User implements Serializable {
    private static final long serialVersionUID = -91489657244065578L;
    /**
     * 也可以用id登录
     */
    private Long id;
    /**
     * 昵称,默认id
     */
    private String nickname;
    /**
     * 邮箱作为登录
     */
    private String email;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录时间
     */
    private Date loginTime;
    /**
     * 上次登录时间
     */
    private Date lastTime;
    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 真实姓名
     */
    private String realname;

    private String sex;

    private String phone;

    private Date birthday;

    private String address;
    /**
     * 教育
     */
    private String education;
    /**
     * 专业
     */
    private String major;
    /**
     * 个人描述
     */
    private String description;
    /**
     * 用户信息修改时间
     */
    private Date updateTime;
    /**
     * 保存的是用户头像的uri
     */
    private String img;
    /**
     * 积分，注册时为100
     */
    private Integer point;
    /**
     * 用户的等级
     */
    private Integer levelId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

}
