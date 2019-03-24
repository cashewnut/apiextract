package pers.xyy.deprecatedapi.persistence.impl;

import pers.xyy.deprecatedapi.model.Library;
import pers.xyy.deprecatedapi.persistence.ILibraryDAO;
import pers.xyy.deprecatedapi.utils.DBUtil;
import pers.xyy.deprecatedapi.utils.LoadProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LibraryDAO implements ILibraryDAO {

    private final static String GETLIBRARY = LoadProperties.get("GETLIBRARY");

    @Override
    public List<Library> getLibrary() {
        List<Library> libraries = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(GETLIBRARY);
            rs = preparedStatement.executeQuery();
            while(rs.next())
                libraries.add(new Library(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)));

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.closeResultset(rs);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(connection);
        }
        return libraries;
    }
}
