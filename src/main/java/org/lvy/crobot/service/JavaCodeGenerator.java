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
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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
public class JavaCodeGenerator {

    public static final String FOUR_SPACE = StringUtils.repeat(StringUtils.SPACE,4);

    public static final String ORG_APACHE_IBATIS_ANNOTATIONS_PARAM
        = "org.apache.ibatis.annotations.Param";

    @NotNull
    public static JavaFile getMapperJavaFile(String mapperSuffix, String tbName,
                                              List<Column> columns, String pkgName,
                                              String domainPkg) {
        String entityName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tbName);
        ClassName entityType = ClassName.get(domainPkg, entityName);

        List<Column> ids = columns.stream().filter(c -> c.getPk()).collect(
            Collectors.toList());

        Builder builder = TypeSpec.interfaceBuilder(getMapperClassName(tbName, mapperSuffix))
            .addJavadoc(
                "@author: guozheng \n@date: $N \n",
                LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
            .addModifiers(Modifier.PUBLIC);

        MethodSpec getById = generateAbstractGetById(entityType, ids);
        MethodSpec selectByExample = generateAbstractSelectByExample(entityType, entityType,
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityName));
        MethodSpec update = generateAbstractUpdate(entityType,
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entityName));

        builder.addMethod(getById);
        builder.addMethod(selectByExample);
        builder.addMethod(update);

        return JavaFile
            .builder(pkgName, builder.build())
            .skipJavaLangImports(true)
            .indent(FOUR_SPACE).build();
    }

    @NotNull
    private static MethodSpec generateAbstractGetById(TypeName returnType, List<Column> parameterCols) {
        List<ParameterSpec> parameterSpecs = parameterCols.stream()
            .map(idCol ->
                ParameterSpec.builder(
                    Helper.getType(idCol.getColumnClass()),
                    idCol.getFieldName())
                    .addAnnotation(
                        AnnotationSpec
                            .builder(
                                ClassName.bestGuess(ORG_APACHE_IBATIS_ANNOTATIONS_PARAM))
                            .addMember("value", "$S", idCol.getFieldName())
                            .build())
                    .build())
            .collect(Collectors.toList());

        return MethodSpec.methodBuilder("getById")
            .addJavadoc("根据ID查询\n")
            .addParameters(parameterSpecs)
            .returns(returnType)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
    }

    @NotNull
    private static MethodSpec generateAbstractSelectByExample(
        TypeName returnType, TypeName paramType, String paramName) {
        ParameterSpec parameterSpec = ParameterSpec
            .builder(paramType, paramName)
            .addAnnotation(AnnotationSpec
                .builder(ClassName.bestGuess(ORG_APACHE_IBATIS_ANNOTATIONS_PARAM))
                .addMember("value", "$S", paramName)
                .build())
            .build();

        return MethodSpec.methodBuilder("selectByExample")
            .addJavadoc("根据example条件查询\n")
            .addParameter(parameterSpec)
            .returns(ParameterizedTypeName.get(ClassName.get(List.class), returnType))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
    }

    @NotNull
    private static MethodSpec generateAbstractUpdate(TypeName paramType, String paramName) {
        ParameterSpec parameterSpec = ParameterSpec
            .builder(paramType, paramName)
            .addAnnotation(AnnotationSpec
                .builder(ClassName.bestGuess(ORG_APACHE_IBATIS_ANNOTATIONS_PARAM))
                .addMember("value", "$S", paramName)
                .build())
            .build();

        return MethodSpec.methodBuilder("update")
            .addJavadoc("根据ID更新不为null的字段\n返回影响的行\n")
            .addParameter(parameterSpec)
            .returns(int.class)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
    }

    private static String getMapperClassName(String tableName, String mapperSuffix) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName +"_"+
            mapperSuffix);
    }

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

    public static class Helper {

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
