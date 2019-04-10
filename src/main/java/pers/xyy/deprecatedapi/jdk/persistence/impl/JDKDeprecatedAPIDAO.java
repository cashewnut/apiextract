package pers.xyy.deprecatedapi.jdk.persistence.impl;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.persistence.IJDKDeprecatedAPIDAO;
import pers.xyy.deprecatedapi.utils.DBUtil;
import pers.xyy.deprecatedapi.utils.LoadProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JDKDeprecatedAPIDAO implements IJDKDeprecatedAPIDAO {

    public final static String INSERT = LoadProperties.get("JDK_DEPRECATED_API_INSERT");
    public final static String GET = LoadProperties.get("JDK_DEPRECATED_API_GET");
    public final static String UPDATE = LoadProperties.get("JDK_DEPRECATED_API_UPDATE");

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
            preparedStatement.setString(8,jdkDeprecatedAPI.getReplacedComment());
            preparedStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }

    }

    @Override
    public List<JDKDeprecatedAPI> getJDKDeprecatedAPIs() {
        List<JDKDeprecatedAPI> jdkDeprecatedAPIS = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(GET);
            rs = preparedStatement.executeQuery();
            while(rs.next()){
                JDKDeprecatedAPI api = new JDKDeprecatedAPI();
                api.setId(rs.getInt(1));
                api.setPackageName(rs.getString(2));
                api.setClassName(rs.getString(3));
                api.setMethodName(rs.getString(4));
                api.setMethodReturnType(rs.getString(5));
                api.setMethodArgs(rs.getString(6));
                api.setComment(rs.getString(7));
                api.setLine(rs.getInt(8));
                api.setReplacedComment(rs.getString(9));
                jdkDeprecatedAPIS.add(api);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closeResultset(rs);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
        return jdkDeprecatedAPIS;
    }

    @Override
    public void updateById(JDKDeprecatedAPI jdkDeprecatedAPI) {

    }

    @Override
    public void updateById(List<JDKDeprecatedAPI> jdkDeprecatedAPIS) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE);
            for(JDKDeprecatedAPI api : jdkDeprecatedAPIS){
                preparedStatement.setString(1,api.getPackageName());
                preparedStatement.setString(2,api.getClassName());
                preparedStatement.setString(3,api.getMethodName());
                preparedStatement.setString(4,api.getMethodReturnType());
                preparedStatement.setString(5,api.getMethodArgs());
                preparedStatement.setString(6,api.getComment());
                preparedStatement.setInt(7,api.getLine());
                preparedStatement.setString(8,api.getReplacedComment());
                preparedStatement.setInt(9,api.getId());
                preparedStatement.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
    }
}
