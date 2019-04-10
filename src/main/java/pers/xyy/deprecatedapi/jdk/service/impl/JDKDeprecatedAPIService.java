package pers.xyy.deprecatedapi.jdk.service.impl;

import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.persistence.IJDKDeprecatedAPIDAO;
import pers.xyy.deprecatedapi.jdk.persistence.impl.JDKDeprecatedAPIDAO;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;

import java.util.List;

public class JDKDeprecatedAPIService implements IJDKDeprecatedAPIService {

    private IJDKDeprecatedAPIDAO dao = new JDKDeprecatedAPIDAO();

    @Override
    public void saveJDKDeprecatedAPI(JDKDeprecatedAPI jdkDeprecatedAPI) {
        dao.insert(jdkDeprecatedAPI);
    }

    @Override
    public List<JDKDeprecatedAPI> getJDKDeprecatedAPIs() {
        return dao.getJDKDeprecatedAPIs();
    }

    @Override
    public void updateById(JDKDeprecatedAPI jdkDeprecatedAPI) {
        dao.insert(jdkDeprecatedAPI);
    }

    @Override
    public void updateById(List<JDKDeprecatedAPI> jdkDeprecatedAPIS) {
        dao.updateById(jdkDeprecatedAPIS);
    }
}
