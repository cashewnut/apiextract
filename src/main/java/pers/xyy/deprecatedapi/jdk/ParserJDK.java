package pers.xyy.deprecatedapi.jdk;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.model.LibraryAPI;
import pers.xyy.deprecatedapi.service.ILibraryAPIService;
import pers.xyy.deprecatedapi.service.impl.LibraryAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParserJDK {

    private IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();


    /**
     * parse a .java file.
     *
     * @param path path of a class
     */
    public void parseFile(String path) {
        CompilationUnit cu = FileUtil.openCU(path);
        System.out.println(">>>>>>>>" + path);

        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(new File("/home/fdse/xyy/jdk5")));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        //mds用于存放deprecated method
        List<MethodDeclaration> mds = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> visitor = new DeprecatedAPIVisitor();
        visitor.visit(cu, mds);

        for (MethodDeclaration md : mds) {
            JDKDeprecatedAPI api = new JDKDeprecatedAPI();
            api.setPackageName(md.resolve().getPackageName());
            api.setClassName(md.resolve().getClassName());
            api.setMethodReturnType(md.getType().toString());
            api.setMethodName(md.getNameAsString());
            String args = md.getParameters().stream().map(p -> p.getType().asString()).collect(Collectors.joining(","));
            api.setMethodArgs(args);
            if (md.getComment().isPresent())
                api.setComment(md.getComment().get().getContent());
            if (md.getBegin().isPresent())
                api.setLine(md.getBegin().get().line);
            System.out.println(String.format("%s.%s:%s", api.getPackageName(), api.getClassName(), api.getMethodName()));
            service.saveJDKDeprecatedAPI(api);
        }

    }

    class DeprecatedAPIVisitor extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration n, List<MethodDeclaration> arg) {
            super.visit(n, arg);
            for (AnnotationExpr annotate : n.getAnnotations()) {
                if (annotate.getNameAsString().equals("Deprecated")) {
                    arg.add(n);
                    return;
                }
            }
            if (n.getComment().isPresent()) {
                String comment = n.getComment().get().getContent();
                String regex = "@deprecated";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(comment);
                if (m.find())
                    arg.add(n);
            }
        }
    }

    public void parserFile(String path) {
        CompilationUnit cu = FileUtil.openCU(path);
        System.out.println(">>>>>>>>" + path);

        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(new File("/home/fdse/xyy/jdk5")));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        List<MethodDeclaration> mds = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> visitor = new DeprecatedAPIVisitor();
        visitor.visit(cu, mds);

        for (MethodDeclaration md : mds) {
            if(md.getNameAsString().equals("setThreshold"))
                continue;
            JDKDeprecatedAPI api = new JDKDeprecatedAPI();
            api.setPackageName(md.resolve().getPackageName());
            api.setClassName(md.resolve().getClassName());
            api.setMethodReturnType(md.getType().toString());
            api.setMethodName(md.getNameAsString());
            String args = md.getParameters().stream().map(p -> p.getType().asString()).collect(Collectors.joining(","));
            api.setMethodArgs(args);
            System.out.println(String.format("%s.%s:%s", api.getPackageName(), api.getClassName(), api.getMethodName()));
            String rArgs = "";
            for (int i = 0; i < md.resolve().getNumberOfParams(); i++)
                rArgs = rArgs + md.resolve().getParam(i).describeType() + ",";
            if (rArgs.endsWith(","))
                rArgs = rArgs.substring(0, rArgs.length() - 1);
            service.updateArgs(api, rArgs);
        }

    }

    public static void main(String[] args) {
        //new ParserJDK().parseFile("/Users/xiyaoguo/Desktop/Assert.java");
    }


}
