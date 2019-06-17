package pers.xyy.deprecatedapi.study;

import pers.xyy.deprecatedapi.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudyService {

    private final static String search = "select id,repos_name,local_addr from repository_java where stars_count>1000 and master_host=84 and primaryLanguage='Java' and is_downloaded=1 and id > 529262 and id<657388";
    private final static String URL = "jdbc:mysql://10.141.221.85:3306/github?useUnicode=true&characterEncoding=UTF-8&useSSL=false";

    public List<Project> getProjects() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Project> projects = new ArrayList<>();
        try {
            connection = DBUtil.getConnection(URL);
            preparedStatement = connection.prepareStatement(search);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Project project = new Project();
                project.setId(resultSet.getInt(1));
                project.setRepoName(resultSet.getString(2));
                project.setLocalAddress(resultSet.getString(3));
                projects.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeResultset(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
        return projects;
    }

}
