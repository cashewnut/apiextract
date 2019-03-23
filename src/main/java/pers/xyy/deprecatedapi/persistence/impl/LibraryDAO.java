package pers.xyy.deprecatedapi.persistence.impl;


import pers.xyy.deprecatedapi.model.LibraryAPI;
import pers.xyy.deprecatedapi.persistence.ILibraryAPIDAO;
import pers.xyy.deprecatedapi.utils.DBUtil;
import pers.xyy.deprecatedapi.utils.LoadProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LibraryDAO implements ILibraryAPIDAO {

    private final static String INSERT_LIBRARY_API = LoadProperties.get("INSERT_LIBRARY_API");

    @Override
    public int insertLibraryAPI(LibraryAPI libraryAPI) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer id = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_LIBRARY_API, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, libraryAPI.getPkg());
            preparedStatement.setString(2, libraryAPI.getClazz());
            preparedStatement.setString(3, libraryAPI.getMethod());
            preparedStatement.setInt(4, libraryAPI.getLine());
            preparedStatement.setString(5, libraryAPI.getComment());
            preparedStatement.setInt(6, libraryAPI.getLibrary());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeResultset(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
        return id;
    }
}
