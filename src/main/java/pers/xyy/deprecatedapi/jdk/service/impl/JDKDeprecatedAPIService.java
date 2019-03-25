package pers.xyy.deprecatedapi.jdk.service.impl;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.persistence.IJDKDeprecatedAPIDAO;
import pers.xyy.deprecatedapi.jdk.persistence.impl.JDKDeprecatedAPIDAO;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;

public class JDKDeprecatedAPIService implements IJDKDeprecatedAPIService {

    private IJDKDeprecatedAPIDAO dao = new JDKDeprecatedAPIDAO();

    @Override
    public void saveJDKDeprecatedAPI(JDKDeprecatedAPI jdkDeprecatedAPI) {
        dao.insert(jdkDeprecatedAPI);
    }
}
