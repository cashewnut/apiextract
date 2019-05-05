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
        dao.updateById(jdkDeprecatedAPI);
    }

    @Override
    public void updateById(List<JDKDeprecatedAPI> jdkDeprecatedAPIS) {
        dao.updateById(jdkDeprecatedAPIS);
    }

    @Override
    public void updateArgs(JDKDeprecatedAPI jdkDeprecatedAPI, String args) {
        dao.updateArgs(jdkDeprecatedAPI, args);
    }

    @Override
    public JDKDeprecatedAPI get(JDKDeprecatedAPI jdkDeprecatedAPI) {
        JDKDeprecatedAPI api = dao.get(jdkDeprecatedAPI);
        if (api == null && jdkDeprecatedAPI.getMethodReturnType().contains(".")) {
            String[] types = jdkDeprecatedAPI.getMethodReturnType().split("[.]");
            jdkDeprecatedAPI.setMethodReturnType(types[types.length - 1]);
            api = dao.get(jdkDeprecatedAPI);
        }
        return api;
    }
}
