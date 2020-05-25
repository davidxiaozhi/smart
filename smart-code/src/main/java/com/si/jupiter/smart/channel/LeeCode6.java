package com.si.jupiter.smart.channel;

import io.netty.util.internal.StringUtil;

public class LeeCode6 {
    /**
     * 蛇形n阶 矩阵打印 例如 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16
     * 输出 1, 2, 3,  4,
     *     12,13,14, 5
     *     11,16,15, 6
     *     10, 9, 8, 7
     *
     * 输入 1,2,3,4,5,6,7,8,9
     * 输出 1,2,3
     *     8,9,4
     *     7,6,5
     *  如果字符串的化同理
     *
     *
     *
     * @param args
     */
    public static void main(String[] args){
        String[] input = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};
        snakeStr(input,5);
    }

    /**
     *  技术方案
     *  1.首先初始化一个 n * M 数组 这里的 如果能整除 M = in.length/n, 否则 M= in.length/n   + 1
     *  2.计算每一个字符的下标
     *  注意事项, 每一个 i,j 的自增自减(条件)
     *  1.首先 j 0->n-1 切
     *  2.i 1->n-1
     *  3,j n-2 - 2
     *  4,j n-2 - 2
     *
     *   蛇形n阶 矩阵打印 例如 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16
     *      * 输出 1, 2, 3,  4,
     *      *     12,13,14, 5
     *      *     11,16,15, 6
     *      *     10, 9, 8, 7
     *
     *  设计基于 靠边站的思想 边分为 横边 和 竖边 ,最终所有边,遍历介绍哈
     * @return
     */
    public static String snakeStr(String[] in,int n ){
        if(in.length == 0){
            return "";
        }
        //计算有多少了列
        int m = 0;
        if (in.length%n==0){
            m = in.length /n;
        }
        else{
            m = in.length /n + 1;
        }
        String[][] res = new String[n][m];

        int h = 0;
        int s = 0;
        int sw = 1; // 奇数横边 偶数竖边
        boolean is_add = true;
        for (int i = 0; i < in.length; i++) {
            res[h][s] = in[i];
            if(sw%2==1){
                if (is_add){ //如果自增注意左边界
                    if (s<m-sw/4-1){
                        s++;
                    }
                    else{
                        //横向自增达到边界,换边, 同时
                        sw+=1;
                        h+=1; //横向+1

                    }
                }
                else{ //如果横自减注意右边界
                    if (s>0+sw/4){
                        s--;
                    }
                    else{
                        sw+=1;
                        h-=1;
                    }
                }
            }else {
                if (is_add){ //如果自增注意左边界
                    if (h<n-sw/4-1){
                        h++;
                    }
                    else{
                        //竖向自增达到边界,换边, 同时
                        sw+=1;
                        s-=1; //横向+1
                        is_add=false; //竖边增加到头开始减

                    }
                }
                else{
                    if (h>0+sw/4){
                        h--;
                    }
                    else{
                        sw+=1;
                        s+=1;
                        is_add=true;//竖边减到头开始加
                    }
                }
            }

        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(res[i][j]+" ");
            }
            System.out.println("");
        }
        return res.toString();
    }
}
