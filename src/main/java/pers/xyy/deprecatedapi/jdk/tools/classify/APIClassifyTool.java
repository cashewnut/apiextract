package pers.xyy.deprecatedapi.jdk.tools.classify;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.LoadProperties;

import java.util.List;

/**
 * @author xiyaoguo
 * 弃用API原因分析工具。
 * 1、D_API直接调用了R_API，参数一致，或两者方法体完全相同，或D_API调用了R_API，R_API的invoker是D_API的参数。
 * 分析：D_API和R_API两者功能完全相同，弃用其中一个。
 * 原因：API重复。
 * 2、R_API调用D_API，参数一致。
 * 分析：R_API在D_API之前或与D_API同时引入，D_API没有问题但不建议使用。
 * 原因：API命名规范。
 * 3、R_API调用了多个D_API，且这几个D_API的替换API相同。
 * 分析：R_API通过参数选择具体调用哪个D_API(setEnable(),setVisible())，或是多个D_API具有连贯性，不建议分开使用。
 * 原因：API整合。
 * 4、D_API参数比R_API多：D_API调用R_API，或者两者调用了同样的API(48)。参数需要能一一对应。
 * 分析：原先使用D_API时，某个参数是默认配置进R_API的，现在希望使用者按照需要自己配置该参数。
 * 原因：取消默认配置。
 * 5、R_API参数比D_API多：R_API调用了D_API，或两者调用了相同的API。参数需要能一一对应。
 * 分析：不希望使用者使用默认参数，而是使用自定义配置。
 * 原因：希望使用者确定参数。自定义配置。
 * 6、R_API调用D_API，参数不同，但是R_API的参数是对D_API参数的封装(参考例128，129)
 * 分析：R_API的参数是对D_API参数的封装。
 * 原因：参数的封装。
 * 7、无法从代码层面上分析出R_API与D_API的关联。
 * 分析：两个API有相同的功能，但有不同的实现方案。
 * 原因：出于安全/性能/返回值优化(将数组优化成ArrayList)等考虑，无法得出准确的结论。
 */
public class APIClassifyTool {

    public IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public static void main(String[] args) {

        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);


    }

    public void classify() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getReplacedComment() == null)
                continue;
            System.out.println(api.getId() + " : type" + type(api));

        }
    }

    private int type(JDKDeprecatedAPI api) {


        return EnumDeprecatedAPIType.TYPE7.getValue();
    }

    //D_API调用了R_API或者D_API与R_API方法体相同
    private boolean isType1(JDKDeprecatedAPI api) {
        Method rAPI = new Method(api.getrPackageName(), api.getrClassName(), api.getrMethodName(), api.getrReturnType(), api.getrMethodArgs());

        return false;
    }

    /**
     * 判断方法调用与方法声明是否是同一个方法，简易版，只比较方法名和参数个数，
     * 有兴趣的话后续可以改进。
     *
     * @param mc
     * @param md
     * @return
     */
    private boolean equals(MethodCallExpr mc, MethodDeclaration md) {
        //name
        if (!mc.getNameAsString().equals(md.getNameAsString()))
            return false;

        //args
        if (mc.getArguments() == null && md.getParameters() == null)
            return true;
        if (mc.getArguments() == null || md.getParameters() == null)
            return false;
        if (mc.getArguments().size() != md.getParameters().size())
            return false;

        return true;
    }

    private boolean equals(MethodCallExpr mc, Method method) {
        //name
        if (!mc.getNameAsString().equals(method.getName()))
            return false;

        //args
        if (mc.getArguments() == null && (method.getMethodArgs() == null || method.getMethodArgs().isEmpty()))
            return true;
        if (mc.getArguments() == null || method.getMethodArgs() == null)
            return false;
        if (mc.getArguments().size() != method.getMethodArgs().split(",").length)
            return false;

        return true;
    }

    private String toURL(String packageName, String className) {
        StringBuilder sb = new StringBuilder(LoadProperties.get("JDK_PATH"));
        sb.append(packageName.replace(".", "/")).append("/");
        if (className.contains(".")) {
            String[] clazzs = className.split("\\.");
            className = clazzs[clazzs.length - 1];
        }
        sb.append(className);
        return sb.toString();
    }
}
