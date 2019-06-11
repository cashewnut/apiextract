import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.tools.replace.model.Method;
import pers.xyy.deprecatedapi.jdk.tools.replace.model.Replace;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

//正则表达式
public class Database2 {

    private static IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

    public static void main(String[] args) throws URISyntaxException {


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

        new Database2().notice();

    }

    public void classify() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getType() != 0 && api.getType() != 2 || api.getrClassName() == null)
                continue;
            System.out.println(api.getId());
            if ((api.getClassName().equals(api.getrClassName()) || isBase(api)) && typeEquals(api.getMethodArgs().split(","), api.getrMethodArgs().split(","))) {
                api.setType(1);
                System.out.println("type1 : " + api.getId());
            }

        }
        service.updateById(apis);
    }

    public void format() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getReplace() == null)
                continue;
            System.out.println(api.getId());
            api.setReplace(JSON.toJSONString(JSONObject.parseObject(api.getReplace(), Replace.class)));
        }
        service.updateById(apis);
    }

    //添加返回值不同的提示
    public void notice() {
        List<JDKDeprecatedAPI> apis = service.getJDKDeprecatedAPIs();
        for (JDKDeprecatedAPI api : apis) {
            if (api.getMethodReturnType() == null || api.getrReturnType() == null)
                continue;
            if (api.getType() == 1) {
                Replace replace = new Replace();
                replace.setOperations(Collections.singletonList("$m0"));
                Method method = new Method();
                method.setName(api.getrMethodName());
                replace.setMethods(Collections.singletonList(method));
                if (!typeEquals(api.getMethodReturnType(), api.getrReturnType()))
                    replace.setComment("Deprecated and replaced methods have different return values.");
                api.setReplace(JSON.toJSONString(replace));
            } else {
                if (api.getReplace() != null) {
                    Replace replace = JSONObject.parseObject(api.getReplace(), Replace.class);
                    if (!typeEquals(api.getMethodReturnType(), api.getrReturnType())) {
                        if (replace.getComment() == null)
                            replace.setComment("Deprecated and replaced methods have different return values.");
                        else
                            replace.setComment("Deprecated and replaced methods have different return values.\n" + replace.getComment());
                    }
                    api.setReplace(JSON.toJSONString(replace));
                }
            }
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

    private boolean typeEquals(String[] l1, String[] l2) {
        if (l1.length != l2.length)
            return false;
        boolean res = true;
        for (int i = 0; i < l1.length; i++) {
            String args1 = l1[i];
            String args2 = l2[i];
            res &= typeEquals(args1, args2);
        }
        return res;
    }

    private boolean typeEquals(String str1, String str2) {
        if (str1.isEmpty() && str2.isEmpty())
            return true;
        if (str1.isEmpty() || str2.isEmpty())
            return false;

        //TODO 判断两个类是否是父子类，暂时写死，有时间重新写
        Map<String, Set<String>> map = new HashMap<>();
        map.put("JComponent", new HashSet<>(Arrays.asList("JMenuBar", "JScrollPane")));
        String[] str1s = str1.split("\\.");
        String[] str2s = str2.split("\\.");
        String s1 = str1s[str1s.length - 1], s2 = str2s[str2s.length - 1];
        if (s1.equals(s2))
            return true;
        if (map.containsKey(s1)) {
            if (map.get(s1).contains(s2))
                return true;
        }
        if (map.containsKey(s2)) {
            return map.get(s2).contains(s1);
        }
        return false;
    }

}
