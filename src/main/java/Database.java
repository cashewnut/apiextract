import pers.xyy.deprecatedapi.jdk.model.JDKDeprecatedAPI;
import pers.xyy.deprecatedapi.jdk.service.IJDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.jdk.service.impl.JDKDeprecatedAPIService;
import pers.xyy.deprecatedapi.utils.FileUtil;

import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//正则表达式
public class Database {

    public static void main(String[] args) throws URISyntaxException {
        IJDKDeprecatedAPIService service = new JDKDeprecatedAPIService();

        List<JDKDeprecatedAPI> jdkDeprecatedAPIS = service.getJDKDeprecatedAPIs();

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
        List<String> regexs = FileUtil.read(Test.class.getResource("/rm").toURI().getPath());
        for (int i = 0; i < regexs.size(); i++) {
            int id = Integer.parseInt(regexs.get(i));
            JDKDeprecatedAPI api = jdkDeprecatedAPIS.get(id - 1);
            i++;
            api.setQualifiedSignature(regexs.get(++i));
            //api.setReplacedComment(regexs.get(++i));
        }

        service.updateById(jdkDeprecatedAPIS);
    }

}
