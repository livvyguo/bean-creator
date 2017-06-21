package org.lvy.crobot.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lvy.crobot.domain.Column;

/**
 * @author guozheng
 * @date 2017/06/20
 */
public class DomainService {

    public static final String FOUR_SPACE = StringUtils.repeat(StringUtils.SPACE,4);

    @NotNull
    public static JavaFile getJavaEntityFile(String tbName, List<Column> columns, String pkgName) {
        Builder builder = TypeSpec.classBuilder(
            CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tbName))
            .addSuperinterface(Serializable.class)
            .addJavadoc(
                "@author: guozheng \n@date: $N \n",
                LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
            .addModifiers(Modifier.PUBLIC);

        List<FieldSpec> fields = generateFields(columns);
        List<MethodSpec> getters = generateGetterMethods(columns);
        List<MethodSpec> setters = generateSetterMethod(columns);
        builder.addMethods(getters);
        builder.addMethods(setters);

        builder.addFields(fields);
        return JavaFile.builder(pkgName, builder.build())
            .skipJavaLangImports(true)
            .indent(FOUR_SPACE)
            .build();
    }

    private static List<MethodSpec> generateSetterMethod(List<Column> columns) {
        return columns
            .stream()
            .map(col ->
                MethodSpec
                    .methodBuilder(setterName(col.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(
                        Helper.getType(col.getColumnClass()),
                        col.getFieldName())
                    .addStatement("this.$N = $N",
                        col.getFieldName() ,
                        col.getFieldName())
                    .returns(TypeName.VOID)
                    .addJavadoc("setter for $N \n",col.getColComment())
                    .build()
            ).collect(Collectors.toList());
    }

    private static List<MethodSpec> generateGetterMethods(List<Column> columns) {
        return columns
            .stream()
            .map(col ->
                MethodSpec
                    .methodBuilder(getterName(col.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return $N" , col.getFieldName())
                    .returns(Helper.getType(col.getColumnClass()))
                    .addJavadoc("getter for $N \n" ,col.getColComment())
                    .build()
            ).collect(Collectors.toList());
    }

    private static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    @NotNull
    private static String getterName(final String name) {
        return "get" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

    @NotNull
    private static String setterName(final String name) {
        return "set" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

    private static List<FieldSpec> generateFields(List<Column> columns) {
        return columns
            .stream()
            .map(col -> FieldSpec
                .builder(
                    Helper.getType(col.getColumnClass()),
                    col.getFieldName(),
                    Modifier.PRIVATE)
                .addJavadoc(col.getColComment() + getLineSeparator())
                .build())
            .collect(Collectors.toList());
    }

    private static class Helper {

        public static final Class<?> getType(String typeName) {
            return RELATION.getOrDefault(typeName, byte[].class);
        }

        private static final Map<String, Class<?>> RELATION = Maps.newHashMap();

        static {
            RELATION.putIfAbsent(Integer.class.getTypeName(), Integer.class);
            RELATION.putIfAbsent(String.class.getTypeName(), String.class);
            RELATION.putIfAbsent(Long.class.getTypeName(), Long.class);
            RELATION.putIfAbsent(Boolean.class.getTypeName(), Boolean.class);
            RELATION.putIfAbsent(Float.class.getTypeName(), Float.class);
            RELATION.putIfAbsent(Double.class.getTypeName(), Double.class);
            RELATION.putIfAbsent(BigDecimal.class.getTypeName(), BigDecimal.class);
            RELATION.putIfAbsent(BigInteger.class.getTypeName(), BigInteger.class);
            RELATION.putIfAbsent(java.sql.Date.class.getTypeName(), java.util.Date.class);
            RELATION.putIfAbsent(java.sql.Time.class.getTypeName(), java.util.Date.class);
            RELATION.putIfAbsent(java.sql.Timestamp.class.getTypeName(), java.util.Date.class);
        }
    }

}
