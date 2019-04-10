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
        List<String> regexs = FileUtil.read(Test.class.getResource("/args").toURI().getPath());
        List<JDKDeprecatedAPI> jdkDeprecatedAPIS = service.getJDKDeprecatedAPIs();
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
        }
        service.updateById(jdkDeprecatedAPIS);
    }

}
