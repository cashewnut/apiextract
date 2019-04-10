package pers.xyy.deprecatedapi.jdk;

import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        File root = new File("/home/fdse/xyy/JDK/JDK1.6-Java SE Development Kit 6u45");
        List<String> javaFilePath;
        for(File f : Objects.requireNonNull(root.listFiles())){
            javaFilePath = FileUtil.getJavaFilePath(f);
            for(String path : javaFilePath){
                new ParserJDK().parseFile(path);
            }
        }


    }

}
