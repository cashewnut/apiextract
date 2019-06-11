package pers.xyy.deprecatedapi.jdk.tools.classify;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiyaoguo
 * 弃用API原因分析工具。
 * 共有7种分类。每种分类下还有转移/非转移的子分类。
 * 转移：R_API在JDK7中已经出现，对于D_API的替换只是进行转移操作。
 * 非转移：R_API在JDK7中没有出现，在JDK8中才出现。
 * 1、API去重/替换。
 * 2、开放/扩展参数。
 * 3、缩减/关闭参数。
 * 4、参数整合(转移)。
 * 5、API整合(转移)。
 * 6、API拆解(转移)。
 * 7、其他。
 */
public class APIClassifyTool4 {

    public static IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();
    private final static float threshold = Float.parseFloat(LoadProperties.get("API_EQUALS_THRESHOLD"));
    private final static Set<Integer> compactArgs = Arrays.stream(LoadProperties.get("COMPACT_ARGS").split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    private final static Set<Integer> setDInvokeR = Arrays.stream(LoadProperties.get("D_INVOKE_R").split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    private final static Set<Integer> setRInvokeD = Arrays.stream(LoadProperties.get("R_INVOKE_D").split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    private final static Set<Integer> setDEqualsR = Arrays.stream(LoadProperties.get("D_EQUALS_R").split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    private final static Set<Integer> duplicatedSet = Arrays.stream(LoadProperties.get("DUP_API").split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    private final static Set<Integer> openArgs = Arrays.stream(LoadProperties.get("OPEN_ARGS").split(",")).map(Integer::parseInt).collect(Collectors.toSet());
    private final static Set<Integer> notFound = Arrays.stream(LoadProperties.get("NOT_FOUND").split(",")).map(Integer::parseInt).collect(Collectors.toSet());

    public static void main(String[] args) {

        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);


        APIClassifyTool4 tool = new APIClassifyTool4();
//        for (int id : Arrays.stream("16,17,19,128,129,313,314,330,331,356,360,361,363,364,366,405,418".split(",")).map(Integer::parseInt).collect(Collectors.toList())) {
//            //for (int id : Arrays.stream("128".split(",")).map(Integer::parseInt).collect(Collectors.toList())) {
//            JDKDeprecatedAPI api = service.getById(id);
//            Method rAPI = new Method(api.getrPackageName(), api.getrClassName(), api.getrMethodName(), api.getrReturnType(), api.getrMethodArgs());
//            Method dAPI = new Method(api.getPackageName(), api.getClassName(), api.getMethodName(), api.getMethodReturnType(), api.getMethodArgs());
//            System.out.println("id : " + id + ", ret : " + tool.isRInvokeD(rAPI, dAPI));
//        }
        tool.classify();


        /*APIClassifyTool4 tool = new APIClassifyTool4();
        Method dAPI = new Method("java.awt", "Component", "enable", "void", "");
        Method rAPI = new Method("java.awt", "Component", "setEnabled", "void", "boolean");
        System.out.println(tool.isRInvokeD(rAPI, dAPI));
        JDKDeprecatedAPI api = new JDKDeprecatedAPI();
        api.setrPackageName("java.awt");
        api.setrClassName("Component");
        api.setrMethodName("setEnabled");
        api.setrReturnType("void");
        api.setrMethodArgs("boolean");
        System.out.println(tool.isCompactAPI(api));
        MethodDeclaration md = tool.getMDFromCU(FileUtil.openCU(tool.toURL("java.awt", "Component")), "enable", "void", "");
        System.out.println(md.getTypeAsString());*/

    }

    public void classify() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        int count = 0;
        int noReplace = 0;
        int[] nums = new int[9];
        for (JDKDeprecatedAPI api : apis) {
//            if (api.getId() != 79)
//                continue;
            Set<Integer> splitAPI = new HashSet<>(Arrays.asList(15, 93, 285));
            if (splitAPI.contains(api.getId())) {
                System.out.println("id : " + api.getId() + ", type : " + 61);
                nums[6]++;
                continue;
            }
            if (api.getrPackageName() == null) {
                noReplace++;
                continue;
            }
            System.out.print("id : " + api.getId());
            int type = type(api);
            nums[type / 10]++;

            System.out.println(", type : " + type);


        }

        Arrays.stream(nums).forEach(System.out::println);
        System.out.println(424 - noReplace);
    }

    private int type(JDKDeprecatedAPI api) {
        Method rAPI = new Method(api.getrPackageName(), api.getrClassName(), api.getrMethodName(), api.getrReturnType(), api.getrMethodArgs());
        Method dAPI = new Method(api.getPackageName(), api.getClassName(), api.getMethodName(), api.getMethodReturnType(), api.getMethodArgs());
        int transfer = isMethodExist(rAPI) ? 1 : 2;
        if (api.getId() == 416 || api.getId() == 26)
            transfer = 1;
        int type = 8;
        if (api.getId() == 93 || api.getId() == 285 || api.getId() == 15) {
            type = 6;
        } else if (api.getId() == 20) {
            return 11;
        }
//        else if (!typeEquals(api.getMethodReturnType(), api.getrReturnType())) {
//            type = 7;
//        }
        else if (notFound.contains(api.getId())) {
            type = 8;
        } else if (duplicatedSet.contains(api.getId())) {
            type = 1;
        } else if (openArgs.contains(api.getId())) {
            type = 2;
        } else if (setDInvokeR.contains(api.getId())) { //deprecated API调用replaced API
            if (compactArgs.contains(api.getId()))
                type = 4;
            else if (isDuplicated(dAPI, rAPI)) {
//                if (typeEquals(dAPI.getReturnType(), rAPI.getReturnType())) //如果返回值也相同，则返回1，返回值不同返回7。
                    type = 1;
//                else type = 7;
            } else if (isCloseArgs(dAPI, rAPI)) {
                type = 3;
            } else if (isOpenArgs(dAPI, rAPI)) {
                type = 2;
            }
        } else if (setRInvokeD.contains(api.getId())) {
            if (compactArgs.contains(api.getId()))
                type = 4;
            else if (isDuplicated(dAPI, rAPI)) {
//                if (typeEquals(dAPI.getReturnType(), rAPI.getReturnType()))
                    type = 1;
//                else type = 7;
            } else if (isCompactAPI(api)) {
                type = 5;
            } else if (isOpenArgs(rAPI, dAPI)) {
                type = 3;
            } else if (isCloseArgs(rAPI, dAPI)) {
                type = 2;
            }
        } else if (setDEqualsR.contains(api.getId())) {
            if (compactArgs.contains(api.getId()))
                type = 4;
//            else if (!typeEquals(dAPI.getReturnType(), rAPI.getReturnType())) {
//                type = 7;
//            }
            else if (isDuplicated(dAPI, rAPI)) {
                if (typeEquals(dAPI.getReturnType(), rAPI.getReturnType()))
                    type = 1;
            } else {
                if (isCloseArgs4DER(dAPI, rAPI))
                    type = 3;
                else
                    type = 2;
            }

        }
        return type * 10 + transfer;
    }

    /**
     * 如果D中有参数没有被R使用，则说明是关闭参数
     *
     * @param dAPI
     * @param rAPI
     * @return
     */
    private boolean isCloseArgs(Method dAPI, Method rAPI) {
        MethodDeclaration md = getMDFromCU(FileUtil.openCU(toURL(dAPI.getPackageName(), dAPI.getClassName())), dAPI.getName(), dAPI.getReturnType(), dAPI.getMethodArgs());
        List<String> dAPIArgsNames = new ArrayList<>();
        assert md != null;
        if (md.getParameters() != null) {
            for (int i = 0; i < md.getParameters().size(); i++)
                dAPIArgsNames.add(md.getParameter(i).getNameAsString());
        }
        MethodCallExpr rmc = null;
        for (MethodCallExpr mc : md.findAll(MethodCallExpr.class)) {
            if (mc.getNameAsString().equals(rAPI.getName()) && (mc.getArguments().size() == 0 && rAPI.getMethodArgs().isEmpty() || mc.getArguments().size() == rAPI.getMethodArgs().split(",").length)) {
                rmc = mc;
                break;
            }
        }
        if (rmc == null) {
            System.out.println("Error : can't found this mc in close!!");
            return false;
        }
        Set<String> rAPIArgsNames = new HashSet<>();
        if (rmc.getArguments() != null) {
            for (Expression name : rmc.getArguments())
                rAPIArgsNames.add(name.toString());
        }
        for (String name : dAPIArgsNames) {
            if (!rAPIArgsNames.contains(name))
                return true;
        }
        return false;
    }


    //只针对D调R的情况，判断是否是开放参数的情况。
    private boolean isOpenArgs(Method dAPI, Method rAPI) {
        MethodDeclaration md = getMDFromCU(FileUtil.openCU(toURL(dAPI.getPackageName(), dAPI.getClassName())), dAPI.getName(), dAPI.getReturnType(), dAPI.getMethodArgs());
        Set<String> dAPIArgsNames = new HashSet<>();
        assert md != null;
        if (md.getParameters() != null) {
            for (int i = 0; i < md.getParameters().size(); i++)
                dAPIArgsNames.add(md.getParameter(i).getNameAsString());
        }
        MethodCallExpr rmc = null;
        for (MethodCallExpr mc : md.findAll(MethodCallExpr.class)) {
            if (mc.getNameAsString().equals(rAPI.getName()) && (mc.getArguments().size() == 0 && rAPI.getMethodArgs().isEmpty() || mc.getArguments().size() == rAPI.getMethodArgs().split(",").length)) {
                rmc = mc;
                break;
            }
        }
        if (rmc == null) {
            System.out.println("Error : can't found this mc in open!!");
            return false;
        }
        List<String> rAPIArgsNames = new ArrayList<>();
        if (rmc.getArguments() != null) {
            for (Expression name : rmc.getArguments())
                rAPIArgsNames.add(name.toString());
        }
        for (String name : rAPIArgsNames) {
            if (!dAPIArgsNames.contains(name))
                return true;
        }
        return false;

    }

    private boolean isCloseArgs4DER(Method dAPI, Method rAPI) {
        int num1 = dAPI.getMethodArgs().isEmpty() ? 0 : dAPI.getMethodArgs().split(",").length;
        int num2 = rAPI.getMethodArgs().isEmpty() ? 0 : dAPI.getMethodArgs().split(",").length;
        return num1 >= num2;
    }


    //为了防止父子类继承关系，只从方法名、参数、返回值进行校验
    private boolean isCompactAPI(JDKDeprecatedAPI api) {
        List<JDKDeprecatedAPI> apis = service.getByReplaced(api);
        Set<String> set = new HashSet<>();
        int ret = 0;
        for (JDKDeprecatedAPI api1 : apis) {
            String signature = api1.getMethodName() + "_" + api1.getMethodArgs() + "_" + api1.getMethodReturnType();
            if (!set.contains(signature)) {
                set.add(signature);
                ret++;
            }
        }
        return ret >= 2;
    }

    /**
     * R_API是否等同于D_API,起名字太难了
     * 两种策略：
     * 1、返回值不是void，返回值相同。此处只考虑return methodcall的情况。
     * 2、1不成立的情况下比较两者的方法调用，只要有大量相同的方法调用则认为相同。(2 * same / (r + d)) > threshold
     *
     * @param dAPI
     * @param rAPI
     * @return
     */
    private boolean isREqualsD(Method dAPI, Method rAPI) {
        MethodDeclaration dmd = getMDFromCU(FileUtil.openCU(toURL(dAPI.getPackageName(), dAPI.getClassName())), dAPI.getName(), dAPI.getReturnType(), dAPI.getMethodArgs());
        MethodDeclaration rmd = getMDFromCU(FileUtil.openCU(toURL(rAPI.getPackageName(), rAPI.getClassName())), rAPI.getName(), rAPI.getReturnType(), rAPI.getMethodArgs());
        if (dmd == null) {
            System.out.println(dAPI.getName() + " is null!");
            return false;
        }
        if (rmd == null) {
            System.out.println(rAPI.getName() + " is null!");
            return false;
        }
        if (!dmd.getTypeAsString().equals("void") && !rmd.getTypeAsString().equals("void")) {
            if (dmd.findAll(ReturnStmt.class).size() == 1 && rmd.findAll(ReturnStmt.class).size() == 1) {
                ReturnStmt drt = dmd.findAll(ReturnStmt.class).get(0);
                ReturnStmt rrt = rmd.findAll(ReturnStmt.class).get(0);
                if (drt.getExpression().get().toString().equals(rrt.getExpression().get().toString()))
                    return true;
                if (drt.getExpression().get() instanceof MethodCallExpr && rrt.getExpression().get() instanceof MethodCallExpr) {
                    MethodCallExpr dmc = drt.getExpression().get().asMethodCallExpr();
                    MethodCallExpr rmc = rrt.getExpression().get().asMethodCallExpr();
                    if (equals(dmc, rmc))
                        return true;
                }
            }
        }
        //调用方法相似
        List<MethodCallExpr> dmcs = dmd.findAll(MethodCallExpr.class);
        List<MethodCallExpr> rmcs = rmd.findAll(MethodCallExpr.class);
        if (dmcs.size() == 0 || rmcs.size() == 0)
            return false;
        int sc = 0;
        for (MethodCallExpr dmc : dmcs) {
            for (MethodCallExpr rmc : rmcs) {
                if (equals(dmc, rmc))
                    sc++;
            }
        }
        return (float) sc * 2 / (dmcs.size() + rmcs.size()) > threshold;

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

    private boolean isDuplicated(Method dAPI, Method rAPI) {
        return typeEquals(dAPI.getMethodArgs().split("\\."), rAPI.getMethodArgs().split("\\."));
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
        if ((mc.getArguments() == null || mc.getArguments().size() == 0) && (method.getMethodArgs() == null || method.getMethodArgs().isEmpty()))
            return true;
        if ((mc.getArguments() == null || mc.getArguments().size() == 0) || (method.getMethodArgs() == null || method.getMethodArgs().isEmpty()))
            return false;
        if (mc.getArguments().size() != method.getMethodArgs().split(",").length)
            return false;

        return true;
    }

    private boolean equals(MethodCallExpr mc1, MethodCallExpr mc2) {
        String name1 = mc1.getNameAsString();
        String name2 = mc2.getNameAsString();
        if (!name1.equals(name2))
            return false;
        if (mc1.getArguments() == null && mc2.getArguments() == null)
            return true;
        if (mc1.getArguments() == null || mc2.getArguments() == null)
            return false;
        return mc1.getArguments().size() == mc2.getArguments().size();
    }

    private String toURL(String packageName, String className) {
        if (className.equals("LightweightDispatcher"))
            className = "Container";
        StringBuilder sb = new StringBuilder(LoadProperties.get("JDK8_PATH"));
        sb.append(packageName.replace(".", "/")).append("/");
        if (className.contains(".")) {
            String[] clazzs = className.split("\\.");
            className = clazzs[0];
        }
        sb.append(className).append(".java");
        return sb.toString();
    }

    private String toJDK7URL(String packageName, String className) {
        if (className.equals("LightweightDispatcher"))
            className = "Container";
        StringBuilder sb = new StringBuilder(LoadProperties.get("JDK7_PATH"));
        sb.append(packageName.replace(".", "/")).append("/");
        if (className.contains(".")) {
            String[] clazzs = className.split("\\.");
            className = clazzs[0];
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
        ClassOrInterfaceDeclaration cid = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
        for (MethodDeclaration md : mds) {
            if (!md.getNameAsString().equals(methodName))
                continue;
            if (!typeEquals(md.getTypeAsString(), returnType))
                continue;
            if ((args == null || args.isEmpty()) && (md.getParameters() == null || md.getParameters().size() == 0) && md.getParentNode().get().equals(cid))
                return md;
            if (args != null && md.getParameters() != null) {
                String[] cmpArgs1 = args.split(",");
                String[] cmpArgs2 = new String[md.getParameters().size()];
                for (int i = 0; i < md.getParameters().size(); i++)
                    cmpArgs2[i] = md.getParameter(i).getTypeAsString();
                if (typeEquals(cmpArgs1, cmpArgs2)) {
                    if (md.getParentNode().get().equals(cid))
                        return md;
                }

            }
        }
        return null;
    }

    //jdk7中是否存在这个api，用于判断是否是"转移"
    private boolean isMethodExist(Method method) {
        CompilationUnit cu = FileUtil.openCU(toJDK7URL(method.getPackageName(), method.getClassName()));
        return getMDFromCU(cu, method.getName(), method.getReturnType(), method.getMethodArgs()) != null;
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

        //TODO 判断两个类是否是父子类，暂时写死，有时间重新写
        Map<String, Set<String>> map = new HashMap<>();
        map.put("JComponent", new HashSet<>(Arrays.asList("JMenuBar", "JScrollPane")));
        String[] str1s = str1.split("\\.");
        String[] str2s = str2.split("\\.");
        String s1 = str1s[str1s.length - 1], s2 = str2s[str2s.length - 1];
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
