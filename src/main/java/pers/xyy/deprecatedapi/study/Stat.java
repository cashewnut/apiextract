package pers.xyy.deprecatedapi.study;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Stat {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        File file = new File("/Users/xiyaoguo/Desktop/deprecatedAPI/845/commons-lang3");
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(file));
            String str;
            while ((str = br.readLine()) != null){
                if(str.length() > 1){
                    if(Character.isDigit(str.charAt(0)))
                        sb.append(str);
                }
            }
        }catch (Exception e){

        }
        System.out.println(sb.toString());
    }

}
