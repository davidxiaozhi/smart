package com.si.jupiter.smart.channel;

import java.util.ArrayList;
import java.util.HashMap;

public class LeeCode5 {
    /**
     * 给定一个字符串，要求这个字符串当中最长的回文串。
     *
     * 示例
     * Input: "aabcdbcaee"
     * Output: "abcdcba"
     * Note: "aba" is also a valid answer.
     * Input: "aabcddcbaee"
     * Output: "abcddcba"
     *
     * @param args
     */
    public static void main(String[] args){
        System.out.println(findChild("aabcddcbaee"));
        System.out.println(findChild("aabcdcbaee"));
    }

    /**
     * 逻辑树
     * 回文串 分为奇数串abcba 和 偶数串 abccba
     * 第一方案
     * 1.索引存储 例如 a 对应全部索引 b 对应全部索引
     * 2.索引遍历
     *      2.1 索引数目>2 需要两两组合遍历,奇数串验证至中心两侧是否字符一致即可 中心字符不做验证,偶数串 全部验证完成
     * 第二方案
     * 1.基于窗口滑动的思想
     * 例如假设最长回文窗口为 N ,基于 N 滑动 验证机制相同,窗口宽度递减
     * 重点关注:暂无 奇 偶 处理不同
     *
     * @param str
     * @return
     */
    public static String findChild(String str){
        //方案一
        HashMap<Character, ArrayList<Integer>> index= new HashMap<Character, ArrayList<Integer>>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            //遍历保证有序
            if(index.containsKey(c)){
                ArrayList<Integer>  list = index.get(c);
                list.add(i);
            }else{
                ArrayList<Integer>  list = new ArrayList<>();
                list.add(i);
                index.put(c,list);
            }
        }
        //处理窗口
        int ms=0;
        int me=0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            ArrayList<Integer>  list = index.get(c);
            if (list.size()>2){
                for (int j = 0; j < list.size(); j++) {
                    int start = list.get(j);
                    if (start>i){
                        for (int k = j+1; k < list.size(); k++) {
                            int end = list.get(k);
                            //奇偶 都可以 奇数不用比较中间位
                            int mid = (end-start+1)/2;
                            int s = start;
                            int e = end;
                            boolean check = false;
                            while (s<=mid-1){
                                if (str.charAt(s)!=str.charAt(e)){
                                   break;
                                }
                                if (s==mid-1&&str.charAt(s)==str.charAt(e)){
                                    check=true;
                                }
                                s= s+1;
                                e= e-1;
                            }
                            if (check){
                                if(end-start>me-ms){
                                    me=end;
                                    ms=start;
                                }
                            }
                        }
                    }
                }
            }
        }
        return str.substring(ms,me+1);
    }
}
