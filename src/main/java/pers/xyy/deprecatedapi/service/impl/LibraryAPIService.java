package pers.xyy.deprecatedapi.service.impl;

import pers.xyy.deprecatedapi.model.LibraryAPI;
import pers.xyy.deprecatedapi.persistence.ILibraryAPIDAO;
import pers.xyy.deprecatedapi.persistence.impl.LibraryDAO;
import pers.xyy.deprecatedapi.service.ILibraryAPIService;

public class LibraryAPIService implements ILibraryAPIService {

    private ILibraryAPIDAO libraryAPIDAO = new LibraryDAO();

    @Override
    public int saveLibraryAPI(LibraryAPI libraryAPI) {
        return libraryAPIDAO.insertLibraryAPI(libraryAPI);
    }
}
