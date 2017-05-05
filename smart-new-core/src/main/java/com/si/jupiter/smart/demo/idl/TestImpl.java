package com.si.jupiter.smart.demo.idl;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 14:45
 */
public class TestImpl implements Test {
    public String test2() {
        return "hello test";
    }

    public User test(String s) {
        User u = new User();
        u.setAge(12);
        u.setName("张三" + s);
        List<String> likes = new ArrayList<>();
        likes.add("美食");
        u.setLikes(likes);
        return u;
    }

    public void test(String s, int i) {
        System.out.println(s + "------" + i);
    }
}
