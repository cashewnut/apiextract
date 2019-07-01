package pers.xyy.deprecatedapi.study;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;
import pers.xyy.deprecatedapi.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProjectStudy {

    private final static String baseURL = "/home/fdse/xiyaoguo/jars/";

//    private final static String jdkOut = "/home/fdse/xiyaoguo/out1000/jdk";
//    private final static String junitOut = "/home/fdse/xiyaoguo/out1000/junit";
//    private final static String guavaOut = "/home/fdse/xiyaoguo/out1000/guava";
//    private final static String ioOut = "/home/fdse/xiyaoguo/out1000/commons-io";
//    private final static String lang3Out = "/home/fdse/xiyaoguo/out1000/commons-lang3";

    private final static String jdkOut = "/home/fdse/xiyaoguo/out500/jdk";
    private final static String junitOut = "/home/fdse/xiyaoguo/out500/junit";
    private final static String guavaOut = "/home/fdse/xiyaoguo/out500/guava";
    private final static String ioOut = "/home/fdse/xiyaoguo/out500/commons-io";
    private final static String lang3Out = "/home/fdse/xiyaoguo/out500/commons-lang3";

    static {
        try {
            TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(),
                    JarTypeSolver.getJarTypeSolver(baseURL + "commons-lang3-3.9.jar"),
                    JarTypeSolver.getJarTypeSolver(baseURL + "commons-io-2.6.jar"),
                    JarTypeSolver.getJarTypeSolver(baseURL + "guava-28.0-jre.jar"),
                    JarTypeSolver.getJarTypeSolver(baseURL + "junit-4.12.jar"));
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
            JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
        } catch (IOException e) {

        }
    }

    private List<JDKDeprecatedAPI> jdks;
    private List<JDKDeprecatedAPI> junits;
    private List<JDKDeprecatedAPI> guavas;
    private List<JDKDeprecatedAPI> ios;
    private List<JDKDeprecatedAPI> lang3s;

    private IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public ProjectStudy() {
        this.jdks = service.getAPIsByDBName("jdk_deprecated_api8");
        this.junits = service.getAPIsByDBName("junit");
        this.guavas = service.getAPIsByDBName("guava");
        this.ios = service.getAPIsByDBName("commons_io");
        this.lang3s = service.getAPIsByDBName("commons_lang3");
    }

    public void analyze(String project) {
        List<String> javaFilePaths = FileUtil.getJavaFilePath(new File(project));
        System.out.println("This project has " + javaFilePaths.size() + " classes");
        if (javaFilePaths.size() > 2000)
            return;
        for (String path : javaFilePaths) {
            try {
//                System.out.println(path);
                CompilationUnit cu = FileUtil.openCU(path);
                List<MethodCallExpr> mcs = cu.findAll(MethodCallExpr.class);
                for (MethodCallExpr mc : mcs) {
                    isDeprecated(mc);
                }
            } catch (Exception e) {

            } catch (StackOverflowError e1) {
                System.out.println("StackOverflowError");
            }
        }
    }

    private void isDeprecated(MethodCallExpr mc) {
        try {
            ResolvedMethodDeclaration rmd = mc.resolveInvokedMethod();
            String methodName = mc.getNameAsString();
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

            //check jdk
            for(JDKDeprecatedAPI api : jdks){
                if (api.getType() == 0)
                    continue;
                if (api.getMethodName().equals(methodName)) {
                    if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","), params.split(","))) {
                        FileUtil.write(jdkOut, api.getId() + ",");
                        return;
                    }
                }
            }

            //check junit
            for (JDKDeprecatedAPI api : junits) {
                if (api.getType() == 0)
                    continue;
                if (api.getMethodName().equals(methodName)) {
                    if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","), params.split(","))) {
                        FileUtil.write(junitOut, api.getId() + ",");
                        return;
                    }
                }
            }
            //check guava
            for (JDKDeprecatedAPI api : guavas) {
                if (api.getType() == 0)
                    continue;
                if (api.getMethodName().equals(methodName)) {
                    if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","), params.split(","))) {
                        FileUtil.write(guavaOut, api.getId() + ",");
                        return;
                    }
                }
            }
            //check junit
            for (JDKDeprecatedAPI api : ios) {
                if (api.getType() == 0)
                    continue;
                if (api.getMethodName().equals(methodName)) {
                    if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","), params.split(","))) {
                        FileUtil.write(ioOut, api.getId() + ",");
                        return;
                    }
                }
            }
            //check junit
            for (JDKDeprecatedAPI api : lang3s) {
                if (api.getType() == 0)
                    continue;
                if (api.getMethodName().equals(methodName)) {
                    if (StringUtils.typeEquals(type, api.getMethodReturnType()) && StringUtils.typeEquals(className, api.getClassName()) && StringUtils.typeEquals(pgName, api.getPackageName()) && StringUtils.typeEquals(api.getMethodArgs().split(","), params.split(","))) {
                        FileUtil.write(lang3Out, api.getId() + ",");
                        return;
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        StudyService studyService = new StudyService();
        ProjectStudy projectStudy = new ProjectStudy();
        List<Project> projects = studyService.getProjects();
        for (Project project : projects) {
            System.out.println(">>>>>>>>>>>>>>>>>>>ProjectID : " + project.getId());
            FileUtil.write(jdkOut, "\n\nProjectID : " + project.getId() + "\n");
            FileUtil.write(junitOut, "\n\nProjectID : " + project.getId() + "\n");
            FileUtil.write(guavaOut, "\n\nProjectID : " + project.getId() + "\n");
            FileUtil.write(ioOut, "\n\nProjectID : " + project.getId() + "\n");
            FileUtil.write(lang3Out, "\n\nProjectID : " + project.getId() + "\n");
            try {
                projectStudy.analyze(project.getLocalAddress());
            } catch (StackOverflowError e) {
                System.out.println("StackOverflowError");
            }
        }


    }


}
