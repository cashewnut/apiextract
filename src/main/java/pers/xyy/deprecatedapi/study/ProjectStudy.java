package pers.xyy.deprecatedapi.study;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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
import java.util.*;
import java.util.stream.Collectors;

public class ProjectStudy {

    private List<String> javaFilesPath;
    private final static IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();
    private List<JDKDeprecatedAPI> apis;
    private List<Integer> ids = new ArrayList<>();

    static {
        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
    }


    public Set<String> methodNameSet;

    public ProjectStudy() {
    }

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
        System.out.println("This file has " + javaFilesPath.size() + " classes");
        for (String path : javaFilesPath) {
            try {
                CompilationUnit cu = FileUtil.openCU(path);
                List<MethodCallExpr> mcs = cu.findAll(MethodCallExpr.class);
                for (MethodCallExpr 我现在就要用中文命名这个变量名以表示我现在多郁闷 : mcs) {
                    if (isDeprecated(我现在就要用中文命名这个变量名以表示我现在多郁闷))
                        count++;
                }
            } catch (Exception e) {

            } catch (StackOverflowError error) {

            }

        }
        return count;
    }

    private boolean isDeprecated(MethodCallExpr mc) {
        try {
            if (!methodNameSet.contains(mc.getNameAsString()))
                return false;
            List<JDKDeprecatedAPI> candidates = getByMethodName(mc.getNameAsString());
            //System.out.println(candidates.size());
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
                if (api.getType() == 0)
                    continue;
                if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","), params.split(","))) {
                    //FileUtil.write("/home/fdse/xyy/study/ids", api.getId() + "");
                    FileUtil.write("/home/fdse/xiyaoguo/out.txt", api.getId() + ",");
                    System.out.print(api.getId() + ",");
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

    public void run() {
        List<Project> projects = new StudyService().getProjects();
        for (Project project : projects) {
            try {
                System.out.println("The project's id is " + project.getId());
                ProjectStudy projectStudy = new ProjectStudy(project.getLocalAddress());
                System.out.println(project.getLocalAddress());
                System.out.println("The project has " + projectStudy.getJavaFilesPath().size() + "classes!");
                if (projectStudy.getJavaFilesPath().size() > 2000)
                    continue;
                projectStudy.invokeCount();
            } catch (StackOverflowError e) {

            }
        }
    }


    public List<Integer> getIds() {
        return ids;
    }

    public void run2() {
        ProjectStudy projectStudy = new ProjectStudy("/Users/xiyaoguo/Desktop/code");
        System.out.println(projectStudy.invokeCount());
        String idsString = projectStudy.getIds().stream().map(Object::toString).collect(Collectors.joining(","));
        System.out.println("ids : " + idsString);
        //FileUtil.write("/home/fdse/xyy/study/ids", idsString);
    }

    public static void main(String[] args) {
        new ProjectStudy().run();


    }
//find-sec-bugs

}
