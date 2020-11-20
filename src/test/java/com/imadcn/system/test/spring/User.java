package com.imadcn.system.test.spring;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author Anson <br>
 * Created on 2020/11/20 17:19<br>
 * 名称：User.java <br>
 * 描述：JSON测试对象<br>
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1345785407707600201L;

    public User(){}

    public User(String name, LocalDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    private String name;
    private LocalDateTime dateTime;
    private Long longValue;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", dateTime=" + dateTime +
                ", longValue=" + longValue +
                ", date=" + date +
                '}';
    }
}