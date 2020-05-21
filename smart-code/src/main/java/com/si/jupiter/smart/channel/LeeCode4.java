package com.si.jupiter.smart.channel;

public class LeeCode4 {
    /**
     * 给定两个大小为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。 请你找出这两个正序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。 你可以假设 nums1 和 nums2 不会同时为空。
     *
     * 示例1：
     * nums1 = [1, 3]
     * nums2 = [2]
     *
     * 则中位数是 2.0
     *
     * 示例2：
     * nums1 = [1, 2]
     * nums2 = [3, 4]
     *
     * 则中位数是 (2 + 3)/2 = 2.5
     * 逻辑实现树 一定写清洗,否则很费劲
     * @param args
     */
    public static void main(String[] args){
        int[] nums1 = {1,2};
        int[] nums2 = {3,4};
        System.out.println(middle(nums1,nums2));
    }

    public static double middle(int[] nums1,int[] nums2){
        int count = nums1.length+nums2.length;
        int mid_index = 0;
        int q = 0; //表示1 奇 0 偶
        if(count%2==0){
            mid_index=count/2;
            q = 0;
        }
        else{
            mid_index=(count+1)/2;
            q = 1;
        }
        boolean isFirstNum= true;
        double res=0;
        for(int i=0,j=0,k=0;k<count&&(i<nums1.length||j<nums2.length);){
            //1.先比较
            //2.判定
            //3.自增
            if (k+1==mid_index){
                if(isFirstNum){
                    res+=nums1[i];
                }
                else{
                    res+=nums2[j];
                }
                //如果奇数直接返回
                if (q==1){
                    return res;
                }
            }else if(k==mid_index){ //第二个值 表明是偶数,因此不需要判定奇偶了
                if(isFirstNum){
                    res+=nums1[i];
                }
                else{
                    res+=nums2[j];
                }
                return res/2.0;
            }
            if(isFirstNum){ //自增长,边界问题考虑
                if(i<nums1.length-1){ //第一组未遍历到末尾
                    if (nums1[i]<nums2[j]){ //小于
                        isFirstNum=true;
                        i++;
                    }//大于 切换数组即可
                    else{
                        isFirstNum=false;
                    }
                }
                else{//第一组遍历到末尾 切换
                    isFirstNum=false;
                }
            }else{
                if(j< nums2.length-1){//第二组未遍历到末尾
                    if(nums1[i]<nums2[j]){
                        isFirstNum=false;
                        j++;
                    }
                }
                else{//第二组遍历到末尾 切换
                    isFirstNum=true;
                }
            }
            k++;
        }

        return -1;
    }
}
