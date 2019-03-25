package pers.xyy.deprecatedapi.jdk.persistence.impl;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.persistence.IJDKDeprecatedAPIDAO;
import pers.xyy.deprecatedapi.utils.DBUtil;
import pers.xyy.deprecatedapi.utils.LoadProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class JDKDeprecatedAPIDAO implements IJDKDeprecatedAPIDAO {

    public final static String INSERT = LoadProperties.get("JDK_DEPRECATED_API_INSERT");

    @Override
    public void insert(JDKDeprecatedAPI jdkDeprecatedAPI) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(INSERT);

            preparedStatement.setString(1,jdkDeprecatedAPI.getPackageName());
            preparedStatement.setString(2,jdkDeprecatedAPI.getClassName());
            preparedStatement.setString(3,jdkDeprecatedAPI.getMethodName());
            preparedStatement.setString(4,jdkDeprecatedAPI.getMethodReturnType());
            preparedStatement.setString(5,jdkDeprecatedAPI.getMethodArgs());
            preparedStatement.setString(6,jdkDeprecatedAPI.getComment());
            preparedStatement.setInt(7,jdkDeprecatedAPI.getLine());

            preparedStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }

    }
}
