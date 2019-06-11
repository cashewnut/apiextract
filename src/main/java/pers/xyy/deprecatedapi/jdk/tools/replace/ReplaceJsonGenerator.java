package pers.xyy.deprecatedapi.jdk.tools.replace;

import com.alibaba.fastjson.JSON;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.tools.replace.model.Args;
import pers.xyy.deprecatedapi.jdk.tools.replace.model.Method;
import pers.xyy.deprecatedapi.jdk.tools.replace.model.Replace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReplaceJsonGenerator {

    private IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public void generate() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getType() == 0)
                continue;
            if (api.getFeatureType() == 8 && api.getConfidenceType() != 1) {
                //f8c23(api);
            }
            if (api.getFeatureType() == 4 && api.getConfidenceType() == 2) {
                //f4c2(api);
            }
            if (api.getFeatureType() == 6) {
                //f6(api);
            }
            if(api.getFeatureType() == 2 && api.getConfidenceType() != 1){
                //f2c23(api);
            }
            if(api.getFeatureType() == 1){
                f1(api);
            }

        }
    }

    public void f8c23(JDKDeprecatedAPI api) {
        Replace replace = new Replace();
        replace.addComment("Return value type changed! (" + api.getMethodReturnType() + ") -> (" + api.getrReturnType() + ")");
        replace.setConfidence(1);
        replace.setOperations(Collections.singletonList("$m0"));
        Method method = new Method();
        if (api.getType() == 3)
            method.setInvoker(api.getrInvoker());
        if (!api.getrMethodArgs().equals("")) {
            List<Args> argsList = new ArrayList<>();
            for (String argsType : api.getrMethodArgs().split(",")) {
                Args args = new Args();
                args.setName(argsType);
                argsList.add(args);
            }
            method.setArgs(argsList);
        }
        method.setName(api.getrMethodName());
        replace.setMethods(Collections.singletonList(method));
        api.setReplace(JSON.toJSONString(replace));
        service.updateById(api);
    }

    public void f4c2(JDKDeprecatedAPI api) {
        Replace replace = new Replace();
        replace.setConfidence(2);
        replace.setOperations(Collections.singletonList("$m0"));
        Method method = new Method();
        if (api.getType() == 3)
            method.setInvoker(api.getrInvoker());
        if (!api.getrMethodArgs().equals("")) {
            List<Args> argsList = new ArrayList<>();
            for (String argsType : api.getrMethodArgs().split(",")) {
                Args args = new Args();
                args.setName(argsType);
                argsList.add(args);
            }
            method.setArgs(argsList);
        }
        method.setName(api.getrMethodName());
        replace.setMethods(Collections.singletonList(method));
        api.setReplace(JSON.toJSONString(replace));
        service.updateById(api);
    }

    public void f6(JDKDeprecatedAPI api) {
        if (api.getConfidenceType() != 1 || api.getReplace() != null)
            return;

        Replace replace = new Replace();
        replace.setConfidence(5);
        replace.setOperations(Collections.singletonList("$m0"));
        Method method = new Method();
        method.setInvoker(api.getrInvoker());
        method.setName(api.getrMethodName());
        replace.setMethods(Collections.singletonList(method));
        api.setReplace(JSON.toJSONString(replace));
        service.updateById(api);
    }

    public void f2c23(JDKDeprecatedAPI api) {
        Replace replace = new Replace();
        replace.setConfidence(2);
        replace.setOperations(Collections.singletonList("$m0"));
        Method method = new Method();
        if (!api.getrMethodArgs().equals("")) {
            List<Args> argsList = new ArrayList<>();
            for (String argsType : api.getrMethodArgs().split(",")) {
                Args args = new Args();
                args.setName(argsType);
                argsList.add(args);
            }
            method.setArgs(argsList);
        }
        method.setName(api.getrMethodName());
        replace.setMethods(Collections.singletonList(method));
        api.setReplace(JSON.toJSONString(replace));
        service.updateById(api);
    }

    public void f1(JDKDeprecatedAPI api){
        Replace replace = new Replace();
        if(api.getConfidenceType() == 1)
            replace.setConfidence(5);
        else
            replace.setConfidence(3);
        Method method = new Method();
        if(api.getType() != 1)
            method.setInvoker(api.getrClassName());
        method.setName(api.getrMethodName());
        replace.setMethods(Collections.singletonList(method));
        api.setReplace(JSON.toJSONString(replace));
        service.updateById(api);
    }


    public static void main(String[] args) {
        new ReplaceJsonGenerator().generate();
    }


}
//"comments":["Return value type changed! (XMLInputFactory) -> (XMLOutputFactory)"],