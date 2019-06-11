package pers.xyy.deprecatedapi.study;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
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
import pers.xyy.deprecatedapi.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectStudy {

    private List<String> javaFilesPath;
    private final static IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();
    private List<JDKDeprecatedAPI> apis;

    static {
        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
    }


    public Set<String> methodNameSet;

    public ProjectStudy(String projectPath) {
        this.javaFilesPath = FileUtil.getJavaFilePath(new File(projectPath));
        this.apis = service.getJDKDeprecatedAPIs();
        this.methodNameSet = getMethodNameSet();
    }

    public List<String> getJavaFilesPath() {
        return javaFilesPath;
    }

    public int invokeCount() {
        int count = 0;
        for (String path : javaFilesPath) {
            try {
                CompilationUnit cu = FileUtil.openCU(path);
                List<MethodCallExpr> mcs = cu.findAll(MethodCallExpr.class);
                for (MethodCallExpr 我现在就要用中文命名这个变量名以表示我现在多郁闷 : mcs) {
                    if (isDeprecated(我现在就要用中文命名这个变量名以表示我现在多郁闷))
                        count++;
                }
            }catch (Exception e){

            }

        }
        return count;
    }

    private boolean isDeprecated(MethodCallExpr mc) {
        try {
            if (!methodNameSet.contains(mc.getNameAsString()))
                return false;
            List<JDKDeprecatedAPI> candidates = getByMethodName(mc.getNameAsString());
            System.out.println(candidates.size());
            ResolvedMethodDeclaration rmd = mc.resolveInvokedMethod();
            String type = rmd.getReturnType().describe();
            String className = rmd.getClassName();
            String pgName = rmd.getPackageName();
            int paramsCount = rmd.getNumberOfParams();
            String params = "";
            for (int i = 0; i < paramsCount; i++) {
                if (i == paramsCount - 1)
                    params += rmd.getParam(i).describeType();
                else
                    params = params + rmd.getParam(i).describeType() + ",";
            }

            for (JDKDeprecatedAPI api : candidates) {
                if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","),params.split(","))) {
                    System.out.println(api.getId());
                    return true;
                }

            }
            return false;
        } catch (Exception e) {

        }
        return false;
    }

    public List<JDKDeprecatedAPI> getByMethodName(String name) {
        List<JDKDeprecatedAPI> ret = new ArrayList<>();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getMethodName().equals(name))
                ret.add(api);
        }
        return ret;
    }

    public Set<String> getMethodNameSet() {
        Set<String> set = new HashSet<>();
        for (JDKDeprecatedAPI api : apis) {
            set.add(api.getMethodName());
        }
        return set;
    }

    public static void main(String[] args) {
        ProjectStudy projectStudy = new ProjectStudy("/home/fdse/xyy/study/openjdk-jdk-jdk");
        System.out.println("The project has " + projectStudy.getJavaFilesPath().size() + "classes!");
        System.out.println("count : " + projectStudy.invokeCount());

    }


}
