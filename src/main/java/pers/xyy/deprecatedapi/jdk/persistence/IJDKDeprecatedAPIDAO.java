package pers.xyy.deprecatedapi.jdk.persistence;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;

import java.util.List;

public interface IJDKDeprecatedAPIDAO {

    void insert(JDKDeprecatedAPI jdkDeprecatedAPI);

    List<JDKDeprecatedAPI> getJDKDeprecatedAPIs();

    void updateById(JDKDeprecatedAPI jdkDeprecatedAPI);

    void updateById(List<JDKDeprecatedAPI> jdkDeprecatedAPIS);

    JDKDeprecatedAPI get(JDKDeprecatedAPI jdkDeprecatedAPI);

    void updateArgs(JDKDeprecatedAPI jdkDeprecatedAPI, String args);

    List<JDKDeprecatedAPI> getByReplaced(JDKDeprecatedAPI jdkDeprecatedAPI);

    JDKDeprecatedAPI getById(int id);

}
