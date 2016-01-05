package qj.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Quan Le on 10/28/2015.
 */
public class ArgsUtil {
    public static Map<String,String> parse(String[] args) {
        HashMap<String, String> ret = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            String key = args[i].substring(1);
            ret.put(key.substring(1), args[i+1]);
        }
        return ret;
    }
}
