package org.lvy.crobot;

import java.sql.SQLException;
import java.util.List;

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


    public static void main(String[] args) {

        try {
            List<Table> tables = DBAnalysis.collectAllTables(URL, "ipright", "root", "livvy");

            System.out.println(tables);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
