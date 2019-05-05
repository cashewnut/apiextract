package pers.xyy.deprecatedapi.jdk.service;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;

import java.util.List;

public interface IJDKDeprecatedAPIService {

    void saveJDKDeprecatedAPI(JDKDeprecatedAPI jdkDeprecatedAPI);

    List<JDKDeprecatedAPI> getJDKDeprecatedAPIs();

    void updateById(JDKDeprecatedAPI jdkDeprecatedAPI);

    void updateById(List<JDKDeprecatedAPI> jdkDeprecatedAPIS);

    void updateArgs(JDKDeprecatedAPI jdkDeprecatedAPI,String args);

    JDKDeprecatedAPI get(JDKDeprecatedAPI jdkDeprecatedAPI);

}
