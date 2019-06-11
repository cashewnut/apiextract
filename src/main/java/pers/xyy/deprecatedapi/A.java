package pers.xyy.deprecatedapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class A {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            System.out.println(new Random().nextInt(34) + 1);
            new Thread().sleep(100);
        }
        System.out.println(new Random().nextInt(11) + 1);
        new Thread().sleep(1000);
        System.out.println(new Random().nextInt(11) + 1);
    }

}
