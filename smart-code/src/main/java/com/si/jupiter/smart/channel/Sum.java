package com.si.jupiter.smart.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sum {
    /**
     * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
     * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
     * 给定 nums = [2, 7, 11, 15], target = 9
     * 因为 nums[0] + nums[1] = 2 + 7 = 9
     * 所以返回 [0, 1]
     * https://github.com/MisterBooo/LeetCodeAnimation/blob/master/notes/LeetCode%E7%AC%AC1%E5%8F%B7%E9%97%AE%E9%A2%98%EF%BC%9A%E4%B8%A4%E6%95%B0%E4%B9%8B%E5%92%8C.md
     * @param args
     */
    public static void main(String[] args) {
        int[] nums = {2,7,11,15};
        int target = 9;
        ArrayList<Integer>  indexs = getIndex(nums,target);
        if (indexs.size()==2) {
            System.out.printf("%d index:[%d] + %d index:[%d] = %d", nums[indexs.get(0).intValue()], indexs.get(0).intValue(),
                    nums[indexs.get(1).intValue()], indexs.get(1).intValue(),target);
        }
        else{
            System.out.printf("%d 未找到两个和为当前值的索引", target);
        }
    }

    public static ArrayList<Integer> getIndex(int[] nums, int target)  {
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        Map<Integer ,Integer> key_index = new HashMap<Integer, Integer>();
        for(int i = 0; i<nums.length; i++ ){
            key_index.put(nums[i],i);
        }
        int t = target;
        for (int i = 0; i < nums.length; i++) {
            int b = target - nums[i];
            if(key_index.containsKey(b)){
                indexs.add(i);
                indexs.add(key_index.get(b));
                return indexs;
            }
            else{
                continue;
            }
        }
        return indexs;
    }
}
