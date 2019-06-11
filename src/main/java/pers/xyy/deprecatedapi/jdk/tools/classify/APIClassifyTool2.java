package pers.xyy.deprecatedapi.jdk.tools.classify;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;
import pers.xyy.deprecatedapi.utils.LoadProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class APIClassifyTool2 {

    public IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public static void main(String[] args) {

        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        APIClassifyTool2 tool = new APIClassifyTool2();
        Method dAPI = new Method("java.awt","Component","enable","void","");
        Method rAPI = new Method("java.awt","Component","setEnabled","void","boolean");
        System.out.println(tool.isRInvokeD(rAPI,dAPI));

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
        Method rAPI = new Method(api.getrPackageName(), api.getrClassName(), api.getrMethodName(), api.getrReturnType(), api.getrMethodArgs());
        Method dAPI = new Method(api.getPackageName(), api.getClassName(), api.getMethodName(), api.getMethodReturnType(), api.getMethodArgs());

        return EnumDeprecatedAPIType2.TYPE7.getValue();
    }

    //D_API调用了R_API或者D_API与R_API方法体相同
    private boolean isType1(Method dAPI, Method rAPI) {
        return false;
    }

    /**
     * dAPI是否调用了rAPI，直接调用或间接调用，如果是间接调用只追溯一次，
     * 后续可以通过过滤来遍历多次。
     *
     * @param dAPI
     * @param rAPI
     * @return
     */
    private boolean isDInvokeR(Method dAPI, Method rAPI) {
        CompilationUnit cu = FileUtil.openCU(toURL(dAPI.getPackageName(), dAPI.getClassName()));
        MethodDeclaration md = getMDFromCU(cu, dAPI.getName(), dAPI.getReturnType(), dAPI.getMethodArgs());
        if (md == null) {
            System.out.println(dAPI.toString() + " can't be found!");
            return false;
        }
        List<MethodCallExpr> mcs = md.findAll(MethodCallExpr.class);
        for (MethodCallExpr mc : mcs) {
            if (equals(mc, rAPI))
                return true;
        }
        //间接调用
        for (MethodCallExpr mc : mcs) {
            try {
                ResolvedMethodDeclaration rmd = mc.resolveInvokedMethod();
                String url = toURL(rmd.getPackageName(), rmd.getClassName());
                CompilationUnit mccu = FileUtil.openCU(url);
                String args;
                if (rmd.getNumberOfParams() == 0)
                    args = null;
                else {
                    List<ResolvedParameterDeclaration> rpd = new ArrayList<>();
                    for (int i = 0; i < rmd.getNumberOfParams(); i++)
                        rpd.add(rmd.getParam(i));
                    args = rpd.stream().map(ResolvedParameterDeclaration::describeType).collect(Collectors.joining(","));
                }
                String returnType = rmd.getReturnType().describe();
                MethodDeclaration mcmd = getMDFromCU(mccu, rmd.getName(), returnType, args);//拿到对应的MethodDeclaration
                List<MethodCallExpr> mcmcs = mcmd.findAll(MethodCallExpr.class);
                for (MethodCallExpr mcmc : mcmcs) {
                    if (equals(mcmc, rAPI))
                        return true;
                }

            } catch (Exception e) {

            }
        }
        return false;
    }

    private boolean isRInvokeD(Method rAPI, Method dAPI) {
        return isDInvokeR(rAPI, dAPI);
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
        sb.append(className).append(".java");
        return sb.toString();
    }

    /**
     * 从CU文件中定位MethodDeclaration
     *
     * @param cu
     * @param methodName
     * @param returnType
     * @param args
     * @return
     */
    private MethodDeclaration getMDFromCU(CompilationUnit cu, String methodName, String returnType, String args) {
        List<MethodDeclaration> mds = cu.findAll(MethodDeclaration.class);
        for (MethodDeclaration md : mds) {
            if (!md.getNameAsString().equals(methodName))
                continue;
            if (!typeEquals(md.getTypeAsString(), returnType))
                continue;
            if ((args == null || args.isEmpty()) && (md.getParameters() == null || md.getParameters().size() == 0))
                return md;
            if (args != null && md.getParameters() != null) {
                String[] cmpArgs1 = args.split(",");
                String[] cmpArgs2 = (String[]) md.getParameters().stream().map(NodeWithType::getTypeAsString).toArray();
                if (typeEquals(cmpArgs1, cmpArgs2))
                    return md;
            }
        }
        return null;
    }

    private boolean typeEquals(String[] l1, String[] l2) {
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

    private boolean typeEquals(String str1, String str2) {
        if (str1.isEmpty() && str2.isEmpty())
            return true;
        if (str1.isEmpty() || str2.isEmpty())
            return false;
        String[] str1s = str1.split("\\.");
        String[] str2s = str2.split("\\.");
        return str1s[str1s.length - 1].equals(str2s[str2s.length - 1]);
    }
}
