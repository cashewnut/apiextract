import pers.xyy.deprecatedapi.utils.FileUtil;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class B {

    public static void main(String[] args) {
//        Pattern p = Pattern.compile("[\\w|.]+\\.[\\w]+\\((([\\w]|\\s|\\.)+,?)*\\)");
//        Matcher m = p.matcher("FocusTraversalPolicy.getDefaultComponent(Container)");
//        System.out.println(m.matches());
        System.out.println(Arrays.binarySearch(new int[]{6,0},0,1,5));

    }

}
