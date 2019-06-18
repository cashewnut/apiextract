package pers.xyy.deprecatedapi;


import pers.xyy.deprecatedapi.utils.DBUtil;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class A {

    private static final String SQL = "select id,comment from commons_lang3";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(SQL);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                FileUtil.write("/Users/xiyaoguo/Desktop/commons-lang3_comments", "<No." + rs.getInt(1) + ">\n");
                FileUtil.write("/Users/xiyaoguo/Desktop/commons-lang3_comments", rs.getString(2) + "\n\n\n");
            }
        } catch (Exception e) {

        }


    }

}
