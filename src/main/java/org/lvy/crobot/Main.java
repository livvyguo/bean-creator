package org.lvy.crobot;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.lvy.crobot.domain.Column;
import org.lvy.crobot.domain.Table;
import org.lvy.crobot.service.DomainService;
import org.lvy.crobot.util.DBAnalysis;

/**
 * @author guozheng
 * @date 2017/05/21
 */
public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/";

    private static final String USER = "root";

    private static final String PASSWORD = "livvy";

    private static final String PACKAGE_NAME = "org.lvy.domain";


    public static void main(String[] args) throws IOException {

        try {
            List<Table> tables = DBAnalysis.collectAllTables(URL, "ipright", "root", "livvy");
            String mapperSuffix = "mapper";

            for (Table table : tables) {
                String tbName = table.getName();
                List<Column> columns = table.getColumns();

                JavaFile javaFile = DomainService.getJavaEntityFile(tbName, columns,
                    PACKAGE_NAME);
                Builder builder = TypeSpec.interfaceBuilder(getMapperClassName(tbName, mapperSuffix))
                    .addSuperinterface(Serializable.class)
                    .addJavadoc(
                        "@author: guozheng \n@date: $N \n",
                        LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
                    .addModifiers(Modifier.PUBLIC);

                System.out.println(javaFile.toJavaFileObject().getName());

                //MethodSpec.methodBuilder("getById")

                //MethodSpec.methodBuilder("getById").



            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getMapperClassName(String tableName, String mapperSuffix) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName +"_"+
            mapperSuffix);
    }

}
