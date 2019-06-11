package pers.xyy.deprecatedapi.jdk;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class Diff {

    private final static String GET_JDK8 = "SELECT package,class,method_name,method_return_type,method_args,id from jdk_deprecated_api7";
    private final static String GET_JDK5 = "SELECT package,class,method_name,method_return_type,method_args,id from jdk_deprecated_api5";

    public static void main(String[] args) {
        Diff diff = new Diff();
        Set<JDKDeprecatedAPI> apis5 = diff.getAPIs(GET_JDK5);
        Set<JDKDeprecatedAPI> apis8 = diff.getAPIs(GET_JDK8);
        for(JDKDeprecatedAPI api : apis5){
            if(!apis8.contains(api))
                System.out.println(api.getId());
        }
    }

    public Set<JDKDeprecatedAPI> getAPIs(String sql){
        Set<JDKDeprecatedAPI> apis = new HashSet<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            while(rs.next()){
                JDKDeprecatedAPI api = new JDKDeprecatedAPI();
                api.setPackageName(rs.getString(1));
                api.setClassName(rs.getString(2));
                api.setMethodName(rs.getString(3));
                api.setMethodReturnType(rs.getString(4));
                api.setMethodArgs(rs.getString(5));
                api.setId(rs.getInt(6));
                apis.add(api);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closeResultset(rs);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
        return apis;
    }

}
