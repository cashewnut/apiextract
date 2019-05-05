package pers.xyy.deprecatedapi.jdk;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.util.List;

public class ReplacementTool {

    public static void main(String[] args) {
        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu = FileUtil.openCU("/Users/xiyaoguo/Documents/workspace/IntelliJ IDEA/apiextract/src/main/java/C.java");

        List<MethodCallExpr> mcs = cu.findAll(MethodCallExpr.class);
        Visitor visitor = new Visitor();
        for (MethodCallExpr mc : mcs)
            visitor.replace(mc);

        System.out.println(cu);
    }
}

