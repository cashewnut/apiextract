package pers.xyy.deprecatedapi.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import pers.xyy.deprecatedapi.model.LibraryAPI;
import pers.xyy.deprecatedapi.service.impl.LibraryAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.lang.reflect.AnnotatedArrayType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    private String pgName;
    private String clazzName;
    private Integer library;

    private ILibraryAPIService service = new LibraryAPIService();

    public void parseFile(File file) {

        CompilationUnit cu = FileUtil.openCU(file.getAbsolutePath());
        this.pgName = cu.getPackageDeclaration().get().getNameAsString();
        ClassOrInterfaceDeclaration clazz = cu.getClassByName(file.getName().replace(".java", "")).get();
        this.clazzName = clazz.getNameAsString();

        NodeList<BodyDeclaration<?>> body = clazz.getMembers();//获取一个类中的成员
        List<MethodDeclaration> deprecatedMethods = getDeprecatedMethod(getMethodList(body));

        for (MethodDeclaration method : deprecatedMethods) {
            LibraryAPI libraryAPI = new LibraryAPI();
            libraryAPI.setPkg(this.pgName);
            libraryAPI.setClazz(clazzName);
            libraryAPI.setLibrary(this.library);
            libraryAPI.setComment(method.getComment().get().getContent());
            libraryAPI.setLine(method.getBegin().get().line);
            StringBuilder sb = new StringBuilder();
            for (Modifier modifier : method.getModifiers())
                sb.append(modifier.getKeyword().asString()).append(" ");
            sb.append(method.getType().asString()).append(" ");
            sb.append(method.getNameAsString()).append("(");
            method.getParameters().forEach(p -> p.setAnnotations(new NodeList<>()));
            sb.append(method.getParameters().stream().map(Node::toString).collect(Collectors.joining(", "))).append(") ");
            System.out.println(sb);
            service.saveLibraryAPI(libraryAPI);
        }

    }

    private List<MethodDeclaration> getMethodList(NodeList<BodyDeclaration<?>> body) {
        List<MethodDeclaration> methods = new ArrayList<>();
        for (BodyDeclaration bodyDeclaration : body) {
            if (bodyDeclaration instanceof MethodDeclaration)
                methods.add((MethodDeclaration) bodyDeclaration);
        }
        return methods;
    }

    private List<MethodDeclaration> getDeprecatedMethod(List<MethodDeclaration> methods) {
        List<MethodDeclaration> deprecatedMethod = new ArrayList<>();
        for (MethodDeclaration methodDeclaration : methods) {
            for (AnnotationExpr annotate : methodDeclaration.getAnnotations()) {
                if (annotate.getNameAsString().equals("Deprecated")) {
                    deprecatedMethod.add(methodDeclaration);
                    break;
                }
            }
        }
        return deprecatedMethod;
    }

    public void setLibrary(Integer library) {
        this.library = library;
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parseFile(new File("/Users/xiyaoguo/Desktop/Assert.java"));
    }

}
