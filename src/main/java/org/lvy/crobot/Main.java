package org.lvy.crobot;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.lvy.crobot.domain.Column;
import org.lvy.crobot.domain.Table;
import org.lvy.crobot.service.JavaCodeGenerator;
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
            List<Table> tables = DBAnalysis.collectAllTables(URL, "mydb", "root", "livvy");
            String mapperSuffix = "mapper";
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template resultMapTemplate = gt.getTemplate("/resultMap.btl");
            Template allFieldTemplate = gt.getTemplate("/allField.btl");
            Template insertTemplate = gt.getTemplate("/insert.btl");
            Template updateTemplate = gt.getTemplate("/update.btl");
            for (Table table : tables) {
                String tbName = table.getName();
                List<Column> columns = table.getColumns();
                String beanName = CaseFormat.LOWER_UNDERSCORE.to
                    (CaseFormat.UPPER_CAMEL, tbName);
                String typeName = ClassName.get(DOMAIN_PACKAGE_NAME, beanName).toString();

                resultMapTemplate.binding("resultMapId", "result" + beanName);
                resultMapTemplate.binding("resultType", typeName);
                resultMapTemplate.binding("columns", columns);

                insertTemplate.binding("tableName", tbName);
                insertTemplate.binding("columns", columns);
                System.out.println(insertTemplate.render());
                updateTemplate.binding("tableName", tbName);
                updateTemplate.binding("columns", columns);
                System.out.println(updateTemplate.render());

                String render = resultMapTemplate.render();
                System.out.println(render);

                allFieldTemplate.binding("allField", columns.stream().map(Column::getName)
                    .collect(Collectors.joining(" ,\n    ")));
                System.out.println(allFieldTemplate.render());

                JavaFile mapperJavaFile = JavaCodeGenerator.getMapperJavaFile(mapperSuffix, tbName, columns,
                    MAPPER_PACKAGE_NAME, DOMAIN_PACKAGE_NAME);

                //String s = mapperJavaFile.toString();
                //System.out.println(s);
                mapperJavaFile.writeTo(Paths.get("."));

                JavaFile javaEntityFile = JavaCodeGenerator.getJavaEntityFile(tbName, columns,
                    DOMAIN_PACKAGE_NAME);
                javaEntityFile.writeTo(Paths.get("."));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
