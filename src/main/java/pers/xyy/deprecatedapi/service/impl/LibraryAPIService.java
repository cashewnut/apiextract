package pers.xyy.deprecatedapi.service.impl;

import pers.xyy.deprecatedapi.model.LibraryAPI;
import pers.xyy.deprecatedapi.persistence.ILibraryAPIDAO;
import pers.xyy.deprecatedapi.persistence.impl.LibraryAPIDAO;
import pers.xyy.deprecatedapi.service.ILibraryAPIService;

public class LibraryAPIService implements ILibraryAPIService {

    private ILibraryAPIDAO libraryAPIDAO = new LibraryAPIDAO();

    @Override
    public void saveLibraryAPI(LibraryAPI libraryAPI) {
        libraryAPIDAO.insertLibraryAPI(libraryAPI);
    }
}
