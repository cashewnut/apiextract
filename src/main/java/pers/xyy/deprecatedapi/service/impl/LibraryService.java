package pers.xyy.deprecatedapi.service.impl;

import pers.xyy.deprecatedapi.model.Library;
import pers.xyy.deprecatedapi.persistence.ILibraryDAO;
import pers.xyy.deprecatedapi.persistence.impl.LibraryDAO;
import pers.xyy.deprecatedapi.service.ILibraryService;

import java.util.List;

public class LibraryService implements ILibraryService {

    ILibraryDAO libraryDAO = new LibraryDAO();

    @Override
    public List<Library> getLibrarys() {
        return libraryDAO.getLibrary();
    }
}
