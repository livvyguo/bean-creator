package org.lvy.crobot;

import java.util.LinkedList;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author guozheng
 * @date 2017/06/07
 */
public class Test {
    public static void main(String[] args) {
        LinkedList<Object> objects =
            Lists.newLinkedList();
        objects.add("ad");
        objects.add("ae");

        Object o = objects.get(0);
        System.out.println(o);

        Object o1 = objects.get(objects.size() - 1);

        System.out.println(o1);
        Object first = objects.getFirst();

        System.out.println(first);

        Object last = objects.getLast();

        System.out.println(last);

        String xx = "a:1;b:2;c:3";

        Map<String, String> collect =
            Splitter.on(";")
                .withKeyValueSeparator(":")
                .split(xx);

        System.out.println(collect);

    }
}
