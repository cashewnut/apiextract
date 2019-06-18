package pers.xyy.deprecatedapi.utils;

import java.util.*;

public class StringUtils {

    public static boolean typeEquals(String[] l1, String[] l2) {
        if (l1.length != l2.length)
            return false;
        boolean res = true;
        for (int i = 0; i < l1.length; i++) {
            String args1 = l1[i];
            String args2 = l2[i];
            res &= typeEquals(args1, args2);
        }
        return res;
    }

    public static boolean typeEquals(String str1, String str2) {
        if (str1.isEmpty() && str2.isEmpty())
            return true;
        if (str1.isEmpty() || str2.isEmpty())
            return false;

        //TODO 判断两个类是否是父子类，暂时写死，有时间重新写
        Map<String, Set<String>> map = new HashMap<>();
        map.put("JComponent", new HashSet<>(Arrays.asList("JMenuBar", "JScrollPane")));
        map.put("Component", new HashSet<>(Arrays.asList("JComponent", "JTable")));
        map.put("JTable", new HashSet<>(Arrays.asList("JComponent", "Component")));
        String[] str1s = str1.split("\\.");
        String[] str2s = str2.split("\\.");
        String s1 = str1s[str1s.length - 1], s2 = str2s[str2s.length - 1];
        s1 = s1.replace("<?>", "");
        s2 = s2.replace("<?>", "");
        if (s1.equals(s2))
            return true;
        if (map.containsKey(s1)) {
            if (map.get(s1).contains(s2))
                return true;
        }
        if (map.containsKey(s2)) {
            return map.get(s2).contains(s1);
        }
        return false;
    }

}
