package pers.xyy.deprecatedapi.study;

import com.alibaba.fastjson.JSONObject;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.tools.replace.model.Replace;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Statistics {

    static class Type {
        int feature;
        int replace;// 1.能；2.不能

        public Type(int feature, int replace) {
            this.feature = feature;
            this.replace = replace;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Type type = (Type) o;
            return feature == type.feature &&
                    replace == type.replace;
        }

        @Override
        public int hashCode() {
            return Objects.hash(feature, replace);
        }
    }

    public static void main(String[] args) {
        String str = FileUtil.read("/Users/xiyaoguo/Desktop/jdk_result").get(0);
        List<Integer> ids = Arrays.stream(str.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        System.out.println(ids.size());
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Integer id : ids) {
            countMap.put(id, countMap.getOrDefault(id, 0) + 1);
        }
        Map<Type, Integer> map = new HashMap<>();
        IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();
        for (Integer id : countMap.keySet()) {
            JDKDeprecatedAPI api = service.getById(id);
            int featureType = api.getFeatureType();
            if(api.getId() == 15){
                Type type = new Type(featureType, 1);
                map.put(type, map.getOrDefault(type, 0) + countMap.get(id));
                continue;
            }
            Replace replace = JSONObject.parseObject(api.getReplace(), Replace.class);

            if (replace.getConfidence() == null)
                System.out.println(api.getId() + " has not confidence");
            int confidence = replace.getConfidence();
            int rp = (confidence == 3 || confidence == 5) ? 1 : 2;
            Type type = new Type(featureType, rp);
            map.put(type, map.getOrDefault(type, 0) + countMap.get(id));
        }

        for(Type type : map.keySet()){
            System.out.println("feature : " + type.feature + ", replace : " + type.replace + ", count : " + map.get(type));
        }

    }

}
