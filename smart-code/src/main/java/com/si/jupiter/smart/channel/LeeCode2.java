package com.si.jupiter.smart.channel;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class LeeCode2 {
    /**
     * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
     *
     * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
     *
     * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
     *
     * 示例：
     *
     * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
     * 输出：7 -> 0 -> 8
     * 原因：341 + 465 = 806
     * @param args
     */
    public static void main(String[] args) {
        LinkedList<Integer> a = new LinkedList<Integer>();
        a.add(1);
        a.add(4);
        a.add(3);
        LinkedList<Integer> b = new LinkedList<Integer>();
        b.add(5);
        b.add(6);
        b.add(4);
        LinkedList<Integer> res = sumForTwo(a,b);
        Collections.reverse(res);
        Iterator<Integer> res_it = res.iterator();;
        while (res_it.hasNext()){
            System.out.print(res_it.next());
        }
        System.out.println();

    }
    public static LinkedList<Integer> sumForTwo(LinkedList<Integer> a,LinkedList<Integer> b){
        LinkedList<Integer> res = new LinkedList<>();
        Iterator<Integer> it_a = a.iterator();
        Iterator<Integer> it_b = b.iterator();
        int c=0;
        for (; it_a.hasNext()|| it_b.hasNext(); ) {
            int sum = c;
            if (it_a.hasNext()){
                sum+=it_a.next();
            }
            if (it_b.hasNext()){
                sum+=it_b.next();
            }
            c = sum/10;
            res.add(sum%10);
        }
        return res;
    }
}
