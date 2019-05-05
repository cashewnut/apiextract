package pers.xyy.deprecatedapi.jdk;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类用于定位替换方法的包，类，方法，参数
 */
public class ReplaceMethodLocation {

    private static IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public boolean equals(String[] l1, String[] l2) {
        if (l1.length != l2.length)
            return false;
        boolean res = true;
        for (int i = 0; i < l1.length; i++) {
            String args1 = l1[i];
            String args2 = l2[i];
            res &= equals(args1, args2);
        }
        return res;
    }

    public boolean equals(String str1, String str2) {
        if (str1.isEmpty() && str2.isEmpty())
            return true;
        if (str1.isEmpty() || str2.isEmpty())
            return false;
        return str1.split("[.]")[str1.split("[.]").length - 1].equals(str2.split("[.]")[str2.split("[.]").length - 1]);
    }

    /**
     * 从Java文件中找到qs所代表的方法
     *
     * @param cu Java文件
     * @param qs 方法签名,get(int,int)
     * @return
     */
    public MethodDeclaration getTarget(CompilationUnit cu, String qs) {
        MethodDeclaration target = null;
        List<MethodDeclaration> mds = cu.findAll(MethodDeclaration.class);
        int index = qs.indexOf("(");
        String rMethodName = index == -1 ? qs : qs.substring(0, index);
        for (MethodDeclaration md : mds) {
            if (md.getNameAsString().equals(rMethodName)) {
                if (index == -1) {// getPassword
                    target = md;
                    break;
                } else { //get()
                    String param = qs.substring(index + 1, qs.indexOf(")")).replace(" ", "");
                    if (param.isEmpty()) { //get()
                        if (md.getParameters().size() == 0) {
                            target = md;
                            break;
                        }
                    } else { //get(int)
                        String[] params = param.split(","); //注释中的参数
                        ResolvedMethodDeclaration rmd = md.resolve();
                        String[] rMethodParamNums = new String[rmd.getNumberOfParams()];
                        try {
                            for (int i = 0; i < rMethodParamNums.length; i++)
                                rMethodParamNums[i] = rmd.getParam(i).describeType();
                            if (equals(params, rMethodParamNums)) {
                                target = md;
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return target;
    }

    public static void main(String[] args) {
        //System.out.println("()".substring(1, "()".length() - 1));
        ReplaceMethodLocation rm = new ReplaceMethodLocation();
        // init JavaParser
        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {

            Set<Integer> except = new HashSet<>(Arrays.asList(15, 80, 127, 129, 332, 345, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 285));
            if (except.contains(api.getId()))
                continue;

            if (api.getQualifiedSignature() == null)
                continue;
            StringBuilder url = new StringBuilder("/Users/xiyaoguo/Desktop/src/");
            MethodDeclaration target = null; //target:目标替换方法
            if (api.getQualifiedSignature().startsWith("#")) {// #getThreshold(ObjectName),#setInitThreshold
                String className = api.getClassName();
                if (className.contains("."))
                    className = className.split("[.]")[0];
                url.append(api.getPackageName().replace(".", "/")).append("/").append(className).append(".java");
                CompilationUnit cu = FileUtil.openCU(url.toString());
                String qs = api.getQualifiedSignature().substring(1);
                target = rm.getTarget(cu, qs);
            }
            if (target == null) {
                Pattern p = Pattern.compile("[\\w]+"); //getPassword
                Matcher m = p.matcher(api.getQualifiedSignature());
                if (m.matches()) {
                    String className = api.getClassName();
                    if (className.contains("."))
                        className = className.split("[.]")[0];
                    url = new StringBuilder("/Users/xiyaoguo/Desktop/src/");
                    url.append(api.getPackageName().replace(".", "/")).append("/").append(className).append(".java");
                    CompilationUnit cu = FileUtil.openCU(url.toString());
                    target = rm.getTarget(cu, api.getQualifiedSignature());

                }

                p = Pattern.compile("[\\w]+\\((([\\w]|\\s|\\.)+,?)*\\)");//get(),get(int,int)
                m = p.matcher(api.getQualifiedSignature());
                if (m.matches()) {
                    String rMethodName = api.getQualifiedSignature().substring(0, api.getQualifiedSignature().indexOf("("));
                    //strategy1:替换方法是否在本类中
                    String className = api.getClassName();
                    url = new StringBuilder("/Users/xiyaoguo/Desktop/src/");
                    url.append(api.getPackageName().replace(".", "/")).append("/").append(className).append(".java");
                    CompilationUnit cu = FileUtil.openCU(url.toString());
                    target = rm.getTarget(cu, api.getQualifiedSignature());
                    //strategy2:查弃用方法是否调用了替换方法，找到替换方法的位置
                    if (target == null) {
                        List<MethodDeclaration> mds = cu.findAll(MethodDeclaration.class);
                        MethodDeclaration deprecatedAPI = null;
                        for (MethodDeclaration md : mds) {
                            if (md.getNameAsString().equals(api.getMethodName())) {
                                ResolvedMethodDeclaration rmd = md.resolve();
                                String rArgs = "";
                                try {
                                    for (int i = 0; i < rmd.getNumberOfParams(); i++)
                                        rArgs = rArgs + rmd.getParam(i).describeType() + ",";
                                    if (rArgs.endsWith(","))
                                        rArgs = rArgs.substring(0, rArgs.length() - 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (rArgs.equals(api.getMethodArgs())) {
                                    deprecatedAPI = md;
                                    break;
                                }
                            }
                        }
                        if (deprecatedAPI != null) {
                            List<MethodCallExpr> mcs = deprecatedAPI.findAll(MethodCallExpr.class);
                            for (MethodCallExpr mc : mcs) {
                                ResolvedMethodDeclaration rmd = mc.resolveInvokedMethod();
                                if (rmd.getName().equals(rMethodName)) {
                                    String rPackageName = rmd.getPackageName();
                                    String rClassName = rmd.getClassName();
                                    String rType = rmd.getReturnType().describe();
                                    String[] rArgs = new String[rmd.getNumberOfParams()];
                                    try {
                                        for (int i = 0; i < rmd.getNumberOfParams(); i++)
                                            rArgs[i] = rmd.getParam(i).describeType();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (rClassName.contains("."))
                                        rClassName = rClassName.split("[.]")[0];
                                    url = new StringBuilder("/Users/xiyaoguo/Desktop/src/");
                                    url.append(rPackageName.replace(".", "/")).append("/").append(rClassName).append(".java");
                                    for (MethodDeclaration md : FileUtil.openCU(url.toString()).findAll(MethodDeclaration.class)) {
                                        if (md.getNameAsString().equals(rMethodName)) {
                                            String[] params = new String[md.getParameters().size()];
                                            for (int i = 0; i < params.length; i++)
                                                params[i] = md.getParameter(i).getType().asString();
                                            if (rm.equals(rType, md.getTypeAsString()) && rm.equals(rArgs, params)) {
                                                target = md;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //strategy3:查弃用方法所在类的父类
                    //不需要查了，没这种情况
                }

            }
            if (target == null) { //View#modelToView,java.awt.GraphicsEnvironment#getAllFonts
                if (api.getQualifiedSignature().contains("#") && !api.getQualifiedSignature().startsWith("#")) {
                    String rClassName = api.getQualifiedSignature().split("#")[0];
                    String rMethodName = api.getQualifiedSignature().split("#")[1];
                    url = new StringBuilder("/Users/xiyaoguo/Desktop/src/");
                    if (rClassName.contains("."))
                        url.append(rClassName.replace(".", "/")).append(".java");
                    else
                        url.append(api.getPackageName().replace(".", "/")).append("/").append(rClassName).append(".java");
                    CompilationUnit cu = FileUtil.openCU(url.toString());
                    target = rm.getTarget(cu, rMethodName);
                }
            }
            if (target == null) {// KeyboardFocusManager.getFocusOwner(),javax.xml.soap.SOAPFactory.createElement(javax.xml.soap.Name)
                Pattern p = Pattern.compile("[\\w|.]+\\.[\\w]+\\((([\\w]|\\s|\\.)+,?)*\\)");
                Matcher m = p.matcher(api.getQualifiedSignature());
                if (m.matches()) {
                    //temp = javax.xml.soap.SOAPFactory.createElement
                    String temp = api.getQualifiedSignature().substring(0, api.getQualifiedSignature().indexOf("("));
                    String rMethodName = api.getQualifiedSignature().substring(temp.lastIndexOf(".") + 1);
                    String rClassName = api.getQualifiedSignature().substring(0, temp.lastIndexOf("."));
                    url = new StringBuilder("/Users/xiyaoguo/Desktop/src/");
                    if (rClassName.contains("."))
                        url.append(rClassName.replace(".", "/")).append(".java");
                    else
                        url.append(api.getPackageName().replace(".", "/")).append("/").append(rClassName).append(".java");
                    CompilationUnit cu = FileUtil.openCU(url.toString());
                    target = rm.getTarget(cu, rMethodName);
                }
            }
            if (target == null)
                continue;
            ResolvedMethodDeclaration rmd = target.resolve();
            api.setrPackageName(rmd.getPackageName());
            api.setrClassName(rmd.getClassName());
            api.setrReturnType(target.getType().asString());
            api.setrMethodName(target.getNameAsString());
            System.out.println(api.getId());
            String rArgs = "";
            try {
                for (int i = 0; i < rmd.getNumberOfParams(); i++)
                    rArgs = rArgs + rmd.getParam(i).describeType() + ",";
                if (rArgs.endsWith(","))
                    rArgs = rArgs.substring(0, rArgs.length() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            api.setrMethodArgs(rArgs);
            service.updateById(api);

        }
    }

}
