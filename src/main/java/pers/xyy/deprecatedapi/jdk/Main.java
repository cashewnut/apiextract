package pers.xyy.deprecatedapi.jdk;

import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        File root = new File("/Users/xiyaoguo/Desktop/jdk8");
        List<String> javaFilePath;
        int res = 0;
        for(File f : Objects.requireNonNull(root.listFiles())){
            javaFilePath = FileUtil.getJavaFilePath(f);
            for(String path : javaFilePath){
                res +=new ParserJDK().parserFile(path);
            }
        }
        System.out.println(res);


    }

}
