package org.lvy.bc.constant;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

/**
 * @author guozheng
 * @date 2017/05/03
 */
public class Main {
    public static void main(String[] args) throws IOException {

        List<String> lines = Files.readLines(
            new File("/Users/livvy/IdeaProjects/bean-creator/src/main/resources/field.txt"),
            Charset.defaultCharset());
        lines.forEach(l->{
            String field = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, l);
            System.out.println(field);
        });

        List<String> names = lines.stream().map(
            l -> CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, l)).collect
            (Collectors.toList());



        System.out.println("------------------");

        List<String> f = names.stream().map(l -> String.format("%n #{%s}", l)).collect(
            Collectors.toList());

        String join = Joiner.on(" ,").join(f);
        System.out.println(join);

        System.out.println("-----------------------");
        List<String> collect = lines.stream().map(l -> {
            String fName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, l);
            return String.format("<if test=\"query.%s != null\">%n and %s = #{query.%s}, %n</if>",
                fName, l, fName);
        }).collect(Collectors.toList());

        System.out.println(Joiner.on(System.getProperty("line.separator", "\n")).join(collect));

        System.out.println("------------------");

    }
}
