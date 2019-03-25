import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {

        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(),new JavaParserTypeSolver(new File("src/main/java")));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit cu = FileUtil.openCU("/Users/xiyaoguo/Documents/workspace/IntelliJ IDEA/apiextract/src/main/java/B.java");
        List<MethodCallExpr> mcs = cu.findAll(MethodCallExpr.class);
        //mcs.get(0).
        System.out.println(cu.findAll(MethodCallExpr.class).get(0).resolveInvokedMethod().getQualifiedSignature());
        //cu.findAll(MethodCallExpr.class).forEach(mce ->System.out.println(mce.resolveInvokedMethod().getQualifiedSignature()));
//        methodCalls.forEach(mc ->System.out.println(
//                JavaParserFacade.get(typeSolver).solve(mc)
//                        .getCorrespondingDeclaration().getQualifiedSignature()));

    }

//    public static class MethodNamePrinter extends VoidVisitorAdapter<Void> {
//        @Override
//        public void visit(MethodDeclaration n, Void arg) {
//            super.visit(n, arg);
//            System.out.println(n.getName());
//        }
//    }

}
