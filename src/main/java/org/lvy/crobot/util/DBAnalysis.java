package org.lvy.crobot.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import org.lvy.crobot.domain.Column;
import org.lvy.crobot.domain.Table;

/**
 * @author guozheng
 * @date 2017/05/21
 */
public class DBAnalysis {

    public static final String SELECT_FROM_S_LIMIT_1 = "SELECT * FROM %s LIMIT 1";
    public static final String SHOW_FULL_COLUMNS_FROM_TABLE = "SHOW FULL COLUMNS FROM %s";

    public static final String FIELD = "field";
    public static final String COMMENT = "Comment";

    private Connection connection;

    private DBAnalysis(String connStr, String db, String username, String password) throws ClassNotFoundException,

        SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(connStr + db, username, password);
    }

    private static DBAnalysis instance = null;

    private static DBAnalysis getInstance(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DBAnalysis(connStr, db, username, password);
        }
        return instance;
    }

    private static Connection getConnection(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        return getInstance(connStr, db, username, password).connection;
    }

    /**
     * 获取表的主键
     * @param conn 数据库连接
     * @param tableName  表名
     * @return 表中的主键
     * @throws SQLException
     */
    private static List getPks(Connection conn, String tableName) throws SQLException {
        List pks = new ArrayList();
        ResultSet rsPks = conn.getMetaData().getPrimaryKeys(null, null, tableName);
        while (rsPks.next()) {
            pks.add(rsPks.getString("COLUMN_NAME"));
        }
        rsPks.close(); //关闭
        return pks;
    }

    /**
     *  获取所有的列信息
     * @param conn 数据库连接
     * @param tableName 表名
     * @return 列的详细信息
     * @throws SQLException
     */
    private static List<Column> getColumns(Connection conn,String tableName) throws SQLException {
        List<Column> cols = new ArrayList<Column>();
        //获取这个表的主键 ，并存储在list中
        List pks = getPks(conn,tableName);
        Map<String, String> fieldAndComment = getFieldAndComment(conn, tableName);
        try(PreparedStatement stmt = conn.prepareStatement(
            String.format(SELECT_FROM_S_LIMIT_1,tableName))) {
            ResultSet rs = stmt.executeQuery();
            //此处需要优化 limit 1 top 1 rownum <= 1  根据不同数据库
            ResultSetMetaData rsCols = rs.getMetaData();
            int columnCount = rsCols.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Column col = new Column();
                col.setTableName(rsCols.getTableName(i));
                String columnName = rsCols.getColumnName(i);
                col.setName(columnName);
                col.setFieldName(
                    CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName));
                col.setType(rsCols.getColumnTypeName(i));
                col.setPk(pks.contains(rsCols.getColumnName(i)));
                col.setLength(rsCols.getColumnDisplaySize(i));
                col.setNull(rsCols.isNullable(i) == 0 ? false : true);
                col.setColumnClass(rsCols.getColumnClassName(i));
                col.setColComment(fieldAndComment.get(columnName));
                cols.add(col);
            }
            rs.close();
            return cols;
        }
    }

    private static Map<String, String> getFieldAndComment(Connection connection, String table)
        throws SQLException {
        LinkedHashMap<String, String> fieldCommentMap = Maps.newLinkedHashMap();
        try (PreparedStatement preparedStatement = connection.prepareStatement(String.format(SHOW_FULL_COLUMNS_FROM_TABLE, table))) {
            ResultSet fieldCommentResultSet = preparedStatement.executeQuery();
            while (fieldCommentResultSet.next()) {
                fieldCommentMap.put(fieldCommentResultSet.getNString(FIELD), fieldCommentResultSet.getNString(COMMENT));
            }
        }
        return fieldCommentMap;
    }

    /**
     * 获取所有表信息
     * @param connStr  数据库连接字符串
     * @param db 连接的库
     * @param username  数据库用户名
     * @param password   数据库密码
     * @return  库中表信息
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static List<Table> collectAllTables(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        try(Connection conn = getConnection(connStr, db, username, password)) {
            return collectAllTables(conn, db);
        }
    }

    /**
     *  获取所有表信息
     * @param conn 数据库连接 s
     * @param db 数据库
     * @return  库中表信息
     * @throws SQLException
     */
    public static List<Table> collectAllTables(Connection conn, String db) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();

        //获取库中的所有表
        ResultSet rsTables = dmd.getTables(null, null, null, new String[]{"TABLE"});
        List<Table> tables = new ArrayList<Table>();
        //将表存到list中
        while (rsTables.next()) {
            Table tb = new Table();
            tb.setSpace(db);
            //获取表名称
            String tbName = rsTables.getString("TABLE_NAME");
            tb.setName(tbName);

            //获取表中的字段及其类型
            List<Column> cols = getColumns(conn,tbName);
            tb.setColumns(cols);
            tables.add(tb);
        }
        rsTables.close();

        return tables;
        //connection未关闭
    }


}
