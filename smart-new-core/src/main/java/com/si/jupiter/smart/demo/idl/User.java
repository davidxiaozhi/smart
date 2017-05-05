package com.si.jupiter.smart.demo.idl;

import java.util.List;

/**
 * Author: baichuan - lizhipeng
 * Date: 2017/01/03 17:21
 */
public class User {
    private String name;
    private int age;
    private List<String> likes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", likes=" + likes +
                '}';
    }
}
