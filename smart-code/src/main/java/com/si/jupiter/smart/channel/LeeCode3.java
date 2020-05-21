package com.si.jupiter.smart.channel;

import java.util.ArrayList;
import java.util.HashMap;

public class LeeCode3 {
    /**
     * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
     * 示例 1:
     * 输入: "abcabcbb"
     * 输出: 3
     * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
     * @param args
     */
    public static void main(String[] args){
        //String s = "abcabcdbb";
        String s = "aabsdefghka";
        int res_index = 0;
        int res_max = 1;
        int str_length = s.toCharArray().length;
        for (int i = 0;i<str_length;i++){
            int j = i+1;
            //记录从 index =i 开始所有的字符出现的开始位置
            HashMap<Character,Integer> window_index = new HashMap<>();
            window_index.put(s.charAt(i),i);
            for (; j < str_length; j++) {
                if(window_index.containsKey(s.charAt(j))||j==str_length-1){
                    if(j-i>res_max){
                        res_max=j-i;
                        res_index=i;
                    }
                    break;
                }
                else {
                    window_index.put(s.charAt(j),j);
                }
            }


        }
        if (res_max==1){
            System.out.println(s.charAt(res_index));
        }
        else {
            System.out.printf("max  child seq  length is %d, the start index is %d,and the seq is %s\n", res_max, res_index, s.substring(res_index, res_index + res_max));
        }
    }
}
