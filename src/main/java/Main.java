import pers.xyy.deprecatedapi.service.Parser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.setLibrary(Integer.parseInt(args[1]));
        parser.parseFile(new File(args[0]));
    }

}
