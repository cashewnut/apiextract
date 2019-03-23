package pers.xyy.deprecatedapi;


import pers.xyy.deprecatedapi.service.Parser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        //System.out.println(Integer.parseInt(args[0]));
        //System.out.println(args[1]);
        parser.setLibrary(Integer.parseInt(args[0]));
        parser.parseFile(new File(args[1]));
//        System.out.println("hello");
    }

}
