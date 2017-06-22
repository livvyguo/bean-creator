package org.lvy.crobot;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.lvy.crobot.domain.Column;
import org.lvy.crobot.domain.Table;
import org.lvy.crobot.util.DBAnalysis;

/**
 * @author guozheng
 * @date 2017/05/21
 */
public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/";

    private static final String USER = "root";

    private static final String PASSWORD = "livvy";

    private static final String DOMAIN_PACKAGE_NAME = "org.lvy.domain";
    private static final String MAPPER_PACKAGE_NAME = "org.lvy.mapper";


    public static void main(String[] args) throws IOException {

        try {
            List<Table> tables = DBAnalysis.collectAllTables(URL, "ipright", "root", "livvy");
            String mapperSuffix = "mapper";

            for (Table table : tables) {
                String tbName = table.getName();
                List<Column> columns = table.getColumns();

                //JavaFile mapperJavaFile = JavaCodeGenerator.getMapperJavaFile(mapperSuffix, tbName, columns,
                //    MAPPER_PACKAGE_NAME, DOMAIN_PACKAGE_NAME);
                //
                ////String s = mapperJavaFile.toString();
                ////System.out.println(s);
                //mapperJavaFile.writeTo(Paths.get("."));



            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
