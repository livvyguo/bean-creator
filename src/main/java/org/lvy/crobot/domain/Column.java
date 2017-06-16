package org.lvy.crobot.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author guozheng
 * @date 2017/05/21
 */
public class Column {
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 列名称(字段名称)
     */
    private String name;
    /**
     * 是否主键
     */
    private Boolean isPk;
    /**
     * 默认值
     */
    private String value;
    /**
     * 是否为空
     */
    private Boolean isNull;
    /**
     * 数据类型
     */
    private String type;
    /**
     * 数据长度
     */
    private int length;
    /**
     * 代码类型
     */
    private int codeType;

    private String columnClass;

    private String colComment;


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPk() {
        return isPk;
    }

    public void setPk(Boolean pk) {
        isPk = pk;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getNull() {
        return isNull;
    }

    public void setNull(Boolean notNull) {
        isNull = notNull;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public String getColumnClass() {
        return columnClass;
    }

    public void setColumnClass(String columnClass) {
        this.columnClass = columnClass;
    }

    public String getColComment() {
        return colComment;
    }

    public void setColComment(String colComment) {
        this.colComment = colComment;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
