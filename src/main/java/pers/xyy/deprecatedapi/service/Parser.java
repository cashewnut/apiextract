package pers.xyy.deprecatedapi.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import pers.xyy.deprecatedapi.model.LibraryAPI;
import pers.xyy.deprecatedapi.service.impl.LibraryAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    private String pgName;
    private String clazzName;
    private Integer library;

    private ILibraryAPIService service = new LibraryAPIService();

    public void parseFile(String path){
        List<File> javaFile = FileUtil.getJavaFiles(new File(path));
        for(File file : javaFile)
            parseFile(file);
    }

    public void parseFile(File file) {

        CompilationUnit cu = FileUtil.openCU(file.getAbsolutePath());
        if(cu.getPackageDeclaration().isPresent())
            this.pgName = cu.getPackageDeclaration().get().getNameAsString();
        ClassOrInterfaceDeclaration clazz = getClass(cu);
        if(clazz == null)
            return;
        this.clazzName = clazz.getNameAsString();

        NodeList<BodyDeclaration<?>> body = clazz.getMembers();//获取一个类中的成员
        List<MethodDeclaration> deprecatedMethods = getDeprecatedMethod(getMethodList(body));

        for (MethodDeclaration method : deprecatedMethods) {
            System.out.println(pgName + "." + clazzName);
            LibraryAPI libraryAPI = new LibraryAPI();
            libraryAPI.setPkg(this.pgName);
            libraryAPI.setClazz(clazzName);
            libraryAPI.setLibrary(this.library);
            if(method.getComment().isPresent())
                libraryAPI.setComment(method.getComment().get().getContent());
            if(method.getBegin().isPresent())
                libraryAPI.setLine(method.getBegin().get().line);
            StringBuilder sb = new StringBuilder();
            for (Modifier modifier : method.getModifiers())
                sb.append(modifier.asString()).append(" ");
            sb.append(method.getType().toString()).append(" ");
            sb.append(method.getNameAsString()).append("(");
            String types = method.getParameters().stream().map(p->p.getType().asString()).collect(Collectors.joining(", "));
            sb.append(types).append(")");
            libraryAPI.setMethod(sb.toString());
            System.out.println(sb);
            service.saveLibraryAPI(libraryAPI);
        }

    }

    private ClassOrInterfaceDeclaration getClass(CompilationUnit cu) {
        try {
            TypeDeclaration tp = cu.getTypes().stream().filter((n) -> (n instanceof ClassOrInterfaceDeclaration)).findFirst().get();
            return (ClassOrInterfaceDeclaration) tp;
        } catch (Exception e) {
            return null;
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
        parser.setLibrary(1);
        parser.parseFile("/Users/xiyaoguo/Desktop/5.1.5");
    }

}
