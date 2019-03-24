package pers.xyy.deprecatedapi;

import pers.xyy.deprecatedapi.model.Library;
import pers.xyy.deprecatedapi.service.Parser;
import pers.xyy.deprecatedapi.service.impl.LibraryService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        List<Library> libraries = new LibraryService().getLibrarys();
        for (Library library : libraries) {
            parser.setLibrary(library.getId());
            parser.parseFile(library.getPath());
        }
//        for (int i = 22; i >= 0; i--) {
//            System.out.println("insert into library(name,version,path) values('spring-core','4.3." + i + "','/home/fdse/xyy/library/spring-core/4.3." + i + "');");
//        }

    }

}
