package org.example.main;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author huangyuting
 * @Description:
 * @date 2022/3/23 14:56
 */
public class Test2 {
    public static void main(String[] args) {
        final ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        final Iterator<Integer> iterator = integers.iterator();

        while (iterator.hasNext()){
            final Integer integer = iterator.next();
            if(integer.equals(2)){
                integers.remove(integer);
            }
        }

//        for (Integer integer : integers) {
//            if(integer.equals(2)){
//                integers.remove(integer);
//            }
//        }

        for (Integer integer : integers) {
            System.out.println(integer);
        }
    }
}
