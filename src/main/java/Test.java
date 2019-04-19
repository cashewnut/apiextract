import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.DBUtil;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws URISyntaxException {

//        String str = "     * Returns the component at the specified index.\n" +
//                "     *\n" +
//                "     * @param i an integer specifying the position, where 0 is first\n" +
//                "     * @return the <code>Component</code> at the position,\n" +
//                "     *          or <code>null</code> for an invalid index\n" +
//                "     * @deprecated replaced by <code>getComponent(int i)</code>";

//        String str = "     * Returns the text contained in this <code>TextComponent</code>.\n" +
//                "     * If the underlying document is <code>null</code>, will give a\n" +
//                "     * <code>NullPointerException</code>.\n" +
//                "     * <p>\n" +
//                "     * For security reasons, this method is deprecated.  Use the\n" +
//                "     <code>* getPassword</code> method instead.\n" +
//                "     * @deprecated As of Java 2 platform v1.2,\n" +
//                "     * replaced by <code>getPassword</code>.\n" +
//                "     * @return the text";
//        String str = "use method <code>set_result</code>";
//
//        Pattern p = Pattern.compile("use (the )?method <code>([#]?[\\w]+(\\([\\w\\s,\\[\\].<>?]*\\))?\\s?)*</code>");
////        Pattern p = Pattern.compile("\\{@link ([#]?[\\w]+\\([\\w\\s,\\[\\].<>?]*\\)\\s?)*}.");
//        Matcher m = p.matcher(str);
//        System.out.println(m.find());
//        System.out.println(m.group());

        //System.out.println(Test.class.getResource("/args").getPath());

//        List<String> prefixs = FileUtil.read(Test.class.getResource("/args").toURI().getPath());
        //System.out.println(prefixs);

        IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();
        List<JDKDeprecatedAPI> jdkDeprecatedAPIS = service.getJDKDeprecatedAPIs();
        //格式化注释
        /*for (JDKDeprecatedAPI api : jdkDeprecatedAPIS) {
            String comment = api.getComment();
            if (comment != null)
                api.setComment(comment.trim());
        }
        service.updateById(jdkDeprecatedAPIS);*/

        //String str =

        //输出文件
        for (int i = 0; i < jdkDeprecatedAPIS.size(); i++) {
            JDKDeprecatedAPI api = jdkDeprecatedAPIS.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append("<No.").append(api.getId()).append(">\n");
            sb.append(api.getComment()).append("\n");
            sb.append(api.getPackageName()).append(".").append(api.getClassName()).append(".");
            sb.append(api.getMethodName()).append("(").append(api.getMethodArgs()).append(")  ").append("line : ").append(api.getLine());
            sb.append("\n\n").append(api.getReplacedComment());
            sb.append("\n\n-----------------------------\n");
            FileUtil.write("/Users/xiyaoguo/Desktop/deprecated_api_list", sb.toString());
        }


    }


}
