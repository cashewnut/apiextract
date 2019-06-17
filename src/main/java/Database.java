import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//正则表达式
public class Database {

    private static IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public static void main(String[] args) {


        //抽取正则表达式
        /*List<String> regexs = FileUtil.read(Test.class.getResource("/args").toURI().getPath());
        for(JDKDeprecatedAPI api : jdkDeprecatedAPIS){
            String comment = api.getComment();
            if(comment == null)
                continue;
            for(String regex : regexs){
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(comment);
                if(m.find()){
                    api.setReplacedComment(m.group());
                    break;
                }
            }
        }*/

        //规范replace_comment
//        List<String> regexs = FileUtil.read(Test.class.getResource("/rm").toURI().getPath());
//        for (int i = 0; i < regexs.size(); i++) {
//            int id = Integer.parseInt(regexs.get(i));
//            JDKDeprecatedAPI api = jdkDeprecatedAPIS.get(id - 1);
//            i++;
//            api.setQualifiedSignature(regexs.get(++i));
//            //api.setReplacedComment(regexs.get(++i));
//        }
//
//        service.updateById(jdkDeprecatedAPIS);

        JDKDeprecatedAPI api = new JDKDeprecatedAPI();
        api.setPackageName("javax.swing");
        api.setClassName("FocusManager");
        api.setrPackageName("java.awt");
        api.setrClassName("KeyboardFocusManager");
        //new Database().isBase(api);

        new Database().classify();

    }

    public void classify() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getType() != 0 || api.getrClassName() == null)
                continue;
            System.out.println(api.getId());
            if (api.getClassName().equals(api.getrClassName()))
                api.setType(2);
            else if (isBase(api))
                api.setType(2);

        }
        service.updateById(apis);
    }

    //判断api中原方法是否是替换方法的子类
    private boolean isBase(JDKDeprecatedAPI api) {
        String baseUrl = "/Users/xiyaoguo/Desktop/deprecated API/src/";
        String className = api.getClassName().contains(".") ? api.getClassName().split("\\.")[1] : api.getClassName();
        String pcgName = api.getPackageName().replace(".", "/") + "/";
        while (true) {
            File file = new File(baseUrl + pcgName + className + ".java");
            if (file.exists()) {
                CompilationUnit cu = FileUtil.openCU(file.getAbsolutePath());
                ClassOrInterfaceDeclaration clazz = getClass(cu);
                if (clazz != null) {
                    if (clazz.getExtendedTypes() != null && clazz.getExtendedTypes().size() > 0) {
                        className = clazz.getExtendedTypes().get(0).asString();
                        if (className.equals(api.getrClassName()))
                            return true;
                        else {
                            NodeList<ImportDeclaration> importDeclarations = cu.getImports();
                            Set<String> packageSet = new HashSet<>();
                            if (importDeclarations != null) {
                                for (ImportDeclaration id : importDeclarations) {
                                    String impt = id.getNameAsString();
                                    packageSet.add(impt.substring(0, impt.lastIndexOf(".")));
                                    packageSet.add(impt);
                                }
                            }
                            for (String str : packageSet) {
                                pcgName = str.replace(".", "/") + "/";
                                if (new File(baseUrl + pcgName + className + ".java").exists())
                                    break;
                            }
                        }
                    } else
                        return false;
                }

            }

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

}
