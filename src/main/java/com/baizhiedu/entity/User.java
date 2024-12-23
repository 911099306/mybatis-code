package com.baizhiedu.entity;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id;
    private String name;
    private Integer version;


    public User() {
}

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public User(Integer id, String name, Integer version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
